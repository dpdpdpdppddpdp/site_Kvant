# Kvant CRM - Система управления строительной компанией

## Описание проекта

Система управления строительной компанией с динамическим контентом, системой пользователей и административной панелью.

## Технологии

### Backend
- **Java 17**
- **Spring Boot 3.1.5**
- **Spring Security** - аутентификация и авторизация
- **Spring Data JPA** - работа с базой данных
- **Hibernate** - ORM
- **SQLite** - база данных
- **Maven** - управление зависимостями

### Frontend
- **HTML5** - разметка
- **CSS3** - стилизация
- **JavaScript** - интерактивность
- **Fetch API** - взаимодействие с бэкендом

## Структура проекта

```
kvant_diplom/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/kvant/
│   │   │       ├── config/          # Конфигурация Spring
│   │   │       ├── controller/      # REST контроллеры
│   │   │       ├── entity/          # Сущности базы данных
│   │   │       ├── repository/      # Репозитории JPA
│   │   │       ├── security/        # Безопасность
│   │   │       └── service/         # Бизнес-логика
│   │   └── resources/
│   │       ├── application.properties # Конфигурация приложения
│   │       └── static/              # Статические файлы
│   │           ├── index.html       # Лендинг страница
│   │           ├── login.html       # Страница входа
│   │           ├── register.html    # Страница регистрации
│   │           ├── profile.html     # Личный кабинет
│   │           ├── admin.html       # Админ панель (заявки)
│   │           └── cms.html         # CMS панель (контент)
└── pom.xml                           # Maven конфигурация
```

## Требования

- **Java 17** или выше
- **Maven 3.6** или выше
- **Любой современный браузер**

## Установка и запуск

### 1. Клонирование проекта

```bash
cd c:/Users/Otez/Desktop/kvant/kvant_diplom
```

### 2. Сборка проекта

```bash
mvn clean package
```

### 3. Запуск приложения

```bash
mvn spring-boot:run
```

Приложение будет доступно по адресу: http://localhost:8081

### 4. Создание администратора

После запуска приложения создайте первого администратора через API:

```bash
curl -X POST http://localhost:8081/api/auth/create-admin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123",
    "email": "admin@kvant.ru",
    "firstName": "Admin",
    "lastName": "User",
    "phone": "+79294259774"
  }'
```

Или через PowerShell:

