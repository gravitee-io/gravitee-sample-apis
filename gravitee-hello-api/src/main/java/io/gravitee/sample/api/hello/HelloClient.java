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
package io.gravitee.sample.api.hello;

import io.gravitee.gateway.grpc.manualflowcontrol.HelloReply;
import io.gravitee.gateway.grpc.manualflowcontrol.HelloRequest;
import io.gravitee.gateway.grpc.manualflowcontrol.StreamingGreeterGrpc;
import io.grpc.stub.StreamObserver;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.net.SocketAddress;
import io.vertx.grpc.client.GrpcClient;
import io.vertx.grpc.client.GrpcClientChannel;

public class HelloClient extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        int port = Integer.parseInt(System.getProperty("grpc.port", "50051"));
        String host = System.getProperty("grpc.host", "localhost");
        Boolean ssl = Boolean.parseBoolean(System.getProperty("grpc.ssl", "false"));
        // Create the channel
        HttpClientOptions httpClientOptions = new HttpClientOptions();
        httpClientOptions.setSsl(ssl);
        httpClientOptions.setTrustAll(ssl);
        httpClientOptions.setUseAlpn(ssl);

        GrpcClient client = GrpcClient.client(vertx, httpClientOptions);
        GrpcClientChannel channel = new GrpcClientChannel(client, SocketAddress.inetSocketAddress(port, host));
        StreamingGreeterGrpc.StreamingGreeterStub stub = StreamingGreeterGrpc.newStub(channel);

        // Call the remote service
        StreamObserver<HelloRequest> requestStreamObserver = stub.sayHelloStreaming(
            new StreamObserver<HelloReply>() {
                @Override
                public void onNext(HelloReply helloReply) {
                    System.out.println("Stream: " + helloReply.getMessage());
                }

                @Override
                public void onError(Throwable throwable) {
                    System.err.println(throwable.getMessage());
                }

                @Override
                public void onCompleted() {}
            }
        );

        System.out.println("Client call on " + host + ":" + port + "[ssl=" + ssl + "]");

        requestStreamObserver.onNext(HelloRequest.newBuilder().setName("Jean").build());
    }
}
