/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
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

package org.wso2.carbon.identity.entitlement.endpoint.exception;

import org.wso2.carbon.identity.entitlement.endpoint.util.EntitlementEndpointConstants;

/**
 * Concrete exception class extending AnstractEntitlementExcetion
 * Corresponds to an error occrured in Authentication
 */
public class UnauthorizedException extends AbstractEntitlementException {
    public UnauthorizedException() {
        super(EntitlementEndpointConstants.ERROR_UNAUTHORIZED_CODE);
    }

    public UnauthorizedException(String message) {
        super(EntitlementEndpointConstants.ERROR_UNAUTHORIZED_CODE, message);
    }
}
