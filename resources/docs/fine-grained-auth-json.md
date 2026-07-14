# Fine-grained Authorization using XACML Requests in JSON Format

WSO2 Identity Server supports XACML 3.0 requests and responses in JSON format, allowing you to integrate fine-grained authorization into your applications via standard REST calls without using XML.

> **Prerequisite**: The XACML connector must be installed. See the [setup guide](../../README.md).

---

## Overview

When your PEP sends an authorization request to the IS PDP via REST, you can use either XML or JSON as the content type. JSON format is more convenient for modern REST-based applications and produces more compact messages.

The endpoint for XACML decision requests is:

```
POST https://localhost:9443/api/identity/entitlement/decision/pdp
```

For tenant users:
```
POST https://localhost:9443/t/{tenant_domain}/api/identity/entitlement/decision/pdp
```

Authentication: **Basic Auth** (Base64-encoded `username:password` in the `Authorization` header).

---

## Scenario

A web application has a resource `index.jsp` that different user types can access with different actions:

| User | Permitted actions |
|---|---|
| `publicUser` | `view-welcome`, `view-summary` |
| `internalUser` | `view-status` |
| `adminUser` | `modify-welcome`, `modify-summary` |

All other requests are denied, and a denial obligation message is returned.

---

## Step 1: Create and activate the policy

1. Navigate to **Policy Administration** and click **New Policy > Write Policy in XML**.
2. Paste the following policy and click **Create**:

```xml
<Policy xmlns="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17"
    PolicyId="web-filter-policy-1"
    RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:first-applicable"
    Version="1.0">
  <Target>
    <AnyOf>
      <AllOf>
        <Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
          <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">index.jsp</AttributeValue>
          <AttributeDesignator
            AttributeId="urn:oasis:names:tc:xacml:1.0:resource:resource-id"
            Category="urn:oasis:names:tc:xacml:3.0:attribute-category:resource"
            DataType="http://www.w3.org/2001/XMLSchema#string"
            MustBePresent="true"/>
        </Match>
      </AllOf>
    </AnyOf>
  </Target>

  <Rule Effect="Permit" RuleId="Rule_for_publicUser">
    <Target>
      <AnyOf><AllOf>
        <Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
          <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">publicUser</AttributeValue>
          <AttributeDesignator AttributeId="http://wso2.org/identity/user/username"
            Category="http://wso2.org/identity/user"
            DataType="http://www.w3.org/2001/XMLSchema#string" MustBePresent="true"/>
        </Match>
      </AllOf></AnyOf>
    </Target>
    <Condition>
      <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-at-least-one-member-of">
        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-bag">
          <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">view-welcome</AttributeValue>
          <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">view-summary</AttributeValue>
        </Apply>
        <AttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id"
          Category="urn:oasis:names:tc:xacml:3.0:attribute-category:action"
          DataType="http://www.w3.org/2001/XMLSchema#string" MustBePresent="true"/>
      </Apply>
    </Condition>
  </Rule>

  <Rule Effect="Permit" RuleId="Rule_for_internalUser">
    <Target>
      <AnyOf><AllOf>
        <Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
          <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">internalUser</AttributeValue>
          <AttributeDesignator AttributeId="http://wso2.org/identity/user/username"
            Category="http://wso2.org/identity/user"
            DataType="http://www.w3.org/2001/XMLSchema#string" MustBePresent="true"/>
        </Match>
      </AllOf></AnyOf>
    </Target>
    <Condition>
      <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-at-least-one-member-of">
        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-bag">
          <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">view-status</AttributeValue>
        </Apply>
        <AttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id"
          Category="urn:oasis:names:tc:xacml:3.0:attribute-category:action"
          DataType="http://www.w3.org/2001/XMLSchema#string" MustBePresent="true"/>
      </Apply>
    </Condition>
  </Rule>

  <Rule Effect="Permit" RuleId="Rule_for_adminUser">
    <Target>
      <AnyOf><AllOf>
        <Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
          <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">adminUser</AttributeValue>
          <AttributeDesignator AttributeId="http://wso2.org/identity/user/username"
            Category="http://wso2.org/identity/user"
            DataType="http://www.w3.org/2001/XMLSchema#string" MustBePresent="true"/>
        </Match>
      </AllOf></AnyOf>
    </Target>
    <Condition>
      <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-at-least-one-member-of">
        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-bag">
          <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">modify-welcome</AttributeValue>
          <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">modify-summary</AttributeValue>
        </Apply>
        <AttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id"
          Category="urn:oasis:names:tc:xacml:3.0:attribute-category:action"
          DataType="http://www.w3.org/2001/XMLSchema#string" MustBePresent="true"/>
      </Apply>
    </Condition>
  </Rule>

  <Rule Effect="Deny" RuleId="Rule_deny_all"/>

  <ObligationExpressions>
    <ObligationExpression FulfillOn="Deny" ObligationId="fail_to_permit">
      <AttributeAssignmentExpression AttributeId="obligation-id">
        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
          You cannot access the resource index.jsp
        </AttributeValue>
      </AttributeAssignmentExpression>
    </ObligationExpression>
  </ObligationExpressions>
</Policy>
```

