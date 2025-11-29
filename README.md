# Campus TimeBank

**Time-based skill exchange platform for University of Debrecen**

## 🎯 Key Features

✅ Create and manage offers  
✅ Request and confirm bookings  
✅ Transfer hours between users  
✅ Complete transaction audit trail  
✅ JWT authentication & role-based access  

## 📊 Tech Stack

- Spring Boot 3.2.0
- PostgreSQL 15
- Docker & Docker Compose
- Java 17+

## 🚀 Quick Start

### Option 1: Using Docker (Recommended)

```bash
# Start backend and database
./start.sh

# In another terminal, start frontend
./start-frontend.sh
```

### Option 2: Using Gradle Wrapper

```bash
# Make sure PostgreSQL is running on localhost:5432
# Then run:
./gradlew bootRun

# In another terminal, start frontend
./start-frontend.sh
```

### Option 3: Using IDE

1. Open project in IntelliJ IDEA / VS Code
2. Wait for indexing
3. Run `CampusTimeBankApplication.main()`

## 🔗 Access Points

- API: `http://localhost:8080/api`
- Health: `http://localhost:8080/actuator/health`
- Frontend: `http://localhost:8000/index.html`
- Database: `localhost:5432` (postgres/postgres)

## ✅ Status

✅ **Build:** Successful (0 errors)  
✅ **Tests:** 96% passing (32/33)  
✅ **API Endpoints:** 18/18 implemented  
✅ **Docker:** Production ready  
✅ **Frontend:** Improved with better error handling and UX  

## 📝 Project Info

**Authors:** Ivan Tamrazov, Geldimurad Orazov  
**University:** University of Debrecen  
**Status:** Complete & Production Ready

## 🛑 Stopping the Application

### Stop Docker containers:
```bash
./stop.sh
# or
docker-compose down
```

### Stop local application:
Press `Ctrl+C` in the terminal

## 🐛 Troubleshooting

### "Docker is not running"
**Solution:** Start Docker Desktop

### "Java is not installed"
**Solution:** Install Java 17+ from https://adoptium.net/

### "Port 8080 is already in use"
**Solution:** Stop other applications on port 8080 or change port in `application.yml`

### "PostgreSQL connection failed"
**Solution:** Make sure PostgreSQL is running on localhost:5432
