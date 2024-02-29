FROM maven:3.8.3-openjdk-17 AS build
LABEL authors="David Blbulyan"
LABEL description="JWT spring boot starter"
COPY pom.xml .
COPY src src
RUN --mount=type=cache,target=/root/.m2 mvn install -DskipTests