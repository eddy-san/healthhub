@echo off
setlocal enabledelayedexpansion

echo [STEP 0] Load .env

for /f "tokens=1,* delims==" %%A in (.env) do (
    if "%%A"=="MSSQL_SA_PASSWORD" set MSSQL_SA_PASSWORD=%%B
    if "%%A"=="APP_LOGIN_USER" set APP_LOGIN_USER=%%B
    if "%%A"=="APP_LOGIN_PASSWORD" set APP_LOGIN_PASSWORD=%%B
)

set JAVA_TOOL_OPTIONS=-Ddb.password=%MSSQL_SA_PASSWORD%

set WILDFLY_HOME=..\..\wildfly-39.0.1.Final\wildfly-39.0.1.Final
set WILDFLY_BIN=%WILDFLY_HOME%\bin
set WILDFLY_DEPLOY=%WILDFLY_HOME%\standalone\deployments
set WAR=target\healthhub-1.0-SNAPSHOT.war

echo [STEP 1] Start SQL Server container
docker compose up -d
if errorlevel 1 goto fail

echo [STEP 1.1] Waiting for SQL Server

set SQL_READY=false

for /L %%I in (1,1,30) do (
    docker exec healthhub-sql /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "%MSSQL_SA_PASSWORD%" -C -Q "SELECT 1" >nul 2>&1

    if not errorlevel 1 (
        set SQL_READY=true
        echo SQL Server ready.
        goto sqlready
    )

    echo Waiting for SQL Server... (%%I/30)
    timeout /t 2 >nul
)

:sqlready

if "%SQL_READY%"=="false" (
    echo ERROR: SQL Server did not start.
    goto end
)

echo [STEP 2] Run Liquibase
call mvnw.cmd -Pupdate-schema clean process-resources
if errorlevel 1 goto fail

echo [STEP 3] Build WAR
call mvnw.cmd clean package
if errorlevel 1 goto fail

echo [STEP 4] Check WildFly

curl -s http://localhost:8080/ >nul 2>&1

if not errorlevel 1 (
    echo WildFly already running.
    goto wildflyready
)

echo Starting WildFly...

start "WildFly" /D "%WILDFLY_BIN%" standalone.bat

for /L %%I in (1,1,60) do (

    curl -s http://localhost:8080/ >nul 2>&1

    if not errorlevel 1 (
        echo WildFly ready.
        goto wildflyready
    )

    echo Waiting for WildFly... (%%I/60)
    timeout /t 1 >nul
)

echo ERROR: WildFly did not start.
goto end

:wildflyready

echo [STEP 5] Deploy WAR

copy /Y "%WAR%" "%WILDFLY_DEPLOY%" >nul
type nul > "%WILDFLY_DEPLOY%\healthhub-1.0-SNAPSHOT.war.dodeploy"

echo.
echo ====================================
echo HealthHub deployed successfully
echo http://localhost:8080/healthhub-1.0-SNAPSHOT/
echo ====================================
echo.

goto end

:fail
echo.
echo ERROR: Deployment failed.
echo.

:end
pause
endlocal