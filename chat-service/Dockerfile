# Stage 1: Build con Maven e Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the shared-dtos module into the build context
COPY shared-dtos /app/shared-dtos

# Copy the chat-service module's pom.xml and source code
COPY chat-service/pom.xml /app/chat-service/pom.xml
COPY chat-service/src /app/chat-service/src

# Install the shared-dtos module first,
RUN mvn -f /app/shared-dtos/pom.xml clean install -DskipTests

# Build the chat-service module, now that shared-dtos dependency is installed
RUN mvn -f /app/chat-service/pom.xml clean package -DskipTests


# Stage 2: Runtime con JRE Java 21
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/chat-service/target/*.jar app.jar
#porta utilizzata all'interno del container
EXPOSE 8082
CMD ["java", "-jar", "app.jar"]
