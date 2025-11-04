# Campus TimeBank Presentation Guide

## Quick Start

### Step 1: Start Application

```bash
# Start all services via Docker
./start.sh
```

**What happens:**
- PostgreSQL database starts
- Spring Boot application builds and starts
- Database migrations are applied
- Services become available in a few seconds

**Expected time:** 1-2 minutes

### Step 2: Check Functionality

```bash
# Check container status
docker-compose ps

# Check health endpoint
curl http://localhost:8080/actuator/health
```

**Expected result:**
```json
{"status":"UP"}
```

---

## Presentation Structure

### Introduction (2 minutes)

**What to say:**
- "Campus TimeBank is a time-based service exchange platform"
- "Students can offer their services (tutoring, project help, etc.)"
- "Payment happens in time hours, not money"
- "Upon registration, each user receives an initial balance of 10 hours"

**What to show:**
- Project architecture (Docker Compose)
- List of available API endpoints

---

## Main Demonstration Scenarios

### Scenario 1: Registration and Authentication (3 minutes)

**Goal:** Show the registration and login process

#### 1.1 Register First User

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

**What to note:**
- Response status: 201 Created
- JWT token is returned
- User automatically receives wallet with balance of 10.00

**Saving token:**
```bash
TOKEN_A=$(curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"password123","firstName":"John","lastName":"Doe","faculty":"Computer Science","studentId":"DE123456"}' \
  | grep -o '"token":"[^"]*' | cut -d'"' -f4)
```

#### 1.2 Register Second User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email":"maria@example.com",
    "password":"password123",
    "firstName":"Maria",
    "lastName":"Smith",
    "faculty":"Mathematics",
    "studentId":"DE789012"
  }'
```

**Saving token:**
```bash
TOKEN_B=$(curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"maria@example.com","password":"password123","firstName":"Maria","lastName":"Smith","faculty":"Mathematics","studentId":"DE789012"}' \
  | grep -o '"token":"[^"]*' | cut -d'"' -f4)
```

#### 1.3 Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email":"john@example.com",
    "password":"password123"
  }'
```

**What to note:**
- New JWT token is returned
- Token is used for authentication in subsequent requests

---

### Scenario 2: Offer Management (5 minutes)

**Goal:** Show creating and managing service offers

#### 2.1 Create Offer

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

**What to explain:**
- `hoursRate` is the rate in hours (how many hours to pay per hour of service)
- Offer automatically receives ACTIVE status
- Offer is visible to all users

**Saving offer ID:**
```bash
OFFER_ID_A=$(curl -s -X POST http://localhost:8080/api/offers \
  -H "Authorization: Bearer $TOKEN_A" \
  -H "Content-Type: application/json" \
  -d '{"title":"Java Programming Tutoring","description":"Help with Java, Spring Boot, databases","hoursRate":2.5}' \
  | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
```

#### 2.2 View Active Offers

```bash
curl -X GET "http://localhost:8080/api/offers/active/list?page=0&size=10"
```

**What to show:**
- List of all active offers
- Pagination of results
- Information about owner of each offer

#### 2.3 Update Offer

```bash
curl -X PUT http://localhost:8080/api/offers/$OFFER_ID_A \
  -H "Authorization: Bearer $TOKEN_A" \
  -H "Content-Type: application/json" \
  -d '{
    "title":"Java Programming Tutoring (Updated)",
    "description":"Help with Java, Spring Boot, databases. 3 years of experience.",
    "hoursRate":3.0
  }'
```

#### 2.4 Deactivate/Activate Offer

```bash
# Deactivate
curl -X PUT http://localhost:8080/api/offers/$OFFER_ID_A/deactivate \
  -H "Authorization: Bearer $TOKEN_A"

# Activate
curl -X PUT http://localhost:8080/api/offers/$OFFER_ID_A/activate \
  -H "Authorization: Bearer $TOKEN_A"
```

**What to explain:**
- Deactivated offer is not visible in active list
- It can be activated back
- Cannot create bookings for inactive offers

---

### Scenario 3: Booking Lifecycle (7 minutes)

**Goal:** Show complete process from creation to completion of booking

#### 3.1 Create Booking

```bash
curl -X POST http://localhost:8080/api/bookings \
  -H "Authorization: Bearer $TOKEN_B" \
  -H "Content-Type: application/json" \
  -d '{
    "offerId":'$OFFER_ID_A',
    "hours":3.0
  }'
```

**What to explain:**
- User B requests service from user A
- Booking is created with PENDING status
- Total amount = hours × hoursRate = 3.0 × 2.5 = 7.5 hours

**Saving booking ID:**
```bash
BOOKING_ID=$(curl -s -X POST http://localhost:8080/api/bookings \
  -H "Authorization: Bearer $TOKEN_B" \
  -H "Content-Type: application/json" \
  -d '{"offerId":'$OFFER_ID_A',"hours":3.0}' \
  | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
```

#### 3.2 Confirm Booking

```bash
curl -X PUT http://localhost:8080/api/bookings/$BOOKING_ID/confirm \
  -H "Authorization: Bearer $TOKEN_A"
```

**What to explain:**
- Only offer owner can confirm booking
- Upon confirmation, status changes to CONFIRMED
- Hours are reserved for requester (not deducted, only reserved)

#### 3.3 Complete Booking

```bash
curl -X PUT http://localhost:8080/api/bookings/$BOOKING_ID/complete \
  -H "Authorization: Bearer $TOKEN_A"
```

