#!/bin/bash

# =============================================================================
# Script with ready-to-use curl requests for Campus TimeBank presentation
# =============================================================================
# 
# Usage:
#   1. Start the application via Docker: ./start.sh
#   2. Execute commands in order (copy and paste into terminal)
#   3. Or run the entire script: bash PRESENTATION_REQUESTS.sh
#
# =============================================================================

BASE_URL="http://localhost:8080/api"

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to format JSON (check for jq or python)
format_json() {
    local json="$1"
    if command -v jq &> /dev/null; then
        echo "$json" | jq '.'
    elif command -v python3 &> /dev/null; then
        echo "$json" | python3 -m json.tool 2>/dev/null || echo "$json"
    else
        echo "$json"
    fi
}

# Function to extract ID from JSON response
extract_id() {
    local json="$1"
    if command -v jq &> /dev/null; then
        # First try to get .id, if not - then .user.id
        echo "$json" | jq -r '.id // .user.id // empty'
    else
        # Without jq, first search for "id": in root, then in user
        local id=$(echo "$json" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
        if [ -z "$id" ]; then
            # Try to find in user object
            id=$(echo "$json" | grep -o '"user".*"id":[0-9]*' | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
        fi
        echo "$id"
    fi
}

# Function to extract token from JSON response
extract_token() {
    local json="$1"
    if command -v jq &> /dev/null; then
        echo "$json" | jq -r '.token // empty'
    else
        echo "$json" | grep -o '"token":"[^"]*' | cut -d'"' -f4
    fi
}

# Function to check service availability
check_service() {
    local response=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/../actuator/health" 2>/dev/null)
    if [ "$response" != "200" ]; then
        echo -e "${RED}ERROR: Service unavailable at $BASE_URL${NC}"
        echo -e "${YELLOW}Make sure the application is running: ./start.sh${NC}"
        exit 1
    fi
}

echo -e "${BLUE}=== Campus TimeBank - Presentation Script ===${NC}\n"

# Check service availability
echo -e "${YELLOW}Checking service availability...${NC}"
check_service
echo -e "${GREEN}✓ Service available${NC}\n"

# Generate unique emails for this run
TIMESTAMP=$(date +%s)
EMAIL_A="john${TIMESTAMP}@example.com"
EMAIL_B="maria${TIMESTAMP}@example.com"

# =============================================================================
# SECTION 1: REGISTRATION AND AUTHENTICATION
# =============================================================================

echo -e "${YELLOW}--- Section 1: Registration and Authentication ---${NC}\n"

# 1.1 Register user A
echo -e "${GREEN}1.1. Registering user A (John)${NC}"
echo "Email: $EMAIL_A"
echo "curl -X POST $BASE_URL/auth/register \\"
echo "  -H \"Content-Type: application/json\" \\"
echo "  -d '{\"email\":\"$EMAIL_A\",\"password\":\"password123\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"faculty\":\"Computer Science\",\"studentId\":\"DE123456\"}'"

REGISTER_RESPONSE_A=$(curl -s -w "\n%{http_code}" -X POST $BASE_URL/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$EMAIL_A\",\"password\":\"password123\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"faculty\":\"Computer Science\",\"studentId\":\"DE123456\"}")

HTTP_CODE_A=$(echo "$REGISTER_RESPONSE_A" | tail -n1)
BODY_A=$(echo "$REGISTER_RESPONSE_A" | sed '$d')

if [ "$HTTP_CODE_A" != "201" ]; then
    echo -e "${YELLOW}Registration failed (HTTP $HTTP_CODE_A), trying login...${NC}"
    # Try to login, maybe user already exists
    LOGIN_RESP_A=$(curl -s -w "\n%{http_code}" -X POST $BASE_URL/auth/login \
      -H "Content-Type: application/json" \
      -d "{\"email\":\"$EMAIL_A\",\"password\":\"password123\"}")
    HTTP_CODE_LOGIN_A=$(echo "$LOGIN_RESP_A" | tail -n1)
    BODY_LOGIN_A=$(echo "$LOGIN_RESP_A" | sed '$d')
    if [ "$HTTP_CODE_LOGIN_A" = "200" ]; then
        TOKEN_A=$(extract_token "$BODY_LOGIN_A")
        USER_ID_A=$(extract_id "$BODY_LOGIN_A")
        echo -e "${GREEN}✓ Login successful${NC}"
        echo "Token for user A: ${TOKEN_A:0:50}..."
        echo "ID for user A: $USER_ID_A"
    else
        echo -e "${RED}ERROR: Failed to register or login${NC}"
        format_json "$BODY_A"
        TOKEN_A=""
        USER_ID_A=""
    fi
else
    TOKEN_A=$(extract_token "$BODY_A")
    USER_ID_A=$(extract_id "$BODY_A")
    echo -e "${GREEN}✓ Registration successful${NC}"
    echo "Token for user A: ${TOKEN_A:0:50}..."
    echo "ID for user A: $USER_ID_A"
    format_json "$BODY_A"
fi
echo ""

# 1.2 Register user B
echo -e "${GREEN}1.2. Registering user B (Maria)${NC}"
echo "Email: $EMAIL_B"
echo "curl -X POST $BASE_URL/auth/register \\"
echo "  -H \"Content-Type: application/json\" \\"
echo "  -d '{\"email\":\"$EMAIL_B\",\"password\":\"password123\",\"firstName\":\"Maria\",\"lastName\":\"Smith\",\"faculty\":\"Mathematics\",\"studentId\":\"DE789012\"}'"

REGISTER_RESPONSE_B=$(curl -s -w "\n%{http_code}" -X POST $BASE_URL/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$EMAIL_B\",\"password\":\"password123\",\"firstName\":\"Maria\",\"lastName\":\"Smith\",\"faculty\":\"Mathematics\",\"studentId\":\"DE789012\"}")

HTTP_CODE_B=$(echo "$REGISTER_RESPONSE_B" | tail -n1)
BODY_B=$(echo "$REGISTER_RESPONSE_B" | sed '$d')

if [ "$HTTP_CODE_B" != "201" ]; then
    echo -e "${YELLOW}Registration failed (HTTP $HTTP_CODE_B), trying login...${NC}"
    # Try to login, maybe user already exists
    LOGIN_RESP_B=$(curl -s -w "\n%{http_code}" -X POST $BASE_URL/auth/login \
      -H "Content-Type: application/json" \
      -d "{\"email\":\"$EMAIL_B\",\"password\":\"password123\"}")
    HTTP_CODE_LOGIN_B=$(echo "$LOGIN_RESP_B" | tail -n1)
    BODY_LOGIN_B=$(echo "$LOGIN_RESP_B" | sed '$d')
    if [ "$HTTP_CODE_LOGIN_B" = "200" ]; then
        TOKEN_B=$(extract_token "$BODY_LOGIN_B")
        USER_ID_B=$(extract_id "$BODY_LOGIN_B")
        echo -e "${GREEN}✓ Login successful${NC}"
        echo "Token for user B: ${TOKEN_B:0:50}..."
        echo "ID for user B: $USER_ID_B"
    else
        echo -e "${RED}ERROR: Failed to register or login${NC}"
        format_json "$BODY_B"
        TOKEN_B=""
        USER_ID_B=""
    fi
else
    TOKEN_B=$(extract_token "$BODY_B")
    USER_ID_B=$(extract_id "$BODY_B")
    echo -e "${GREEN}✓ Registration successful${NC}"
    echo "Token for user B: ${TOKEN_B:0:50}..."
    echo "ID for user B: $USER_ID_B"
    format_json "$BODY_B"
fi
echo ""

# 1.3 Login (user A)
echo -e "${GREEN}1.3. Login (user A)${NC}"
echo "curl -X POST $BASE_URL/auth/login \\"
echo "  -H \"Content-Type: application/json\" \\"
echo "  -d '{\"email\":\"$EMAIL_A\",\"password\":\"password123\"}'"

LOGIN_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST $BASE_URL/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$EMAIL_A\",\"password\":\"password123\"}")

HTTP_CODE_LOGIN=$(echo "$LOGIN_RESPONSE" | tail -n1)
BODY_LOGIN=$(echo "$LOGIN_RESPONSE" | sed '$d')

if [ "$HTTP_CODE_LOGIN" != "200" ]; then
    echo -e "${RED}ERROR: Login failed (HTTP $HTTP_CODE_LOGIN)${NC}"
    format_json "$BODY_LOGIN"
    TOKEN_A_LOGIN=""
else
    TOKEN_A_LOGIN=$(extract_token "$BODY_LOGIN")
    echo "Token after login: $TOKEN_A_LOGIN"
    format_json "$BODY_LOGIN"
fi
echo ""

# 1.4 Validation example (error)
echo -e "${GREEN}1.4. Validation example - registration with invalid email${NC}"
echo "curl -X POST $BASE_URL/auth/register \\"
echo "  -H \"Content-Type: application/json\" \\"
echo "  -d '{\"email\":\"invalid-email\",\"password\":\"123\",\"firstName\":\"Test\",\"lastName\":\"User\"}'"

RESPONSE=$(curl -s -X POST $BASE_URL/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"invalid-email","password":"123","firstName":"Test","lastName":"User"}')
format_json "$RESPONSE"

echo -e "\n"

# =============================================================================
# SECTION 2: USER MANAGEMENT
# =============================================================================

echo -e "${YELLOW}--- Section 2: User Management ---${NC}\n"

# 2.1 Get current user info (A)
echo -e "${GREEN}2.1. Getting current user info (A)${NC}"
echo "curl -X GET $BASE_URL/users/me \\"
echo "  -H \"Authorization: Bearer $TOKEN_A\""

if [ -n "$TOKEN_A" ]; then
    RESPONSE=$(curl -s -X GET $BASE_URL/users/me \
      -H "Authorization: Bearer $TOKEN_A")
    format_json "$RESPONSE"
else
    echo -e "${RED}Skipped: token for user A not obtained${NC}"
fi

echo -e "\n"

# 2.2 Get user info by ID
echo -e "${GREEN}2.2. Getting user B info by ID${NC}"
if [ -n "$USER_ID_B" ]; then
    echo "curl -X GET $BASE_URL/users/$USER_ID_B \\"
    echo "  -H \"Authorization: Bearer $TOKEN_A\""
    if [ -n "$TOKEN_A" ]; then
        RESPONSE=$(curl -s -X GET $BASE_URL/users/$USER_ID_B \
          -H "Authorization: Bearer $TOKEN_A")
        format_json "$RESPONSE"
    else
        echo -e "${RED}Skipped: token for user A not obtained${NC}"
    fi
else
    echo -e "${YELLOW}Using ID=2 (assuming user B has ID=2)${NC}"
    echo "curl -X GET $BASE_URL/users/2 \\"
    echo "  -H \"Authorization: Bearer $TOKEN_A\""
    if [ -n "$TOKEN_A" ]; then
        RESPONSE=$(curl -s -X GET $BASE_URL/users/2 \
          -H "Authorization: Bearer $TOKEN_A")
        format_json "$RESPONSE"
    else
        echo -e "${RED}Skipped: token for user A not obtained${NC}"
    fi
fi

echo -e "\n"

# =============================================================================
# SECTION 3: OFFER MANAGEMENT
# =============================================================================

echo -e "${YELLOW}--- Section 3: Offer Management ---${NC}\n"

# 3.1 Create offer by user A
echo -e "${GREEN}3.1. Creating offer by user A (Programming Tutoring)${NC}"
echo "curl -X POST $BASE_URL/offers \\"
echo "  -H \"Authorization: Bearer $TOKEN_A\" \\"
echo "  -H \"Content-Type: application/json\" \\"
echo "  -d '{\"title\":\"Java Programming Tutoring\",\"description\":\"Help with Java, Spring Boot, databases\",\"hoursRate\":2.5}'"

if [ -n "$TOKEN_A" ]; then
    OFFER_RESPONSE_A=$(curl -s -w "\n%{http_code}" -X POST $BASE_URL/offers \
      -H "Authorization: Bearer $TOKEN_A" \
      -H "Content-Type: application/json" \
      -d '{"title":"Java Programming Tutoring","description":"Help with Java, Spring Boot, databases","hoursRate":2.5}')
    
    HTTP_CODE_OFFER_A=$(echo "$OFFER_RESPONSE_A" | tail -n1)
    BODY_OFFER_A=$(echo "$OFFER_RESPONSE_A" | sed '$d')
    
    if [ "$HTTP_CODE_OFFER_A" != "201" ]; then
        echo -e "${RED}ERROR: Offer creation failed (HTTP $HTTP_CODE_OFFER_A)${NC}"
        format_json "$BODY_OFFER_A"
        OFFER_ID_A=""
    else
        OFFER_ID_A=$(extract_id "$BODY_OFFER_A")
        echo "Created offer ID: $OFFER_ID_A"
        format_json "$BODY_OFFER_A"
    fi
else
    echo -e "${RED}Skipped: token for user A not obtained${NC}"
    OFFER_ID_A=""
fi
echo ""

# 3.2 Create offer by user B
echo -e "${GREEN}3.2. Creating offer by user B (Math Tutoring)${NC}"
echo "curl -X POST $BASE_URL/offers \\"
echo "  -H \"Authorization: Bearer $TOKEN_B\" \\"
echo "  -H \"Content-Type: application/json\" \\"
echo "  -d '{\"title\":\"Advanced Mathematics Tutoring\",\"description\":\"Help with linear algebra, calculus, probability theory\",\"hoursRate\":2.0}'"

if [ -n "$TOKEN_B" ]; then
    OFFER_RESPONSE_B=$(curl -s -w "\n%{http_code}" -X POST $BASE_URL/offers \
      -H "Authorization: Bearer $TOKEN_B" \
      -H "Content-Type: application/json" \
      -d '{"title":"Advanced Mathematics Tutoring","description":"Help with linear algebra, calculus, probability theory","hoursRate":2.0}')
    
    HTTP_CODE_OFFER_B=$(echo "$OFFER_RESPONSE_B" | tail -n1)
    BODY_OFFER_B=$(echo "$OFFER_RESPONSE_B" | sed '$d')
    
    if [ "$HTTP_CODE_OFFER_B" != "201" ]; then
        echo -e "${RED}ERROR: Offer creation failed (HTTP $HTTP_CODE_OFFER_B)${NC}"
        format_json "$BODY_OFFER_B"
        OFFER_ID_B=""
    else
        OFFER_ID_B=$(extract_id "$BODY_OFFER_B")
        echo "Created offer ID: $OFFER_ID_B"
        format_json "$BODY_OFFER_B"
    fi
else
    echo -e "${RED}Skipped: token for user B not obtained${NC}"
    OFFER_ID_B=""
fi
echo ""

# 3.3 View all active offers
echo -e "${GREEN}3.3. Viewing all active offers${NC}"
echo "curl -X GET \"$BASE_URL/offers/active/list?page=0&size=10\""

if [ -n "$TOKEN_A" ]; then
    RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET "$BASE_URL/offers/active/list?page=0&size=10" \
      -H "Authorization: Bearer $TOKEN_A")
    HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE" | cut -d: -f2)
    BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE/d')
    if [ "$HTTP_CODE" = "200" ]; then
        format_json "$BODY"
    else
        echo -e "${YELLOW}HTTP Code: $HTTP_CODE${NC}"
        format_json "$BODY"
    fi
