# Lottery Backend System

## Описание проекта
Бэкенд-система для проведения лотерейных тиражей с возможностью получения билетов, определения выигрышей и просмотра результатов.

## Технологический стек
- **Java 24**
- **Gradle 8.14 (Kotlin DSL)**
- **PostgreSQL 16**
- **Docker & Docker Compose**
- **JDBC (pure Java, no Spring)**
- **HikariCP Connection Pool**

## Запуск проекта

### Предварительные требования

- Docker & Docker Compose
- Java 24 (для локального запуска)
- Gradle 8.14

### 1. Клонировать репозиторий
```
git clone https://github.com/rascalnes/MEPHE_SEM2_HACKATHON_PJ_CASE3_SCRIPT1_NES.git
cd MEPHE_SEM2_HACKATHON_PJ_CASE3_SCRIPT1_NES
```

### 2. Собрать приложение
```
./gradlew clean build
```

### 3. Запустить PostgreSQL и Adminer
```
docker-compose up -d
```

### 4. Запустить приложение

```
./gradlew run
```
### 5. Доступ
APIs:
    http://localhost:8080
Adminer (Database):
    http://localhost:8081
    login: lottery_user
    pass: lottery_pass
    db: lottery_db

### 6. Структра базы данных (Модели БД)

Структура БД:
    1. users
    id				UUID / BIGSERIAL PRIMARY KEY
    login				VARCHAR(50) UNIQUE NOT NULL
    password			VARCHAR(255) NOT NULL
    role				VARCHAR(20) DEFAULT 'USER'	(ADMIN / USER)
    created_at			TIMESTAMP    

    2. draws
        id				BIGSERIAL PRIMARY KEY
        name				VARCHAR(100) NOT NULL
        status				VARCHAR(20) NOT NULL	(DRAFT, ACTIVE, FINISHED)
        created_by			UUID → users(id)
        created_at			TIMESTAMP
        started_at			TIMESTAMP (nullable)
        finished_at			TIMESTAMP (nullable)

    3. draw_results
        id				BIGSERIAL PRIMARY KEY
        draw_id				BIGINT → draws(id)
        winning_combo			VARCHAR(255) NOT NULL
        generated_at			TIMESTAMP
    
    4. tickets
        id				BIGSERIAL PRIMARY KEY
        draw_id				BIGINT → draws(id)
        user_id				UUID → users(id)
        numbers				VARCHAR(255) NOT NULL
        status				VARCHAR(20) NOT NULL	(PENDING, WIN, LOSE)
        checked_at			TIMESTAMP (nullable)
        purchased_at			TIMESTAMP

### 6. Архитектура приложения

