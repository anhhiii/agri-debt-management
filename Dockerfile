## Build + run Spring Boot (Java 21) for this project
# syntax=docker/dockerfile:1

FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .

## Maven wrapper
COPY mvnw ./
COPY .mvn .mvn

## App source
COPY src ./src

RUN chmod +x mvnw
RUN ./mvnw -B -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app

## Copy the built Spring Boot jar
COPY --from=builder /app/target/*.jar /app/app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]