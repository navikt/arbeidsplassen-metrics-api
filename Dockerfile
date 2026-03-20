FROM maven:3.9.12-eclipse-temurin-25-alpine AS build

COPY pom.xml .
COPY src ./src
RUN --mount=type=cache,target=/root/.m2  mvn clean package -Dmaven.test.skip

FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-25
ENV TZ="Europe/Oslo"
ENTRYPOINT ["java", "-jar", "/app.jar"]

COPY --from=build target/arbeidsplassen-metrics-api-*.jar /app.jar
