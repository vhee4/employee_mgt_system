FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/ApiGateway-*.jar api-gateway.jar

EXPOSE 8082

ENTRYPOINT ["java", "-Dspring.config.name=bootstrap", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "api-gateway.jar"]
