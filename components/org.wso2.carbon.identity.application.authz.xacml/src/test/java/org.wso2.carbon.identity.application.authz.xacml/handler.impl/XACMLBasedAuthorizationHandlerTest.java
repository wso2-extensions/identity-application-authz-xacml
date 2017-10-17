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
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;
import org.wso2.balana.utils.exception.PolicyBuilderException;
import org.wso2.balana.utils.policy.PolicyBuilder;
import org.wso2.balana.utils.policy.dto.RequestElementDTO;
import org.wso2.carbon.identity.application.authentication.framework.config.model.SequenceConfig;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.application.authentication.framework.util.FrameworkUtils;
import org.wso2.carbon.identity.application.authz.xacml.internal.AppAuthzDataholder;
import org.wso2.carbon.identity.entitlement.EntitlementException;
import org.wso2.carbon.identity.entitlement.EntitlementService;
import org.wso2.carbon.identity.entitlement.common.dto.RequestDTO;
import org.wso2.carbon.identity.entitlement.common.util.PolicyCreatorUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

/**
 * XACMLBasedAuthorizationHandlerTest defines unit tests for XACMLBasedAuthorizationHandler class.
 */
@PrepareForTest({LogFactory.class, FrameworkUtils.class, PolicyCreatorUtil.class, PolicyBuilder.class})
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
            +   "<ns:Result>"
            +   "<ns:Decision>"
            +   DECISION
            +   "</ns:Decision>"
            +   "</ns:Result>"
            +   "</ns:root>";

    @ObjectFactory
    public IObjectFactory getObjectFactory() {

        return new PowerMockObjectFactory();
    }

    @BeforeClass
    public void init() {

        mockStatic(LogFactory.class);
        when(LogFactory.getLog(XACMLBasedAuthorizationHandler.class)).thenReturn(log);
        xacmlBasedAuthorizationHandler = spy(XACMLBasedAuthorizationHandler.getInstance());
        context = mock(AuthenticationContext.class);
        when(log.isDebugEnabled()).thenReturn(true);
    }

    @Test
    public void testGetInstance() {

        XACMLBasedAuthorizationHandler xacmlBasedAuthorizationHandler = XACMLBasedAuthorizationHandler.getInstance();
        assertNotNull(xacmlBasedAuthorizationHandler);
        assertEquals(xacmlBasedAuthorizationHandler, XACMLBasedAuthorizationHandler.getInstance());
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
        assertTrue(requestDTO.getRowDTOs().size() == 8);
    }

    @Test
    public void testEvaluateXACMLResponse() throws Exception {

        String response = WhiteboxImpl.invokeMethod(xacmlBasedAuthorizationHandler, "evaluateXACMLResponse",
                xacmlResponse);
        assertEquals(response, DECISION);
    }

    @Test
    public void testIsAuthorized() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        assertFalse(xacmlBasedAuthorizationHandler.isAuthorized(request, response, null));

        mockStatic(FrameworkUtils.class);
        doNothing().when(FrameworkUtils.class);
        FrameworkUtils.endTenantFlow();
        SequenceConfig sequenceConfig = new SequenceConfig();
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
        assertFalse(xacmlBasedAuthorizationHandler.isAuthorized(request, response, context));

        xacmlResponse = xacmlResponse.replace(DECISION, RULE_EFFECT_NOT_APPLICABLE);
        when(entitlementService.getDecision(anyString())).thenReturn(xacmlResponse);
        assertTrue(xacmlBasedAuthorizationHandler.isAuthorized(request, response, context));

        xacmlResponse = xacmlResponse.replace(RULE_EFFECT_NOT_APPLICABLE, RULE_EFFECT_PERMIT);
        when(entitlementService.getDecision(anyString())).thenReturn(xacmlResponse);
        assertTrue(xacmlBasedAuthorizationHandler.isAuthorized(request, response, context));

        when(entitlementService.getDecision(anyString())).thenThrow(new EntitlementException(ERROR));
        assertFalse(xacmlBasedAuthorizationHandler.isAuthorized(request, response, context));

        when(policyBuilder.buildRequest(any(RequestElementDTO.class))).thenThrow(new PolicyBuilderException(ERROR));
        assertFalse(xacmlBasedAuthorizationHandler.isAuthorized(request, response, context));
    }
}
