# 🔐 Auth & RBAC Service

A production-quality **Authentication and Role-Based Access Control (RBAC)** backend service built with Java and Spring Boot. Features stateless JWT authentication, BCrypt password hashing, audit logging, and a fully documented REST API via Swagger UI.

---

## 🚀 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.3 |
| Security | Spring Security 7 + JWT (JJWT 0.12.3) |
| Database | PostgreSQL 17 |
| ORM | Hibernate / Spring Data JPA |
| Documentation | SpringDoc OpenAPI (Swagger UI) |
| Boilerplate Reduction | Lombok |
| Build Tool | Maven |

---

## 📁 Project Structure

```
src/main/java/com/project/authservice/
├── config/          # Security, Swagger, Async, Data initializer
├── controller/      # REST controllers (Auth, User, Admin)
├── dto/
│   ├── request/     # RegisterRequest, LoginRequest
│   └── response/    # ApiResponse, AuthResponse, UserResponse, ErrorResponse
├── entity/          # User, Role, AuditLog
├── exception/       # Global handler + custom exceptions
├── filter/          # JwtAuthenticationFilter
├── repository/      # Spring Data JPA repositories
├── security/        # UserPrincipal, CustomUserDetailsService, EntryPoint
├── service/
│   └── impl/        # AuthServiceImpl, UserServiceImpl, AuditLogServiceImpl
└── util/            # JwtUtil
```

---

## ✨ Features

- **User Registration** with input validation and duplicate detection
- **JWT Login** returning a signed Bearer token (24hr expiry)
- **Role-Based Access Control** with `ROLE_USER` and `ROLE_ADMIN`
- **Protected Endpoints** — `/api/user/**` and `/api/admin/**`
- **BCrypt Password Hashing** — passwords never stored in plaintext
- **Audit Logging** — async logging of login success, login failure, and registration events
- **Global Exception Handling** — consistent JSON error responses across all endpoints
- **Swagger UI** — fully interactive API documentation
- **Stateless Architecture** — no sessions, no server-side state

---

## 🗄️ Database Schema

```
users
├── id (PK)
├── username (unique)
├── email (unique)
├── password (BCrypt hash)
├── enabled
├── created_at
└── updated_at

roles
├── id (PK)
└── name (ROLE_USER | ROLE_ADMIN)

user_roles (join table)
├── user_id (FK)
└── role_id (FK)

audit_logs
├── id (PK)
├── event_type
├── principal
├── description
├── ip_address
├── outcome
└── created_at
```

---

## 🔒 API Endpoints

### Authentication (Public)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login and receive JWT token |

### User (Requires: USER or ADMIN role)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/user/me` | Get current user profile |
| GET | `/api/user/profile/{username}` | Get profile by username |

### Admin (Requires: ADMIN role only)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/admin/dashboard` | Admin dashboard |
| GET | `/api/admin/users` | List all users (paginated) |
| GET | `/api/admin/users/{id}` | Get user by ID |
| GET | `/api/admin/audit-logs` | View all audit logs |
| GET | `/api/admin/audit-logs/{principal}` | View logs by user |

---

## 📬 Example API Requests

### Register
```http
POST http://localhost:8081/api/auth/register
Content-Type: application/json

{
  "username": "vishu",
  "email": "vishu@example.com",
  "password": "securepass123"
}
```

### Login
```http
POST http://localhost:8081/api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "vishu",
  "password": "securepass123"
}
```

### Access Protected Endpoint
```http
GET http://localhost:8081/api/user/me
Authorization: Bearer <your_jwt_token_here>
```

---

## ⚙️ Running Locally

### Prerequisites
- Java 21+
- PostgreSQL running locally
- IntelliJ IDEA (recommended)

### Steps

**1. Clone the repository**
```bash
git clone https://github.com/vishu1618/authservice.git
cd authservice
```

**2. Create the database**
```sql
CREATE DATABASE auth_rbac_db;
```

**3. Configure `application.properties`**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/auth_rbac_db
spring.datasource.username=your_postgres_username
spring.datasource.password=your_postgres_password
```

**4. Run the application**

Open in IntelliJ and click the Run button, or use the Maven wrapper:
```bash
./mvnw spring-boot:run
```

**5. Open Swagger UI**
```
http://localhost:8081/swagger-ui/index.html
```

Roles (`ROLE_USER` and `ROLE_ADMIN`) are seeded automatically on first startup via `DataInitializer`.

---

## 🔑 JWT Flow

```
Client → POST /api/auth/login
       ← JWT Token (Bearer)

Client → GET /api/user/me
         Authorization: Bearer <token>
       ← User Profile
```

The `JwtAuthenticationFilter` intercepts every request, validates the token, and sets the `SecurityContext` before the request reaches any controller.

---

## 📖 Swagger UI Preview

Full interactive API documentation available at `/swagger-ui/index.html` after running the app.

All endpoints are grouped by tag (Authentication, User, Admin), schemas are auto-generated, and protected endpoints show the lock icon requiring a Bearer token.

---

## 👤 Author

**Vishu Choudhary**  
GitHub: [@vishu1618](https://github.com/vishu1618)

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).
