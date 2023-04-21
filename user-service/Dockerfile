FROM docker.io/maven:3.9.1 AS builder

ARG VERSION=0.0.1-SNAPSHOT

ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME

ADD . $HOME
RUN --mount=type=cache,target=/root/.m2 mvn -Drevision=$VERSION -Dmaven.test.skip -f $HOME/pom.xml clean package

FROM docker.io/openjdk:17-alpine@sha256:a996cdcc040704ec6badaf5fecf1e144c096e00231a29188596c784bcf858d05

ARG VERSION=0.0.1-SNAPSHOT
ARG SERVICE_NAME="user-service"

COPY --from=builder /usr/app/target/$SERVICE_NAME-$VERSION.jar /app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
