# Campus TimeBank Frontend

Simple HTML frontend for the Campus TimeBank system.

## Quick Start

1. **Start the backend:**
   ```bash
   ./start.sh
   ```
   Wait for the application to be ready (usually takes 20-30 seconds).

2. **Test the API (optional):**
   ```bash
   ./test_frontend_api.sh
   ```

3. **Open the frontend:**
   - **Option 1:** Simply open `index.html` in your web browser (double-click the file)
   - **Option 2:** Use a simple HTTP server:
     ```bash
     python3 -m http.server 8000
     # Then open http://localhost:8000/index.html
     ```

## Features

### ✅ Authentication
- User registration with email, password, name, faculty
- User login
- Admin creation (first admin can be created without authentication)

### ✅ User Features
- **Profile:** View your profile and wallet balance (earned/spent hours)
- **Offers:**
  - Create service offers
  - View all active offers (with pagination)
  - View and manage your offers
  - Edit offers
  - Activate/deactivate offers
- **Bookings:**
  - Create bookings for offers
  - View your bookings as requester
  - View your bookings as owner
  - Confirm, complete, or cancel bookings

### ✅ Admin Features (ADMIN role only)
- Create new admin users
- View all users with pagination
- Activate/deactivate users
- Change user roles (STUDENT/ADMIN)
- View all transactions with pagination
- Filter transactions by user
- Filter transactions by type

## Usage Guide

### First Time Setup

1. **Register a new user:**
   - Fill in the registration form
   - Email must be unique
   - Password must be at least 6 characters
   - After registration, you'll be automatically logged in

2. **Create your first offer:**
   - Enter a title and description
   - Set the hours rate (how many hours per service)
   - Click "Create Offer"
   - Your offer will appear in "My Offers"

3. **Browse and book offers:**
   - Click "Refresh" in "All Active Offers" to see available services
   - To book an offer, note its ID and use it in the "Create Booking" form
   - Enter the number of hours you want to book

### Managing Your Offers

- **Edit:** Click "Edit" button, enter new values
- **Activate/Deactivate:** Toggle offer visibility
- Active offers appear in the public list, inactive offers are hidden

### Managing Bookings

**As Requester (you booked someone's service):**
- Cancel pending or confirmed bookings
- View booking status and details

**As Owner (someone booked your service):**
- Confirm pending bookings
- Complete confirmed bookings (this transfers hours)
- Cancel bookings if needed

### Admin Panel

If you have ADMIN role:
- Create additional admin users
- View and manage all users
- Monitor all transactions in the system
- Control user access and roles

## Troubleshooting

### "Cannot connect to server"
- Make sure the backend is running: `./start.sh`
- Check that the backend is accessible at `http://localhost:8080`
- Open browser console (F12) to see detailed error messages

### CORS Errors
- The backend CORS is configured to allow all origins
- If you see CORS errors, restart the backend after ensuring CORS config is correct

### Empty Lists
- If lists show "No items found", it's normal - create some data first
- Click "Refresh" buttons to reload data

### Token Expired
- If you get 403 errors, try logging out and logging in again
- Tokens expire after 24 hours

## API Base URL

The frontend is configured to use `http://localhost:8080/api` as the base URL.
If your backend runs on a different address, modify the `API_BASE` constant in `index.html`:

```javascript
const API_BASE = 'http://your-backend-url:port/api';
```

## Technical Details

- **No Dependencies:** Pure HTML, CSS, and vanilla JavaScript
- **Token Storage:** JWT tokens stored in browser localStorage
- **Error Handling:** Comprehensive error messages and validation
- **Security:** All sensitive operations require authentication
- **Responsive:** Works on desktop and mobile browsers

## Testing

Run the test script to verify API connectivity:
```bash
./test_frontend_api.sh
```

This will test:
- User registration
- User info retrieval
- Offer creation
- My offers retrieval
- Active offers retrieval

