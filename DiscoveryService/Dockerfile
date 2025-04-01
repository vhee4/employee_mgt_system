# Use OpenJDK 17 as the base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file into the container
COPY target/DiscoveryService-*.jar discovery-service.jar

# Expose the service port
EXPOSE 8761

# Command to run the application
ENTRYPOINT ["java", "-jar", "discovery-service.jar"]
