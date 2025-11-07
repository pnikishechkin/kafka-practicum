FROM maven:3.9-amazoncorretto-25-alpine AS builder

WORKDIR /app

COPY src src
COPY pom.xml .

RUN mvn clean package

FROM amazoncorretto:25-alpine-jdk

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring

COPY --from=builder --chown=spring:spring /app/target/*.jar app.jar

EXPOSE 8080

COPY target/kafka-project-4-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]