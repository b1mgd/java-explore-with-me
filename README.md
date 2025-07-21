# Рейтинг событий (Event Ratings)

## Описание

Реализована система рейтинга событий (лайк/дизлайк) с публичными и приватными API, а также с поддержкой Postman-тестов.

---

### Ожидаемое поведение

- **PRIVATE** может поставить лайк или дизлайк событию (POST `/users/{userId}/events/{eventId}/rating`).
- **PRIVATE** может получить свою оценку события (GET `/users/{userId}/events/{eventId}/rating`).
- **PRIVATE** может удалить свою оценку (DELETE `/users/{userId}/events/{eventId}/rating`).
- **PUBLIC** можно получить рейтинг конкретного события (GET `/events/{eventId}/rating`).
- **PUBLIC** можно получить топ событий по рейтингу (GET `/events/rating/top`).

---

### Структура и валидация

- **Сущность Rating** (`event_ratings`):
  - `id`: bigint, PK
  - `is_like`: boolean, not null (лайк — true, дизлайк — false)
  - `created_on`: timestamp, not null
  - `user_id`: bigint, not null, FK на пользователя
  - `event_id`: bigint, not null, FK на событие

- **DTO для создания оценки** (`RatingPost`):
  - `isLike`: boolean, not null (валидация: обязательно указать, что это лайк или дизлайк)

- **Валидация и ограничения**:
  - Один пользователь может поставить только одну оценку на событие.
  - При попытке повторно оценить — ошибка.
  - При удалении/получении оценки — проверка существования.
  - Допускается оценка своих же событий (как самолайк в соцсетях).

---

### Примеры эндпоинтов

**Приватные:**
- `POST   /users/{userId}/events/{eventId}/rating` — поставить лайк/дизлайк
- `GET    /users/{userId}/events/{eventId}/rating` — получить свою оценку
- `DELETE /users/{userId}/events/{eventId}/rating` — удалить свою оценку

**Публичные:**
- `GET /events/{eventId}/rating` — получить рейтинг события (кол-во лайков/дизлайков)
- `GET /events/rating/top` — получить топ событий по рейтингу (с пагинацией)

---

### Примеры запросов

**POST /users/1/events/10/rating**
```json
{
  "isLike": true
}
```

**Ответ:**
```json
{
  "id": 123,
  "isLike": true,
  "createdOn": "2024-06-01T12:00:00",
  "user": { ... },
  "event": { ... }
}
```

---

### Тесты

- В каталоге `/postman` реализованы коллекции Postman для проверки всех сценариев работы рейтинга: добавление, получение, удаление, публичные запросы, пагинация топа.

---

### SQL

```sql
CREATE TABLE IF NOT EXISTS event_ratings
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    is_like    BOOLEAN                     NOT NULL,
    created_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id    BIGINT                      NOT NULL REFERENCES users (id),
    event_id   BIGINT                      NOT NULL REFERENCES events (id)
);
```
