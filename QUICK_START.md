# Quick Start - Campus TimeBank

## Startup (3 commands)

```bash
# 1. Start services
./start.sh

# 2. Wait 15 seconds and check
sleep 15 && curl http://localhost:8080/actuator/health

# 3. Run demonstration
bash PRESENTATION_REQUESTS.sh
```

## Stop

```bash
./stop.sh
```

## Check status

```bash
docker-compose ps
curl http://localhost:8080/actuator/health
```

## View logs

```bash
docker-compose logs -f app
```

## Quick registration and test

```bash
# Register user A
TOKEN_A=$(curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"password123","firstName":"John","lastName":"Doe","faculty":"Computer Science","studentId":"DE123456"}' \
  | python3 -c "import sys, json; print(json.load(sys.stdin)['token'])" 2>/dev/null)

# Check token
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer $TOKEN_A"
```

## Detailed instructions

See file `INSTRUCTION.md`
