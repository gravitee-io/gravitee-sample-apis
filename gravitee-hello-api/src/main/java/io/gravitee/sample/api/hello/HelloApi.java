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

import io.gravitee.gateway.grpc.helloworld.GreeterGrpc;
import io.gravitee.gateway.grpc.manualflowcontrol.HelloReply;
import io.gravitee.gateway.grpc.manualflowcontrol.HelloRequest;
import io.gravitee.gateway.grpc.manualflowcontrol.StreamingGreeterGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class HelloApi {

    private static final int STREAM_MESSAGE_NUMBER = 3;
    private static final long STREAM_SLEEP_MILLIS = 10;

    public GreeterGrpc.GreeterImplBase simpleService() {
        return new GreeterGrpc.GreeterImplBase() {
            @Override
            public void sayHello(
                io.gravitee.gateway.grpc.helloworld.HelloRequest request,
                StreamObserver<io.gravitee.gateway.grpc.helloworld.HelloReply> responseObserver
            ) {
                responseObserver.onNext(
                    io.gravitee.gateway.grpc.helloworld.HelloReply.newBuilder().setMessage("Hello " + request.getName()).build()
                );
                responseObserver.onCompleted();
            }
        };
    }

    public StreamingGreeterGrpc.StreamingGreeterImplBase streamService() {
        return new StreamingGreeterGrpc.StreamingGreeterImplBase() {
            @Override
            public StreamObserver<HelloRequest> sayHelloStreaming(StreamObserver<HelloReply> responseObserver) {
                return new StreamObserver<HelloRequest>() {
                    @Override
                    public void onNext(HelloRequest helloRequest) {
                        for (int i = 0; i < STREAM_MESSAGE_NUMBER; i++) {
                            HelloReply helloReply = HelloReply
                                .newBuilder()
                                .setMessage("Hello " + helloRequest.getName() + " part " + i)
                                .build();
                            responseObserver.onNext(helloReply);

                            try {
                                Thread.sleep(STREAM_SLEEP_MILLIS);
                            } catch (InterruptedException e) {
                                responseObserver.onError(Status.ABORTED.asException());
                            }
                        }
                        responseObserver.onCompleted();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        System.err.println(throwable.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("completed");
                    }
                };
            }
        };
    }
}
