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

package org.wso2.carbon.identity.api.server.entitlement.common;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.entitlement.EntitlementAdminService;
import org.wso2.carbon.identity.entitlement.EntitlementPolicyAdminService;

import static org.testng.Assert.assertNotNull;

public class EntitlementManagementServiceHolderTest {

    private static MockedStatic<PrivilegedCarbonContext> mockedCarbonContextStatic;

    @BeforeClass
    public static void setUp() {

        EntitlementAdminService mockedEntitlementAdminService = Mockito.mock(EntitlementAdminService.class);
        EntitlementPolicyAdminService mockedEntitlementPolicyAdminService =
                Mockito.mock(EntitlementPolicyAdminService.class);
        mockedCarbonContextStatic = Mockito.mockStatic(PrivilegedCarbonContext.class);
        PrivilegedCarbonContext mockedCarbonContext = Mockito.mock(PrivilegedCarbonContext.class);

        mockedCarbonContextStatic.when(PrivilegedCarbonContext::getThreadLocalCarbonContext)
                .thenReturn(mockedCarbonContext);

        Mockito.when(mockedCarbonContext.getOSGiService(EntitlementAdminService.class, null))
                .thenReturn(mockedEntitlementAdminService);
        Mockito.when(mockedCarbonContext.getOSGiService(EntitlementPolicyAdminService.class, null))
                .thenReturn(mockedEntitlementPolicyAdminService);
    }

    @AfterClass
    public static void tearDown() {

        mockedCarbonContextStatic.close();
    }

    @Test
    public void testGetEntitlementAdminService() {

        EntitlementAdminService service = EntitlementManagementServiceHolder.getEntitlementAdminService();

        assertNotNull(service, "The returned EntitlementAdminService should not be null.");
    }

    @Test
    public void testGetEntitlementPolicyAdminService() {

        EntitlementPolicyAdminService service = EntitlementManagementServiceHolder.getEntitlementPolicyAdminService();

        assertNotNull(service, "The returned EntitlementPolicyAdminService should not be null.");
    }
}
