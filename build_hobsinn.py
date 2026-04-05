#!/usr/bin/env python3
"""
HOBSINN Build Script - Fixes and builds the existing codebase
"""

import os
import subprocess
import sys
from pathlib import Path

class HobsinnBuilder:
    def __init__(self, base_path: str = "/home/user/hobsinn"):
        self.base_path = Path(base_path)

    def fix_build_gradle(self):
        """Fix the root build.gradle file"""
        print("🔧 Fixing build.gradle...")

        build_gradle_path = self.base_path / "build.gradle"
        if build_gradle_path.exists():
            content = build_gradle_path.read_text()

            # Remove the problematic java plugin from allprojects
            if "apply plugin: 'java'" in content:
                content = content.replace("apply plugin: 'java'\n", "")
                print("✅ Removed problematic java plugin from allprojects")

            # Ensure proper Kotlin configuration
            if "kotlin {" not in content:
                content += """

subprojects {
    tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile) {
        kotlinOptions {
            freeCompilerArgs = ['-Xjsr305=strict']
            jvmTarget = '21'
        }
    }
}"""

            build_gradle_path.write_text(content)
            print("✅ Updated build.gradle")

    def fix_scheduling_service(self):
        """Fix the scheduling service build.gradle"""
        print("🔧 Fixing scheduling-service build.gradle...")

        service_gradle = self.base_path / "services/scheduling-service/build.gradle"
        if service_gradle.exists():
            content = service_gradle.read_text()

            # Add missing dependencies
            if "hibernate-spatial" not in content:
                # Find the dependencies block and add spatial support
                dep_start = content.find("dependencies {")
                if dep_start != -1:
                    insert_pos = content.find("\n}", dep_start)
                    if insert_pos != -1:
                        content = content[:insert_pos] + """
    implementation 'org.hibernate.orm:hibernate-spatial:6.4.4.Final'
    implementation 'org.locationtech.jts:jts-core:1.19.0'
""" + content[insert_pos:]

            service_gradle.write_text(content)
            print("✅ Added spatial dependencies to scheduling-service")

    def run_build(self):
        """Run the Gradle build"""
        print("🏗️  Running Gradle build...")

        os.chdir(self.base_path)

        try:
            # Clean first
            result = subprocess.run(["./gradlew", "clean"], capture_output=True, text=True, timeout=300)
            if result.returncode != 0:
                print(f"❌ Clean failed: {result.stderr}")
                return False

            # Build
            result = subprocess.run(["./gradlew", "build", "--no-daemon", "-x", "test"], capture_output=True, text=True, timeout=600)
            if result.returncode == 0:
                print("✅ Build successful!")
                return True
            else:
                print(f"❌ Build failed: {result.stderr}")
                return False

        except subprocess.TimeoutExpired:
            print("❌ Build timed out")
            return False
        except FileNotFoundError:
            print("❌ gradlew not found. Run from project root.")
            return False

    def run_tests(self):
        """Run tests"""
        print("🧪 Running tests...")

        os.chdir(self.base_path)

        try:
            result = subprocess.run(["./gradlew", "test", "--no-daemon"], capture_output=True, text=True, timeout=300)
            if result.returncode == 0:
                print("✅ Tests passed!")
                return True
            else:
                print(f"❌ Tests failed: {result.stderr}")
                return False
        except subprocess.TimeoutExpired:
            print("❌ Tests timed out")
            return False

    def build_docker_images(self):
        """Build Docker images"""
        print("🐳 Building Docker images...")

        os.chdir(self.base_path)

        services = ["api-gateway", "user-service", "scheduling-service"]

        for service in services:
            try:
                result = subprocess.run(["docker", "build", "-t", f"hobsinn/{service}", f"services/{service}"], capture_output=True, text=True, timeout=300)
                if result.returncode == 0:
                    print(f"✅ Built {service} image")
                else:
                    print(f"❌ Failed to build {service}: {result.stderr}")
                    return False
            except subprocess.TimeoutExpired:
                print(f"❌ Docker build for {service} timed out")
                return False

        return True

    def run_full_pipeline(self):
        """Run the complete build pipeline"""
        print("🚀 Starting HOBSINN build pipeline...")

        steps = [
            ("Fix build.gradle", self.fix_build_gradle),
            ("Fix scheduling service", self.fix_scheduling_service),
            ("Build project", self.run_build),
            ("Run tests", self.run_tests),
            ("Build Docker images", self.build_docker_images),
        ]

        for step_name, step_func in steps:
            print(f"\n📋 {step_name}...")
            if not step_func():
                print(f"❌ Pipeline failed at: {step_name}")
                return False

        print("\n🎉 Pipeline completed successfully!")
        print("📝 Next steps:")
        print("1. Run: docker-compose up -d")
        print("2. Test services at their respective ports")
        return True


if __name__ == "__main__":
    base_path = sys.argv[1] if len(sys.argv) > 1 else "/home/user/hobsinn"

    builder = HobsinnBuilder(base_path)
    success = builder.run_full_pipeline()

    sys.exit(0 if success else 1)