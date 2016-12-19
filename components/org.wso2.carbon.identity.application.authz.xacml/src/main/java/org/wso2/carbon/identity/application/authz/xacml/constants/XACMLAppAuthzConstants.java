/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
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

package org.wso2.carbon.identity.application.authz.xacml.constants;

public class XACMLAppAuthzConstants {

    public static final String SP_CATEGORY = "http://wso2.org/identity/sp";
    public static final String USER_CATEGORY = "http://wso2.org/identity/user";
    public static final String AUTH_CATEGORY = "http://wso2.org/identity/auth";
    public static final String AUTH_CONTEXT_PROPERTY_CATEGORY = "http://wso2.org/identity/auth-context-property";
    public static final String AUTH_CONTEXT_REQ_PARAM_CATEGORY = "http://wso2.org/identity/auth-context-request-param";
    public static final String AUTH_CONTEXT_REQ_HEADER_CATEGORY = "http://wso2" +
            ".org/identity/auth-context-request-header";
    public static final String ACTION_CATEGORY = "http://wso2.org/identity/identity-action";
    public static final String AUTH_ACTION_ID = ACTION_CATEGORY + "/action-name";
    public static final String AUTH_CTX_ID = AUTH_CATEGORY + "/auth-ctx-id";
    public static final String SP_NAME_ID = SP_CATEGORY + "/sp-name";
    public static final String SP_DOMAIN_ID = SP_CATEGORY + "/sp-tenant-domain";
    public static final String USERNAME_ID = USER_CATEGORY + "/username";
    public static final String USER_STORE_ID = USER_CATEGORY + "/user-store-domain";
    public static final String USER_TENANT_DOMAIN_ID = USER_CATEGORY + "/user-tenant-domain";

    public static final String INBOUND_PROTOCOL_ATTRIBUTE = AUTH_CATEGORY + "/inbound-protocol";
    public static final String CLIENT_IP_ATTRIBUTE = AUTH_CATEGORY + "/user-ip";


    private XACMLAppAuthzConstants() {

    }

}
