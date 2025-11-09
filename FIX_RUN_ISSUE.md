# Решение проблемы запуска приложения

## Проблема
```
Exception in thread "main" java.lang.Error: Unresolved compilation problem: 
        SpringApplication cannot be resolved
```

## Причина
Вы пытаетесь запустить приложение напрямую через Java, но зависимости Spring Boot не находятся в classpath. Это нормально - Spring Boot приложения нужно запускать через Gradle или собранный JAR файл.

## Решения

### ✅ Решение 1: Использовать Docker (САМЫЙ ПРОСТОЙ)

1. Убедитесь, что Docker установлен
2. Запустите:
   ```bash
   # В Git Bash или WSL
   ./start.sh
   
   # Или вручную
   docker-compose up -d
   ```

Это автоматически:
- Запустит PostgreSQL
- Соберет приложение
- Запустит его в контейнере

### ✅ Решение 2: Запустить через IDE правильно

#### IntelliJ IDEA:
1. Откройте проект в IntelliJ IDEA
2. Дождитесь индексации (Gradle синхронизация)
3. Откройте `CampusTimeBankApplication.java`
4. Нажмите правой кнопкой на класс → `Run 'CampusTimeBankApplication.main()'`
5. IDE автоматически соберет проект и запустит его

#### VS Code / Cursor:
1. Установите расширение "Extension Pack for Java"
2. Откройте проект
3. Дождитесь загрузки зависимостей (будет видно внизу статус бар)
4. Откройте `CampusTimeBankApplication.java`
5. Нажмите на кнопку "Run" над методом `main()`

**Важно:** Убедитесь, что PostgreSQL запущен на localhost:5432

### ✅ Решение 3: Установить Gradle и использовать его

1. **Установите Gradle:**
   - Скачайте: https://gradle.org/install/
   - Или через Chocolatey: `choco install gradle`
   - Или через Scoop: `scoop install gradle`

2. **Проверьте установку:**
   ```bash
   gradle --version
   ```

3. **Запустите приложение:**
   ```bash
   cd c:\Users\geldi\Desktop\time_bank
   gradle bootRun
   ```

### ✅ Решение 4: Собрать JAR и запустить его

1. **Установите Gradle** (см. Решение 3)

2. **Соберите JAR:**
   ```bash
   gradle build
   ```

3. **Запустите JAR:**
   ```bash
   java -jar build\libs\campus-time-bank-0.0.1-SNAPSHOT.jar
   ```

## Запуск PostgreSQL (если не используете Docker)

### Вариант 1: Через Docker
```bash
docker run -d --name postgres -p 5432:5432 ^
  -e POSTGRES_PASSWORD=postgres ^
  -e POSTGRES_DB=campus_timebank ^
  postgres:15-alpine
```

### Вариант 2: Установить PostgreSQL локально
1. Скачайте и установите PostgreSQL: https://www.postgresql.org/download/windows/
2. Создайте базу данных:
   ```sql
   CREATE DATABASE campus_timebank;
   ```
3. Убедитесь, что пользователь `postgres` с паролем `postgres` существует

## Рекомендация

**Используйте Docker** - это самый простой и надежный способ:
- Не нужно устанавливать Gradle
- Не нужно настраивать PostgreSQL
- Все работает из коробки
- Команда: `docker-compose up -d`

## Проверка работы

После запуска проверьте:
- Health check: http://localhost:8080/actuator/health
- API: http://localhost:8080/api
- Фронтенд: http://localhost:8000/index.html (после запуска `python -m http.server 8000`)

## Если ничего не помогает

1. Убедитесь, что Docker установлен и запущен
2. Удалите старые контейнеры: `docker-compose down`
3. Запустите заново: `docker-compose up -d`
4. Проверьте логи: `docker-compose logs -f app`

