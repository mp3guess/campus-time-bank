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

### ⚡ Самый простой способ (один клик):

**Windows:**
```batch
START.bat
```
*(Автоматически откроет два окна терминала и запустит всё)*

**Linux/Mac:**
```bash
./START.sh
```
*(Автоматически откроет два терминала и запустит всё)*

### 📝 Пошаговая инструкция:

#### Windows:
```batch
# Шаг 1: Перейдите в папку проекта
cd C:\Users\geldi\Desktop\time_bank

# Шаг 2: Запустите бэкенд (Терминал 1)
start-app.bat

# Шаг 3: Откройте НОВЫЙ терминал и запустите фронтенд (Терминал 2)
cd C:\Users\geldi\Desktop\time_bank
start-frontend.bat
```

#### Linux/Mac:
```bash
# Шаг 1: Перейдите в папку проекта
cd ~/Desktop/time_bank

# Шаг 2: Запустите бэкенд (Терминал 1)
./start-app.sh

# Шаг 3: Откройте НОВЫЙ терминал и запустите фронтенд (Терминал 2)
cd ~/Desktop/time_bank
./start-frontend.sh
```

**📖 Полная инструкция с командами:** См. `START_HERE.md`

### Альтернативные способы:
- **Только Docker**: `./start.sh` (Linux/Mac) или используйте `start-app.bat`/`start-app.sh`
- **Локально с Gradle**: `./gradlew bootRun` (автоматически загрузит Gradle если нужно)
- **Через IDE**: Откройте проект и запустите `CampusTimeBankApplication.main()`

**📖 Подробные инструкции:** См. `QUICKSTART.md`, `FIX_RUN_ISSUE.md` или `RUN_LOCALLY.md`

## 🔗 Access Points

- API: `http://localhost:8080/api`
- Health: `http://localhost:8080/actuator/health`
- Frontend: `http://localhost:8000/index.html` (после запуска `python -m http.server 8000`)
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

## 🐛 Troubleshooting

Если возникают проблемы с запуском, см. `FIX_RUN_ISSUE.md`
