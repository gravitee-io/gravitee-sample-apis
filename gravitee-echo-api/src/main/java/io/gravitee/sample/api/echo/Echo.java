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

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

/**
 * @author Nicolas GERAUD (nicolas at graviteesource.com)
 * @author GraviteeSource Team
 */
public class Echo {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        Route get = router.route().method(HttpMethod.GET).produces("application/json");
        get.handler(routingContext -> {
            JsonObject content = new JsonObject();

            //headers
            JsonObject headers = new JsonObject();
            int statusCode = routingContext.request().getParam("statusCode") == null
                    ? 200
                    : Integer.valueOf(routingContext.request().getParam("statusCode"));
            routingContext.request().headers().forEach(entry -> headers.put(entry.getKey(), entry.getValue()));
            content.put("headers", headers);

            //response
            routingContext.response()
                    .putHeader("content-type", "application/json")
                    .setStatusCode(statusCode)
                    .end(content.encodePrettily());
        });

        Route post = router.route().method(HttpMethod.POST);
        post.handler(routingContext -> {
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
        });

        int port = 8080;
        if (args.length > 0) {
            try{
                port = Integer.parseInt(args[0]);
            } catch (Exception e) {
                System.err.println("Usage: java -jar gravitee-echo-api-VERSION.jar <port>");
            }
        }
        server.requestHandler(router::accept).listen(port);
        System.out.println("Server listening on port " + port);
    }
}
