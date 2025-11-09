#!/bin/bash

# Script to start Campus TimeBank via Docker Compose

echo "🚀 Starting Campus TimeBank..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running!"
    echo "Please start Docker and try again."
    exit 1
fi

# Stop and remove existing containers (if any)
echo "🧹 Cleaning up old containers..."
docker-compose down

# Build and start containers
echo "🔨 Building application image..."
docker-compose build

if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi

echo "▶️  Starting services..."
docker-compose up -d

if [ $? -ne 0 ]; then
    echo "❌ Failed to start services!"
    exit 1
fi

echo "⏳ Waiting for application to be ready..."
sleep 15

# Check status
echo ""
echo "📊 Container status:"
docker-compose ps

# Check health
echo ""
echo "🔍 Checking application health..."
sleep 5
if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "✅ Application is healthy!"
else
    echo "⚠️  Application might still be starting..."
    echo "Check logs with: docker-compose logs -f app"
fi

echo ""
echo "✅ Services started!"
echo "📍 API available at: http://localhost:8080/api"
echo "📍 Health check: http://localhost:8080/actuator/health"
echo "📍 Frontend: http://localhost:8000/index.html (run ./start-frontend.sh)"
echo ""
echo "📝 To view logs use: docker-compose logs -f app"
echo "🛑 To stop use: ./stop.sh"
echo ""
echo "🌐 To start frontend, run: ./start-frontend.sh"

