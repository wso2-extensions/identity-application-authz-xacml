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

package org.wso2.carbon.identity.api.server.entitlement.v1.impl;

import org.junit.BeforeClass;
import org.junit.Test;
import org.wso2.carbon.identity.api.server.entitlement.v1.core.ServerEntitlementsManagementService;
import org.wso2.carbon.identity.api.server.entitlement.v1.model.PolicyCombiningAlgorithmDTO;
import org.wso2.carbon.identity.api.server.entitlement.v1.model.PolicyDTO;
import org.wso2.carbon.identity.api.server.entitlement.v1.model.PublisherDataHolderDTO;
import org.wso2.carbon.identity.entitlement.EntitlementException;
import org.wso2.carbon.identity.entitlement.dto.PaginatedPolicySetDTO;
import org.wso2.carbon.identity.entitlement.dto.PublisherDataHolder;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class EntitlementsApiServiceImplTest {

    private static EntitlementsApiServiceImpl entitlementsApiService;
    private static ServerEntitlementsManagementService mockServerEntitlementsManagementService;

    @BeforeClass
    public static void init() {

        entitlementsApiService = new EntitlementsApiServiceImpl();
        mockServerEntitlementsManagementService = mock(ServerEntitlementsManagementService.class);
    }

    @Test
    public void testAddSubscriber() throws EntitlementException {

        PublisherDataHolderDTO publisherDataHolderDTO = mock(PublisherDataHolderDTO.class);
        Response response = entitlementsApiService.addSubscriber(publisherDataHolderDTO);
        verify(mockServerEntitlementsManagementService, times(1)).addSubscriber(null);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testAddSubscriberWithException() throws EntitlementException {

        PublisherDataHolderDTO publisherDataHolderDTO = new PublisherDataHolderDTO();
        doThrow(new EntitlementException("Error")).when(entitlementsApiService).addSubscriber(any());
        Response response = entitlementsApiService.addSubscriber(publisherDataHolderDTO);
        verify(mockServerEntitlementsManagementService, times(1))
                .addSubscriber(any(PublisherDataHolder.class));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void testDeleteSubscriber() throws EntitlementException {

        String subscriberId = "testSubscriberId";
        Response response = entitlementsApiService.deleteSubscriber(subscriberId);
        verify(mockServerEntitlementsManagementService, times(1)).deleteSubscriber(subscriberId);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testDeleteSubscriberWithException() throws EntitlementException {

        String subscriberId = "testSubscriberId";
        doThrow(new EntitlementException("Error")).when(mockServerEntitlementsManagementService)
                .deleteSubscriber(subscriberId);
        Response response = entitlementsApiService.deleteSubscriber(subscriberId);
        verify(mockServerEntitlementsManagementService, times(1)).deleteSubscriber(subscriberId);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void testEditSubscriber() throws EntitlementException {

        PublisherDataHolderDTO publisherDataHolderDTO = new PublisherDataHolderDTO();
        Response response = entitlementsApiService.editSubscriber(publisherDataHolderDTO);
        verify(mockServerEntitlementsManagementService, times(1))
                .updateSubscriber(any(PublisherDataHolder.class));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testEntitlementsCombiningAlgorithmGet() throws EntitlementException {

        when(mockServerEntitlementsManagementService.getGlobalPolicyAlgorithm()).thenReturn("algorithm");
        Response response = entitlementsApiService.entitlementsCombiningAlgorithmGet();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("algorithm", response.getEntity());
    }

    @Test
    public void testEntitlementsCombiningAlgorithmGetWithException() throws EntitlementException {

        doThrow(new EntitlementException("Error")).when(mockServerEntitlementsManagementService)
                .getGlobalPolicyAlgorithm();
        Response response = entitlementsApiService.entitlementsCombiningAlgorithmGet();
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void testEntitlementsCombiningAlgorithmPatch() throws EntitlementException {

        PolicyCombiningAlgorithmDTO policyCombiningAlgorithmDTO = new PolicyCombiningAlgorithmDTO();
        Response response = entitlementsApiService.entitlementsCombiningAlgorithmPatch(policyCombiningAlgorithmDTO);
        verify(mockServerEntitlementsManagementService, times(1))
                .updateSubscriber(any(PublisherDataHolder.class));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testEntitlementsPoliciesIdDelete() throws EntitlementException {

        String id = "testId";
        Response response = entitlementsApiService.entitlementsPoliciesIdDelete(id);
        verify(mockServerEntitlementsManagementService, times(1)).deleteSubscriber(id);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testEntitlementsPoliciesIdGet() throws EntitlementException {

        String id = "testId";
        org.wso2.carbon.identity.entitlement.dto.PolicyDTO policyDTO = new org.wso2.carbon.identity.entitlement.dto.PolicyDTO();
        policyDTO.setPolicyId(id);
        when(mockServerEntitlementsManagementService.getPolicy(anyString())).thenReturn(policyDTO);
        Response response = entitlementsApiService.entitlementsPoliciesIdGet(id);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(policyDTO, response.getEntity());
    }

    @Test
    public void testEntitlementsPoliciesPatch() throws EntitlementException {

        PolicyDTO policyDTO = new PolicyDTO();
        Response response = entitlementsApiService.entitlementsPoliciesPatch(policyDTO);
        verify(mockServerEntitlementsManagementService, times(1))
                .updatePolicy(any(org.wso2.carbon.identity.entitlement.dto.PolicyDTO.class));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testEntitlementsPoliciesPost() throws EntitlementException {

        PolicyDTO policyDTO = new PolicyDTO();
        Response response = entitlementsApiService.entitlementsPoliciesPost(policyDTO);

        verify(mockServerEntitlementsManagementService, times(1))
                .addPolicy(any(org.wso2.carbon.identity.entitlement.dto.PolicyDTO.class));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetAllPolicies() throws EntitlementException {

        PaginatedPolicySetDTO paginatedPolicySetDTO = new PaginatedPolicySetDTO();
        when(mockServerEntitlementsManagementService.getAllPolicies(anyString(), anyString(), anyInt(), anyBoolean()))
                .thenReturn(paginatedPolicySetDTO);

        Response response = entitlementsApiService.getAllPolicies(true, "policyType", "search", 1);

        verify(mockServerEntitlementsManagementService, times(1)).getAllPolicies("policyType", "search", 1, true);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(paginatedPolicySetDTO, response.getEntity());
    }

    @Test
    public void testGetAllSubscribers() throws EntitlementException {

        String subscriberSearchString = "*";

        PublisherDataHolder publisherDataHolder = new PublisherDataHolder();
        String[] subscriberIds = new String[]{"subscriberId1", "subscriberId2"};
        when(mockServerEntitlementsManagementService.getSubscriberIds(anyString())).thenReturn(subscriberIds);

        Response response = entitlementsApiService.getAllSubscribers(subscriberSearchString, null);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(publisherDataHolder, response.getEntity());
    }

    @Test
    public void testGetSubscriber() throws EntitlementException {

        String subscriberSearchString = "*";
        String subscriberId = "id";

        PublisherDataHolder publisherDataHolder = new PublisherDataHolder();
        when(mockServerEntitlementsManagementService.getSubscriber(anyString())).thenReturn(publisherDataHolder);

        Response response = entitlementsApiService.getAllSubscribers(subscriberSearchString, subscriberId);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(publisherDataHolder, response.getEntity());
    }
}
