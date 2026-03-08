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
set DRIVER_SOURCE=%USERPROFILE%\.m2\repository\com\microsoft\sqlserver\mssql-jdbc\12.6.1.jre11\mssql-jdbc-12.6.1.jre11.jar
set DRIVER_TARGET=docker\wildfly\modules\com\microsoft\sqlserver\main\mssql-jdbc.jar

if not exist "%COMPOSE_FILE%" (
    echo ERROR: %COMPOSE_FILE% not found
    goto fail
)

echo [STEP 1] Build WAR
call .\mvnw.cmd clean package
if errorlevel 1 goto fail

echo [STEP 2] Prepare SQL Server JDBC driver for WildFly image
if not exist "%DRIVER_SOURCE%" (
    echo ERROR: JDBC driver not found:
    echo %DRIVER_SOURCE%
    goto fail
)

copy /Y "%DRIVER_SOURCE%" "%DRIVER_TARGET%" >nul
if errorlevel 1 goto fail

echo [STEP 3] Start SQL Server if needed
docker compose --env-file .env -f "%COMPOSE_FILE%" up -d healthhub-sql
if errorlevel 1 goto fail

echo [STEP 4] Build and start WildFly app container
docker compose --env-file .env -f "%COMPOSE_FILE%" up --build -d healthhub-app
if errorlevel 1 goto fail

echo.
echo ====================================
echo HealthHub deployed successfully
echo http://localhost:8080/healthhub
echo ====================================
echo.
goto end

:fail
echo.
echo ERROR: Deployment failed.
echo.
exit /b 1

:end
pause
endlocal