else
    RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET "$BASE_URL/offers/active/list?page=0&size=10")
    HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE" | cut -d: -f2)
    BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE/d')
    echo -e "${YELLOW}HTTP Code: $HTTP_CODE (authentication required)${NC}"
    if [ -n "$BODY" ]; then
        format_json "$BODY"
    fi
fi

echo -e "\n"

# 3.4 View specific offer
echo -e "${GREEN}3.4. Viewing specific offer (ID: $OFFER_ID_A)${NC}"
echo "curl -X GET $BASE_URL/offers/$OFFER_ID_A"

if [ -n "$OFFER_ID_A" ]; then
    if [ -n "$TOKEN_A" ]; then
        RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET $BASE_URL/offers/$OFFER_ID_A \
          -H "Authorization: Bearer $TOKEN_A")
    else
        RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET $BASE_URL/offers/$OFFER_ID_A)
    fi
    HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE" | cut -d: -f2)
    BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE/d')
    if [ "$HTTP_CODE" = "200" ]; then
        format_json "$BODY"
    else
        echo -e "${YELLOW}HTTP Code: $HTTP_CODE${NC}"
        if [ -n "$BODY" ]; then
            format_json "$BODY"
        fi
    fi
else
    echo -e "${RED}Skipped: offer A ID not obtained${NC}"
