#!/usr/bin/env bash
set -euo pipefail

WILDFLY_URL="http://localhost:8080/healthhub"

echo "[STEP 0] Load .env"
MSSQL_SA_PASSWORD="$(grep '^MSSQL_SA_PASSWORD=' .env | cut -d '=' -f2- | tr -d '\r')"
APP_LOGIN_USER="$(grep '^APP_LOGIN_USER=' .env | cut -d '=' -f2- | tr -d '\r')"
APP_LOGIN_PASSWORD="$(grep '^APP_LOGIN_PASSWORD=' .env | cut -d '=' -f2- | tr -d '\r')"

export JAVA_TOOL_OPTIONS="-Ddb.password=$MSSQL_SA_PASSWORD"
export APP_LOGIN_USER
export APP_LOGIN_PASSWORD

echo "[STEP 1] Start SQL Server only"
docker compose up -d healthhub-sql

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

echo "[STEP 4] Start / rebuild app container"
docker compose up -d --build healthhub-app

echo "[STEP 4.1] Wait for app"
APP_READY=false
for i in {1..60}; do
  if curl -fsS "$WILDFLY_URL/" >/dev/null 2>&1 || curl -fsS http://localhost:8080/ >/dev/null 2>&1; then
    APP_READY=true
    echo "App ready."
    break
  fi
  echo "Waiting for app... ($i/60)"
  sleep 2
done

if [ "$APP_READY" = false ]; then
  echo "ERROR: App did not become ready in time."
  docker compose logs --tail=200 healthhub-app || true
  exit 1
fi

echo
echo "HealthHub deployed 🚀"
echo "$WILDFLY_URL/"