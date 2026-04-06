#!/usr/bin/env python3
"""
HOBSINN Build & Fix Script
Fix build issues and compile the codebase
"""

import subprocess
import sys
import os
from pathlib import Path

class HobsinnBuilder:
    def __init__(self, base_path=None):
        self.base_path = Path(base_path) if base_path else Path(__file__).parent

    def run_command(self, command, cwd=None, check=True):
        """Run a shell command"""
        try:
            result = subprocess.run(
                command,
                shell=True,
                cwd=cwd or self.base_path,
                capture_output=True,
                text=True,
                check=check
            )
            return result.stdout, result.stderr, result.returncode
        except subprocess.CalledProcessError as e:
            return e.stdout, e.stderr, e.returncode

    def build(self):
        """Build the project"""
        print("🔨 Building HOBSINN...")

        # Check if gradlew exists
        gradlew_path = self.base_path / "gradlew"
        if not gradlew_path.exists():
            print("❌ gradlew not found. Using gradle...")
            build_cmd = "gradle build -x test"
        else:
            build_cmd = "./gradlew build -x test"

        stdout, stderr, code = self.run_command(build_cmd, check=False)

        if code == 0:
            print("✅ Build successful!")
            return True
        else:
            print("❌ Build failed")
            print("STDOUT:", stdout)
            print("STDERR:", stderr)
            return False

    def fix_build(self):
        """Attempt to fix common build issues"""
        print("🔧 Attempting to fix build issues...")

        # Clean build
        print("Cleaning...")
        self.run_command("./gradlew clean", check=False)

        # Check Java version
        stdout, stderr, code = self.run_command("java -version", check=False)
        print("Java version:", stdout.split('\n')[0] if stdout else "Unknown")

        # Check Gradle version
        stdout, stderr, code = self.run_command("./gradlew --version", check=False)
        if code == 0:
            version_line = [line for line in stdout.split('\n') if 'Gradle' in line]
            print("Gradle version:", version_line[0] if version_line else "Unknown")

        print("Please ensure Java 21 is installed and JAVA_HOME is set correctly.")
        return True

def main():
    base_path = sys.argv[1] if len(sys.argv) > 1 else None
    builder = HobsinnBuilder(base_path)

    if builder.build():
        print("🎉 Build completed successfully!")
    else:
        builder.fix_build()
        print("Please fix the issues and try again.")

if __name__ == "__main__":
    main()