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

public class AttributeDTOs  {
  
    private String attributeValue;
    private String attributeDataType;
    private String attributeId;
    private String attributeCategory;

    /**
    **/
    public AttributeDTOs attributeValue(String attributeValue) {

        this.attributeValue = attributeValue;
        return this;
    }
    
    @ApiModelProperty(value = "")
    @JsonProperty("attributeValue")
    @Valid
    public String getAttributeValue() {
        return attributeValue;
    }
    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    /**
    **/
    public AttributeDTOs attributeDataType(String attributeDataType) {

        this.attributeDataType = attributeDataType;
        return this;
    }
    
    @ApiModelProperty(value = "")
    @JsonProperty("attributeDataType")
    @Valid
    public String getAttributeDataType() {
        return attributeDataType;
    }
    public void setAttributeDataType(String attributeDataType) {
        this.attributeDataType = attributeDataType;
    }

    /**
    **/
    public AttributeDTOs attributeId(String attributeId) {

        this.attributeId = attributeId;
        return this;
    }
    
    @ApiModelProperty(value = "")
    @JsonProperty("attributeId")
    @Valid
    public String getAttributeId() {
        return attributeId;
    }
    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    /**
    **/
    public AttributeDTOs attributeCategory(String attributeCategory) {

        this.attributeCategory = attributeCategory;
        return this;
    }
    
    @ApiModelProperty(value = "")
    @JsonProperty("attributeCategory")
    @Valid
    public String getAttributeCategory() {
        return attributeCategory;
    }
    public void setAttributeCategory(String attributeCategory) {
        this.attributeCategory = attributeCategory;
    }



    @Override
    public boolean equals(java.lang.Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AttributeDTOs attributeDTOs = (AttributeDTOs) o;
        return Objects.equals(this.attributeValue, attributeDTOs.attributeValue) &&
            Objects.equals(this.attributeDataType, attributeDTOs.attributeDataType) &&
            Objects.equals(this.attributeId, attributeDTOs.attributeId) &&
            Objects.equals(this.attributeCategory, attributeDTOs.attributeCategory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributeValue, attributeDataType, attributeId, attributeCategory);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class AttributeDTOs {\n");
        
        sb.append("    attributeValue: ").append(toIndentedString(attributeValue)).append("\n");
        sb.append("    attributeDataType: ").append(toIndentedString(attributeDataType)).append("\n");
        sb.append("    attributeId: ").append(toIndentedString(attributeId)).append("\n");
        sb.append("    attributeCategory: ").append(toIndentedString(attributeCategory)).append("\n");
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

