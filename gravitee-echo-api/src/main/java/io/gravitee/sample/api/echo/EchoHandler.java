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
package io.gravitee.sample.api.echo;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Nicolas GERAUD (nicolas at graviteesource.com)
 * @author GraviteeSource Team
 */
public class EchoHandler implements Handler<RoutingContext> {

    @Override
    public void handle(final RoutingContext routingContext) {
        final HttpServerRequest request = routingContext.request();
        request.pause();

        int statusCode = HttpResponseStatus.OK.code();
        String statusMessage = HttpResponseStatus.OK.reasonPhrase();
        if (request.getParam("statusCode") != null) {
            try {
                statusCode = Integer.parseInt(request.getParam("statusCode"));
                statusMessage = HttpResponseStatus.valueOf(statusCode).reasonPhrase();
            } catch (NumberFormatException e) {
                // NaN so we do nothing
            }
        }
        if (request.getParam("statusMessage") != null) {
            statusMessage = request.getParam("statusMessage");
        }

        final AtomicLong bodySize = new AtomicLong(0);
        final JsonObject content = jsonEchoResponse(request);
        final int finalStatusCode = statusCode;
        final String finalStatusMessage = statusMessage;

        request.handler(event -> bodySize.addAndGet(event.length()));
        request.endHandler(aVoid -> {
            content.put("bodySize", bodySize.get());
            routingContext
                .response()
                .putHeader("content-type", "application/json")
                .setStatusCode(finalStatusCode)
                .setStatusMessage(finalStatusMessage)
                .end(content.encodePrettily());
        });

        request.resume();
    }

    private JsonObject jsonEchoResponse(HttpServerRequest request) {
        JsonObject content = new JsonObject();

        //headers
        JsonObject headers = new JsonObject();
        request
            .headers()
            .entries()
            .forEach(entry -> {
                String value = entry.getValue();
                if (headers.containsKey(entry.getKey())) {
                    value = String.join(",", String.valueOf(headers.getValue(entry.getKey())), value);
                }
                headers.put(entry.getKey(), value);
            });

        content.put("headers", headers);

        //query params
        JsonObject queryParams = new JsonObject();
        for (Map.Entry<String, String> entry : request.params().entries()) {
            if (queryParams.containsKey(entry.getKey())) {
                if (queryParams.getValue(entry.getKey()) instanceof String) {
                    //transform String to List
                    List<Object> values = new ArrayList<>();
                    values.add(queryParams.getValue(entry.getKey()));
                    queryParams.put(entry.getKey(), values);
                }
                ((JsonArray) queryParams.getValue(entry.getKey())).add(entry.getValue());
            } else {
                queryParams.put(entry.getKey(), entry.getValue());
            }
        }
        content.put("query_params", queryParams);
        return content;
    }
}
