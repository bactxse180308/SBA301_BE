# 📮 Hướng dẫn Test API Global Search trên Postman

> **Base URL:** `http://localhost:8080`

---

## 1. Lấy JWT Token (Login)

API search yêu cầu xác thực JWT. Bạn cần đăng nhập trước để lấy `accessToken`.

### Request

| Thuộc tính   | Giá trị                                |
|-------------|----------------------------------------|
| **Method**  | `POST`                                 |
| **URL**     | `http://localhost:8080/api/v1/auth/login` |
| **Headers** | `Content-Type: application/json`       |

### Body (raw JSON)

```json
{
  "email": "admin@electroshop.com",
  "password": "admin123"
}
```

> ⚠️ Thay `email` và `password` bằng tài khoản có sẵn trong hệ thống (ADMIN hoặc CUSTOMER đều được).

### Response mẫu (200 OK)

```json
{
  "status": 200,
  "message": "Success",
  "data": {
    "userId": 1,
    "email": "admin@electroshop.com",
    "fullName": "Admin User",
    "role": "ADMIN",
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 3600
  },
  "timestamp": "2026-03-12T12:00:00"
}
```

### 💡 Mẹo: Tự động lưu token trong Postman

Vào tab **Tests** của request Login, thêm script:

```javascript
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.environment.set("accessToken", jsonData.data.accessToken);
    pm.environment.set("refreshToken", jsonData.data.refreshToken);
}
```

Sau đó bạn có thể dùng `{{accessToken}}` trong các request khác mà không cần copy thủ công.

---

## 2. Request Test API Search

### Request

| Thuộc tính   | Giá trị                                |
|-------------|----------------------------------------|
| **Method**  | `GET`                                  |
| **URL**     | `http://localhost:8080/api/search`      |

### Headers

| Key             | Value                         |
|-----------------|-------------------------------|
| `Authorization` | `Bearer {{accessToken}}`      |

> Nếu không dùng Postman Environment, thay `{{accessToken}}` bằng chuỗi token thực từ bước 1.

### Query Params

| Key     | Value     | Bắt buộc | Mô tả                                  |
|---------|-----------|----------|-----------------------------------------|
| `q`     | `iphone`  | ✅ Có    | Từ khóa tìm kiếm                       |
| `limit` | `5`       | ❌ Không | Số kết quả tối đa mỗi nhóm (mặc định: 5) |

### URL đầy đủ

```
GET http://localhost:8080/api/search?q=iphone&limit=5
```

### Response mẫu (200 OK)

```json
{
  "status": 200,
  "message": "Success",
  "data": {
    "products": [
      {
        "productId": 1,
        "productName": "iPhone 15 Pro Max",
        "price": 29990000,
        "categoryName": "Điện thoại",
        "brandName": "Apple",
        "status": "ACTIVE",
        "mainImage": "https://example.com/iphone15.jpg"
      }
    ],
    "orders": [
      {
        "orderId": 42,
        "userId": 5,
        "userFullName": "Nguyễn Văn A",
        "totalAmount": 29990000,
        "orderStatus": "DELIVERED",
        "orderDate": "2026-03-10T14:30:00",
        "shippingAddress": "123 Đường ABC",
        "paymentMethod": "COD"
      }
    ],
    "customers": [
      {
        "userId": 5,
        "fullName": "Nguyễn Văn A",
        "email": "nguyenvana@email.com",
        "phoneNumber": "0901234567",
        "role": "CUSTOMER",
        "status": "ACTIVE",
        "registrationDate": "2026-01-15T09:00:00"
      }
    ],
    "reviews": [
      {
        "reviewId": 12,
        "userId": 5,
        "userFullName": "Nguyễn Văn A",
        "productId": 1,
        "productName": "iPhone 15 Pro Max",
        "rating": 5,
        "comment": "iPhone quá tuyệt vời!",
        "reviewDate": "2026-02-20T10:15:00"
      }
    ]
  },
  "timestamp": "2026-03-12T12:00:00"
}
```

---

## 3. Các Test Case

### ✅ Case 1: Tìm kiếm có kết quả

| Thuộc tính       | Giá trị                                      |
|-----------------|-----------------------------------------------|
| **URL**         | `http://localhost:8080/api/search?q=iphone&limit=5` |
| **Authorization** | `Bearer <valid_token>`                      |
| **Expected**    | `200 OK` – Data chứa kết quả trong các nhóm |

**Kiểm tra:**
- `status` = `200`
- `data.products` là mảng (có thể có hoặc không có phần tử)
- `data.orders` là mảng
- `data.customers` là mảng
- `data.reviews` là mảng
- Mỗi mảng có tối đa `limit` phần tử

**Postman Test Script:**

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has grouped results", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.data).to.have.property("products");
    pm.expect(jsonData.data).to.have.property("orders");
    pm.expect(jsonData.data).to.have.property("customers");
    pm.expect(jsonData.data).to.have.property("reviews");
});

