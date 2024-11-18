/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.oauth2.validators;

import org.mockito.MockedStatic;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.balana.utils.exception.PolicyBuilderException;
import org.wso2.balana.utils.policy.PolicyBuilder;
import org.wso2.balana.utils.policy.dto.RequestElementDTO;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.application.authentication.framework.util.FrameworkUtils;
import org.wso2.carbon.identity.application.common.model.ServiceProvider;
import org.wso2.carbon.identity.application.mgt.validator.DefaultApplicationValidator;
import org.wso2.carbon.identity.common.testng.WithCarbonHome;
import org.wso2.carbon.identity.entitlement.EntitlementException;
import org.wso2.carbon.identity.entitlement.EntitlementService;
import org.wso2.carbon.identity.entitlement.common.dto.RequestDTO;
import org.wso2.carbon.identity.entitlement.common.util.PolicyCreatorUtil;
import org.wso2.carbon.identity.oauth.cache.AuthorizationGrantCache;
import org.wso2.carbon.identity.oauth.cache.AuthorizationGrantCacheEntry;
import org.wso2.carbon.identity.oauth.cache.AuthorizationGrantCacheKey;
import org.wso2.carbon.identity.oauth.dao.OAuthAppDO;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.authz.OAuthAuthzReqMessageContext;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenReqDTO;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AuthorizeReqDTO;
import org.wso2.carbon.identity.oauth2.model.AccessTokenDO;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.carbon.identity.oauth2.util.OAuth2Util;
import org.wso2.carbon.identity.oauth2.validators.xacml.XACMLScopeValidator;
import org.wso2.carbon.identity.oauth2.validators.xacml.constants.XACMLScopeValidatorConstants;
import org.wso2.carbon.identity.oauth2.validators.xacml.internal.OAuthScopeValidatorDataHolder;

import java.lang.reflect.Method;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Unit tests for XACMLScopeValidator class.
 */
@WithCarbonHome
public class XACMLScopeValidatorTest {

    private static final String ADMIN_USER = "admin_user";
    private static final String APP_NAME = "SP_APP";
    private static final String DECISION = "decision";
    private static final String RULE_EFFECT_PERMIT = "Permit";
    private static final String RULE_EFFECT_NOT_APPLICABLE = "NotApplicable";
    private static final String POLICY = "policy";
    private static final String ERROR = "error";
    private static final String CONSUMER_KEY = "consumer-key";

    private org.wso2.carbon.identity.oauth2.validators.xacml.XACMLScopeValidator xacmlScopeValidator = new XACMLScopeValidator();
    private AccessTokenDO accessTokenDO;
    private OAuthAppDO authApp;
    private final String RESOURCE = "resource";
    private String accessToken = "cf7da41d-6a73-3cfe-9c17-9cf1927c7f46";
    private OAuthTokenReqMessageContext tokenReqMessageContext;
    private OAuth2AccessTokenReqDTO oauth2AccessTokenReqDTO;
    private OAuthAuthzReqMessageContext oauthAuthzMsgCtx;
    private OAuth2AuthorizeReqDTO oAuth2AuthorizeReqDTO;
    private AuthenticatedUser authenticatedUser;
    private AuthorizationGrantCacheEntry authorizationGrantCacheEntry;
    private AuthorizationGrantCacheKey cacheKey;

    @BeforeClass
    public void init() {

        String[] scopeArray = new String[]{"scope1", "scope2", "scope3"};
        authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setUserName(ADMIN_USER);
        accessTokenDO = new AccessTokenDO();
        accessTokenDO.setConsumerKey(CONSUMER_KEY);
        accessTokenDO.setAuthzUser(authenticatedUser);
        accessTokenDO.setScope(scopeArray);
        accessTokenDO.setAccessToken(accessToken);
        authApp = new OAuthAppDO();
        authApp.setApplicationName(APP_NAME);

        oauth2AccessTokenReqDTO = new OAuth2AccessTokenReqDTO();
        oauth2AccessTokenReqDTO.setClientId(CONSUMER_KEY);
        oauth2AccessTokenReqDTO.setScope(scopeArray);
        tokenReqMessageContext = new OAuthTokenReqMessageContext(oauth2AccessTokenReqDTO);
        tokenReqMessageContext.setAuthorizedUser(authenticatedUser);

        oAuth2AuthorizeReqDTO = new OAuth2AuthorizeReqDTO();
        oAuth2AuthorizeReqDTO.setConsumerKey(CONSUMER_KEY);
        oAuth2AuthorizeReqDTO.setUser(authenticatedUser);
        oAuth2AuthorizeReqDTO.setScopes(scopeArray);
        oauthAuthzMsgCtx = new OAuthAuthzReqMessageContext(oAuth2AuthorizeReqDTO);

        cacheKey = new AuthorizationGrantCacheKey(accessToken);
    }

