# Kvant CRM — Система управления строительной компанией

Веб-приложение для строительной компании: публичный лендинг с формой заявки, личный кабинет пользователя, административная панель и CMS для редактирования контента сайта.

**Продакшн:** [https://kvant-aga.amvera.io](https://kvant-aga.amvera.io)

---

## Содержание

1. [Технологический стек](#технологический-стек)
2. [Структура проекта](#структура-проекта)
3. [Требования](#требования)
4. [Запуск проекта](#запуск-проекта)
5. [Деплой на Amvera](#деплой-на-amvera)
6. [Страницы и API](#страницы-и-api)
7. [Особенности разработки](#особенности-разработки)
8. [Тестирование](#тестирование)
9. [Устранение неполадок](#устранение-неполадок)

---

## Технологический стек

### Backend

| Технология | Версия | Назначение |
|---|---|---|
| Java | 17 | Основной язык |
| Spring Boot | 3.1.5 | Фреймворк приложения |
| Spring Security | 6.x | Аутентификация и авторизация |
| Spring Data JPA | 3.x | Работа с БД через репозитории |
| Hibernate | 6.2 | ORM, генерация SQL |
| PostgreSQL | 17 | Реляционная база данных |
| HikariCP | встроен | Пул соединений с БД |
| JavaMailSender | встроен | Отправка email (SMTP) |
| Apache POI | 5.x | Генерация Excel-отчётов |
| Maven | 3.6+ | Сборка и управление зависимостями |

### Frontend

| Технология | Назначение |
|---|---|
| HTML5 | Разметка страниц |
| CSS3 | Стилизация, адаптивная вёрстка (Flexbox, Grid) |
| JavaScript (ES6+) | Интерактивность, работа с API |
| Fetch API | Асинхронные HTTP-запросы к бэкенду |
| SVG-иконки | Встроенные векторные иконки без сторонних библиотек |

### Инфраструктура

| Технология | Назначение |
|---|---|
| Amvera Cloud | Хостинг приложения (JVM-среда, порт 80) |
| CloudNativePG (CNPG) | Управляемый PostgreSQL 17 на Amvera |
| BCrypt | Хеширование паролей |
| JSESSIONID cookie | Сессионная аутентификация |
| SMTP mail.ru (порт 465, SSL) | Отправка email-уведомлений |

---

## Структура проекта

```
kvant_diplom/
├── amvera.yml                   # Конфигурация деплоя на Amvera
├── pom.xml
└── src/main/
    ├── java/com/kvant/
    │   ├── config/
    │   │   ├── SecurityConfig.java
    │   │   └── WebConfig.java
    │   ├── controller/
    │   │   ├── AuthController.java        # Регистрация, вход, профиль
    │   │   ├── LeadController.java        # Заявки
    │   │   ├── ClientController.java      # Клиенты
    │   │   ├── ProjectController.java     # Проекты
    │   │   ├── SiteContentController.java # CMS контент
    │   │   └── UserManagementController.java # Управление пользователями
    │   ├── dto/
    │   │   └── LeadRequestDto.java
    │   ├── entity/
    │   │   ├── User.java
    │   │   ├── Lead.java
    │   │   ├── Client.java
    │   │   ├── Employee.java
    │   │   ├── Project.java
    │   │   ├── Session.java
    │   │   └── SiteContent.java
    │   ├── repository/         # Spring Data JPA репозитории
    │   ├── security/
    │   │   └── CustomAuthenticationFailureHandler.java
    │   └── service/
    │       ├── LeadService.java
    │       ├── EmailService.java
    │       ├── SiteContentService.java
    │       ├── ExcelService.java
    │       ├── ClientService.java
    │       └── ProjectService.java
    └── resources/
        ├── application.properties           # Базовый профиль (локальный)
        ├── application-prod.properties      # Продакшн профиль (Amvera)
        └── static/
            ├── index.html        # Лендинг
            ├── login.html        # Вход
            ├── register.html     # Регистрация
            ├── profile.html      # Личный кабинет
            ├── projects.html     # Портфолио
            ├── admin.html        # Административная панель (ADMIN)
            └── cms.html          # CMS контента (ADMIN)
```

---

## Требования

- **Java 17+**
- **Maven 3.6+**
- **PostgreSQL 14+**

---

## Запуск проекта

### Локальный запуск

**Шаг 1.** Клонировать репозиторий:
```bash
git clone https://github.com/dpdpdpdppddpdp/site_Kvant.git
cd site_Kvant/kvant_diplom
```

**Шаг 2.** Создать базу данных:
```sql
CREATE DATABASE kvant_db;
```

**Шаг 3.** Задать параметры в `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/kvant_db
spring.datasource.username=postgres
spring.datasource.password=ВАШ_ПАРОЛЬ

spring.mail.host=smtp.mail.ru
spring.mail.port=465
spring.mail.username=ВАШ_EMAIL
spring.mail.password=ВАШ_ПАРОЛЬ_ПРИЛОЖЕНИЯ
```

**Шаг 4.** Запустить:
```bash
mvn spring-boot:run
```

Приложение доступно по адресу: **http://localhost:8080**

При первом запуске автоматически создаётся администратор: `admin` / `admin123`.

---

## Деплой на Amvera

Приложение задеплоено на [Amvera Cloud](https://amvera.ru) и доступно по адресу **https://kvant-aga.amvera.io**.

### Конфигурация (`amvera.yml`)

```yaml
meta:
  environment: jvm
  toolchain:
    name: maven
    version: "17"
build:
  args: clean package -DskipTests
  artifacts:
    target/*.jar: /
run:
  jarName: kvant-crm-1.0.0.jar
  persistenceMount: /data
  containerPort: "80"
  servicePort: "80"
```

### Переменные окружения на Amvera

| Переменная | Значение |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `SERVER_PORT` | `80` |
| `SSL_ENABLED` | `false` |
| `DB_URL` | `jdbc:postgresql://amvera-aga-cnpg-kvant-db-rw:5432/kvantDB` |
| `DB_USERNAME` | имя пользователя БД |
| `DB_PASSWORD` | пароль БД |
| `MAIL_USERNAME` | email для SMTP |
| `MAIL_PASSWORD` | пароль приложения SMTP |
| `JAVA_TOOL_OPTIONS` | `-Djava.net.preferIPv4Stack=true` |

### База данных

Используется **CloudNativePG PostgreSQL 17** внутри Amvera (проект `kvant-db`).
Хост внутри кластера: `amvera-aga-cnpg-kvant-db-rw`.
Схема создаётся автоматически Hibernate при первом старте (`ddl-auto=update`).

### Обновление деплоя

```bash
git push origin main
```

После push — нажать **«Пересобрать»** в дашборде Amvera.

---

## Страницы и API

### Страницы приложения

| URL | Описание | Доступ |
|-----|----------|--------|
| `/` | Лендинг с формой заявки | Все |
| `/login.html` | Вход в систему | Все |
| `/register.html` | Регистрация с email-верификацией | Все |
| `/profile.html` | Личный кабинет | USER, MANAGER, ADMIN |
| `/projects.html` | Портфолио проектов | Все |
| `/admin.html` | Административная панель | ADMIN |
| `/cms.html` | Редактирование контента | ADMIN |

### REST API

<details>
<summary><b>Аутентификация</b></summary>

| Метод | URL | Описание |
|-------|-----|----------|
| POST | `/api/auth/send-code` | Отправить код верификации на email |
| POST | `/api/auth/register` | Регистрация с подтверждением кода |
| POST | `/api/auth/register-direct` | Регистрация без email-верификации |
| POST | `/api/auth/create-admin` | Создание администратора |
| GET | `/api/auth/me` | Текущий пользователь |
| PUT | `/api/auth/update-profile` | Обновление профиля |
| POST | `/api/auth/change-password` | Смена пароля |
| GET | `/api/auth/check-username` | Проверка уникальности username |
| GET | `/api/auth/check-email` | Проверка уникальности email |

</details>

<details>
<summary><b>Заявки</b></summary>

| Метод | URL | Описание | Доступ |
|-------|-----|----------|--------|
| POST | `/api/leads` | Создать заявку | Все |
| GET | `/api/leads` | Все заявки | ADMIN |
| GET | `/api/leads/my` | Заявки текущего пользователя | USER |
| PUT | `/api/leads/{id}` | Обновить статус заявки | ADMIN |
| GET | `/api/leads/export` | Экспорт заявок в Excel | ADMIN |

</details>

<details>
<summary><b>Контент сайта</b></summary>

| Метод | URL | Описание | Доступ |
|-------|-----|----------|--------|
| GET | `/api/content` | Весь контент | Все |
| GET | `/api/content/key/{key}` | Контент по ключу | Все |
| GET | `/api/content/section/{section}` | Контент по секции | Все |
| PUT | `/api/content/key/{key}` | Обновить значение | ADMIN |
| POST | `/api/content/initialize` | Инициализация дефолтного контента | ADMIN |

</details>

<details>
<summary><b>Управление пользователями</b></summary>

| Метод | URL | Описание | Доступ |
|-------|-----|----------|--------|
| GET | `/api/users` | Все пользователи | ADMIN |
| POST | `/api/users` | Создать пользователя | ADMIN |
| GET | `/api/users/{id}` | Пользователь по ID | ADMIN |
| PUT | `/api/users/{id}` | Обновить пользователя | ADMIN |
| DELETE | `/api/users/{id}` | Удалить пользователя | ADMIN |
| POST | `/api/users/{id}/block` | Заблокировать | ADMIN |
| POST | `/api/users/{id}/unblock` | Разблокировать | ADMIN |
| POST | `/api/users/{id}/reset-password` | Сбросить пароль | ADMIN |

</details>

---

## Особенности разработки

### Архитектура
- Монолитное Spring Boot приложение по паттерну **Controller → Service → Repository**
- Фронтенд — статические HTML/JS файлы, взаимодействуют с бэкендом через REST API (без Thymeleaf)
- Два Spring-профиля: `default` (локальный) и `prod` (Amvera)

### База данных
- **PostgreSQL 17** в **третьей нормальной форме (3НФ)**
- Схема управляется Hibernate (`ddl-auto=update`) — таблицы создаются автоматически при старте
- Пул соединений **HikariCP** (встроен в Spring Boot)

### Безопасность
- Пароли хранятся в хешированном виде (**BCrypt**, strength=10)
- Аутентификация через сессионные cookie **JSESSIONID**
- Регистрация двухшаговая: на email отправляется **6-значный код** (действителен 10 минут)

### CMS
- Контент сайта хранится в таблице `site_content` в виде пар ключ–значение
- Редактирование на превью страницы — клик по тексту открывает inline-редактор
- Изменения сохраняются в БД и сразу отображаются на сайте

### Frontend
- Чистый **Vanilla JS** (ES6+), без фреймворков
- Адаптивная вёрстка на **CSS Grid и Flexbox** — работает на мобильных, планшетах и десктопе
- Бургер-меню для экранов до 1024px
- Анимации появления — **IntersectionObserver API**

---

## Тестирование

### 1. Регистрация пользователя

```
1. Открыть /register.html
2. Заполнить форму: username, email, password (мин. 8 символов)
3. Нажать "Отправить код" — на email придёт 6-значный код
4. Ввести код и нажать "Подтвердить"
Ожидаемый результат: редирект на /login.html
```

### 2. Вход и личный кабинет

```
1. Открыть /login.html, ввести username и password
Ожидаемый результат:
  - Роль USER/MANAGER → редирект на /profile.html
  - Роль ADMIN → редирект на /admin.html
```

### 3. Отправка заявки

```
1. Открыть / (лендинг)
2. Заполнить форму "Оставить заявку" (имя, телефон, услуга)
3. Нажать "Отправить"
Ожидаемый результат: сообщение об успехе, заявка появляется в /admin.html,
на email администратора приходит уведомление
```

### 4. Административная панель

```
1. Войти как admin / admin123
2. Открыть /admin.html
3. Проверить список заявок, изменить статус одной из них
4. Нажать "Экспорт Excel" — скачается .xlsx файл
```

### 5. CMS — редактирование контента

```
1. Войти как ADMIN, открыть /cms.html
2. Кликнуть на любой текст в превью → откроется редактор
3. Изменить текст, нажать "Сохранить"
Ожидаемый результат: текст обновился на лендинге без перезапуска сервера
```

### 6. Создание сотрудника

```
1. Войти как ADMIN, открыть /admin.html
2. Раздел "Пользователи" → "Создать пользователя"
3. Заполнить: username, password, email, роль (MANAGER)
Ожидаемый результат: пользователь появился в списке
```

---

## Устранение неполадок

### Локально — порт уже занят
```powershell
Get-Process -Name java | Stop-Process -Force
```

### Пересоздать схему БД с нуля
В `application.properties` временно изменить:
```properties
spring.jpa.hibernate.ddl-auto=create
```
После первого запуска вернуть на `update`.

### Ошибка отправки email
- SMTP хост: `smtp.mail.ru`, порт `465`, SSL включён
- Использовать **пароль приложения** (не основной пароль от почты)

---

## Контакты

- **Сайт:** https://kvant-aga.amvera.io
- **Email:** info@kvant.ru

---

*Проект разработан в учебных целях.*
