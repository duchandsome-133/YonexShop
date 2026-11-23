# 🏸 YONEX SPORTS E-COMMERCE PLATFORM

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.3.5-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot 3.3.5" />
  <img src="https://img.shields.io/badge/Database-SQL_Server-CC292B?style=for-the-badge&logo=microsoft-sql-server&logoColor=white" alt="SQL Server" />
  <img src="https://img.shields.io/badge/Security-Spring_Security_6-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white" alt="Spring Security 6" />
  <img src="https://img.shields.io/badge/Migration-Flyway-CC0202?style=for-the-badge&logo=flyway&logoColor=white" alt="Flyway" />
  <a href="https://github.com/duchandsome-133/YonexShop/actions/workflows/ci.yml">
    <img src="https://github.com/duchandsome-133/YonexShop/actions/workflows/ci.yml/badge.svg" alt="Java CI with Maven" />
  </a>
</p>

---

## Giới thiệu
Hệ thống thương mại điện tử chuyên cung cấp phụ kiện và dụng cụ thể thao cầu lông Yonex chính hãng, xây dựng trên nền tảng **Spring Boot 3**, **Thymeleaf**, **Spring Security 6** và **Microsoft SQL Server**.

---

## 1. Công nghệ sử dụng
* **Backend:** Java 21, Spring Boot 3.3.5
* **Web MVC & Security:** Spring MVC, Spring Security 6 (BCrypt Password Encoder, CSRF Protection, Role-based Access Control, Form Login)
* **Frontend Rendering:** Thymeleaf 3, Thymeleaf Layout Dialect, Bootstrap 5, Bootstrap Icons, HTML5, CSS3, JavaScript
* **ORM & Database:** Spring Data JPA, Hibernate, **Microsoft SQL Server**
* **Database Migration:** Flyway (`src/main/resources/db/migration`)
* **Kiểm thử:** JUnit 5, Mockito, Spring Security Test
* **Phương thức thanh toán:** COD (Thanh toán bằng tiền mặt khi nhận hàng) duy nhất theo đúng yêu cầu SRS phiên bản 1.0.

---

## 2. Cấu trúc thư mục mã nguồn
```
YonexShop
├── pom.xml
├── README.md
├── .mvn/wrapper/
├── src/main/java/com/yonex/shop/
│   ├── YonexShopApplication.java
│   ├── config/             # Spring Security, CustomUserDetails, DataInitializer, WebMvcConfig
│   ├── controller/         # AuthController, ClientProductController, CartController, CheckoutController, ProfileController
│   │   └── admin/          # AdminDashboardController, AdminProductController, AdminCategoryController, AdminOrderController, AdminInventoryController, AdminUserController
│   ├── dto/                # RegisterRequest, CheckoutRequest, ProductRequest, CategoryRequest, etc.
│   ├── entity/             # User, Role, Address, Category, Product, ProductVariant, Inventory, Cart, Order, OrderItem, Review
│   ├── exception/          # ResourceNotFoundException, BadRequestException
│   ├── repository/         # Spring Data JPA Repositories
│   └── service/            # Business services & implementations
│       └── impl/
├── src/main/resources/
│   ├── application.properties
│   ├── db/migration/
│   │   ├── V1__init_schema.sql   # DDL tạo bảng, khóa ngoại, ràng buộc và index cho SQL Server
│   │   └── V2__seed_data.sql     # Dữ liệu mẫu khởi tạo (Roles, Categories, Products, Inventory, Demo users)
│   ├── static/
│   │   ├── css/style.css
│   │   └── images/
│   └── templates/
│       ├── layout/         # main.html (Customer layout), admin.html (Admin portal layout)
│       ├── client/         # home, product-list, product-detail, cart, checkout, order-success, orders, order-detail, profile, addresses
│       ├── admin/          # dashboard, products, product-form, categories, orders, order-detail, inventory, users, staff, reports
│       ├── auth/           # login, register
│       └── error/          # 403.html
└── src/test/java/com/yonex/shop/
    ├── YonexShopApplicationTests.java
    └── service/
        ├── OrderServiceTest.java
        └── UserServiceTest.java
```

