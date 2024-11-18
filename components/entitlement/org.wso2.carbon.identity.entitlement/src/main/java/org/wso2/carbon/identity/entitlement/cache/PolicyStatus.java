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

package org.wso2.carbon.identity.entitlement.cache;

import java.io.Serializable;

/**
 * Created by harsha on 1/25/15.
 */
public class PolicyStatus implements Serializable {

    private static final long serialVersionUID = -5173389109938987102L;

    private String policyId = null;
    private int statusCount = 0;
    private String policyAction;

    public PolicyStatus() {

    }

    public PolicyStatus(String policyId) {
        this.policyId = policyId;
    }

    public PolicyStatus(String policyId, int statusCount, String policyAction) {
        this.policyId = policyId;
        this.statusCount = statusCount;
        this.policyAction = policyAction;
    }

    public PolicyStatus(int statusCount, String policyAction) {
        this.statusCount = statusCount;
        this.policyAction = policyAction;
    }

    public int getStatusCount() {
        return statusCount;
    }

    public void setStatusCount(int statusCount) {
        this.statusCount = statusCount;
    }

    public String getPolicyAction() {
        return policyAction;
    }

    public void setPolicyAction(String policyAction) {
        this.policyAction = policyAction;
    }

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }


}
