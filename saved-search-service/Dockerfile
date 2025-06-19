# Stage 1: Build con Maven e Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the shared-dtos module into the build context
COPY shared-dtos /app/shared-dtos

# Copy the saved-search-service module's pom.xml and source code
COPY saved-search-service/pom.xml /app/saved-search-service/pom.xml
COPY saved-search-service/src /app/saved-search-service/src

# Install the shared-dtos module first,
RUN mvn -f /app/shared-dtos/pom.xml clean install -DskipTests

# Build the saved-search-service module, now that shared-dtos dependency is installed
RUN mvn -f /app/saved-search-service/pom.xml clean package -DskipTests


# Stage 2: Runtime con JRE Java 21
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the built jar from the build stage to the runtime image
COPY --from=build /app/saved-search-service/target/*.jar app.jar

EXPOSE 8084

# Run the application
CMD ["java", "-jar", "app.jar"]
