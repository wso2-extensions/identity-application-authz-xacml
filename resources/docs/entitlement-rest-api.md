# Entitlement Management REST API

The XACML connector ships a REST API for managing entitlement policies, subscribers, and the global policy combining algorithm. It also exposes a XACML decision endpoint for evaluating policies programmatically.

> **Prerequisite**: The XACML connector must be installed. See the [setup guide](../../README.md).

---

## Base URLs

| API | Base URL |
|---|---|
| Entitlement management (v1) | `https://localhost:9443/api/identity/entitlement/v1/` |
| XACML decision | `https://localhost:9443/api/identity/entitlement/decision/` |
| Tenant-scoped (v1) | `https://localhost:9443/t/{tenant_domain}/api/identity/entitlement/v1/` |

Authentication: **Basic Auth** — Base64-encode `username:password` and pass as `Authorization: Basic <token>`.

---

## Policy management

### List policies

```bash
curl -X GET 'https://localhost:9443/api/identity/entitlement/v1/entitlements/policies' \
  -H 'Authorization: Basic YWRtaW46YWRtaW4=' \
  -H 'Accept: application/json'
```

### Get a policy

```bash
curl -X GET 'https://localhost:9443/api/identity/entitlement/v1/entitlements/policies/{policyId}' \
  -H 'Authorization: Basic YWRtaW46YWRtaW4=' \
  -H 'Accept: application/json'
```

### Create a policy

```bash
curl -X POST 'https://localhost:9443/api/identity/entitlement/v1/entitlements/policies' \
  -H 'Authorization: Basic YWRtaW46YWRtaW4=' \
  -H 'Content-Type: application/json' \
  -d '{
    "policyId": "sample-policy",
    "policy": "<Policy xmlns=\"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\" PolicyId=\"sample-policy\" RuleCombiningAlgId=\"urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:deny-overrides\" Version=\"1.0\"><Target/><Rule Effect=\"Permit\" RuleId=\"permit-all\"/></Policy>",
    "active": false
  }'
```

### Update a policy

```bash
curl -X PATCH 'https://localhost:9443/api/identity/entitlement/v1/entitlements/policies/{policyId}' \
  -H 'Authorization: Basic YWRtaW46YWRtaW4=' \
  -H 'Content-Type: application/json' \
  -d '{
    "policy": "<Policy ...updated XML.../>",
    "active": true
  }'
```

### Delete a policy

```bash
curl -X DELETE 'https://localhost:9443/api/identity/entitlement/v1/entitlements/policies/{policyId}' \
  -H 'Authorization: Basic YWRtaW46YWRtaW4='
```

### Publish a policy

Publishing syncs the policy from PAP to PDP so it is used for runtime evaluation.

```bash
curl -X POST 'https://localhost:9443/api/identity/entitlement/v1/entitlements/publish' \
  -H 'Authorization: Basic YWRtaW46YWRtaW4=' \
  -H 'Content-Type: application/json' \
  -d '{
    "policyIds": ["sample-policy"],
    "action": "CREATE",
    "enabled": true,
    "order": 0
  }'
```

**Publish actions**:

| Action | Description |
|---|---|
| `CREATE` | Publish to PDP for the first time |
| `UPDATE` | Re-publish an already-published policy after editing |
| `DELETE` | Remove the policy from PDP |
| `ENABLE` | Enable a disabled policy in PDP |
| `DISABLE` | Disable a policy in PDP without deleting it |
| `ORDER` | Re-order existing published policies |

---

## Subscriber management

Subscribers are external systems (e.g., XACML PDPs in a federated setup) that receive published policies.

### List subscribers

```bash
curl -X GET 'https://localhost:9443/api/identity/entitlement/v1/entitlements/subscribers' \
  -H 'Authorization: Basic YWRtaW46YWRtaW4=' \
  -H 'Accept: application/json'
```

### Get a subscriber

```bash
curl -X GET 'https://localhost:9443/api/identity/entitlement/v1/entitlements/subscribers/{subscriberId}' \
  -H 'Authorization: Basic YWRtaW46YWRtaW4=' \
  -H 'Accept: application/json'
```

### Create a subscriber

```bash
curl -X POST 'https://localhost:9443/api/identity/entitlement/v1/entitlements/subscribers' \
  -H 'Authorization: Basic YWRtaW46YWRtaW4=' \
  -H 'Content-Type: application/json' \
  -d '{
    "subscriberId": "my-pdp-subscriber",
    "subscriberUrl": "https://external-pdp.example.com/pdp",
    "subscriberUsername": "pdpuser",
    "subscriberPassword": "pdppassword"
  }'
```

### Update a subscriber

```bash
curl -X PATCH 'https://localhost:9443/api/identity/entitlement/v1/entitlements/subscribers/{subscriberId}' \
  -H 'Authorization: Basic YWRtaW46YWRtaW4=' \
  -H 'Content-Type: application/json' \
  -d '{
    "subscriberUrl": "https://new-pdp.example.com/pdp"
  }'
```

