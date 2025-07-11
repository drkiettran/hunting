# Dockerfile for Spring Boot services (place in each service directory)
# Example: user-management-service/Dockerfile
FROM openjdk:17-jdk-slim

LABEL maintainer="Persistent Hunt Team"

VOLUME /tmp

# Add the application's jar to the container
COPY target/*.jar app.jar

# Add wait-for-it script for service dependencies
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

# Expose the port
EXPOSE 8081

# Run the jar file
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]