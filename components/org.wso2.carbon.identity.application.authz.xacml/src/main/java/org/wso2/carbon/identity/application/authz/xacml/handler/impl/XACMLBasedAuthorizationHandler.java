/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.application.authz.xacml.handler.impl;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jaxen.JaxenException;
import org.wso2.balana.utils.Constants.PolicyConstants;
import org.wso2.balana.utils.exception.PolicyBuilderException;
import org.wso2.balana.utils.policy.PolicyBuilder;
import org.wso2.balana.utils.policy.dto.RequestElementDTO;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.exception.FrameworkException;
import org.wso2.carbon.identity.application.authentication.framework.exception.PostAuthenticationFailedException;
import org.wso2.carbon.identity.application.authentication.framework.handler.request.AbstractPostAuthnHandler;
import org.wso2.carbon.identity.application.authentication.framework.handler.request.PostAuthnHandlerFlowStatus;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.application.authentication.framework.util.FrameworkUtils;
import org.wso2.carbon.identity.application.authz.xacml.constants.XACMLAppAuthzConstants;
import org.wso2.carbon.identity.application.authz.xacml.internal.AppAuthzDataholder;
import org.wso2.carbon.identity.base.IdentityConstants;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.identity.entitlement.EntitlementException;
import org.wso2.carbon.identity.entitlement.common.EntitlementPolicyConstants;
import org.wso2.carbon.identity.entitlement.common.dto.RequestDTO;
import org.wso2.carbon.identity.entitlement.common.dto.RowDTO;
import org.wso2.carbon.identity.entitlement.common.util.PolicyCreatorUtil;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;

public class XACMLBasedAuthorizationHandler extends AbstractPostAuthnHandler {

    private static final Log log = LogFactory.getLog(XACMLBasedAuthorizationHandler.class);
    private static final String DECISION_XPATH = "//ns:Result/ns:Decision/text()";
    private static final String XACML_NS = "urn:oasis:names:tc:xacml:3.0:core:schema:wd-17";
    private static final String XACML_NS_PREFIX = "ns";
    private static final String RULE_EFFECT_PERMIT = "Permit";
    private static final String RULE_EFFECT_NOT_APPLICABLE = "NotApplicable";
    public static final String ACTION_AUTHENTICATE = "authenticate";

    /**
     * Executes the authorization flow
     *
     * @param request  request
     * @param response response
     * @param context  context
     */
    @Override
    public PostAuthnHandlerFlowStatus handle(HttpServletRequest request, HttpServletResponse response,
                                             AuthenticationContext context) throws PostAuthenticationFailedException {

        if (log.isDebugEnabled()) {
            log.debug("In policy authorization flow...");
        }

        if (!isAuthorizationEnabled(context) && getAuthenticatedUser(context) == null) {
            return PostAuthnHandlerFlowStatus.SUCCESS_COMPLETED;
        }
        try {
//                get the ip from request and add as a authctx property because the request won't available at PIP
            context.addParameter(IdentityConstants.USER_IP, IdentityUtil.getClientIpAddress(request));
            FrameworkUtils.addAuthenticationContextToCache(context.getContextIdentifier(), context);

            //TODO: "RequestDTO" and "PolicyCreatorUtil" is taken from entitlement.ui. Need to reconsider of
            // using the ui bundle
            RequestDTO requestDTO = createRequestDTO(context);
            RequestElementDTO requestElementDTO = PolicyCreatorUtil.createRequestElementDTO(requestDTO);

            String requestString = PolicyBuilder.getInstance().buildRequest(requestElementDTO);
            if (log.isDebugEnabled()) {
                log.debug("XACML Authorization request :\n" + requestString);
            }

            FrameworkUtils.startTenantFlow(context.getTenantDomain());
            String responseString =
                    AppAuthzDataholder.getInstance().getEntitlementService().getDecision(requestString);
            if (log.isDebugEnabled()) {
                log.debug("XACML Authorization response :\n" + responseString);
            }
            String authzResponse = evaluateXACMLResponse(responseString);
            boolean isAuthorized = false;
            if (RULE_EFFECT_NOT_APPLICABLE.equalsIgnoreCase(authzResponse)) {
                log.warn(String.format(
                        "No applicable rule for service provider '%s@%s', Hence authorizing the user by default. " +
                                "Add an authorization policy (or unset authorization) to fix this warning.",
                        context.getServiceProviderName(), context.getTenantDomain()));
                isAuthorized = true;
            } else if (RULE_EFFECT_PERMIT.equalsIgnoreCase(authzResponse)) {
                isAuthorized = true;
            }
            FrameworkUtils.removeAuthenticationContextFromCache(context.getContextIdentifier());
            if (!isAuthorized) {
                throw new PostAuthenticationFailedException("Authorization Failed", "XACML policy evaluation failed " +
                        "for user");
            } else {
                return PostAuthnHandlerFlowStatus.SUCCESS_COMPLETED;
            }
        } catch (PolicyBuilderException | EntitlementException | FrameworkException e) {
            throw new PostAuthenticationFailedException("Authorization Failed", "Error while trying to evaluate " +
                    "authorization", e);
        } finally {
            FrameworkUtils.endTenantFlow();
        }
    }

