FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

COPY pom.xml .
COPY src ./src
RUN --mount=type=cache,target=/root/.m2  mvn clean package -Dmaven.test.skip

FROM gcr.io/distroless/java21
ENV TZ="Europe/Oslo"
ENTRYPOINT ["java", "-jar", "/app.jar"]

COPY --from=build target/arbeidsplassen-metrics-api-*.jar /app.jar
