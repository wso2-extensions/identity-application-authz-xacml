# XACML Connector

XACML is very popular as a fine grained authorization method among the community.
Fine-grained authorization specifies the requirements and variables in an access control
policy that is used to authorize access to a resource. However, there are plenty of other
aspects of XACML other than it being just a fine grained authorization mechanism.

We have remove the default support for XACML from Identity Server 7.1 onwards.
However, you can still use the XACML feature by following the below guide to enable it.

## How to enable XACML connector

### Pre-requisites:

1. Download Identity Server latest pack using the following link.
   https://wso2.com/identity-server/

2. Download the XACML connector artifacts from here https://store.wso2.com/connector/identity-application-authz-xacml.

3. Unzip the downloaded pack.

### Setup database:

1. Execute the database scripts in `<XACML_CONNECTOR>/dbscripts` folder against identity DB.

### Configure Identity Server:

#### Linux users:

If you are a linux user, you can use the setup script from [setup.sh](setup.sh) to configure
the Identity Server. Make sure to change the XACML_CONNECTOR and IS_HOME variable value as per
your setup.

#### Non-linux users:

Please follow the below steps.

1. Add the jar files in `<XACML_CONNECTOR>/dropins` folder to the
`<IS_HOME>/repository/components/dropins` folder.

2. Add the jar file in  <XACML_CONNECTOR>/api-server folder to the 
`<IS_HOME>/repository/deployment/server/webapps/api/WEB-INF/lib` folder.

3. Add the `<XACML_CONNECTOR>/config-files/entitlement.properties` file to the 
`<IS_HOME>/repository/conf/identity`.

4. Add the `<XACML_CONNECTOR>/config-files/entitlement.properties.j2` file to the
`<IS_HOME>/repository/resources/conf/templates/repository/conf/identity`.

5. Add the `balana-config.xml` file to the `<IS_HOME>/repository/conf/security`.

6. Append the json content in the 
`<XACML_CONNECTOR>/config-files/org.wso2.carbon.identity.xacml.server.feature.default.json`
file to the `<IS_HOME>/repository/resources/conf/default.json`.

7. Add the XACML Scope validator to the Scope Validators list in `identity.xml.j2` file. Append the below XML tag inside of <ScopeValidators></ScopeValidators> tags.
```
{% if oauth.scope_validator.xacml.enable %}
   <ScopeValidator class="{{oauth.scope_validator.xacml.class}}"/>
{% endif %}

```

