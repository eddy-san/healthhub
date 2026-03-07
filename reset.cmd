@echo off
setlocal enabledelayedexpansion

echo [STEP 0] Load .env

for /f "usebackq tokens=1,* delims==" %%A in (".env") do (
    if "%%A"=="MSSQL_SA_PASSWORD" set MSSQL_SA_PASSWORD=%%B
)

if "%MSSQL_SA_PASSWORD%"=="" (
    echo ERROR: MSSQL_SA_PASSWORD not found in .env
    goto fail
)

echo [STEP 1] Reset SQL Server container and volumes
docker compose down -v
if errorlevel 1 goto fail

echo [STEP 2] Start SQL Server container
docker compose up -d
if errorlevel 1 goto fail

echo [STEP 3] Waiting for SQL Server

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
    goto fail
)

echo [STEP 4] Run Liquibase migrations
call .\mvnw.cmd -Pupdate-schema "-Ddb.password=%MSSQL_SA_PASSWORD%" process-resources
if errorlevel 1 goto fail

echo.
echo ====================================
echo HealthHub database reset completed
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