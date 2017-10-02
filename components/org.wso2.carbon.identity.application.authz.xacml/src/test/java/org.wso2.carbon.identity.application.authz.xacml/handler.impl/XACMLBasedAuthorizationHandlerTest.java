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
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.testng.Assert;
import org.testng.IObjectFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;
import org.wso2.balana.utils.policy.PolicyBuilder;
import org.wso2.carbon.identity.application.authentication.framework.config.model.SequenceConfig;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.application.authentication.framework.util.FrameworkUtils;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.identity.entitlement.common.dto.RequestDTO;

/**
 * XACMLBasedAuthorizationHandlerTest defines unit tests for XACMLBasedAuthorizationHandler class.
 */

@PrepareForTest({LogFactory.class, FrameworkUtils.class, PolicyBuilder.class, IdentityUtil.class})
public class XACMLBasedAuthorizationHandlerTest {

    private XACMLBasedAuthorizationHandler xacmlBasedAuthorizationHandler;
    private AuthenticationContext context;
    private Log log = PowerMockito.mock(Log.class);

    @ObjectFactory
    public IObjectFactory getObjectFactory() {

        return new org.powermock.modules.testng.PowerMockObjectFactory();
    }

    @BeforeClass
    public void init() {

        PowerMockito.mockStatic(LogFactory.class);
        PowerMockito.when(LogFactory.getLog(XACMLBasedAuthorizationHandler.class)).thenReturn(log);
        xacmlBasedAuthorizationHandler = XACMLBasedAuthorizationHandler.getInstance();
        context = PowerMockito.mock(AuthenticationContext.class);
    }

    @Test
    public void testGetInstance() {

        XACMLBasedAuthorizationHandler xacmlBasedAuthorizationHandler = XACMLBasedAuthorizationHandler.getInstance();
        Assert.assertNotNull(xacmlBasedAuthorizationHandler);
        Assert.assertEquals(XACMLBasedAuthorizationHandler.getInstance(), xacmlBasedAuthorizationHandler);
    }

    @Test
    public void testCreateRequestDTO() throws Exception {

        SequenceConfig sequenceConfig = PowerMockito.mock(SequenceConfig.class);
        AuthenticatedUser authenticatedUser = PowerMockito.mock(AuthenticatedUser.class);
        PowerMockito.when(context.getSequenceConfig()).thenReturn(sequenceConfig);
        PowerMockito.when(sequenceConfig.getAuthenticatedUser()).thenReturn(authenticatedUser);
        PowerMockito.when(authenticatedUser.getUserName()).thenReturn("adminUser");
        RequestDTO requestDTO = WhiteboxImpl.invokeMethod(xacmlBasedAuthorizationHandler,
                "createRequestDTO", context);
        Assert.assertTrue(requestDTO.getRowDTOs().size() == 8);
    }

}
