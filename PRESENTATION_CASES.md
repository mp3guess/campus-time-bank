# Test Cases for Campus TimeBank Presentation

## General Information

**Base URL:** `http://localhost:8080/api`

**Authentication Format:** JWT token in header `Authorization: Bearer <token>`

---

## 1. User Registration and Authentication

### 1.1 New User Registration
- **Goal:** Show the process of registering a new user in the system
- **Endpoint:** `POST /api/auth/register`
- **Request Body:**
  - email (required, valid email)
  - password (required, minimum 6 characters)
  - firstName (required)
  - lastName (required)
  - faculty (optional)
  - studentId (optional)
- **Expected Result:** 
  - Status 201 Created
  - Response with JWT token and user information
  - Automatic wallet creation with initial balance of 10.00 hours

### 1.2 Login
- **Goal:** Show the authentication process for an existing user
- **Endpoint:** `POST /api/auth/login`
- **Request Body:**
  - email
  - password
- **Expected Result:**
  - Status 200 OK
  - Response with JWT token and user information

### 1.3 Registration Validation
- **Goal:** Show validation error handling
- **Tests:**
  - Registration with invalid email
  - Registration with short password (< 6 characters)
  - Registration with empty required fields
- **Expected Result:** Status 400 Bad Request with error descriptions

---

## 2. User Management

### 2.1 Get Current User Information
- **Goal:** Show retrieving information about the registered user
- **Endpoint:** `GET /api/users/me`
- **Authentication:** Required (JWT token)
- **Expected Result:**
  - Status 200 OK
  - Current user information (id, email, name, faculty, student ID, role)

### 2.2 Get Another User's Information
- **Goal:** Show viewing other users' profiles
- **Endpoint:** `GET /api/users/{id}`
- **Authentication:** Required (JWT token)
- **Expected Result:**
  - Status 200 OK
  - User information by ID

---

## 3. Offer Management (Service Offers)

### 3.1 Create New Offer
- **Goal:** Show how a user can create a service offer
- **Endpoint:** `POST /api/offers`
- **Authentication:** Required (JWT token)
- **Request Body:**
  - title (required)
  - description (required)
  - hoursRate (required, positive number)
- **Expected Result:**
  - Status 201 Created
  - Created offer with unique ID
  - Offer status: ACTIVE

### 3.2 View All Active Offers
- **Goal:** Show list of available offers
- **Endpoint:** `GET /api/offers/active/list`
- **Parameters:** page, size (for pagination)
- **Expected Result:**
  - Status 200 OK
  - List of active offers with pagination

### 3.3 View All Offers (Including Inactive)
- **Goal:** Show complete list of all offers
- **Endpoint:** `GET /api/offers`
- **Parameters:** page, size (for pagination)
- **Expected Result:**
  - Status 200 OK
  - List of all offers

### 3.4 View Specific Offer
- **Goal:** Show detailed information about an offer
- **Endpoint:** `GET /api/offers/{offerId}`
- **Expected Result:**
  - Status 200 OK
  - Detailed offer information

### 3.5 View Offers by Specific User
- **Goal:** Show offers by a specific owner
- **Endpoint:** `GET /api/offers/owner/{ownerId}`
- **Parameters:** page, size (for pagination)
- **Expected Result:**
  - Status 200 OK
  - List of user's offers

### 3.6 View Own Offers
- **Goal:** Show list of current user's offers
- **Endpoint:** `GET /api/offers/my-offers`
- **Authentication:** Required (JWT token)
- **Expected Result:**
  - Status 200 OK
  - List of all current user's offers

### 3.7 Update Offer
- **Goal:** Show changing offer information
- **Endpoint:** `PUT /api/offers/{offerId}`
- **Authentication:** Required (JWT token), owner only
- **Request Body:** (same fields as when creating)
- **Expected Result:**
  - Status 200 OK
  - Updated offer

### 3.8 Deactivate Offer
- **Goal:** Show temporary hiding of an offer
- **Endpoint:** `PUT /api/offers/{offerId}/deactivate`
- **Authentication:** Required (JWT token), owner only
- **Expected Result:**
  - Status 204 No Content
  - Offer becomes inactive

### 3.9 Activate Offer
- **Goal:** Show restoring a previously deactivated offer
- **Endpoint:** `PUT /api/offers/{offerId}/activate`
- **Authentication:** Required (JWT token), owner only
- **Expected Result:**
  - Status 204 No Content
  - Offer becomes active

---

## 4. Booking Management

### 4.1 Create Booking
- **Goal:** Show the process of requesting a service from another user
- **Endpoint:** `POST /api/bookings`
- **Authentication:** Required (JWT token)
- **Request Body:**
  - offerId (required)
  - hours (required, positive number)
- **Expected Result:**
  - Status 201 Created
  - Created booking with status PENDING
  - Information about total amount (hours * hoursRate)

