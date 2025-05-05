# XACML Connector

XACML is very popular as a fine grained authorization method among the community.
Fine-grained authorization specifies the requirements and variables in an access control
policy that is used to authorize access to a resource. However, there are plenty of other
aspects of XACML other than it being just a fine grained authorization mechanism.

We have remove the default support for XACML from Identity Server 7.1 onwards.
However, you can still use the XACML feature by following the below guide to enable it.

## How to enable XACML connector

### Pre-requisites:

1. Download Identity Server latest pack using the following link 
   https://wso2.com/identity-server/ and note the unzipped location as <IS_HOME>.

2. Download the XACML connector artifacts from here https://store.wso2.com/connector/identity-application-authz-xacml 
   and note the unzipped location as <XACML_CONNECTOR>.

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

2. Add the jar file in  `<XACML_CONNECTOR>/api-server` folder to the 
`<IS_HOME>/repository/deployment/server/webapps/api/WEB-INF/lib` folder.

3. Add the `<XACML_CONNECTOR>/config-files/entitlement.properties` file to the 
`<IS_HOME>/repository/conf/identity`.

4. Add the `<XACML_CONNECTOR>/config-files/entitlement.properties.j2` file to the
`<IS_HOME>/repository/resources/conf/templates/repository/conf/identity`.

5. Add the `balana-config.xml` file to the `<IS_HOME>/repository/conf/security`.

   [//]: # (7. Add default XACML policies resides in folder)
   [//]: # (`<XACML_CONNECTOR>/config-files/policies`  to the)
   [//]: # (`<IS_HOME>/repository/resources/identity/policies/xacml/default` folder.)

6. Add XACML rest api webapp resides in folder `<XACML_CONNECTOR>/webapps` to the
   `<IS_HOME>/repository/deployment/server/webapps` folder.

7. Add the below configuration to the `<IS_HOME>/repository/conf/deployment.toml` file.

```toml
# Enable tenanted support.
[tenant_context.rewrite]
custom_webapps = ["/api/identity/entitlement/v1/"]

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

[identity.entitlement]
entitlement_engine_caching_interval = "1d"
JSON_shorten_form_enabled = false

[identity.entitlement.default_attribute_finder.properties]
MapFederatedUsersToLocal = true

[identity.entitlement.xacml_policy_status]
use_last_status_only = false

[identity.entitlement.policy_point.pdp]
enabled = true
default_caching_interval = "5m"
schema_validation_enabled = true
balana_config_enabled = true
multiple_decision_profile_enabled = true
global_policy_combining_algorithm = "urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:deny-overrides"
registry_level_policy_cache_clear = false
reference_max_policy_entries = "3000"
policy_finders = ["org.wso2.carbon.identity.entitlement.persistence.JDBCPolicyPersistenceManager"]
policy_store_module = "org.wso2.carbon.identity.entitlement.persistence.JDBCPolicyPersistenceManager"
policy_data_store_module = "org.wso2.carbon.identity.entitlement.policy.store.DefaultPolicyDataStore"

[identity.entitlement.policy_point.pdp.caching.decision_caching]
enabled = true
caching_interval = "$ref{identity.entitlement.policy_point.pdp.default_caching_interval}"

[identity.entitlement.policy_point.pdp.caching.attribute_caching]
enabled = true
caching_interval = "$ref{identity.entitlement.policy_point.pdp.default_caching_interval}"

[identity.entitlement.policy_point.pdp.caching.resource_caching]
enabled = true
caching_interval = "$ref{identity.entitlement.policy_point.pdp.default_caching_interval}"

[identity.entitlement.policy_point.pdp.caching.policy_caching]
caching_interval = "100s"

[identity.entitlement.policy_point.pap]
enabled = true
policy_add_start_enable = true
items_per_page = 10
store_metadata = true
entitlement_data_finders = ["org.wso2.carbon.identity.entitlement.pap.CarbonEntitlementDataFinder"]
policy_publisher_modules = ["org.wso2.carbon.identity.entitlement.policy.publisher.CarbonBasicPolicyPublisherModule"]
status_data_handlers = ["org.wso2.carbon.identity.entitlement.persistence.JDBCSimplePAPStatusDataHandler"]

[identity.entitlement.policy_point.pip]
attribute_designators = [
   "org.wso2.carbon.identity.entitlement.pip.DefaultAttributeFinder",
   "org.wso2.carbon.identity.application.authz.xacml.pip.AuthenticationContextAttributePIP"
]
resource_finders = ["org.wso2.carbon.identity.entitlement.pip.DefaultResourceFinder"]

[oauth.scope_validator.xacml]
enable = true
class = "org.wso2.carbon.identity.oauth2.validators.xacml.XACMLScopeValidator"

[[oauth.custom_scope_validator]]
class = "org.wso2.carbon.identity.oauth2.validators.xacml.XACMLScopeValidator"
```

8. Add below configuration to enable the UI in `<IS_HOME>/repository/conf/deployment.toml` file.

```toml
[console.policyAdministration]
enabled = "true"

[console.ui]
isXacmlConnectorEnabled = "true"
```

9. Restart Identity Server.

#### Enable Authorization :

Enable `Enable authorization` for the desired application.

1. Navigate to console.
2. Navigate to applications and select desired application.
3. Go to Advanced section.
4. Enable the checkbox for `Enable authorization`.
