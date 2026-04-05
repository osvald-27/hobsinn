#!/usr/bin/env python3
"""
HOBSINN Incremental Builder - Add services/features incrementally
"""

import os
import sys
from pathlib import Path
from typing import Dict, List, Any

class HobsinnIncrementalBuilder:
    def __init__(self, base_path: str = "/home/user/hobsinn"):
        self.base_path = Path(base_path)

    def add_service(self, service_name: str, port: int = None):
        """Add a new service to the existing codebase"""
        print(f"➕ Adding {service_name} service...")

        # Update settings.gradle
        settings_file = self.base_path / "settings.gradle"
        if settings_file.exists():
            content = settings_file.read_text()
            if f"'services:{service_name}'" not in content:
                # Find the include block and add the new service
                include_start = content.find("include(")
                if include_start != -1:
                    include_end = content.find(")", include_start)
                    if include_end != -1:
                        content = content[:include_end] + f",\n    'services:{service_name}'" + content[include_end:]
                        settings_file.write_text(content)
                        print(f"✅ Added {service_name} to settings.gradle")

        # Create service directory structure
        service_dir = self.base_path / f"services/{service_name}"
        service_dir.mkdir(parents=True, exist_ok=True)

        # Create basic service files
        base_package = f"com.hobsinnovations.hobsinn.{service_name.replace('-', '')}"

        files = {
            f"services/{service_name}/build.gradle": self._get_service_build_gradle(service_name),
            f"services/{service_name}/Dockerfile": self._get_dockerfile(service_name),
            f"services/{service_name}/src/main/resources/application.yml": self._get_application_yml(service_name, port or 8080),
            f"services/{service_name}/src/main/kotlin/{base_package.replace('.', '/')}/Application.kt": self._get_application_class(service_name, base_package),
            f"services/{service_name}/src/main/kotlin/{base_package.replace('.', '/')}/api/Controller.kt": self._get_controller_class(service_name, base_package),
        }

        for file_path, content in files.items():
            full_path = self.base_path / file_path
            full_path.parent.mkdir(parents=True, exist_ok=True)
            full_path.write_text(content)

        print(f"✅ Created {service_name} service skeleton")

    def add_entity(self, service_name: str, entity_name: str, fields: List[Dict[str, str]] = None):
        """Add an entity to a service"""
        print(f"📝 Adding {entity_name} entity to {service_name}...")

        base_package = f"com.hobsinnovations.hobsinn.{service_name.replace('-', '')}"
        entity_dir = self.base_path / f"services/{service_name}/src/main/kotlin/{base_package.replace('.', '/')}/domain"
        entity_dir.mkdir(parents=True, exist_ok=True)

        entity_file = entity_dir / f"{entity_name}.kt"
        entity_file.write_text(self._get_entity_class(entity_name, base_package, fields))

        print(f"✅ Created {entity_name} entity")

    def add_repository(self, service_name: str, repo_name: str, entity_name: str):
        """Add a repository to a service"""
        print(f"🗃️  Adding {repo_name} repository to {service_name}...")

        base_package = f"com.hobsinnovations.hobsinn.{service_name.replace('-', '')}"
        repo_dir = self.base_path / f"services/{service_name}/src/main/kotlin/{base_package.replace('.', '/')}/repository"
        repo_dir.mkdir(parents=True, exist_ok=True)

        repo_file = repo_dir / f"{repo_name}.kt"
        repo_file.write_text(self._get_repository_interface(repo_name, entity_name, base_package))

        print(f"✅ Created {repo_name} repository")

    def add_service_class(self, service_name: str, service_class_name: str):
        """Add a service class to a service"""
        print(f"⚙️  Adding {service_class_name} service to {service_name}...")

        base_package = f"com.hobsinnovations.hobsinn.{service_name.replace('-', '')}"
        service_dir = self.base_path / f"services/{service_name}/src/main/kotlin/{base_package.replace('.', '/')}/service"
        service_dir.mkdir(parents=True, exist_ok=True)

        service_file = service_dir / f"{service_class_name}.kt"
        service_file.write_text(self._get_service_class(service_class_name, base_package))

        print(f"✅ Created {service_class_name} service")

    def add_migration(self, service_name: str, migration_name: str, sql: str):
        """Add a database migration"""
        print(f"🗄️  Adding {migration_name} migration to {service_name}...")

        migration_dir = self.base_path / f"services/{service_name}/src/main/resources/db/migration"
        migration_dir.mkdir(parents=True, exist_ok=True)

        # Find next migration number
        existing = list(migration_dir.glob("V*.sql"))
        next_num = len(existing) + 1

        migration_file = migration_dir / f"V{next_num}__{migration_name}.sql"
        migration_file.write_text(sql)

        print(f"✅ Created migration V{next_num}__{migration_name}.sql")

    def _get_service_build_gradle(self, service_name: str) -> str:
        return f'''plugins {{
    id 'org.jetbrains.kotlin.jvm'
    id 'org.jetbrains.kotlin.plugin.spring'
    id 'org.jetbrains.kotlin.plugin.jpa'
    id 'org.springframework.boot'
    id 'io.spring.dependency-management'
}}

dependencies {{
    implementation project(':shared:common-domain')
    implementation project(':shared:common-events')
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'io.micrometer:micrometer-registry-prometheus'

    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}}'''

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

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]'''

    def _get_application_yml(self, service_name: str, port: int) -> str:
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

management:
  endpoints:
    web:
      exposure:
        include: health,prometheus
  endpoint:
    health:
      show-details: when-authorized'''

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

    def _get_entity_class(self, entity_name: str, base_package: str, fields: List[Dict[str, str]] = None) -> str:
        field_lines = []
        if fields:
            for field in fields:
                field_lines.append(f"    val {field['name']}: {field['type']},")

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