fi

echo -e "\n"

# 3.5 View offers by specific user
echo -e "${GREEN}3.5. Viewing offers by user A${NC}"
echo "curl -X GET \"$BASE_URL/offers/owner/1?page=0&size=10\""

if [ -n "$USER_ID_A" ]; then
    RESPONSE=$(curl -s -X GET "$BASE_URL/offers/owner/$USER_ID_A?page=0&size=10")
    format_json "$RESPONSE"
else
    RESPONSE=$(curl -s -X GET "$BASE_URL/offers/owner/1?page=0&size=10")
    format_json "$RESPONSE"
fi

echo -e "\n"

# 3.6 View own offers
echo -e "${GREEN}3.6. Viewing own offers (user A)${NC}"
echo "curl -X GET $BASE_URL/offers/my-offers \\"
echo "  -H \"Authorization: Bearer $TOKEN_A\""

if [ -n "$TOKEN_A" ]; then
    RESPONSE=$(curl -s -X GET $BASE_URL/offers/my-offers \
      -H "Authorization: Bearer $TOKEN_A")
    format_json "$RESPONSE"
else
    echo -e "${RED}Skipped: token for user A not obtained${NC}"
fi

echo -e "\n"

# 3.7 Update offer
echo -e "${GREEN}3.7. Updating offer (user A)${NC}"
echo "curl -X PUT $BASE_URL/offers/$OFFER_ID_A \\"
echo "  -H \"Authorization: Bearer $TOKEN_A\" \\"
echo "  -H \"Content-Type: application/json\" \\"
echo "  -d '{\"title\":\"Java Programming Tutoring (Updated)\",\"description\":\"Help with Java, Spring Boot, databases. 3 years of experience.\",\"hoursRate\":3.0}'"

