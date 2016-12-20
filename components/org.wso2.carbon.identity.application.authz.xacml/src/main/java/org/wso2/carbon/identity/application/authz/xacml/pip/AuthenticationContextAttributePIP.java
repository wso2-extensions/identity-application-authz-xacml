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

package org.wso2.carbon.identity.application.authz.xacml.pip;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.util.FrameworkUtils;
import org.wso2.carbon.identity.application.authz.xacml.constants.XACMLAppAuthzConstants;
import org.wso2.carbon.identity.base.IdentityConstants;
import org.wso2.carbon.identity.entitlement.pip.AbstractPIPAttributeFinder;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Provides the attributes from authentication context. The attributes include the following
 * <ul>
 *     <li><b>http://wso2.org/identity/auth-context-property</b> - Authentication context properties</li>
 *     <li><b>http://wso2.org/identity/auth-context-request-param</b> - Authentication request parameters</li>
 *     <li><b>http://wso2.org/identity/auth-context-request-header</b> - Authentication request headers</li>
 *     <li><b>http://wso2.org/authentication/user-ip</b> - Authenticated user's IP</li>
 *     <li><b>http://wso2.org/authentication/inbound-protocol</b> - Authentication protocol (eg. saml)</li>
 * </ul>
 */
public class AuthenticationContextAttributePIP extends AbstractPIPAttributeFinder {

    private static final String PIP_NAME = "AuthenticationContextAttributePIP";

    private static final Set<String> SUPPORTED_ATTRIBUTES;
    private static final Log log = LogFactory.getLog(AuthenticationContextAttributePIP.class);

    static {
        SUPPORTED_ATTRIBUTES = new HashSet<>();
        SUPPORTED_ATTRIBUTES.add(XACMLAppAuthzConstants.INBOUND_PROTOCOL_ATTRIBUTE);
        SUPPORTED_ATTRIBUTES.add(XACMLAppAuthzConstants.CLIENT_IP_ATTRIBUTE);
        SUPPORTED_ATTRIBUTES.add(XACMLAppAuthzConstants.AUTH_CONTEXT_PROPERTY_CATEGORY);
        SUPPORTED_ATTRIBUTES.add(XACMLAppAuthzConstants.AUTH_CONTEXT_REQ_PARAM_CATEGORY);
        SUPPORTED_ATTRIBUTES.add(XACMLAppAuthzConstants.AUTH_CONTEXT_REQ_HEADER_CATEGORY);
    }

    /**
     * Since we override the {@link #getAttributeValues(URI, URI, URI, String, EvaluationCtx)} this won't be called.
     */
    @Override
    public Set<String> getAttributeValues(String subject, String resource, String action, String environment,
                                          String attributeId, String issuer) throws Exception {

        throw new UnsupportedOperationException("Method unsupported in the context");
    }

    @Override
    public Set<String> getAttributeValues(URI attributeType, URI attributeId, URI category, String issuer,
                                          EvaluationCtx evaluationCtx) throws Exception {

        EvaluationResult context;
        String contextId = null;

        if (StringUtils.isBlank(attributeId.toString())) {
            log.debug("Empty attribute URI received..");
            return Collections.EMPTY_SET;
        }
        context = evaluationCtx
                .getAttribute(new URI(StringAttribute.identifier), new URI(XACMLAppAuthzConstants.AUTH_CTX_ID),
                        issuer, new URI(XACMLAppAuthzConstants.AUTH_CATEGORY));
        if (context != null && context.getAttributeValue() != null && context.getAttributeValue().isBag()) {
            BagAttribute bagAttribute = (BagAttribute) context.getAttributeValue();
            if (bagAttribute.size() > 0) {
                contextId = ((AttributeValue) bagAttribute.iterator().next()).encode();
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Finding attributes for the context %1$s", contextId));
                }
            }
        }


        if (contextId != null) {
            AuthenticationContext authCtx = FrameworkUtils.getAuthenticationContextFromCache(contextId);
            if (authCtx != null) {
                Set<String> values = new HashSet<>();
                switch ((category.toString())) {
                    case XACMLAppAuthzConstants.AUTH_CONTEXT_PROPERTY_CATEGORY:
                        values = getAuthenticationContextProperty(authCtx, attributeType, attributeId, category,
                                issuer, evaluationCtx);
                        break;
                    case XACMLAppAuthzConstants.AUTH_CONTEXT_REQ_PARAM_CATEGORY:
                        values = getAuthenticationRequestParameter(authCtx, attributeType, attributeId, category,
                                issuer, evaluationCtx);
                        break;
                    case XACMLAppAuthzConstants.AUTH_CONTEXT_REQ_HEADER_CATEGORY:
                        values = getAuthenticationRequestHeader(authCtx, attributeType, attributeId, category,
                                issuer, evaluationCtx);
                        break;
                    default:
                }

                if (!values.isEmpty()) {
                    if (log.isDebugEnabled()) {
                        String valuesString = StringUtils.join(values, ",");
                        log.debug("Returning " + attributeId + " value as " + valuesString);
                    }
                    return values;
                }

                switch (attributeId.toString()) {
                    case XACMLAppAuthzConstants.INBOUND_PROTOCOL_ATTRIBUTE:
                        values.add(authCtx.getRequestType());
                        break;
                    case XACMLAppAuthzConstants.CLIENT_IP_ATTRIBUTE:
                        Object ipObj = authCtx.getParameter(IdentityConstants.USER_IP);
                        if (ipObj != null) {
                            values.add(ipObj.toString());
                        }
                        break;
                    default:
                }
                if (log.isDebugEnabled()) {
                    String valuesString = StringUtils.join(values, ",");
                    log.debug("Returning " + attributeId + " value as " + valuesString);
                }
                return values;
            }
        }
        return Collections.emptySet();
    }

    @Override
    public void init(Properties properties) throws Exception {

    }

    @Override
    public String getModuleName() {

        return PIP_NAME;
    }

    @Override
    public Set<String> getSupportedAttributes() {

        return SUPPORTED_ATTRIBUTES;
    }

    protected Set<String> getAuthenticationContextProperty(AuthenticationContext authCtx, URI attributeType, URI
            attributeId, URI category, String issuer, EvaluationCtx evaluationCtx) {

        Set<String> values = new HashSet<>();

        Object property = authCtx.getProperty(attributeId.toString());

        if (property != null) {
            if (property instanceof String) {
                values.add((String) property);
            } else {
                values.add(property.toString());
            }

        }

        return values;
    }

    protected Set<String> getAuthenticationRequestParameter(AuthenticationContext authCtx, URI attributeType, URI
            attributeId, URI category, String issuer, EvaluationCtx evaluationCtx) {

        Set<String> values = new HashSet<>();

        String[] params = authCtx.getAuthenticationRequest().getRequestQueryParam(attributeId.toString());

        if (params != null) {
            for (String param : params) {
                values.add(param);
            }
        }

        return values;
    }

    protected Set<String> getAuthenticationRequestHeader(AuthenticationContext authCtx, URI attributeType, URI
            attributeId, URI category, String issuer, EvaluationCtx evaluationCtx) {

        Set<String> values = new HashSet<>();

        String headers = authCtx.getAuthenticationRequest().getRequestHeaders().get(attributeId.toString());
        values.add(headers);

        return values;
    }
}
