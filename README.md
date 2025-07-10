# 💳 BankCards - Финансовый сервис на Spring Boot

Это полноценное приложение для работы с банковскими картами. Реализована регистрация, авторизация, управление картами, переводы, блокировка, пополнение баланса, а также админ-панель.

## Быстрый старт

### Клонирование проекта и запуск

```bash
# 1. Клонируем репозиторий
git clone https://github.com/suzerain-r/bank-rest.git

# 2. Переходим в директорию проекта
cd bank-rest

# 3. Собираем и запускаем контейнеры
docker-compose up --build
```

### Backend API
http://localhost:8080

### Swagger UI
http://localhost:8080/swagger-ui.html

### Пользовательский интерфейс(Небольшой front)
Регистрация / Вход - http://localhost:8080/auth.html

Личный кабинет пользователя - http://localhost:8080/user.html

Управление картами - http://localhost:8080/card.html

Панель администратора -	http://localhost:8080/admin.html