    @DataProvider(name = "createRequestObj")
    public Object[][] createRequestObj() {

        return new Object[][]{
                // Create XACML request string for token validation phase.
                {accessTokenDO.getScope(), XACMLScopeValidatorConstants.ACTION_VALIDATE
                        , RESOURCE, accessTokenDO.getAccessToken()},

                // Create XACML request string for token issuing phase.
                {tokenReqMessageContext.getOauth2AccessTokenReqDTO().getScope(),
                        XACMLScopeValidatorConstants.ACTION_SCOPE_VALIDATE, null, null},

                // Create XACML request string for code issuing phase.
                {oauthAuthzMsgCtx.getAuthorizationReqDTO().getScopes(),
                        XACMLScopeValidatorConstants.ACTION_SCOPE_VALIDATE, null, null},
        };
    }

    @Test(dataProvider = "createRequestObj")
    public void testCreateRequestObj(String[] scopes, String action, String resource, String token) throws Exception {

        try (MockedStatic<PolicyBuilder> mockedPolicyBuilder = mockStatic(PolicyBuilder.class);
             MockedStatic<AuthorizationGrantCache> authorizationGrantCacheMockedStatic
                     = mockStatic(AuthorizationGrantCache.class)){
            PolicyBuilder policyBuilder = mock(PolicyBuilder.class);

            mockedPolicyBuilder.when(PolicyBuilder::getInstance).thenReturn(policyBuilder);
            mockedPolicyBuilder.when(() -> policyBuilder.buildRequest(any(RequestElementDTO.class))).thenReturn(POLICY);

            AuthorizationGrantCache authorizationGrantCache = mock(AuthorizationGrantCache.class);

            authorizationGrantCacheMockedStatic.when(
                    AuthorizationGrantCache::getInstance).thenReturn(authorizationGrantCache);
            authorizationGrantCacheMockedStatic.when(() -> authorizationGrantCache.getValueFromCacheByToken(any
                    (AuthorizationGrantCacheKey.class))).thenReturn(authorizationGrantCacheEntry);

            XACMLScopeValidator xacmlScopeValidator = new XACMLScopeValidator();
            Method createRequest = XACMLScopeValidator.class.getDeclaredMethod("createRequest", String[].class,
                    AuthenticatedUser.class, OAuthAppDO.class, String.class, String.class, String.class);
            createRequest.setAccessible(true);
            String request = (String)  createRequest.invoke(xacmlScopeValidator, scopes, authenticatedUser, authApp, action, resource, token);
            assertFalse(request.isEmpty());
        }
    }

    @Test
    public void testExtractXACMLResponse() throws Exception {

        String xacmlResponse = "<ns:root xmlns:ns=\"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\">"
                + "<ns:Result>"
                + "<ns:Decision>"
                + DECISION
                + "</ns:Decision>"
                + "</ns:Result>"
                + "</ns:root>";

        XACMLScopeValidator xacmlScopeValidator = new XACMLScopeValidator();
        Method extractDecisionFromXACMLResponse = XACMLScopeValidator.class.getDeclaredMethod(
                "extractDecisionFromXACMLResponse", String.class);
        extractDecisionFromXACMLResponse.setAccessible(true);
        String response = (String) extractDecisionFromXACMLResponse.invoke(xacmlScopeValidator, xacmlResponse);
        assertEquals(response, DECISION);
    }

