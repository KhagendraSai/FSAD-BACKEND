# FSAD Backend (Spring Boot)

This backend is created for the React frontend in the same workspace.

## Tech Stack

- Spring Boot 3.3.2
- Java 17
- Spring Web + Spring Data JPA
- Spring Security + JWT
- MySQL
- Swagger (OpenAPI) via springdoc

## Run

1. Make sure MySQL is running.
2. Optional: set environment variables:
   - `DB_URL`
   - `DB_USERNAME`
   - `DB_PASSWORD`
  - `JWT_SECRET`
  - `JWT_EXPIRATION_MS`
3. Start backend:

```bash
cd backend
mvn spring-boot:run
```

Server runs at `http://localhost:8080`.

## Swagger

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

## Authentication API

Base path: `/api/auth`

- `POST /api/auth/register` - create account and return JWT
- `POST /api/auth/login` - sign in and return JWT

Sample login/register payload:

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "123456",
  "role": "student"
}
```

## User API (JWT protected)

Base path: `/api/users`

- `GET /api/users` - list users
- `GET /api/users/{id}` - get one user
- `POST /api/users` - create user
- `PUT /api/users/{id}` - update user
- `DELETE /api/users/{id}` - delete user

### Sample JSON

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "123456",
  "role": "student"
}
```

## Additional APIs (JWT protected)

- Courses: `/api/courses`
- Attendance: `/api/attendance`
- Marks: `/api/marks`

All support CRUD operations via GET, POST, PUT, and DELETE.
