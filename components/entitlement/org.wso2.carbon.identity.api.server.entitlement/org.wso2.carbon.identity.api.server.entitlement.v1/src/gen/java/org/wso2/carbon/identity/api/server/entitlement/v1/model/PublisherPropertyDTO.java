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
import javax.validation.constraints.*;


import io.swagger.annotations.*;
import java.util.Objects;
import javax.validation.Valid;
import javax.xml.bind.annotation.*;

public class PublisherPropertyDTO  {
  
    private String id;
    private String displayName;
    private String value;
    private Boolean required;
    private Integer displayOrder;
    private Boolean secret;
    private String module;

    /**
    **/
    public PublisherPropertyDTO id(String id) {

        this.id = id;
        return this;
    }
    
    @ApiModelProperty(value = "")
    @JsonProperty("id")
    @Valid
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    /**
    **/
    public PublisherPropertyDTO displayName(String displayName) {

        this.displayName = displayName;
        return this;
    }
    
    @ApiModelProperty(value = "")
    @JsonProperty("displayName")
    @Valid
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
    **/
    public PublisherPropertyDTO value(String value) {

        this.value = value;
        return this;
    }
    
    @ApiModelProperty(value = "")
    @JsonProperty("value")
    @Valid
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    /**
    **/
    public PublisherPropertyDTO required(Boolean required) {

        this.required = required;
        return this;
    }
    
    @ApiModelProperty(value = "")
    @JsonProperty("required")
    @Valid
    public Boolean getRequired() {
        return required;
    }
    public void setRequired(Boolean required) {
        this.required = required;
    }

    /**
    **/
    public PublisherPropertyDTO displayOrder(Integer displayOrder) {

        this.displayOrder = displayOrder;
        return this;
    }
    
    @ApiModelProperty(value = "")
    @JsonProperty("displayOrder")
    @Valid
    public Integer getDisplayOrder() {
        return displayOrder;
    }
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    /**
    **/
    public PublisherPropertyDTO secret(Boolean secret) {

        this.secret = secret;
        return this;
    }
    
    @ApiModelProperty(value = "")
    @JsonProperty("secret")
    @Valid
    public Boolean getSecret() {
        return secret;
    }
    public void setSecret(Boolean secret) {
        this.secret = secret;
    }

    /**
    **/
    public PublisherPropertyDTO module(String module) {

        this.module = module;
        return this;
    }
    
    @ApiModelProperty(value = "")
    @JsonProperty("module")
    @Valid
    public String getModule() {
        return module;
    }
    public void setModule(String module) {
        this.module = module;
    }



    @Override
    public boolean equals(java.lang.Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PublisherPropertyDTO publisherPropertyDTO = (PublisherPropertyDTO) o;
        return Objects.equals(this.id, publisherPropertyDTO.id) &&
            Objects.equals(this.displayName, publisherPropertyDTO.displayName) &&
            Objects.equals(this.value, publisherPropertyDTO.value) &&
            Objects.equals(this.required, publisherPropertyDTO.required) &&
            Objects.equals(this.displayOrder, publisherPropertyDTO.displayOrder) &&
            Objects.equals(this.secret, publisherPropertyDTO.secret) &&
            Objects.equals(this.module, publisherPropertyDTO.module);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, displayName, value, required, displayOrder, secret, module);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class PublisherPropertyDTO {\n");
        
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
        sb.append("    value: ").append(toIndentedString(value)).append("\n");
        sb.append("    required: ").append(toIndentedString(required)).append("\n");
        sb.append("    displayOrder: ").append(toIndentedString(displayOrder)).append("\n");
        sb.append("    secret: ").append(toIndentedString(secret)).append("\n");
        sb.append("    module: ").append(toIndentedString(module)).append("\n");
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

