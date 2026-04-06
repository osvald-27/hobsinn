#!/usr/bin/env python3
"""
HOBSINN Incremental Builder
Add features incrementally to existing services
"""

import sys
from pathlib import Path

class IncrementalBuilder:
    def __init__(self):
        self.base_path = Path(__file__).parent

    def add_service(self, name, port):
        """Add a new service"""
        print(f"➕ Adding service: {name} on port {port}")
        print("This would scaffold a new service, but for now, services are already implemented.")
        return True

    def add_entity(self, service, entity, fields):
        """Add entity to service"""
        print(f"➕ Adding entity {entity} to {service} with fields: {fields}")
        print("Entity already added manually.")
        return True

    def add_repository(self, service, repo, entity):
        """Add repository"""
        print(f"➕ Adding repository {repo} for {entity} in {service}")
        return True

    def add_service_class(self, service, class_name):
        """Add service class"""
        print(f"➕ Adding service class {class_name} to {service}")
        return True

    def add_migration(self, service, name, sql):
        """Add database migration"""
        print(f"➕ Adding migration {name} to {service}")
        print(f"SQL: {sql}")
        return True

def main():
    if len(sys.argv) < 2:
        print("Usage: python3 incremental_builder.py <command> [args...]")
        return

    builder = IncrementalBuilder()
    command = sys.argv[1]

    if command == "add-service" and len(sys.argv) >= 4:
        builder.add_service(sys.argv[2], sys.argv[3])
    elif command == "add-entity" and len(sys.argv) >= 5:
        builder.add_entity(sys.argv[2], sys.argv[3], sys.argv[4:])
    elif command == "add-repository" and len(sys.argv) >= 5:
        builder.add_repository(sys.argv[2], sys.argv[3], sys.argv[4])
    elif command == "add-service-class" and len(sys.argv) >= 4:
        builder.add_service_class(sys.argv[2], sys.argv[3])
    elif command == "add-migration" and len(sys.argv) >= 5:
        builder.add_migration(sys.argv[2], sys.argv[3], " ".join(sys.argv[4:]))
    else:
        print(f"Unknown command: {command}")

if __name__ == "__main__":
    main()