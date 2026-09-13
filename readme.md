# FinBank Microservices — Loan Application

## 1. Giới thiệu

Project mô phỏng hệ thống ngân hàng FinBank sử dụng kiến trúc Microservices.

Các service chính:

* **API Gateway**: nhận request từ client và định tuyến đến các Microservice.
* **Customer Service**: quản lý thông tin khách hàng.
* **Account Service**: quản lý tài khoản của khách hàng.
* **Loan Service**: xử lý đăng ký khoản vay và gọi Customer Service, Account Service thông qua OpenFeign.
* **Discovery Server (Eureka)**: đăng ký và tìm kiếm các Microservice.

Luồng tổng quát:

```text
Client
   |
   | POST /api/loans/apply
   v
API Gateway :8222
   |
   | LoggingFilter
   |
   v
Loan Service
   |
   +---- FeignClient ----> Customer Service
   |                       GET /api/customers/{id}
   |
   +---- FeignClient ----> Account Service
   |                       GET /api/accounts/customer/{customerId}
   |
   v
Tính lãi + tiền trả hàng tháng
   |
   v
Lưu Loan
   |
   v
Response
```

---

# 2. Các thành phần hệ thống

| Service          | Port | Chức năng                  |
| ---------------- | ---: | -------------------------- |
| Discovery Server | 8761 | Service Discovery          |
| API Gateway      | 8222 | Gateway / Routing / Filter |
| Customer Service | 8081 | Quản lý khách hàng         |
| Account Service  | 8082 | Quản lý tài khoản          |
| Loan Service     | 8083 | Xử lý khoản vay            |

> Port có thể thay đổi tùy cấu hình thực tế của project.

---

# 3. Loan Service

## 3.1. API đăng ký khoản vay

### Endpoint

```http
POST http://localhost:8222/api/loans/apply
```

Request body:

```json
{
    "customerId": 1,
    "amount": 120000000,
    "termMonths": 12,
    "purpose": "Mua xe"
}
```

---

# 4. Luồng xử lý đăng ký khoản vay

Khi client gọi:

```http
POST /api/loans/apply
```

Loan Service thực hiện các bước:

### Bước 1 — Kiểm tra Customer

Loan Service sử dụng `CustomerServiceClient` để gọi:

```http
GET /api/customers/{id}
```

Ví dụ:

```http
GET /api/customers/1
```

Nếu khách hàng không tồn tại thì trả về lỗi.

---

### Bước 2 — Lấy danh sách tài khoản

Loan Service sử dụng `AccountServiceClient`:

```http
GET /api/accounts/customer/{customerId}
```

Ví dụ:

```http
GET /api/accounts/customer/1
```

Loan Service kiểm tra danh sách tài khoản và tìm tài khoản có trạng thái `ACTIVE`.

Nếu khách hàng không có tài khoản active:

```text
no active account
```

thì không tạo khoản vay.

---

### Bước 3 — Tính lãi

Lãi suất mặc định:

```text
8% / năm
```

Công thức:

```text
Interest = Amount × (InterestRate / 100) × (TermMonths / 12)
```

Tổng tiền phải trả:

```text
TotalPayment = Amount + Interest
```

Tiền phải trả mỗi tháng:

```text
MonthlyPayment = TotalPayment / TermMonths
```

Ví dụ:

```text
Amount       = 120.000.000
InterestRate = 8%
TermMonths   = 12
```

Tiền lãi:

```text
120.000.000 × 8% × 12 / 12
= 9.600.000
```

Tổng tiền phải trả:

```text
120.000.000 + 9.600.000
= 129.600.000
```

Tiền phải trả mỗi tháng:

```text
129.600.000 / 12
= 10.800.000
```

---

### Bước 4 — Tạo khoản vay

Sau khi kiểm tra Customer và Account thành công, Loan Service tạo Loan:

```text
customerId
amount
termMonths
purpose
interestRate = 8%
status = PENDING
createdAt
```

Sau đó lưu vào database.

---

### Bước 5 — Trả response

API trả về thông tin tổng hợp từ nhiều service:

```json
{
    "loanId": 1,
    "customerId": 1,
    "customerName": "Nguyen Van A",
    "phone": "0988888888",
    "accountNumber": "123456789",
    "amount": 120000000,
    "termMonths": 12,
    "interestRate": 8,
    "monthlyPayment": 10800000,
    "status": "PENDING",
    "createdAt": "2026-09-12T15:30:00"
}
```

Trong đó:

* `customerName`, `phone`: lấy từ Customer Service.
* `accountNumber`: lấy từ Account Service.
* `amount`, `termMonths`, `purpose`, `interestRate`, `status`: lấy từ Loan Service.
* `monthlyPayment`: được Loan Service tính toán.

---

# 5. FeignClient

Loan Service sử dụng OpenFeign để giao tiếp đồng bộ với các service khác.

## CustomerServiceClient

```java
@FeignClient(name = "customer-service")
public interface CustomerServiceClient {

    @GetMapping("/api/customers/{id}")
    CustomerResponse getCustomerById(
            @PathVariable Long id
    );
}
```

## AccountServiceClient

```java
@FeignClient(name = "account-service")
public interface AccountServiceClient {

    @GetMapping("/api/accounts/customer/{customerId}")
    List<AccountResponse> getAccountsByCustomerId(
            @PathVariable Long customerId
    );
}
```

Nhờ Eureka, Loan Service không cần hard-code địa chỉ IP/port của Customer Service và Account Service.

---

# 6. API Gateway

API Gateway chạy tại:

```text
http://localhost:8222
```

Client chỉ gọi API thông qua Gateway.

Ví dụ:

```text
Client
   |
   v
localhost:8222
   |
   v
Loan Service
```

Không gọi trực tiếp:

```text
localhost:8083/api/loans/apply
```

mà gọi:

```text
localhost:8222/api/loans/apply
```

---

# 7. LoggingFilter

API Gateway sử dụng `GlobalFilter` để xử lý tất cả request.

Class:

```java
LoggingFilter implements GlobalFilter, Ordered
```

Filter thực hiện:

1. Ghi log HTTP method.
2. Ghi log request path.
3. Ghi thời gian bắt đầu.
4. Đo thời gian xử lý request.
5. Thêm header `X-Response-Time`.
6. Ghi log thời gian xử lý.

Ví dụ:

```text
========== REQUEST ==========
Method: POST
Path: /api/loans/apply
Start Time: 1789212345123

========== RESPONSE ==========
Method: POST
Path: /api/loans/apply
Status: 200 OK
Response Time: 157 ms
```

Response trả về client có header:

```text
X-Response-Time: 157ms
```

Header này có thể kiểm tra trên Postman tại tab **Headers**.

---

