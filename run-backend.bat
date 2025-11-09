@echo off
REM Simple script to run backend with Gradle

echo.
echo ========================================
echo   Starting Campus TimeBank Backend
echo ========================================
echo.

cd /d "%~dp0"

echo [INFO] Make sure PostgreSQL is running on localhost:5432
echo [INFO] Database: campus_timebank
echo [INFO] User: postgres
echo [INFO] Password: postgres
echo.

REM Check if Gradle Wrapper exists
if not exist "gradlew.bat" (
    echo [ERROR] Gradle Wrapper not found!
    echo.
    echo Please make sure you're in the project directory.
    pause
    exit /b 1
)

echo [INFO] Building and starting application...
echo [INFO] This may take a few minutes on first run...
echo.

call gradlew.bat bootRun

if errorlevel 1 (
    echo.
    echo [ERROR] Failed to start application!
    echo.
    echo Common issues:
    echo   1. PostgreSQL is not running
    echo   2. Database 'campus_timebank' does not exist
    echo   3. Java is not installed
    echo.
    pause
)

