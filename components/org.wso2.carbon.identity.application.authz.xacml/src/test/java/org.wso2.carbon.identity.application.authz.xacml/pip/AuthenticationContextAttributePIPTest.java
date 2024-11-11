/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
import org.mockito.MockedStatic;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticationRequest;
import org.wso2.carbon.identity.application.authentication.framework.util.FrameworkUtils;
import org.wso2.carbon.identity.application.authz.xacml.constants.XACMLAppAuthzConstants;
import org.wso2.carbon.identity.base.IdentityConstants;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * AuthenticationContextAttributePIPTest defines unit tests for AuthenticationContextAttributePIP class.
 */
public class AuthenticationContextAttributePIPTest {

    private AuthenticationContextAttributePIP authenticationContextAttributePIP;
    private EvaluationCtx evaluationCtx;
    private URI attributeType;
    private URI attributeId;
    private URI category;
    private String issuer;
    private static final String TYPE = "type";
    private static final String CONTEXT_ID = "context_id";
    private static final String URI_STRING_1 = "12345";
    private static final String URI_STRING_2 = "12345/12345";
    private static final String URI_STRING_3 = "//:12345";
    private static final String ISSUER = "travelocity.com";
    private static final String USER_IP = "user-ip";
    private static final String ATTRIBUTE_VALUE = "attributeValue";
    private static final String HEADER = "header";
    private static final String AUTHENTICATION_CONTEXT_ATTRIBUTE_PIP = "AuthenticationContextAttributePIP";

