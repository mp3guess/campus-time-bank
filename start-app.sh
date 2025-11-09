#!/bin/bash

# Complete startup script for Campus TimeBank
# This script handles everything: PostgreSQL, building, and running the app

echo ""
echo "========================================"
echo "  Campus TimeBank - Complete Setup"
echo "========================================"
echo ""

# Check if Docker is available
if command -v docker &> /dev/null; then
    echo "[INFO] Docker detected. Using Docker Compose..."
    echo ""
    
    # Check if containers are already running
    if docker ps --filter "name=campus-timebank" --format "{{.Names}}" | grep -q "campus-timebank"; then
        echo "[INFO] Containers are already running!"
        echo "[INFO] API: http://localhost:8080/api"
        echo "[INFO] Health: http://localhost:8080/actuator/health"
        echo ""
        echo "To view logs: docker-compose logs -f app"
        echo "To stop: docker-compose down"
        exit 0
    fi
    
    # Start with Docker Compose
    echo "[1/3] Starting services with Docker Compose..."
    docker-compose up -d
    
    if [ $? -ne 0 ]; then
        echo "[ERROR] Failed to start Docker containers!"
        echo ""
        echo "Make sure Docker is running and try again."
        exit 1
    fi
    
    echo "[2/3] Waiting for services to be ready..."
    sleep 15
    
    echo "[3/3] Checking application health..."
    sleep 5
    if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo ""
        echo "========================================"
        echo "  SUCCESS! Application is running!"
        echo "========================================"
        echo ""
        echo "API: http://localhost:8080/api"
        echo "Health: http://localhost:8080/actuator/health"
        echo "Frontend: http://localhost:8000/index.html"
        echo ""
        echo "To view logs: docker-compose logs -f app"
        echo "To stop: docker-compose down"
        echo ""
    else
        echo "[WARNING] Application might still be starting..."
        echo "Check logs with: docker-compose logs -f app"
    fi
    
    exit 0
fi

# Docker not available, try local setup
echo "[INFO] Docker not found. Attempting local setup..."
echo ""

# Check if PostgreSQL is running
echo "[1/4] Checking PostgreSQL..."
sleep 2
if ! curl -s http://localhost:5432 > /dev/null 2>&1; then
    echo "[WARNING] PostgreSQL might not be running!"
    echo ""
    echo "Starting PostgreSQL with Docker (one-time setup)..."
    docker run -d --name postgres-timebank -p 5432:5432 \
      -e POSTGRES_PASSWORD=postgres \
      -e POSTGRES_DB=campus_timebank \
      postgres:15-alpine
    
    if [ $? -ne 0 ]; then
        echo "[ERROR] Failed to start PostgreSQL!"
        echo ""
        echo "Please install Docker or start PostgreSQL manually."
        echo ""
        exit 1
    fi
    
    echo "[INFO] Waiting for PostgreSQL to be ready..."
    sleep 10
fi

# Check if Java is available
echo "[2/4] Checking Java..."
if ! command -v java &> /dev/null; then
    echo "[ERROR] Java is not installed or not in PATH!"
    echo ""
    echo "Please install Java 17 or higher."
    echo "Download from: https://adoptium.net/"
    echo ""
    exit 1
fi

# Use Gradle Wrapper
echo "[3/4] Building application with Gradle Wrapper..."
if [ -f "./gradlew" ]; then
    chmod +x ./gradlew
    ./gradlew clean build -x test
    
    if [ $? -ne 0 ]; then
        echo "[ERROR] Build failed!"
        exit 1
    fi
    
    echo "[4/4] Starting application..."
    echo ""
    echo "========================================"
    echo "  Application is starting..."
    echo "========================================"
    echo ""
    echo "API: http://localhost:8080/api"
    echo "Health: http://localhost:8080/actuator/health"
    echo ""
    echo "Press Ctrl+C to stop the application"
    echo ""
    
    ./gradlew bootRun
else
    echo "[ERROR] Gradle Wrapper not found!"
    echo ""
    echo "Please use Docker or install Gradle manually."
    echo ""
    exit 1
fi