[//]: # (7. Add default XACML policies resides in folder)
[//]: # (`<XACML_CONNECTOR>/config-files/policies`  to the)
[//]: # (`<IS_HOME>/repository/resources/identity/policies/xacml/default` folder.)

8. Add XACML rest api webapp resides in folder `<XACML_CONNECTOR>/webapps` to the
`<IS_HOME>/repository/deployment/server/webapps` folder.

9. Add the below configuration to the `<IS_HOME>/repository/conf/deployment.toml` file.

```toml
# Entitlement Policies API
[[resource.access_control]]
context = "(.*)/api/identity/entitlement/v1/entitlements/policies(.*)"
secure = "true"
http_method = "GET"
scopes = ["internal_entitlement_policy_mgt_view"]

[[resource.access_control]]
context = "(.*)/api/identity/entitlement/v1/entitlements/policies(.*)"
secure = "true"
http_method = "POST"
scopes = ["internal_entitlement_policy_mgt_create"]

[[resource.access_control]]
context = "(.*)/api/identity/entitlement/v1/entitlements/policies(.*)"
secure = "true"
http_method = "PATCH"
scopes = ["internal_entitlement_policy_mgt_update"]

[[resource.access_control]]
context = "(.*)/api/identity/entitlement/v1/entitlements/policies(.*)"
secure = "true"
http_method = "DELETE"
scopes = ["internal_entitlement_policy_mgt_delete"]

# Entitlement Subscriber API
[[resource.access_control]]
context = "(.*)/api/identity/entitlement/v1/entitlements/subscribers(.*)"
secure = "true"
http_method = "GET"
scopes = ["internal_entitlement_subscriber_mgt_view"]

[[resource.access_control]]
context = "(.*)/api/identity/entitlement/v1/entitlements/subscribers(.*)"
secure = "true"
http_method = "POST"
scopes = ["internal_entitlement_subscriber_mgt_create"]

[[resource.access_control]]
context = "(.*)/api/identity/entitlement/v1/entitlements/subscribers(.*)"
secure = "true"
http_method = "PATCH"
scopes = ["internal_entitlement_subscriber_mgt_update"]

[[resource.access_control]]
context = "(.*)/api/identity/entitlement/v1/entitlements/subscribers(.*)"
secure = "true"
http_method = "DELETE"
scopes = ["internal_entitlement_subscriber_mgt_delete"]

# Entitlement Policy Publish API
[[resource.access_control]]
context = "(.*)/api/identity/entitlement/v1/entitlements/publish(.*)"
secure = "true"
http_method = "POST"
scopes = ["internal_entitlement_policy_publish"]

# Entitlement Global Policy Combining Algorithm API
[[resource.access_control]]
context = "(.*)/api/identity/entitlement/v1/entitlements/combining-algorithm(.*)"
secure = "true"
http_method = "GET"
scopes = ["internal_entitlement_gpa_get"]

[[resource.access_control]]
context = "(.*)/api/identity/entitlement/v1/entitlements/combining-algorithm(.*)"
secure = "true"
http_method = "PATCH"
scopes = ["internal_entitlement_gpa_set"]

[[api_resources]]
name = "Entitlement Policies API"
identifier = "/api/identity/entitlement/v1/entitlements/policies"
requiresAuthorization = true
description = "API representation of the Entitlements Policy Management API"
type = "TENANT"

[[api_resources.scopes]]
displayName = "Create Policy"
name = "internal_entitlement_policy_mgt_create"
description = "Create new Policy"

[[api_resources.scopes]]
displayName = "Update Policy"
name = "internal_entitlement_policy_mgt_update"
description = "Update Policy"

[[api_resources.scopes]]
displayName = "View Policy"
name = "internal_entitlement_policy_mgt_view"
description = "View Policy"

[[api_resources.scopes]]
displayName = "Delete Policy"
name = "internal_entitlement_policy_mgt_delete"
description = "Delete Policy"

[[api_resources]]
name = "Entitlement Subscriber API"
identifier = "/api/identity/entitlement/v1/entitlements/subscribers"
requiresAuthorization = true
description = "API representation of the Entitlement Subscriber Management API"
type = "TENANT"

[[api_resources.scopes]]
displayName = "Create Subscriber"
name = "internal_entitlement_subscriber_mgt_create"
description = "Create new Subscriber"

[[api_resources.scopes]]
displayName = "Update Subscriber"
name = "internal_entitlement_subscriber_mgt_update"
description = "Update Subscriber"

[[api_resources.scopes]]
displayName = "View Subscriber"
name = "internal_entitlement_subscriber_mgt_view"
description = "View Subscriber"

[[api_resources.scopes]]
displayName = "Delete Subscriber"
name = "internal_entitlement_subscriber_mgt_delete"
description = "Delete Subscriber"

[[api_resources]]
name = "Entitlement Global Policy Combining Algorithm API"
identifier = "/api/identity/entitlement/v1/entitlements/combining-algorithm"
requiresAuthorization = true
description = "API representation of the Entitlement Global Policy Combining Algorithm API"
type = "TENANT"

[[api_resources.scopes]]
displayName = "Set Global Policy Combining Algorithm API"
name = "internal_entitlement_gpa_set"
description = "Set Global Policy Combining Algorithm"

[[api_resources.scopes]]
displayName = "Get Global Policy Combining Algorithm API"
name = "internal_entitlement_gpa_get"
description = "Get Global Policy Combining Algorithm"

[[api_resources]]
name = "Entitlement Policy Publish API"
identifier = "/api/identity/entitlement/v1/entitlements/publish"
requiresAuthorization = true
description = "API representation of the Entitlements Policy Publish API"
type = "TENANT"

[[api_resources.scopes]]
displayName = "Publish Policy"
name = "internal_entitlement_policy_publish"
description = "Publish new Policy"

[[event_listener]]
id = "xacml_authorization_handler"
type = "org.wso2.carbon.identity.core.handler.AbstractIdentityHandler"
name = "org.wso2.carbon.identity.application.authz.xacml.handler.impl.XACMLBasedAuthorizationHandler"
order = 899
```

10. Add below configuration to enable the UI in `<IS_HOME>/repository/conf/deployment.toml` file.

```toml
[console.policyAdministration]
enabled = "true"

[console.ui]
isXacmlConnectorEnabled = "true"
```

11. Restart Identity Server.

#### Enable Authorization :

Enable `Enable authorization` for the desired application.

1. Navigate to console.
2. Navigate to applications and select desired application.
3. Go to Advanced section.
4. Enable the checkbox for `Enable authorization`.
