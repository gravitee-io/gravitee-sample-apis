#
# Copyright (C) 2015 The Gravitee team (http://gravitee.io)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#         http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
FROM eclipse-temurin:17.0.3_7-jre-alpine
ARG grpc_host
ARG grpc_port
ARG grpc_ssl
ENV grpc_host=$grpc_host
ENV grpc_port=$grpc_port
ENV grpc_ssl=$grpc_ssl
VOLUME /tmp
ADD target/gravitee-hello-api-1.0.0-SNAPSHOT-fat.jar gravitee-hello-api-1.0.0-SNAPSHOT-fat.jar
ENTRYPOINT java -jar /gravitee-hello-api-1.0.0-SNAPSHOT-fat.jar -Dgrpc.host=${grpc_host} -Dgrpc.port=${grpc_port} -Dgrpc.ssl=${grpc_ssl}
