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

package org.wso2.carbon.identity.api.server.entitlement.v1;

import org.wso2.carbon.identity.api.server.entitlement.v1.*;
import org.wso2.carbon.identity.api.server.entitlement.v1.model.*;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import java.io.InputStream;
import java.util.List;
import org.wso2.carbon.identity.api.server.entitlement.v1.model.Error;
import java.util.List;
import org.wso2.carbon.identity.api.server.entitlement.v1.model.PolicyCombiningAlgorithmDTO;
import org.wso2.carbon.identity.api.server.entitlement.v1.model.PolicyDTO;
import org.wso2.carbon.identity.api.server.entitlement.v1.model.PolicySubscriberDTO;
import org.wso2.carbon.identity.api.server.entitlement.v1.model.PublisherDataHolderDTO;
import javax.ws.rs.core.Response;


public interface EntitlementsApiService {

      public Response addSubscriber(PublisherDataHolderDTO isPDPPolicy);

      public Response deleteSubscriber(String id);

      public Response editSubscriber(PublisherDataHolderDTO isPDPPolicy, String id);

      public Response entitlementsCombiningAlgorithmGet();

      public Response entitlementsCombiningAlgorithmPatch(PolicyCombiningAlgorithmDTO policyCombiningAlgorithmDTO);

      public Response entitlementsPoliciesIdDelete(String id);

      public Response entitlementsPoliciesIdGet(String id, String version);

      public Response entitlementsPoliciesIdPatch(String id, List<PolicyDTO> policyDTO);

      public Response entitlementsPoliciesPost(List<PolicyDTO> policyDTO);

      public Response getAllPolicies(Boolean isPDPPolicy, String policyType, String policySearchString, Integer pageNumber);

      public Response getAllSubscribers(String subscriberSearchString);

      public Response getSubscriber(String id);

      public Response publishPolicies(PolicySubscriberDTO subscriberSearchString);
}
