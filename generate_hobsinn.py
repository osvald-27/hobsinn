#!/usr/bin/env python3
"""
HOBSINN Codebase Generator - Complete Implementation Script
Follows the architecture document for 0 XAF cost implementation
"""

import os
import shutil
from pathlib import Path
from typing import Dict, List, Any
import json

class HobsinnCodebaseGenerator:
    def __init__(self, base_path: str = "/home/user/hobsinn"):
        self.base_path = Path(base_path)
        self.services = [
            "api-gateway", "user-service", "scheduling-service",
            "campaign-service", "reporting-service", "payment-service",
            "notification-service", "provider-service", "analytics-service"
        ]

    def create_directory_structure(self):
        """Create the complete directory structure"""
        print("📁 Creating directory structure...")

        # Root structure
        dirs = [
            "infrastructure/docker",
            "shared/common-domain/src/main/kotlin/com/hobsinnovations/hobsinn",
            "shared/common-events/src/main/kotlin/com/hobsinnovations/hobsinn",
            "mobile/hobsinn-android",
            "dashboard/hobsinn-admin",
            "data-pipeline/hotspot-detector",
            "data-pipeline/matching-engine",
            "data-pipeline/analytics-aggregator",
            "scripts"
        ]

        for service in self.services:
            dirs.extend([
                f"services/{service}/src/main/kotlin/com/hobsinnovations/hobsinn/{service.replace('-', '')}",
                f"services/{service}/src/main/kotlin/com/hobsinnovations/hobsinn/{service.replace('-', '')}/api",
                f"services/{service}/src/main/kotlin/com/hobsinnovations/hobsinn/{service.replace('-', '')}/domain",
                f"services/{service}/src/main/kotlin/com/hobsinnovations/hobsinn/{service.replace('-', '')}/repository",
                f"services/{service}/src/main/kotlin/com/hobsinnovations/hobsinn/{service.replace('-', '')}/service",
                f"services/{service}/src/main/resources",
                f"services/{service}/src/main/resources/db/migration",
                f"services/{service}/src/test/kotlin/com/hobsinnovations/hobsinn/{service.replace('-', '')}",
                f"services/{service}/Dockerfile"
            ])

        for dir_path in dirs:
            (self.base_path / dir_path).mkdir(parents=True, exist_ok=True)

    def create_shared_libraries(self):
        """Create common-domain and common-events"""
        print("📚 Creating shared libraries...")

        # Common Domain
        domain_files = {
            "shared/common-domain/src/main/kotlin/com/hobsinnovations/hobsinn/domain.kt": self._get_common_domain(),
            "shared/common-domain/build.gradle": self._get_shared_build_gradle("common-domain"),
        }

        # Common Events
        events_files = {
            "shared/common-events/src/main/kotlin/com/hobsinnovations/hobsinn/events.kt": self._get_common_events(),
            "shared/common-events/build.gradle": self._get_shared_build_gradle("common-events"),
        }

        for file_path, content in {**domain_files, **events_files}.items():
            self._write_file(file_path, content)

    def create_service_skeleton(self, service_name: str):
        """Create a complete service skeleton"""
        print(f"🏗️  Creating {service_name}...")

        service_config = self._get_service_config(service_name)
        base_package = f"com.hobsinnovations.hobsinn.{service_name.replace('-', '')}"

        files = {
            f"services/{service_name}/build.gradle": self._get_service_build_gradle(service_name),
            f"services/{service_name}/Dockerfile": self._get_dockerfile(service_name),
            f"services/{service_name}/src/main/resources/application.yml": self._get_application_yml(service_name),
            f"services/{service_name}/src/main/kotlin/{base_package.replace('.', '/')}/Application.kt": self._get_application_class(service_name, base_package),
        }

        # Add service-specific files
        if service_config.get("has_controller"):
            files[f"services/{service_name}/src/main/kotlin/{base_package.replace('.', '/')}/api/Controller.kt"] = self._get_controller_class(service_name, base_package)

        if service_config.get("has_entities"):
            for entity in service_config.get("entities", []):
                files[f"services/{service_name}/src/main/kotlin/{base_package.replace('.', '/')}/domain/{entity}.kt"] = self._get_entity_class(entity, base_package)

        if service_config.get("has_repositories"):
            for repo in service_config.get("repositories", []):
                files[f"services/{service_name}/src/main/kotlin/{base_package.replace('.', '/')}/repository/{repo}.kt"] = self._get_repository_interface(repo, base_package)

        if service_config.get("has_services"):
            for service in service_config.get("services", []):
                files[f"services/{service_name}/src/main/kotlin/{base_package.replace('.', '/')}/service/{service}.kt"] = self._get_service_class(service, base_package)

        # Database migrations
        if service_config.get("migrations"):
            for i, migration in enumerate(service_config["migrations"]):
                files[f"services/{service_name}/src/main/resources/db/migration/V{i+1}__{migration['name']}.sql"] = migration["sql"]

        for file_path, content in files.items():
            self._write_file(file_path, content)

    def create_infrastructure(self):
        """Create Docker Compose and infrastructure files"""
        print("🐳 Creating infrastructure...")

        files = {
            "docker-compose.yml": self._get_docker_compose(),
            "infrastructure/docker/docker-compose.yml": self._get_docker_compose(),
            "infrastructure/docker/prometheus.yml": self._get_prometheus_config(),
            ".github/workflows/ci.yml": self._get_github_actions_ci(),
            "scripts/setup-local.sh": self._get_setup_script(),
        }

        for file_path, content in files.items():
            self._write_file(file_path, content)

    def create_root_build_files(self):
        """Create root Gradle build files"""
        print("⚙️  Creating root build configuration...")

        files = {
            "build.gradle": self._get_root_build_gradle(),
            "settings.gradle": self._get_settings_gradle(),
            "gradle.properties": self._get_gradle_properties(),
            "gradlew": self._get_gradle_wrapper_script(),
            "gradlew.bat": self._get_gradle_wrapper_bat(),
            "gradle/wrapper/gradle-wrapper.properties": self._get_gradle_wrapper_properties(),
            "gradle/wrapper/gradle-wrapper.jar": "",  # This would need to be downloaded
        }

        for file_path, content in files.items():
            if content:  # Skip empty jar file
                self._write_file(file_path, content)

    def _get_service_config(self, service_name: str) -> Dict[str, Any]:
        """Get configuration for each service"""
        configs = {
            "api-gateway": {
                "port": 8080,
                "has_controller": True,
                "has_entities": False,
                "dependencies": ["common-domain", "common-events", "spring-boot-starter-web", "spring-boot-starter-security"],
            },
            "user-service": {
                "port": 8086,
                "has_controller": True,
                "has_entities": True,
                "entities": ["User", "UserRole"],
                "repositories": ["UserRepository"],
                "services": ["UserService", "JwtTokenProvider"],
                "migrations": [{"name": "initial_user_schema", "sql": self._get_user_schema()}],
                "dependencies": ["common-domain", "common-events", "spring-boot-starter-web", "spring-boot-starter-data-jpa", "postgresql", "spring-boot-starter-security", "jjwt"],
            },
            "scheduling-service": {
                "port": 8081,
                "has_controller": True,
                "has_entities": True,
                "entities": ["PickupRequest", "PickupStatus"],
                "repositories": ["PickupRequestRepository"],
                "services": ["SchedulingService", "MatchingEngine"],
                "migrations": [{"name": "initial_scheduling_schema", "sql": self._get_scheduling_schema()}],
                "dependencies": ["common-domain", "common-events", "spring-boot-starter-web", "spring-boot-starter-data-jpa", "postgresql", "hibernate-spatial", "spring-kafka", "flyway-core"],
            },
            # Add other services...
        }
        return configs.get(service_name, {})

    def _get_common_domain(self) -> str:
        return '''package com.hobsinnovations.hobsinn

// Common domain types and enums used across services

enum class UserRole {
    HOUSEHOLD, PICKUP, AMBASSADOR, SYSTEM_ADMINISTRATOR
}

enum class PickupStatus {
    PENDING, INVALID, MATCHED, CONFIRMED,
    IN_PROGRESS, COMPLETED, CANCELLED, REASSIGNED
}

enum class RequestType {
    SPECIAL_CALL, GENERAL
}

// Common base entity
abstract class BaseEntity {
    val createdAt: java.time.Instant = java.time.Instant.now()
    var updatedAt: java.time.Instant = java.time.Instant.now()
}

// Common response types
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: String? = null
)

data class PageResponse<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int,
    val size: Int
)'''

    def _get_common_events(self) -> str:
        return '''package com.hobsinnovations.hobsinn

import java.util.UUID

// Base event class
abstract class HobsinnEvent(
    val eventId: UUID = UUID.randomUUID(),
    val occurredAt: java.time.Instant = java.time.Instant.now(),
    val correlationId: UUID = UUID.randomUUID()
)

// Pickup events
data class SpecialCallCreatedEvent(
    val requestId: UUID,
    val requestingUserId: UUID,
    val locationLat: java.math.BigDecimal,
    val locationLng: java.math.BigDecimal,
    val requestedTime: java.time.Instant
) : HobsinnEvent()

data class PickupMatchedEvent(
    val requestId: UUID,
    val pickupUserId: UUID
) : HobsinnEvent()

// Add other event types...
'''

    def _get_root_build_gradle(self) -> str:
        return '''plugins {
    id 'org.jetbrains.kotlin.jvm' version '1.9.22' apply false
    id 'org.jetbrains.kotlin.plugin.spring' version '1.9.22' apply false
    id 'org.jetbrains.kotlin.plugin.jpa' version '1.9.22' apply false
    id 'org.springframework.boot' version '3.2.3' apply false
    id 'io.spring.dependency-management' version '1.1.4' apply false
}

allprojects {
    apply plugin: 'java'

    group = 'com.hobsinnovations.hobsinn'
    version = '1.0.0-SNAPSHOT'

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    repositories {
        mavenCentral()
        maven { url 'https://repo.spring.io/milestone' }
    }
}

subprojects {
    apply plugin: 'org.jetbrains.kotlin.jvm'

    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile) {
        kotlinOptions {
            freeCompilerArgs = ['-Xjsr305=strict']
            jvmTarget = '21'
        }
    }

    tasks.withType(Test) {
        useJUnitPlatform()
        jvmArgs '-XX:+EnableDynamicAgentLoading'
    }
}'''

    def _get_settings_gradle(self) -> str:
        services = "',\\n    '".join(self.services)
        return f"""rootProject.name = 'hobsinn'

include(
    'shared:common-domain',
    'shared:common-events',
    '{services}'
)"""

    def _get_gradle_properties(self) -> str:
        return '''org.gradle.jvmargs=-Xmx2g -Dfile.encoding=UTF-8 --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED
org.gradle.parallel=true
org.gradle.caching=true
kotlin.code.style=official
org.gradle.java.installations.auto-download=true'''

    def _get_shared_build_gradle(self, name: str) -> str:
        return f'''plugins {{
    id 'org.jetbrains.kotlin.jvm'
    id 'org.jetbrains.kotlin.plugin.spring'
}}

dependencies {{
    implementation 'org.springframework:spring-context'
    implementation 'com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1'
}}'''

    def _get_service_build_gradle(self, service_name: str) -> str:
        config = self._get_service_config(service_name)
        deps = config.get("dependencies", [])

        dep_lines = []
        for dep in deps:
            if dep.startswith("spring-boot-starter"):
                dep_lines.append(f"    implementation 'org.springframework.boot:{dep}'")
            elif dep == "postgresql":
                dep_lines.append("    implementation 'org.postgresql:postgresql'")
            elif dep == "hibernate-spatial":
                dep_lines.append("    implementation 'org.hibernate.orm:hibernate-spatial:6.4.4.Final'")
            elif dep == "spring-kafka":
                dep_lines.append("    implementation 'org.springframework.kafka:spring-kafka'")
            elif dep == "flyway-core":
                dep_lines.append("    implementation 'org.flywaydb:flyway-core'")
            elif dep == "jjwt":
                dep_lines.append("    implementation 'io.jsonwebtoken:jjwt-api:0.11.5'")
                dep_lines.append("    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.11.5'")
                dep_lines.append("    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.11.5'")
            elif dep.startswith("common-"):
                dep_lines.append(f"    implementation project(':shared:{dep}')")
            else:
                dep_lines.append(f"    implementation '{dep}'")

        return f'''plugins {{
    id 'org.jetbrains.kotlin.jvm'
    id 'org.jetbrains.kotlin.plugin.spring'
    id 'org.jetbrains.kotlin.plugin.jpa'
    id 'org.springframework.boot'
    id 'io.spring.dependency-management'
}}

dependencies {{
{"\\n".join(dep_lines)}
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'io.micrometer:micrometer-registry-prometheus'

    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}}'''

    def _get_application_yml(self, service_name: str) -> str:
        config = self._get_service_config(service_name)
        port = config.get("port", 8080)

        return f'''server:
  port: {port}

spring:
  application:
    name: {service_name}
  profiles:
    active: local

  datasource:
    url: jdbc:postgresql://localhost:5432/hobsinn
    username: hobsinn
    password: hobsinn
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.spatial.dialect.postgis.PostgisPG95Dialect

  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: {service_name}
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer

  flyway:
    enabled: true
    locations: classpath:db/migration

logging:
  level:
    com.hobsinnovations.hobsinn: INFO
    org.springframework.security: DEBUG

management:
  endpoints:
    web:
      exposure:
        include: health,prometheus
  endpoint:
    health:
      show-details: when-authorized'''

    def _get_dockerfile(self, service_name: str) -> str:
        return f'''FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY shared shared
COPY services/{service_name} services/{service_name}

RUN ./gradlew :services:{service_name}:build --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=0 /app/services/{service_name}/build/libs/*.jar app.jar

EXPOSE {self._get_service_config(service_name).get("port", 8080)}

ENTRYPOINT ["java", "-jar", "app.jar"]'''

    def _get_docker_compose(self) -> str:
        return '''version: '3.8'

services:
  postgres:
    image: postgis/postgis:15-3.4
    environment:
      POSTGRES_DB: hobsinn
      POSTGRES_USER: hobsinn
      POSTGRES_PASSWORD: hobsinn
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./services/scheduling-service/src/main/resources/db/migration:/docker-entrypoint-initdb.d
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U hobsinn"]
      interval: 10s
      timeout: 5s
      retries: 5

  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.3
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"

  kafka:
    image: confluentinc/cp-kafka:7.5.3
    depends_on:
      - zookeeper
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_INTERNAL:PLAINTEXT
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092,PLAINTEXT_INTERNAL://kafka:29092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
    ports:
      - "9092:9092"
    healthcheck:
      test: ["CMD-SHELL", "kafka-broker-api-versions --bootstrap-server localhost:9092"]
      interval: 10s
      timeout: 5s
      retries: 5

  kafka-ui:
    image: provectuslabs/kafka-ui:latest
    depends_on:
      - kafka
    environment:
      KAFKA_CLUSTERS_0_NAME: local
      KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS: kafka:29092
    ports:
      - "8090:8080"

  redis:
    image: redis:7.2-alpine
    ports:
      - "6379:6379"

  prometheus:
    image: prom/prometheus:v2.48.1
    volumes:
      - ./infrastructure/docker/prometheus.yml:/etc/prometheus/prometheus.yml
    ports:
      - "9090:9090"

  grafana:
    image: grafana/grafana:10.2.3
    environment:
      GF_SECURITY_ADMIN_PASSWORD: admin
    ports:
      - "3000:3000"

volumes:
  postgres_data:'''

    def _get_application_class(self, service_name: str, base_package: str) -> str:
        class_name = service_name.replace('-', '').capitalize() + 'Application'
        return f'''package {base_package}

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class {class_name}

fun main(args: Array<String>) {{
    runApplication<{class_name}>(*args)
}}'''

    def _get_controller_class(self, service_name: str, base_package: str) -> str:
        class_name = service_name.replace('-', '').capitalize() + 'Controller'
        return f'''package {base_package}.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/{service_name.replace('-', '')}")
class {class_name} {{

    @GetMapping("/health")
    fun health() = ResponseEntity.ok(mapOf("status" to "UP", "service" to "{service_name}"))
}}'''

    def _get_entity_class(self, entity_name: str, base_package: str) -> str:
        return f'''package {base_package}.domain

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "{entity_name.lower()}s")
class {entity_name}(
    @Id
    @Column(name = "{entity_name.lower()}_id")
    val id: UUID = UUID.randomUUID(),

    // Add entity fields here

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
)'''

    def _get_repository_interface(self, repo_name: str, base_package: str) -> str:
        entity_name = repo_name.replace('Repository', '')
        return f'''package {base_package}.repository

import {base_package}.domain.{entity_name}
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface {repo_name} : JpaRepository<{entity_name}, UUID> {{
    // Add custom query methods here
}}'''

    def _get_service_class(self, service_name: str, base_package: str) -> str:
        return f'''package {base_package}.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class {service_name} {{
    // Add service methods here
}}'''

    def _get_user_schema(self) -> str:
        return '''-- User service schema
CREATE TABLE users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    phone_number VARCHAR(20) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    role VARCHAR(30) NOT NULL CHECK (role IN ('HOUSEHOLD', 'PICKUP', 'AMBASSADOR', 'SYSTEM_ADMINISTRATOR')),
    preferred_language VARCHAR(5) NOT NULL DEFAULT 'en' CHECK (preferred_language IN ('en', 'fr')),
    badge_count INTEGER NOT NULL DEFAULT 0,
    rating_score DECIMAL(5,4) NOT NULL DEFAULT 1.0 CHECK (rating_score BETWEEN 0.0 AND 1.0),
    star_rating DECIMAL(3,2) NOT NULL DEFAULT 6.0 CHECK (star_rating BETWEEN 0.0 AND 6.0),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_phone ON users(phone_number);
CREATE INDEX idx_users_role ON users(role);'''

    def _get_scheduling_schema(self) -> str:
        return '''-- Scheduling service schema
CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE pickup_requests (
    request_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    requesting_user_id UUID NOT NULL,
    assigned_provider_id UUID,
    request_type VARCHAR(20) NOT NULL DEFAULT 'SPECIAL_CALL' CHECK (request_type IN ('SPECIAL_CALL', 'GENERAL')),
    location_lat DECIMAL(10,7) NOT NULL,
    location_lng DECIMAL(10,7) NOT NULL,
    location_point GEOGRAPHY(POINT,4326),
    bag_count INTEGER NOT NULL CHECK (bag_count > 0),
    estimated_volume_m3 DECIMAL(8,3),
    estimated_cost_xaf DECIMAL(12,2) NOT NULL,
    platform_fee_xaf DECIMAL(12,2) NOT NULL,
    total_cost_xaf DECIMAL(12,2) NOT NULL,
    requested_time TIMESTAMPTZ NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'INVALID', 'MATCHED', 'CONFIRMED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'REASSIGNED')),
    idempotency_key VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CHECK (requested_time > created_at)
);

CREATE INDEX idx_requests_user ON pickup_requests(requesting_user_id);
CREATE INDEX idx_requests_provider ON pickup_requests(assigned_provider_id) WHERE assigned_provider_id IS NOT NULL;
CREATE INDEX idx_requests_location ON pickup_requests USING GIST(location_point);
CREATE INDEX idx_requests_active ON pickup_requests(status, requested_time) WHERE status NOT IN ('COMPLETED', 'CANCELLED', 'INVALID');'''

    def _get_prometheus_config(self) -> str:
        return '''global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'hobsinn-services'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['api-gateway:8080', 'user-service:8086', 'scheduling-service:8081']'''

    def _get_github_actions_ci(self) -> str:
        return '''name: CI

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest

    services:
      postgres:
        image: postgis/postgis:15-3.4
        env:
          POSTGRES_DB: hobsinn
          POSTGRES_USER: hobsinn
          POSTGRES_PASSWORD: hobsinn
        ports:
          - 5432:5432
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

      kafka:
        image: confluentinc/cp-kafka:7.5.3
        env:
          KAFKA_BROKER_ID: 1
          KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
          KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
          KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
        ports:
          - 9092:9092

      zookeeper:
        image: confluentinc/cp-zookeeper:7.5.3
        env:
          ZOOKEEPER_CLIENT_PORT: 2181
        ports:
          - 2181:2181

    steps:
    - uses: actions/checkout@v4

    - name: Set up JDK 21
      uses: actions/setup-java@v4
      with:
        java-version: '21'
        distribution: 'temurin'

    - name: Cache Gradle packages
      uses: actions/cache@v3
      with:
        path: |
          ~/.gradle/caches
          ~/.gradle/wrapper
        key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
        restore-keys: |
          ${{ runner.os }}-gradle-

    - name: Build with Gradle
      run: ./gradlew build --no-daemon

    - name: Run tests
      run: ./gradlew test --no-daemon'''

    def _get_setup_script(self) -> str:
        return '''#!/bin/bash
# HOBSINN Local Development Setup Script

set -e

echo "🚀 Setting up HOBSINN local development environment..."

# Start infrastructure
echo "🐳 Starting Docker infrastructure..."
docker-compose up -d postgres kafka zookeeper redis

# Wait for services to be ready
echo "⏳ Waiting for PostgreSQL..."
until docker-compose exec -T postgres pg_isready -U hobsinn; do
  sleep 2
done

echo "⏳ Waiting for Kafka..."
until docker-compose exec -T kafka kafka-broker-api-versions --bootstrap-server localhost:9092; do
  sleep 2
done

echo "✅ Infrastructure is ready!"

# Build and start services
echo "🏗️  Building services..."
./gradlew build --no-daemon -x test

echo "🎉 Setup complete! Run 'docker-compose up' to start all services."'''

    def _get_gradle_wrapper_script(self) -> str:
        return '''#!/bin/sh

# Gradle wrapper script (simplified version)

JAVA_OPTS="$JAVA_OPTS --add-opens java.base/java.lang=ALL-UNNAMED"
exec java $JAVA_OPTS -jar "$(dirname "$0")/gradle/wrapper/gradle-wrapper.jar" "$@"'''

    def _get_gradle_wrapper_bat(self) -> str:
        return '''@echo off
set JAVA_OPTS=%JAVA_OPTS% --add-opens java.base/java.lang=ALL-UNNAMED
java %JAVA_OPTS% -jar "%~dp0gradle\wrapper\gradle-wrapper.jar" %*'''

    def _get_gradle_wrapper_properties(self) -> str:
        return '''distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.5-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists'''

    def _write_file(self, file_path: str, content: str):
        """Write content to file, creating directories as needed"""
        full_path = self.base_path / file_path
        full_path.parent.mkdir(parents=True, exist_ok=True)
        with open(full_path, 'w', encoding='utf-8') as f:
            f.write(content)

    def generate(self):
        """Generate the complete HOBSINN codebase"""
        print("🎯 Starting HOBSINN codebase generation...")
        print("📋 Following architecture document specifications")
        print("💰 Maintaining 0 XAF cost policy (local infrastructure only)")

        self.create_directory_structure()
        self.create_root_build_files()
        self.create_shared_libraries()

        for service in self.services:
            self.create_service_skeleton(service)

        self.create_infrastructure()

        print("✅ Codebase generation complete!")
        print("📝 Next steps:")
        print("1. Run: chmod +x scripts/setup-local.sh")
        print("2. Run: ./scripts/setup-local.sh")
        print("3. Run: docker-compose up")
        print("4. Test: curl http://localhost:8080/v1/health")


if __name__ == "__main__":
    import sys
    base_path = sys.argv[1] if len(sys.argv) > 1 else "/home/user/hobsinn"

    generator = HobsinnCodebaseGenerator(base_path)
    generator.generate()