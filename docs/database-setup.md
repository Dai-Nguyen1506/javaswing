# Hướng Dẫn Kết Nối PostgreSQL

## Tổng Quan
Ứng dụng Sunshine Seashore đã được nâng cấp để hỗ trợ hai phương thức lưu trữ:
1. **File-based storage** (mặc định cũ)
2. **PostgreSQL database** (mới)

## Cài Đặt PostgreSQL

### Bước 1: Cài đặt PostgreSQL
1. Tải và cài đặt PostgreSQL từ: https://www.postgresql.org/download/
2. Trong quá trình cài đặt, thiết lập:
   - Username: `postgres`
   - Password: (chọn password của bạn, ví dụ: `postgres`)
   - Port: `5432` (mặc định)

### Bước 2: Tạo Database
1. Mở **pgAdmin 4** hoặc **psql** command line
2. Chạy script tạo database từ file `db/project.sql`:
   ```sql
   -- Tạo database
   CREATE DATABASE "OOP Java"
       WITH
       OWNER = postgres
       ENCODING = 'UTF8';
   ```

3. Chạy script tạo bảng và enum types (phần còn lại trong `db/project.sql`)

### Bước 3: Import Dữ Liệu Mẫu
Chạy script `db/seed.sql` để tạo dữ liệu mẫu:
```sql
-- Thực hiện tất cả các INSERT statements trong file seed.sql
```

## Cấu Hình Ứng Dụng

### 1. Cập nhật file `src/database.properties`
Mở file và cập nhật thông tin kết nối của bạn:
```properties
db.url=jdbc:postgresql://localhost:5432/OOP Java
db.username=postgres
db.password=your_password_here
```

**Lưu ý:** Thay `your_password_here` bằng password PostgreSQL của bạn.

### 2. Bật/Tắt chế độ Database
Trong file `src/com/oop/project/Main.java`, tìm dòng:
```java
private static final boolean USE_DATABASE = true;
```

- `true`: Sử dụng PostgreSQL database
- `false`: Sử dụng file-based storage (cũ)

## Cài Đặt Dependencies

### Maven Dependencies
File `pom.xml` đã được cập nhật với PostgreSQL JDBC driver:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.1</version>
</dependency>
```

### Cài đặt dependencies
Chạy lệnh sau trong terminal:
```bash
mvn clean install
```

## Cấu Trúc Database

### Các Bảng Chính

1. **users** - Quản lý người dùng và xác thực
   - user_id (PK)
   - user_name, user_password, user_role
   - user_email, user_phone_number

2. **customer** - Thông tin khách hàng
   - customer_id (PK)
   - cus_name, cus_email, cus_phone_number

3. **equipment** - Thiết bị cho thuê
   - equipment_id (PK)
   - equipment_name, equipment_fee
   - equipment_number, equipment_available

4. **items** - Chi tiết từng thiết bị
   - item_id (PK)
   - item_name, item_condition, item_status
   - equipment_id (FK)

5. **instructor** - Huấn luyện viên
   - instructor_id (PK)
   - instructor_name, instructor_email, instructor_phone_number

6. **lesson** - Gói học
   - lesson_id (PK)
   - lesson_name, lesson_fee
   - instructor_id (FK)

7. **rental_contract** - Hợp đồng thuê
   - rental_id (PK)
   - rental_duration, rental_fee, rental_status
   - lesson_id (FK), customer_id (FK), equipment_id (FK)

8. **log_history** - Lịch sử đăng nhập/đăng xuất
   - log_id (PK)
   - log_status, log_time
   - user_id (FK)

### Enum Types
- `user_role_enum`: Admin, Staff
- `log_status_enum`: login, logout
- `item_condition_enum`: Good, Needs Repair, Out of Service
- `item_status_enum`: Available, Unavailable
- `rental_status_enum`: Draft, Active, Returned, Completed, Overdue

## Repository Implementations

### File-based (Cũ)
- `FileUserRepository`
- `FileCustomerRepository`
- `FileEquipmentRepository`
- `FileLessonPackageRepository`
- `FileRentalContractRepository`

### Database-based (Mới)
- `DbUserRepository`
- `DbCustomerRepository`
- `DbEquipmentRepository`
- `DbLessonPackageRepository`
- `DbRentalContractRepository`
- `DbInstructorRepository`

## Chạy Ứng Dụng

### Sử dụng Maven
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.oop.project.Main"
```

### Sử dụng IDE
1. Import project vào Eclipse/IntelliJ
2. Chạy `Main.java`

## Kiểm Tra Kết Nối

Khi chạy ứng dụng với `USE_DATABASE = true`:
- Nếu kết nối thành công: Console sẽ hiển thị "Using PostgreSQL database for storage"
- Nếu kết nối thất bại: Hiển thị dialog lỗi và thoát ứng dụng

## Troubleshooting

### Lỗi: "Failed to connect to database"
**Nguyên nhân:**
- PostgreSQL chưa được khởi động
- Database "OOP Java" chưa được tạo
- Thông tin kết nối sai

**Giải pháp:**
1. Kiểm tra PostgreSQL đang chạy (Windows Services hoặc pgAdmin)
2. Xác nhận database "OOP Java" tồn tại
3. Kiểm tra file `database.properties`

### Lỗi: "PostgreSQL Driver not found"
**Giải pháp:**
```bash
mvn clean install -U
```

### Lỗi: Không tìm thấy file database.properties
**Giải pháp:**
- Đảm bảo file `database.properties` nằm trong `src/` folder
- File sẽ tự động được copy vào classpath khi compile

## Dữ Liệu Mẫu

### Users (từ seed.sql)
- **admin01** / adminpass (Admin)
- **staff01** / staffpass1 (Staff)
- **staff02** / staffpass2 (Staff)

### Customers
10 khách hàng mẫu (Alice, Bob, Charlie, ...)

### Equipment
5 loại thiết bị, mỗi loại 10 items:
- Surfboard
- Kayak
- Paddleboard
- Snorkeling Gear
- Beach Bike

### Instructors
3 huấn luyện viên:
- Mark Ocean
- Luna Wave
- Jack Tide

### Lessons
5 gói học với giá từ $15-$25

### Rental Contracts
30 hợp đồng mẫu với các trạng thái khác nhau

## Lưu Ý Quan Trọng

1. **Backup Data**: Trước khi chuyển từ file sang database, backup các file trong `data/` folder
2. **Migration**: Không có công cụ tự động migrate từ file sang database. Cần import dữ liệu thủ công nếu cần
3. **Performance**: Database sẽ nhanh hơn với số lượng records lớn
4. **Concurrent Access**: Database hỗ trợ nhiều user đồng thời tốt hơn file-based

## Chuyển Đổi Giữa File và Database

### Từ File sang Database
1. Export dữ liệu từ files
2. Tạo INSERT statements tương ứng
3. Chạy SQL scripts
4. Đổi `USE_DATABASE = true`

### Từ Database sang File
1. Export dữ liệu từ database
2. Tạo CSV/serialized files
3. Đổi `USE_DATABASE = false`

## Tài Liệu Tham Khảo

- PostgreSQL Documentation: https://www.postgresql.org/docs/
- JDBC Tutorial: https://docs.oracle.com/javase/tutorial/jdbc/
- Maven Dependencies: https://mvnrepository.com/artifact/org.postgresql/postgresql

---
**Tác giả:** Sunshine Seashore Development Team  
**Ngày cập nhật:** 2026-01-29