if [ -n "$TOKEN_A" ] && [ -n "$OFFER_ID_A" ]; then
    RESPONSE=$(curl -s -X PUT $BASE_URL/offers/$OFFER_ID_A \
      -H "Authorization: Bearer $TOKEN_A" \
      -H "Content-Type: application/json" \
      -d '{"title":"Java Programming Tutoring (Updated)","description":"Help with Java, Spring Boot, databases. 3 years of experience.","hoursRate":3.0}')
    format_json "$RESPONSE"
else
    echo -e "${RED}Skipped: token or offer ID not obtained${NC}"
fi

echo -e "\n"

# 3.8 Deactivate offer
echo -e "${GREEN}3.8. Deactivating offer${NC}"
echo "curl -X PUT $BASE_URL/offers/$OFFER_ID_B/deactivate \\"
echo "  -H \"Authorization: Bearer $TOKEN_B\""

if [ -n "$TOKEN_B" ] && [ -n "$OFFER_ID_B" ]; then
    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X PUT $BASE_URL/offers/$OFFER_ID_B/deactivate \
      -H "Authorization: Bearer $TOKEN_B")
    if [ "$HTTP_CODE" = "204" ]; then
        echo -e "${GREEN}Status: 204 No Content (offer deactivated)${NC}"
    else
        echo -e "${RED}ERROR: Deactivation failed (HTTP $HTTP_CODE)${NC}"
    fi
else
    echo -e "${RED}Skipped: token or offer ID not obtained${NC}"
fi

echo -e "\n"

# 3.9 Activate offer
echo -e "${GREEN}3.9. Activating offer${NC}"
echo "curl -X PUT $BASE_URL/offers/$OFFER_ID_B/activate \\"
echo "  -H \"Authorization: Bearer $TOKEN_B\""

if [ -n "$TOKEN_B" ] && [ -n "$OFFER_ID_B" ]; then
    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X PUT $BASE_URL/offers/$OFFER_ID_B/activate \
      -H "Authorization: Bearer $TOKEN_B")
    if [ "$HTTP_CODE" = "204" ]; then
        echo -e "${GREEN}Status: 204 No Content (offer activated)${NC}"
    else
        echo -e "${RED}ERROR: Activation failed (HTTP $HTTP_CODE)${NC}"
    fi
else
    echo -e "${RED}Skipped: token or offer ID not obtained${NC}"
fi


# =============================================================================
# SECTION 4: BOOKING MANAGEMENT
# =============================================================================

echo -e "${YELLOW}--- Section 4: Booking Management ---${NC}\n"

# 4.1 Create booking (B requests service from A)
echo -e "${GREEN}4.1. Creating booking (B requests service from A)${NC}"
echo "curl -X POST $BASE_URL/bookings \\"
echo "  -H \"Authorization: Bearer $TOKEN_B\" \\"
echo "  -H \"Content-Type: application/json\" \\"
echo "  -d '{\"offerId\":$OFFER_ID_A,\"hours\":3.0}'"

if [ -n "$TOKEN_B" ] && [ -n "$OFFER_ID_A" ]; then
    BOOKING_RESPONSE_1=$(curl -s -w "\n%{http_code}" -X POST $BASE_URL/bookings \
      -H "Authorization: Bearer $TOKEN_B" \
      -H "Content-Type: application/json" \
      -d "{\"offerId\":$OFFER_ID_A,\"hours\":3.0}")
    
    HTTP_CODE_BOOKING_1=$(echo "$BOOKING_RESPONSE_1" | tail -n1)
    BODY_BOOKING_1=$(echo "$BOOKING_RESPONSE_1" | sed '$d')
    
    if [ "$HTTP_CODE_BOOKING_1" != "201" ]; then
        echo -e "${RED}ERROR: Booking creation failed (HTTP $HTTP_CODE_BOOKING_1)${NC}"
        format_json "$BODY_BOOKING_1"
        BOOKING_ID_1=""
    else
        BOOKING_ID_1=$(extract_id "$BODY_BOOKING_1")
        echo "Created booking ID: $BOOKING_ID_1"
        format_json "$BODY_BOOKING_1"
    fi
else
    echo -e "${RED}Skipped: token or offer ID not obtained${NC}"
    BOOKING_ID_1=""
fi
echo ""

# 4.2 View specific booking
echo -e "${GREEN}4.2. Viewing specific booking (ID: $BOOKING_ID_1)${NC}"
echo "curl -X GET $BASE_URL/bookings/$BOOKING_ID_1"

if [ -n "$BOOKING_ID_1" ]; then
    if [ -n "$TOKEN_A" ]; then
        RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET $BASE_URL/bookings/$BOOKING_ID_1 \
          -H "Authorization: Bearer $TOKEN_A")
    else
        RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET $BASE_URL/bookings/$BOOKING_ID_1)
    fi
    HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE" | cut -d: -f2)
    BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE/d')
    if [ "$HTTP_CODE" = "200" ]; then
        format_json "$BODY"
    else
        echo -e "${YELLOW}HTTP Code: $HTTP_CODE${NC}"
        if [ -n "$BODY" ]; then
            format_json "$BODY"
        fi
    fi
else
    echo -e "${RED}Skipped: booking ID not obtained${NC}"
fi

echo -e "\n"

# 4.3 Confirm booking by offer owner (A confirms)
echo -e "${GREEN}4.3. Confirming booking by offer owner (A confirms)${NC}"
echo "curl -X PUT $BASE_URL/bookings/$BOOKING_ID_1/confirm \\"
echo "  -H \"Authorization: Bearer $TOKEN_A\""

if [ -n "$TOKEN_A" ] && [ -n "$BOOKING_ID_1" ]; then
    RESPONSE=$(curl -s -X PUT $BASE_URL/bookings/$BOOKING_ID_1/confirm \
      -H "Authorization: Bearer $TOKEN_A")
    format_json "$RESPONSE"
