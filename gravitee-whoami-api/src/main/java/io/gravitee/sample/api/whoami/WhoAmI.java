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
package io.gravitee.sample.api.whoami;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;

/**
 * @author Nicolas GERAUD (nicolas at graviteesource.com)
 * @author GraviteeSource Team
 */
public class WhoAmI {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        Route get = router.route().method(HttpMethod.GET).produces("application/json");
        get.handler(routingContext -> {
            JsonObject content = new JsonObject();

            int statusCode = routingContext.request().getParam("statusCode") == null
                    ? 200
                    : Integer.valueOf(routingContext.request().getParam("statusCode"));

            int latency = routingContext.request().getParam("latency") == null ||
                          Integer.valueOf(routingContext.request().getParam("latency")) < 1
                    ? 1
                    : Integer.valueOf(routingContext.request().getParam("latency"));

            //os
            OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
            JsonObject os = new JsonObject();
            os.put("arch", operatingSystemMXBean.getArch());
            os.put("name", operatingSystemMXBean.getName());
            os.put("version", operatingSystemMXBean.getVersion());
            os.put("availableProcessors", operatingSystemMXBean.getAvailableProcessors());
            os.put("systemLoadAverage", operatingSystemMXBean.getSystemLoadAverage());
            content.put("os", os);

            //network
            try {
                JsonArray network = new JsonArray();
                Collections.list(NetworkInterface.getNetworkInterfaces()).forEach(
                        networkInterface ->  {
                            JsonObject networkInterfaceAsJson = new JsonObject()
                                    .put("name", networkInterface.getName())
                                    .put("displayName", networkInterface.getDisplayName());
                            JsonArray inetAddresses = new JsonArray();
                            Collections.list(networkInterface.getInetAddresses()).forEach(
                                    inetAddress -> inetAddresses.add(inetAddress.toString())
                            );
                            networkInterfaceAsJson.put("InetAddress", inetAddresses);
                            network.add(networkInterfaceAsJson);
                        }
                );
                content.put("network", network);
            } catch (SocketException e) {
                e.printStackTrace();
            }

            //system properties
            JsonObject systemProperties = new JsonObject();
            System.getProperties().entrySet().forEach(property -> systemProperties.put(property.getKey().toString(), property.getValue()));
            content.put("systemProperties", systemProperties);

            //system env
            JsonObject systemEnv = new JsonObject();
            System.getenv().entrySet().forEach(env -> systemEnv.put(env.getKey(), env.getValue()));
            content.put("systemEnv", systemEnv);

            //response
            HttpServerResponse response = routingContext.response();
            vertx.setTimer(latency, id -> response.putHeader("content-type", "application/json")
                    .setStatusCode(statusCode)
                    .end(content.encodePrettily()));
        });

        server.requestHandler(router::accept).listen(8080);
    }
}
