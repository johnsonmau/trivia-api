# Use a lightweight base image with Java installed
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the Spring Boot JAR file to the container
COPY target/trivia.jar app.jar

# Expose the port your application runs on
EXPOSE 8080

# Set the command to run your Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