else
    echo -e "${RED}Skipped: token or booking ID not obtained${NC}"
fi

echo -e "\n"

# 4.4 View bookings as requester
echo -e "${GREEN}4.4. Viewing bookings as requester (B)${NC}"
echo "curl -X GET \"$BASE_URL/bookings/my/as-requester?page=0&size=10\" \\"
echo "  -H \"Authorization: Bearer $TOKEN_B\""

if [ -n "$TOKEN_B" ]; then
    RESPONSE=$(curl -s -X GET "$BASE_URL/bookings/my/as-requester?page=0&size=10" \
      -H "Authorization: Bearer $TOKEN_B")
    format_json "$RESPONSE"
else
    echo -e "${RED}Skipped: token for user B not obtained${NC}"
fi

echo -e "\n"

# 4.5 View bookings as offer owner
echo -e "${GREEN}4.5. Viewing bookings as offer owner (A)${NC}"
echo "curl -X GET \"$BASE_URL/bookings/my/as-owner?page=0&size=10\" \\"
echo "  -H \"Authorization: Bearer $TOKEN_A\""

if [ -n "$TOKEN_A" ]; then
    RESPONSE=$(curl -s -X GET "$BASE_URL/bookings/my/as-owner?page=0&size=10" \
      -H "Authorization: Bearer $TOKEN_A")
    format_json "$RESPONSE"
else
    echo -e "${RED}Skipped: token for user A not obtained${NC}"
fi

echo -e "\n"

# 4.6 View bookings by offer
echo -e "${GREEN}4.6. Viewing bookings by offer (ID: $OFFER_ID_A)${NC}"
echo "curl -X GET \"$BASE_URL/bookings/offer/$OFFER_ID_A?page=0&size=10\""

if [ -n "$OFFER_ID_A" ]; then
    if [ -n "$TOKEN_A" ]; then
        RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET "$BASE_URL/bookings/offer/$OFFER_ID_A?page=0&size=10" \
          -H "Authorization: Bearer $TOKEN_A")
    else
        RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET "$BASE_URL/bookings/offer/$OFFER_ID_A?page=0&size=10")
    fi
    HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE" | cut -d: -f2)
    BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE/d')
    if [ "$HTTP_CODE" = "200" ]; then
        format_json "$BODY"
    else
        echo -e "${YELLOW}HTTP Code: $HTTP_CODE${NC}"
        if [ -n "$BODY" ]; then
            format_json "$BODY"
        fi
    fi
else
    echo -e "${RED}Skipped: offer ID not obtained${NC}"
fi

echo -e "\n"

# 4.7 View bookings by status
echo -e "${GREEN}4.7. Viewing bookings with status CONFIRMED${NC}"
echo "curl -X GET \"$BASE_URL/bookings/status/CONFIRMED?page=0&size=10\""

if [ -n "$TOKEN_A" ]; then
    RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET "$BASE_URL/bookings/status/CONFIRMED?page=0&size=10" \
      -H "Authorization: Bearer $TOKEN_A")
else
    RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET "$BASE_URL/bookings/status/CONFIRMED?page=0&size=10")
fi
HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE" | cut -d: -f2)
BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE/d')
if [ "$HTTP_CODE" = "200" ]; then
    format_json "$BODY"
else
    echo -e "${YELLOW}HTTP Code: $HTTP_CODE${NC}"
    if [ -n "$BODY" ]; then
        format_json "$BODY"
    fi
fi

echo -e "\n"

# 4.8 Complete booking
echo -e "${GREEN}4.8. Completing booking${NC}"
echo "curl -X PUT $BASE_URL/bookings/$BOOKING_ID_1/complete \\"
echo "  -H \"Authorization: Bearer $TOKEN_A\""

if [ -n "$TOKEN_A" ] && [ -n "$BOOKING_ID_1" ]; then
    RESPONSE=$(curl -s -X PUT $BASE_URL/bookings/$BOOKING_ID_1/complete \
      -H "Authorization: Bearer $TOKEN_A")
    format_json "$RESPONSE"
else
    echo -e "${RED}Skipped: token or booking ID not obtained${NC}"
fi

echo -e "\n"

# 4.9 Create second booking (A requests service from B)
echo -e "${GREEN}4.9. Creating second booking (A requests service from B)${NC}"
echo "curl -X POST $BASE_URL/bookings \\"
echo "  -H \"Authorization: Bearer $TOKEN_A\" \\"
echo "  -H \"Content-Type: application/json\" \\"
echo "  -d '{\"offerId\":$OFFER_ID_B,\"hours\":2.0}'"

if [ -n "$TOKEN_A" ] && [ -n "$OFFER_ID_B" ]; then
    BOOKING_RESPONSE_2=$(curl -s -w "\n%{http_code}" -X POST $BASE_URL/bookings \
      -H "Authorization: Bearer $TOKEN_A" \
      -H "Content-Type: application/json" \
      -d "{\"offerId\":$OFFER_ID_B,\"hours\":2.0}")
    
    HTTP_CODE_BOOKING_2=$(echo "$BOOKING_RESPONSE_2" | tail -n1)
    BODY_BOOKING_2=$(echo "$BOOKING_RESPONSE_2" | sed '$d')
    
    if [ "$HTTP_CODE_BOOKING_2" != "201" ]; then
        echo -e "${RED}ERROR: Booking creation failed (HTTP $HTTP_CODE_BOOKING_2)${NC}"
        format_json "$BODY_BOOKING_2"
        BOOKING_ID_2=""
    else
        BOOKING_ID_2=$(extract_id "$BODY_BOOKING_2")
        echo "Created booking ID: $BOOKING_ID_2"
        format_json "$BODY_BOOKING_2"
    fi
else
    echo -e "${RED}Skipped: token or offer ID not obtained${NC}"
    BOOKING_ID_2=""
fi
echo ""

