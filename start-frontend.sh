#!/bin/bash

# Start frontend HTTP server

echo ""
echo "========================================"
echo "  Starting Frontend Server"
echo "========================================"
echo ""

# Check if Python is available
if command -v python3 &> /dev/null; then
    echo "[INFO] Starting HTTP server on port 8000..."
    echo ""
    echo "Frontend will be available at: http://localhost:8000/index.html"
    echo ""
    echo "Press Ctrl+C to stop the server"
    echo ""
    python3 -m http.server 8000
    exit 0
fi

if command -v python &> /dev/null; then
    echo "[INFO] Starting HTTP server on port 8000..."
    echo ""
    echo "Frontend will be available at: http://localhost:8000/index.html"
    echo ""
    echo "Press Ctrl+C to stop the server"
    echo ""
    python -m http.server 8000
    exit 0
fi

echo "[ERROR] Python is not installed or not in PATH!"
echo ""
echo "Please install Python 3 from: https://www.python.org/downloads/"
echo ""
exit 1

