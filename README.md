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

2. Download the XACML connector artifacts from here https://store.wso2.com.

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

7. Add default XACML policies resides in folder
`<XACML_CONNECTOR>/config-files/policies`  to the
`<IS_HOME>/repository/resources/identity/policies/xacml/default` folder.

8. Restart Identity Server.
