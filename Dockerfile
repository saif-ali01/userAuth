# Use the official OpenJDK image as the base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the target directory to the container
COPY target/auth-0.0.1-SNAPSHOT.jar /app/auth-0.0.1-SNAPSHOT.jar

# Expose the port your application runs on (adjust if necessary)
EXPOSE 8080

# Command to run your JAR file
ENTRYPOINT ["java", "-jar", "/app/auth-0.0.1-SNAPSHOT.jar"]
