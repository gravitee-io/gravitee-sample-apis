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

import io.gravitee.sample.api.hello.HelloApi;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.grpc.VertxServer;
import io.vertx.grpc.VertxServerBuilder;

public class GrpcApi extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        int port = Integer.parseInt(System.getProperty("grpc.port", "50051"));
        HelloApi helloApi = new HelloApi();
        VertxServer rpcServer = VertxServerBuilder.forAddress(vertx, "localhost", port).addService(helloApi.service()).build();
        rpcServer.start(result -> {
            if (result.succeeded()) {
                System.out.println("Server listening on port " + port);
                startPromise.complete();
            } else {
                startPromise.fail(result.cause());
            }
        });
    }
}
