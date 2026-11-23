# Build stage using standard Temurin JDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml, settings.xml and source code
COPY settings.xml .
COPY pom.xml .
COPY src ./src

# Build package using Google Maven Central Mirror to bypass 429 rate limit
RUN mvn clean package -s settings.xml -DskipTests

# Run stage with lightweight JRE 21
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-Dserver.port=${PORT}", "-jar", "app.jar"]
