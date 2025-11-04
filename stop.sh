#!/bin/bash

# Script to stop Campus TimeBank

echo "🛑 Stopping Campus TimeBank..."

docker-compose down

echo "✅ Services stopped!"
echo ""
echo "💡 For full cleanup (including volumes) use: docker-compose down -v"