---

## 3. Cấu hình Microsoft SQL Server

### Bước 1: Tạo Database trên SSMS
Mở **SQL Server Management Studio (SSMS)** và chạy câu lệnh:
```sql
CREATE DATABASE YonexShop;
GO
```

### Bước 2: Cấu hình kết nối trong `application.properties`
Trong file `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=YonexShop;encrypt=true;trustServerCertificate=true
spring.datasource.username=${DB_USERNAME:sa}
spring.datasource.password=${DB_PASSWORD:YourStrongPassword123}
```
*Bạn có thể cấu hình biến môi trường `DB_USERNAME` và `DB_PASSWORD` tương ứng với tài khoản SQL Server của máy tính.*

---

## 4. Tài khoản mặc định cho môi trường phát triển (Development)

Hệ thống được nạp sẵn 3 tài khoản demo cho 3 vai trò:

| Vai trò | Email đăng nhập | Mật khẩu | Quyền hạn |
| :--- | :--- | :--- | :--- |
| **Quản trị viên (Admin)** | `admin@yonexshop.vn` | `Password@123` | Toàn quyền quản lý sản phẩm, danh mục, nhân viên, khách hàng, đơn hàng và báo cáo doanh thu |
| **Nhân viên (Staff)** | `staff@yonexshop.vn` | `Password@123` | Xử lý đơn hàng (`PENDING` -> `CONFIRMED` -> `SHIPPING` -> `DELIVERED`), quản lý và điều chỉnh tồn kho |
| **Khách hàng (Customer)** | `customer@yonexshop.vn` | `Password@123` | Mua sắm, quản lý giỏ hàng, đặt hàng COD, theo dõi đơn, hủy đơn chờ xác nhận, đánh giá sản phẩm |

---

## 5. Hướng dẫn chạy ứng dụng

### Chạy trực tiếp từ dòng lệnh:
```bash
# Clean và build dự án
mvn clean package -DskipTests

# Chạy ứng dụng Spring Boot
mvn spring-boot:run
```

Sau khi ứng dụng khởi động thành công:
* **Trang chủ mua sắm:** `http://localhost:8080/`
* **Trang quản trị Admin/Staff:** `http://localhost:8080/admin/dashboard`
* **Trang đăng nhập:** `http://localhost:8080/login`

---

## 6. Tuân thủ các quy tắc nghiệp vụ theo đặc tả SRS
* **BR-01 & BR-02:** Email duy nhất, tài khoản công khai đăng ký mặc định là `ROLE_CUSTOMER`.
* **BR-03 & BR-08:** Bắt buộc đăng nhập để đặt hàng, phương thức thanh toán COD duy nhất.
* **BR-05, BR-10 & BR-16:** Trừ tồn kho nguyên tử (`deductStockConditional`), ngăn chặn race condition khi nhiều khách đặt đồng thời; không cho tồn kho âm.
* **BR-06 & BR-07:** Giá được tính lại từ database phía server khi checkout; snapshot đơn giá, tên sản phẩm, SKU được lưu bất biến vào `OrderItems`.
* **BR-09:** Tổng thanh toán = Tiền hàng + Phí vận chuyển (Miễn phí cho đơn từ 1.000.000đ, đơn dưới 1 triệu tính 30.000đ).
* **BR-11 & BR-12:** Khách hàng chỉ xem/thao tác trên đơn hàng và địa chỉ của chính mình; Staff/Admin xử lý đơn theo quyền.
* **BR-14:** Chỉ đơn hàng đã giao thành công (`DELIVERED`) mới đủ điều kiện đánh giá sản phẩm (1-5 sao).
* **BR-15:** Doanh thu trong báo cáo chỉ tính đơn hàng đã giao thành công (`DELIVERED`); các đơn đã hủy (`CANCELLED`) không được tính.