3. Locate the policy in the list and click **Activate** to enable it for runtime evaluation.

---

## Step 2: Send a JSON authorization request

### Request format

A XACML 3.0 JSON request maps XACML categories to JSON objects. The standard categories are:

| XACML category URI | JSON shorthand |
|---|---|
| `urn:oasis:names:tc:xacml:3.0:attribute-category:resource` | `Resource` |
| `urn:oasis:names:tc:xacml:3.0:attribute-category:action` | `Action` |
| `urn:oasis:names:tc:xacml:1.0:subject-category:access-subject` | `AccessSubject` |
| Custom categories (e.g., `http://wso2.org/identity/user`) | Use the full URI as the key |

### Example request: Denied (adminUser doing view-welcome)

```bash
curl -X POST https://localhost:9443/api/identity/entitlement/decision/pdp \
  -H 'Authorization: Basic YWRtaW46YWRtaW4=' \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -d '{
    "Request": {
      "http://wso2.org/identity/user": {
        "Attribute": [{
          "AttributeId": "http://wso2.org/identity/user/username",
          "Value": "adminUser",
          "DataType": "string",
          "IncludeInResult": true
        }]
      },
      "Resource": {
        "Attribute": [{
          "AttributeId": "resource-id",
          "Value": "index.jsp",
          "DataType": "string",
          "IncludeInResult": true
        }]
      },
      "Action": {
        "Attribute": [{
          "AttributeId": "action-id",
          "Value": "view-welcome",
          "DataType": "string",
          "IncludeInResult": true
        }]
      }
    }
  }'
```

**Response**: `Deny` with obligation (attributes included because `IncludeInResult: true`):

```json
{
  "Response": [{
    "Decision": "Deny",
    "Status": {
      "StatusCode": { "Value": "urn:oasis:names:tc:xacml:1.0:status:ok" }
    },
    "Obligations": [{
      "Id": "fail_to_permit",
      "AttributeAssignments": [{
        "AttributeId": "obligation-id",
        "Value": "You cannot access the resource index.jsp",
        "DataType": "http://www.w3.org/2001/XMLSchema#string"
      }]
    }],
    "Action": {
      "Attribute": [{ "AttributeId": "urn:oasis:names:tc:xacml:1.0:action:action-id", "Value": "view-welcome", "IncludeInResult": "true", "DataType": "http://www.w3.org/2001/XMLSchema#string" }]
    },
    "http://wso2.org/identity/user": {
      "Attribute": [{ "AttributeId": "http://wso2.org/identity/user/username", "Value": "adminUser", "IncludeInResult": "true", "DataType": "http://www.w3.org/2001/XMLSchema#string" }]
    },
    "Resource": {
      "Attribute": [{ "AttributeId": "urn:oasis:names:tc:xacml:1.0:resource:resource-id", "Value": "index.jsp", "IncludeInResult": "true", "DataType": "http://www.w3.org/2001/XMLSchema#string" }]
    }
  }]
}
```

### Example request: Permitted (adminUser doing modify-welcome)

```bash
curl -X POST https://localhost:9443/api/identity/entitlement/decision/pdp \
  -H 'Authorization: Basic YWRtaW46YWRtaW4=' \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -d '{
    "Request": {
      "http://wso2.org/identity/user": {
        "Attribute": [{ "AttributeId": "http://wso2.org/identity/user/username", "Value": "adminUser", "DataType": "string", "IncludeInResult": false }]
      },
      "Resource": {
        "Attribute": [{ "AttributeId": "resource-id", "Value": "index.jsp", "DataType": "string", "IncludeInResult": false }]
      },
      "Action": {
        "Attribute": [{ "AttributeId": "action-id", "Value": "modify-welcome", "DataType": "string", "IncludeInResult": false }]
      }
    }
  }'
```

**Response**: `Permit` (compact, attributes not included because `IncludeInResult: false`):

```json
{
  "Response": [{
    "Decision": "Permit",
    "Status": {
      "StatusCode": { "Value": "urn:oasis:names:tc:xacml:1.0:status:ok" }
    }
  }]
}
```

---

## JSON shorthand form

XACML 3.0 JSON profile supports a shorthand form where attribute values are written inline instead of as objects:

```json
{
  "Request": {
    "Action": {
      "action-id": "read"
    },
    "Resource": {
      "resource-id": "http://example.org/resource"
    }
  }
}
```

To enable shorthand form, set `JSON_shorten_form_enabled = true` in your `deployment.toml`:

```toml
[identity.entitlement]
JSON_shorten_form_enabled = true
```

---

## Decision values

| Decision | Meaning |
|---|---|
| `Permit` | Access granted |
| `Deny` | Access denied |
| `NotApplicable` | No policy matched the request |
| `Indeterminate` | Policy evaluation error or missing required attributes |

---

## Batching multiple questions in one request

If you need decisions for several combinations of attributes (multiple actions on the same resource, multiple users on the same action, all children under a parent resource, etc.), send a single **Multiple Decision Profile (MDP)** request instead of one call per question. See [Multiple Decision Profile (MDP)](multiple-decision-profile.md).
