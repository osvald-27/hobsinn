#!/usr/bin/env python3
"""
HOBSINN Master Script - One-command codebase management
"""

import subprocess
import sys
import os
from pathlib import Path

class HobsinnMaster:
    def __init__(self):
        self.base_path = Path("/home/user/hobsinn")
        self.scripts = {
            "generate": "generate_hobsinn.py",
            "build": "build_hobsinn.py",
            "incremental": "incremental_builder.py",
            "test": "test_runner.py"
        }

    def run_command(self, script_name: str, args: list = None):
        """Run a script with arguments"""
        script_path = self.base_path / self.scripts[script_name]
        if not script_path.exists():
            print(f"❌ Script {script_name} not found at {script_path}")
            return False

        cmd = ["python3", str(script_path)]
        if args:
            cmd.extend(args)

        print(f"🚀 Running: {' '.join(cmd)}")

        try:
            result = subprocess.run(cmd, cwd=self.base_path)
            return result.returncode == 0
        except KeyboardInterrupt:
            print("\n⏹️  Interrupted by user")
            return False
        except Exception as e:
            print(f"❌ Error running {script_name}: {e}")
            return False

    def generate_complete_codebase(self):
        """Generate the complete HOBSINN codebase"""
        print("🎯 Generating complete HOBSINN codebase...")
        return self.run_command("generate")

    def build_and_test(self):
        """Build the codebase and run tests"""
        print("🏗️  Building and testing HOBSINN...")

        steps = [
            ("build", []),
            ("test", ["all"])
        ]

        for script, args in steps:
            if not self.run_command(script, args):
                print(f"❌ Failed at {script} step")
                return False

        return True

    def setup_development_environment(self):
        """Set up complete development environment"""
        print("🛠️  Setting up HOBSINN development environment...")

        # Generate codebase
        if not self.generate_complete_codebase():
            return False

        # Make scripts executable
        scripts_dir = self.base_path / "scripts"
        if scripts_dir.exists():
            for script in scripts_dir.glob("*.sh"):
                os.chmod(script, 0o755)

        # Build and test
        if not self.build_and_test():
            return False

        print("✅ Development environment setup complete!")
        print("📝 Next steps:")
        print("1. Run: docker-compose up -d")
        print("2. Test services at their respective ports")
        print("3. Use incremental_builder.py to add more features")
        return True

    def quick_start(self):
        """Quick start for existing codebase"""
        print("⚡ Quick start HOBSINN...")

        # Test current state
        if not self.run_command("test", ["build"]):
            print("❌ Build test failed. Running build script...")
            if not self.run_command("build"):
                return False

        # Start infrastructure
        print("🐳 Starting infrastructure...")
        try:
            subprocess.run(["docker-compose", "up", "-d"], cwd=self.base_path, check=True)
        except subprocess.CalledProcessError:
            print("❌ Failed to start Docker Compose")
            return False

        # Test services
        print("⏳ Waiting for services to start...")
        import time
        time.sleep(30)  # Wait for services to start

        if self.run_command("test", ["infrastructure"]) and self.run_command("test", ["services"]):
            print("✅ HOBSINN is running!")
            print("🌐 API Gateway: http://localhost:8080")
            print("👤 User Service: http://localhost:8086")
            print("📅 Scheduling Service: http://localhost:8081")
            print("📊 Grafana: http://localhost:3000 (admin/admin)")
            print("📋 Kafka UI: http://localhost:8090")
            return True
        else:
            print("❌ Some services failed to start")
            return False

    def show_help(self):
        """Show help information"""
        print("HOBSINN Master Script")
        print("=" * 50)
        print("One-command codebase management for HOBSINN project")
        print()
        print("COMMANDS:")
        print("  generate          - Generate complete codebase from scratch")
        print("  build            - Fix and build existing codebase")
        print("  test [type]      - Run tests (build, infrastructure, services, docker-compose, all)")
        print("  incremental      - Add services/features incrementally")
        print("  setup            - Complete development environment setup")
        print("  start            - Quick start existing codebase")
        print("  help             - Show this help")
        print()
        print("EXAMPLES:")
        print("  python3 master.py setup")
        print("  python3 master.py test all")
        print("  python3 master.py incremental add-service payment-service 8082")
        print()
        print("SCRIPTS:")
        for name, script in self.scripts.items():
            print(f"  {name:12} - {script}")


def main():
    if len(sys.argv) < 2:
        HobsinnMaster().show_help()
        return

    master = HobsinnMaster()
    command = sys.argv[1]
    args = sys.argv[2:]

    if command == "generate":
        success = master.generate_complete_codebase()
    elif command == "build":
        success = master.run_command("build", args)
    elif command == "test":
        success = master.run_command("test", args)
    elif command == "incremental":
        success = master.run_command("incremental", args)
    elif command == "setup":
        success = master.setup_development_environment()
    elif command == "start":
        success = master.quick_start()
    elif command == "help":
        master.show_help()
        return
    else:
        print(f"❌ Unknown command: {command}")
        master.show_help()
        return

    if command not in ["help"]:
        status = "✅ SUCCESS" if success else "❌ FAILED"
        print(f"\n{status}")


if __name__ == "__main__":
    main()