# 4.10 Confirm and complete second booking
echo -e "${GREEN}4.10. Confirming second booking (B confirms)${NC}"
if [ -n "$TOKEN_B" ] && [ -n "$BOOKING_ID_2" ]; then
    RESPONSE=$(curl -s -X PUT $BASE_URL/bookings/$BOOKING_ID_2/confirm \
      -H "Authorization: Bearer $TOKEN_B")
    format_json "$RESPONSE"
else
    echo -e "${RED}Skipped: token or booking ID not obtained${NC}"
fi

echo -e "\n"

echo -e "${GREEN}4.11. Completing second booking${NC}"
if [ -n "$TOKEN_B" ] && [ -n "$BOOKING_ID_2" ]; then
    RESPONSE=$(curl -s -X PUT $BASE_URL/bookings/$BOOKING_ID_2/complete \
      -H "Authorization: Bearer $TOKEN_B")
    format_json "$RESPONSE"
else
    echo -e "${RED}Skipped: token or booking ID not obtained${NC}"
fi

echo -e "\n"

# 4.12 Cancel booking (example)
echo -e "${GREEN}4.12. Canceling booking (example)${NC}"
echo "curl -X PUT \"$BASE_URL/bookings/$BOOKING_ID_2/cancel?reason=Change of plans\" \\"
echo "  -H \"Authorization: Bearer $TOKEN_A\""

# Create new booking for cancellation demo
if [ -n "$TOKEN_B" ] && [ -n "$OFFER_ID_A" ]; then
    BOOKING_RESPONSE_3=$(curl -s -w "\n%{http_code}" -X POST $BASE_URL/bookings \
      -H "Authorization: Bearer $TOKEN_B" \
      -H "Content-Type: application/json" \
      -d "{\"offerId\":$OFFER_ID_A,\"hours\":1.0}")
    
    HTTP_CODE_BOOKING_3=$(echo "$BOOKING_RESPONSE_3" | tail -n1)
    BODY_BOOKING_3=$(echo "$BOOKING_RESPONSE_3" | sed '$d')
    
    if [ "$HTTP_CODE_BOOKING_3" = "201" ]; then
        BOOKING_ID_3=$(extract_id "$BODY_BOOKING_3")
        RESPONSE=$(curl -s -X PUT "$BASE_URL/bookings/$BOOKING_ID_3/cancel?reason=Change of plans" \
          -H "Authorization: Bearer $TOKEN_B")
        format_json "$RESPONSE"
    else
        echo -e "${RED}Failed to create booking for cancellation demo${NC}"
        format_json "$BODY_BOOKING_3"
    fi
else
    echo -e "${RED}Skipped: token or offer ID not obtained${NC}"
fi

echo -e "\n"

# =============================================================================
# SECTION 5: BALANCE AND HISTORY CHECK
# =============================================================================

echo -e "${YELLOW}--- Section 5: Balance and History Check ---${NC}\n"

# 5.1 Check user A balance
echo -e "${GREEN}5.1. Checking user A info (wallet balance)${NC}"
echo "curl -X GET $BASE_URL/users/me \\"
echo "  -H \"Authorization: Bearer $TOKEN_A\""

if [ -n "$TOKEN_A" ]; then
    RESPONSE=$(curl -s -X GET $BASE_URL/users/me \
      -H "Authorization: Bearer $TOKEN_A")
    format_json "$RESPONSE"
else
    echo -e "${RED}Skipped: token for user A not obtained${NC}"
fi

echo -e "\n"

# 5.2 Check user B balance
echo -e "${GREEN}5.2. Checking user B info (wallet balance)${NC}"
echo "curl -X GET $BASE_URL/users/me \\"
echo "  -H \"Authorization: Bearer $TOKEN_B\""

if [ -n "$TOKEN_B" ]; then
    RESPONSE=$(curl -s -X GET $BASE_URL/users/me \
      -H "Authorization: Bearer $TOKEN_B")
    format_json "$RESPONSE"
else
    echo -e "${RED}Skipped: token for user B not obtained${NC}"
fi

echo -e "\n"

# =============================================================================
# SECTION 6: ERROR HANDLING
# =============================================================================

echo -e "${YELLOW}--- Section 6: Error Handling Demonstration ---${NC}\n"

# 6.1 Attempt to access without authentication
echo -e "${GREEN}6.1. Attempting to access without authentication${NC}"
echo "curl -X GET $BASE_URL/users/me"

RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET $BASE_URL/users/me)
HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE" | cut -d: -f2)
BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE/d')
echo -e "${YELLOW}HTTP Code: $HTTP_CODE (expected 401 or 403)${NC}"
if [ -n "$BODY" ]; then
    format_json "$BODY"
fi

echo -e "\n"

# 6.2 Attempt to create booking for own offer
echo -e "${GREEN}6.2. Attempting to create booking for own offer${NC}"
echo "curl -X POST $BASE_URL/bookings \\"
echo "  -H \"Authorization: Bearer $TOKEN_A\" \\"
echo "  -H \"Content-Type: application/json\" \\"
echo "  -d '{\"offerId\":$OFFER_ID_A,\"hours\":1.0}'"

if [ -n "$TOKEN_A" ] && [ -n "$OFFER_ID_A" ]; then
    RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST $BASE_URL/bookings \
      -H "Authorization: Bearer $TOKEN_A" \
      -H "Content-Type: application/json" \
      -d "{\"offerId\":$OFFER_ID_A,\"hours\":1.0}")
    HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE" | cut -d: -f2)
    BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE/d')
    echo -e "${YELLOW}HTTP Code: $HTTP_CODE (expected error, cannot book own offer)${NC}"
    if [ -n "$BODY" ]; then
        format_json "$BODY"
    fi
else
    echo -e "${RED}Skipped: token or offer ID not obtained${NC}"
fi

echo -e "\n"

# 6.3 Attempt to create offer with invalid data
echo -e "${GREEN}6.3. Attempting to create offer with invalid data${NC}"
echo "curl -X POST $BASE_URL/offers \\"
echo "  -H \"Authorization: Bearer $TOKEN_A\" \\"
echo "  -H \"Content-Type: application/json\" \\"
echo "  -d '{\"title\":\"\",\"description\":\"\",\"hoursRate\":-5}'"

