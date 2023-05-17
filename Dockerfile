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

WORKDIR /telemed/

COPY --from=builder /telemed/telemed/build/libs/telemed.jar /telemed/

CMD java -jar /telemed/telemed.jar redis false false

# CMD gradle serverHttp

