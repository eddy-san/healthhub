@echo off
setlocal enabledelayedexpansion

echo.
echo ================================
echo HealthHub DEPLOY
echo ================================
echo.

REM Ins Script-Verzeichnis wechseln
cd /d "%~dp0"

echo [STEP 0] Load .env

if not exist ".env" (
    echo ERROR: .env not found
    goto fail
)

for /f "usebackq tokens=1,* delims==" %%A in (".env") do (
    if not "%%A"=="" (
        if /I "%%A"=="MSSQL_SA_PASSWORD" set "MSSQL_SA_PASSWORD=%%B"
    )
)

if "%MSSQL_SA_PASSWORD%"=="" (
    echo ERROR: MSSQL_SA_PASSWORD not found in .env
    goto fail
)

set "COMPOSE_FILE=docker\docker-compose.yml"
set "DRIVER_SOURCE=%USERPROFILE%\.m2\repository\com\microsoft\sqlserver\mssql-jdbc\12.6.1.jre11\mssql-jdbc-12.6.1.jre11.jar"
set "DRIVER_TARGET=docker\wildfly\modules\com\microsoft\sqlserver\main\mssql-jdbc.jar"

if not exist "%COMPOSE_FILE%" (
    echo ERROR: %COMPOSE_FILE% not found
    goto fail
)

echo.
echo [STEP 1] Build WAR
call mvnw.cmd clean package
if errorlevel 1 (
    echo ERROR: Maven build failed
    goto fail
)

echo.
echo [STEP 2] Prepare SQL Server JDBC driver for WildFly image
if not exist "%DRIVER_SOURCE%" (
    echo ERROR: JDBC driver not found:
    echo %DRIVER_SOURCE%
    echo.
    echo Tip: run Maven once or check the driver version/path.
    goto fail
)

if not exist "docker\wildfly\modules\com\microsoft\sqlserver\main" (
    echo ERROR: Target directory does not exist:
    echo docker\wildfly\modules\com\microsoft\sqlserver\main
    goto fail
)

copy /Y "%DRIVER_SOURCE%" "%DRIVER_TARGET%" >nul
if errorlevel 1 (
    echo ERROR: Could not copy JDBC driver
    goto fail
)

echo.
echo [STEP 3] Start SQL Server if needed
docker compose --env-file .env -f "%COMPOSE_FILE%" up -d healthhub-sql
if errorlevel 1 (
    echo ERROR: Could not start healthhub-sql
    goto fail
)

echo.
echo [STEP 4] Wait for SQL Server
set "SQL_READY=0"

for /L %%i in (1,1,40) do (
    docker exec healthhub-sql /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "%MSSQL_SA_PASSWORD%" -C -Q "SELECT 1" >nul 2>&1

    if not errorlevel 1 (
        set "SQL_READY=1"
        echo SQL Server ready.
        goto sqlready
    )

    echo Waiting for SQL Server... %%i/40
    timeout /t 2 >nul
)

:sqlready
if "%SQL_READY%"=="0" (
    echo ERROR: SQL Server did not become ready
    goto fail
)

echo.
echo [STEP 5] Build and start WildFly app container
docker compose --env-file .env -f "%COMPOSE_FILE%" up --build -d healthhub-app
if errorlevel 1 (
    echo ERROR: Could not build/start healthhub-app
    goto fail
)

echo.
echo ====================================
echo HealthHub deployed successfully
echo http://localhost:8080/healthhub
echo ====================================
echo.
goto end

:fail
echo.
echo ====================================
echo ERROR: Deployment failed
echo ====================================
echo.
pause
exit /b 1

:end
pause
endlocal
exit /b 0