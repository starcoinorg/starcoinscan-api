FROM openjdk:8-jdk-alpine as build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN --mount=type=cache,target=/root/.m2 ./mvnw install -DskipTests

FROM openjdk:8-jdk-alpine
ARG ES_PASSWORD
ENV ES_PASSWORD=$ES_PASSWORD
RUN addgroup -S starcoin && adduser -S starcoin -G starcoin
VOLUME /tmp
USER starcoin
ARG DEPENDENCY=/workspace/app/target
COPY --from=build ${DEPENDENCY}/scan-1.0-SNAPSHOT.jar /app/lib/app.jar
ENTRYPOINT ["java","-noverify","-XX:TieredStopAtLevel=1","-jar","app/lib/app.jar","-Dspring.main.lazy-initialization=true","ES_PASSWORD=$ES_PASSWORD"]