FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/EmployeeService-*.jar employee-service.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "-Dspring.config.name=bootstrap", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}","employee-service.jar"]
