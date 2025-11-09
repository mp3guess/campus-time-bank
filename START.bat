@echo off
REM Quick start script - opens two terminals and starts backend and frontend

echo.
echo ========================================
echo   Campus TimeBank - Quick Start
echo ========================================
echo.

REM Get the current directory
set PROJECT_DIR=%~dp0
cd /d "%PROJECT_DIR%"

echo [INFO] Project directory: %PROJECT_DIR%
echo.

REM Check if we're in the right directory
if not exist "start-app.bat" (
    echo [ERROR] start-app.bat not found!
    echo [ERROR] Please run this script from the project root directory.
    pause
    exit /b 1
)

echo [1/2] Starting backend in new window...
start "Campus TimeBank - Backend" cmd /k "cd /d %PROJECT_DIR% && start-app.bat"

echo [INFO] Waiting 5 seconds for backend to start...
timeout /t 5 /nobreak >nul

echo [2/2] Starting frontend in new window...
start "Campus TimeBank - Frontend" cmd /k "cd /d %PROJECT_DIR% && start-frontend.bat"

echo.
echo ========================================
echo   SUCCESS!
echo ========================================
echo.
echo Two windows have been opened:
echo   1. Backend - Campus TimeBank Backend
echo   2. Frontend - Campus TimeBank Frontend
echo.
echo Wait for backend to start (usually 20-30 seconds)
echo Then open in browser: http://localhost:8000/index.html
echo.
echo Press any key to close this window...
pause >nul

