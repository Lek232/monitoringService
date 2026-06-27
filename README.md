# Monitoring Service

Сервис для сбора, хранения и визуализации метрик микросервисов, а также управления триггерами для оповещений. Поддерживает интеграцию с Telegram, REST API, автоматический опрос метрик и гибкую систему триггеров.

## Возможности

- Сбор метрик - автоматический опрос /actuator/metrics и /actuator/health у микросервисов.
- Метрики PostgreSQL - сбор статистики запросов к БД через эндпоинт /api/postgres-metrics.
- Планировщик - периодический сбор метрик каждые 10 секунд (настраиваемо).
- Telegram-бот - отправка отчётов и уведомлений о срабатывании триггеров.
- Триггеры - гибкая система с поддержкой числовых и текстовых метрик, операторов сравнения, cooldown и состояния активности.
- HTML-отчёты - форматированные отчёты в виде моноширинного текста для Telegram и API.
- REST API - полное управление триггерами и получение отчётов через Swagger UI.
- Docker-поддержка - готовые контейнеры для PostgreSQL и самого сервиса.

## Технологии

- Язык: Kotlin 2.2.21
- Фреймворк: Spring Boot 4.0.1 (Web MVC, Data JPA)
- База данных: PostgreSQL 17 (с поддержкой pg_stat_statements)
- ORM: Hibernate через Spring Data JPA
- API документация: Springdoc OpenAPI 3.0.1 (Swagger)
- Telegram: Telegram Bots API (Long Polling)
- Сборка: Gradle (Kotlin DSL)
- Контейнеризация: Docker + Docker Compose
- HTTP-клиент: RestClient (синхронный)

## Установка и запуск

### Требования

- JDK 21 (или совместимая JRE 21)
- Docker и Docker Compose
- PostgreSQL 17 (если запуск без Docker)

### Локальный запуск (без Docker)

Клонируем репозиторий:
git clone https://github.com/your-username/monitoring-service.git
cd monitoring-service

Создаём базу данных PostgreSQL вручную:
createdb -U postgres metrics

Настраиваем переменные окружения (или редактируем application.yaml):
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/metrics
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=1234

Сборка и запуск:
./gradlew bootJar
java -jar build/libs/*.jar

### Запуск через Docker Compose (рекомендуется)

Клонируем репозиторий:
git clone https://github.com/your-username/monitoring-service.git
cd monitoring-service

Запускаем все контейнеры:
docker-compose up --build

После запуска:
- Сервис доступен по адресу: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- PostgreSQL: localhost:5432 (логин/пароль: postgres/1234)

## Конфигурация

Все настройки задаются в application.yaml или через переменные окружения.

Основные параметры:

services:
  list:
    - name: "example_service"
      url: "example_url"
      apiKey: "example_apiKey"

telegram:
  bot:
    enabled: true
    username: "example_bot_name"
    token: "YOUR_TELEGRAM_BOT_TOKEN"

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/metrics
    username: postgres
    password: "1234"

## Структура проекта

src/main/kotlin/com/test/monitoringService/
  component/              Usecase-компоненты (filler)
  configuration/          Конфигурации (BotConfig, ServiceConfig)
  controller/             REST-контроллеры (Report, Trigger, Notification)
  dao/                    DAO-слой (прямые SQL-запросы)
  model/                  DTO, Entity, Enum'ы
  repository/             Spring Data JPA репозитории
  service/                Бизнес-логика, REST-клиент, планировщик
  MonitoringServiceApplication.kt

src/main/resources/
  application.yaml        Основной конфиг
  schema.sql              Инициализация БД (pg_stat_statements)

build.gradle.kts          Сборка Gradle (Kotlin DSL)
docker-compose.yaml       Docker Compose
Dockerfile                Многоэтапный Dockerfile

## API Эндпоинты

Swagger UI доступен по адресу:
http://localhost:8080/swagger-ui.html

Основные эндпоинты:

GET /api/metrics - Получить метрики всех сервисов
GET /api/postgres - Получить метрики PostgreSQL
GET /api/triggers - Получить все триггеры
GET /api/triggers/active - Получить активные триггеры
POST /api/triggers - Создать триггер
PATCH /api/triggers/{triggerName} - Обновить триггер
PATCH /api/triggers/switch/{triggerName} - Включить/выключить триггер
DELETE /api/triggers/{triggerName} - Удалить триггер
GET /api/notifications - Последние 50 уведомлений

## Telegram-бот

Если включён (telegram.bot.enabled=true), бот отвечает на команды:

/start - Начать получать периодические отчёты
/stop - Остановить получение отчётов
/report - Получить отчёт по метрикам сейчас
/postgres - Получить отчёт по PostgreSQL
/triggers - Меню управления триггерами
/triggersHelp - Подробная справка по триггерам
/create/name,service,metric,operator,threshold,cooldown - Создать триггер
/enable/name - Активировать триггер
/disable/name - Отключить триггер
/delete/name - Удалить триггер
/all - Список всех триггеров
/allActive - Список активных триггеров
/allForService/name - Триггеры для конкретного сервиса

## Триггеры (система оповещений)

Доступные метрики:

Для сервиса:
- HEALTH_STATUS - состояние сервиса (текст)
- AVAILABILITY - доступность в процентах
- MEMORY_LOAD - загрузка памяти в процентах
- CPU_USAGE - использование CPU в процентах
- THREADS_LIVE - количество потоков
- CONSUMPTION_DIFFERENCE - рост потребления

Для базы данных (если есть):
- DATABASE_STATUS - состояние БД (текст)
- DATABASE_LOAD - нагрузка на БД в процентах
- TOTAL_QUERIES - общее число запросов
- TOTAL_CALLS - число вызовов
- MAX_TOTAL_TIME_MS - время самого долгого запроса (мс)
- AVG_EXEC_TIME_MS - среднее время выполнения (мс)
- MAX_STDDEV_EXEC_TIME_MS - максимальное отклонение (мс)
- AVG_CACHE_HIT - попадание в кэш в процентах

Операторы сравнения:

Числовые: EQ, NE, GT, LT, GTE, LTE
Текстовые: CONTAINS, NOT_CONTAINS

Пример создания триггера через API:

{
  "name": "high_cpu_trigger",
  "serviceName": "security",
  "metric": "CPU_USAGE",
  "operator": "GT",
  "threshold": "80",
  "cooldown": 5
}

## Контакты

Автор: Толкачев Олег
Email: olega232@gmail.com
Telegram: @Lek1752
Ссылка на проект: https://github.com/Lek232/monitoringService
