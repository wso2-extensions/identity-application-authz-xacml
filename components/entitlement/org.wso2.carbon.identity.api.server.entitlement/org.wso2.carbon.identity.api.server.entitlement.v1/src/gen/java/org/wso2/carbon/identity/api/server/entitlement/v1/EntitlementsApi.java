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

import org.springframework.beans.factory.annotation.Autowired;
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
import org.wso2.carbon.identity.api.server.entitlement.v1.EntitlementsApiService;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import io.swagger.annotations.*;

import javax.validation.constraints.*;

@Path("/entitlements")
@Api(description = "The entitlements API")

public class EntitlementsApi  {

    @Autowired
    private EntitlementsApiService delegate;

    @Valid
    @POST
    @Path("/subscribers")
    
    @Produces({ "application/json" })
    @ApiOperation(value = "Add Subscriber ", notes = "This API provides the capability to add a Subscriber.<br>   <b>Permission required:</b> <br>       * /permission/admin/manage/identity/entitlementsubscribermgt/create <br>   <b>Scope required:</b> <br>       * internal_entitlement_subscriber_mgt_create ", response = Void.class, authorizations = {
        @Authorization(value = "basicAuth"),
        @Authorization(value = "oauth2", scopes = {
            
        })
    }, tags={ "Policy Subscribers", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful", response = Void.class),
        @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
        @ApiResponse(code = 404, message = "Not Found", response = Error.class),
        @ApiResponse(code = 500, message = "Server Error", response = Error.class)
    })
    public Response addSubscriber(    @Valid @NotNull(message = "Property  cannot be null.") @ApiParam(value = "Whether PDP Policy or PAP Policy ",required=true)  @QueryParam("is PDP Policy") PublisherDataHolderDTO isPDPPolicy) {

        return delegate.addSubscriber(isPDPPolicy );
    }

    @Valid
    @DELETE
    @Path("/subscribers/{id}")
    
    @Produces({ "application/json" })
    @ApiOperation(value = "Delete Subscriber ", notes = "This API provides the capability to delete a Subscriber.<br>   <b>Permission required:</b> <br>       * /permission/admin/manage/identity/entitlementsubscribermgt/delete <br>   <b>Scope required:</b> <br>       * internal_entitlement_subscriber_mgt_delete ", response = Void.class, authorizations = {
        @Authorization(value = "basicAuth"),
        @Authorization(value = "oauth2", scopes = {
            
        })
    }, tags={ "Policy Subscribers", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Delete Successful", response = Void.class),
        @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
        @ApiResponse(code = 404, message = "Not Found", response = Error.class),
        @ApiResponse(code = 500, message = "Server Error", response = Error.class)
    })
    public Response deleteSubscriber(@ApiParam(value = "",required=true) @PathParam("id") String id) {

        return delegate.deleteSubscriber(id );
    }

    @Valid
    @PATCH
    @Path("/subscribers/{id}")
    
    @Produces({ "application/json" })
    @ApiOperation(value = "Edit Subscriber ", notes = "This API provides the capability to edit a Subscriber.<br>   <b>Permission required:</b> <br>       * /permission/admin/manage/identity/entitlementsubscribermgt/update <br>   <b>Scope required:</b> <br>       * internal_entitlement_subscriber_mgt_update ", response = Void.class, authorizations = {
        @Authorization(value = "basicAuth"),
        @Authorization(value = "oauth2", scopes = {
            
        })
    }, tags={ "Policy Subscribers", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful", response = Void.class),
        @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
        @ApiResponse(code = 404, message = "Not Found", response = Error.class),
        @ApiResponse(code = 500, message = "Server Error", response = Error.class)
    })
    public Response editSubscriber(    @Valid @NotNull(message = "Property  cannot be null.") @ApiParam(value = "Whether PDP Policy or PAP Policy ",required=true)  @QueryParam("is PDP Policy") PublisherDataHolderDTO isPDPPolicy, @ApiParam(value = "",required=true) @PathParam("id") String id) {

        return delegate.editSubscriber(isPDPPolicy,  id );
    }

    @Valid
    @GET
    @Path("/combining-algorithm")
    
    @Produces({ "application/json" })
    @ApiOperation(value = "Get global policy combining algorithm", notes = "This API provides the capability to get the gloabl policy combining algorithm.<br>   <b>Permission required:</b> <br>       * /permission/admin/manage/identity/policycombiningalgorithm/view <br>   <b>Scope required:</b> <br>       * internal_entitlement_mgt_policy_comb ", response = PolicyCombiningAlgorithmDTO.class, authorizations = {
        @Authorization(value = "basicAuth"),
        @Authorization(value = "oauth2", scopes = {
            
        })
    }, tags={ "Policy Combining Algorithms", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Policy combining algorithm is set.", response = PolicyCombiningAlgorithmDTO.class),
        @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
        @ApiResponse(code = 404, message = "Not Found", response = Error.class),
        @ApiResponse(code = 500, message = "Server Error", response = Error.class)
    })
    public Response entitlementsCombiningAlgorithmGet() {

        return delegate.entitlementsCombiningAlgorithmGet();
    }

