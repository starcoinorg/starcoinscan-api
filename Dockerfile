FROM openjdk:8-jdk-alpine as build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN --mount=type=cache,target=/root/.m2 ./mvnw install -DskipTests
WORKDIR /workspace/app/target/dependency
RUN jar -xf ../*.jar

FROM openjdk:8-jdk-alpine
ARG ES_PASSWORD
ENV ES_PASSWORD=$ES_PASSWORD
RUN addgroup -S starcoin && adduser -S starcoin -G starcoin
VOLUME /tmp
USER starcoin
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-noverify","-XX:TieredStopAtLevel=1","-cp","app:app/lib/*","-Dspring.main.lazy-initialization=true","ES_PASSWORD=$ES_PASSWORD", "org.starcoin.scan.ScanApplication"]