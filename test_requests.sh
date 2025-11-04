#!/bin/bash

BASE_URL="http://localhost:8080/api"

echo "======================================"
echo "Testing Campus TimeBank API"
echo "======================================"
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Function to check response
check_response() {
    local response="$1"
    local expected_code="$2"
    local description="$3"
    
    local http_code=$(echo "$response" | grep -o "HTTP_CODE:[0-9]*" | cut -d: -f2)
    local body=$(echo "$response" | sed '/HTTP_CODE:/d')
    
    if [ "$http_code" == "$expected_code" ]; then
        echo -e "${GREEN}✓ $description (HTTP $http_code)${NC}"
        return 0
    else
        echo -e "${RED}✗ $description (expected $expected_code, got $http_code)${NC}"
        echo "Response: $body" | head -3
        return 1
    fi
}

# Test 1: Register user A
echo "1. Registering user A..."
RESPONSE_A=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST $BASE_URL/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"demojohn@example.com","password":"password123","firstName":"John","lastName":"Doe","faculty":"Computer Science","studentId":"DE123456"}')

if check_response "$RESPONSE_A" "201" "Registering user A"; then
    TOKEN_A=$(echo "$RESPONSE_A" | python3 -c "import sys, json; print(json.load(sys.stdin)['token'])" 2>/dev/null || echo "$RESPONSE_A" | grep -o '"token":"[^"]*' | cut -d'"' -f4)
    USER_ID_A=$(echo "$RESPONSE_A" | python3 -c "import sys, json; print(json.load(sys.stdin)['user']['id'])" 2>/dev/null || echo "")
    echo "  Token: ${TOKEN_A:0:50}..."
    echo "  User ID: $USER_ID_A"
else
    TOKEN_A=""
    USER_ID_A=""
fi
echo ""

# Test 2: Register user B
echo "2. Registering user B..."
RESPONSE_B=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST $BASE_URL/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"demomaria@example.com","password":"password123","firstName":"Maria","lastName":"Smith","faculty":"Mathematics","studentId":"DE789012"}')

if check_response "$RESPONSE_B" "201" "Registering user B"; then
    TOKEN_B=$(echo "$RESPONSE_B" | python3 -c "import sys, json; print(json.load(sys.stdin)['token'])" 2>/dev/null || echo "$RESPONSE_B" | grep -o '"token":"[^"]*' | cut -d'"' -f4)
    USER_ID_B=$(echo "$RESPONSE_B" | python3 -c "import sys, json; print(json.load(sys.stdin)['user']['id'])" 2>/dev/null || echo "")
    echo "  Token: ${TOKEN_B:0:50}..."
    echo "  User ID: $USER_ID_B"
else
    TOKEN_B=""
    USER_ID_B=""
fi
echo ""

# Test 3: Get current user
if [ -n "$TOKEN_A" ]; then
    echo "3. Getting current user info..."
    RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET $BASE_URL/users/me \
      -H "Authorization: Bearer $TOKEN_A")
    check_response "$RESPONSE" "200" "Getting current user info"
else
    echo -e "${YELLOW}3. Skipped (no token)${NC}"
fi
echo ""

# Test 4: Create offer by user A
if [ -n "$TOKEN_A" ]; then
    echo "4. Creating offer by user A..."
    RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST $BASE_URL/offers \
      -H "Authorization: Bearer $TOKEN_A" \
      -H "Content-Type: application/json" \
      -d '{"title":"Java Programming Tutoring","description":"Help with Java, Spring Boot, databases","hoursRate":2.5}')
    
    if check_response "$RESPONSE" "201" "Creating offer"; then
        OFFER_ID_A=$(echo "$RESPONSE" | python3 -c "import sys, json; print(json.load(sys.stdin)['id'])" 2>/dev/null || echo "$RESPONSE" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
        echo "  Offer ID: $OFFER_ID_A"
    else
        OFFER_ID_A=""
    fi
else
    echo -e "${YELLOW}4. Skipped (no token)${NC}"
    OFFER_ID_A=""
fi
echo ""

# Test 5: View active offers
echo "5. Viewing active offers..."
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET "$BASE_URL/offers/active/list?page=0&size=10")
check_response "$RESPONSE" "200" "Viewing active offers"
echo ""

