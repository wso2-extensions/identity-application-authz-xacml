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

import org.powermock.api.mockito.PowerMockito;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.balana.ctx.EvaluationCtx;

import java.net.URI;
import java.util.Set;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

/**
 * AuthenticationContextAttributePIPTest defines unit tests for AuthenticationContextAttributePIP class.
 */
public class AuthenticationContextAttributePIPTest {

    private AuthenticationContextAttributePIP authenticationContextAttributePIP;
    private EvaluationCtx evaluationCtx;

    @BeforeClass
    public void init() {

        authenticationContextAttributePIP = new AuthenticationContextAttributePIP();
        evaluationCtx = PowerMockito.mock(EvaluationCtx.class);
    }


    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testGetAttributeValues() throws Exception {

        authenticationContextAttributePIP.getAttributeValues("a", "b", "c", "d", "e", "f");
    }

    @Test
    public void testOverloadedGetAttributeValues() throws Exception {

        Set<String> result = authenticationContextAttributePIP.getAttributeValues(new URI("http://wso2.org"),
                new URI("12345"), new URI("local"), "travelocity.com", evaluationCtx);
        assertEquals(result.size(), 0);
    }

    @Test
    public void testGetModuleName(){

        assertEquals(authenticationContextAttributePIP.getModuleName(),
                "AuthenticationContextAttributePIP");
    }

    @Test
    public void testGetSupportedAttributes(){

        assertTrue(authenticationContextAttributePIP.getSupportedAttributes().size() > 0);
    }



}
