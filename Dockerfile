# The docker image is hosted in a private repositoty on Azure. Registry name: crtelemed.azurecr.io
# Usage: docker build -t crtelemed.azurecr.io/telemed:(datetime:20230504-1615) -t crtelemed.azurecr.io/telemed:latest -f Dockerfile .

# Push docker image to registry
# docker push crtelemed.azurecr.io/telemed:(datetime:20230504-1615)
# docker push crtelemed.azurecr.io/telemed:latest

# Test proper install
# docker run --rm -it -p 4567:4567 telemed:latest bash
#    and then 'java -jar /telemed/telemed.jar memory false false' in the running container.
# Default startup command is running telemed with inmemory database configuration.
#
# To execute the docker container run the following command:
# - docker run --rm -d -p 4567:4567 --name telemed-server telemed:latest
# - docker run --rm -e DATABASESERVER='memory' -p 4567:4567 crtelemed.azurecr.io/telemed:latest

FROM henrikbaerbak/jdk17-gradle74 AS builder

ENV LANG C.UTF-8
ENV LC_ALL C.UTF-8

WORKDIR /telemed/

COPY telemed/ /telemed/telemed/
COPY broker/ /telemed/broker/
COPY settings.gradle /telemed/settings.gradle
RUN gradle jar

# FROM henrikbaerbak/jdk17-gradle74
FROM openjdk:17.0.1-jdk-slim

ENV LANG C.UTF-8
ENV LC_ALL C.UTF-8
ENV DATABASE_CONNECTIONSTRING memory

WORKDIR /telemed/

COPY --from=builder /telemed/telemed/build/libs/telemed.jar /telemed/

CMD java -jar /telemed/telemed.jar $DATABASE_CONNECTIONSTRING false false

# CMD gradle serverHttp

