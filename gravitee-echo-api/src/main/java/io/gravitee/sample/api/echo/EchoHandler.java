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

/**
 * @author Nicolas GERAUD (nicolas at graviteesource.com)
 * @author GraviteeSource Team
 */
public class EchoHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext routingContext) {
        if (HttpMethod.GET.equals(routingContext.request().method())) {
            JsonObject content = new JsonObject();

            //headers
            JsonObject headers = new JsonObject();
            int statusCode = routingContext.request().getParam("statusCode") == null
                    ? 200
                    : Integer.valueOf(routingContext.request().getParam("statusCode"));
            routingContext.request().headers().forEach(entry -> headers.put(entry.getKey(), entry.getValue()));
            content.put("headers", headers);

            //query params
            JsonObject queryParams = new JsonObject();
            for (Map.Entry<String, String> entry : routingContext.request().params().entries()) {
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
            //response
            routingContext.response()
                    .putHeader("content-type", "application/json")
                    .setStatusCode(statusCode)
                    .end(content.encodePrettily());
        } else {
            HttpServerRequest request = routingContext.request();
            HttpServerResponse response = routingContext.response();

            int statusCode = request.getParam("statusCode") == null
                    ? 200
                    : Integer.valueOf(request.getParam("statusCode"));

            response.setStatusCode(statusCode);

            request.headers().entries().stream()
                    .filter( entry -> !"host".equalsIgnoreCase(entry.getKey()) )
                    .forEach( entry -> response.putHeader(entry.getKey(), entry.getValue()) );

            request.handler(response::write);
            request.endHandler(aVoid -> response.end());
        }
    }
}
