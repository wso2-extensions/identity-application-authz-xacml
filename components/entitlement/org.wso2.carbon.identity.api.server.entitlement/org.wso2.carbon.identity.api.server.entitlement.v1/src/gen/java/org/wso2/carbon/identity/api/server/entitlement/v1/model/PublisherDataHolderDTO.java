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
import org.wso2.carbon.identity.api.server.entitlement.v1.model.PublisherPropertyDTO;
import javax.validation.constraints.*;


import io.swagger.annotations.*;
import java.util.Objects;
import javax.validation.Valid;
import javax.xml.bind.annotation.*;

public class PublisherDataHolderDTO  {
  
    private String moduleName;
    private List<PublisherPropertyDTO> publisherPropertyDTO = null;


    /**
    **/
    public PublisherDataHolderDTO moduleName(String moduleName) {

        this.moduleName = moduleName;
        return this;
    }
    
    @ApiModelProperty(value = "")
    @JsonProperty("moduleName")
    @Valid
    public String getModuleName() {
        return moduleName;
    }
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
    **/
    public PublisherDataHolderDTO publisherPropertyDTO(List<PublisherPropertyDTO> publisherPropertyDTO) {

        this.publisherPropertyDTO = publisherPropertyDTO;
        return this;
    }
    
    @ApiModelProperty(value = "")
    @JsonProperty("publisherPropertyDTO")
    @Valid
    public List<PublisherPropertyDTO> getPublisherPropertyDTO() {
        return publisherPropertyDTO;
    }
    public void setPublisherPropertyDTO(List<PublisherPropertyDTO> publisherPropertyDTO) {
        this.publisherPropertyDTO = publisherPropertyDTO;
    }

    public PublisherDataHolderDTO addPublisherPropertyDTOItem(PublisherPropertyDTO publisherPropertyDTOItem) {
        if (this.publisherPropertyDTO == null) {
            this.publisherPropertyDTO = new ArrayList<>();
        }
        this.publisherPropertyDTO.add(publisherPropertyDTOItem);
        return this;
    }

    

    @Override
    public boolean equals(java.lang.Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PublisherDataHolderDTO publisherDataHolderDTO = (PublisherDataHolderDTO) o;
        return Objects.equals(this.moduleName, publisherDataHolderDTO.moduleName) &&
            Objects.equals(this.publisherPropertyDTO, publisherDataHolderDTO.publisherPropertyDTO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moduleName, publisherPropertyDTO);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class PublisherDataHolderDTO {\n");
        
        sb.append("    moduleName: ").append(toIndentedString(moduleName)).append("\n");
        sb.append("    publisherPropertyDTO: ").append(toIndentedString(publisherPropertyDTO)).append("\n");
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
