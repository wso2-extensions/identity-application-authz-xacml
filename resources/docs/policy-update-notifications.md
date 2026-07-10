# Policy Update Notifications and External PEP Endpoints

WSO2 Identity Server can send notifications when XACML policies change. This allows external Policy Enforcement Points (PEPs) to invalidate their local decision caches and stay in sync with the PDP. Notifications can be sent as JSON, XML, or EMAIL to registered HTTP endpoints or email addresses.

> **Prerequisite**: The XACML connector must be installed. See the [setup guide](../../README.md).

---

## Overview

There are two related but distinct notification scenarios:

| Scenario | What triggers it | Who receives it |
|---|---|---|
| **Policy update notifications** | A policy is created, updated, enabled, disabled, or deleted in the PAP/PDP | External PEP endpoints or email recipients |
| **External PEP endpoint notifications** | Any policy change OR a change to user roles, permissions, or attributes | Pre-registered external PEP endpoints |

Both use the `entitlement.properties` and `msg-mgt.properties` configuration files (located in `<IS_HOME>/repository/conf/identity/`).

---

## Part 1: REST notifications for policy updates

### Step 1: Register the notification listener

Add the following to `<IS_HOME>/repository/conf/identity/entitlement.properties`:

```properties
PAP.Status.Data.Handler.2=org.wso2.carbon.identity.entitlement.EntitlementNotificationExtension
org.wso2.carbon.identity.entitlement.EntitlementNotificationExtension.1=pdpNotificationAction,ENABLE;DISABLE;UPDATE;DELETE
org.wso2.carbon.identity.entitlement.EntitlementNotificationExtension.2=papNotification,true
org.wso2.carbon.identity.entitlement.EntitlementNotificationExtension.3=pdpNotification,true
```

> **Note**: If you already have a `PAP.Status.Data.Handler.2` configured, use `PAP.Status.Data.Handler.3` for this entry.

### Step 2: Configure the notification destination

Add to `<IS_HOME>/repository/conf/identity/msg-mgt.properties`. The following sample sends a JSON POST to an endpoint on every policy change:

```properties
module.name.1=json
json.subscription.1=policyUpdate
json.subscription.policyUpdate.jsonContentTemplate=/path/to/templates/entitlement-json-template
json.subscription.policyUpdate.endpoint.1=pepEndpoint1
json.subscription.policyUpdate.endpoint.pepEndpoint1.address=https://localhost:9443/restEndpoint
json.subscription.policyUpdate.endpoint.pepEndpoint1.AuthenticationRequired=true
json.subscription.policyUpdate.endpoint.pepEndpoint1.username=admin
json.subscription.policyUpdate.endpoint.pepEndpoint1.password=admin
json.subscription.policyUpdate.jsonId=3232
```

**JSON template format** — create a file at the path above with content like:

```json
{"TargetID":"(targetId)","Username":"(username)","Target":"(target)","Action":"(action)"}
```

Available dynamic placeholders from the `policyUpdate` event:

| Placeholder | Value |
|---|---|
| `(operation)` | The operation performed (e.g., `UPDATE`) |
| `(targetId)` | The policy ID that changed |
| `(username)` | The user who made the change |
| `(target)` | The policy content |
| `(action)` | The action performed |

**Property reference**:

| Property | Description |
|---|---|
| `module.name.1=json` | Registers the JSON sending module in the notification framework |
| `json.subscription.1=policyUpdate` | Subscribes the JSON module to `policyUpdate` events |
| `json.subscription.policyUpdate.jsonContentTemplate` | Path to the JSON body template file |
| `json.subscription.policyUpdate.endpoint.1=pepEndpoint1` | Names the first target endpoint |
| `...endpoint.pepEndpoint1.address` | URL to POST the notification to |
| `...endpoint.pepEndpoint1.AuthenticationRequired=true` | Enable basic auth for the endpoint |
| `...endpoint.pepEndpoint1.username` | Username for basic auth |
| `...endpoint.pepEndpoint1.password` | Password for basic auth |

---

## Part 2: Notifications to external PEP endpoints

This covers a broader set of triggers — not just policy changes, but also user attribute/role/permission changes that should cause PEP caches to be cleared.

### Step 1: Add the PAP status data handler

In `<IS_HOME>/repository/conf/deployment.toml`, append the notification handler alongside the JDBC handler already added during connector setup:

```toml
[identity.entitlement.policy_point.pap]
status_data_handlers = [
  "org.wso2.carbon.identity.entitlement.persistence.JDBCSimplePAPStatusDataHandler",
  "org.wso2.carbon.identity.entitlement.SimplePAPStatusDataHandler"
]
```

