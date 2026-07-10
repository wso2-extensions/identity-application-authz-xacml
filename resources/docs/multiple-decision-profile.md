# Working with XACML Multiple Decision Profile (MDP) Requests

The **Multiple Decision Profile (MDP)** is a XACML 3.0 feature that lets a Policy Enforcement Point (PEP) ask several access-control questions in a single XACML request. The Policy Decision Point (PDP) evaluates each question independently and returns one `<Result>` per question in the same response.

MDP is useful when the PEP would otherwise have to send many almost-identical requests. For example, checking every action a user might perform on a resource, or authorizing every child under a parent resource. Reusing the shared attribute values across all questions minimizes network traffic and simplifies the PEP logic.

> **Prerequisite**: The XACML connector must be installed. See the [setup guide](../../README.md).

---

## When to use MDP

WSO2 Identity Server supports MDP requests in two forms:

| Form | When to use |
|---|---|
| **Repeating attribute categories** | The PEP needs decisions for several values of the same category (e.g., "can this user perform action A, action B, action C on resource X?"). Every category can be repeated. |
| **Hierarchical resources** | The PEP knows only the root of a resource tree (e.g., a folder) and wants decisions for every child or descendant based on a `scope` attribute. |

Both forms can also be sent in JSON. See [JSON MDP requests](#json-mdp-requests).

---

## Enabling MDP

MDP is enabled by default. The relevant `deployment.toml` flag is:

```toml
[identity.entitlement.policy_point.pdp]
multiple_decision_profile_enabled = true
```

Set to `false` only if you want the PDP to reject MDP requests.

---

## MDP by repeating attribute categories

### Scenario

A web application needs to know which of four actions user `Sam` is allowed to perform on `index.jsp`:

- `view-welcome`
- `view-status`
- `view-summary`
- `modify-welcome`

Instead of issuing four separate requests, the PEP sends one request that repeats the **action** category four times.

### Sample request

```xml
<Request xmlns="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17"
    CombinedDecision="false" ReturnPolicyIdList="false">

  <Attributes Category="urn:oasis:names:tc:xacml:3.0:attribute-category:resource">
    <Attribute AttributeId="urn:oasis:names:tc:xacml:1.0:resource:resource-id" IncludeInResult="false">
      <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">index.jsp</AttributeValue>
    </Attribute>
  </Attributes>

  <Attributes Category="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject">
    <Attribute AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" IncludeInResult="false">
      <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">Sam</AttributeValue>
    </Attribute>
  </Attributes>

  <Attributes Category="urn:oasis:names:tc:xacml:3.0:attribute-category:action">
    <Attribute AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" IncludeInResult="true">
      <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">view-welcome</AttributeValue>
    </Attribute>
  </Attributes>
  <Attributes Category="urn:oasis:names:tc:xacml:3.0:attribute-category:action">
    <Attribute AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" IncludeInResult="true">
      <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">view-summary</AttributeValue>
    </Attribute>
  </Attributes>
  <Attributes Category="urn:oasis:names:tc:xacml:3.0:attribute-category:action">
    <Attribute AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" IncludeInResult="true">
      <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">view-status</AttributeValue>
    </Attribute>
  </Attributes>
  <Attributes Category="urn:oasis:names:tc:xacml:3.0:attribute-category:action">
    <Attribute AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" IncludeInResult="true">
      <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">modify-welcome</AttributeValue>
    </Attribute>
  </Attributes>
</Request>
```

Two things to note:

- **`IncludeInResult="true"`** on the repeated attribute. This is what lets you distinguish which decision belongs to which action in the response.
- **Shared attributes** (subject, resource) are declared once and reused across all decisions.

### Sample response

The PDP returns one `<Result>` per repeated action:

```xml
<Response>
  <Result>
    <Decision>Permit</Decision>
    <Status><StatusCode Value="urn:oasis:names:tc:xacml:1.0:status:ok"/></Status>
    <Attributes Category="urn:oasis:names:tc:xacml:3.0:attribute-category:action">
      <Attribute AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" IncludeInResult="true">
        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">view-welcome</AttributeValue>
      </Attribute>
    </Attributes>
  </Result>
  <Result>
    <Decision>Permit</Decision>
    <Status><StatusCode Value="urn:oasis:names:tc:xacml:1.0:status:ok"/></Status>
    <Attributes Category="urn:oasis:names:tc:xacml:3.0:attribute-category:action">
      <Attribute AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" IncludeInResult="true">
        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">view-summary</AttributeValue>
      </Attribute>
    </Attributes>
  </Result>
  <Result>
    <Decision>Permit</Decision>
    <Status><StatusCode Value="urn:oasis:names:tc:xacml:1.0:status:ok"/></Status>
    <Attributes Category="urn:oasis:names:tc:xacml:3.0:attribute-category:action">
      <Attribute AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" IncludeInResult="true">
        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">view-status</AttributeValue>
      </Attribute>
    </Attributes>
  </Result>
  <Result>
    <Decision>Deny</Decision>
    <Status><StatusCode Value="urn:oasis:names:tc:xacml:1.0:status:ok"/></Status>
    <Attributes Category="urn:oasis:names:tc:xacml:3.0:attribute-category:action">
      <Attribute AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" IncludeInResult="true">
        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">modify-welcome</AttributeValue>
      </Attribute>
    </Attributes>
  </Result>
</Response>
```

---

## MDP for hierarchical resources

XACML 3.0 also supports a hierarchical resource profile. The PEP can send a single request that names a **root** resource plus a `scope` attribute, and the PDP produces decisions for the children (or all descendants) of that root.

This depends on the deployed `resource_finder` in `deployment.toml`:

```toml
[identity.entitlement.policy_point.pip]
resource_finders = ["org.wso2.carbon.identity.entitlement.pip.DefaultResourceFinder"]
```

### Scope values

The `scope` attribute on the resource category controls how the PDP walks the tree:

| Scope | Meaning |
|---|---|
| `Children` | Only level-1 children of the root |
| `Descendants` | The root and every descendant |

The actual set of children/descendants is looked up by the configured resource finder.

### Sample request

```xml
<Request xmlns="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17"
    CombinedDecision="false" ReturnPolicyIdList="false">

  <Attributes Category="urn:oasis:names:tc:xacml:3.0:attribute-category:action">
    <Attribute AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" IncludeInResult="false">
      <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">read</AttributeValue>
    </Attribute>
  </Attributes>

  <Attributes Category="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject">
    <Attribute AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" IncludeInResult="false">
      <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">asela</AttributeValue>
    </Attribute>
  </Attributes>

  <Attributes Category="urn:oasis:names:tc:xacml:3.0:attribute-category:resource">
    <Attribute AttributeId="urn:oasis:names:tc:xacml:2.0:resource:scope" IncludeInResult="false">
      <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">Children</AttributeValue>
    </Attribute>
    <Attribute AttributeId="urn:oasis:names:tc:xacml:1.0:resource:resource-id" IncludeInResult="true">
      <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">index.jsp</AttributeValue>
    </Attribute>
    <Attribute AttributeId="urn:oasis:names:tc:xacml:1.0:resource:root-resource-id" IncludeInResult="false">
      <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">index.jsp</AttributeValue>
    </Attribute>
  </Attributes>
</Request>
```

Key points:

- The **root** is `resource-id = index.jsp`.
- The **scope** attribute uses AttributeId `urn:oasis:names:tc:xacml:2.0:resource:scope` on the resource category.
- The additional `root-resource-id` is a convenience attribute that policies can reference. Its actual AttributeId is up to how your policies are written.
- `IncludeInResult="true"` on the resource-id ensures the response echoes the resolved child in every `<Result>`, so the PEP can tell which decision belongs to which child.

### Sample response

The PDP returns one `<Result>` per resolved child. Deny/Permit is derived from the applicable rules for that specific child.

---

## JSON MDP requests

XACML 3.0 JSON profile supports MDP by using JSON arrays for the repeated categories. For general JSON request format, see [Fine-grained Authorization using JSON Format](fine-grained-auth-json.md).

### Sample request

Two users (`adminUser` and `publicUser`) checking four actions on `index.jsp`. The PDP produces eight decisions (2 users × 4 actions):

```bash
curl -X POST https://localhost:9443/api/identity/entitlement/decision/pdp \
  -H 'Authorization: Basic YWRtaW46YWRtaW4=' \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -d '{
    "Request": {
      "http://wso2.org/identity/user": [
        {
          "Attribute": [{
            "AttributeId": "http://wso2.org/identity/user/username",
            "Value": "adminUser",
            "IncludeInResult": true,
            "DataType": "string"
          }]
        },
        {
          "Attribute": [{
            "AttributeId": "http://wso2.org/identity/user/username",
            "Value": "publicUser",
            "IncludeInResult": true,
            "DataType": "string"
          }]
        }
      ],
      "Resource": {
        "Attribute": [{
          "AttributeId": "urn:oasis:names:tc:xacml:1.0:resource:resource-id",
          "Value": "index.jsp",
          "IncludeInResult": true,
          "DataType": "http://www.w3.org/2001/XMLSchema#string"
        }]
      },
      "Action": [
        { "Attribute": [{ "AttributeId": "urn:oasis:names:tc:xacml:1.0:action:action-id", "Value": "view-welcome",   "IncludeInResult": true, "DataType": "http://www.w3.org/2001/XMLSchema#string" }] },
        { "Attribute": [{ "AttributeId": "urn:oasis:names:tc:xacml:1.0:action:action-id", "Value": "view-status",    "IncludeInResult": true, "DataType": "http://www.w3.org/2001/XMLSchema#string" }] },
        { "Attribute": [{ "AttributeId": "urn:oasis:names:tc:xacml:1.0:action:action-id", "Value": "view-summary",   "IncludeInResult": true, "DataType": "http://www.w3.org/2001/XMLSchema#string" }] },
        { "Attribute": [{ "AttributeId": "urn:oasis:names:tc:xacml:1.0:action:action-id", "Value": "modify-welcome", "IncludeInResult": true, "DataType": "http://www.w3.org/2001/XMLSchema#string" }] }
      ]
    }
  }'
```

To repeat a category in JSON, wrap the category value in an **array** (`[ ... ]`). A non-repeated category can be a single object.

### Sample response

The response contains an entry per (user × action) combination. Attributes with `IncludeInResult: true` are echoed back so the PEP can pair a decision to the specific question:

```json
{
  "Response": [
    {
      "Decision": "Permit",
      "Status": { "StatusCode": { "Value": "urn:oasis:names:tc:xacml:1.0:status:ok" } },
      "http://wso2.org/identity/user": { "Attribute": [{ "AttributeId": "http://wso2.org/identity/user/username", "Value": "adminUser",  "IncludeInResult": "true", "DataType": "http://www.w3.org/2001/XMLSchema#string" }] },
      "Action":   { "Attribute": [{ "AttributeId": "urn:oasis:names:tc:xacml:1.0:action:action-id",             "Value": "modify-welcome", "IncludeInResult": "true", "DataType": "http://www.w3.org/2001/XMLSchema#string" }] },
      "Resource": { "Attribute": [{ "AttributeId": "urn:oasis:names:tc:xacml:1.0:resource:resource-id",         "Value": "index.jsp",       "IncludeInResult": "true", "DataType": "http://www.w3.org/2001/XMLSchema#string" }] }
    }
    /* ... one entry per user × action combination ... */
  ]
}
```

---

## Combined decision

The `CombinedDecision` flag on the `<Request>` element controls whether the PDP returns one merged decision or many:

| `CombinedDecision` | Response |
|---|---|
| `false` (default) | One `<Result>` per question, so the caller can inspect each independently. |
| `true` | The PDP combines all the individual decisions into a single `<Result>` using the global policy combining algorithm. |

Use `CombinedDecision="true"` when the PEP only needs a single yes/no answer (e.g., "can the user do *any* of these things?"). Use the default when you need per-question decisions.

---

## See also

- [Fine-grained Authorization using JSON Format](fine-grained-auth-json.md): Base JSON request format
- [Entitlement Management REST API](entitlement-rest-api.md): Decision endpoint and REST usage
- [XACML Policy Reference](xacml-policy-reference.md): Policy structure, combining algorithms
