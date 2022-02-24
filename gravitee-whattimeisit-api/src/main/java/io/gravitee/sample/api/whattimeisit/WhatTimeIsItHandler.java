/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.sample.api.whattimeisit;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Nicolas GERAUD (nicolas at graviteesource.com)
 * @author GraviteeSource Team
 */
public class WhatTimeIsItHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext routingContext) {
        JsonObject content = new JsonObject();

        Date now = new Date();
        content.put("timestamp", now.getTime());
        content.put("date", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS").format(now));

        //response
        HttpServerResponse response = routingContext.response();
        response.putHeader("content-type", "application/json").setStatusCode(200).end(content.encodePrettily());
    }
}
