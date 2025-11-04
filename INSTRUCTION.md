# Campus TimeBank Setup Instructions for Presentation

## Quick Start

### Step 1: Environment Setup

**Requirements:**
- Docker Desktop installed and running
- Docker Compose installed
- Terminal (Terminal, iTerm, etc.)

**Check:**
```bash
docker --version
docker-compose --version
```

If commands are not found, install Docker Desktop from the official website.

---

## Starting the Application

### Option 1: Using Ready Scripts (Recommended)

#### 1. Start Services

```bash
# Go to project directory
cd /Users/ivantamrazov/Desktop/zh/campus-time-bank/campus-time-bank

# Run startup script
./start.sh
```

**What the script does:**
- Stops existing containers (if any)
- Builds Docker image for the application
- Starts PostgreSQL and Spring Boot application
- Applies database migrations
- Shows container status

**Expected time:** 1-3 minutes

#### 2. Check Functionality

After startup, wait 10-15 seconds and check status:

```bash
# Check container status
docker-compose ps

# Check health endpoint
curl http://localhost:8080/actuator/health
```

**Expected result:**
- Both containers in "Up" and "healthy" status
- Health endpoint returns: `{"status":"UP"}`

#### 3. View Logs (Optional)

If you need to see what's happening with the application:

```bash
# Real-time logs
docker-compose logs -f app

# Last 50 lines of logs
docker-compose logs --tail=50 app
```

---

### Option 2: Manual Startup via Docker Compose

If you want to start manually:

```bash
# Stop and cleanup (if needed)
docker-compose down -v

# Build image
docker-compose build

# Start services in background
docker-compose up -d

# Check status
docker-compose ps
```

---

## Executing Requests for Presentation

### Option 1: Automatic Script (Fast and Convenient)

```bash
# Run all requests sequentially
bash PRESENTATION_REQUESTS.sh
```

**What the script does:**
- Registers two users
- Creates offers
- Creates and processes bookings
- Checks balances
- Demonstrates error handling

**Note:** The script automatically checks service availability and handles errors.

---

### Option 2: Manual Command Execution (For Step-by-Step Demonstration)

#### 1. Register User A

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email":"john@example.com",
    "password":"password123",
    "firstName":"John",
    "lastName":"Doe",
    "faculty":"Computer Science",
    "studentId":"DE123456"
  }'
```

**Save the token** from the response (field `token`). For convenience:

```bash
TOKEN_A=$(curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"password123","firstName":"John","lastName":"Doe","faculty":"Computer Science","studentId":"DE123456"}' \
  | python3 -c "import sys, json; print(json.load(sys.stdin)['token'])" 2>/dev/null)

echo "Token saved: ${TOKEN_A:0:50}..."
```

#### 2. Register User B

```bash
TOKEN_B=$(curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"maria@example.com","password":"password123","firstName":"Maria","lastName":"Smith","faculty":"Mathematics","studentId":"DE789012"}' \
  | python3 -c "import sys, json; print(json.load(sys.stdin)['token'])" 2>/dev/null)
```

#### 3. Create Offer by User A

```bash
curl -X POST http://localhost:8080/api/offers \
  -H "Authorization: Bearer $TOKEN_A" \
  -H "Content-Type: application/json" \
  -d '{
    "title":"Java Programming Tutoring",
    "description":"Help with Java, Spring Boot, databases",
    "hoursRate":2.5
  }'
```

**Save the offer ID** from the response.

#### 4. View Active Offers

```bash
curl -X GET "http://localhost:8080/api/offers/active/list?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN_B"
```

#### 5. Create Booking by User B

```bash
# Replace OFFER_ID_A with actual offer ID
curl -X POST http://localhost:8080/api/bookings \
  -H "Authorization: Bearer $TOKEN_B" \
  -H "Content-Type: application/json" \
  -d '{
    "offerId":1,
    "hours":3.0
  }'
```

#### 6. Confirm Booking by User A

```bash
# Replace BOOKING_ID with actual booking ID
curl -X PUT http://localhost:8080/api/bookings/1/confirm \
  -H "Authorization: Bearer $TOKEN_A"
