# ⚡ ElectroShop — Advanced E-Commerce Backend

> A high-performance, scalable **B2B & B2C e-commerce backend** built with Spring Boot 4. Goes far beyond simple CRUD — featuring complex bulk-order pricing, multi-branch inventory management, high-concurrency voucher redemption, and production-grade payment integration.

---

## 📋 Table of Contents

- [Key Features](#-key-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Getting Started](#-getting-started)
- [Environment Variables](#-environment-variables)
- [API Documentation](#-api-documentation)
- [Running Tests](#-running-tests)
- [Project Structure](#-project-structure)
- [Monitoring](#-monitoring--observability)
- [Contributing](#-contributing)

---

## ✨ Key Features

### 💰 Enterprise Pricing Engine
- **Tiered / Bulk Pricing** — product costs decrease automatically based on quantity thresholds
- **Dynamic Order Valuation** — real-time total calculation with volume discounts and per-company contract pricing

### 🏢 Multi-Branch Inventory & Logistics
- **Distributed Stock Management** — tracks product availability across multiple physical `StoreBranch` locations
- **Warehouse Operations** — integrated supplier management and `StockTransaction` tracking for safe, auditable stock replenishment

### 🔐 Security & Authentication
- **JWT Stateless Auth** — secure, scalable API access with JSON Web Tokens
- **Role-Based Access Control (RBAC)** — fine-grained permissions for `ADMIN`, `COMPANY_REP`, and `CUSTOMER` roles
- **Google OAuth2** — social login via Google ID token verification (`google-api-client`)

### 💳 Payments & Vouchers
- **VNPay Integration** — production-ready payment gateway for Vietnamese e-commerce
- **Smart Voucher System** — high-concurrency redemption with automated expiration via `@EnableScheduling`

### 📬 Notifications
- **Email Service** — transactional emails (order confirmation, OTP, password reset) via Spring Mail

---

## 🛠 Tech Stack

| Layer | Technology | Version |
|---|---|---|
| Core Framework | Spring Boot | 4.0.1 |
| Language | Java | 17 |
| Build Tool | Maven | — |
| Persistence | Spring Data JPA (Hibernate) | — |
| Database | Microsoft SQL Server | — |
| Schema Migration | Flyway (flyway-sqlserver) | — |
| Security | Spring Security + JJWT | 0.11.5 |
| Social Auth | Google API Client | 2.2.0 |
| Cache / Session | Redis (`spring-boot-starter-data-redis`) | — |
| File Storage | AWS SDK S3 / Cloudflare R2 | 2.42.8 |
| Object Mapping | MapStruct | 1.6.3 |
| Boilerplate Reduction | Lombok | — |
| HTML Parsing | Jsoup | 1.18.3 |
| API Documentation | SpringDoc OpenAPI (Swagger UI) | 2.7.0 |
| Metrics | Micrometer + Prometheus | — |
| Observability | Spring Boot Actuator | — |
| Email | Spring Boot Mail | — |

---

## 🏗 Architecture

Follows a strict **Controller → Service → Repository** layered architecture with full DTO separation via MapStruct.

```
┌─────────────────────────────────┐
│       Swagger UI / Clients      │
└────────────────┬────────────────┘
                 │ HTTP
┌────────────────▼────────────────┐
│         REST Controllers        │
│   (@RestController, @Valid)     │
└────────────────┬────────────────┘
                 │
┌────────────────▼────────────────┐
│       Service Layer             │
│   (Interface + Impl pattern)    │
└──────┬──────────────────┬───────┘
       │                  │
┌──────▼──────┐   ┌───────▼──────┐
│  Repository │   │  Integrations│
│  (JPA/Specs)│   │  Redis, S3,  │
│             │   │  VNPay, Mail │
└──────┬──────┘   └──────────────┘
       │
┌──────▼──────────┐
│   SQL Server DB │
│   (Flyway Mgd.) │
└─────────────────┘
```

**Design Principles:**
- Business logic strictly encapsulated in `service/impl/`
- All entity ↔ DTO conversions via **MapStruct** (zero runtime reflection cost)
- Centralized error handling with `@RestControllerAdvice`
- Complex queries via **JPQL** and **Spring Data Specifications**

---

## 🚀 Getting Started

### Prerequisites

| Tool | Version |
|---|---|
| JDK | 17+ |
| Maven | 3.9+ |
| Docker & Docker Compose | Latest |
| SQL Server | 2019+ (or via Docker) |
| Redis | 7+ (or via Docker) |

### 1. Clone the repository

```bash
git clone https://github.com/your-username/electroshop.git
cd electroshop
```

### 2. Configure environment

```bash
cp .env.example .env
# Edit .env with your credentials (see Environment Variables section)
```

### 3a. Run with Docker Compose (Recommended)

```bash
docker-compose up -d
```

This starts SQL Server, Redis, and the application together.

### 3b. Run locally with Maven

```bash
# Make sure SQL Server and Redis are running first
./mvnw spring-boot:run
```

### 4. Verify

```
Application: http://localhost:8080
Swagger UI:  http://localhost:8080/swagger-ui.html
Actuator:    http://localhost:8080/actuator/health
```

---

## 🔧 Environment Variables

Create a `.env` file at project root. **Never commit real credentials.**

```env
# ── Database ──────────────────────────────
DB_URL=jdbc:sqlserver://localhost:1433;databaseName=electroshop
DB_USERNAME=sa
DB_PASSWORD=your_password

# ── Redis ─────────────────────────────────
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# ── JWT ───────────────────────────────────
JWT_SECRET=your_256bit_secret_key
JWT_EXPIRATION_MS=86400000

# ── AWS S3 / Cloudflare R2 ────────────────
AWS_ACCESS_KEY=your_access_key
AWS_SECRET_KEY=your_secret_key
AWS_REGION=ap-southeast-1
AWS_BUCKET_NAME=electroshop-media
AWS_ENDPOINT_URL=https://<account>.r2.cloudflarestorage.com  # Remove for AWS S3

# ── VNPay ─────────────────────────────────
VNPAY_TMN_CODE=your_tmn_code
VNPAY_HASH_SECRET=your_hash_secret
VNPAY_URL=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html

# ── Google OAuth2 ─────────────────────────
GOOGLE_CLIENT_ID=your_google_client_id

# ── Mail ──────────────────────────────────
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password
```

---

## 📖 API Documentation

Interactive Swagger UI is available at:

```
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON spec:

```
http://localhost:8080/v3/api-docs
```

**Main API Groups:**

| Group | Prefix | Description |
|---|---|---|
| Auth | `/api/auth` | Login, register, OAuth2, OTP |
| Products | `/api/products` | Catalog, pricing tiers, search |
| Orders | `/api/orders` | Place, track, manage orders |
| Inventory | `/api/inventory` | Stock per branch, transactions |
| Vouchers | `/api/vouchers` | Create, redeem, validate |
| Users | `/api/users` | Profile, company reps, RBAC |
| Payments | `/api/payments` | VNPay initiate & callback |
| Admin | `/api/admin` | Dashboard, reporting |

---

## 🧪 Running Tests

```bash
# Run all tests
./mvnw test

# Run with coverage report
./mvnw verify

# Run a specific test class
./mvnw test -Dtest=OrderServiceTest
```

---

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/sba302/electronicsShop/
│   │   ├── config/          # Spring Security, Redis, AWS, Swagger config
│   │   ├── controller/      # REST endpoints
│   │   ├── dto/             # Request / Response DTOs
│   │   │   ├── request/
│   │   │   └── response/
│   │   ├── entity/          # JPA entities
│   │   ├── exception/       # Custom exceptions + GlobalExceptionHandler
│   │   ├── mapper/          # MapStruct interfaces
│   │   ├── repository/      # Spring Data JPA repositories
│   │   ├── scheduler/       # @Scheduled tasks (voucher expiry, cleanup)
│   │   ├── security/        # JWT filter, UserDetailsService
│   │   └── service/
│   │       ├── interfaces/  # Service contracts
│   │       └── impl/        # Service implementations
│   └── resources/
│       ├── db/migration/    # Flyway SQL migration scripts (V1__, V2__…)
│       ├── application.yml
│       └── application-dev.yml
└── test/
    └── java/com/sba302/electronicsShop/
```

---

## 📊 Monitoring & Observability

| Endpoint | Description |
|---|---|
| `/actuator/health` | Application health status |
| `/actuator/info` | Build & version info |
| `/actuator/metrics` | All Micrometer metrics |
| `/actuator/prometheus` | Prometheus scrape endpoint |

**Prometheus + Grafana setup** (optional, via Docker Compose):

```bash
docker-compose --profile monitoring up -d
# Grafana: http://localhost:3000 (admin/admin)
```

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

---

> **Note:** This project is built with a production-grade mindset — prioritizing scalability, security, data consistency under high concurrency, and clean domain-driven design.
