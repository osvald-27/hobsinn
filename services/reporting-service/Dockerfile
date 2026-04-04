# Stage 1: Build
FROM gradle:8.5-jdk21-alpine AS builder
WORKDIR /workspace
COPY shared/common-domain/  shared/common-domain/
COPY shared/common-events/  shared/common-events/
COPY settings.gradle.kts build.gradle.kts gradle.properties .
COPY services/reporting-service/ services/reporting-service/
RUN gradle :services:reporting-service:bootJar --no-daemon -x test

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine AS runtime
RUN addgroup -S hobsinn && adduser -S hobsinn -G hobsinn
USER hobsinn
WORKDIR /app
COPY --from=builder /workspace/services/reporting-service/build/libs/*.jar app.jar
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"
EXPOSE 8083
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -q --spider http://localhost:8083/actuator/health || exit 1
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
