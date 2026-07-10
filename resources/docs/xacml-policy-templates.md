# XACML Policy Templates

The XACML connector ships a set of pre-built policy templates in the [`resources/policies/`](../policies/) directory. These templates cover the most common authorization patterns and can be used as starting points — copy the template, fill in the placeholders, and publish.

> **Prerequisite**: The XACML connector must be installed. See the [setup guide](../../README.md).

---

## Template groups

Templates are divided into four groups based on the flow they target:

| Group | File prefix | Count | Used in |
|---|---|---|---|
| [Authentication](#authentication-templates) | `authn_` | 10 | Controls application login (post-authentication) |
| [Provisioning](#provisioning-templates) | `provisioning_` | 5 | Controls outbound user provisioning |
| [Scope validation](#scope-validation-templates) | `scope_` | 2 | Controls OAuth token issuance / validation |
| [Miscellaneous](#miscellaneous) | `eval_` | 1 | Permission tree evaluation |

---

## Authentication templates

These templates are evaluated by the XACML Authorization Handler when a user authenticates to an application that has **Enable authorization** turned on. See [Fine-grained Authorization for Applications](fine-grained-authorization.md).

### `authn_role_based_policy_template`

Permits users who have at least one of the specified roles; denies all others.

**Placeholders**:

| Placeholder | Description | Example |
|---|---|---|
| `SP_NAME` | Service provider (application) name | `MyApp` |
| `ROLE_1` | First permitted role | `admin` |
| `ROLE_2` | Second permitted role (remove if not needed) | `manager` |

---

### `authn_scope_based_policy_template`

Permits users who authenticated with specified OAuth scopes; denies all others.

**Placeholders**:

| Placeholder | Description | Example |
|---|---|---|
| `SP_NAME` | Service provider name | `MyApp` |
| `SCOPE1` | First required OAuth scope | `read` |
| `SCOPE2` | Second required scope (remove if not needed) | `write` |

---

### `authn_time_based_policy_template`

Permits authentication only within a specified time window; denies outside it.

**Placeholders**:

| Placeholder | Description | Example |
|---|---|---|
| `SP_NAME` | Service provider name | `MyApp` |
| `09:00:00` | Start time — replace with desired time. Timezone offset supported (e.g., `09:00:00+05:30`) | `08:00:00` |
| `17:00:00` | End time — replace with desired time. | `18:00:00` |

---

### `authn_time_and_role_based_policy_template`

Permits users with specified roles who authenticate within a time window; denies all others.

**Placeholders**: `SP_NAME`, `ROLE_1`, `ROLE_2`, `09:00:00` (start time), `17:00:00` (end time).

---

### `authn_time_and_scope_based_policy_template`

Permits users who authenticate with specified OAuth scopes within a time window.

**Placeholders**: `SP_NAME`, `SCOPE1`, `SCOPE2`, `09:00:00` (start time), `17:00:00` (end time).

---

### `authn_time_and_user_claim_based_policy_template`

Permits users whose claim values match and who authenticate within a time window.

**Placeholders**:

| Placeholder | Description | Example |
|---|---|---|
| `SP_NAME` | Service provider name | `MyApp` |
| `CLAIM_URI_1` | Claim URI | `http://wso2.org/claims/department` |
| `CLAIM_VALUE_1` | Expected claim value | `Engineering` |
| `CLAIM_URI_2` | Second claim URI (remove if not needed) | `http://wso2.org/claims/country` |
| `CLAIM_VALUE_2` | Second expected claim value | `LK` |
| `09:00:00` / `17:00:00` | Time window start/end — replace with desired values | `08:00:00` / `18:00:00` |

---

### `authn_time_and_user_store_based_policy_template`

Permits users from a specific user store who authenticate within a time window.

**Placeholders**: `SP_NAME`, `USERSTORE_1`, `USERSTORE_2`, `09:00:00` (start time), `17:00:00` (end time).

---

### `authn_user_claim_based_policy_template`

Permits users whose claim values match; denies all others.

**Placeholders**: `SP_NAME`, `CLAIM_URI_1`, `CLAIM_VALUE_1`, `CLAIM_URI_2`, `CLAIM_VALUE_2`.

---

### `authn_user_store_based_policy_template`

Permits users from a specific user store; denies all others.

**Placeholders**: `SP_NAME`, `USERSTORE_1`, `USERSTORE_2`.

---

### `authn_group_based_policy_template`

Permits users who belong to at least one of the specified groups; denies all others.

**Placeholders**: `SP_NAME`, `GROUP_1`, `GROUP_2`.

---

## Provisioning templates

These templates are evaluated during outbound provisioning when **Enable rules** is turned on for an outbound connector. See [Rule-based Provisioning](rule-based-provisioning.md).

### `provisioning_role_based_policy_template`

Permits provisioning of users with specified roles; denies all others.

**Placeholders**:

| Placeholder | Description | Example |
|---|---|---|
| `SP_NAME` | Service provider name | `MyApp` |
| `IDP_NAME` | Outbound identity provider name | `GoogleIDP` |
| `ROLE_1` | First permitted role | `finance` |
| `ROLE_2` | Second permitted role (remove if not needed) | `hr` |

---

### `provisioning_user_claim_based_policy_template`

Permits provisioning of users whose claim values match; denies all others.

**Placeholders**: `SP_NAME`, `IDP_NAME`, `CLAIM_URI_1`, `CLAIM_VALUE_1`, `CLAIM_URI_2`, `CLAIM_VALUE_2`.

---

### `provisioning_time_based_policy_template`

Permits provisioning only within a time window; denies outside it.

**Placeholders**: `SP_NAME`, `IDP_NAME`, `09:00:00` (start time), `17:00:00` (end time).

---

### `provisioning_time_and_role_based_policy_template`

Permits provisioning of users with specified roles within a time window.

**Placeholders**: `SP_NAME`, `IDP_NAME`, `ROLE_1`, `ROLE_2`, `09:00:00` (start time), `17:00:00` (end time).

---

### `provisioning_time_and_user_claim_based_policy_template`

Permits provisioning of users whose claim values match within a time window.

**Placeholders**: `SP_NAME`, `IDP_NAME`, `CLAIM_URI_1`, `CLAIM_VALUE_1`, `CLAIM_URI_2`, `CLAIM_VALUE_2`, `09:00:00` (start time), `17:00:00` (end time).

---

## Scope validation templates

These templates are evaluated by the XACML Scope Validator during OAuth token issuance/validation. See [Validate OAuth Scope with XACML](attribute-based-access-control.md).

### `scope_based_token_issuance_policy_template`

Permits token issuance when the requested scopes are valid for the authenticated user; denies otherwise.

**Placeholders**:

| Placeholder | Description | Example |
|---|---|---|
| `SP_NAME` | Service provider name | `MyApp` |
| `SCOPE_1` | First permitted OAuth scope | `read` |
| `SCOPE_2` | Second permitted scope (remove if not needed) | `write` |

---

### `scope_based_token_validation_policy_template`

Permits token validation when the token's scope is valid for the requested resource; denies otherwise.

**Placeholders**: `SP_NAME`, `SCOPE_1`.

---

## Miscellaneous

### `eval_permission_tree_policy`

Evaluates access based on the WSO2 permission tree — the hierarchical permission structure defined in the IS Management Console under **User Management > Permissions**.

**How it works**: The policy uses a custom XACML function `urn:oasis:names:tc:xacml:1.0:function:eval-permission-tree` that checks whether the subject (user) has the required permission node in the permission tree for the requested resource. The action must be `ui.execute`.

**When to use**: When you want to enforce UI-level permissions (e.g., restricting access to management console sections) through the XACML engine rather than the built-in permission check.

**Placeholders**: None — this template works as-is. The `resource-id` in the XACML request should match the permission node path (e.g., `/permission/admin/manage/identity`).

**Example request**:
```xml
<Request xmlns="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17" CombinedDecision="false" ReturnPolicyIdList="false">
  <Attributes Category="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject">
    <Attribute AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" IncludeInResult="false">
      <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">john</AttributeValue>
    </Attribute>
  </Attributes>
  <Attributes Category="urn:oasis:names:tc:xacml:3.0:attribute-category:resource">
    <Attribute AttributeId="urn:oasis:names:tc:xacml:1.0:resource:resource-id" IncludeInResult="false">
      <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">/permission/admin/manage/identity</AttributeValue>
    </Attribute>
  </Attributes>
  <Attributes Category="urn:oasis:names:tc:xacml:3.0:attribute-category:action">
    <Attribute AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" IncludeInResult="false">
      <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">ui.execute</AttributeValue>
    </Attribute>
  </Attributes>
</Request>
```

---

## How to use a template

1. Open the template XML file from [`resources/policies/`](../policies/).
2. In the IS Console, navigate to **Policy Administration**.
3. Click **New Policy > Write Policy in XML**.
4. Paste the template content into the editor.
5. Replace all placeholders (e.g., `SP_NAME`, `ROLE_1`) with actual values.
6. Update the `PolicyId` attribute to a unique, descriptive name.
7. Click **Create**. The policy is created in **Inactive** state.
8. Click **Activate** to enable it for runtime evaluation.

> The original template file remains unchanged — your customised version is a separate policy.
