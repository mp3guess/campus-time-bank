FROM openjdk:17-jdk-slim as builder

WORKDIR /app

# Copy gradle files first (for better caching)
COPY gradle/ gradle/
COPY gradlew build.gradle settings.gradle ./

# Download dependencies
RUN chmod +x ./gradlew && \
    ./gradlew dependencies --no-daemon

# Copy source code
COPY src/ src/

# Build application
RUN ./gradlew build -x test --no-daemon

# Runtime stage
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy jar from builder
COPY --from=builder /app/build/libs/campus-time-bank-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD java -cp app.jar org.springframework.boot.loader.JarLauncher ping || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
