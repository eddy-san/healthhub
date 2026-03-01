#!/bin/bash
./mvnw clean package
cp -f target/healthhub-1.0-SNAPSHOT.war \
  ../../wildfly-39.0.1.Final/wildfly-39.0.1.Final/standalone/deployments/
echo "HealthHub deployed 🚀"