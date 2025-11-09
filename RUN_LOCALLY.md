# Запуск приложения локально (без Docker)

## Вариант 1: Через Gradle (рекомендуется)

### Требования:
- Java 17 или выше
- Gradle установлен (или используйте Gradle Wrapper)
- PostgreSQL запущен на localhost:5432

### Шаги:

1. **Убедитесь, что PostgreSQL запущен:**
   ```bash
   # Через Docker (если установлен)
   docker run -d --name postgres -p 5432:5432 ^
     -e POSTGRES_PASSWORD=postgres ^
     -e POSTGRES_DB=campus_timebank ^
     postgres:15-alpine
   ```

2. **Запустите приложение:**
   ```bash
   # Windows
   gradle bootRun
   
   # Или используйте скрипт
   run.bat
   ```

3. **Проверьте, что приложение запустилось:**
   ```bash
   curl http://localhost:8080/actuator/health
   ```

## Вариант 2: Через IDE (IntelliJ IDEA / Eclipse / VS Code)

### IntelliJ IDEA:
1. Откройте проект
2. Дождитесь индексации и загрузки зависимостей
3. Найдите класс `CampusTimeBankApplication`
4. Нажмите правой кнопкой → Run 'CampusTimeBankApplication.main()'

### VS Code / Cursor:
1. Установите расширение "Extension Pack for Java"
2. Откройте проект
3. Откройте файл `CampusTimeBankApplication.java`
4. Нажмите на кнопку "Run" над методом `main()`

**Важно:** Убедитесь, что:
- PostgreSQL запущен на localhost:5432
- База данных `campus_timebank` создана
- Пользователь `postgres` с паролем `postgres` существует

## Вариант 3: Через Docker (самый простой)

```bash
# Windows (через Git Bash или WSL)
./start.sh

# Или вручную
docker-compose up -d
```

## Решение проблем

### Ошибка "SpringApplication cannot be resolved"
**Причина:** Зависимости не загружены или проект не собран.

**Решение:**
1. Загрузите зависимости через Gradle:
   ```bash
   gradle build --refresh-dependencies
   ```

2. Или используйте IDE для синхронизации Gradle проекта

3. Или используйте Docker (самый надежный способ)

### Ошибка подключения к базе данных
**Причина:** PostgreSQL не запущен или недоступен.

**Решение:**
1. Проверьте, что PostgreSQL запущен:
   ```bash
   # Windows
   netstat -an | findstr 5432
   ```

2. Запустите PostgreSQL через Docker:
   ```bash
   docker run -d --name postgres -p 5432:5432 ^
     -e POSTGRES_PASSWORD=postgres ^
     -e POSTGRES_DB=campus_timebank ^
     postgres:15-alpine
   ```

### Ошибка компиляции Java
**Причина:** Версия Java не совместима.

**Решение:**
- Убедитесь, что используется Java 17 или выше
- Проверьте версию: `java -version`
- В build.gradle указана Java 17, но можно использовать Java 17+

## Быстрый старт

1. **Запустите PostgreSQL:**
   ```bash
   docker run -d --name postgres -p 5432:5432 ^
     -e POSTGRES_PASSWORD=postgres ^
     -e POSTGRES_DB=campus_timebank ^
     postgres:15-alpine
   ```

2. **Запустите приложение:**
   ```bash
   gradle bootRun
   ```

3. **Откройте фронтенд:**
   ```bash
   # В другом терминале
   python -m http.server 8000
   # Откройте http://localhost:8000/index.html
   ```

## Проверка работы

После запуска проверьте:
- Health check: http://localhost:8080/actuator/health
- API: http://localhost:8080/api
- Фронтенд: http://localhost:8000/index.html

