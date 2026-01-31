# 📋 Coding Standard - Microservices Lab Management

> Hướng dẫn chuẩn code cho các services: IAM, User, Patient, Instrument, Warehouse, Test-Order, Monitoring

---

## 📑 Mục lục

1. [Cấu trúc Package](#1-cấu-trúc-package)
2. [Quy tắc đặt tên](#2-quy-tắc-đặt-tên)
3. [Entity & Database](#3-entity--database)
4. [DTO](#4-dto)
5. [Repository](#5-repository)
6. [Service](#6-service)
7. [Controller & REST API](#7-controller--rest-api)
8. [Endpoint Naming](#8-endpoint-naming)
9. [Response Format](#9-response-format)
10. [Exception Handling](#10-exception-handling)
11. [Validation](#11-validation)
12. [Security](#12-security)
13. [Mapper (Entity ↔ DTO)](#13-mapper-entity--dto)
14. [Best Practices](#14-best-practices)

---

## 1. Cấu trúc Package

### Quy tắc
- **Bắt buộc**: Tất cả services phải tuân thủ cấu trúc package chuẩn
- **Phân tách layer**: Mỗi layer 1 package riêng biệt (controller, service, repository, entity, dto)
- **Không mix layers**: Controller KHÔNG gọi trực tiếp Repository
- **DTO phân loại**: Tách rõ request/ và response/
- **Base classes**: Đặt trong baseResponseDTO/ (ApiResponse, PageResponse)
- **Enums**: Riêng package enums/, KHÔNG để trong entity

### Template
```
src/main/java/com/microservice/{service-name}/
├── config/              # Security, Feign, etc.
├── controller/          # REST Controllers
├── dto/
│   ├── request/        # Request DTOs
│   ├── response/       # Response DTOs
│   └── baseResponseDTO/ # ApiResponse, PageResponse
├── entity/             # JPA Entities
├── repository/         # Spring Data JPA
├── service/            # Business Logic
├── exception/          # Custom Exceptions
├── enums/              # Enum classes
├── util/               # Utilities
└── client/             # Feign Clients
```

### Ví dụ thực tế
```
instrument-service/
├── controller/
│   └── InstrumentController.java
├── dto/
│   ├── request/
│   │   └── CreateInstrumentRequestDTO.java
│   └── response/
│       └── InstrumentResponseDTO.java
├── entity/
│   └── Instrument.java
└── service/
    ├── InstrumentService.java
    └── InstrumentServiceImpl.java
```

---

## 2. Quy tắc đặt tên

### Quy tắc chung
- **Nhất quán**: Toàn bộ project phải dùng chung 1 convention
- **Tên có nghĩa**: Tránh viết tắt (trừ các từ phổ biến: id, dto, url)
- **Không dùng tiếng Việt**: Tất cả tên phần tử code bằng tiếng Anh
- **Không dùng số**: Tránh `user1`, `data2` (trừ index: `i`, `j`)
- **Suffix rõ ràng**: Luôn đặt suffix theo loại class (Controller, Service, DTO, etc.)

### 2.1. Package Names
- **Luôn lowercase**, snake_case cho service name

```java
✅ package com.microservice.instrument_service.controller;
❌ package com.microservice.InstrumentService;
```

### 2.2. Class Names - PascalCase

| Loại | Suffix | Ví dụ |
|------|--------|-------|
| Entity | Không suffix | `Instrument`, `User` |
| Request DTO | `RequestDTO` | `CreateUserRequestDTO` |
| Response DTO | `ResponseDTO` / `DTO` | `UserResponseDTO`, `UserDTO` |
| Controller | `Controller` | `UserController` |
| Service Interface | `Service` | `UserService` |
| Service Impl | `ServiceImpl` | `UserServiceImpl` |
| Repository | `Repository` | `UserRepository` |
| Exception | `Exception` | `ResourceNotFoundException` |

### 2.3. Variables & Methods - camelCase

**Variables:**
```java
✅ private String patientCode;
✅ private boolean isActive;
❌ private String pc;  // quá ngắn, không rõ nghĩa
```

**Methods:** Bắt đầu bằng động từ, mô tả rõ hành động

| Pattern | Ví dụ |
|---------|-------|
| `get*` | `getUserById()` |
| `find*` | `findByEmail()` |
| `create*` | `createUser()` |
| `update*` | `updateProfile()` |
| `delete*` | `deleteUser()` |
| `is*`, `has*` | `isActive()`, `hasPermission()` |

### 2.4. Constants - SCREAMING_SNAKE_CASE

```java
public static final String DEFAULT_ROLE = "USER";
public static final int MAX_RETRY_ATTEMPTS = 3;
```

### 2.5. Enums

```java
public enum InstrumentStatus {
    READY,
    MAINTENANCE,
    ERROR,
    INACTIVE
}
```

---

## 3. Entity & Database

### Quy tắc
- **Luôn dùng `@Column(name = "...")`**: Map rõ ràng với database column (snake_case)
- **Enum phải STRING**: `@Enumerated(EnumType.STRING)`, **KHÔNG dùng ORDINAL**
- **Timestamps tự động**: Dùng `@PrePersist` và `@PreUpdate` để set `createdAt`, `updatedAt`
- **Lombok bắt buộc**: `@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
- **KHÔNG để logic trong Entity**: Entity chỉ là data container
- **Relationships**: Chỉ define khi thực sự cần (tránh N+1 query)

### Ví dụ

```java
@Entity
@Table(name = "instruments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Instrument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "serial_number", unique = true, length = 100)
    private String serialNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "instrument_status", nullable = false)
    private InstrumentStatus instrumentStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
```

### Quy tắc

| Java (Entity) | Database (Column) |
|---------------|-------------------|
| `patientCode` | `patient_code` |
| `serialNumber` | `serial_number` |
| `createdAt` | `created_at` |

**Key Points:**
- ✅ Luôn dùng `@Column(name = "...")` để map rõ ràng
- ✅ Enum dùng `EnumType.STRING` (không dùng ORDINAL)
- ✅ Auto-increment Long hoặc UUID cho ID

---

## 4. DTO

### Quy tắc
- **KHÔNG expose Entity**: Luôn dùng DTO để trả về API, **KHÔNG BAO GIỞ** trả Entity
- **Request DTO**: Phải có validation annotations (`@NotBlank`, `@Email`, etc.)
- **Response DTO**: Chỉ chứa fields cần thiết, KHÔNG expose sensitive data (password, internal IDs)
- **Lombok**: Dùng `@Data` cho DTO (hoặc `@Getter/@Setter` + `@Builder`)
- **Immutable khi có thể**: Response DTO nên immutable (final fields + `@Builder`)
- **Swagger docs**: Luôn dùng `@Schema` để document

### Ví dụ Request DTO

```java
@Data
public class CreateUserRequestDTO {

    @Schema(example = "user@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 20)
    private String password;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 50)
    private String fullName;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @Past(message = "Date must be in the past")
    private LocalDate dateOfBirth;
}
```

### Ví dụ Response DTO

```java
@Data
@Builder
public class UserResponseDTO {
    private UUID id;
    private String email;
    private String fullName;
    private String roleCode;
    private Boolean enabled;
}
```

### Validation Annotations

| Annotation | Mục đích |
|------------|----------|
| `@NotNull` | Không được null |
| `@NotBlank` | Không null/empty/whitespace |
| `@Email` | Email hợp lệ |
| `@Size` | Giới hạn độ dài |
| `@Min`, `@Max` | Giới hạn giá trị số |
| `@Past`, `@Future` | Giới hạn ngày tháng |
| `@Pattern` | Regex pattern |

---

## 5. Repository

### Quy tắc
- **Luôn extend `JpaRepository<Entity, ID>`**: KHÔNG tự viết implementation
- **Method naming**: Tuân thủ Spring Data JPA convention (`findBy`, `existsBy`, `countBy`)
- **Return type**: Dùng `Optional<>` cho single result, `List<>` cho multiple
- **Custom query**: Dùng `@Query` với JPQL (tránh Native query nếu có thể)
- **KHÔNG chứa logic**: Repository chỉ query database, logic để ở Service
- **Pagination**: Dùng `Pageable` parameter cho queries trả nhiều kết quả

### Ví dụ

```java
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // Derived Query
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRoleCode(String roleCode);

    // Custom JPQL
    @Query("SELECT u FROM User u WHERE u.role.code = :roleCode AND u.enabled = true")
    List<User> findActiveUsersByRole(@Param("roleCode") String roleCode);

    // Pagination
    Page<User> findByRoleCodeAndEnabledTrue(String roleCode, Pageable pageable);
}
```

### Query Methods

| Keyword | JPQL |
|---------|------|
| `findBy` | `WHERE x.field = ?1` |
| `existsBy` | `SELECT COUNT(x) > 0 WHERE ...` |
| `countBy` | `SELECT COUNT(x) WHERE ...` |
| `deleteBy` | `DELETE FROM x WHERE ...` |

---

## 6. Service

### Quy tắc
- **Bắt buộc tách Interface và Implementation**: `UserService` (interface) + `UserServiceImpl` (impl)
- **Transaction management**:
  - `@Transactional` cho write operations (create, update, delete)
  - `@Transactional(readOnly = true)` cho read operations (get, find, search)
- **KHÔNG gọi Service khác qua interface**: Inject implementation trực tiếp nếu cần
- **Exception handling**: Throw custom exceptions rõ ràng (ResourceNotFoundException, BadRequestException)
- **Logging**: Log tất cả write operations (`log.info`) và errors (`log.error`)
- **Mapping**: Tạo private method `mapToDTO()` và `mapToEntity()` trong service
- **Constructor injection**: Dùng `@RequiredArgsConstructor` (Lombok) với `final` fields

### Ví dụ Interface

```java
public interface InstrumentService {
    ApiResponse<Void> changeStatus(Long id, String status);
    PageResponse<InstrumentDTO> searchInstruments(String name, Pageable pageable);
    ApiResponse<InstrumentDTO> createInstrument(CreateInstrumentRequestDTO request);
}
```

### Implementation

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class InstrumentServiceImpl implements InstrumentService {

    private final InstrumentRepository instrumentRepository;

    @Override
    @Transactional
    public ApiResponse<Void> changeStatus(Long id, String status) {
        log.info("Changing instrument {} status to {}", id, status);

        Instrument instrument = instrumentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Instrument not found: " + id));

        instrument.setInstrumentStatus(InstrumentStatus.valueOf(status));
        instrumentRepository.save(instrument);

        return ApiResponse.<Void>builder()
                .status(true)
                .message("Status changed successfully")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InstrumentDTO> searchInstruments(
            String name, Pageable pageable) {
        Page<Instrument> page = instrumentRepository.findByNameContaining(name, pageable);
        
        return PageResponse.<InstrumentDTO>builder()
                .content(page.getContent().stream().map(this::mapToDTO).toList())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    private InstrumentDTO mapToDTO(Instrument entity) {
        return InstrumentDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .status(entity.getInstrumentStatus())
                .build();
    }
}
```

**Key Points:**
- `@Transactional` cho write operations
- `@Transactional(readOnly = true)` cho read operations
- Throw exception rõ ràng
- Helper methods để map Entity ↔ DTO

---

## 7. Controller & REST API

### Quy tắc
- **Thin controller**: KHÔNG chứa business logic, chỉ routing và validation
- **KHÔNG gọi Repository**: Controller chỉ gọi Service
- **Return `ResponseEntity<>`**: Bắt buộc dùng `ResponseEntity` để control HTTP status
- **Validation**: Dùng `@Valid` cho `@RequestBody`
- **Authorization**: Bắt buộc dùng `@PreAuthorize` cho mọi endpoint (trừ public)
- **Swagger docs**: Luôn có `@Tag`, `@Operation`, `@Parameter`
- **Base path**: `/api/{resource-plural}` (ví dụ: `/api/users`, `/api/instruments`)

### Ví dụ

```java
@RestController
@Tag(name = "Instrument", description = "API quản lý thiết bị")
@RequestMapping("/api/instruments")
@RequiredArgsConstructor
public class InstrumentController {

    private final InstrumentService instrumentService;

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('UPDATE_INSTRUMENT_STATUS')")
    @Operation(summary = "Thay đổi trạng thái thiết bị")
    public ResponseEntity<ApiResponse<Void>> changeStatus(
            @PathVariable Long id,
            @RequestParam InstrumentStatus status,
            @Valid @RequestBody ReasonDTO reason) {
        
        ApiResponse<Void> response = instrumentService.changeStatus(id, status.name());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('VIEW_INSTRUMENT')")
    @Operation(summary = "Tìm kiếm instruments")
    public ResponseEntity<PageResponse<InstrumentDTO>> search(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<InstrumentDTO> response = instrumentService.searchInstruments(name, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_INSTRUMENT')")
    @Operation(summary = "Tạo instrument mới")
    public ResponseEntity<ApiResponse<InstrumentDTO>> create(
            @Valid @RequestBody CreateInstrumentRequestDTO request) {
        
        ApiResponse<InstrumentDTO> response = instrumentService.createInstrument(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_INSTRUMENT')")
    @Operation(summary = "Lấy instrument theo ID")
    public ResponseEntity<ApiResponse<InstrumentDTO>> getById(@PathVariable Long id) {
        ApiResponse<InstrumentDTO> response = instrumentService.getById(id);
        return ResponseEntity.ok(response);
    }
}
```

### HTTP Status Codes

| Code | Khi nào dùng |
|------|--------------|
| `200 OK` | GET, PUT, PATCH thành công |
| `201 CREATED` | POST tạo mới thành công |
| `204 NO CONTENT` | DELETE thành công |
| `400 BAD REQUEST` | Validation fail |
| `401 UNAUTHORIZED` | Chưa đăng nhập |
| `403 FORBIDDEN` | Không có quyền |
| `404 NOT FOUND` | Resource không tồn tại |
| `409 CONFLICT` | Email/Code đã tồn tại |
| `500 INTERNAL ERROR` | Lỗi server |

---

## 8. Endpoint Naming

### Quy tắc
- **Plural nouns**: `/api/users` (KHÔNG dùng `/api/user`)
- **Lowercase + kebab-case**: `/api/test-orders` (KHÔNG dùng `/api/testOrders` hoặc `/api/TestOrders`)
- **No verbs trong path**: Dùng HTTP methods (GET, POST, PUT, DELETE) thay vì verbs
- **No trailing slash**: `/api/users` (KHÔNG `/api/users/`)
- **Path params cho ID**: `/api/users/{id}` (specific resource)
- **Query params cho filter**: `?status=active&page=0` (filtering, pagination)
- **Action endpoints**: Chỉ dùng khi KHÔNG thể dùng HTTP methods (ví dụ: `/api/users/{id}/lock`)

### RESTful Standard

| Method | Endpoint | Mục đích | Status |
|--------|----------|----------|--------|
| `GET` | `/api/users` | Lấy danh sách | 200 |
| `GET` | `/api/users/{id}` | Lấy theo ID | 200 |
| `GET` | `/api/users/search` | Tìm kiếm với filter | 200 |
| `POST` | `/api/users` | Tạo mới | 201 |
| `PUT` | `/api/users/{id}` | Cập nhật toàn bộ | 200 |
| `PATCH` | `/api/users/{id}` | Cập nhật một phần | 200 |
| `DELETE` | `/api/users/{id}` | Xóa | 200/204 |

### Nested Resources

```
GET    /api/instruments/{id}/reagents
POST   /api/instruments/{id}/reagents
DELETE /api/instruments/{id}/reagents/{reagentId}
```

### Action Endpoints (Non-CRUD)

```
PUT    /api/instruments/{id}/status
POST   /api/users/{id}/lock
POST   /api/auth/login
POST   /api/test-orders/{id}/approve
```

### ❌ Tránh những pattern này

```
❌ GET  /api/getUsers              // Không dùng động từ
❌ POST /api/createUser            // Dùng POST /api/users
❌ GET  /api/users/delete/{id}     // Dùng DELETE method
❌ POST /api/user-search           // Dùng GET /api/users/search
❌ GET  /api/user_list             // Dùng /api/users
```

### Query Parameters & Conventions

**Examples:**
```
GET /api/users?role=ADMIN&status=active&page=0&size=10
GET /api/instruments?name=abc&status=READY&sort=createdAt,desc
```

**Naming rules:**
- Lowercase: `/api/users`
- Kebab-case: `/api/test-orders`
- Plural nouns: `/api/users`
- No trailing slash: `/api/users`

---

## 9. Response Format

### Quy tắc
- **Bắt buộc dùng wrapper**: Luôn wrap response trong `ApiResponse<>` hoặc `PageResponse<>`
- **ApiResponse cho single object**: Trả về 1 object hoặc void operation
- **PageResponse cho list**: Trả về danh sách có phân trang
- **`status` field**: `true` = success, `false` = fail/error
- **`message` field**: User-friendly message (tiếng Việt OK)
- **`data` field**: Có thể null nếu chỉ trả message

### Ví dụ ApiResponse

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean status;
    private String message;
    private T data;
}
```

```json
{
    "status": true,
    "message": "User created successfully",
    "data": {"id": "123e4567", "email": "user@example.com"}
}
```

### Ví dứ PageResponse

```java
@Data
@Builder
public class PageResponse<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
```

**JSON:**
```json
{
    "content": [{"id": 1, "name": "Item 1"}],
    "pageNumber": 0,
    "totalElements": 25
}
```

---

## 10. Exception Handling

### Quy tắc
- **Global handler**: Bắt buộc dùng `@RestControllerAdvice` để handle exceptions tập trung
- **Custom exceptions**: Tạo exceptions có tên rõ nghĩa (ResourceNotFoundException, BadRequestException, ResourceConflictException)
- **Extend RuntimeException**: KHÔNG dùng checked exceptions
- **HTTP status mapping**: 
  - 400 = Validation/Bad input
  - 404 = Not found
  - 409 = Conflict (duplicate)
  - 500 = Unhandled error
- **Luôn log error**: `log.error()` trong exception handler
- **Trả về ApiResponse format**: Consistent error response

### Ví dụ Custom Exceptions

```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}

public class ResourceConflictException extends RuntimeException {
    public ResourceConflictException(String message) {
        super(message);
    }
}
```

### Ví dụ Global Exception Handler

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(
            ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.<Void>builder()
                        .status(false)
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationError(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(
                ApiResponse.builder()
                        .status(false)
                        .message("Validation failed")
                        .data(errors)
                        .build());
    }
}
```

### Sử dụng

```java
@Override
public UserDTO getUserById(UUID id) {
    return userRepository.findById(id)
            .map(this::mapToDTO)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
}
```

---

## 11. Validation

### Quy tắc
- **Validation ở DTO**: Đặt validation annotations trên Request DTO fields
- **Bắt buộc dùng `@Valid`**: Trong controller `@RequestBody`
- **Message rõ ràng**: Luôn có `message` attribute cho mỗi validation
- **Không duplicate validation**: Validation logic chỉ ở 1 nơi (DTO), KHÔNG duplicate trong Service
- **Business validation**: Logic phức tạp (check database) để trong Service, throw exception
- **Common validations**:
  - `@NotNull`: Field bắt buộc
  - `@NotBlank`: String không empty
  - `@Email`: Email format
  - `@Size`: Độ dài string/collection
  - `@Past/@Future`: Ngày quá khứ/tương lai

### Ví dụ

```java
@Data
public class CreatePatientRequestDTO {

    @NotBlank(message = "Code is required")
    @Pattern(regexp = "^PAT-[0-9]{4}$", message = "Invalid format")
    private String patientCode;

    @NotBlank
    @Size(min = 2, max = 100)
    private String fullName;

    @NotNull
    private Gender gender;

    @Past
    private LocalDate dateOfBirth;

    @Email
    private String email;

    @Pattern(regexp = "^[0-9]{10}$")
    private String phone;
}
```

### Sử dụng trong Controller

```java
@PostMapping
public ResponseEntity<?> create(
        @Valid @RequestBody CreatePatientRequestDTO request) {
    // @Valid tự động trigger validation
}
```

---

## 12. Security

### Quy tắc
- **Bắt buộc authorization**: Mọi endpoint (trừ public) phải có `@PreAuthorize`
- **Permission naming**: `{ACTION}_{RESOURCE}` (ví dụ: `CREATE_USER`, `VIEW_INSTRUMENT`)
- **SCREAMING_SNAKE_CASE**: Tên permissions phải viết hoa toàn bộ
- **KHÔNG hardcode roles**: Dùng permissions, KHÔNG check role trực tiếp
- **Get current user**: Dùng `SecurityContextHolder.getContext().getAuthentication()`
- **KHÔNG lưu password plain text**: Luôn encode với BCrypt

### Ví dụ Method-level Security

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_USER')")
    public ResponseEntity<?> createUser(...) { }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LAB_MANAGER')")
    public ResponseEntity<?> deleteUser(...) { }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getProfile() { }
}
```

### Get Current User

```java
@Service
public class UserService {

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
```

### Permission Naming

```
✅ CREATE_USER, UPDATE_USER, DELETE_USER, VIEW_USER
✅ CREATE_PATIENT, UPDATE_PATIENT
✅ ACTIVATE_DEACTIVATE_INSTRUMENT
❌ user.create, UserCreate, create_users
```

---

## 13. Mapper (Entity ↔ DTO)

### Quy tắc
- **KHÔNG expose Entity**: Controller và Service chỉ làm việc với DTO
- **Mapping trong Service**: Tạo private helper methods `mapToDTO()` và `mapToEntity()`
- **Manual mapping**: Viết thủ công (KHÔNG dùng MapStruct/ModelMapper cho project nhỏ)
- **Builder pattern**: Dùng `.builder()` của Lombok để tạo DTO
- **Null safety**: Check null trước khi map nested objects
- **KHÔNG map sensitive data**: Password, internal timestamps không map sang Response DTO

### Ví dụ

```java
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponseDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToDTO(user);
    }

    @Override
    public ApiResponse<UserResponseDTO> createUser(CreateUserRequestDTO request) {
        User user = mapToEntity(request);
        user = userRepository.save(user);
        return ApiResponse.<UserResponseDTO>builder()
                .status(true)
                .message("User created successfully")
                .data(mapToDTO(user))
                .build();
    }

    // ===== MAPPER METHODS =====

    /**
     * Map Entity -> Response DTO
     */
    private UserResponseDTO mapToDTO(User entity) {
        return UserResponseDTO.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .fullName(entity.getFullName())
                .gender(entity.getGender())
                .roleCode(entity.getRole() != null ? entity.getRole().getCode() : null)
                .enabled(entity.getEnabled())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    /**
     * Map Request DTO -> Entity (for create)
     */
    private User mapToEntity(CreateUserRequestDTO dto) {
        return User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword())) // Encode password
                .fullName(dto.getFullName())
                .gender(dto.getGender())
                .dateOfBirth(dto.getDateOfBirth())
                .phone(dto.getPhone())
                .enabled(true)
                .build();
    }

    /**
     * Update Entity from DTO (for update)
     */
    private void updateEntityFromDTO(User entity, UpdateUserRequestDTO dto) {
        if (dto.getFullName() != null) {
            entity.setFullName(dto.getFullName());
        }
        if (dto.getPhone() != null) {
            entity.setPhone(dto.getPhone());
        }
        // Update only provided fields
    }
}
```

**Key points:**
- `mapToDTO()`: Entity → Response DTO
- `mapToEntity()`: Request DTO → Entity (create)
- `updateEntityFromDTO()`: Request DTO → Entity (update)
- Check null cho nested objects (`entity.getRole() != null`)
- Encode password khi map

---

## 14. Best Practices

### Quy tắc
- **Logging bắt buộc**:
  - `log.info()` cho write operations (create, update, delete)
  - `log.error()` cho exceptions
  - `log.debug()` cho read operations (nếu cần)
  - KHÔNG log sensitive data (password, token)
- **Constants thay magic values**: Đặt tất cả hardcoded strings/numbers vào Constants class
- **Constructor injection**: Dùng `@RequiredArgsConstructor` với `final` fields (KHÔNG dùng `@Autowired`)
- **Code organization**: 
  - Dependencies ở đầu class
  - Public methods tiếp theo
  - Private helpers cuối cùng
- **Null safety**: Check null trước khi access, dùng `Optional<>` khi phù hợp
- **Comments**: Chỉ comment logic phức tạp, KHÔNG comment code self-explanatory
- **KHÔNG commit commented code**: Xóa code cũ, dùng git history

### Ví dụ

```java
// Logging
@Service
@Slf4j
public class UserServiceImpl {
    public UserDTO createUser(CreateUserRequestDTO request) {
        log.info("Creating user: {}", request.getEmail());
        // Logic...
        log.info("User created successfully");
    }
}

// Constants
public class Constants {
    public static final String ADMIN = "ADMIN";
    public static final int MIN_AGE = 18;
}

// Constructor Injection
@Service
@RequiredArgsConstructor
public class ServiceImpl {
    private final UserRepository userRepository;
    private final EmailService emailService;
}
```

---

## ✅ Checklist

### Entity
- [ ] `@Entity`, `@Table(name = "...")`
- [ ] `@Column(name = "...")` cho mapping
- [ ] `@Enumerated(EnumType.STRING)`
- [ ] Lombok: `@Getter`, `@Setter`, `@Builder`

### DTO
- [ ] Validation annotations: `@NotBlank`, `@Email`, etc.
- [ ] Swagger: `@Schema`
- [ ] Lombok: `@Data`, `@Builder`

### Service
- [ ] Tách interface & implementation
- [ ] `@Transactional` cho write
- [ ] `@Transactional(readOnly = true)` cho read
- [ ] Logging với `@Slf4j`
- [ ] Exception rõ ràng

### Controller
- [ ] `@RestController`, `@RequestMapping`
- [ ] Swagger: `@Tag`, `@Operation`
- [ ] Security: `@PreAuthorize`
- [ ] Validation: `@Valid`
- [ ] Return `ResponseEntity<>`

---

## 📊 Quick Reference

| Component | Naming | Annotation | Return |
|-----------|--------|------------|--------|
| Entity | `PascalCase` | `@Entity` | N/A |
| DTO | `*DTO` | `@Data` | N/A |
| Repository | `*Repository` | `@Repository` | Entity, List, Page, Optional |
| Service | `*Service` | `@Service` | ApiResponse, PageResponse |
| Controller | `*Controller` | `@RestController` | `ResponseEntity<>` |

---

**Version:** 2.0  
**Updated:** January 27, 2026