### 4.2 View Specific Booking
- **Goal:** Show detailed information about a booking
- **Endpoint:** `GET /api/bookings/{bookingId}`
- **Expected Result:**
  - Status 200 OK
  - Detailed booking information (status, participants, amount)

### 4.3 Confirm Booking by Offer Owner
- **Goal:** Show the process of accepting a service request
- **Endpoint:** `PUT /api/bookings/{bookingId}/confirm`
- **Authentication:** Required (JWT token), offer owner only
- **Expected Result:**
  - Status 200 OK
  - Booking changes to status CONFIRMED
  - Hours reservation for requester

### 4.4 Complete Booking
- **Goal:** Show the process of completing a performed service
- **Endpoint:** `PUT /api/bookings/{bookingId}/complete`
- **Authentication:** Required (JWT token), any participant can execute
- **Expected Result:**
  - Status 200 OK
  - Booking changes to status COMPLETED
  - Transaction execution: hours transfer from requester to owner

### 4.5 Cancel Booking
- **Goal:** Show the process of canceling a booking
- **Endpoint:** `PUT /api/bookings/{bookingId}/cancel?reason=<reason>`
- **Authentication:** Required (JWT token), any participant
- **Parameters:** reason (optional, default "No reason provided")
- **Expected Result:**
  - Status 200 OK
  - Booking changes to status CANCELLED
  - Return of reserved hours (if was confirmed)

### 4.6 View Own Bookings as Requester
- **Goal:** Show list of bookings where user requested service
- **Endpoint:** `GET /api/bookings/my/as-requester`
- **Authentication:** Required (JWT token)
- **Parameters:** page, size (for pagination)
- **Expected Result:**
  - Status 200 OK
  - List of bookings where current user is requester

### 4.7 View Own Bookings as Offer Owner
- **Goal:** Show list of bookings where user provides service
- **Endpoint:** `GET /api/bookings/my/as-owner`
- **Authentication:** Required (JWT token)
- **Parameters:** page, size (for pagination)
- **Expected Result:**
  - Status 200 OK
  - List of bookings where current user is offer owner

### 4.8 View Bookings by Specific Offer
- **Goal:** Show all bookings for a specific offer
- **Endpoint:** `GET /api/bookings/offer/{offerId}`
- **Parameters:** page, size (for pagination)
- **Expected Result:**
  - Status 200 OK
  - List of all bookings for the offer

### 4.9 View Bookings by Status
- **Goal:** Show filtering bookings by status
- **Endpoint:** `GET /api/bookings/status/{status}`
- **Parameters:** page, size (for pagination)
- **Possible Statuses:** PENDING, CONFIRMED, COMPLETED, CANCELLED
- **Expected Result:**
  - Status 200 OK
  - List of bookings with specified status

---

## 5. Complete Usage Scenario (End-to-End)

### Scenario 1: Service Exchange Between Two Users

1. **User A Registration**
   - Registration via `/api/auth/register`
   - Get token

2. **User B Registration**
   - Registration via `/api/auth/register`
   - Get token

3. **User A Creates Offer**
   - Create offer "Mathematics Tutoring"
   - Rate: 2.0 hours per hour of service

4. **User B Views Active Offers**
   - Get list of active offers
   - Find user A's offer

5. **User B Creates Booking**
   - Request for 3 hours of service
   - Booking with status PENDING

6. **User A Confirms Booking**
   - Confirm booking
   - Status changes to CONFIRMED
   - Reserve 6.0 hours for user B

7. **User A Completes Booking**
   - Complete service
   - Status changes to COMPLETED
   - Transfer 6.0 hours from user B to user A

8. **Balance Check**
   - View user information (wallet balances)

---

## 6. Error Handling Demonstration Cases

### 6.1 Attempt to Access Without Authentication
- **Test:** Request to protected endpoint without token
- **Expected Result:** Status 401 Unauthorized

### 6.2 Attempt to Create Booking with Insufficient Balance
- **Test:** Create booking for amount exceeding balance
- **Expected Result:** Status 400 Bad Request with appropriate message

### 6.3 Attempt to Confirm Someone Else's Booking
- **Test:** User tries to confirm booking where they are not the offer owner
- **Expected Result:** Status 403 Forbidden or 400 Bad Request

### 6.4 Attempt to Create Booking for Inactive Offer
- **Test:** Create booking for deactivated offer
- **Expected Result:** Status 400 Bad Request

### 6.5 Attempt to Create Booking for Own Offer
- **Test:** Create booking for own offer
- **Expected Result:** Status 400 Bad Request

---

## 7. Recommended Demonstration Order

1. **Introduction** (2 min)
   - Brief project description
   - Show startup via Docker

2. **Authentication** (3 min)
   - Register two users
   - Login

3. **Offer Management** (5 min)
   - Create offer
   - View active offers
   - Deactivate/activate

4. **Booking Lifecycle** (7 min)
   - Create booking
   - Confirm
   - Complete
   - View history

5. **Error Handling** (3 min)
   - Demonstrate validation
   - Demonstrate access control

**Total Time:** ~20 minutes
