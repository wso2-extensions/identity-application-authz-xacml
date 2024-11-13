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
