/*
 * Copyright (c) 2017, WSO2 LLC. (https://www.wso2.com).
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.application.authz.xacml.handler.impl;

import junit.framework.Assert;
import org.mockito.MockedStatic;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.balana.utils.exception.PolicyBuilderException;
import org.wso2.balana.utils.policy.PolicyBuilder;
import org.wso2.balana.utils.policy.dto.RequestElementDTO;
import org.wso2.carbon.identity.application.authentication.framework.config.model.ApplicationConfig;
import org.wso2.carbon.identity.application.authentication.framework.config.model.SequenceConfig;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.exception.PostAuthenticationFailedException;
import org.wso2.carbon.identity.application.authentication.framework.handler.request.PostAuthnHandlerFlowStatus;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.application.authentication.framework.util.FrameworkUtils;
import org.wso2.carbon.identity.application.authz.xacml.internal.AppAuthzDataholder;
import org.wso2.carbon.identity.application.common.model.ServiceProvider;
import org.wso2.carbon.identity.entitlement.EntitlementException;
import org.wso2.carbon.identity.entitlement.EntitlementService;
import org.wso2.carbon.identity.entitlement.common.dto.RequestDTO;
import org.wso2.carbon.identity.entitlement.common.util.PolicyCreatorUtil;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * XACMLBasedAuthorizationHandlerTest defines unit tests for XACMLBasedAuthorizationHandler class.
 */
public class XACMLBasedAuthorizationHandlerTest {

    private XACMLBasedAuthorizationHandler xacmlBasedAuthorizationHandler;
    private AuthenticationContext context;

    private static final String ADMIN_USER = "admin_user";
    private static final String DECISION = "decision";
    private static final String RULE_EFFECT_PERMIT = "Permit";
    private static final String RULE_EFFECT_NOT_APPLICABLE = "NotApplicable";
    private static final String POLICY = "policy";
    private static final String ERROR = "error";
    private static String xacmlResponse = "<ns:root xmlns:ns=\"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\">"
            + "<ns:Result>"
            + "<ns:Decision>"
            + DECISION
            + "</ns:Decision>"
            + "</ns:Result>"
            + "</ns:root>";

    @BeforeClass
    public void init() {

        xacmlBasedAuthorizationHandler = spy(new XACMLBasedAuthorizationHandler());
        context = mock(AuthenticationContext.class);
    }

    @AfterClass
    public void tearDown() {

        AppAuthzDataholder.getInstance().setEntitlementService(null);
    }

    @Test
    public void testCreateRequestDTO() throws Exception {

        SequenceConfig sequenceConfig = mock(SequenceConfig.class);
        AuthenticatedUser authenticatedUser = mock(AuthenticatedUser.class);
        when(context.getSequenceConfig()).thenReturn(sequenceConfig);
        when(sequenceConfig.getAuthenticatedUser()).thenReturn(authenticatedUser);
        when(authenticatedUser.getUserName()).thenReturn(ADMIN_USER);
        Method createRequestDTO = XACMLBasedAuthorizationHandler.class.getDeclaredMethod(
                "createRequestDTO", AuthenticationContext.class);
        createRequestDTO.setAccessible(true);
        RequestDTO requestDTO = (RequestDTO) createRequestDTO.invoke(xacmlBasedAuthorizationHandler, context);
        Assert.assertEquals(requestDTO.getRowDTOs().size(), 9);
    }

    @Test
    public void testEvaluateXACMLResponse() throws Exception {

        Method evaluateXACMLResponse = XACMLBasedAuthorizationHandler.class.getDeclaredMethod(
                "evaluateXACMLResponse", String.class);
        evaluateXACMLResponse.setAccessible(true);
        String response = (String) evaluateXACMLResponse.invoke(xacmlBasedAuthorizationHandler, xacmlResponse);
        assertEquals(response, DECISION);
    }

    @DataProvider(name = "authorizationDataProvider")
    public Object[][] authorizationDataProvider() {

        return new Object[][]{
                {true, DECISION, RULE_EFFECT_NOT_APPLICABLE},
                {true, RULE_EFFECT_NOT_APPLICABLE, RULE_EFFECT_PERMIT},
                {true, DECISION, RULE_EFFECT_NOT_APPLICABLE},
                {false, RULE_EFFECT_NOT_APPLICABLE, RULE_EFFECT_PERMIT},
        };
    }

