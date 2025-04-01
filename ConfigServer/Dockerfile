FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/ConfigServer-*.jar config-server.jar

EXPOSE 9000


ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "config-server.jar"]
