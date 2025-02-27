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

package org.wso2.carbon.identity.entitlement.dto;

import java.io.Serializable;
import java.util.Arrays;

/**
 * encapsulates the policy data that is stored in the policy store
 */
public class PolicyStoreDTO implements Serializable {

    private String policyId;

    private String policy;

    private int policyOrder;

    private boolean active;

    private boolean setOrder;

    private boolean setActive;

    private String version;

    private AttributeDTO[] attributeDTOs = new AttributeDTO[0];

    public PolicyStoreDTO() {

    }

    public PolicyStoreDTO(PolicyStoreDTO policyStoreDTO) {

        this.policyId = policyStoreDTO.getPolicyId();
        this.policy = policyStoreDTO.getPolicy();
        this.policyOrder = policyStoreDTO.getPolicyOrder();
        this.active = policyStoreDTO.isActive();
        this.setOrder = policyStoreDTO.isSetOrder();
        this.setActive = policyStoreDTO.isSetActive();
        this.version = policyStoreDTO.getVersion();
        this.attributeDTOs = policyStoreDTO.getAttributeDTOs();
    }

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public int getPolicyOrder() {
        return policyOrder;
    }

    public void setPolicyOrder(int policyOrder) {
        this.policyOrder = policyOrder;
    }

    public AttributeDTO[] getAttributeDTOs() {
        return Arrays.copyOf(attributeDTOs, attributeDTOs.length);
    }

    public void setAttributeDTOs(AttributeDTO[] attributeDTOs) {
        this.attributeDTOs = Arrays.copyOf(attributeDTOs, attributeDTOs.length);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isSetOrder() {
        return setOrder;
    }

    public void setSetOrder(boolean setOrder) {
        this.setOrder = setOrder;
    }

    public boolean isSetActive() {
        return setActive;
    }

    public void setSetActive(boolean setActive) {
        this.setActive = setActive;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
