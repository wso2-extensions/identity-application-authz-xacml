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

import org.apache.axiom.om.util.Base64;
import org.wso2.carbon.identity.api.server.entitlement.v1.*;
import org.wso2.carbon.identity.api.server.entitlement.v1.core.ServerEntitlementsManagementService;
import org.wso2.carbon.identity.api.server.entitlement.v1.model.*;
import org.wso2.carbon.identity.entitlement.EntitlementException;
import org.wso2.carbon.identity.entitlement.dto.AttributeDTO;
import org.wso2.carbon.identity.entitlement.dto.PaginatedPolicySetDTO;
import org.wso2.carbon.identity.entitlement.dto.PublisherDataHolder;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

/**
 * Entitlements API service implementation.
 */
public class EntitlementsApiServiceImpl implements EntitlementsApiService {

    private final ServerEntitlementsManagementService serverEntitlementsManagementService =
            new ServerEntitlementsManagementService();

    @Override
    public Response addSubscriber(PublisherDataHolderDTO publisherDataHolderDTO) {

        PublisherDataHolder publisherDataHolder = convertToPublisherDataHolder(publisherDataHolderDTO);
        try {
            serverEntitlementsManagementService.addSubscriber(publisherDataHolder);
        } catch (EntitlementException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok().build();
    }

    @Override
    public Response deleteSubscriber(String subscriberId) {

        try {
            serverEntitlementsManagementService.deleteSubscriber(subscriberId);
        } catch (EntitlementException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok().build();
    }

    @Override
    public Response editSubscriber(PublisherDataHolderDTO publisherDataHolderDTO) {

        try {
            serverEntitlementsManagementService.updateSubscriber(convertToPublisherDataHolder(publisherDataHolderDTO));
        } catch (EntitlementException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok().build();
    }

    @Override
    public Response entitlementsCombiningAlgorithmGet() {

        String globalPolicyAlgorithm = null;
        try {
            globalPolicyAlgorithm = serverEntitlementsManagementService.getGlobalPolicyAlgorithm();
        } catch (EntitlementException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok().entity(globalPolicyAlgorithm).build();
    }

    @Override
    public Response entitlementsCombiningAlgorithmPatch(PolicyCombiningAlgorithmDTO policyCombiningAlgorithmDTO) {

        try {
            serverEntitlementsManagementService.setGlobalPolicyAlgorithm(
                    policyCombiningAlgorithmDTO.getPolicyCombiningAlgorithm());
        } catch (EntitlementException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok().build();
    }

    @Override
    public Response entitlementsPoliciesIdDelete(String id) {

        try {
            serverEntitlementsManagementService.removePolicy(new String(Base64.decode(id)));
        } catch (EntitlementException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok().build();
    }

    @Override
    public Response entitlementsPoliciesIdGet(String id) {

        org.wso2.carbon.identity.entitlement.dto.PolicyDTO policyDTO = null;
        try {
            policyDTO = serverEntitlementsManagementService.getPolicy(new String(Base64.decode(id)));
        } catch (EntitlementException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok().entity(policyDTO).build();
    }

    @Override
    public Response entitlementsPoliciesPatch(PolicyDTO policyDTO) {

        org.wso2.carbon.identity.entitlement.dto.PolicyDTO policy = convertToPolicyDTO(policyDTO);
        try {
            serverEntitlementsManagementService.updatePolicy(policy);
        } catch (EntitlementException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok().build();
    }

    @Override
    public Response entitlementsPoliciesPost(PolicyDTO policyDTO) {

        org.wso2.carbon.identity.entitlement.dto.PolicyDTO policy = convertToPolicyDTO(policyDTO);
        try {
            serverEntitlementsManagementService.addPolicy(policy);
        } catch (EntitlementException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok().build();
    }

    @Override
    public Response getAllPolicies(Boolean isPDPPolicy, String policyType, String policySearchString,
                                   Integer pageNumber) {

        PaginatedPolicySetDTO policyIds = null;
        try {
            policyIds = serverEntitlementsManagementService.getAllPolicies(policyType, policySearchString, pageNumber,
                    isPDPPolicy);
        } catch (EntitlementException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok().entity(policyIds).build();
    }

    @Override
    public Response getAllSubscribers(String subscriberSearchString, String subscriberId) {

        String[] subscriberIds = null;
        try {
            if (subscriberId != null) {
                return Response.ok().entity(serverEntitlementsManagementService.getSubscriber(subscriberId)).build();
            }
            subscriberIds = serverEntitlementsManagementService.getSubscriberIds(subscriberSearchString);
        } catch (EntitlementException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok().entity(subscriberIds).build();
    }

    @Override
    public Response publishPolicies(PolicySubscriberDTO policySubscriberDTO) {

        try {
            serverEntitlementsManagementService.publishPolicies(
                    policySubscriberDTO.getPolicyIds().toArray(new String[0]),
                    policySubscriberDTO.getSubscriberIds().toArray(new String[0]), policySubscriberDTO.getAction(),
                    policySubscriberDTO.getVersion(), policySubscriberDTO.getEnable(), policySubscriberDTO.getOrder());
        } catch (EntitlementException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok().build();
    }

    private PublisherDataHolder convertToPublisherDataHolder(PublisherDataHolderDTO publisherDataHolderDTO) {

        PublisherDataHolder publisherDataHolder = new PublisherDataHolder();
        publisherDataHolder.setModuleName(publisherDataHolderDTO.getModuleName());
        List<PublisherPropertyDTO> publisherPropertyDTOs = publisherDataHolderDTO.getPublisherPropertyDTO();
        List<org.wso2.carbon.identity.entitlement.dto.PublisherPropertyDTO> publisherPropertyDTOArray =
                new ArrayList<>();
        publisherPropertyDTOs.forEach(publisherPropertyDTO -> {
            org.wso2.carbon.identity.entitlement.dto.PublisherPropertyDTO publisherProperty
                    = new org.wso2.carbon.identity.entitlement.dto.PublisherPropertyDTO();
            publisherProperty.setId(publisherPropertyDTO.getId());
            publisherProperty.setDisplayName(publisherPropertyDTO.getDisplayName());
            publisherProperty.setValue(publisherPropertyDTO.getValue());
            publisherProperty.setRequired(publisherPropertyDTO.getRequired());
            publisherProperty.setDisplayOrder(publisherPropertyDTO.getDisplayOrder());
            publisherProperty.setSecret(publisherPropertyDTO.getSecret());
            publisherProperty.setModule(publisherPropertyDTO.getModule());
            publisherPropertyDTOArray.add(publisherProperty);
        });
        publisherDataHolder.setPropertyDTOs(publisherPropertyDTOArray.toArray(
                new org.wso2.carbon.identity.entitlement.dto.PublisherPropertyDTO[0]));
        return publisherDataHolder;
    }

    private org.wso2.carbon.identity.entitlement.dto.PolicyDTO convertToPolicyDTO(PolicyDTO policyDTO) {

        org.wso2.carbon.identity.entitlement.dto.PolicyDTO policy =
                new org.wso2.carbon.identity.entitlement.dto.PolicyDTO();
        policy.setPolicy(policyDTO.getPolicy());
        policy.setPolicyId(policyDTO.getPolicyId());
        policy.setActive(policyDTO.getActive());
        policy.setPromote(policyDTO.getPromote());
        policy.setPolicyType(policyDTO.getPolicyType());
        policy.setPolicyEditor(policyDTO.getPolicyEditor());
        List<String> policyEditorData = policyDTO.getPolicyEditorData();
        policy.setPolicyEditorData(policyEditorData.toArray(new String[0]));
        policy.setPolicyOrder(policyDTO.getPolicyOrder());
        policy.setVersion(policyDTO.getVersion());
        policy.setLastModifiedTime(policyDTO.getLastModifiedTime());
        policy.setLastModifiedUser(policyDTO.getLastModifiedUser());
        List<AttributeDTO> attributeDTOs = new ArrayList<>();
        policyDTO.getAttributeDTOs().forEach(attributeDTO -> {
            AttributeDTO attribute = new AttributeDTO();
            attribute.setAttributeValue(attributeDTO.getAttributeValue());
            attribute.setAttributeDataType(attributeDTO.getAttributeDataType());
            attribute.setAttributeId(attributeDTO.getAttributeId());
            attribute.setCategory(attributeDTO.getAttributeCategory());
            attributeDTOs.add(attribute);
        });
        policy.setAttributeDTOs(attributeDTOs.toArray(new AttributeDTO[0]));
        List<String> policySetIdReferences = policyDTO.getPolicySetIdReferences();
        policy.setPolicySetIdReferences(policySetIdReferences.toArray(new String[0]));
        List<String> policyIdReferences = policyDTO.getPolicyIdReferences();
        policy.setPolicyIdReferences(policyIdReferences.toArray(new String[0]));

        return policy;
    }
}
