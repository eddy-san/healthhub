@echo off
setlocal enabledelayedexpansion

echo [STEP 0] Load .env

if not exist ".env" (
    echo ERROR: .env not found
    goto fail
)

for /f "usebackq tokens=1,* delims==" %%A in (".env") do (
    if "%%A"=="MSSQL_SA_PASSWORD" set MSSQL_SA_PASSWORD=%%B
)

if "%MSSQL_SA_PASSWORD%"=="" (
    echo ERROR: MSSQL_SA_PASSWORD not found in .env
    goto fail
)

set COMPOSE_FILE=docker\docker-compose.yml

if not exist "%COMPOSE_FILE%" (
    echo ERROR: %COMPOSE_FILE% not found
    goto fail
)

echo [STEP 1] Remove conflicting standalone containers if they exist
docker rm -f healthhub-sql >nul 2>&1
docker rm -f healthhub-app >nul 2>&1

echo [STEP 2] Stop and remove all containers and volumes
docker compose --env-file .env -f "%COMPOSE_FILE%" down -v
if errorlevel 1 (
    echo WARNING: docker compose down returned a non-zero exit code, continuing...
)

echo [STEP 3] Start SQL Server container
docker compose --env-file .env -f "%COMPOSE_FILE%" up -d healthhub-sql
if errorlevel 1 goto fail

echo [STEP 4] Wait for SQL Server to become ready

set SQL_READY=false

for /L %%I in (1,1,40) do (
    docker exec healthhub-sql /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "%MSSQL_SA_PASSWORD%" -C -Q "SELECT 1" >nul 2>&1

    if not errorlevel 1 (
        set SQL_READY=true
        echo SQL Server ready.
        goto sqlready
    )

    echo Waiting for SQL Server... (%%I/40)
    timeout /t 2 >nul
)

:sqlready

if "%SQL_READY%"=="false" (
    echo ERROR: SQL Server did not become ready in time.
    goto fail
)

echo [STEP 5] Run Liquibase migrations
call .\mvnw.cmd -Pupdate-schema "-Ddb.password=%MSSQL_SA_PASSWORD%" process-resources
if errorlevel 1 goto fail

echo.
echo ====================================
echo HealthHub reset completed successfully
echo SQL Server is running and schema is up to date
echo ====================================
echo.
goto end

:fail
echo.
echo ERROR: Reset failed.
echo.
exit /b 1

:end
pause
endlocal