#!/usr/bin/env bash
set -u

fail() {
    echo
    echo "ERROR: $1"
    echo
    echo "ERROR: Reset failed."
    echo
    exit 1
}

echo "[STEP 0] Load .env"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR" || exit 1

[ -f ".env" ] || fail ".env not found"

MSSQL_SA_PASSWORD="$(grep '^MSSQL_SA_PASSWORD=' .env | cut -d '=' -f2- | tr -d '\r')"
[ -n "$MSSQL_SA_PASSWORD" ] || fail "MSSQL_SA_PASSWORD not found in .env"

COMPOSE_FILE="docker/docker-compose.yml"
[ -f "$COMPOSE_FILE" ] || fail "$COMPOSE_FILE not found"

SQL_VOLUME="docker_mssql_data"

echo "[STEP 1] Stop SQL Server"
docker compose --env-file .env -f "$COMPOSE_FILE" stop healthhub-sql >/dev/null 2>&1 || true

echo "[STEP 2] Remove standalone SQL container if it exists"
docker rm -f healthhub-sql >/dev/null 2>&1 || true

echo "[STEP 3] Remove SQL data volume only"
docker volume rm "$SQL_VOLUME" >/dev/null 2>&1 || fail "Could not remove SQL volume: $SQL_VOLUME"

echo "[STEP 4] Start SQL Server container"
docker compose --env-file .env -f "$COMPOSE_FILE" up -d healthhub-sql || fail "Could not start healthhub-sql"

echo "[STEP 5] Wait for SQL Server to become ready"
SQL_READY=false

for i in $(seq 1 40); do
    if docker exec healthhub-sql /opt/mssql-tools18/bin/sqlcmd \
        -S localhost \
        -U sa \
        -P "$MSSQL_SA_PASSWORD" \
        -C \
        -Q "SELECT 1" >/dev/null 2>&1; then
        SQL_READY=true
        echo "SQL Server ready."
        break
    fi

    echo "Waiting for SQL Server... ($i/40)"
    sleep 2
done

[ "$SQL_READY" = true ] || fail "SQL Server did not become ready in time"

echo "[STEP 6] Configure Java"
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"

echo "[STEP 7] Run Liquibase migrations"
./mvnw -Pupdate-schema "-Ddb.password=$MSSQL_SA_PASSWORD" process-resources || fail "Liquibase migrations failed"

echo "[STEP 8] Start CloudBeaver"
docker compose --env-file .env -f "$COMPOSE_FILE" up -d cloudbeaver || fail "Could not start CloudBeaver"

echo
echo "===================================="
echo "HealthHub hard reset completed successfully"
echo "SQL data was reset"
echo "CloudBeaver configuration was preserved"
echo "SQL Server and CloudBeaver are running"
echo "===================================="
echo