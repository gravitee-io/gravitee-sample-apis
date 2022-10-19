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
package io.gravitee.sample.api;

import static io.vertx.core.http.HttpMethod.GET;
import static io.vertx.core.http.HttpMethod.POST;

import io.gravitee.sample.api.echo.EchoHandler;
import io.gravitee.sample.api.whattimeisit.WhatTimeIsItHandler;
import io.gravitee.sample.api.whoami.WhoAmIHandler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * @author Nicolas GERAUD (nicolas at graviteesource.com)
 * @author GraviteeSource Team
 */
public class SampleApi {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        final HttpServerOptions httpServerOptions = new HttpServerOptions();
        httpServerOptions.setCompressionSupported(true);
        HttpServer server = vertx.createHttpServer(httpServerOptions);
        Router router = Router.router(vertx);

        router.route().handler(StaticHandler.create());

        router.route("/echo").produces("application/json").handler(new EchoHandler());

        router.route("/whoami").method(GET).produces("application/json").handler(new WhoAmIHandler());

        router.route("/whattimeisit").method(GET).produces("application/json").handler(new WhatTimeIsItHandler());

        int port = 8080;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception e) {
                System.err.println("Usage: java -jar gravitee-sample-api-VERSION.jar <port>");
            }
        }
        server.requestHandler(router).listen(port);
        System.out.println("Server listening on port " + port);
    }
}
