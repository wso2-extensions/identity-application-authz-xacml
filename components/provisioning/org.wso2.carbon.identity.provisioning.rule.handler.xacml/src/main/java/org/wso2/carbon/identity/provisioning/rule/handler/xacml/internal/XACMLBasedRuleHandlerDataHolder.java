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

package org.wso2.carbon.identity.provisioning.rule.handler.xacml.internal;

import org.wso2.carbon.identity.entitlement.EntitlementService;
import org.wso2.carbon.identity.provisioning.rules.ProvisioningHandler;


public class XACMLBasedRuleHandlerDataHolder {

    private static XACMLBasedRuleHandlerDataHolder instance = new XACMLBasedRuleHandlerDataHolder();
    private EntitlementService entitlementService = null;
    private ProvisioningHandler provisioningHandler = null;

    private XACMLBasedRuleHandlerDataHolder() {

    }

    public static XACMLBasedRuleHandlerDataHolder getInstance() {

        return instance;
    }

    public EntitlementService getEntitlementService(){
        return entitlementService;
    }

    public void setEntitlementService(EntitlementService entitlementService) {
        this.entitlementService = entitlementService;
    }

    public ProvisioningHandler getProvisioningHandler(){
        return provisioningHandler;
    }

    public void setOutboundProvisioningService (ProvisioningHandler provisioningHandler) {
        this.provisioningHandler = provisioningHandler;
    }
}
