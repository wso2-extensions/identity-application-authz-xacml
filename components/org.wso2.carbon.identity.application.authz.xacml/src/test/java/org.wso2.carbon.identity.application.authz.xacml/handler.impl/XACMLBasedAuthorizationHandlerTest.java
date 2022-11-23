/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockObjectFactory;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.testng.IObjectFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;
import org.wso2.balana.utils.exception.PolicyBuilderException;
import org.wso2.balana.utils.policy.PolicyBuilder;
import org.wso2.balana.utils.policy.dto.RequestElementDTO;
import org.wso2.carbon.base.CarbonBaseConstants;
import org.wso2.carbon.context.PrivilegedCarbonContext;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.nio.file.Paths;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import static org.wso2.carbon.utils.multitenancy.MultitenantConstants.SUPER_TENANT_DOMAIN_NAME;

/**
 * XACMLBasedAuthorizationHandlerTest defines unit tests for XACMLBasedAuthorizationHandler class.
 */
@PrepareForTest({LogFactory.class, FrameworkUtils.class, PolicyCreatorUtil.class, PolicyBuilder.class, PrivilegedCarbonContext.class})
@PowerMockIgnore("javax.xml.*")
public class XACMLBasedAuthorizationHandlerTest {

    private XACMLBasedAuthorizationHandler xacmlBasedAuthorizationHandler;
    private AuthenticationContext context;
    private Log log = mock(Log.class);

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

    @ObjectFactory
    public IObjectFactory getObjectFactory() {

        return new PowerMockObjectFactory();
    }

    @BeforeClass
    public void init() {

        String carbonHome = Paths.get(System.getProperty("user.dir"), "target", "test-classes").toString();
        System.setProperty(CarbonBaseConstants.CARBON_HOME, carbonHome);
        System.setProperty(CarbonBaseConstants.CARBON_CONFIG_DIR_PATH, Paths.get(carbonHome, "conf").toString());

        mockStatic(LogFactory.class);
        when(LogFactory.getLog(XACMLBasedAuthorizationHandler.class)).thenReturn(log);
        xacmlBasedAuthorizationHandler = spy(new XACMLBasedAuthorizationHandler());
        context = mock(AuthenticationContext.class);
        when(log.isDebugEnabled()).thenReturn(true);
    }

    @Test
    public void testCreateRequestDTO() throws Exception {

        SequenceConfig sequenceConfig = mock(SequenceConfig.class);
        AuthenticatedUser authenticatedUser = mock(AuthenticatedUser.class);
        when(context.getSequenceConfig()).thenReturn(sequenceConfig);
        when(sequenceConfig.getAuthenticatedUser()).thenReturn(authenticatedUser);
        when(authenticatedUser.getUserName()).thenReturn(ADMIN_USER);
        RequestDTO requestDTO = WhiteboxImpl.invokeMethod(xacmlBasedAuthorizationHandler,
                "createRequestDTO", context);
        assertTrue(requestDTO.getRowDTOs().size() == 9);
    }

    @Test
    public void testEvaluateXACMLResponse() throws Exception {

        String response = WhiteboxImpl.invokeMethod(xacmlBasedAuthorizationHandler,
                "evaluateXACMLResponse", xacmlResponse);
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

        mockStatic(PrivilegedCarbonContext.class);
        PrivilegedCarbonContext privilegedCarbonContext = mock(PrivilegedCarbonContext.class);
        when(PrivilegedCarbonContext.getThreadLocalCarbonContext()).thenReturn(privilegedCarbonContext);
        when(privilegedCarbonContext.getTenantDomain()).thenReturn(SUPER_TENANT_DOMAIN_NAME);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        ApplicationConfig applicationConfig = new ApplicationConfig(new ServiceProvider());
        applicationConfig.setEnableAuthorization(isAuthorizationEnabled);
        mockStatic(FrameworkUtils.class);
        doNothing().when(FrameworkUtils.class);
        FrameworkUtils.endTenantFlow();
        SequenceConfig sequenceConfig = new SequenceConfig();
        sequenceConfig.setApplicationConfig(applicationConfig);
        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setUserName(ADMIN_USER);
        sequenceConfig.setAuthenticatedUser(authenticatedUser);
        when(context.getSequenceConfig()).thenReturn(sequenceConfig);
        RequestElementDTO requestElementDTO = mock(RequestElementDTO.class);
        mockStatic(PolicyCreatorUtil.class);
        when(PolicyCreatorUtil.createRequestElementDTO(any(RequestDTO.class))).thenReturn(requestElementDTO);
        PolicyBuilder policyBuilder = mock(PolicyBuilder.class);
        mockStatic(PolicyBuilder.class);
        when(PolicyBuilder.getInstance()).thenReturn(policyBuilder);
        when(policyBuilder.buildRequest(any(RequestElementDTO.class))).thenReturn(POLICY);
        EntitlementService entitlementService = mock(EntitlementService.class);
        AppAuthzDataholder.getInstance().setEntitlementService(entitlementService);
        when(entitlementService.getDecision(anyString())).thenReturn(xacmlResponse);

        xacmlResponse = xacmlResponse.replace(replaceTarget, replacement);
        when(entitlementService.getDecision(anyString())).thenReturn(xacmlResponse);
        PostAuthnHandlerFlowStatus flowStatus = xacmlBasedAuthorizationHandler.handle(request, response, context);
        assertEquals(PostAuthnHandlerFlowStatus.SUCCESS_COMPLETED, flowStatus);
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

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        if (!throwEntitlementException && throwPolicyBuilderException) {
            xacmlBasedAuthorizationHandler.handle(request, response, null);
        }
        ApplicationConfig applicationConfig = new ApplicationConfig(new ServiceProvider());
        applicationConfig.setEnableAuthorization(isAuthorizationEnabled);
        mockStatic(FrameworkUtils.class);
        doNothing().when(FrameworkUtils.class);
        FrameworkUtils.endTenantFlow();
        SequenceConfig sequenceConfig = new SequenceConfig();
        sequenceConfig.setApplicationConfig(applicationConfig);
        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setUserName(ADMIN_USER);
        sequenceConfig.setAuthenticatedUser(authenticatedUser);
        when(context.getSequenceConfig()).thenReturn(sequenceConfig);
        RequestElementDTO requestElementDTO = mock(RequestElementDTO.class);
        mockStatic(PolicyCreatorUtil.class);
        when(PolicyCreatorUtil.createRequestElementDTO(any(RequestDTO.class))).thenReturn(requestElementDTO);
        PolicyBuilder policyBuilder = mock(PolicyBuilder.class);
        mockStatic(PolicyBuilder.class);
        when(PolicyBuilder.getInstance()).thenReturn(policyBuilder);
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