```powershell
Invoke-WebRequest -Uri http://localhost:8081/api/auth/create-admin `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"username":"admin","password":"admin123","email":"admin@kvant.ru","firstName":"Admin","lastName":"User","phone":"+79294259774"}'
```

## Использование

### Страницы приложения

- **http://localhost:8081/** - Лендинг страница
- **http://localhost:8081/login.html** - Страница входа
- **http://localhost:8081/register.html** - Страница регистрации
- **http://localhost:8081/profile.html** - Личный кабинет пользователя
- **http://localhost:8081/admin.html** - Админ панель (заявки)
- **http://localhost:8081/cms.html** - CMS панель (управление контентом)

### Регистрация пользователя

1. Перейдите на http://localhost:8081/register.html
2. Заполните форму регистрации:
   - Имя пользователя (минимум 3 символа)
   - Email
   - Пароль (минимум 8 символов)
   - Подтверждение пароля
   - Имя (необязательно)
   - Фамилия (необязательно)
3. Нажмите "Зарегистрироваться"

### Вход в систему

1. Перейдите на http://localhost:8081/login.html
2. Введите имя пользователя и пароль
3. Нажмите "Войти"

### Личный кабинет

После входа вы попадете в личный кабинет, где можно:
- Просматривать и редактировать личные данные
- Менять пароль
- Просматривать информацию аккаунта

### Административная панель

Администратор имеет доступ к:
- **Панель заявок** (http://localhost:8081/admin.html):
  - Просмотр всех заявок
  - Экспорт заявок в Excel
  - Автообновление списка заявок

- **CMS панель** (http://localhost:8081/cms.html):
  - Редактирование контента сайта
  - Создание нового контента
  - Удаление контента
  - Инициализация дефолтного контента

- **Управление пользователями** (через API):
  - Просмотр всех пользователей
  - Редактирование пользователей
  - Блокировка/разблокировка пользователей
  - Сброс паролей

## API Endpoints

### Аутентификация

- `POST /api/auth/register` - Регистрация пользователя
- `POST /api/auth/create-admin` - Создание администратора
- `GET /api/auth/check-username?username={username}` - Проверка уникальности имени пользователя
- `GET /api/auth/check-email?email={email}` - Проверка уникальности email
- `GET /api/auth/me` - Получение информации о текущем пользователе
- `PUT /api/auth/update-profile` - Обновление профиля
- `POST /api/auth/change-password` - Смена пароля

### Управление пользователями (только для администраторов)

- `GET /api/users` - Получение всех пользователей
- `GET /api/users/{id}` - Получение пользователя по ID
- `PUT /api/users/{id}` - Обновление пользователя
- `DELETE /api/users/{id}` - Удаление пользователя
- `POST /api/users/{id}/block` - Блокировка пользователя
- `POST /api/users/{id}/unblock` - Разблокировка пользователя
- `POST /api/users/{id}/reset-password` - Сброс пароля пользователя

### Управление контентом

- `GET /api/content` - Получение всего контента
- `GET /api/content/section/{section}` - Получение контента по секции
- `GET /api/content/key/{key}` - Получение контента по ключу
- `POST /api/content` - Создание контента (только для администраторов)
- `PUT /api/content/key/{key}` - Обновление контента по ключу (только для администраторов)
- `PUT /api/content/{id}` - Обновление контента по ID (только для администраторов)
- `DELETE /api/content/{id}` - Удаление контента (только для администраторов)
- `POST /api/content/initialize` - Инициализация дефолтного контента (только для администраторов)

### Заявки

- `POST /api/leads` - Создание заявки
- `GET /api/leads` - Получение всех заявок
- `GET /api/leads/export` - Экспорт заявок в Excel

## Безопасность

### Реализованные меры безопасности

1. **Хеширование паролей** - Использование BCrypt для хеширования паролей
2. **CSRF защита** - CSRF защита включена для форм, отключена для API
3. **Ограничение попыток входа** - CustomAuthenticationFailureHandler для обработки неудачных попыток
4. **Ролевая авторизация** - Разделение прав доступа между ADMIN и USER
5. **Валидация данных** - Проверка корректности данных на клиенте и сервере
6. **Уникальность пользователей** - Проверка уникальности email и username

### Роли пользователей

- **ADMIN** - Полный доступ ко всем функциям системы
- **USER** - Доступ только к личному кабинету

## Конфигурация

Конфигурация приложения находится в файле `src/main/resources/application.properties`:

```properties
# Сервер
server.port=8081

# База данных SQLite
spring.datasource.url=jdbc:sqlite:kvant.db
spring.datasource.driver-class-name=org.sqlite.JDBC

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.community.dialect.SQLiteDialect

# Email настройки
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Email администратора
admin.email=info@kvant.ru
```

## Динамический контент

Контент сайта хранится в базе данных и может быть изменен администратором через CMS панель. Контент автоматически обновляется на сайте каждые 5 минут.

### Ключи контента

- `hero_title` - Заголовок hero секции
- `hero_subtitle` - Подзаголовок hero секции
- `hero_cta` - Текст кнопки в hero секции
- `stats_projects` - Количество проектов
- `stats_experience` - Опыт работы
- `stats_satisfaction` - Удовлетворенность клиентов
- `stats_clients` - Количество клиентов
- `contact_phone` - Телефон компании
- `contact_email` - Email компании
- `contact_address` - Адрес компании

## Устранение неполадок

### Порт уже занят

Если порт 8081 уже занят, измените его в `application.properties`:

```properties
server.port=8082
```

Или остановите процесс, занимающий порт:

```powershell
netstat -ano | findstr :8081
taskkill /PID <PID> /F
```

### Ошибка базы данных

Если возникли проблемы с базой данных, удалите файл `kvant.db` и перезапустите приложение. База данных будет создана заново.

### Ошибка отправки email

Для работы email уведомлений нужно:
1. Включить двухфакторную аутентификацию в Gmail
2. Создать пароль приложения
3. Указать email и пароль приложения в `application.properties`

## Разработка

### Добавление нового контента

1. Войдите в систему как администратор
2. Перейдите на http://localhost:8081/cms.html
3. Нажмите "Инициализировать контент" для создания базового контента
4. Редактируйте контент по необходимости

### Добавление нового API endpoint

1. Создайте метод в соответствующем контроллере
2. Добавьте аннотации `@GetMapping`, `@PostMapping` и т.д.
3. При необходимости добавьте `@PreAuthorize` для защиты endpoint

## Лицензия

Проект создан для учебных целей.

## Контакты

- Email: info@kvant.ru
- Телефон: +7 (929) 425-97-74