if [ -n "$TOKEN_A" ]; then
    RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST $BASE_URL/offers \
      -H "Authorization: Bearer $TOKEN_A" \
      -H "Content-Type: application/json" \
      -d '{"title":"","description":"","hoursRate":-5}')
    HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE" | cut -d: -f2)
    BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE/d')
    echo -e "${YELLOW}HTTP Code: $HTTP_CODE (expected 400 - validation error)${NC}"
    if [ -n "$BODY" ]; then
        format_json "$BODY"
    fi
else
    echo -e "${RED}Skipped: token for user A not obtained${NC}"
fi

echo -e "\n"

# =============================================================================
# SECTION 7: ADMIN FUNCTIONALITY
# =============================================================================

echo -e "${YELLOW}--- Section 7: Admin Functionality ---${NC}\n"

# 7.1 Create admin user (first admin, no auth required)
echo -e "${GREEN}7.1. Creating first admin user${NC}"
ADMIN_EMAIL="admin${TIMESTAMP}@example.com"
echo "Email: $ADMIN_EMAIL"
echo "curl -X POST $BASE_URL/admin/create-admin \\"
echo "  -H \"Content-Type: application/json\" \\"
echo "  -d '{\"email\":\"$ADMIN_EMAIL\",\"password\":\"admin123\",\"firstName\":\"Admin\",\"lastName\":\"User\",\"faculty\":\"Administration\"}'"

ADMIN_REGISTER_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST $BASE_URL/admin/create-admin \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$ADMIN_EMAIL\",\"password\":\"admin123\",\"firstName\":\"Admin\",\"lastName\":\"User\",\"faculty\":\"Administration\"}")

HTTP_CODE_ADMIN=$(echo "$ADMIN_REGISTER_RESPONSE" | tail -1)
BODY_ADMIN=$(echo "$ADMIN_REGISTER_RESPONSE" | sed '$d')

if [ "$HTTP_CODE_ADMIN" != "201" ]; then
    echo -e "${RED}ERROR: Admin creation failed (HTTP $HTTP_CODE_ADMIN)${NC}"
    format_json "$BODY_ADMIN"
    ADMIN_TOKEN=""
    ADMIN_ID=""
else
    ADMIN_TOKEN=$(extract_token "$BODY_ADMIN")
    ADMIN_ID=$(extract_id "$BODY_ADMIN")
    echo -e "${GREEN}✓ Admin created successfully${NC}"
    echo "Token for admin: ${ADMIN_TOKEN:0:50}..."
    echo "ID for admin: $ADMIN_ID"
    format_json "$BODY_ADMIN"
fi

echo -e "\n"

# 7.2 Get all users (admin only)
echo -e "${GREEN}7.2. Getting all users (admin only)${NC}"
echo "curl -X GET \"$BASE_URL/admin/users?page=0&size=10\" \\"
echo "  -H \"Authorization: Bearer $ADMIN_TOKEN\""

if [ -n "$ADMIN_TOKEN" ]; then
    RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/admin/users?page=0&size=10" \
      -H "Authorization: Bearer $ADMIN_TOKEN")
    HTTP_CODE=$(echo "$RESPONSE" | tail -1)
    BODY=$(echo "$RESPONSE" | sed '$d')
    
    if [ "$HTTP_CODE" != "200" ]; then
        echo -e "${RED}ERROR: Failed to get users (HTTP $HTTP_CODE)${NC}"
        format_json "$BODY"
    else
        echo -e "${GREEN}✓ Users retrieved successfully${NC}"
        format_json "$BODY"
    fi
else
    echo -e "${RED}Skipped: admin token not obtained${NC}"
fi

echo -e "\n"

# 7.3 Get user by ID (admin only)
echo -e "${GREEN}7.3. Getting user by ID (admin only)${NC}"
echo "curl -X GET $BASE_URL/admin/users/$USER_ID_A \\"
echo "  -H \"Authorization: Bearer $ADMIN_TOKEN\""

if [ -n "$ADMIN_TOKEN" ] && [ -n "$USER_ID_A" ]; then
    RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/admin/users/$USER_ID_A" \
      -H "Authorization: Bearer $ADMIN_TOKEN")
    HTTP_CODE=$(echo "$RESPONSE" | tail -1)
    BODY=$(echo "$RESPONSE" | sed '$d')
    
    if [ "$HTTP_CODE" != "200" ]; then
        echo -e "${RED}ERROR: Failed to get user (HTTP $HTTP_CODE)${NC}"
        format_json "$BODY"
    else
        echo -e "${GREEN}✓ User retrieved successfully${NC}"
        format_json "$BODY"
    fi
else
    echo -e "${RED}Skipped: admin token or user ID not obtained${NC}"
fi

echo -e "\n"

# 7.4 Deactivate user (admin only)
echo -e "${GREEN}7.4. Deactivating user (admin only)${NC}"
echo "curl -X PUT $BASE_URL/admin/users/$USER_ID_B/deactivate \\"
echo "  -H \"Authorization: Bearer $ADMIN_TOKEN\""

if [ -n "$ADMIN_TOKEN" ] && [ -n "$USER_ID_B" ]; then
    RESPONSE=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/admin/users/$USER_ID_B/deactivate" \
      -H "Authorization: Bearer $ADMIN_TOKEN")
    HTTP_CODE=$(echo "$RESPONSE" | tail -1)
    BODY=$(echo "$RESPONSE" | sed '$d')
    
    if [ "$HTTP_CODE" != "200" ]; then
        echo -e "${RED}ERROR: Failed to deactivate user (HTTP $HTTP_CODE)${NC}"
        format_json "$BODY"
    else
        echo -e "${GREEN}✓ User deactivated successfully${NC}"
        format_json "$BODY"
    fi
else
    echo -e "${RED}Skipped: admin token or user ID not obtained${NC}"
fi

echo -e "\n"

# 7.5 Activate user (admin only)
echo -e "${GREEN}7.5. Activating user (admin only)${NC}"
echo "curl -X PUT $BASE_URL/admin/users/$USER_ID_B/activate \\"
echo "  -H \"Authorization: Bearer $ADMIN_TOKEN\""

