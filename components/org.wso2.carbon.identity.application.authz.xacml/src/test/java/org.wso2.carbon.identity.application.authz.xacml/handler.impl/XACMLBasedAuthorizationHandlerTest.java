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
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.testng.IObjectFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.application.authentication.framework.config.model.SequenceConfig;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.entitlement.common.dto.RequestDTO;
import org.powermock.modules.testng.PowerMockObjectFactory;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * XACMLBasedAuthorizationHandlerTest defines unit tests for XACMLBasedAuthorizationHandler class.
 */
@PrepareForTest(LogFactory.class)
public class XACMLBasedAuthorizationHandlerTest {

    private XACMLBasedAuthorizationHandler xacmlBasedAuthorizationHandler;
    private AuthenticationContext context;
    private Log log = mock(Log.class);

    @ObjectFactory
    public IObjectFactory getObjectFactory() {

        return new PowerMockObjectFactory();
    }

    @BeforeClass
    public void init() {

        mockStatic(LogFactory.class);
        when(LogFactory.getLog(XACMLBasedAuthorizationHandler.class)).thenReturn(log);
        xacmlBasedAuthorizationHandler = XACMLBasedAuthorizationHandler.getInstance();
        context = mock(AuthenticationContext.class);
    }

    @Test
    public void testGetInstance() {

        XACMLBasedAuthorizationHandler xacmlBasedAuthorizationHandler = XACMLBasedAuthorizationHandler.getInstance();
        assertNotNull(xacmlBasedAuthorizationHandler);
    }

    @Test
    public void testCreateRequestDTO() throws Exception {

        SequenceConfig sequenceConfig = mock(SequenceConfig.class);
        AuthenticatedUser authenticatedUser = mock(AuthenticatedUser.class);
        when(context.getSequenceConfig()).thenReturn(sequenceConfig);
        when(sequenceConfig.getAuthenticatedUser()).thenReturn(authenticatedUser);
        when(authenticatedUser.getUserName()).thenReturn("adminUser");
        RequestDTO requestDTO = WhiteboxImpl.invokeMethod(xacmlBasedAuthorizationHandler,
                "createRequestDTO", context);
        assertTrue(requestDTO.getRowDTOs().size() == 8);
    }
}
