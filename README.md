# HOBSINN Codebase Scripts

One-command scripts to build and manage the complete HOBSINN microservices architecture.

## Architecture Overview

HOBSINN is a microservices-based platform for waste pickup management in Cameroon, featuring:

- **9 Services**: API Gateway, User, Scheduling, Campaign, Reporting, Payment, Notification, Provider, Analytics
- **Tech Stack**: Spring Boot 3.2.3, Kotlin 1.9.22, PostgreSQL + PostGIS, Kafka, Docker
- **Cost**: 0 XAF (local infrastructure only)
- **Features**: Special call scheduling, general slot booking, real-time matching, geospatial queries

## Scripts Overview

### `master.py` - Main Orchestrator
```bash
# Complete setup (generate + build + test)
python3 master.py setup

# Quick start existing codebase
python3 master.py start

# Run tests
python3 master.py test all

# Show help
python3 master.py help
```

### `generate_hobsinn.py` - Complete Codebase Generation
```bash
# Generate entire codebase from scratch
python3 generate_hobsinn.py

# Generate in specific directory
python3 generate_hobsinn.py /path/to/project
```

### `build_hobsinn.py` - Build & Fix Existing Codebase
```bash
# Fix build issues and compile
python3 build_hobsinn.py

# Build in specific directory
python3 build_hobsinn.py /path/to/project
```

### `incremental_builder.py` - Add Features Incrementally
```bash
# Add new service
python3 incremental_builder.py add-service payment-service 8082

# Add entity to service
python3 incremental_builder.py add-entity user-service UserProfile name:String email:String

# Add repository
python3 incremental_builder.py add-repository user-service UserProfileRepository UserProfile

# Add service class
python3 incremental_builder.py add-service-class scheduling-service MatchingEngine

# Add database migration
python3 incremental_builder.py add-migration user-service add_user_profiles "CREATE TABLE user_profiles (...);"
```

### `test_runner.py` - Validation & Testing
```bash
# Full test suite
python3 test_runner.py

# Test specific component
python3 test_runner.py build          # Test Gradle build
python3 test_runner.py infrastructure # Test PostgreSQL, Kafka, Redis
python3 test_runner.py services       # Test application services
python3 test_runner.py docker-compose # Test Docker containers
```

## Quick Start (Recommended)

1. **Generate & Setup**:
   ```bash
   python3 master.py setup
   ```

2. **Start Infrastructure**:
   ```bash
   docker-compose up -d
   ```

3. **Test Everything**:
   ```bash
   python3 master.py test all
   ```

4. **Access Services**:
   - API Gateway: http://localhost:8080
   - User Service: http://localhost:8086
   - Scheduling Service: http://localhost:8081
   - Grafana: http://localhost:3000 (admin/admin)
   - Kafka UI: http://localhost:8090

## Development Workflow

### Adding New Features
```bash
# Add a new service
python3 incremental_builder.py add-service analytics-service 8087

# Add entities, repositories, services as needed
python3 incremental_builder.py add-entity analytics-service PickupAnalytics date:LocalDate totalPickups:Int

# Build and test
python3 master.py build
python3 master.py test all
```

### Fixing Build Issues
```bash
# Run build fixer
python3 build_hobsinn.py

# Or use master script
python3 master.py build
```

### Testing Changes
```bash
# Test build only
python3 test_runner.py build

# Test infrastructure
python3 test_runner.py infrastructure

# Test all services
python3 test_runner.py services
```

## Project Structure Generated

```
hobsinn/
├── build.gradle                    # Root build config
├── settings.gradle                 # Project structure
├── docker-compose.yml             # Infrastructure
├── gradle.properties              # Build properties
├── gradlew & gradlew.bat          # Gradle wrappers
├── infrastructure/docker/         # Docker configs
├── services/                      # Microservices
│   ├── api-gateway/
│   ├── user-service/
│   ├── scheduling-service/
│   ├── campaign-service/
│   ├── reporting-service/
│   ├── payment-service/
│   ├── notification-service/
│   ├── provider-service/
│   └── analytics-service/
├── shared/                        # Common libraries
│   ├── common-domain/
│   └── common-events/
└── scripts/                       # Utility scripts
```

## Services Configuration

| Service | Port | Description |
|---------|------|-------------|
| api-gateway | 8080 | API routing & security |
| user-service | 8086 | User management & auth |
| scheduling-service | 8081 | Pickup scheduling & matching |
| campaign-service | 8083 | Marketing campaigns |
| reporting-service | 8084 | Analytics & reporting |
| payment-service | 8085 | Payment processing |
| notification-service | 8087 | Push notifications |
| provider-service | 8088 | Provider management |
| analytics-service | 8089 | Advanced analytics |

## Infrastructure

- **PostgreSQL 15 + PostGIS 3.4**: Geospatial database
- **Kafka 7.5.3**: Event streaming
- **Redis 7.2**: Caching & sessions
- **Prometheus**: Metrics collection
- **Grafana**: Monitoring dashboard
- **Kafka UI**: Message browsing

## Key Features Implemented

- ✅ **Transactional Outbox Pattern**: Reliable event publishing
- ✅ **Geospatial Queries**: Location-based pickup matching
- ✅ **Real-time Events**: Kafka-based service communication
- ✅ **Spring Security**: JWT authentication
- ✅ **Flyway Migrations**: Database versioning
- ✅ **Docker Compose**: Local development environment
- ✅ **CI/CD Ready**: GitHub Actions pipeline
- ✅ **Monitoring**: Prometheus + Grafana integration

## Cost Optimization (0 XAF)

- Local PostgreSQL instead of cloud DB
- Local Kafka instead of cloud messaging
- Local Redis instead of cloud cache
- Docker Compose for infrastructure
- No cloud services required

## Next Steps

1. Run the setup script and verify everything works
2. Add business logic to service classes
3. Implement REST API endpoints
4. Add comprehensive tests
5. Set up monitoring dashboards
6. Deploy to production environment

## Troubleshooting

### Build Issues
```bash
# Fix common build problems
python3 build_hobsinn.py

# Check build status
python3 test_runner.py build
```

### Infrastructure Issues
```bash
# Restart infrastructure
docker-compose down
docker-compose up -d

# Check infrastructure status
python3 test_runner.py infrastructure
```

### Service Issues
```bash
# Check service health
python3 test_runner.py services

# View service logs
docker-compose logs <service-name>
```

---

**Note**: These scripts follow the HOBSINN architecture document specifications and maintain the 0 XAF cost policy through local infrastructure usage.