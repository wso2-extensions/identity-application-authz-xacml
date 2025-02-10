# Configure rule-based provisioning

This page guides you through provisioning users based on defined XACML rules.

To get a better understanding of rule-based provisioning, consider a scenario where you need to provision users assigned to the finance role from WSO2 Identity Server to the GoogleIDP. To implement this scenario, you can define a XACML policy that permits the provisioning operation if the provisioning user is assigned the finance role.

Follow the steps given below to configure rule-based provisioning in WSO2 Identity Server.

## Sample scenario

Let's assume we need to provision users based on their email domain and restrict the provisioning of users who do not have the email attribute with the permitted domain (`@abc.com`). Let's create a XACML policy to implement this scenario.

## Setting up the WSO2 Identity Server

### Prerequisites
Setup outbound provisioning using the desired outbound provisioning connector ([Google](https://is.docs.wso2.com/en/latest/guides/users/outbound-provisioning/outbound-connectors/google/), [Salesforce](https://is.docs.wso2.com/en/latest/guides/users/outbound-provisioning/outbound-connectors/salesforce/) or [SCIM2](https://is.docs.wso2.com/en/latest/guides/users/outbound-provisioning/outbound-connectors/scim2/)).

### Configure the resident service provider

You need to enable rules in the outbound provisioning connector you have added to the resident service provider. This can be done using the following API:
```bash
curl --location --request PUT 'https://localhost:9443/api/server/v1/applications/resident' \
--header 'accept: application/json' \
--header 'Authorization: Basic YWRtaW46YWRtaW4=' \
--header 'Content-Type: application/json' \
--data '{
    "outboundProvisioningIdps": [
        {
            "idp": <IDP_NAME>,
            "connector": "SCIM2",
            "blocking": false,
            "rules": true,
            "jit": false
        }
    ]
}'
```

Replace the `<IDP_NAME>` with the name of the outbound provisioning connector you have registered in the earlier step.

### Configure the service provider

If you have configured outbound provisioning in your application, you should **Enable rules** in the outbound provisioning connector. This can be done using the following API:

```bash
curl --location --request PATCH 'https://localhost:9443/api/server/v1/applications/<APPLICATION-ID>' \
--header 'accept: application/json' \
--header 'Authorization: Basic YWRtaW46YWRtaW4=' \
--header 'Content-Type: application/json' \
--data '{
    "provisioningConfigurations": {
        "outboundProvisioningIdps": [
            {
                "idp": <IDP_NAME>,
                "connector": "SCIM2",
                "blocking": false,
                "rules": true,
                "jit": false
            }
        ]
    }
}'
```

Replace the `<IDP_NAME>` with the name of the outbound provisioning connector you have registered in the earlier step.

Use the [application API](https://is.docs.wso2.com/en/latest/apis/application-rest-api/#tag/Applications/operation/getAllApplications) to find the application ID of your application and replace the `<APPLICATION-ID>` with it.


## Set up XACML rules

1. Log in to the Console via <https://localhost:9443/console>.
2. Navigate to **Policy Administration**.
3. Click on "New Policy" and retrieve the `provisioning_user_claim_based_policy_template` from this [link](https://github.com/wso2-extensions/identity-application-authz-xacml/blob/master/resources/policies/scope_based_token_issuance_policy_template.xml). 
   Copy the policy content and paste it into the policy editor. Note that this example scenario is based on using the email address as the user claim.
4. Edit the placeholders accordingly with the relevant values.
    1. Change the `PolicyId` as follows:
         ```bash
         PolicyId="provisioning_user_claim_based_policy"
         ```
    2. Edit the `<Description>` tag and enter a description relevant to your custom policy.
    3. Locate the `IDP_NAME` placeholder and replace it with the identity provider name you have created earlier.
    4. Since we need to do a regex match on the email claim, change the condition of the rules as follows:
   ```xml
      <Condition>
         <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-regexp-match">
               <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">.*@abc\.com$</AttributeValue>
               <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                  <AttributeDesignator AttributeId="http://wso2.org/claims/emailaddress" Category="http://wso2.org/identity/user" DataType="http://www.w3.org/2001/XMLSchema#string" MustBePresent="true"></AttributeDesignator>
                </Apply>
            </Apply>
         </Apply>
      </Condition>
   ```
    5. Since we are using only one user claim for this policy, remove the other claim by removing that entire section from the start of the `<Apply>` tag to the ending `</Apply>` tag.  This should be edited in both POST and PUT sections as the provisioning is initiated when creating the user and when updating the user as well.
    6. Also for this example, we do not need a service provider. Therefore, we need to remove the service provider `SP_NAME` match block as well.
5. Once the changes have been made, the policy should be similar to the following.
```xml
                                <Policy xmlns="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17"  PolicyId="provisioning_user_claim_based_policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:first-applicable" Version="1.0">
<Description>This template policy provides ability to authorize provisioning requests initiated from a given service provider(defined by SP_NAME) to a given identity provider(defined by IDP_NAME) in the outbound provisioning flow based on the claim values of the user (CLAIM_URI_1=CLAIM_VALUE_1 and CLAIM_URI_2=CLAIM_VALUE_2). Users with the given claim values will be allowed and any other users will be denied.</Description>
<Target>
<AnyOf>
<AllOf>
<Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">WSO2IDP</AttributeValue>
<AttributeDesignator AttributeId="http://wso2.org/identity/idp/idp-name" Category="http://wso2.org/identity/idp" DataType="http://www.w3.org/2001/XMLSchema#string" MustBePresent="false">
</AttributeDesignator>
</Match>
<Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">provisioning</AttributeValue>
<AttributeDesignator AttributeId="http://wso2.org/identity/identity-action/action-name" Category="http://wso2.org/identity/identity-action" DataType="http://www.w3.org/2001/XMLSchema#string" MustBePresent="false">
</AttributeDesignator>
</Match>
</AllOf>
</AnyOf>
</Target>
<Rule Effect="Permit" RuleId="permit_by_claims_when_create">
<Target>
<AnyOf>
<AllOf>
<Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">POST</AttributeValue>
<AttributeDesignator AttributeId="http://wso2.org/identity/provisioning/provision-operation" Category="http://wso2.org/identity/provisioning" DataType="http://www.w3.org/2001/XMLSchema#string" MustBePresent="true">
</AttributeDesignator>
</Match>
</AllOf>
</AnyOf>
</Target>
<Condition>
<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-regexp-match">
<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">.*@wso2\.com$</AttributeValue>
<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
<AttributeDesignator AttributeId="http://wso2.org/claims/emailaddress" Category="http://wso2.org/identity/user" DataType="http://www.w3.org/2001/XMLSchema#string" MustBePresent="true">
</AttributeDesignator>
</Apply>
</Apply>
</Apply>
</Condition>
</Rule>
<Rule Effect="Permit" RuleId="permit_by_claims_when_update">
<Target>
<AnyOf>
<AllOf>
<Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">PUT</AttributeValue>
<AttributeDesignator AttributeId="http://wso2.org/identity/provisioning/provision-operation" Category="http://wso2.org/identity/provisioning" DataType="http://www.w3.org/2001/XMLSchema#string" MustBePresent="true">
</AttributeDesignator>
</Match>
</AllOf>
</AnyOf>
</Target>
<Condition>
<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
<AttributeDesignator AttributeId="http://wso2.org/claims/emailaddress" Category="http://wso2.org/identity/user" DataType="http://www.w3.org/2001/XMLSchema#string" MustBePresent="true">
</AttributeDesignator>
</Apply>
<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">test@abc.com</AttributeValue>
</Apply>
</Apply>
</Condition>
</Rule>
<Rule Effect="Deny" RuleId="deny_others">
</Rule>
</Policy>
```

6. Click **Create** to save the changes.
7. The policy is initially created in an **Inactive** state. To activate it, click the **Activate** button.
8. To test out whether the policy works, follow the **Try it** out section.


## Try it out

Once the policies are published to PDP, they are ready to execute during outbound provisioning. You can test rule-based provisioning by creating a user in WSO2 Identity Server that matches the rules you enforced.
For example, when you create a user with the email `"jane@abc.com"` in WSO2 Identity Server, it should be provisioned to the external IDP you have configured for outbound provisioning as well. A user who does not have an email with the domain `@abc.com` should not be provisioned.