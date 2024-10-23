# Use an official Gradle image to build the Spring Boot app
FROM gradle:8.10.2-jdk21 AS builder

# Set the working directory inside the container
WORKDIR /leistungsnachweis

# Copy all application code into the container's directory
COPY . .

# Run the Gradle build process and create a shadow JAR without using a daemon
RUN gradle clean shadowJar --no-daemon

# Use an official OpenJDK image to run the application
FROM openjdk:21-jdk-slim

# Define the working directory for the runtime container
WORKDIR /leistungsnachweis

# Copy the built JAR file from the build stage
COPY --from=builder /leistungsnachweis/build/libs/*.jar app.jar

# Execute the application by running the main class in the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
