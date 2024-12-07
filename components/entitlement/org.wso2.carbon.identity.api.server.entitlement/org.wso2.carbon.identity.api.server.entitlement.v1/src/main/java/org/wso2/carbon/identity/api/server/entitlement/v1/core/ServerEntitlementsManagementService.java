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

package org.wso2.carbon.identity.api.server.entitlement.v1.core;

import org.wso2.carbon.identity.api.server.entitlement.common.EntitlementManagementServiceHolder;
import org.wso2.carbon.identity.entitlement.EntitlementAdminService;
import org.wso2.carbon.identity.entitlement.EntitlementException;
import org.wso2.carbon.identity.entitlement.EntitlementPolicyAdminService;
import org.wso2.carbon.identity.entitlement.EntitlementServerException;
import org.wso2.carbon.identity.entitlement.dto.PaginatedPolicySetDTO;
import org.wso2.carbon.identity.entitlement.dto.PolicyDTO;
import org.wso2.carbon.identity.entitlement.dto.PublisherDataHolder;

/**
 * Entitlements Management Service class.
 */
public class ServerEntitlementsManagementService {

    private final EntitlementAdminService entitlementAdminService
            = EntitlementManagementServiceHolder.getEntitlementAdminService();
    private final EntitlementPolicyAdminService entitlementPolicyAdminService
            = EntitlementManagementServiceHolder.getEntitlementPolicyAdminService();

    /**
     * Add a new XACML policy..
     *
     * @param policyDTO Policy DTO object.
     * @throws EntitlementException Throws if invalid policy.
     */
    public void addPolicy(PolicyDTO policyDTO) throws EntitlementException {

        entitlementPolicyAdminService.addPolicy(policyDTO);
    }

    /**
     * Updates a given policy.
     *
     * @param policyDTO Policy DTO object.
     * @throws EntitlementException Throws if invalid policy.
     */
    public void updatePolicy(PolicyDTO policyDTO) throws EntitlementException {

        entitlementPolicyAdminService.updatePolicy(policyDTO);
    }

    /**
     * Get paginated policies.
     *
     * @param policyTypeFilter   Policy type to filter.
     * @param policySearchString Policy search String.
     * @param pageNumber         Page number.
     * @param isPDPPolicy        Whether this is a PDP policy or PAP policy.
     * @return Paginated and filtered policy set.
     * @throws EntitlementServerException Throws if a server error occurs during operation.
     */
    public PaginatedPolicySetDTO getAllPolicies(String policyTypeFilter, String policySearchString,
                                                int pageNumber, boolean isPDPPolicy) throws EntitlementServerException {

        try {
            return entitlementPolicyAdminService.getAllPolicies(policyTypeFilter, policySearchString, pageNumber,
                    isPDPPolicy);
        } catch (EntitlementException e) {
            throw new EntitlementServerException("Error while retrieving Policy Ids.", e);
        }
    }

    /**
     * Gets policy for given policy id.
     *
     * @param policyId    Policy id.
     * @return Returns policy.
     * @throws EntitlementException Throws if error occurred while getting the policy.
     */
    public PolicyDTO getPolicy(String policyId) throws EntitlementException {

        return entitlementPolicyAdminService.getPolicy(policyId, false);
    }

    /**
     * Removes policy for given policy id.
     *
     * @param policyId  Policy Id.
     * @throws EntitlementException Throws if error occurred while deleting the policy.
     */
    public void removePolicy(String policyId) throws EntitlementException {

        entitlementPolicyAdminService.removePolicy(policyId, false);
    }

    /**
     * Gets subscriber details.
     *
     * @param subscribeId Subscriber id.
     * @return Subscriber details as Subscriber DTO.
     * @throws EntitlementException Throws, if any error occurred while getting subscriber.
     */
    public PublisherDataHolder getSubscriber(String subscribeId) throws EntitlementException {

        return entitlementPolicyAdminService.getSubscriber(subscribeId);
    }

    /**
     * Gets all subscribers ids that is registered.
     *
     * @param searchString Search String.
     * @return Subscriber's ids as String array.
     * @throws EntitlementException Throws, if fails to get subscriber ids.
     */
    public String[] getSubscriberIds(String searchString) throws EntitlementException {

        return entitlementPolicyAdminService.getSubscriberIds(searchString);
    }

    /**
     * Add subscriber details in to registry.
     *
     * @param holder Subscriber data as PublisherDataHolder object.
     * @throws EntitlementException Throws, if fails to add subscriber.
     */
    public void addSubscriber(PublisherDataHolder holder) throws EntitlementException {

        entitlementPolicyAdminService.addSubscriber(holder);
    }

    /**
     * Update subscriber detail.
     *
     * @param holder Subscriber data as PublisherDataHolder object.
     * @throws EntitlementException Throws, if fails to update subscriber.
     */
    public void updateSubscriber(PublisherDataHolder holder) throws EntitlementException {

        entitlementPolicyAdminService.updateSubscriber(holder);
    }

    /**
     * Delete subscriber details.
     *
     * @param subscriberId Subscriber id.
     * @throws EntitlementException Throws, if fails to delete subscriber.
     */
    public void deleteSubscriber(String subscriberId) throws EntitlementException {

        entitlementPolicyAdminService.deleteSubscriber(subscriberId);
    }

    /**
     * Publishes given set of policies to all subscribers.
     *
     * @param policyIds     Policy ids to publish,  if null or empty, all policies are published.
     * @param subscriberIds Subscriber ids to publish,  if null or empty, all policies are published.
     * @param action        Publishing action.
     * @param version       Version.
     * @param enabled       Whether policy must be enabled or not.
     * @param order         Order of the policy.
     * @throws EntitlementException Throws, if fails to publish policies.
     */
    public void publishPolicies(String[] policyIds, String[] subscriberIds, String action, String version,
                                boolean enabled, int order) throws EntitlementException {

        entitlementPolicyAdminService.publishPolicies(policyIds, subscriberIds, action, version, enabled, order);
    }

    /**
     * Gets globally defined policy combining algorithm.
     *
     * @return Policy combining algorithm as a String.
     * @throws EntitlementException Throws if fails to get global policy algorithm.
     */
    public String getGlobalPolicyAlgorithm() throws EntitlementException {

        return entitlementAdminService.getGlobalPolicyAlgorithm();
    }

    /**
     * Sets policy combining algorithm globally.
     *
     * @param policyCombiningAlgorithm Policy combining algorithm as a String.
     * @throws EntitlementException Throws if fails to set global policy algorithm.
     */
    public void setGlobalPolicyAlgorithm(String policyCombiningAlgorithm) throws EntitlementException {

        entitlementAdminService.setGlobalPolicyAlgorithm(policyCombiningAlgorithm);
    }
}
