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

package org.wso2.carbon.identity.entitlement.cache;

import org.wso2.carbon.identity.entitlement.PDPConstants;

import java.util.Set;

/**
 *
 */
public class PIPAttributeCache extends EntitlementBaseCache<IdentityCacheKey, IdentityCacheEntry> {

    public PIPAttributeCache(int timeOut) {
        super(PDPConstants.PIP_ATTRIBUTE_CACHE, timeOut);
    }

    public void addToCache(int tenantId, String key, Set<String> attributes) {

        IdentityCacheKey cacheKey = new IdentityCacheKey(tenantId, key);
        IdentityCacheEntry cacheEntry = new IdentityCacheEntry(attributes);
        addToCache(cacheKey, cacheEntry);
    }

    public Set<String> getFromCache(int tenantId, String key) {

        Set<String> attributes = null;
        IdentityCacheKey cacheKey = new IdentityCacheKey(tenantId, key);
        IdentityCacheEntry cacheEntry = getValueFromCache(cacheKey);
        if (cacheEntry != null) {
            attributes = cacheEntry.getCacheEntrySet();
        }
        return attributes;
    }

    public void clearCache() {
        clear();
    }
}
