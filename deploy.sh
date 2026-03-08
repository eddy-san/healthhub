#!/usr/bin/env bash
set -euo pipefail

WILDFLY_HOME="/opt/wildfly"
WILDFLY_BIN="$WILDFLY_HOME/bin"
WILDFLY_DEPLOY="$WILDFLY_HOME/standalone/deployments"
WAR="target/healthhub-1.0-SNAPSHOT.war"

echo "[STEP 0] Load .env"
MSSQL_SA_PASSWORD="$(grep '^MSSQL_SA_PASSWORD=' .env | cut -d '=' -f2- | tr -d '\r')"

export JAVA_TOOL_OPTIONS="-Ddb.password=$MSSQL_SA_PASSWORD"

echo "[STEP 1] Start SQL Server"
docker compose -f docker/docker-compose.yml up -d

echo "[STEP 1.1] Wait for SQL Server"
SQL_READY=false
for i in {1..30}; do
  if docker exec healthhub-sql /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "$MSSQL_SA_PASSWORD" -C -Q "SELECT 1" >/dev/null 2>&1; then
    SQL_READY=true
    echo "SQL Server ready."
    break
  fi
  echo "Waiting for SQL Server... ($i/30)"
  sleep 2
done

if [ "$SQL_READY" = false ]; then
  echo "ERROR: SQL Server did not become ready in time."
  exit 1
fi

echo "[STEP 2] Run Liquibase"
./mvnw -Pupdate-schema clean process-resources

echo "[STEP 3] Build WAR"
./mvnw clean package

echo "[STEP 4] Start WildFly if needed"
if curl -s http://localhost:8080/ >/dev/null 2>&1; then
  echo "WildFly already running."
else
  echo "Starting WildFly..."
  nohup "$WILDFLY_BIN/standalone.sh" >/tmp/wildfly.log 2>&1 &
  for i in {1..60}; do
    if curl -s http://localhost:8080/ >/dev/null 2>&1; then
      echo "WildFly ready."
      break
    fi
    sleep 1
  done
fi

echo "[STEP 5] Deploy WAR"
cp -f "$WAR" "$WILDFLY_DEPLOY/"
touch "$WILDFLY_DEPLOY/healthhub-1.0-SNAPSHOT.war.dodeploy"

echo "HealthHub deployed."