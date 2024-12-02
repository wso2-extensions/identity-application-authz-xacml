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

import org.wso2.carbon.identity.api.server.entitlement.v1.*;
import org.wso2.carbon.identity.api.server.entitlement.v1.model.*;
import java.util.List;

import javax.ws.rs.core.Response;

public class EntitlementsApiServiceImpl implements EntitlementsApiService {

    @Override
    public Response addSubscriber(PublisherDataHolderDTO isPDPPolicy) {

        // do some magic!
        return Response.ok().entity("magic!").build();
    }

    @Override
    public Response deleteSubscriber(String id) {

        // do some magic!
        return Response.ok().entity("magic!").build();
    }

    @Override
    public Response editSubscriber(PublisherDataHolderDTO isPDPPolicy, String id) {

        // do some magic!
        return Response.ok().entity("magic!").build();
    }

    @Override
    public Response entitlementsCombiningAlgorithmGet() {

        // do some magic!
        return Response.ok().entity("magic!").build();
    }

    @Override
    public Response entitlementsCombiningAlgorithmPatch(PolicyCombiningAlgorithmDTO policyCombiningAlgorithmDTO) {

        // do some magic!
        return Response.ok().entity("magic!").build();
    }

    @Override
    public Response entitlementsPoliciesIdDelete(String id) {

        // do some magic!
        return Response.ok().entity("magic!").build();
    }

    @Override
    public Response entitlementsPoliciesIdGet(String id, String version) {

        // do some magic!
        return Response.ok().entity("magic!").build();
    }

    @Override
    public Response entitlementsPoliciesIdPatch(String id, List<PolicyDTO> policyDTO) {

        // do some magic!
        return Response.ok().entity("magic!").build();
    }

    @Override
    public Response entitlementsPoliciesPost(List<PolicyDTO> policyDTO) {

        // do some magic!
        return Response.ok().entity("magic!").build();
    }

    @Override
    public Response getAllPolicies(Boolean isPDPPolicy, String policyType, String policySearchString, Integer pageNumber) {

        // do some magic!
        return Response.ok().entity("magic!").build();
    }

    @Override
    public Response getAllSubscribers(String subscriberSearchString) {

        // do some magic!
        return Response.ok().entity("magic!").build();
    }

    @Override
    public Response getSubscriber(String id) {

        // do some magic!
        return Response.ok().entity("magic!").build();
    }

    @Override
    public Response publishPolicies(PolicySubscriberDTO subscriberSearchString) {

        // do some magic!
        return Response.ok().entity("magic!").build();
    }
}