**What to explain:**
- After completion, status changes to COMPLETED
- Transaction occurs: hours are transferred from requester to offer owner
- 7.5 hours are deducted from user B and added to user A

#### 3.4 View Booking History

```bash
# Bookings where user was requester
curl -X GET "http://localhost:8080/api/bookings/my/as-requester?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN_B"

# Bookings where user was offer owner
curl -X GET "http://localhost:8080/api/bookings/my/as-owner?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN_A"
```

#### 3.5 Check Balances

```bash
# Check user A balance
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer $TOKEN_A"

# Check user B balance
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer $TOKEN_B"
```

**What to show:**
- User A balance increased (was 10.00, now ~17.50)
- User B balance decreased (was 10.00, now ~2.50)

---

### Scenario 4: Error Handling (3 minutes)

**Goal:** Show validation and error handling system

#### 4.1 Registration Validation

```bash
# Attempt registration with invalid email
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email":"invalid-email",
    "password":"123",
    "firstName":"Test",
    "lastName":"User"
  }'
```

**Expected result:** Status 400 Bad Request with error descriptions

#### 4.2 Attempt to Access Without Authentication

```bash
curl -X GET http://localhost:8080/api/users/me
```

**Expected result:** Status 401 Unauthorized

#### 4.3 Attempt to Create Booking for Own Offer

```bash
curl -X POST http://localhost:8080/api/bookings \
  -H "Authorization: Bearer $TOKEN_A" \
  -H "Content-Type: application/json" \
  -d '{
    "offerId":'$OFFER_ID_A',
    "hours":1.0
  }'
```

**Expected result:** Status 400 Bad Request (cannot book own offer)

#### 4.4 Attempt to Create Booking with Insufficient Balance

```bash
# If user doesn't have enough hours
curl -X POST http://localhost:8080/api/bookings \
  -H "Authorization: Bearer $TOKEN_B" \
  -H "Content-Type: application/json" \
  -d '{
    "offerId":'$OFFER_ID_A',
    "hours":100.0
  }'
```

**Expected result:** Status 400 Bad Request with message about insufficient balance

---

## Alternative Method: Using Ready Script

For quick demonstration, you can use the ready script:

```bash
# Run all requests sequentially
bash PRESENTATION_REQUESTS.sh
```

**Advantages:**
- All requests are executed automatically
- Tokens are saved in variables
- Results of each request are shown

**Disadvantages:**
- Less control over presentation pace
- Harder to explain each step

**Recommendation:** Use script for preparation and checking, but during presentation execute commands manually for better control.

---

## Useful Commands for Presentation

### View Application Logs

```bash
# Real-time logs
docker-compose logs -f app

# Last 100 lines of logs
docker-compose logs --tail=100 app
```

### Check Container Status

```bash
docker-compose ps
```

### Restart Application

```bash
docker-compose restart app
```

### Stop All Services

```bash
./stop.sh
```

### Full Cleanup (Including Database)

```bash
docker-compose down -v
```

---

## Frequently Asked Questions During Presentation

### Q: How does the transaction system work?

**A:** When booking is confirmed, hours are reserved (frozen) for the requester. When booking is completed, hours are transferred from requester to offer owner. If booking is canceled, reserved hours are returned.

### Q: What happens to balance when creating a booking?

**A:** Nothing happens until confirmation. Upon confirmation, hours are reserved (available balance decreases). Upon completion - transferred finally. Upon cancellation - returned.

### Q: Can we create multiple bookings for one offer?

**A:** Yes, you can. An offer can have multiple active bookings simultaneously.

### Q: How does the role system work?

**A:** By default, all users have STUDENT role. There is also ADMIN role (for future, for administrative functions).

### Q: Can we change the rate (hoursRate) after creating an offer?

**A:** Yes, through the offer update endpoint. This will not affect already created bookings.

---

## Recommended Presentation Timing

1. **Introduction** - 2 minutes
2. **Registration and Authentication** - 3 minutes
3. **Offer Management** - 5 minutes
4. **Booking Lifecycle** - 7 minutes
5. **Error Handling** - 3 minutes
6. **Questions and Answers** - 5 minutes

**Total time:** ~25 minutes

---

## Preparation Before Presentation

1. ✅ Make sure Docker is installed and running
2. ✅ Test startup `./start.sh`
3. ✅ Check that all endpoints are available
4. ✅ Prepare data examples for demonstration
5. ✅ Check script `PRESENTATION_REQUESTS.sh` works
6. ✅ Prepare answers to possible questions

---

## File Structure for Presentation

- `PRESENTATION_CASES.md` - detailed description of all test cases
- `PRESENTATION_REQUESTS.sh` - ready curl requests for execution
- `PRESENTATION_GUIDE.md` - this guide
- `start.sh` / `stop.sh` - scripts for Docker management

---

## Additional Tips

1. **Use jq for JSON formatting:**
   ```bash
   curl ... | jq '.'
   ```
   If jq is not installed, install: `brew install jq` (macOS) or `apt-get install jq` (Linux)

2. **Save tokens in variables:**
   ```bash
   TOKEN=$(curl ... | grep -o '"token":"[^"]*' | cut -d'"' -f4)
   ```

3. **Use colored output:**
   The script `PRESENTATION_REQUESTS.sh` already uses colored output for better visibility.

4. **Prepare examples in advance:**
   Execute all requests in advance to know what IDs will be created, and prepare sequence for demonstration.

---

Good luck with the presentation! 🚀
