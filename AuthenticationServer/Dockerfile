FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/AuthenticationServer-*.jar auth-service.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "auth-service.jar"]
