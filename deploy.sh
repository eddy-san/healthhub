#!/usr/bin/env bash
set -euo pipefail

fail() {
    echo
    echo "ERROR: $1"
    echo
    exit 1
}

get_env_value() {
    local key="$1"
    local value
    value="$(grep "^${key}=" .env | cut -d '=' -f2- | tr -d '\r' || true)"
    echo "$value"
}

echo "[STEP 0] Load .env"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR" || exit 1

[ -f ".env" ] || fail ".env not found"

MSSQL_SA_PASSWORD="$(get_env_value MSSQL_SA_PASSWORD)"
[ -n "$MSSQL_SA_PASSWORD" ] || fail "MSSQL_SA_PASSWORD not found in .env"

COMPOSE_FILE="docker/docker-compose.yml"
[ -f "$COMPOSE_FILE" ] || fail "$COMPOSE_FILE not found"

echo "[STEP 1] Configure Java"
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"

java -version || fail "java not available"
javac -version || fail "javac not available"
./mvnw -version || fail "maven wrapper not working"

echo "[STEP 2] Build and start containers"
docker compose --env-file .env -f "$COMPOSE_FILE" up -d --build healthhub-app || fail "Could not start healthhub-app"

echo "[STEP 3] Show container status"
docker compose --env-file .env -f "$COMPOSE_FILE" ps || true

echo "[STEP 4] Wait for application"
for i in $(seq 1 60); do
    if curl -kfsS https://healthhub.roth-it-solutions.de/ >/dev/null 2>&1; then
        echo "Application is reachable."
        echo
        echo "===================================="
        echo "HealthHub deploy completed successfully"
        echo "===================================="
        echo
        exit 0
    fi

    echo "Waiting for application... ($i/60)"
    sleep 2
done

echo "[STEP 5] Application logs"
docker compose --env-file .env -f "$COMPOSE_FILE" logs --no-color healthhub-app || true

fail "Application did not become reachable in time"