#!/usr/bin/env python3
"""
HOBSINN Test Runner - Test and validate services
"""

import subprocess
import sys
import time
import requests
from pathlib import Path

class HobsinnTester:
    def __init__(self, base_path: str = "/home/user/hobsinn"):
        self.base_path = Path(base_path)

    def test_infrastructure(self):
        """Test if infrastructure services are running"""
        print("🔍 Testing infrastructure...")

        tests = [
            ("PostgreSQL", "http://localhost:5432", self._test_postgres),
            ("Kafka", "localhost:9092", self._test_kafka),
            ("Redis", "localhost:6379", self._test_redis),
        ]

        results = {}
        for name, url, test_func in tests:
            try:
                result = test_func()
                results[name] = result
                status = "✅" if result else "❌"
                print(f"{status} {name}: {'UP' if result else 'DOWN'}")
            except Exception as e:
                results[name] = False
                print(f"❌ {name}: ERROR - {e}")

        return all(results.values())

    def test_services(self):
        """Test if application services are running"""
        print("🔍 Testing services...")

        services = [
            ("api-gateway", 8080),
            ("user-service", 8086),
            ("scheduling-service", 8081),
        ]

        results = {}
        for service_name, port in services:
            try:
                response = requests.get(f"http://localhost:{port}/v1/{service_name.replace('-', '')}/health", timeout=5)
                is_up = response.status_code == 200
                results[service_name] = is_up
                status = "✅" if is_up else "❌"
                print(f"{status} {service_name}: {'UP' if is_up else 'DOWN'} (port {port})")
            except Exception as e:
                results[service_name] = False
                print(f"❌ {service_name}: ERROR - {e}")

        return all(results.values())

    def test_build(self):
        """Test if the project builds successfully"""
        print("🔍 Testing build...")

        try:
            result = subprocess.run(
                ["./gradlew", "build", "--no-daemon", "-x", "test"],
                cwd=self.base_path,
                capture_output=True,
                text=True,
                timeout=300
            )
            success = result.returncode == 0
            status = "✅" if success else "❌"
            print(f"{status} Build: {'SUCCESS' if success else 'FAILED'}")
            if not success:
                print(f"Build output: {result.stderr[-500:]}")  # Last 500 chars
            return success
        except subprocess.TimeoutExpired:
            print("❌ Build: TIMEOUT")
            return False
        except FileNotFoundError:
            print("❌ Build: gradlew not found")
            return False

    def test_docker_compose(self):
        """Test if docker-compose services are running"""
        print("🔍 Testing Docker Compose...")

        try:
            result = subprocess.run(
                ["docker-compose", "ps"],
                cwd=self.base_path,
                capture_output=True,
                text=True,
                timeout=30
            )

            if result.returncode == 0:
                lines = result.stdout.strip().split('\n')
                if len(lines) > 2:  # Header + at least one service
                    running_count = sum(1 for line in lines[1:] if 'Up' in line)
                    print(f"✅ Docker Compose: {running_count} services running")
                    return running_count > 0
                else:
                    print("❌ Docker Compose: No services found")
                    return False
            else:
                print(f"❌ Docker Compose: {result.stderr}")
                return False
        except FileNotFoundError:
            print("❌ Docker Compose: docker-compose not found")
            return False

    def run_full_test_suite(self):
        """Run complete test suite"""
        print("🧪 Running HOBSINN test suite...")
        print("=" * 50)

        tests = [
            ("Build", self.test_build),
            ("Infrastructure", self.test_infrastructure),
            ("Services", self.test_services),
            ("Docker Compose", self.test_docker_compose),
        ]

        results = {}
        for test_name, test_func in tests:
            print(f"\n📋 Testing {test_name}...")
            results[test_name] = test_func()

        print("\n" + "=" * 50)
        print("📊 Test Results:")

        all_passed = True
        for test_name, passed in results.items():
            status = "✅ PASS" if passed else "❌ FAIL"
            print(f"  {test_name}: {status}")
            if not passed:
                all_passed = False

        if all_passed:
            print("\n🎉 All tests passed! HOBSINN is ready.")
        else:
            print("\n⚠️  Some tests failed. Check the output above.")

        return all_passed

    def _test_postgres(self):
        """Test PostgreSQL connection"""
        try:
            import psycopg2
            conn = psycopg2.connect(
                host="localhost",
                port=5432,
                database="hobsinn",
                user="hobsinn",
                password="hobsinn",
                connect_timeout=5
            )
            conn.close()
            return True
        except ImportError:
            # Fallback to docker exec if psycopg2 not available
            result = subprocess.run(
                ["docker", "exec", "hobsinn_postgres_1", "pg_isready", "-U", "hobsinn"],
                capture_output=True,
                timeout=10
            )
            return result.returncode == 0
        except Exception:
            return False

    def _test_kafka(self):
        """Test Kafka connection"""
        try:
            result = subprocess.run(
                ["docker", "exec", "hobsinn_kafka_1", "kafka-broker-api-versions", "--bootstrap-server", "localhost:9092"],
                capture_output=True,
                timeout=10
            )
            return result.returncode == 0
        except Exception:
            return False

    def _test_redis(self):
        """Test Redis connection"""
        try:
            import redis
            r = redis.Redis(host='localhost', port=6379, socket_connect_timeout=5)
            r.ping()
            return True
        except ImportError:
            # Fallback to docker exec
            result = subprocess.run(
                ["docker", "exec", "hobsinn_redis_1", "redis-cli", "ping"],
                capture_output=True,
                timeout=10
            )
            return "PONG" in result.stdout.decode()
        except Exception:
            return False


def main():
    if len(sys.argv) > 1 and sys.argv[1] == "--help":
        print("HOBSINN Test Runner")
        print("Usage: python test_runner.py [test_type]")
        print("test_types: build, infrastructure, services, docker-compose, all")
        return

    test_type = sys.argv[1] if len(sys.argv) > 1 else "all"
    tester = HobsinnTester()

    if test_type == "build":
        success = tester.test_build()
    elif test_type == "infrastructure":
        success = tester.test_infrastructure()
    elif test_type == "services":
        success = tester.test_services()
    elif test_type == "docker-compose":
        success = tester.test_docker_compose()
    else:
        success = tester.run_full_test_suite()

    sys.exit(0 if success else 1)


if __name__ == "__main__":
    main()