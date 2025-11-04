#!/bin/bash

echo "🧪 Testing Frontend API endpoints..."
echo ""

API_BASE="http://localhost:8080/api"

# Test 1: Register
echo "1. Testing registration..."
TIMESTAMP=$(date +%s)
REGISTER_RESPONSE=$(curl -s -X POST "$API_BASE/auth/register" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"testfrontend${TIMESTAMP}@test.com\",\"password\":\"test123\",\"firstName\":\"Test\",\"lastName\":\"User\",\"faculty\":\"CS\"}")

if echo "$REGISTER_RESPONSE" | grep -q "token"; then
    echo "✅ Registration successful"
    TOKEN=$(echo "$REGISTER_RESPONSE" | python3 -c "import sys, json; print(json.load(sys.stdin)['token'])" 2>/dev/null)
else
    echo "❌ Registration failed"
    echo "$REGISTER_RESPONSE"
    exit 1
fi

# Test 2: Get user info
echo ""
echo "2. Testing get user info..."
USER_RESPONSE=$(curl -s -X GET "$API_BASE/users/me" \
  -H "Authorization: Bearer $TOKEN")

if echo "$USER_RESPONSE" | grep -q "email"; then
    echo "✅ Get user info successful"
else
    echo "❌ Get user info failed"
fi

# Test 3: Create offer
echo ""
echo "3. Testing create offer..."
OFFER_RESPONSE=$(curl -s -X POST "$API_BASE/offers" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"title":"Test Offer Frontend","description":"Test description","hoursRate":2.5}')

if echo "$OFFER_RESPONSE" | grep -q "id"; then
    echo "✅ Create offer successful"
    OFFER_ID=$(echo "$OFFER_RESPONSE" | python3 -c "import sys, json; print(json.load(sys.stdin)['id'])" 2>/dev/null)
else
    echo "❌ Create offer failed"
    echo "$OFFER_RESPONSE"
fi

# Test 4: Get my offers
echo ""
echo "4. Testing get my offers..."
MY_OFFERS=$(curl -s -X GET "$API_BASE/offers/my-offers" \
  -H "Authorization: Bearer $TOKEN")

if echo "$MY_OFFERS" | grep -q "\["; then
    echo "✅ Get my offers successful"
else
    echo "❌ Get my offers failed"
fi

# Test 5: Get active offers
echo ""
echo "5. Testing get active offers..."
ACTIVE_OFFERS=$(curl -s -X GET "$API_BASE/offers/active/list?page=0&size=5")

if echo "$ACTIVE_OFFERS" | grep -q "content"; then
    echo "✅ Get active offers successful"
else
    echo "⚠️  Get active offers returned empty or error (might be expected)"
fi

echo ""
echo "✅ Frontend API tests completed!"
echo ""
echo "📝 Next steps:"
echo "   1. Open index.html in your browser"
echo "   2. Or run: python3 -m http.server 8000"
echo "   3. Navigate to http://localhost:8000/index.html"
echo ""