# Test 6: Create booking by user B
if [ -n "$TOKEN_B" ] && [ -n "$OFFER_ID_A" ]; then
    echo "6. Creating booking (B requests service from A)..."
    RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST $BASE_URL/bookings \
      -H "Authorization: Bearer $TOKEN_B" \
      -H "Content-Type: application/json" \
      -d "{\"offerId\":$OFFER_ID_A,\"hours\":3.0}")
    
    if check_response "$RESPONSE" "201" "Creating booking"; then
        BOOKING_ID=$(echo "$RESPONSE" | python3 -c "import sys, json; print(json.load(sys.stdin)['id'])" 2>/dev/null || echo "$RESPONSE" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
        echo "  Booking ID: $BOOKING_ID"
    else
        BOOKING_ID=""
    fi
else
    echo -e "${YELLOW}6. Skipped (no token or offer ID)${NC}"
    BOOKING_ID=""
fi
echo ""

# Test 7: Confirm booking by user A
if [ -n "$TOKEN_A" ] && [ -n "$BOOKING_ID" ]; then
    echo "7. Confirming booking by offer owner..."
    RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X PUT $BASE_URL/bookings/$BOOKING_ID/confirm \
      -H "Authorization: Bearer $TOKEN_A")
    check_response "$RESPONSE" "200" "Confirming booking"
else
    echo -e "${YELLOW}7. Skipped (no token or booking ID)${NC}"
fi
echo ""

# Test 8: Complete booking
if [ -n "$TOKEN_A" ] && [ -n "$BOOKING_ID" ]; then
    echo "8. Completing booking..."
    RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X PUT $BASE_URL/bookings/$BOOKING_ID/complete \
      -H "Authorization: Bearer $TOKEN_A")
    check_response "$RESPONSE" "200" "Completing booking"
else
    echo -e "${YELLOW}8. Skipped (no token or booking ID)${NC}"
fi
echo ""

# Test 9: Check balances
if [ -n "$TOKEN_A" ]; then
    echo "9. Checking user A balance..."
    RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET $BASE_URL/users/me \
      -H "Authorization: Bearer $TOKEN_A")
    check_response "$RESPONSE" "200" "Checking user A balance"
    if [ $? -eq 0 ]; then
        echo "$RESPONSE" | python3 -c "import sys, json; data=json.load(sys.stdin); print('  Balance:', data.get('wallet', {}).get('balance', 'N/A'))" 2>/dev/null || echo ""
    fi
fi

if [ -n "$TOKEN_B" ]; then
    echo "10. Checking user B balance..."
    RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET $BASE_URL/users/me \
      -H "Authorization: Bearer $TOKEN_B")
    check_response "$RESPONSE" "200" "Checking user B balance"
    if [ $? -eq 0 ]; then
        echo "$RESPONSE" | python3 -c "import sys, json; data=json.load(sys.stdin); print('  Balance:', data.get('wallet', {}).get('balance', 'N/A'))" 2>/dev/null || echo ""
    fi
fi
echo ""

# Test 11: Error handling - access without token
echo "11. Attempting to access without token..."
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET $BASE_URL/users/me)
check_response "$RESPONSE" "401" "Access without token (should be 401)"
echo ""

# Test 12: Validation - invalid email
echo "12. Validation - registration with invalid email..."
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST $BASE_URL/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"invalid-email","password":"123","firstName":"Test","lastName":"User"}')
check_response "$RESPONSE" "400" "Registration with invalid email (should be 400)"
echo ""

echo "======================================"
echo "Testing completed!"
echo "======================================"
