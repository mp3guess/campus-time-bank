@echo off
REM Complete startup script for Campus TimeBank
REM This script handles everything: PostgreSQL, building, and running the app

echo.
echo ========================================
echo   Campus TimeBank - Complete Setup
echo ========================================
echo.

REM Check if Docker is available
where docker >nul 2>&1
if %errorlevel% equ 0 (
    echo [INFO] Docker detected. Using Docker Compose...
    echo.
    
    REM Check if containers are already running
    docker ps --filter "name=campus-timebank" --format "{{.Names}}" | findstr /C:"campus-timebank" >nul 2>&1
    if %errorlevel% equ 0 (
        echo [INFO] Containers are already running!
        echo [INFO] API: http://localhost:8080/api
        echo [INFO] Health: http://localhost:8080/actuator/health
        echo.
        echo To view logs: docker-compose logs -f app
        echo To stop: docker-compose down
        pause
        exit /b 0
    )
    
    REM Start with Docker Compose
    echo [1/3] Starting services with Docker Compose...
    docker-compose up -d
    
    if %errorlevel% neq 0 (
        echo [ERROR] Failed to start Docker containers!
        echo.
        echo Make sure Docker is running and try again.
        pause
        exit /b 1
    )
    
    echo [2/3] Waiting for services to be ready...
    timeout /t 15 /nobreak >nul
    
    echo [3/3] Checking application health...
    timeout /t 5 /nobreak >nul
    curl -s http://localhost:8080/actuator/health >nul 2>&1
    if %errorlevel% equ 0 (
        echo.
        echo ========================================
        echo   SUCCESS! Application is running!
        echo ========================================
        echo.
        echo API: http://localhost:8080/api
        echo Health: http://localhost:8080/actuator/health
        echo Frontend: http://localhost:8000/index.html
        echo.
        echo To view logs: docker-compose logs -f app
        echo To stop: docker-compose down
        echo.
    ) else (
        echo [WARNING] Application might still be starting...
        echo Check logs with: docker-compose logs -f app
    )
    
    pause
    exit /b 0
)

REM Docker not available, try local setup
echo [INFO] Docker not found. Attempting local setup...
echo.

REM Check if PostgreSQL is running
echo [1/4] Checking PostgreSQL...
echo [INFO] Make sure PostgreSQL is running on localhost:5432
echo [INFO] Database: campus_timebank, User: postgres, Password: postgres
echo.

REM Check if Java is available
echo [2/4] Checking Java...
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java is not installed or not in PATH!
    echo.
    echo Please install Java 17 or higher.
    echo Download from: https://adoptium.net/
    echo.
    pause
    exit /b 1
)
echo [INFO] Java found.

REM Use Gradle Wrapper
echo [3/4] Building application with Gradle Wrapper...
if exist gradlew.bat (
    echo [INFO] Using Gradle Wrapper...
    call gradlew.bat clean build -x test
    if errorlevel 1 (
        echo [ERROR] Build failed!
        pause
        exit /b 1
    )
    
    echo [4/4] Starting application...
    echo.
    echo ========================================
    echo   Application is starting...
    echo ========================================
    echo.
    echo API: http://localhost:8080/api
    echo Health: http://localhost:8080/actuator/health
    echo.
    echo Press Ctrl+C to stop the application
    echo.
    
    call gradlew.bat bootRun
) else (
    echo [ERROR] Gradle Wrapper not found!
    echo.
    echo Please use Docker or install Gradle manually.
    echo.
    pause
    exit /b 1
)

