@echo off
REM Script to start Campus TimeBank locally (without Docker)

echo.
echo ========================================
echo   Campus TimeBank - Local Startup
echo ========================================
echo.

REM Check if PostgreSQL is running
echo [1/4] Checking PostgreSQL connection...
timeout /t 2 /nobreak >nul
curl -s http://localhost:5432 >nul 2>&1
if %errorlevel% neq 0 (
    echo.
    echo WARNING: PostgreSQL might not be running!
    echo Please make sure PostgreSQL is running on localhost:5432
    echo.
    echo You can start PostgreSQL with Docker:
    echo   docker run -d --name postgres -p 5432:5432 -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=campus_timebank postgres:15-alpine
    echo.
    pause
)

REM Check if Gradle is installed
echo [2/4] Checking Gradle...
where gradle >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Gradle is not installed or not in PATH
    echo Please install Gradle or use Docker (./start.sh)
    pause
    exit /b 1
)

REM Build the project
echo [3/4] Building application...
call gradle clean build -x test
if %errorlevel% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

REM Run the application
echo [4/4] Starting application...
echo.
echo Application will be available at: http://localhost:8080
echo API endpoint: http://localhost:8080/api
echo Health check: http://localhost:8080/actuator/health
echo.
echo Press Ctrl+C to stop the application
echo.

call gradle bootRun

