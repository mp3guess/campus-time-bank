#!/bin/bash

# Script to start Campus TimeBank via Docker Compose

echo "🚀 Starting Campus TimeBank..."

# Stop and remove existing containers (if any)
echo "🧹 Cleaning up old containers..."
docker-compose down

# Build and start containers
echo "🔨 Building application image..."
docker-compose build

echo "▶️  Starting services..."
docker-compose up -d

echo "⏳ Waiting for application to be ready..."
sleep 10

# Check status
echo ""
echo "📊 Container status:"
docker-compose ps

echo ""
echo "✅ Services started!"
echo "📍 API available at: http://localhost:8080/api"
echo "📍 Health check: http://localhost:8080/actuator/health"
echo ""
echo "📝 To view logs use: docker-compose logs -f app"
echo "🛑 To stop use: ./stop.sh"