### Delete a subscriber

```bash
curl -X DELETE 'https://localhost:9443/api/identity/entitlement/v1/entitlements/subscribers/{subscriberId}' \
  -H 'Authorization: Basic YWRtaW46YWRtaW4='
```

---

## Global policy combining algorithm

The global policy combining algorithm determines how multiple published policies are combined when evaluating a request.

### Get the current algorithm

```bash
curl -X GET 'https://localhost:9443/api/identity/entitlement/v1/entitlements/combining-algorithm' \
  -H 'Authorization: Basic YWRtaW46YWRtaW4=' \
  -H 'Accept: application/json'
```

### Set the algorithm

```bash
curl -X PATCH 'https://localhost:9443/api/identity/entitlement/v1/entitlements/combining-algorithm' \
  -H 'Authorization: Basic YWRtaW46YWRtaW4=' \
  -H 'Content-Type: application/json' \
  -d '{
    "policyAlgorithm": "urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:deny-overrides"
  }'
```

Common algorithm URNs:

| Algorithm | URN |
|---|---|
| Deny overrides | `urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:deny-overrides` |
| Permit overrides | `urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:permit-overrides` |
| First applicable | `urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:first-applicable` |
| Deny unless permit | `urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:deny-unless-permit` |
| Permit unless deny | `urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:permit-unless-deny` |

---

## XACML decision endpoint

The decision endpoint (`/api/identity/entitlement/decision/`) is used to evaluate XACML authorization requests and list available API resources. This is the endpoint your PEP calls at runtime to get an authorization decision.

### List available resources

```bash
curl -X GET 'https://localhost:9443/api/identity/entitlement/decision/home' \
  -H 'Authorization: Basic YWRtaW46YWRtaW4=' \
  -H 'Accept: application/json'
```

### Evaluate a XACML request (XML)

```bash
curl -X POST 'https://localhost:9443/api/identity/entitlement/decision/pdp' \
  -H 'Authorization: Basic YWRtaW46YWRtaW4=' \
  -H 'Content-Type: application/xml' \
  -H 'Accept: application/xml' \
  -d '<Request CombinedDecision="false" ReturnPolicyIdList="false"
        xmlns="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17">
    <Attributes Category="urn:oasis:names:tc:xacml:3.0:attribute-category:action">
      <Attribute AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" IncludeInResult="false">
        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">read</AttributeValue>
      </Attribute>
    </Attributes>
    <Attributes Category="urn:oasis:names:tc:xacml:3.0:attribute-category:resource">
      <Attribute AttributeId="urn:oasis:names:tc:xacml:1.0:resource:resource-id" IncludeInResult="false">
        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">http://example.org/resource</AttributeValue>
      </Attribute>
    </Attributes>
  </Request>'
```

### Evaluate a XACML request (JSON)

```bash
curl -X POST 'https://localhost:9443/api/identity/entitlement/decision/pdp' \
  -H 'Authorization: Basic YWRtaW46YWRtaW4=' \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -d '{
    "Request": {
      "Action": {
        "Attribute": [{ "AttributeId": "urn:oasis:names:tc:xacml:1.0:action:action-id", "Value": "read" }]
      },
      "Resource": {
        "Attribute": [{ "AttributeId": "urn:oasis:names:tc:xacml:1.0:resource:resource-id", "Value": "http://example.org/resource" }]
      }
    }
  }'
```

For a full guide on JSON request/response format, see [Fine-grained Authorization using JSON Format](fine-grained-auth-json.md). To ask several authorization questions in one call (e.g., every action a user might perform on a resource), see [Multiple Decision Profile (MDP)](multiple-decision-profile.md).

---

## Setting up a policy via REST — end-to-end example

1. **Deploy a sample policy**:

   ```bash
   # Upload policy XML file (import via console or POST)
   curl -X POST 'https://localhost:9443/api/identity/entitlement/v1/entitlements/policies' \
     -H 'Authorization: Basic YWRtaW46YWRtaW4=' \
     -H 'Content-Type: application/json' \
     -d '{"policyId":"sample-read-policy","policy":"<Policy.../>","active":false}'
   ```

2. **Publish to PDP**:

   ```bash
   curl -X POST 'https://localhost:9443/api/identity/entitlement/v1/entitlements/publish' \
     -H 'Authorization: Basic YWRtaW46YWRtaW4=' \
     -H 'Content-Type: application/json' \
     -d '{"policyIds":["sample-read-policy"],"action":"CREATE","enabled":true,"order":0}'
   ```

3. **Evaluate**:

   ```bash
   curl -X POST 'https://localhost:9443/api/identity/entitlement/decision/pdp' \
     -H 'Authorization: Basic YWRtaW46YWRtaW4=' \
     -H 'Content-Type: application/json' \
     -d '{"Request":{"Action":{"Attribute":[{"AttributeId":"urn:oasis:names:tc:xacml:1.0:action:action-id","Value":"read"}]}}}'
   ```

   A `Permit` or `Deny` response is returned based on the published policy.