pm.test("Each group respects limit", function () {
    var jsonData = pm.response.json();
    var limit = 5; // hoặc lấy từ request param
    pm.expect(jsonData.data.products.length).to.be.at.most(limit);
    pm.expect(jsonData.data.orders.length).to.be.at.most(limit);
    pm.expect(jsonData.data.customers.length).to.be.at.most(limit);
    pm.expect(jsonData.data.reviews.length).to.be.at.most(limit);
});
```

---

### ✅ Case 2: Tìm kiếm không có kết quả

| Thuộc tính       | Giá trị                                                     |
|-----------------|--------------------------------------------------------------|
| **URL**         | `http://localhost:8080/api/search?q=xyznonexistent123&limit=5` |
| **Authorization** | `Bearer <valid_token>`                                     |
| **Expected**    | `200 OK` – Data chứa 4 mảng rỗng                           |

**Response mẫu:**

```json
{
  "status": 200,
  "message": "Success",
  "data": {
    "products": [],
    "orders": [],
    "customers": [],
    "reviews": []
  },
  "timestamp": "2026-03-12T12:00:00"
}
```

**Postman Test Script:**

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("All groups are empty arrays", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.data.products).to.be.an("array").that.is.empty;
    pm.expect(jsonData.data.orders).to.be.an("array").that.is.empty;
    pm.expect(jsonData.data.customers).to.be.an("array").that.is.empty;
    pm.expect(jsonData.data.reviews).to.be.an("array").that.is.empty;
});
```

---

### ✅ Case 3: Thiếu JWT Token (Unauthorized)

| Thuộc tính       | Giá trị                                      |
|-----------------|-----------------------------------------------|
| **URL**         | `http://localhost:8080/api/search?q=iphone&limit=5` |
| **Authorization** | ❌ Không gửi header Authorization            |
| **Expected**    | `401 Unauthorized`                            |

**Response mẫu:**

```json 
{
  "status": 401,
  "message": "Unauthorized. Please login again or provide a valid token.",
  "data": null,
  "timestamp": "2026-03-12T12:00:00"
}
```

**Postman Test Script:**

```javascript
pm.test("Status code is 401", function () {
    pm.response.to.have.status(401);
});

pm.test("Error message is returned", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.status).to.eql(401);
    pm.expect(jsonData.message).to.include("Unauthorized");
});
```

---

### ✅ Case 4: Token hết hạn hoặc không hợp lệ

| Thuộc tính       | Giá trị                                      |
|-----------------|-----------------------------------------------|
| **URL**         | `http://localhost:8080/api/search?q=iphone&limit=5` |
| **Authorization** | `Bearer invalid.token.here`                 |
| **Expected**    | `401 Unauthorized`                            |

---

### ✅ Case 5: Thiếu query param `q`

| Thuộc tính       | Giá trị                                      |
|-----------------|-----------------------------------------------|
| **URL**         | `http://localhost:8080/api/search?limit=5`    |
| **Authorization** | `Bearer <valid_token>`                      |
| **Expected**    | `400 Bad Request` – Missing required param `q` |

---

### ✅ Case 6: Thay đổi limit

| Thuộc tính       | Giá trị                                      |
|-----------------|-----------------------------------------------|
| **URL**         | `http://localhost:8080/api/search?q=samsung&limit=2` |
| **Authorization** | `Bearer <valid_token>`                      |
| **Expected**    | `200 OK` – Mỗi nhóm tối đa 2 kết quả       |

**Postman Test Script:**

```javascript
pm.test("Each group has at most 2 results", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.data.products.length).to.be.at.most(2);
    pm.expect(jsonData.data.orders.length).to.be.at.most(2);
    pm.expect(jsonData.data.customers.length).to.be.at.most(2);
    pm.expect(jsonData.data.reviews.length).to.be.at.most(2);
});
```

---

### ✅ Case 7: Tìm kiếm theo mã đơn hàng

| Thuộc tính       | Giá trị                                      |
|-----------------|-----------------------------------------------|
| **URL**         | `http://localhost:8080/api/search?q=42&limit=5` |
| **Authorization** | `Bearer <valid_token>`                      |
| **Expected**    | `200 OK` – `orders` chứa đơn hàng có ID = 42 |

---

## 4. Tóm tắt các trường tìm kiếm

| Nhóm         | Trường được tìm kiếm                                 |
|-------------|-------------------------------------------------------|
| **Products**  | `productName`, `description`                        |
| **Orders**    | `orderId`, `user.fullName`, `shippingAddress`       |
| **Customers** | `fullName`, `email`, `phoneNumber` (chỉ role CUSTOMER) |
| **Reviews**   | `comment`, `product.productName`, `user.fullName`   |

> 🔍 Tìm kiếm **case-insensitive** – nhập "IPHONE", "iphone", hay "iPhone" đều cho kết quả giống nhau.

---

## 5. Thiết lập nhanh Postman Environment

1. Mở Postman → **Environments** → **New Environment**
2. Đặt tên: `ElectroShop Local`
3. Thêm các biến:

| Variable       | Initial Value                | Current Value              |
|---------------|------------------------------|----------------------------|
| `baseUrl`     | `http://localhost:8080`      | `http://localhost:8080`    |
| `accessToken` | _(để trống, tự set bởi script)_ | _(để trống)_            |
| `refreshToken`| _(để trống, tự set bởi script)_ | _(để trống)_            |

4. Trong mọi request, dùng:
   - URL: `{{baseUrl}}/api/search?q=keyword&limit=5`
   - Header Authorization: `Bearer {{accessToken}}`
