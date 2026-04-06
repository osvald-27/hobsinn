#!/usr/bin/env python3
"""
HOBSINN Master Orchestrator
Complete setup, start, and test management for HOBSINN microservices
"""

import subprocess
import sys
import os
from pathlib import Path

class HobsinnMaster:
    def __init__(self):
        self.base_path = Path(__file__).parent
        self.services_dir = self.base_path / "services"
        self.infrastructure_dir = self.base_path / "infrastructure" / "docker"

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
            print(f"Command failed: {command}")
            print(f"Error: {e.stderr}")
            return e.stdout, e.stderr, e.returncode

    def setup(self):
        """Complete setup: build and start"""
        print("🚀 Starting HOBSINN setup...")

        # Build all services
        print("📦 Building services...")
        stdout, stderr, code = self.run_command("gradle build -x test", check=False)
        if code != 0:
            print("❌ Build failed")
            print(stderr)
            return False

        # Start infrastructure
        print("🐳 Starting infrastructure...")
        os.chdir(self.infrastructure_dir)
        stdout, stderr, code = self.run_command("docker-compose up -d", check=False)
        if code != 0:
            print("❌ Failed to start infrastructure")
            print(stderr)
            return False

        print("✅ Setup complete!")
        return True

    def start(self):
        """Quick start existing codebase"""
        print("🚀 Starting HOBSINN...")
        os.chdir(self.infrastructure_dir)
        stdout, stderr, code = self.run_command("docker-compose up -d", check=False)
        if code != 0:
            print("❌ Failed to start services")
            print(stderr)
            return False
        print("✅ Services started!")
        return True

    def test_all(self):
        """Run all tests"""
        print("🧪 Running tests...")
        # Run test_runner.py
        stdout, stderr, code = self.run_command("python3 test_runner.py", check=False)
        print(stdout)
        if code != 0:
            print("❌ Tests failed")
        return code == 0

    def help(self):
        """Show help"""
        print("""
HOBSINN Master Orchestrator

Commands:
  setup     - Complete setup (build + start)
  start     - Quick start existing codebase
  test all  - Run all tests
  help      - Show this help

Usage: python3 master.py <command>
        """)

def main():
    if len(sys.argv) < 2:
        HobsinnMaster().help()
        return

    master = HobsinnMaster()
    command = sys.argv[1]

    if command == "setup":
        master.setup()
    elif command == "start":
        master.start()
    elif command == "test" and len(sys.argv) > 2 and sys.argv[2] == "all":
        master.test_all()
    elif command == "help":
        master.help()
    else:
        print(f"Unknown command: {command}")
        master.help()

if __name__ == "__main__":
    main()