    @BeforeClass
    public void init() throws URISyntaxException {

        authenticationContextAttributePIP = new AuthenticationContextAttributePIP();
        evaluationCtx = mock(EvaluationCtx.class);
        issuer = ISSUER;
        attributeType = new URI(URI_STRING_1);
        attributeId = new URI(URI_STRING_1);
        category = new URI(URI_STRING_1);
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testGetAttributeValues() throws Exception {

        authenticationContextAttributePIP.getAttributeValues("a", "b", "c", "d", "e", "f");
    }

    @DataProvider(name = "BuildAttributeValues")
    public Object[][] buildAttributeValues() throws URISyntaxException {

        AttributeValue attributeValue = new AttributeValue(new URI(TYPE)) {
            @Override
            public String encode() {

                return CONTEXT_ID;
            }
        };
        List<AttributeValue> attributeValueList = new ArrayList<>();
        attributeValueList.add(attributeValue);
        BagAttribute bagAttribute1 = new BagAttribute(new URI(TYPE), new ArrayList<AttributeValue>());
        BagAttribute bagAttribute2 = new BagAttribute(new URI(TYPE), attributeValueList);
        AuthenticationContext authenticationContext = new AuthenticationContext();
        Map<String, String[]> queryParams = new HashMap<>();
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        attributeValueList.add(attributeValue);
        authenticationRequest.setRequestQueryParams(queryParams);
        authenticationContext.setAuthenticationRequest(authenticationRequest);

        return new Object[][]{
                {new URI(TYPE), new URI(StringUtils.EMPTY), null, ISSUER, evaluationCtx, null, null, null, 0},
                {new URI(TYPE), new URI(URI_STRING_1), null, ISSUER, evaluationCtx, null, null, null, 0},
                {new URI(TYPE), new URI(URI_STRING_1), null, ISSUER, evaluationCtx,
                        new EvaluationResult((AttributeValue) null), null, null, 0},
                {new URI(TYPE), new URI(URI_STRING_1), null, ISSUER, evaluationCtx, new EvaluationResult(attributeValue)
                        , null, null, 0},
                {new URI(TYPE), new URI(URI_STRING_1), null, ISSUER, evaluationCtx, new EvaluationResult(bagAttribute1)
                        , null, null, 0},
                {new URI(TYPE), new URI(URI_STRING_1), null, ISSUER, evaluationCtx, new EvaluationResult(bagAttribute2)
                        , null, null, 0},
                {new URI(TYPE), new URI(URI_STRING_1), new URI(XACMLAppAuthzConstants.AUTH_CONTEXT_PROPERTY_CATEGORY)
                        , ISSUER, evaluationCtx, new EvaluationResult(bagAttribute2), authenticationContext, null, 0},
                {new URI(TYPE), new URI(URI_STRING_1), new URI(XACMLAppAuthzConstants.AUTH_CONTEXT_REQ_PARAM_CATEGORY)
                        , ISSUER, evaluationCtx, new EvaluationResult(bagAttribute2), authenticationContext, null, 0},
                {new URI(TYPE), new URI(URI_STRING_1), new URI(XACMLAppAuthzConstants.AUTH_CONTEXT_REQ_HEADER_CATEGORY)
                        , ISSUER, evaluationCtx, new EvaluationResult(bagAttribute2), authenticationContext, null, 1},
                {new URI(TYPE), new URI(XACMLAppAuthzConstants.INBOUND_PROTOCOL_ATTRIBUTE), new URI(StringUtils.EMPTY)
                        , ISSUER, evaluationCtx, new EvaluationResult(bagAttribute2), authenticationContext, null, 1},
                {new URI(TYPE), new URI(XACMLAppAuthzConstants.CLIENT_IP_ATTRIBUTE), new URI(StringUtils.EMPTY)
                        , ISSUER, evaluationCtx, new EvaluationResult(bagAttribute2), authenticationContext, null, 0},
                {new URI(TYPE), new URI(XACMLAppAuthzConstants.CLIENT_IP_ATTRIBUTE), new URI(StringUtils.EMPTY)
                        , ISSUER, evaluationCtx, new EvaluationResult(bagAttribute2), authenticationContext
                        , IdentityConstants.USER_IP, 1}
        };
    }

    @Test(dataProvider = "BuildAttributeValues")
    public void testOverloadedGetAttributeValues(Object type, Object id, Object group, String issuer,
                                                 Object evaluationContext, Object evaluationResult,
                                                 Object authenticationContext, String parameter, int expectedResult)
            throws Exception {

        try (MockedStatic<FrameworkUtils> frameworkUtilsMockedStatic = mockStatic(FrameworkUtils.class);) {
            URI attributeType = (URI) type;
            URI attributeId = (URI) id;
            URI category = (URI) group;
            EvaluationCtx evaluationCtx = (EvaluationCtx) evaluationContext;
            EvaluationResult context = (EvaluationResult) evaluationResult;
            AuthenticationContext authCtx = (AuthenticationContext) authenticationContext;
            if (XACMLAppAuthzConstants.CLIENT_IP_ATTRIBUTE.equals(attributeId.toString())) {
                authCtx.addParameter(parameter, USER_IP);
            }
            when(evaluationCtx.getAttribute(any(URI.class), any(URI.class), anyString(), any(URI.class)))
                    .thenReturn(context);
            frameworkUtilsMockedStatic.when(() -> FrameworkUtils.getAuthenticationContextFromCache(anyString()))
                    .thenReturn(authCtx);
            when(FrameworkUtils.getAuthenticationContextFromCache(anyString())).thenReturn(authCtx);
            Set<String> attributeValues = authenticationContextAttributePIP.getAttributeValues(attributeType,
                    attributeId, category, issuer, evaluationCtx);
            assertEquals(attributeValues.size(), expectedResult);
        }
    }

    @Test
    public void testGetModuleName() {

        assertEquals(authenticationContextAttributePIP.getModuleName(), AUTHENTICATION_CONTEXT_ATTRIBUTE_PIP);
    }

    @Test
    public void testGetSupportedAttributes() {

        assertTrue(authenticationContextAttributePIP.getSupportedAttributes().size() > 0);
    }

    @Test
    public void testGetAuthenticationContextProperty() throws Exception {

        attributeId = new URI(URI_STRING_2);

        AuthenticationContext authenticationContext = mock(AuthenticationContext.class);
        Object object = new Object();
        when(authenticationContext.getProperty(anyString())).thenReturn(object);
        Method getAuthenticationContextProperty = AuthenticationContextAttributePIP.class.getDeclaredMethod(
                "getAuthenticationContextProperty", AuthenticationContext.class, URI.class, URI.class, URI.class,
                String.class, EvaluationCtx.class);
        getAuthenticationContextProperty.setAccessible(true);

        Object propertySetObject = getAuthenticationContextProperty.invoke(
                authenticationContextAttributePIP, authenticationContext,
                attributeType, attributeId, category, issuer, evaluationCtx);
        Set<String> propertySet = getpropertySet(propertySetObject);
        assertEquals(propertySet.size(), 1);

        object = ATTRIBUTE_VALUE;
        when(authenticationContext.getProperty(anyString())).thenReturn(object);
        propertySetObject = getAuthenticationContextProperty.invoke(authenticationContextAttributePIP,
                authenticationContext, attributeType, attributeId, category, issuer, evaluationCtx);
        propertySet = getpropertySet(propertySetObject);
        assertEquals(propertySet.size(), 1);

        attributeId = new URI(URI_STRING_3);
        propertySetObject = getAuthenticationContextProperty.invoke(authenticationContextAttributePIP,
                authenticationContext, attributeType, attributeId, category, issuer, evaluationCtx);
        propertySet = getpropertySet(propertySetObject);
        assertEquals(propertySet.size(), 1);
    }

    @Test
    public void testGetAuthenticationRequestParameter() throws Exception {

        attributeId = new URI(URI_STRING_3);
        AuthenticationContext authenticationContext = mock(AuthenticationContext.class);

        AuthenticationRequest authenticationRequest = mock(AuthenticationRequest.class);
        String[] queryParams = new String[]{"1", "2"};
        when(authenticationContext.getAuthenticationRequest()).thenReturn(authenticationRequest);
        when(authenticationRequest.getRequestQueryParam(anyString())).thenReturn(queryParams);

        Method getAuthenticationRequestParameter = AuthenticationContextAttributePIP.class.getDeclaredMethod(
                "getAuthenticationRequestParameter", AuthenticationContext.class, URI.class, URI.class, URI.class,
                String.class, EvaluationCtx.class);
        getAuthenticationRequestParameter.setAccessible(true);

        Object propertySetObject = getAuthenticationRequestParameter.invoke(authenticationContextAttributePIP, authenticationContext,
                attributeType, attributeId, category, issuer, evaluationCtx);
        Set<String> propertySet = getpropertySet(propertySetObject);
        assertEquals(propertySet.size(), 2);

        attributeId = new URI(URI_STRING_2);
        propertySetObject = getAuthenticationRequestParameter.invoke(authenticationContextAttributePIP, authenticationContext,
                attributeType, attributeId, category, issuer, evaluationCtx);
        propertySet = getpropertySet(propertySetObject);
        assertEquals(propertySet.size(), 2);
    }

    @Test
    public void testGetAuthenticationRequestHeader() throws Exception {

        attributeId = new URI(URI_STRING_2);

        AuthenticationContext authenticationContext = mock(AuthenticationContext.class);
        AuthenticationRequest authenticationRequest = mock(AuthenticationRequest.class);
        Map<String, String> map = mock(HashMap.class);
        when(authenticationContext.getAuthenticationRequest()).thenReturn(authenticationRequest);
        when(authenticationRequest.getRequestHeaders()).thenReturn(map);
        when(map.get(anyString())).thenReturn(HEADER);

        Method getAuthenticationRequestHeader = AuthenticationContextAttributePIP.class.getDeclaredMethod(
                "getAuthenticationRequestHeader", AuthenticationContext.class, URI.class, URI.class, URI.class,
                String.class, EvaluationCtx.class);
        getAuthenticationRequestHeader.setAccessible(true);

        Object propertySetObject = getAuthenticationRequestHeader.invoke(authenticationContextAttributePIP, authenticationContext,
                attributeType, attributeId, category, issuer, evaluationCtx);
        Set<String> propertySet = getpropertySet(propertySetObject);
        assertEquals(propertySet.size(), 1);

        attributeId = new URI(URI_STRING_3);
        propertySetObject = getAuthenticationRequestHeader.invoke(authenticationContextAttributePIP, authenticationContext,
                attributeType, attributeId, category, issuer, evaluationCtx);
        propertySet = getpropertySet(propertySetObject);
        assertEquals(propertySet.size(), 1);
    }

    @SuppressWarnings("unchecked")
    private Set<String> getpropertySet(Object propertySetObject) {

        if (propertySetObject instanceof Set<?>) {
            Set<?> tempSet = (Set<?>) propertySetObject;
            if (tempSet.stream().allMatch(element -> element instanceof String)) {
                // Safe cast if all elements are Strings.
                return (Set<String>) tempSet;
            } else {
                throw new ClassCastException("Property contains non-string elements");
            }
        } else {
            throw new ClassCastException("Property is not a Set");
        }
    }
}