    @Test(dataProvider = "authorizationDataProvider")
    public void testIsAuthorized(boolean isAuthorizationEnabled, String replaceTarget, String replacement) throws
            Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        try (MockedStatic<FrameworkUtils> frameworkUtilsMockedStatic = mockStatic(FrameworkUtils.class);
             MockedStatic<PolicyCreatorUtil> policyCreatorUtilMockedStatic = mockStatic(PolicyCreatorUtil.class);
             MockedStatic<PolicyBuilder> policyBuilderMockedStatic = mockStatic(PolicyBuilder.class)) {

            ApplicationConfig applicationConfig = new ApplicationConfig(new ServiceProvider());
            applicationConfig.setEnableAuthorization(isAuthorizationEnabled);

            FrameworkUtils.endTenantFlow();
            SequenceConfig sequenceConfig = new SequenceConfig();
            sequenceConfig.setApplicationConfig(applicationConfig);
            AuthenticatedUser authenticatedUser = new AuthenticatedUser();
            authenticatedUser.setUserName(ADMIN_USER);
            sequenceConfig.setAuthenticatedUser(authenticatedUser);
            when(context.getSequenceConfig()).thenReturn(sequenceConfig);
            RequestElementDTO requestElementDTO = mock(RequestElementDTO.class);
            policyCreatorUtilMockedStatic.when(() -> PolicyCreatorUtil.createRequestElementDTO(any(RequestDTO.class)))
                    .thenReturn(requestElementDTO);
            PolicyBuilder policyBuilder = mock(PolicyBuilder.class);
            policyBuilderMockedStatic.when(PolicyBuilder::getInstance).thenReturn(policyBuilder);
            when(policyBuilder.buildRequest(any(RequestElementDTO.class))).thenReturn(POLICY);
            EntitlementService entitlementService = mock(EntitlementService.class);
            AppAuthzDataholder.getInstance().setEntitlementService(entitlementService);
            when(entitlementService.getDecision(anyString())).thenReturn(xacmlResponse);

            xacmlResponse = xacmlResponse.replace(replaceTarget, replacement);
            when(entitlementService.getDecision(anyString())).thenReturn(xacmlResponse);
            PostAuthnHandlerFlowStatus flowStatus = xacmlBasedAuthorizationHandler.handle(request, response, context);
            assertEquals(PostAuthnHandlerFlowStatus.SUCCESS_COMPLETED, flowStatus);
        }
    }

    @DataProvider(name = "authorizationFailedDataProvider")
    public Object[][] authorizationFailedDataProvider() {

        return new Object[][]{
                {true, DECISION, RULE_EFFECT_NOT_APPLICABLE, true, false},
                {true, RULE_EFFECT_NOT_APPLICABLE, RULE_EFFECT_PERMIT, false, true},
        };
    }

    @Test(dataProvider = "authorizationFailedDataProvider", expectedExceptions = PostAuthenticationFailedException
            .class)

    public void testIsAuthorized(boolean isAuthorizationEnabled, String replaceTarget, String replacement, boolean
            throwPolicyBuilderException, boolean throwEntitlementException) throws
            Exception {

        try (MockedStatic<FrameworkUtils> frameworkUtilsMockedStatic = mockStatic(FrameworkUtils.class);
             MockedStatic<PolicyCreatorUtil> policyCreatorUtilMockedStatic = mockStatic(PolicyCreatorUtil.class);
             MockedStatic<PolicyBuilder> policyBuilderMockedStatic = mockStatic(PolicyBuilder.class)) {

            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);

            if (!throwEntitlementException && throwPolicyBuilderException) {
                xacmlBasedAuthorizationHandler.handle(request, response, null);
            }
            ApplicationConfig applicationConfig = new ApplicationConfig(new ServiceProvider());
            applicationConfig.setEnableAuthorization(isAuthorizationEnabled);

            FrameworkUtils.endTenantFlow();
            SequenceConfig sequenceConfig = new SequenceConfig();
            sequenceConfig.setApplicationConfig(applicationConfig);
            AuthenticatedUser authenticatedUser = new AuthenticatedUser();
            authenticatedUser.setUserName(ADMIN_USER);
            sequenceConfig.setAuthenticatedUser(authenticatedUser);
            when(context.getSequenceConfig()).thenReturn(sequenceConfig);
            RequestElementDTO requestElementDTO = mock(RequestElementDTO.class);
            policyCreatorUtilMockedStatic.when(() -> PolicyCreatorUtil.createRequestElementDTO(any(RequestDTO.class)))
                    .thenReturn(requestElementDTO);
            PolicyBuilder policyBuilder = mock(PolicyBuilder.class);
            policyBuilderMockedStatic.when(PolicyBuilder::getInstance).thenReturn(policyBuilder);
            when(policyBuilder.buildRequest(any(RequestElementDTO.class))).thenReturn(POLICY);
            EntitlementService entitlementService = mock(EntitlementService.class);
            AppAuthzDataholder.getInstance().setEntitlementService(entitlementService);
            when(entitlementService.getDecision(anyString())).thenReturn(xacmlResponse);
            xacmlResponse = xacmlResponse.replace(replaceTarget, replacement);
            when(entitlementService.getDecision(anyString())).thenReturn(xacmlResponse);

            if (throwEntitlementException) {
                when(entitlementService.getDecision(anyString())).thenThrow(new EntitlementException(ERROR));
            } else if (throwPolicyBuilderException) {
                when(policyBuilder.buildRequest(any(RequestElementDTO.class))).thenThrow(new PolicyBuilderException(ERROR));
            }
            xacmlBasedAuthorizationHandler.handle(request, response, context);
        }
    }
}
