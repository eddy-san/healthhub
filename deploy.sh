#!/bin/bash
set -e

docker compose up -d

export JAVA_TOOL_OPTIONS="-Ddb.password=${MSSQL_SA_PASSWORD}"

# 1) DB migrations (no WAR on classpath yet -> no warning)
./mvnw -Pupdate-schema clean process-resources

# 2) Build WAR
./mvnw -q package

# 3) Deploy to WildFly
cp -f target/healthhub-1.0-SNAPSHOT.war \
  ../../wildfly-39.0.1.Final/wildfly-39.0.1.Final/standalone/deployments/
touch ../../wildfly-39.0.1.Final/wildfly-39.0.1.Final/standalone/deployments/healthhub-1.0-SNAPSHOT.war.dodeploy

echo "HealthHub deployed 🚀"

# keep window open when double-clicked
read -p "Press Enter to close..."