if [ -n "$ADMIN_TOKEN" ] && [ -n "$USER_ID_B" ]; then
    RESPONSE=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/admin/users/$USER_ID_B/activate" \
      -H "Authorization: Bearer $ADMIN_TOKEN")
    HTTP_CODE=$(echo "$RESPONSE" | tail -1)
    BODY=$(echo "$RESPONSE" | sed '$d')
    
    if [ "$HTTP_CODE" != "200" ]; then
        echo -e "${RED}ERROR: Failed to activate user (HTTP $HTTP_CODE)${NC}"
        format_json "$BODY"
    else
        echo -e "${GREEN}✓ User activated successfully${NC}"
        format_json "$BODY"
    fi
else
    echo -e "${RED}Skipped: admin token or user ID not obtained${NC}"
fi

echo -e "\n"

# 7.6 Update user role (admin only)
echo -e "${GREEN}7.6. Updating user role (admin only)${NC}"
echo "curl -X PUT $BASE_URL/admin/users/$USER_ID_A/role \\"
echo "  -H \"Authorization: Bearer $ADMIN_TOKEN\" \\"
echo "  -H \"Content-Type: application/json\" \\"
echo "  -d '{\"role\":\"ADMIN\"}'"

if [ -n "$ADMIN_TOKEN" ] && [ -n "$USER_ID_A" ]; then
    RESPONSE=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/admin/users/$USER_ID_A/role" \
      -H "Authorization: Bearer $ADMIN_TOKEN" \
      -H "Content-Type: application/json" \
      -d '{"role":"ADMIN"}')
    HTTP_CODE=$(echo "$RESPONSE" | tail -1)
    BODY=$(echo "$RESPONSE" | sed '$d')
    
    if [ "$HTTP_CODE" != "200" ]; then
        echo -e "${RED}ERROR: Failed to update user role (HTTP $HTTP_CODE)${NC}"
        format_json "$BODY"
    else
        echo -e "${GREEN}✓ User role updated successfully${NC}"
        format_json "$BODY"
    fi
else
    echo -e "${RED}Skipped: admin token or user ID not obtained${NC}"
fi

echo -e "\n"

# 7.7 Get all transactions (admin only)
echo -e "${GREEN}7.7. Getting all transactions (admin only)${NC}"
echo "curl -X GET \"$BASE_URL/admin/transactions?page=0&size=10\" \\"
echo "  -H \"Authorization: Bearer $ADMIN_TOKEN\""

if [ -n "$ADMIN_TOKEN" ]; then
    RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/admin/transactions?page=0&size=10" \
      -H "Authorization: Bearer $ADMIN_TOKEN")
    HTTP_CODE=$(echo "$RESPONSE" | tail -1)
    BODY=$(echo "$RESPONSE" | sed '$d')
    
    if [ "$HTTP_CODE" != "200" ]; then
        echo -e "${RED}ERROR: Failed to get transactions (HTTP $HTTP_CODE)${NC}"
        format_json "$BODY"
    else
        echo -e "${GREEN}✓ Transactions retrieved successfully${NC}"
        format_json "$BODY"
    fi
else
    echo -e "${RED}Skipped: admin token not obtained${NC}"
fi

echo -e "\n"

# 7.8 Get transactions by user ID (admin only)
echo -e "${GREEN}7.8. Getting transactions by user ID (admin only)${NC}"
echo "curl -X GET \"$BASE_URL/admin/transactions/user/$USER_ID_A?page=0&size=10\" \\"
echo "  -H \"Authorization: Bearer $ADMIN_TOKEN\""

if [ -n "$ADMIN_TOKEN" ] && [ -n "$USER_ID_A" ]; then
    RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/admin/transactions/user/$USER_ID_A?page=0&size=10" \
      -H "Authorization: Bearer $ADMIN_TOKEN")
    HTTP_CODE=$(echo "$RESPONSE" | tail -1)
    BODY=$(echo "$RESPONSE" | sed '$d')
    
    if [ "$HTTP_CODE" != "200" ]; then
        echo -e "${RED}ERROR: Failed to get user transactions (HTTP $HTTP_CODE)${NC}"
        format_json "$BODY"
    else
        echo -e "${GREEN}✓ User transactions retrieved successfully${NC}"
        format_json "$BODY"
    fi
else
    echo -e "${RED}Skipped: admin token or user ID not obtained${NC}"
fi

echo -e "\n"

# 7.9 Get transactions by type (admin only)
echo -e "${GREEN}7.9. Getting transactions by type (admin only)${NC}"
echo "curl -X GET \"$BASE_URL/admin/transactions/type/RESERVE?page=0&size=10\" \\"
echo "  -H \"Authorization: Bearer $ADMIN_TOKEN\""

if [ -n "$ADMIN_TOKEN" ]; then
    RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/admin/transactions/type/RESERVE?page=0&size=10" \
      -H "Authorization: Bearer $ADMIN_TOKEN")
    HTTP_CODE=$(echo "$RESPONSE" | tail -1)
    BODY=$(echo "$RESPONSE" | sed '$d')
    
    if [ "$HTTP_CODE" != "200" ]; then
        echo -e "${RED}ERROR: Failed to get transactions by type (HTTP $HTTP_CODE)${NC}"
        format_json "$BODY"
    else
        echo -e "${GREEN}✓ Transactions by type retrieved successfully${NC}"
        format_json "$BODY"
    fi
else
    echo -e "${RED}Skipped: admin token not obtained${NC}"
fi

echo -e "\n"

# =============================================================================
# CONCLUSION
# =============================================================================

echo -e "${BLUE}=== Demonstration completed ===${NC}"
echo -e "Used tokens:"
echo -e "  User A: $TOKEN_A"
echo -e "  User B: $TOKEN_B"
echo -e ""
echo -e "Created IDs:"
echo -e "  Offer A: $OFFER_ID_A"
echo -e "  Offer B: $OFFER_ID_B"
echo -e "  Booking 1: $BOOKING_ID_1"
echo -e "  Booking 2: $BOOKING_ID_2"
if [ -n "$ADMIN_ID" ]; then
    echo -e "  Admin: $ADMIN_ID"
fi
