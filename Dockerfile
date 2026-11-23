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

# Optimized JVM flags for Render Free Tier (512MB RAM, 0.1 CPU):
# -Xms128m -Xmx320m: Limits heap memory to prevent OOM kills within 512MB RAM limit
# -XX:+UseSerialGC: Lowest memory footprint and lowest CPU overhead on single/low vCPU
# -XX:+TieredCompilation -XX:TieredStopAtLevel=1: Fast C1 compiler, reduces cold-start boot time by ~40%
ENTRYPOINT ["java", "-Xms128m", "-Xmx320m", "-XX:+UseSerialGC", "-XX:+TieredCompilation", "-XX:TieredStopAtLevel=1", "-Dserver.port=${PORT}", "-jar", "app.jar"]
