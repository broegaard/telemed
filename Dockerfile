# The docker image is hosted in a private repository on Azure. Registry name: crtelemed.azurecr.io
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

# STAGE 1
# Using image from Henrik Bærbak as base for the builder image
FROM henrikbaerbak/jdk17-gradle74 AS builder

# Selecting the workdir for RUN commands
WORKDIR /telemed/

# Copy necessary files into the image
COPY telemed/ /telemed/telemed/
COPY broker/ /telemed/broker/
COPY settings.gradle /telemed/settings.gradle

# Build the jar file within the image
RUN gradle jar

# STAGE 2
# We use a multistage Dockerfile to avoid copying source files into the final image.
# For stage 2 we use a slim image as base so the final image will be smaller
FROM openjdk:17.0.1-jdk-slim

# Environment variable used to run the system with different database backends
ENV DATABASE_CONNECTIONSTRING memory

# Selecting the workdir for the final image
WORKDIR /telemed/

# Copy the jar file from the first image into the final image
COPY --from=builder /telemed/telemed/build/libs/telemed.jar /telemed/

# The command which will be executed on the server
CMD java -jar /telemed/telemed.jar $DATABASE_CONNECTIONSTRING false false
