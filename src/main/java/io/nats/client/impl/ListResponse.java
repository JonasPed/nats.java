// Copyright 2020 The NATS Authors
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at:
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package io.nats.client.impl;

import static io.nats.client.support.ApiConstants.*;

public abstract class ListResponse {

    private static final String OFFSET_JSON_START = "{\"offset\":";

    protected int total = Integer.MAX_VALUE; // so always has the first "at least one more"
    protected int limit = 0;
    protected int lastOffset = 0;

    void add(String json) {
        total = JsonUtils.readInt(json, TOTAL_RE, 0);
        limit = JsonUtils.readInt(json, LIMIT_RE, 0);
        lastOffset = JsonUtils.readInt(json, OFFSET_RE, 0);
    }

    boolean hasMore() {
        return total > (lastOffset + limit);
    }

    String internalNextJson() {
        return hasMore() ? noFilterJson() : null;
    }

    String noFilterJson() {
        return OFFSET_JSON_START + (lastOffset + limit) + "}";
    }

    String internalNextJson(String fieldName, String filter) {
        if (hasMore()) {
            if (filter == null) {
                return noFilterJson();
            }
            return OFFSET_JSON_START + (lastOffset + limit)
                    + ",\"" + fieldName + "\":\"" + filter + "\"}";
        }
        return null;
    }
}