```

#### 7. Complete Booking by User A

```bash
curl -X PUT http://localhost:8080/api/bookings/1/complete \
  -H "Authorization: Bearer $TOKEN_A"
```

#### 8. Check Balances

```bash
# User A balance
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer $TOKEN_A"

# User B balance
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer $TOKEN_B"
```

---

## Useful Commands

### View Container Status

```bash
docker-compose ps
```

### View Logs

```bash
# Application logs in real-time
docker-compose logs -f app

# Database logs
docker-compose logs -f postgres

# Last 100 lines of application logs
docker-compose logs --tail=100 app
```

### Restart Application

```bash
# Restart only application
docker-compose restart app

# Full reload
docker-compose down
docker-compose up -d
```

### Check Database Connection

```bash
# Connect to PostgreSQL (password: postgres)
docker exec -it campus-timebank-db psql -U postgres -d campus_timebank
```

---

## Stopping Services

### Option 1: Using Script

```bash
./stop.sh
```

### Option 2: Manual Stop

```bash
# Stop containers
docker-compose down

# Stop and remove volumes (database cleanup)
docker-compose down -v
```

---

## Troubleshooting

### Problem: Docker Daemon Not Running

**Error:** `Cannot connect to the Docker daemon`

**Solution:**
1. Start Docker Desktop
2. Wait for full startup (Docker icon should be active in tray)
3. Try again: `docker ps`

---

### Problem: Port 8080 Already in Use

**Error:** `Bind for 0.0.0.0:8080 failed: port is already allocated`

**Solution:**
```bash
# Find process using port 8080
lsof -i :8080

# Or change port in docker-compose.yml
# In docker-compose.yml change "8080:8080" to "8081:8080"
```

---

### Problem: Container Not Starting or Crashing

**Solution:**
```bash
# View logs for diagnostics
docker-compose logs app

# Check container status
docker-compose ps

# Try rebuilding image
docker-compose build --no-cache
docker-compose up -d
```

---

### Problem: Requests Return 403 Forbidden

**Reason:** Authentication required for all endpoints except `/api/auth/**`

**Solution:** Use JWT token in header:
```bash
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

### Problem: Health Endpoint Not Responding

**Solution:**
```bash
# Check that application has started completely
docker-compose logs app | grep "Started CampusTimeBankApplication"

# Wait another 10-15 seconds after startup
sleep 15
curl http://localhost:8080/actuator/health
```

---

## Additional Tools

### Formatting JSON Responses

If you have `jq` installed:
```bash
curl ... | jq '.'
```

If `jq` is not installed, use `python3`:
```bash
curl ... | python3 -m json.tool
```

### Saving Responses to File

```bash
# Save JSON response to file
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer $TOKEN_A" \
  > response.json
```

---

## Recommended Order of Actions for Presentation

1. **Preparation (5 minutes before start):**
   ```bash
   ./start.sh
   # Wait 30 seconds
   curl http://localhost:8080/actuator/health
   ```

2. **Demonstration:**
   - Option A: Use `bash PRESENTATION_REQUESTS.sh` for automatic demonstration
   - Option B: Execute commands manually from `PRESENTATION_GUIDE.md` for step-by-step explanation

3. **After Presentation:**
   ```bash
   ./stop.sh
   ```

---

## Project File Structure

- `start.sh` - service startup script
- `stop.sh` - service shutdown script
- `PRESENTATION_REQUESTS.sh` - automatic curl requests for demonstration
- `PRESENTATION_GUIDE.md` - detailed presentation guide
- `PRESENTATION_CASES.md` - list of all test cases
- `INSTRUCTION.md` - this instruction
- `docker-compose.yml` - Docker Compose configuration
- `Dockerfile.simple` - simplified Dockerfile (uses ready jar)

---

## Quick Reference

```bash
# Start
./start.sh

# Check
curl http://localhost:8080/actuator/health

# Demonstration
bash PRESENTATION_REQUESTS.sh

# Stop
./stop.sh
```

---

**Good luck with the presentation! 🚀**
