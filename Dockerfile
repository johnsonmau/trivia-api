# Use a lightweight base image with Java installed
FROM eclipse-temurin:17-jdk-alpine AS builder

# Set the working directory for the build
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Download dependencies without running the entire build
RUN ./mvnw dependency:go-offline

# Copy the source code
COPY src ./src

# Package the application
RUN ./mvnw package -DskipTests

# Production stage
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the packaged JAR file from the builder stage
COPY --from=builder /app/target/trivia-1.0.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
