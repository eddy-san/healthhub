@echo off
setlocal enabledelayedexpansion

echo [STEP 0] Load .env

for /f "usebackq tokens=1,* delims==" %%A in (".env") do (
    if "%%A"=="MSSQL_SA_PASSWORD" set MSSQL_SA_PASSWORD=%%B
)

set WILDFLY_HOME=..\..\wildfly-39.0.1.Final\wildfly-39.0.1.Final
set WILDFLY_BIN=%WILDFLY_HOME%\bin
set WILDFLY_DEPLOY=%WILDFLY_HOME%\standalone\deployments
set WAR=target\healthhub-1.0-SNAPSHOT.war

if not exist "%WILDFLY_HOME%" (
    echo ERROR: WildFly home not found: %WILDFLY_HOME%
    goto fail
)

if "%MSSQL_SA_PASSWORD%"=="" (
    echo ERROR: MSSQL_SA_PASSWORD not found in .env
    goto fail
)

echo [STEP 1] Build WAR
call .\mvnw.cmd clean package
if errorlevel 1 goto fail

echo [STEP 2] Check WildFly

curl -s http://localhost:8080/ >nul 2>&1
if not errorlevel 1 (
    echo WildFly already running.
    goto wildflyready
)

echo Starting WildFly...
start "WildFly" /D "%WILDFLY_BIN%" cmd /c "set MSSQL_SA_PASSWORD=%MSSQL_SA_PASSWORD% && standalone.bat"

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
goto fail

:wildflyready

echo [STEP 3] Clean old deployment markers
del /Q "%WILDFLY_DEPLOY%\healthhub-1.0-SNAPSHOT.war" 2>nul
del /Q "%WILDFLY_DEPLOY%\healthhub-1.0-SNAPSHOT.war.deployed" 2>nul
del /Q "%WILDFLY_DEPLOY%\healthhub-1.0-SNAPSHOT.war.failed" 2>nul
del /Q "%WILDFLY_DEPLOY%\healthhub-1.0-SNAPSHOT.war.isdeploying" 2>nul
del /Q "%WILDFLY_DEPLOY%\healthhub-1.0-SNAPSHOT.war.undeployed" 2>nul
del /Q "%WILDFLY_DEPLOY%\healthhub-1.0-SNAPSHOT.war.dodeploy" 2>nul

echo [STEP 4] Deploy WAR
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
exit /b 1

:end
pause
endlocal