# Use an official Java runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /auth

# Copy the compiled JAR file into the container
COPY target/auth-0.0.1-SNAPSHOT.jar app.jar

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]

# Expose the port your app runs on
EXPOSE 8080
