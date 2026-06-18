# Kvant CRM — Система управления строительной компанией

Веб-приложение для строительной компании: публичный лендинг с формой заявки, личный кабинет пользователя, административная панель заявок и CMS для редактирования контента сайта.

---

## Содержание

1. [Технологический стек](#технологический-стек)
2. [Структура проекта](#структура-проекта)
3. [Требования](#требования)
4. [Запуск проекта](#запуск-проекта)
5. [Страницы и API](#страницы-и-api)
6. [Особенности разработки](#особенности-разработки)
7. [Тестирование](#тестирование)
8. [Устранение неполадок](#устранение-неполадок)

---

## Технологический стек

### Backend

| Технология | Версия | Назначение |
|---|---|---|
| Java | 17 | Основной язык |
| Spring Boot | 3.1.5 | Фреймворк приложения |
| Spring Security | 6.x | Аутентификация, авторизация, HTTPS |
| Spring Data JPA | 3.x | Работа с БД через репозитории |
| Hibernate | 6.2 | ORM, генерация SQL |
| PostgreSQL | 14+ | Реляционная база данных |
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

### Безопасность и инфраструктура

| Технология | Назначение |
|---|---|
| TLS / HTTPS | Шифрование трафика, порт 8443 |
| PKCS12 (keystore.p12) | Самоподписанный SSL-сертификат |
| BCrypt | Хеширование паролей |
| JSESSIONID cookie | Сессионная аутентификация |
| SMTP mail.ru (порт 465, SSL) | Отправка кодов верификации |

---

## Структура проекта

```
kvant_diplom/
├── pom.xml
└── src/main/
    ├── java/com/kvant/
    │   ├── config/
    │   │   ├── SecurityConfig.java        # Spring Security + HTTPS
    │   │   ├── HttpsRedirectConfig.java   # HTTP → HTTPS редирект
    │   │   ├── DatabaseMigration.java     # Инициализация данных при старте
    │   │   └── WebConfig.java             # CORS и MVC настройки
    │   ├── controller/
    │   │   ├── AuthController.java        # Регистрация, вход, профиль
    │   │   ├── LeadController.java        # Заявки
    │   │   ├── SiteContentController.java # CMS контент
    │   │   ├── UserManagementController.java
    │   │   ├── ClientController.java
    │   │   └── ProjectController.java
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
        ├── application.properties
        └── static/
            ├── index.html        # Лендинг
            ├── login.html        # Вход
            ├── register.html     # Регистрация
            ├── profile.html      # Личный кабинет
            ├── projects.html     # Портфолио
            ├── admin.html        # Панель заявок (ADMIN)
            ├── cms.html          # CMS контента (ADMIN)
            ├── css/style.css
            ├── js/main.js
            └── images/projects/  # Фотографии объектов
```

---

## Требования

- **Java 17+**
- **Maven 3.6+**
- **PostgreSQL 14+**

---

## Запуск проекта

### Вариант 1 — Локальный запуск через Maven

**Шаг 1.** Клонировать репозиторий:
```bash
git clone https://github.com/dpdpdpdppddpdp/site_Kvant.git
cd site_Kvant
```

**Шаг 2.** Создать базу данных:
```sql
CREATE DATABASE kvant_db;
```

**Шаг 3.** Задать параметры подключения в `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/kvant_db
spring.datasource.username=postgres
spring.datasource.password=ВАШ_ПАРОЛЬ

spring.mail.host=smtp.mail.ru
spring.mail.port=465
spring.mail.username=ВАШ_EMAIL
spring.mail.password=ВАШ_ПАРОЛЬ_ПРИЛОЖЕНИЯ
```

**Шаг 4.** Запустить приложение:
```bash
mvn spring-boot:run
```

Приложение будет доступно по адресу: **https://localhost:8443**

---

### Вариант 2 — Сборка JAR и запуск

```bash
# Собрать JAR-файл
mvn clean package -DskipTests

# Запустить собранный JAR
java -jar target/kvant-crm-1.0.0.jar
```

---

### Вариант 3 — Запуск на Windows через PowerShell

```powershell
# Остановить предыдущий запущенный процесс (если есть)
Get-Process -Name java -ErrorAction SilentlyContinue | Stop-Process -Force

# Запустить в фоне с выводом логов в файл
Start-Process -FilePath "mvn" `
  -ArgumentList "spring-boot:run", "-f", "pom.xml" `
  -RedirectStandardOutput "run.log" `
  -NoNewWindow
```

---

### После запуска — создание первого администратора

```powershell
Invoke-WebRequest -Uri "https://localhost:8443/api/auth/create-admin" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"username":"admin","password":"admin123","email":"admin@kvant.ru","firstName":"Admin","lastName":"User","phone":"+79294259774"}'
```

> **Примечание:** При первом открытии браузер покажет предупреждение о самоподписанном сертификате.
> Нажмите «Дополнительно» → «Перейти на сайт» (chrome) или «Принять риск» (firefox).

---

## Страницы и API

### Страницы приложения

| URL | Описание | Доступ |
|-----|----------|--------|
| `https://localhost:8443/` | Лендинг с формой заявки | Все |
| `https://localhost:8443/login.html` | Вход в систему | Все |
| `https://localhost:8443/register.html` | Регистрация с email-верификацией | Все |
| `https://localhost:8443/profile.html` | Личный кабинет | USER, ADMIN |
| `https://localhost:8443/projects.html` | Портфолио проектов | Все |
| `https://localhost:8443/admin.html` | Управление заявками | ADMIN |
| `https://localhost:8443/cms.html` | Редактирование контента | ADMIN |

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

| Метод | URL | Описание |
|-------|-----|----------|
| GET | `/api/users` | Все пользователи |
| PUT | `/api/users/{id}` | Обновить пользователя |
| DELETE | `/api/users/{id}` | Удалить пользователя |
| POST | `/api/users/{id}/block` | Заблокировать |
| POST | `/api/users/{id}/unblock` | Разблокировать |

</details>

---

## Особенности разработки

### Архитектура
- Монолитное Spring Boot приложение по паттерну **Controller → Service → Repository**
- Нет шаблонизаторов (Thymeleaf и т.п.) — фронтенд полностью на статических HTML/JS файлах, взаимодействующих с бэкендом через REST API

### База данных
- **PostgreSQL** в **третьей нормальной форме (3НФ)**: нет транзитивных зависимостей, все FK явные
- Схема управляется Hibernate (`ddl-auto=update`) — таблицы создаются и мигрируются автоматически при старте
- Пул соединений **HikariCP** (встроен в Spring Boot)

### Безопасность
- Весь трафик шифруется по **HTTPS/TLS** (порт 8443). HTTP (порт 8081) отдаёт редирект 302 на HTTPS
- SSL-сертификат — самоподписанный **PKCS12**, генерируется через `keytool`
- Пароли хранятся только в хешированном виде (**BCrypt**, strength=10)
- Аутентификация — через сессионные cookie **JSESSIONID**, сессия хранится в памяти сервера
- Регистрация двухшаговая: сначала на email отправляется **6-значный код** (действителен 10 минут), затем подтверждение

### CMS (управление контентом)
- Контент сайта (заголовки, телефон, адрес и т.д.) хранится в таблице `site_content` в виде пар ключ–значение
- Редактирование прямо на превью страницы — клик по тексту открывает inline-редактор
- Изменения сохраняются в БД и мгновенно отображаются на сайте

### Frontend
- Без фреймворков — чистый **Vanilla JS** (ES6+)
- Адаптивная вёрстка на **CSS Grid и Flexbox**, без Bootstrap
- Иконки — встроенные **SVG** (нет зависимости от иконочных шрифтов)
- Анимации появления элементов — **IntersectionObserver API**

---

## Тестирование

Ниже приведены ручные сценарии проверки ключевых функций.

### 1. Регистрация пользователя

```
1. Открыть https://localhost:8443/register.html
2. Заполнить форму: username, email, password (мин. 8 символов)
3. Нажать "Отправить код" — на email придёт 6-значный код
4. Ввести код в поле и нажать "Подтвердить"
Ожидаемый результат: редирект на /login.html
```

### 2. Вход и личный кабинет

```
1. Открыть https://localhost:8443/login.html
2. Ввести username и password → нажать "Войти"
Ожидаемый результат:
  - Роль USER → редирект на /profile.html
  - Роль ADMIN → редирект на /admin.html
```

### 3. Отправка заявки

```
1. Открыть https://localhost:8443/ (лендинг)
2. Заполнить форму "Оставить заявку" (имя, телефон, услуга)
3. Нажать "Отправить"
Ожидаемый результат: сообщение об успехе, заявка появляется в /admin.html
```

### 4. Административная панель

```
1. Войти как ADMIN
2. Открыть https://localhost:8443/admin.html
3. Проверить список заявок, изменить статус одной из них
4. Нажать "Экспорт Excel" — скачается .xlsx файл с заявками
```

### 5. CMS — редактирование контента

```
1. Войти как ADMIN, открыть https://localhost:8443/cms.html
2. Вкладка "Конструктор": кликнуть на любой текст в превью → откроется редактор
3. Изменить текст, нажать "Сохранить"
Ожидаемый результат: текст обновился на лендинге без перезапуска сервера
```

### 6. Проверка защиты маршрутов

```
Без авторизации попробовать открыть:
  - https://localhost:8443/admin.html  → редирект на /login.html
  - https://localhost:8443/cms.html   → редирект на /login.html
  - https://localhost:8443/profile.html → редирект на /login.html

Под ролью USER попробовать открыть:
  - https://localhost:8443/admin.html → 403 Forbidden
```

### 7. Проверка HTTPS-редиректа

```
Открыть http://localhost:8081/
Ожидаемый результат: автоматический редирект на https://localhost:8443/
```

---

## Устранение неполадок

### Порт 8443 уже занят
```powershell
Get-Process -Name java | Stop-Process -Force
```

### Пересоздать схему БД с нуля
В `application.properties` временно изменить:
```properties
spring.jpa.hibernate.ddl-auto=create
```
После первого запуска вернуть на `update`.

### Ошибка SSL-сертификата
Если `keystore.p12` отсутствует, сгенерировать заново:
```bash
keytool -genkeypair -alias kvant -keyalg RSA -keysize 2048 \
  -storetype PKCS12 -keystore src/main/resources/keystore.p12 \
  -validity 3650 -storepass kvant2024 \
  -dname "CN=localhost, OU=Kvant, O=Kvant, L=Moscow, ST=Moscow, C=RU"
```

### Ошибка отправки email
Проверить:
- SMTP хост: `smtp.mail.ru`, порт `465`, SSL включён
- Используется **пароль приложения** (не основной пароль от почты)

---

## Контакты

- **Сайт:** https://localhost:8443
- **Email:** info@kvant.ru
- **Телефон:** +7 (929) 425-97-74

---

*Проект разработан в учебных целях.*
