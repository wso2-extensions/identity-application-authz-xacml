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

package org.wso2.carbon.identity.application.authz.xacml.internal;

import org.osgi.service.component.ComponentContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.entitlement.EntitlementService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class AppAuthzServiceComponentTest {

    private AppAuthzServiceComponent appAuthzServiceComponent;
    private ComponentContext componentContext;
    private EntitlementService entitlementService;
    private AppAuthzDataholder appAuthzDataholder;

    @BeforeClass
    public void init() {

        appAuthzServiceComponent = spy(new AppAuthzServiceComponent());
        componentContext = mock(ComponentContext.class);
        entitlementService = mock(EntitlementService.class);
        appAuthzDataholder = AppAuthzDataholder.getInstance();
    }

    @AfterClass
    public void tearDown() {

        appAuthzDataholder.setEntitlementService(null);
    }

    @Test
    public void testActivate() throws Exception {

        appAuthzServiceComponent.activate(componentContext);
    }

    @Test
    public void testSetEntitlementService() {

        appAuthzServiceComponent.setEntitlementService(entitlementService);
        assertNotNull(appAuthzDataholder.getEntitlementService());
        assertEquals(appAuthzDataholder.getEntitlementService(), entitlementService);
    }

    @Test
    public void testUnsetEntitlementService() {

        appAuthzServiceComponent.unsetEntitlementService(entitlementService);
        assertNull(AppAuthzDataholder.getInstance().getEntitlementService());
    }
}
