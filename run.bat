@echo off
REM Quick run script - starts the application using Gradle

echo Starting Campus TimeBank...
echo.

REM Check if Gradle is available
where gradle >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Gradle is not installed!
    echo.
    echo Options:
    echo   1. Install Gradle: https://gradle.org/install/
    echo   2. Use Docker: bash start.sh (requires Git Bash or WSL)
    echo   3. Use IntelliJ IDEA or Eclipse to run the application
    echo.
    pause
    exit /b 1
)

REM Run the application
gradle bootRun

