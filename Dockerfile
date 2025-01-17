# Use an OpenJDK base image
FROM openjdk:17-jdk-slim

# Add the JAR file to the container
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app.jar"]