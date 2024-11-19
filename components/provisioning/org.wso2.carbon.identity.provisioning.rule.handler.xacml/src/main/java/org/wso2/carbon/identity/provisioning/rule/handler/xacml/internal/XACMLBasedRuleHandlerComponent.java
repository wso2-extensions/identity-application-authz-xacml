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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.wso2.carbon.identity.entitlement.EntitlementService;
import org.wso2.carbon.identity.provisioning.rule.handler.xacml.XACMLBasedRuleHandler;
import org.wso2.carbon.identity.provisioning.rules.ProvisioningHandler;

@Component(
        name = "identity.provisioning.rule.handler.xacml.component",
        immediate = true
)
public class XACMLBasedRuleHandlerComponent {

    private static final Log log = LogFactory.getLog(XACMLBasedRuleHandlerComponent.class);

    @SuppressWarnings("unchecked")
    @Activate
    protected void activate(ComponentContext ctxt) {

        try {
            ctxt.getBundleContext().registerService(ProvisioningHandler.class,
                    XACMLBasedRuleHandler.getInstance(), null);
            if (log.isDebugEnabled()) {
                log.debug("XACML based outbound provisioning rule handler bundle is activated");
            }
        } catch (Throwable throwable) {
            log.error("Error while starting XACML based outbound provisioning rule handler", throwable);
        }
    }

    @Reference(
            name = "identity.entitlement.service",
            service = EntitlementService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetEntitlementService"
    )
    protected void setEntitlementService(EntitlementService entitlementService) {

        if (log.isDebugEnabled()) {
            log.debug("EntitlementService is set in the XACML based Outbound Provisioning bundle");
        }
        XACMLBasedRuleHandlerDataHolder.getInstance().setEntitlementService(entitlementService);
    }

    protected void unsetEntitlementService(EntitlementService entitlementService) {

        if (log.isDebugEnabled()) {
            log.debug("EntitlementService is unset in the XACML based Outbound Provisioning bundle");
        }
        XACMLBasedRuleHandlerDataHolder.getInstance().setEntitlementService(null);
    }
}
