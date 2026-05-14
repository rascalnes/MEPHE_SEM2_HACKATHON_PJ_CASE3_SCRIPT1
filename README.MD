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
git clone https://github.com/rascalnes/MEPHE_SEM2_HACKATHON_PJ_CASE3_SCRIPT1_NES.git

cd MEPHE_SEM2_HACKATHON_PJ_CASE3_SCRIPT1_NES

### 2. Собрать приложение
./gradlew clean build

### 3. Запустить PostgreSQL и Adminer
docker-compose up -d

### 4. Запустить приложение
./gradlew run

### 5. Доступ
APIs:
    http://localhost:8080
Adminer (Database):
    http://localhost:8081
    login: lottery_user
    pass: lottery_pass
    db: lottery_db



## ОПИСАНИЕ И ПРИМЕРЫ

| Description                          | API                                 | Method | Request                                                                                                                                    | Response                                                                                                                                                                                                  |   |
|--------------------------------------|-------------------------------------|--------|--------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---|
| Состояние/работоспособность          | http://localhost:8080/health        | POST   | curl -X POST http://localhost:8080/health                                                                                                  | {"status":"ok","env":"development"}                                                                                                                                                                       | + |
| Регистрация                          | http://localhost:8080/auth/register | POST   | curl -X POST http://localhost:8080/auth/register -H "Content-Type: application/json" -d '{ "login": "john_doe", "password":"password123"}' | {"success":true,"token":"463661ca-fe23-4a95-a586-acc5a9d42606-1778760234462","message":"Authentication successful","user":{"id":"995edfc2-5b71-4624-9ec4-6c38f91a02db","login":"john_doe","role":"USER"}} | + |
| Авторизация                          | http://localhost:8080/auth/login    | POST   | curl -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d '{ "login": "john_doe", "password":"password123"}'    | {"success":true,"token":"0f2f2740-afdf-4a94-9fad-f68f237f16c3-1778760506199","message":"Authentication successful","user":{"id":"995edfc2-5b71-4624-9ec4-6c38f91a02db","login":"john_doe","role":"USER"}} | + |
| Авторизация (не верный логин/пароль) | http://localhost:8080/auth/login    | POST   | curl -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d '{ "login": "john_doe", "password":"badpassword"}'    | {"success":false,"message":"Invalid login or password"}                                                                                                                                                   | + |
|                                      |                                     |        |                                                                                                                                            |                                                                                                                                                                                                           |   |
|                                      |                                     |        |                                                                                                                                            |                                                                                                                                                                                                           |   |
|                                      |                                     |        |                                                                                                                                            |                                                                                                                                                                                                           |   |


