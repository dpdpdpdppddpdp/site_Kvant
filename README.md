# Kvant CRM — Система управления строительной компанией

Веб-приложение для строительной компании с лендингом, системой заявок, личным кабинетом пользователя и административной панелью.

## Технологии

### Backend
- **Java 17**
- **Spring Boot 3.1.5**
- **Spring Security** — аутентификация, авторизация, принудительный HTTPS
- **Spring Data JPA / Hibernate** — ORM, работа с базой данных
- **PostgreSQL** — реляционная база данных (3НФ)
- **HikariCP** — пул соединений с БД
- **Maven** — управление зависимостями
- **JavaMailSender** — отправка email с кодом верификации (SMTP mail.ru)

### Frontend
- **HTML5 / CSS3 / JavaScript** — разметка, стили, интерактивность
- **Fetch API** — взаимодействие с REST API

### Безопасность
- **HTTPS / TLS** — шифрование трафика (порт 8443), самоподписанный сертификат (PKCS12)
- **HTTP → HTTPS редирект** — порт 8081 автоматически перенаправляет на 8443
- **BCrypt** — хеширование паролей
- **JSESSIONID** — сессионные cookie для аутентификации

## Структура проекта

```
kvant_diplom/
├── src/main/
│   ├── java/com/kvant/
│   │   ├── config/          # SecurityConfig, HttpsRedirectConfig, WebConfig
│   │   ├── controller/      # REST контроллеры (Auth, Lead, Content, User, ...)
│   │   ├── dto/             # DTO объекты
│   │   ├── entity/          # JPA сущности (User, Lead, Client, Employee, Project, ...)
│   │   ├── repository/      # Spring Data репозитории
│   │   ├── security/        # CustomAuthenticationFailureHandler
│   │   └── service/         # Бизнес-логика (Lead, Email, Content, Excel, ...)
│   └── resources/
│       ├── application.properties
│       └── static/
│           ├── index.html       # Лендинг
│           ├── login.html       # Вход
│           ├── register.html    # Регистрация (с email-верификацией)
│           ├── profile.html     # Личный кабинет
│           ├── projects.html    # Страница проектов
│           ├── admin.html       # Панель заявок (ADMIN)
│           ├── cms.html         # CMS-панель контента (ADMIN)
│           ├── css/             # Стили
│           ├── js/              # Скрипты
│           └── images/          # Изображения проектов
└── pom.xml
```

## Требования

- **Java 17+**
- **Maven 3.6+**
- **PostgreSQL 14+** (локальный или удалённый)

## Установка и запуск

### 1. Клонировать репозиторий

```bash
git clone https://github.com/dpdpdpdppddpdp/site_Kvant.git
cd site_Kvant
```

### 2. Создать базу данных PostgreSQL

```sql
CREATE DATABASE kvant_db;
```

### 3. Настроить подключение

Отредактировать `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/kvant_db
spring.datasource.username=postgres
spring.datasource.password=ВАШ_ПАРОЛЬ

spring.mail.host=smtp.mail.ru
spring.mail.port=465
spring.mail.username=ВАШ_EMAIL
spring.mail.password=ВАШ_ПАРОЛЬ_ПРИЛОЖЕНИЯ
```

### 4. Запуск

```bash
mvn spring-boot:run
```

Приложение запустится на **https://localhost:8443**
Запросы на http://localhost:8081 автоматически редиректятся на HTTPS.

> При первом открытии браузер покажет предупреждение о самоподписанном сертификате — нажмите «Дополнительно → Перейти на сайт».

### 5. Создание администратора

```powershell
Invoke-WebRequest -Uri https://localhost:8443/api/auth/create-admin `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"username":"admin","password":"admin123","email":"admin@kvant.ru","firstName":"Admin","lastName":"User","phone":"+79294259774"}'
```

## Страницы приложения

| URL | Описание | Доступ |
|-----|----------|--------|
| `https://localhost:8443/` | Лендинг с формой заявки | Все |
| `https://localhost:8443/login.html` | Вход в систему | Все |
| `https://localhost:8443/register.html` | Регистрация с email-верификацией | Все |
| `https://localhost:8443/profile.html` | Личный кабинет | USER, ADMIN |
| `https://localhost:8443/projects.html` | Портфолио проектов | Все |
| `https://localhost:8443/admin.html` | Управление заявками | ADMIN |
| `https://localhost:8443/cms.html` | Редактирование контента сайта | ADMIN |

## API Endpoints

### Аутентификация
- `POST /api/auth/send-code` — отправить код верификации на email
- `POST /api/auth/register` — регистрация с подтверждением кода
- `POST /api/auth/register-direct` — регистрация без email-верификации
- `POST /api/auth/create-admin` — создание администратора
- `GET /api/auth/me` — текущий пользователь
- `PUT /api/auth/update-profile` — обновление профиля
- `POST /api/auth/change-password` — смена пароля
- `GET /api/auth/check-username` — проверка уникальности username
- `GET /api/auth/check-email` — проверка уникальности email

### Заявки
- `POST /api/leads` — создать заявку
- `GET /api/leads` — все заявки (ADMIN)
- `GET /api/leads/my` — заявки текущего пользователя
- `PUT /api/leads/{id}` — обновить статус заявки (ADMIN)
- `GET /api/leads/export` — экспорт в Excel (ADMIN)

### Контент
- `GET /api/content` — весь контент
- `GET /api/content/key/{key}` — контент по ключу
- `GET /api/content/section/{section}` — контент по секции
- `PUT /api/content/key/{key}` — обновить (ADMIN)
- `POST /api/content/initialize` — инициализация дефолтного контента (ADMIN)

### Пользователи (ADMIN)
- `GET /api/users` — все пользователи
- `PUT /api/users/{id}` — обновить пользователя
- `DELETE /api/users/{id}` — удалить пользователя
- `POST /api/users/{id}/block` — заблокировать
- `POST /api/users/{id}/unblock` — разблокировать

## Безопасность

- **HTTPS** — весь трафик шифруется TLS (порт 8443), HTTP (8081) редиректит на HTTPS
- **BCrypt** — пароли хранятся в хешированном виде
- **Session cookie** — аутентификация через JSESSIONID
- **Ролевая модель** — ADMIN и USER с разными правами доступа
- **Email-верификация** — при регистрации отправляется 6-значный код (действителен 10 минут)
- **Защита эндпоинтов** — `@PreAuthorize`, `requiresSecure()` в Spring Security

## База данных

PostgreSQL, схема в **3НФ**. Таблицы: `users`, `leads`, `clients`, `employees`, `projects`, `sessions`, `site_content`.

Hibernate управляет схемой автоматически (`ddl-auto=update`).

## Устранение неполадок

### Порт уже занят
```powershell
Get-Process -Name java | Stop-Process -Force
```

### Пересоздать схему БД
Изменить в `application.properties`:
```properties
spring.jpa.hibernate.ddl-auto=create
```
После первого запуска вернуть на `update`.

### Ошибка email
Используется SMTP mail.ru (порт 465, SSL). Убедитесь что включён «пароль приложения» в настройках почты.

## Лицензия

Проект создан в учебных целях.

## Контакты

- Email: info@kvant.ru
- Телефон: +7 (929) 425-97-74
