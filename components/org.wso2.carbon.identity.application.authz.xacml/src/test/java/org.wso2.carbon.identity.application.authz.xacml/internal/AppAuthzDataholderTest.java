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

package org.wso2.carbon.identity.application.authz.xacml.internal;

import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.entitlement.EntitlementService;

/**
 * AppAuthzDataholderTest defines unit tests for AppAuthzDataholder class.
 */
public class AppAuthzDataholderTest {

    @Test
    public void testGetInstance() {

        Assert.assertEquals(AppAuthzDataholder.getInstance(), AppAuthzDataholder.getInstance());
    }

    @Test
    public void testGetAndSetEntitlementService() {

        AppAuthzDataholder appAuthzDataholder = AppAuthzDataholder.getInstance();
        Assert.assertNull(appAuthzDataholder.getEntitlementService());
        appAuthzDataholder.setEntitlementService(PowerMockito.mock(EntitlementService.class));
        Assert.assertNotNull(appAuthzDataholder.getEntitlementService());
        PowerMockito.verifyStatic(Mockito.times(1));
    }
}
