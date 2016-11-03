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

FROM graviteeio/java:8
MAINTAINER Gravitee Team <http://gravitee.io>

COPY target/gravitee-whoami-api-*.jar /opt

WORKDIR /opt
RUN ln -sf gravitee-*.jar gravitee-api.jar
EXPOSE 8080
CMD ["java", "-jar", "gravitee-api.jar"]
