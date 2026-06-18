# 🔐 Login System - Algorithm Visualizer

## 📌 Tổng quan

Hệ thống đăng nhập đã được tích hợp vào ứng dụng Algorithm Visualizer với các tính năng:

- ✅ Đăng nhập (Login)
- ✅ Đăng ký tài khoản mới (Register)
- ✅ Mã hóa mật khẩu bằng BCrypt
- ✅ Database SQLite (không cần cài đặt server)
- ✅ Session Management
- ✅ Nút Logout trên Dashboard

---

## 🗄️ Cấu trúc Database

### Bảng `users`
```sql
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    full_name TEXT,
    email TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)
```

**Database file**: `algorithm_visualizer.db` (tự động tạo ở thư mục gốc project)

---

## 🔑 Tài khoản mặc định

Khi chạy lần đầu, hệ thống tự động tạo tài khoản admin:

```
Username: admin
Password: admin123
```

---

## 🚀 Cách sử dụng

### 1. Build project
```bash
mvn clean install
```

### 2. Chạy ứng dụng
```bash
mvn javafx:run
```

### 3. Đăng nhập
- Nhập username: `admin`
- Nhập password: `admin123`
- Click "Sign In"

### 4. Đăng ký tài khoản mới
- Click "Sign Up" từ màn hình login
- Điền thông tin:
  - **Username** (bắt buộc, tối thiểu 3 ký tự)
  - **Password** (bắt buộc, tối thiểu 6 ký tự)
  - **Confirm Password** (bắt buộc, phải trùng với password)
  - Full Name (tùy chọn)
  - Email (tùy chọn)
- Click "Create Account"

### 5. Đăng xuất
- Click nút "🚪 Logout" ở góc trên bên phải của Dashboard

---

## 📦 Dependencies đã thêm

```xml
<!-- SQLite Database -->
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.46.0.0</version>
</dependency>

<!-- Password Hashing (BCrypt) -->
<dependency>
    <groupId>org.mindrot</groupId>
    <artifactId>jbcrypt</artifactId>
    <version>0.4</version>
</dependency>
```

---

## 📂 Files đã tạo

### Java Classes
- `DatabaseService.java` - Quản lý database & authentication
- `LoginController.java` - Controller cho màn hình login
- `RegisterController.java` - Controller cho màn hình đăng ký
- `SessionManager.java` - Quản lý session user hiện tại

### FXML Views
- `login-view.fxml` - Giao diện đăng nhập
- `register-view.fxml` - Giao diện đăng ký

### CSS Styles
- `login.css` - Style cho login & register

### Modified Files
- `DashboardApplication.java` - Khởi động từ login screen
- `DashboardController.java` - Thêm chức năng logout
- `dashboard-view.fxml` - Thêm nút logout
- `dashboard.css` - Style cho nút logout
- `pom.xml` - Thêm dependencies

---

## 🔒 Bảo mật

### Password Hashing
- Sử dụng **BCrypt** để hash password
- Không lưu plain text password vào database
- BCrypt tự động thêm salt để tăng bảo mật

### SQL Injection Prevention
- Sử dụng **PreparedStatement** cho tất cả queries
- Không concat trực tiếp user input vào SQL

### Session Management
- User info được lưu trong `SessionManager`
- Logout sẽ clear session và quay về login screen

---

## 🎨 UI/UX Features

### Login Screen
- Dark theme đồng nhất với app
- Hiển thị thông tin tài khoản mặc định
- Link chuyển sang Register
- Hỗ trợ Enter key để login

### Register Screen
- Validation đầy đủ:
  - Username tối thiểu 3 ký tự
  - Password tối thiểu 6 ký tự
  - Confirm password phải trùng
  - Kiểm tra username đã tồn tại
  - Validate email format
- Thông báo lỗi/thành công rõ ràng
- Auto redirect về login sau khi đăng ký thành công

### Dashboard
- Hiển thị nút Logout ở top-right
- Confirmation dialog khi logout

---

## 🧪 Testing

### Test Login
1. Chạy app
2. Đăng nhập với `admin / admin123`
3. Kiểm tra có vào Dashboard không

### Test Register
1. Click "Sign Up"
2. Tạo user mới (username khác "admin")
3. Kiểm tra redirect về login
4. Đăng nhập với user mới

### Test Logout
1. Đăng nhập thành công
2. Click "🚪 Logout"
3. Confirm dialog
4. Kiểm tra quay về login screen

### Test Validation
- Thử đăng ký với username đã tồn tại → Hiển thị lỗi
- Thử password < 6 ký tự → Hiển thị lỗi
- Thử confirm password không trùng → Hiển thị lỗi
- Thử login với sai password → Hiển thị lỗi

---

## 📊 Database Management

### Xem database
Sử dụng DB Browser for SQLite hoặc bất kỳ SQLite client nào:

```bash
# Mở file database
sqlite3 algorithm_visualizer.db

# Xem tất cả users
SELECT * FROM users;

# Xem số lượng users
SELECT COUNT(*) FROM users;
```

### Reset database
Xóa file `algorithm_visualizer.db` và chạy lại app. Database sẽ được tạo lại với tài khoản admin mặc định.

---

## 🔧 Customization

### Thay đổi tài khoản admin mặc định
Sửa trong `DatabaseService.java` → method `createDefaultUser()`:

```java
insertStmt.setString(1, "your_username");
insertStmt.setString(2, hashPassword("your_password"));
```

### Thêm field vào bảng users
1. Sửa SQL trong `initializeDatabase()`
2. Update `UserInfo` class
3. Update `registerUser()` method

### Thay đổi database path
Sửa constant trong `DatabaseService.java`:

```java
private static final String DB_URL = "jdbc:sqlite:path/to/your/database.db";
```

---

## ❓ Troubleshooting

### Lỗi "Failed to load dashboard"
- Kiểm tra file `dashboard-view.fxml` tồn tại
- Kiểm tra path trong `LoginController.openDashboard()`

### Database không tạo được
- Kiểm tra quyền write trong thư mục project
- Xem log console có lỗi SQLite không

### Không login được
- Kiểm tra database đã tạo chưa
- Thử xóa file `.db` và chạy lại
- Kiểm tra username/password có đúng không

### CSS không load
- Kiểm tra file `login.css` trong `resources/styles/`
- Kiểm tra path trong FXML: `stylesheets="@../styles/login.css"`

---

## 📝 Notes

- Database file sẽ được tạo tự động ở thư mục gốc project khi chạy lần đầu
- Password được hash bằng BCrypt, không thể recover nếu quên
- Session chỉ tồn tại trong runtime, sẽ mất khi đóng app

---

## 🎯 Future Improvements

- [ ] "Remember Me" functionality
- [ ] Password reset/recovery
- [ ] Email verification
- [ ] User profile page
- [ ] Role-based access control (Admin/User)
- [ ] Activity logging
- [ ] Password strength indicator
- [ ] Account settings
