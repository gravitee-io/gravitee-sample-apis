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
import io.vertx.core.*;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * @author Nicolas GERAUD (nicolas at graviteesource.com)
 * @author GraviteeSource Team
 */
public class SampleApi extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);

        router.route().handler(StaticHandler.create());
        router.route("/echo").method(GET).method(POST).produces("application/json").handler(new EchoHandler());

        router.route().path("/whoami").method(GET).produces("application/json").handler(new WhoAmIHandler());

        router.route().path("/whattimeisit").method(GET).produces("application/json").handler(new WhatTimeIsItHandler());

        int port = Integer.parseInt(System.getProperty("http.port", "8080"));

        server
            .requestHandler(router)
            .listen(
                port,
                result -> {
                    if (result.succeeded()) {
                        System.out.println("Server listening on port " + port);
                        startPromise.complete();
                    } else {
                        startPromise.fail(result.cause());
                    }
                }
            );
    }
}
