# Authentication API Documentation

## Overview
Complete JWT-based authentication flow with register, login, and refresh token functionality.

---

## Endpoints

### 1. Register New User
**POST** `/api/auth/register`

Creates a new user account.

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123",
  "fullName": "John Doe",
  "phoneNumber": "0123456789"
}
```

**Response (201 Created):**
```json
{
  "status": 200,
  "message": "Success",
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "fullName": "John Doe",
    "role": "CUSTOMER",
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 3600
  },
  "timestamp": "2026-01-21T16:35:00"
}
```

**Validations:**
- Email: required, valid email format
- Password: required, minimum 6 characters
- FullName: required, 2-255 characters
- PhoneNumber: optional, 10-15 digits

**Errors:**
- `400` - Email already registered
- `400` - Validation failed

---

### 2. Login
**POST** `/api/auth/login`

Authenticate user and get access/refresh tokens.

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "status": 200,
  "message": "Success",
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "fullName": "John Doe",
    "role": "CUSTOMER",
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 3600
  },
  "timestamp": "2026-01-21T16:35:00"
}
```

**Errors:**
- `404` - Invalid email or password
- `400` - Account is disabled

---

### 3. Refresh Access Token
**POST** `/api/auth/refresh`

Get new access and refresh tokens using a valid refresh token.

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response (200 OK):**
```json
{
  "status": 200,
  "message": "Success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 3600
  },
  "timestamp": "2026-01-21T16:35:00"
}
```

**Errors:**
- `400` - Invalid or expired refresh token
- `404` - User not found
- `400` - Account is disabled

---

## JWT Token Structure

### Access Token Claims:
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "email": "user@example.com",
  "roles": ["CUSTOMER"],
  "authorities": [],
  "privileges": [],
  "iat": 1737456900,
  "exp": 1737460500
}
```

### Token Expiration:
- **Access Token**: 1 hour (3600 seconds)
- **Refresh Token**: 7 days (604800 seconds)

---

## Using Access Token

Include the access token in the `Authorization` header for protected endpoints:

```http
GET /api/products HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## Security Features

1. **Password Hashing**: BCrypt encryption for all passwords
2. **JWT Signing**: HS256 algorithm with secret key
3. **Token Validation**: Signature and expiration verification
4. **Role-Based Access**: Roles and privileges in JWT claims
5. **Account Status**: Active/disabled check on login and refresh

---

## User Roles

- **CUSTOMER**: Default role for registered users
- **ADMIN**: Administrator role (set manually in database)
- **EMPLOYEE**: Employee role (set manually in database)

---

## Public Endpoints (No Authentication Required)

- `/api/auth/login`
- `/api/auth/register`
- `/api/auth/refresh`
- `/swagger-ui/**`
- `/v3/api-docs/**`

---

## Protected Endpoints (Require Authentication)

All other endpoints require a valid JWT access token:
- `/api/products/**`
- `/api/customers/**`
- `/api/orders/**`
- `/api/cart/**`
- etc.

---

## Error Responses

### Validation Error (400):
```json
{
  "status": 400,
  "message": "Validation failed",
  "data": {
    "email": "Email must be valid",
    "password": "Password must be at least 6 characters"
  },
  "timestamp": "2026-01-21T16:35:00"
}
```

### Unauthorized (401):
```json
{
  "status": 401,
  "message": "Unauthorized - Invalid or missing token",
  "data": null,
  "timestamp": "2026-01-21T16:35:00"
}
```

### Forbidden (403):
```json
{
  "status": 403,
  "message": "Access denied. You do not have permission to perform this action.",
  "data": null,
  "timestamp": "2026-01-21T16:35:00"
}
```

---

## Testing with Swagger

1. Navigate to: `http://localhost:8080/swagger-ui.html`
2. Register a new user via `/api/auth/register`
3. Copy the `accessToken` from the response
4. Click "Authorize" button at the top
5. Enter: `Bearer <your-access-token>`
6. Now you can test protected endpoints

---

## Testing with cURL

### Register:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "fullName": "Test User",
    "phoneNumber": "0123456789"
  }'
```

### Login:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

### Refresh Token:
```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }'
```

### Access Protected Endpoint:
```bash
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## Database Schema

The `USER` table is automatically created by JPA with the following structure:

```sql
CREATE TABLE "USER" (
    user_id UUID PRIMARY KEY,
    email NVARCHAR(255) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    full_name NVARCHAR(255),
    phone_number NVARCHAR(20),
    role NVARCHAR(50),
    is_active BIT,
    created_at DATETIME2(7),
    updated_at DATETIME2(7)
);
```

---

## Implementation Details

### Files Created:
1. **Entity**: `User.java` - User entity with UUID primary key
2. **Repository**: `UserRepository.java` - JPA repository with email lookup
3. **Service**: `AuthService.java` (interface) + `AuthServiceImpl.java`
4. **Controller**: `AuthController.java` - REST endpoints
5. **DTOs**:
   - Request: `LoginRequest`, `RegisterRequest`, `RefreshTokenRequest`
   - Response: `AuthResponse`, `TokenResponse`
6. **Config**: `PasswordEncoderConfig.java` - BCrypt encoder bean
7. **Updated**: `JwtAuthFilter.java` - Added `/api/auth/refresh` to public endpoints

### Dependencies Used:
- Spring Security
- Spring Data JPA
- JWT (io.jsonwebtoken)
- Lombok
- Bean Validation