{"\\n".join(field_lines) if field_lines else "    // Add entity fields here"}

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
)'''

    def _get_repository_interface(self, repo_name: str, entity_name: str, base_package: str) -> str:
        return f'''package {base_package}.repository

import {base_package}.domain.{entity_name}
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface {repo_name} : JpaRepository<{entity_name}, UUID> {{
    // Add custom query methods here
}}'''

    def _get_service_class(self, service_class_name: str, base_package: str) -> str:
        return f'''package {base_package}.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class {service_class_name} {{
    // Add service methods here
}}'''


def main():
    if len(sys.argv) < 2:
        print("Usage: python incremental_builder.py <command> [args...]")
        print("Commands:")
        print("  add-service <name> [port]")
        print("  add-entity <service> <entity> [field1:type field2:type ...]")
        print("  add-repository <service> <repo> <entity>")
        print("  add-service-class <service> <class>")
        print("  add-migration <service> <name> <sql_file>")
        return

    builder = HobsinnIncrementalBuilder()

    command = sys.argv[1]

    if command == "add-service":
        service_name = sys.argv[2]
        port = int(sys.argv[3]) if len(sys.argv) > 3 else None
        builder.add_service(service_name, port)

    elif command == "add-entity":
        service_name = sys.argv[2]
        entity_name = sys.argv[3]
        fields = []
        if len(sys.argv) > 4:
            for field_spec in sys.argv[4:]:
                name, type_ = field_spec.split(":")
                fields.append({"name": name, "type": type_})
        builder.add_entity(service_name, entity_name, fields)

    elif command == "add-repository":
        service_name = sys.argv[2]
        repo_name = sys.argv[3]
        entity_name = sys.argv[4]
        builder.add_repository(service_name, repo_name, entity_name)

    elif command == "add-service-class":
        service_name = sys.argv[2]
        service_class_name = sys.argv[3]
        builder.add_service_class(service_name, service_class_name)

    elif command == "add-migration":
        service_name = sys.argv[2]
        migration_name = sys.argv[3]
        sql_file = sys.argv[4]
        with open(sql_file, 'r') as f:
            sql = f.read()
        builder.add_migration(service_name, migration_name, sql)

    else:
        print(f"Unknown command: {command}")


if __name__ == "__main__":
    main()