    @Valid
    @PATCH
    @Path("/combining-algorithm")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Set policy combining algorithm", notes = "This API provides the capability to set the policy combining algorithm.<br>   <b>Permission required:</b> <br>       * /permission/admin/manage/identity/policycombiningalgorithm/update <br>   <b>Scope required:</b> <br>       * internal_entitlement_mgt_policy_comb ", response = Void.class, authorizations = {
        @Authorization(value = "basicAuth"),
        @Authorization(value = "oauth2", scopes = {
            
        })
    }, tags={ "Policy Combining Algorithms", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Policy combining algorithm is set.", response = Void.class),
        @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
        @ApiResponse(code = 404, message = "Not Found", response = Error.class),
        @ApiResponse(code = 500, message = "Server Error", response = Error.class)
    })
    public Response entitlementsCombiningAlgorithmPatch(@ApiParam(value = "" ) @Valid PolicyCombiningAlgorithmDTO policyCombiningAlgorithmDTO) {

        return delegate.entitlementsCombiningAlgorithmPatch(policyCombiningAlgorithmDTO );
    }

    @Valid
    @DELETE
    @Path("/policies/{id}")
    
    @Produces({ "application/json" })
    @ApiOperation(value = "Delete policy", notes = "This API provides the capability to delete a Policy.<br>   <b>Permission required:</b> <br>       * /permission/admin/manage/identity/entitlementmgt/delete <br>   <b>Scope required:</b> <br>       * internal_entitlement_mgt_delete ", response = Void.class, authorizations = {
        @Authorization(value = "basicAuth"),
        @Authorization(value = "oauth2", scopes = {
            
        })
    }, tags={ "Policy Administration Point", })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Policy deleted", response = Void.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
        @ApiResponse(code = 500, message = "Server Error", response = Error.class)
    })
    public Response entitlementsPoliciesIdDelete(@ApiParam(value = "",required=true) @PathParam("id") String id) {

        return delegate.entitlementsPoliciesIdDelete(id );
    }

    @Valid
    @GET
    @Path("/policies/{id}")
    
    @Produces({ "application/json" })
    @ApiOperation(value = "Get policy by id", notes = "This API provides the capability to Get a Policy.<br>   <b>Permission required:</b> <br>       * /permission/admin/manage/identity/entitlementmgt/view <br>   <b>Scope required:</b> <br>       * internal_entitlement_mgt_view ", response = PolicyDTO.class, responseContainer = "List", authorizations = {
        @Authorization(value = "basicAuth"),
        @Authorization(value = "oauth2", scopes = {
            
        })
    }, tags={ "Policy Administration Point", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "A policy object", response = PolicyDTO.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
        @ApiResponse(code = 404, message = "Not Found", response = Error.class),
        @ApiResponse(code = 500, message = "Server Error", response = Error.class)
    })
    public Response entitlementsPoliciesIdGet(@ApiParam(value = "",required=true) @PathParam("id") String id,     @Valid@ApiParam(value = "")  @QueryParam("version") String version) {

        return delegate.entitlementsPoliciesIdGet(id,  version );
    }

    @Valid
    @PATCH
    @Path("/policies/{id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Update policy", notes = "This API provides the capability to Update a Policy.<br>   <b>Permission required:</b> <br>       * /permission/admin/manage/identity/entitlementmgt/update <br>   <b>Scope required:</b> <br>       * internal_entitlement_mgt_update ", response = Void.class, authorizations = {
        @Authorization(value = "basicAuth"),
        @Authorization(value = "oauth2", scopes = {
            
        })
    }, tags={ "Policy Administration Point", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Policy updated.", response = Void.class),
        @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
        @ApiResponse(code = 404, message = "Not Found", response = Error.class),
        @ApiResponse(code = 500, message = "Server Error", response = Error.class)
    })
    public Response entitlementsPoliciesIdPatch(@ApiParam(value = "",required=true) @PathParam("id") String id, @ApiParam(value = "" ) @Valid List<PolicyDTO> policyDTO) {

        return delegate.entitlementsPoliciesIdPatch(id,  policyDTO );
    }

    @Valid
    @POST
    @Path("/policies")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Create policy", notes = "This API provides the capability to create Policies.<br>   <b>Permission required:</b> <br>       * /permission/admin/manage/identity/entitlementmgt/create <br>   <b>Scope required:</b> <br>       * internal_entitlement_mgt_create ", response = Void.class, authorizations = {
        @Authorization(value = "basicAuth"),
        @Authorization(value = "oauth2", scopes = {
            
        })
    }, tags={ "Policy Administration Point", })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "Policy created", response = Void.class),
        @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
        @ApiResponse(code = 404, message = "Not Found", response = Error.class),
        @ApiResponse(code = 500, message = "Server Error", response = Error.class)
    })
    public Response entitlementsPoliciesPost(@ApiParam(value = "" ) @Valid List<PolicyDTO> policyDTO) {

        return delegate.entitlementsPoliciesPost(policyDTO );
    }

    @Valid
    @GET
    @Path("/policies")
    
    @Produces({ "application/json" })
    @ApiOperation(value = "List All Policies ", notes = "This API provides the capability to retrieve the list of Policies available.<br>   <b>Permission required:</b> <br>       * /permission/admin/manage/identity/entitlementmgt/view <br>   <b>Scope required:</b> <br>       * internal_entitlement_mgt_view ", response = PolicyDTO.class, responseContainer = "List", authorizations = {
        @Authorization(value = "basicAuth"),
        @Authorization(value = "oauth2", scopes = {
            
        })
    }, tags={ "Policy Administration Point", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "A list of policies", response = PolicyDTO.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
        @ApiResponse(code = 404, message = "Not Found", response = Error.class),
        @ApiResponse(code = 500, message = "Server Error", response = Error.class)
    })
    public Response getAllPolicies(    @Valid @NotNull(message = "Property  cannot be null.") @ApiParam(value = "Whether PDP Policy or PAP Policy ",required=true, defaultValue="true") @DefaultValue("true")  @QueryParam("isPDPPolicy") Boolean isPDPPolicy,     @Valid@ApiParam(value = "Policy Type filter ", allowableValues="ALL, PDP_ENABLED, PDP_DISABLED")  @QueryParam("policyType") String policyType,     @Valid@ApiParam(value = "Policy search string filter ")  @QueryParam("policySearchString") String policySearchString,     @Valid @Min(1)@ApiParam(value = "Page Number filter ", defaultValue="1") @DefaultValue("1")  @QueryParam("pageNumber") Integer pageNumber) {

        return delegate.getAllPolicies(isPDPPolicy,  policyType,  policySearchString,  pageNumber );
    }

    @Valid
    @GET
    @Path("/subscribers")
    
    @Produces({ "application/json" })
    @ApiOperation(value = "List All Subscribers ", notes = "This API provides the capability to retrieve the list of Subscribers available.<br>   <b>Permission required:</b> <br>       * /permission/admin/manage/identity/entitlementsubscribermgt/view <br>   <b>Scope required:</b> <br>       * internal_entitlement_subscriber_mgt_view ", response = String.class, responseContainer = "List", authorizations = {
        @Authorization(value = "basicAuth"),
        @Authorization(value = "oauth2", scopes = {
            
        })
    }, tags={ "Policy Subscribers", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "A list of subscribers", response = String.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
        @ApiResponse(code = 404, message = "Not Found", response = Error.class),
        @ApiResponse(code = 500, message = "Server Error", response = Error.class)
    })
    public Response getAllSubscribers(    @Valid@ApiParam(value = "Subscriber search string filter ")  @QueryParam("subscriber search string") String subscriberSearchString) {

        return delegate.getAllSubscribers(subscriberSearchString );
    }

    @Valid
    @GET
    @Path("/subscribers/{id}")
    
    @Produces({ "application/json" })
    @ApiOperation(value = "Get Subscriber ", notes = "This API provides the capability to retrieve the Subscriber for a given id.<br>   <b>Permission required:</b> <br>       * /permission/admin/manage/identity/entitlementsubscribermgt/view <br>   <b>Scope required:</b> <br>       * internal_entitlement_subscriber_mgt_view ", response = PublisherDataHolderDTO.class, authorizations = {
        @Authorization(value = "basicAuth"),
        @Authorization(value = "oauth2", scopes = {
            
        })
    }, tags={ "Policy Subscribers", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "The subscribers for the given id", response = PublisherDataHolderDTO.class),
        @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
        @ApiResponse(code = 404, message = "Not Found", response = Error.class),
        @ApiResponse(code = 500, message = "Server Error", response = Error.class)
    })
    public Response getSubscriber(@ApiParam(value = "",required=true) @PathParam("id") String id) {

        return delegate.getSubscriber(id );
    }

    @Valid
    @GET
    @Path("/publish")
    
    @Produces({ "application/json" })
    @ApiOperation(value = "Publish Policies ", notes = "This API provides the capability to Publish Policies.<br>   <b>Permission required:</b> <br>       * /permission/admin/manage/identity/entitlementmgt/publish <br>   <b>Scope required:</b> <br>       * internal_entitlement_mgt_publish ", response = Void.class, authorizations = {
        @Authorization(value = "basicAuth"),
        @Authorization(value = "oauth2", scopes = {
            
        })
    }, tags={ "Publish Policies" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Published Policies", response = Void.class),
        @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
        @ApiResponse(code = 404, message = "Not Found", response = Error.class),
        @ApiResponse(code = 500, message = "Server Error", response = Error.class)
    })
    public Response publishPolicies(    @Valid@ApiParam(value = "Subscriber search string filter ")  @QueryParam("subscriber search string") PolicySubscriberDTO subscriberSearchString) {

        return delegate.publishPolicies(subscriberSearchString );
    }

}
