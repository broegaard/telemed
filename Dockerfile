FROM henrikbaerbak/jdk17-gradle74

ENV LANG C.UTF-8
ENV LC_ALL C.UTF-8

WORKDIR /telemed/

COPY telemed/ /telemed/telemed/
COPY broker/ /telemed/broker/
COPY settings.gradle /telemed/settings.gradle
# COPY build.gradle /telemed/
# COPY gradle /telemed/gradle/

# COPY . /telemed/

# RUN ls /telemed/

CMD gradle serverHttp

