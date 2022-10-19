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

# First stage to share environment variable
FROM graviteeio/java:17 as base
ENV GRAVITEEIO_HOME /opt/graviteeio-sample-apis

# Second stage to add the folder and change the permissions and ownership
FROM base as builder
ADD ./gravitee-sample-api/target/gravitee-sample-api-1.0.0-SNAPSHOT.jar ${GRAVITEEIO_HOME}/gravitee-sample-api.jar
ADD ./bin ${GRAVITEEIO_HOME}/bin
RUN chgrp -R 0 ${GRAVITEEIO_HOME} && \
    chmod -R g=u ${GRAVITEEIO_HOME}

# Third stage to build the final docker image. COPY preserves ownership & permissions
FROM base
LABEL maintainer="contact@graviteesource.com"
COPY --from=builder ${GRAVITEEIO_HOME} ${GRAVITEEIO_HOME}
WORKDIR ${GRAVITEEIO_HOME}
EXPOSE 8080
ENTRYPOINT ["./bin/sample-apis"]
VOLUME ${GRAVITEEIO_HOME}/logs