    /**
     * Tests the validateScope method when validating access tokens, by returning different mock XACML response for
     * entitlementService.
     *
     * @throws Exception exception
     */
    @Test(expectedExceptions = IdentityOAuth2Exception.class)
    public void testValidatedScope() throws Exception {

        String xacmlResponse = "<ns:root xmlns:ns=\"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\">"
                + "<ns:Result>"
                + "<ns:Decision>"
                + DECISION
                + "</ns:Decision>"
                + "</ns:Result>"
                + "</ns:root>";

        try (MockedStatic<FrameworkUtils> frameworkUtilsMockedStatic = mockStatic(FrameworkUtils.class);
             MockedStatic<OAuth2Util> oAuth2UtilMockedStatic = mockStatic(OAuth2Util.class);
             MockedStatic<PolicyCreatorUtil> policyCreatorUtilMockedStatic = mockStatic(PolicyCreatorUtil.class);
             MockedStatic<PolicyBuilder> policyBuilderMockedStatic = mockStatic(PolicyBuilder.class);
             MockedStatic<AuthorizationGrantCache> authorizationGrantCacheMockedStatic
                     = mockStatic(AuthorizationGrantCache.class)) {

            oAuth2UtilMockedStatic.when(() -> OAuth2Util.getAppInformationByClientId(anyString())).thenReturn(authApp);

            RequestElementDTO requestElementDTO = mock(RequestElementDTO.class);
            policyCreatorUtilMockedStatic.when(() -> PolicyCreatorUtil.createRequestElementDTO(any(RequestDTO.class)))
                    .thenReturn(requestElementDTO);
            PolicyBuilder policyBuilder = mock(PolicyBuilder.class);
            policyBuilderMockedStatic.when(PolicyBuilder::getInstance).thenReturn(policyBuilder);
            policyBuilderMockedStatic.when(() -> policyBuilder.buildRequest(any(RequestElementDTO.class))).thenReturn(POLICY);

            EntitlementService entitlementService = mock(EntitlementService.class);
            OAuthScopeValidatorDataHolder.getInstance().setEntitlementService(entitlementService);

            AuthorizationGrantCache authorizationGrantCache = mock(AuthorizationGrantCache.class);

            authorizationGrantCacheMockedStatic.when(
                    AuthorizationGrantCache::getInstance).thenReturn(authorizationGrantCache);
            authorizationGrantCacheMockedStatic.when(() -> authorizationGrantCache.getValueFromCacheByToken(any
                    (AuthorizationGrantCacheKey.class))).thenReturn(authorizationGrantCacheEntry);

            when(entitlementService.getDecision(anyString())).thenReturn(xacmlResponse);
            assertFalse(xacmlScopeValidator.validateScope(accessTokenDO, RESOURCE));

            xacmlResponse = xacmlResponse.replace(DECISION, RULE_EFFECT_NOT_APPLICABLE);
            when(entitlementService.getDecision(anyString())).thenReturn(xacmlResponse);
            assertTrue(xacmlScopeValidator.validateScope(accessTokenDO, RESOURCE));

            xacmlResponse = xacmlResponse.replace(RULE_EFFECT_NOT_APPLICABLE, RULE_EFFECT_PERMIT);
            when(entitlementService.getDecision(anyString())).thenReturn(xacmlResponse);
            assertTrue(xacmlScopeValidator.validateScope(accessTokenDO, RESOURCE));

            when(entitlementService.getDecision(anyString())).thenThrow(new EntitlementException(ERROR));
            xacmlScopeValidator.validateScope(accessTokenDO, RESOURCE);

            policyBuilderMockedStatic.when(() -> policyBuilder.buildRequest(any(RequestElementDTO.class)))
                    .thenThrow(new PolicyBuilderException(ERROR));
            xacmlScopeValidator.validateScope(accessTokenDO, RESOURCE);
        }
    }

    /**
     * Tests the validateScope method during access tokens issuing phase, by returning different mock XACML response
     * for entitlement service.
     *
     * @throws Exception exception
     */
    @Test(expectedExceptions = IdentityOAuth2Exception.class)
    public void testValidateTokenReq() throws Exception {

        String xacmlResponse = "<ns:root xmlns:ns=\"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\">"
                + "<ns:Result>"
                + "<ns:Decision>"
                + DECISION
                + "</ns:Decision>"
                + "</ns:Result>"
                + "</ns:root>";

        try (MockedStatic<FrameworkUtils> frameworkUtilsMockedStatic = mockStatic(FrameworkUtils.class);
             MockedStatic<OAuth2Util> oAuth2UtilMockedStatic = mockStatic(OAuth2Util.class);
             MockedStatic<PolicyCreatorUtil> policyCreatorUtilMockedStatic = mockStatic(PolicyCreatorUtil.class);
             MockedStatic<PolicyBuilder> policyBuilderMockedStatic = mockStatic(PolicyBuilder.class)) {

            oAuth2UtilMockedStatic.when(() -> OAuth2Util.getAppInformationByClientId(anyString())).thenReturn(authApp);

            RequestElementDTO requestElementDTO = mock(RequestElementDTO.class);
            policyCreatorUtilMockedStatic.when(() -> PolicyCreatorUtil.createRequestElementDTO(any(RequestDTO.class)))
                    .thenReturn(requestElementDTO);

            PolicyBuilder policyBuilder = mock(PolicyBuilder.class);
            policyBuilderMockedStatic.when(PolicyBuilder::getInstance).thenReturn(policyBuilder);
            policyBuilderMockedStatic.when(() -> policyBuilder.buildRequest(any(RequestElementDTO.class)))
                    .thenReturn(POLICY);

            EntitlementService entitlementService = mock(EntitlementService.class);
            OAuthScopeValidatorDataHolder.getInstance().setEntitlementService(entitlementService);

            when(entitlementService.getDecision(anyString())).thenReturn(xacmlResponse);
            assertFalse(xacmlScopeValidator.validateScope(tokenReqMessageContext));

            xacmlResponse = xacmlResponse.replace(DECISION, RULE_EFFECT_NOT_APPLICABLE);
            when(entitlementService.getDecision(anyString())).thenReturn(xacmlResponse);
            assertTrue(xacmlScopeValidator.validateScope(tokenReqMessageContext));

            xacmlResponse = xacmlResponse.replace(RULE_EFFECT_NOT_APPLICABLE, RULE_EFFECT_PERMIT);
            when(entitlementService.getDecision(anyString())).thenReturn(xacmlResponse);
            assertTrue(xacmlScopeValidator.validateScope(tokenReqMessageContext));

            when(entitlementService.getDecision(anyString())).thenThrow(new EntitlementException(ERROR));
            xacmlScopeValidator.validateScope(tokenReqMessageContext);

            policyBuilderMockedStatic.when(() -> policyBuilder.buildRequest(any(RequestElementDTO.class)))
                    .thenThrow(new PolicyBuilderException(ERROR));
            xacmlScopeValidator.validateScope(tokenReqMessageContext);
        }
    }

