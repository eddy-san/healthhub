@echo off
setlocal enabledelayedexpansion

echo.
echo ================================
echo HealthHub RESET
echo ================================
echo.

REM Script-Verzeichnis
cd /d %~dp0

echo [STEP 0] Load .env

if not exist ".env" (
    echo ERROR: .env not found
    goto error
)

for /f "tokens=1,2 delims==" %%A in (.env) do (
    if "%%A"=="MSSQL_SA_PASSWORD" set MSSQL_SA_PASSWORD=%%B
)

if "%MSSQL_SA_PASSWORD%"=="" (
    echo ERROR: MSSQL_SA_PASSWORD not found in .env
    goto error
)

set COMPOSE_FILE=docker\docker-compose.yml

if not exist "%COMPOSE_FILE%" (
    echo ERROR: docker-compose.yml not found
    goto error
)

echo.
echo [STEP 1] Remove conflicting containers
docker rm -f healthhub-sql >nul 2>&1
docker rm -f healthhub-app >nul 2>&1

echo.
echo [STEP 2] Stop containers and volumes
docker compose --env-file .env -f %COMPOSE_FILE% down -v

echo.
echo [STEP 3] Start SQL Server
docker compose --env-file .env -f %COMPOSE_FILE% up -d healthhub-sql

if errorlevel 1 (
    echo ERROR: Could not start SQL container
    goto error
)

echo.
echo [STEP 4] Wait for SQL Server

set SQL_READY=0

for /L %%i in (1,1,40) do (

    docker exec healthhub-sql /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "%MSSQL_SA_PASSWORD%" -C -Q "SELECT 1" >nul 2>&1

    if not errorlevel 1 (
        set SQL_READY=1
        echo SQL Server ready.
        goto sqlready
    )

    echo Waiting for SQL Server... %%i/40
    timeout /t 2 >nul
)

:sqlready

if "%SQL_READY%"=="0" (
    echo ERROR: SQL Server did not become ready
    goto error
)

echo.
echo [STEP 5] Run Liquibase
call mvnw.cmd -Pupdate-schema -Ddb.password=%MSSQL_SA_PASSWORD% process-resources

if errorlevel 1 (
    echo ERROR: Liquibase migration failed
    goto error
)

echo.
echo ====================================
echo HealthHub reset completed
echo ====================================
echo.
pause
exit /b 0

:error
echo.
echo RESET FAILED
echo.
pause
exit /b 1