    private RequestDTO createRequestDTO(AuthenticationContext context) {

        List<RowDTO> rowDTOs = new ArrayList<>();
        RowDTO actionDTO =
                createRowDTO(ACTION_AUTHENTICATE,
                        XACMLAppAuthzConstants.AUTH_ACTION_ID, XACMLAppAuthzConstants.ACTION_CATEGORY);
        RowDTO contextIdentifierDTO =
                createRowDTO(context.getContextIdentifier(),
                        XACMLAppAuthzConstants.AUTH_CTX_ID, XACMLAppAuthzConstants.AUTH_CATEGORY);
        RowDTO spNameDTO =
                createRowDTO(context.getServiceProviderName(),
                        XACMLAppAuthzConstants.SP_NAME_ID, XACMLAppAuthzConstants.SP_CATEGORY);
        RowDTO spDomainDTO =
                createRowDTO(context.getTenantDomain(),
                        XACMLAppAuthzConstants.SP_DOMAIN_ID, XACMLAppAuthzConstants.SP_CATEGORY);
        RowDTO usernameDTO =
                createRowDTO(context.getSequenceConfig().getAuthenticatedUser().getUserName(),
                        XACMLAppAuthzConstants.USERNAME_ID, XACMLAppAuthzConstants.USER_CATEGORY);
        RowDTO userStoreDomainDTO =
                createRowDTO(context.getSequenceConfig().getAuthenticatedUser().getUserStoreDomain(),
                        XACMLAppAuthzConstants.USER_STORE_ID, XACMLAppAuthzConstants.USER_CATEGORY);
        RowDTO userTenantDomainDTO =
                createRowDTO(context.getSequenceConfig().getAuthenticatedUser().getTenantDomain(),
                        XACMLAppAuthzConstants.USER_TENANT_DOMAIN_ID, XACMLAppAuthzConstants.USER_CATEGORY);
        String subject = null;
        if (context.getSequenceConfig() != null && context.getSequenceConfig().getAuthenticatedUser() != null) {
            subject = context.getSequenceConfig().getAuthenticatedUser().toString();
        }
        if (subject != null) {
            RowDTO subjectDTO =
                    createRowDTO(subject, PolicyConstants.SUBJECT_ID_DEFAULT, PolicyConstants.SUBJECT_CATEGORY_URI);
            rowDTOs.add(subjectDTO);
        }
        rowDTOs.add(actionDTO);
        rowDTOs.add(contextIdentifierDTO);
        rowDTOs.add(spNameDTO);
        rowDTOs.add(spDomainDTO);
        rowDTOs.add(usernameDTO);
        rowDTOs.add(userStoreDomainDTO);
        rowDTOs.add(userTenantDomainDTO);
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setRowDTOs(rowDTOs);
        return requestDTO;
    }

    private RowDTO createRowDTO(String resourceName, String attributeId, String categoryValue) {

        RowDTO rowDTOTenant = new RowDTO();
        rowDTOTenant.setAttributeValue(resourceName);
        rowDTOTenant.setAttributeDataType(EntitlementPolicyConstants.STRING_DATA_TYPE);
        rowDTOTenant.setAttributeId(attributeId);
        rowDTOTenant.setCategory(categoryValue);
        return rowDTOTenant;

    }

    private String evaluateXACMLResponse(String xacmlResponse) throws FrameworkException {

        try {
            AXIOMXPath axiomxPath = new AXIOMXPath(DECISION_XPATH);
            axiomxPath.addNamespace(XACML_NS_PREFIX, XACML_NS);
            OMElement rootElement =
                    new StAXOMBuilder(new ByteArrayInputStream(xacmlResponse.getBytes(StandardCharsets.UTF_8)))
                            .getDocumentElement();
            return axiomxPath.stringValueOf(rootElement);
        } catch (JaxenException | XMLStreamException e) {
            throw new FrameworkException("Exception occurred when getting decision from xacml response.", e);
        }
    }

    private AuthenticatedUser getAuthenticatedUser(AuthenticationContext authenticationContext) {

        if (authenticationContext != null && authenticationContext.getSequenceConfig() != null) {
            return authenticationContext.getSequenceConfig().getAuthenticatedUser();
        }
        return null;
    }

    private boolean isAuthorizationEnabled(AuthenticationContext authenticationContext) {

        if (authenticationContext != null && authenticationContext.getSequenceConfig() != null &&
                authenticationContext.getSequenceConfig().getApplicationConfig() != null) {
            return authenticationContext.getSequenceConfig().getApplicationConfig().isEnableAuthorization();
        }
        return false;
    }
}
