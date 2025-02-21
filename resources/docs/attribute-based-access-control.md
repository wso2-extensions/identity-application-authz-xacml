# Validating the Scope of OAuth Access Tokens using XACML Policies

WSO2 Identity Server (WSO2 IS) allows you to validate the scope of an
OAuth access token using XACML policies to provide fine-grained access
control to APIs.

If you want the XACML scope validator to execute after checking the
validity of the access token in an OAuth access token validation flow,
you can select the scope validator as XACML when you configure a service
provider. This provides fine-grained access control to APIs.

The following sections walk you through the basic steps
you need to follow to validate the scope of OAuth access tokens using
XACML policies.

### Register the app

Follow the steps given below to configure an application in WSO2 Identity
Server so that the authentication happens as expected.

1. On the Console, go to **Applications**.
2. Click **New Application** and select **Standard-Based Application**.
   <img src="../images/register-an-sba.png" alt="Register a standard-based application" style="width: 600px; display: block; margin: 0; border: 0.3px solid lightgrey;">
3. Provide an application name and select the other options based on your requirements.

> **Note**  
> - You can choose OIDC or SAML as the standard protocol for your application. See the complete list of [OIDC](https://is.docs.wso2.com/en/latest/references/app-settings/oidc-settings-for-app/) and [SAML](https://is.docs.wso2.com/en/latest/references/app-settings/saml-settings-for-app/) configurations.  
> - If you use OIDC, you can authorize APIs to an app to access the APIs in the Console. Learn about [Authorize the API resources for an app](https://is.docs.wso2.com/en/latest/guides/authorization/api-authorization/api-authorization/#authorize-the-api-resources-for-an-app).

4. Click **Register** to complete the registration.
5. After the application is registered, you will be redirected into the application.
6. Do the required changes to the application and click **Update**.
7. Get the created application's inbound protocol (OAuth2 / OIDC) configurations
   by using the following REST API call.

    ```bash
    curl --location 'https://localhost:9443/t/<TENANT_DOMAIN>/api/server/v1/applications/<APPLICATION_ID>/inbound-protocols/oidc' \
    --header 'Authorization: Basic YWRtaW46YWRtaW4='
    ```

8. Copy the response of the above REST API call and add the following value to the
   `scopeValidators` array in the response.

    ```json
   {
    "scopeValidators": [
            "XACML Scope Validator"
    ]
   }
    ```

9. Execute the Inbound Protocols PUT REST API call to update the application with the
   XACML scope validator which added in the step 7. You can get the API call details from
   <a href="https://is.docs.wso2.com/en/latest/apis/application-rest-api/#tag/Inbound-Protocols-OAuth-OIDC/operation/updateInboundOAuthConfiguration/">Update OIDC authentication protocol parameters</a>.

### Register an API resource and Authorize for an app

1. Go to the **API Resources** and click **New API Resource**.
2. Add the relevant details to Basic Details, Scopes, Authorization sections and click **Create**.
3. Go to the **API Authorization** tab in the created application.
4. Click **Authorize an API Resource**, select the created API resource, scopes and click **Finish**.

   > **Note**  
   For more information on API Authorization, see [here](https://is.docs.wso2.com/en/latest/guides/authorization/api-authorization/api-authorization/).


### Create a user and a role

1. Go to **User Management** > **Users**.
2. Click **Add User** button and select **Single User** option.
3. Provide the relevant details and click **Save & Continue**.
4. Go to the **User Management** > **Roles**.
5. Click **New Role**, add the Basic Details, Permission Selection and click **Finish**.
6. Go to the **Users** tab of the created role and assign the created user to the role.

   > **Note**  
   For more information on User Management, see [here](https://is.docs.wso2.com/en/latest/guides/users/).

The next step is to configure the XACML policy to validate the XACML scope during OAuth
token issuance.

### Set up the policy

Follow the instructions given below to publish a policy using a XACML policy
template that is available [here](https://github.com/wso2-extensions/identity-application-authz-xacml/tree/master/resources/policies).

1. Log in to the Console via <https://localhost:9443/console>.
2. Navigate to **Policy Administration**.
3. Click on "New Policy" and copy the `scope_based_token_issuance_policy_template`
   policy from [here](https://github.com/wso2-extensions/identity-application-authz-xacml/blob/master/resources/policies/scope_based_token_issuance_policy_template.xml), and paste it in the policy editor.
4. Edit the policy to customize it depending on your requirement. You
   can change the values of attributes and rules.
5. Click **Create** to save the changes. 
6. The policy is initially created in an **Inactive** state. To activate it, click the **Activate** button.

Now, you have created the policy and enforced it using the policy
template. You can test the policy to evaluate whether XACML scope is
validated at the time of OAuth token issuance.

### Try it out

Follow the steps given below to try out the policy using the above created application.

1. On the Console, go to **Applications**.
2. Select your application, go to the **Protocols** tab of the application and enable the **Password** grant type.
3. Execute the following cURL command to get an access token using the password grant type.

```bash
curl --location 'https://localhost:9443/t/<TENANT_DOMAIN>/oauth2/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--header 'Authorization: Basic <BASE64_ENCODED(CLIENT_ID:CLIENT_SECRET)>' \
--data-urlencode 'grant_type=password' \
--data-urlencode 'username=<USER_NAME>' \
--data-urlencode 'password=<USER_PASSWORD>' \
--data-urlencode 'scope=openid SCOPE_1'
```
If the token request can be permitted by the policy, the response will contain an access token.
If the token request is denied by the policy, the response will contain an error message.
