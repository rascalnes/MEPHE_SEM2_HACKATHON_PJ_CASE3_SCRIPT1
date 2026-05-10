# Stage 1: Build
FROM gradle:8.14-jdk24-alpine AS builder

WORKDIR /app

# Copy build configuration
COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src

# Build the application
RUN gradle clean build --no-daemon

# Stage 2: Runtime
FROM openjdk:24-slim-bullseye

WORKDIR /app

# Install curl for healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy JAR from builder
COPY --from=builder /app/build/libs/lottery-backend-1.0.0.jar ./app.jar

# Create non-root user
RUN groupadd -r lottery && useradd -r -g lottery lottery
USER lottery

# Expose application port
EXPOSE 8080

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]