    /**
     * Tests the validateScope method when sending authorization request, by returning different mock XACML response
     * for entitlement service.
     *
     * @throws Exception exception
     */
    @Test(expectedExceptions = IdentityOAuth2Exception.class)
    public void testValidateAuthorizeReq() throws Exception {

        String xacmlResponse = "<ns:root xmlns:ns=\"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\">"
                + "<ns:Result>"
                + "<ns:Decision>"
                + DECISION
                + "</ns:Decision>"
                + "</ns:Result>"
                + "</ns:root>";

        try (MockedStatic<FrameworkUtils> frameworkUtilsMockedStatic = mockStatic(FrameworkUtils.class);
                MockedStatic<OAuth2Util> oAuth2UtilMockedStatic = mockStatic(OAuth2Util.class);
                MockedStatic<PolicyCreatorUtil> policyCreatorUtilMockedStatic = mockStatic(PolicyCreatorUtil.class);
                MockedStatic<PolicyBuilder> policyBuilderMockedStatic = mockStatic(PolicyBuilder.class)) {

            oAuth2UtilMockedStatic.when(() -> OAuth2Util.getAppInformationByClientId(anyString())).thenReturn(authApp);

            RequestElementDTO requestElementDTO = mock(RequestElementDTO.class);
            policyCreatorUtilMockedStatic.when(() -> PolicyCreatorUtil.createRequestElementDTO(any(RequestDTO.class)))
                    .thenReturn(requestElementDTO);
            PolicyBuilder policyBuilder = mock(PolicyBuilder.class);
            policyBuilderMockedStatic.when(PolicyBuilder::getInstance).thenReturn(policyBuilder);
            policyBuilderMockedStatic.when(() -> policyBuilder.buildRequest(any(RequestElementDTO.class)))
                    .thenReturn(POLICY);

            EntitlementService entitlementService = mock(EntitlementService.class);
            OAuthScopeValidatorDataHolder.getInstance().setEntitlementService(entitlementService);
            when(entitlementService.getDecision(anyString())).thenReturn(xacmlResponse);
            assertFalse(xacmlScopeValidator.validateScope(oauthAuthzMsgCtx));

            xacmlResponse = xacmlResponse.replace(DECISION, RULE_EFFECT_NOT_APPLICABLE);
            when(entitlementService.getDecision(anyString())).thenReturn(xacmlResponse);
            assertTrue(xacmlScopeValidator.validateScope(oauthAuthzMsgCtx));

            xacmlResponse = xacmlResponse.replace(RULE_EFFECT_NOT_APPLICABLE, RULE_EFFECT_PERMIT);
            when(entitlementService.getDecision(anyString())).thenReturn(xacmlResponse);
            assertTrue(xacmlScopeValidator.validateScope(oauthAuthzMsgCtx));

            when(entitlementService.getDecision(anyString())).thenThrow(new EntitlementException(ERROR));
            xacmlScopeValidator.validateScope(oauthAuthzMsgCtx);

            policyBuilderMockedStatic.when(() -> policyBuilder.buildRequest(any(RequestElementDTO.class)))
                    .thenThrow(new PolicyBuilderException(ERROR));
            xacmlScopeValidator.validateScope(oauthAuthzMsgCtx);

        }
    }
}
