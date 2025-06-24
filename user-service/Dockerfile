# Stage 1: Build con Maven e Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the shared-dtos module into the build context
COPY shared-dtos /app/shared-dtos

# Copy the adoption-post-service module's pom.xml and source code
COPY user-service/pom.xml /app/user-service/pom.xml
COPY user-service/src /app/user-service/src

# Install the shared-dtos module first,
RUN mvn -f /app/shared-dtos/pom.xml clean install -DskipTests

# Build the adoption-post-service module, now that shared-dtos dependency is installed
RUN mvn -f /app/user-service/pom.xml clean package -DskipTests


# Stage 2: Runtime con JRE Java 21
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/user-service/target/*.jar app.jar
#porta utilizzata all'interno del container
EXPOSE 8083
CMD ["java", "-jar", "app.jar"]
