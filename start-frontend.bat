@echo off
REM Start frontend HTTP server

echo.
echo ========================================
echo   Starting Frontend Server
echo ========================================
echo.

REM Check if Python is available
where python >nul 2>&1
if %errorlevel% equ 0 (
    echo [INFO] Starting HTTP server on port 8000...
    echo.
    echo Frontend will be available at: http://localhost:8000/index.html
    echo.
    echo Press Ctrl+C to stop the server
    echo.
    python -m http.server 8000
    exit /b 0
)

REM Try python3
where python3 >nul 2>&1
if %errorlevel% equ 0 (
    echo [INFO] Starting HTTP server on port 8000...
    echo.
    echo Frontend will be available at: http://localhost:8000/index.html
    echo.
    echo Press Ctrl+C to stop the server
    echo.
    python3 -m http.server 8000
    exit /b 0
)

echo [ERROR] Python is not installed or not in PATH!
echo.
echo Please install Python 3 from: https://www.python.org/downloads/
echo.
pause
exit /b 1