src/main/java/ru/lottery/
├── controller/
│   ├── AuthController.java        // Обрабатывает /auth/register, /auth/login, /auth/logout
│   ├── DrawController.java        // Обрабатывает /draws (CRUD, start, finish)
│   ├── TicketController.java      // Обрабатывает /tickets (покупка, история, проверка)
│   └── ResultController.java      // Обрабатывает /draws/{id}/results (просмотр выигрышей)
├── service/
│   ├── AuthService.java           // Регистрация, логин, logout, создание сессии
│   ├── DrawService.java           // Создание, старт, финиш тиражей, генерация результатов
│   ├── TicketService.java         // Покупка билетов, проверка статуса, история
│   └── ResultService.java         // Получение выигрышных комбинаций, массовая проверка билетов
├── repository/
│   ├── UserRepository.java        // CRUD для users, поиск по login/uuid
│   ├── DrawRepository.java        // CRUD для draws, поиск по статусу
│   ├── TicketRepository.java      // CRUD для tickets, поиск по draw_id/user_id
│   └── DrawResultRepository.java  // CRUD для draw_results, поиск по draw_id
├── model/
│   ├── User.java                  // Пользователь (id, login, password, role, created_at)
│   ├── Draw.java                  // Тираж (id, name, status, created_by, timestamps)
│   ├── DrawResult.java            // Результат тиража (id, draw_id, winning_combo, generated_at)
│   ├── Ticket.java                // Билет (id, draw_id, user_id, numbers, status, timestamps)
│   └── enums/
│       ├── DrawStatus.java        // DRAFT, ACTIVE, FINISHED + логика переходов
│       ├── TicketStatus.java      // PENDING, WIN, LOSE
│       └── UserRole.java          // ADMIN, USER
├── dto/
│   ├── request/
│   │   ├── LoginRequest.java      // {login, password}
│   │   ├── CreateDrawRequest.java // {name}
│   │   ├── BuyTicketRequest.java  // {draw_id, numbers} (если numbers не переданы - генерируются)
│   │   └── RegisterRequest.java   // {login, password}
│   └── response/
│       ├── AuthResponse.java      // {success, token, message, user}
│       ├── DrawResponse.java      // {success, message, draw, winningCombo}
│       ├── TicketResponse.java    // {success, message, ticket, draw, result}
│       └── ResultResponse.java    // {draw, winningCombo, tickets: [{number, status}]}
├── security/
│   ├── AuthenticationFilter.java  // Проверяет Bearer-токен, добавляет userId/userRole в атрибуты
│   ├── PasswordEncoder.java       // PBKDF2 хеширование паролей (соль + итерации)
│   └── SessionManager.java        // In-memory хранение токенов (userId, role, expires)
├── util/
│   ├── CombinationGenerator.java  // Генерация 6 случайных чисел из 1-49, валидация комбинаций
│   ├── TicketChecker.java         // Сравнение билета с выигрышной комбинацией, обновление статусов
│   └── ValidationUtils.java       // Валидация логина, пароля, имени тиража, комбинации билета
└── config/
├── DatabaseConnection.java    // HikariCP пул соединений, getConnection(), closePool()
└── AppConfig.java             // Загрузка .env, настройки БД, порта, пула соединений

## ОПИСАНИЕ И ПРИМЕРЫ

