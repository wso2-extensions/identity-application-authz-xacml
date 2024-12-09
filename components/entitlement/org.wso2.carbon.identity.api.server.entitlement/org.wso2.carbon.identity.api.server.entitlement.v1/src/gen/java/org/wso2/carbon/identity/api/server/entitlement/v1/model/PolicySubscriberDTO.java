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

package org.wso2.carbon.identity.api.server.entitlement.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;


import io.swagger.annotations.*;
import java.util.Objects;
import javax.validation.Valid;
import javax.xml.bind.annotation.*;

public class PolicySubscriberDTO  {
  
    private List<String> policyIds = null;

    private List<String> subscriberIds = null;

    private String action;
    private String version;
    private Boolean enable;
    private Integer order;

    /**
    **/
    public PolicySubscriberDTO policyIds(List<String> policyIds) {

        this.policyIds = policyIds;
        return this;
    }
    
    @ApiModelProperty(value = "")
    @JsonProperty("policyIds")
    @Valid
    public List<String> getPolicyIds() {
        return policyIds;
    }
    public void setPolicyIds(List<String> policyIds) {
        this.policyIds = policyIds;
    }

    public PolicySubscriberDTO addPolicyIdsItem(String policyIdsItem) {
        if (this.policyIds == null) {
            this.policyIds = new ArrayList<>();
        }
        this.policyIds.add(policyIdsItem);
        return this;
    }

        /**
    **/
    public PolicySubscriberDTO subscriberIds(List<String> subscriberIds) {

        this.subscriberIds = subscriberIds;
        return this;
    }
    
    @ApiModelProperty(value = "")
    @JsonProperty("subscriberIds")
    @Valid
    public List<String> getSubscriberIds() {
        return subscriberIds;
    }
    public void setSubscriberIds(List<String> subscriberIds) {
        this.subscriberIds = subscriberIds;
    }

    public PolicySubscriberDTO addSubscriberIdsItem(String subscriberIdsItem) {
        if (this.subscriberIds == null) {
            this.subscriberIds = new ArrayList<>();
        }
        this.subscriberIds.add(subscriberIdsItem);
        return this;
    }

        /**
    **/
    public PolicySubscriberDTO action(String action) {

        this.action = action;
        return this;
    }
    
    @ApiModelProperty(value = "")
    @JsonProperty("action")
    @Valid
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }

    /**
    **/
    public PolicySubscriberDTO version(String version) {

        this.version = version;
        return this;
    }
    
    @ApiModelProperty(value = "")
    @JsonProperty("version")
    @Valid
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }

    /**
    **/
    public PolicySubscriberDTO enable(Boolean enable) {

        this.enable = enable;
        return this;
    }
    
    @ApiModelProperty(value = "")
    @JsonProperty("enable")
    @Valid
    public Boolean getEnable() {
        return enable;
    }
    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    /**
    **/
    public PolicySubscriberDTO order(Integer order) {

        this.order = order;
        return this;
    }
    
    @ApiModelProperty(value = "")
    @JsonProperty("order")
    @Valid
    public Integer getOrder() {
        return order;
    }
    public void setOrder(Integer order) {
        this.order = order;
    }



    @Override
    public boolean equals(java.lang.Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PolicySubscriberDTO policySubscriberDTO = (PolicySubscriberDTO) o;
        return Objects.equals(this.policyIds, policySubscriberDTO.policyIds) &&
            Objects.equals(this.subscriberIds, policySubscriberDTO.subscriberIds) &&
            Objects.equals(this.action, policySubscriberDTO.action) &&
            Objects.equals(this.version, policySubscriberDTO.version) &&
            Objects.equals(this.enable, policySubscriberDTO.enable) &&
            Objects.equals(this.order, policySubscriberDTO.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(policyIds, subscriberIds, action, version, enable, order);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class PolicySubscriberDTO {\n");
        
        sb.append("    policyIds: ").append(toIndentedString(policyIds)).append("\n");
        sb.append("    subscriberIds: ").append(toIndentedString(subscriberIds)).append("\n");
        sb.append("    action: ").append(toIndentedString(action)).append("\n");
        sb.append("    version: ").append(toIndentedString(version)).append("\n");
        sb.append("    enable: ").append(toIndentedString(enable)).append("\n");
        sb.append("    order: ").append(toIndentedString(order)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
    * Convert the given object to string with each line indented by 4 spaces
    * (except the first line).
    */
    private String toIndentedString(java.lang.Object o) {

        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n");
    }
}