> **Note**: Do not remove the `JDBCSimplePAPStatusDataHandler` entry — it was added during connector installation and is required for policy persistence.

### Step 2: Configure notification extension properties

Add to `deployment.toml` (adjust values as needed):

```toml
# Notification type — choose one: JSON, XML, or EMAIL
org.wso2.carbon.identity.entitlement.EntitlementNotificationExtension.1=notificationType,JSON

# Ignore SSL verification for the target URL (set false in production)
org.wso2.carbon.identity.entitlement.EntitlementNotificationExtension.2=ignoreServerVerification,true

# Target URL: address;username;password
org.wso2.carbon.identity.entitlement.EntitlementNotificationExtension.3=targetUrl,https://pep.example.com/invalidate;admin;admin

# Which PDP actions trigger notifications
org.wso2.carbon.identity.entitlement.EntitlementNotificationExtension.4=pdpNotificationAction,ENABLE;DISABLE;UPDATE;DELETE

# Notify on PAP changes
org.wso2.carbon.identity.entitlement.EntitlementNotificationExtension.5=papNotification,true

# Notify on PDP changes
org.wso2.carbon.identity.entitlement.EntitlementNotificationExtension.6=pdpNotification,true
```

For EMAIL notifications, replace `notificationType,JSON` with `notificationType,EMAIL` and use `emailAddress` instead of `targetUrl`:

```toml
org.wso2.carbon.identity.entitlement.EntitlementNotificationExtension.1=notificationType,EMAIL
org.wso2.carbon.identity.entitlement.EntitlementNotificationExtension.3=emailAddress,pep-admin@example.com
```

**Full attribute reference**:

| Attribute | Description |
|---|---|
| `notificationType` | `JSON`, `XML`, or `EMAIL` |
| `pdpNotification` | Send notification for PDP policy store changes (`true`/`false`) |
| `pdpNotificationAction` | Which PDP actions trigger notifications: `ENABLE`, `DISABLE`, `UPDATE`, `DELETE` (semicolon-separated) |
| `papNotification` | Send notification for PAP policy store changes (`true`/`false`) |
| `targetUrl` | `url;username;password` for JSON/XML endpoint delivery |
| `roleName` | For EMAIL type without `targetUrl` — send to all members of this role |
| `ignoreServerVerification` | Skip SSL certificate validation for the endpoint (`true`/`false`) |
| `emailTemplateFile` | Absolute path to a custom email template file |
| `emailSubject` | Subject line for EMAIL notifications |
| `emailAddress` | Recipient email address for EMAIL notifications |

### Step 3: Configure user attribute change notifications (optional)

To also send notifications when a user's attributes, roles, or permissions change, add to `msg-mgt.properties`:

```properties
module.name.1=email
email.subscription.1=userOperation
email.subscription.userOperation.template=<IS_HOME>/repository/conf/email/entitlement-email-config.xml
email.subscription.userOperation.salutation=Admin
email.subscription.userOperation.subject=User operation change notification
email.subscription.userOperation.endpoint.1=privateMail
email.subscription.userOperation.endpoint.privateMail.address=pep-admin@example.com
email.subscription.userOperation.endpoint.privateMail.subject=User operation change has occurred

email.subscription.2=policyUpdate
email.subscription.policyUpdate.template=<IS_HOME>/repository/conf/email/entitlement-email-config.xml
email.subscription.policyUpdate.salutation=Admin
email.subscription.policyUpdate.subject=XACML policy update notification
email.subscription.policyUpdate.endpoint.1=privateMail
email.subscription.policyUpdate.endpoint.privateMail.address=pep-admin@example.com
email.subscription.policyUpdate.endpoint.privateMail.subject=Policy update notification

threadPool.size=10
```

**Sample email template** — create at `<IS_HOME>/repository/conf/email/entitlement-email-config.xml`:

```
Hi {username},

The XACML PDP policy store has been changed.

Policy Id : {targetId}
Action    : {action}
Policy    : {target}

Best regards,
WSO2 Identity Server
```

### Step 4: SSL certificates for HTTPS endpoints

If your PEP endpoint uses HTTPS, import its certificate into the IS trust store:

```bash
keytool -import -alias pep-cert \
  -file /path/to/pep-certificate.crt \
  -keystore <IS_HOME>/repository/resources/security/client-truststore.jks
```

The default trust store password is `wso2carbon`.

---

## Restart

After making configuration changes, restart WSO2 Identity Server for them to take effect.