| Description                                                  | API (endpoints)                        | Method | Request                                                                                                                                                          | Response                                                                                                                                                                                                                                                                        |
|--------------------------------------------------------------|----------------------------------------|--------|------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Состояние/работоспособность                                  | http://localhost:8080/health           | POST   | curl -X POST http://localhost:8080/health                                                                                                                        | {"status":"ok","env":"development"}                                                                                                                                                                                                                                             |
| Регистрация                                                  | http://localhost:8080/auth/register    | POST   | curl -X POST http://localhost:8080/auth/register -H "Content-Type: application/json" -d '{ "login": "john_doe", "password":"password123"}'                       | {"success":true,"token":"463661ca-fe23-4a95-a586-acc5a9d42606-1778760234462","message":"Authentication successful","user":{"id":"995edfc2-5b71-4624-9ec4-6c38f91a02db","login":"john_doe","role":"USER"}}                                                                       |
| Авторизация                                                  | http://localhost:8080/auth/login       | POST   | curl -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d '{ "login": "john_doe", "password":"password123"}'                          | {"success":true,"token":"0f2f2740-afdf-4a94-9fad-f68f237f16c3-1778760506199","message":"Authentication successful","user":{"id":"995edfc2-5b71-4624-9ec4-6c38f91a02db","login":"john_doe","role":"USER"}}                                                                       |
| Авторизация (неверный логин/пароль)                          | http://localhost:8080/auth/login       | POST   | curl -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d '{ "login": "john_doe", "password":"badpassword"}'                          | {"success":false,"message":"Invalid login or password"}                                                                                                                                                                                                                         |
| Выход                                                        | http://localhost:8080/auth/logout      | POST   | curl -X POST http://localhost:8080/auth/logout -H "Authorization: Bearer <токен>"                                                                                | {"message":"Logged out successfully"}                                                                                                                                                                                                                                           |
| Создание тиража (ADMIN)                                      | http://localhost:8080/draws            | POST   | curl -X POST http://localhost:8080/draws -H "Content-Type: application/json" -H "Authorization: Bearer <token>" -d '{"name":"Новогодняя лотерея 2025"}'          | {"success": true,"message": "Draw created successfully","draw": {"id": 1,"name": "Новогодняя лотерея 2025","status": "DRAFT","createdBy": "550e8400-e29b-41d4-a716-446655440000","createdAt": "2026-05-15T10:30:00","startedAt": null,"finishedAt": null},"winningCombo": null} |
| Получение активных тиражей                                   | http://localhost:8080/draws/active     | GET    | curl -X GET http://localhost:8080/draws/active -H "Authorization: Bearer <token>"                                                                                | {...}                                                                                                                                                                                                                                                                           |
| Запуск тиража (ADMIN)                                        | http://localhost:8080/draws/1/start    | POST   | curl -X POST http://localhost:8080/draws/1/start -H "Authorization: Bearer <token>"                                                                              | {"success": true,"message": "Draw started successfully","draw": {"id": 1,"status": "ACTIVE","startedAt": "2026-05-15T10:35:00"}}                                                                                                                                                |
| Завершение тиража с генерацией выигрышной комбинации (ADMIN) | http://localhost:8080/draws/1/finish   | POST   | curl -X POST http://localhost:8080/draws/1/finish -H "Authorization: Bearer <token>"                                                                             | {"success": true,"message": "Draw finished successfully","draw": {"id": 1,"status": "FINISHED","finishedAt": "2026-05-15T11:00:00"},"winningCombo": "5,12,23,34,41,48"}                                                                                                         |
| Покупка билета (автокомбинация)                              | http://localhost:8080/draws/1/tickets  | POST   | curl -s -X POST http://localhost:8080/draws/1/tickets -H "Authorization: Bearer $USER_TOKEN" -H "Content-Type: application/json" -d '{}'                         | {"id":14,"drawId":1,"numbers":"1,6,11,19,23,32","status":"PENDING","purchasedAt":"May 14, 2026, 1:24:18PM"}                                                                                                                                                                     |
| Покупка билета (ручная комбинация                            | http://localhost:8080/draws/1/tickets  | POST   | curl -s -X POST http://localhost:8080/draws/1/tickets -H "Authorization: Bearer $USER_TOKEN" -H "Content-Type: application/json" -d '{"numbers":"1,2,3,4,5,6"}'  | {"id":15,"drawId":1,"numbers":"1,2,3,4,5,6","status":"PENDING","purchasedAt":"May 14, 2026, 1:24:18PM"}                                                                                                                                                                         |
| Ошибка валидации (5 чисел)                                   | http://localhost:8080/draws/1/tickets  | POST   | curl -s -X POST http://localhost:8080/draws/1/tickets -H "Authorization: Bearer $USER_TOKEN" -H "Content-Type: application/json" -d '{"numbers":"1,2,3,4,5"}'    | {"error":"Некорректная комбинация чисел"} (400)                                                                                                                                                                                                                                 |
| Ошибка валидации (число >49)                                 | http://localhost:8080/draws/1/tickets  | POST   | curl -s -X POST http://localhost:8080/draws/1/tickets -H "Authorization: Bearer $USER_TOKEN" -H "Content-Type: application/json" -d '{"numbers":"1,2,3,4,5,50"}' | {"error":"Некорректная комбинация чисел"}` (400)                                                                                                                                                                                                                                |
| История билетов пользователя                                 | http://localhost:8080/tickets          | GET    | curl -s http://localhost:8080/tickets -H "Authorization: Bearer $USER_TOKEN"                                                                                     | {"id":16,"drawId":1,"numbers":"1,6,11,16,21,28","status":"PENDING","purchasedAt":"May 14, 2026, 4:35:02PM"}                                                                                                                                                                     |
| История билетов с фильтром по тиражу                         | http://localhost:8080/tickets?drawId=1 | GET    | curl -s "http://localhost:8080/tickets?drawId=1" -H "Authorization: Bearer $USER_TOKEN"                                                                          | Только билеты с `drawId=1` (аналогичный массив)                                                                                                                                                                                                                                 |


