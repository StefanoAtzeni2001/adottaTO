# Stage 1: Build con Maven e Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the shared-dtos module into the build context
COPY shared-dtos /app/shared-dtos

# Copy the adoption-post-service module's pom.xml and source code
COPY adoption-post-service/pom.xml /app/adoption-post-service/pom.xml
COPY adoption-post-service/src /app/adoption-post-service/src

# Install the shared-dtos module first,
RUN mvn -f /app/shared-dtos/pom.xml clean install -DskipTests

# Build the adoption-post-service module, now that shared-dtos dependency is installed
RUN mvn -f /app/adoption-post-service/pom.xml clean package -DskipTests


# Stage 2: Runtime con JRE Java 21
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the built jar from the build stage to the runtime image
COPY --from=build /app/adoption-post-service/target/*.jar app.jar

EXPOSE 8081

# Run the application
CMD ["java", "-jar", "app.jar"]
