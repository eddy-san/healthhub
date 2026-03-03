#!/usr/bin/env bash
set -euo pipefail

WILDFLY_HOME="../../wildfly-39.0.1.Final/wildfly-39.0.1.Final"
WILDFLY_BIN="$WILDFLY_HOME/bin"
WILDFLY_DEPLOY="$WILDFLY_HOME/standalone/deployments"
WAR="target/healthhub-1.0-SNAPSHOT.war"

echo "==> 1) Start SQL Server (Docker)"
docker compose up -d

echo "==> 2) Run Liquibase"
export JAVA_TOOL_OPTIONS="-Ddb.password=${MSSQL_SA_PASSWORD:-}"
./mvnw -Pupdate-schema clean process-resources

echo "==> 3) Build WAR"
./mvnw clean package

echo "==> 4) Start WildFly if not running"

if curl -s http://localhost:8080/ >/dev/null 2>&1; then
  echo "WildFly already running."
else
  echo "Starting WildFly..."
  ( cd "$WILDFLY_BIN" && ./standalone.bat ) &
  echo "Waiting for WildFly..."
  for i in {1..60}; do
    if curl -s http://localhost:8080/ >/dev/null 2>&1; then
      break
    fi
    sleep 1
  done
fi

echo "==> 5) Deploy WAR"
cp -f "$WAR" "$WILDFLY_DEPLOY/"
touch "$WILDFLY_DEPLOY/healthhub-1.0-SNAPSHOT.war.dodeploy"

echo
echo "HealthHub deployed 🚀"
echo "http://localhost:8080/healthhub-1.0-SNAPSHOT/"
echo
read -r -p "Press Enter to close..." _