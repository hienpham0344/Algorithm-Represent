package com.example.main.service;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class DatabaseService {
    
    private static final String DB_URL = "jdbc:sqlite:algorithm_visualizer.db";
    private static DatabaseService instance;
    
    private DatabaseService() {
        initializeDatabase();
    }
    
    public static DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }
    
    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            
            // Tạo bảng users
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password_hash TEXT NOT NULL,
                    full_name TEXT,
                    email TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """;
            
            stmt.execute(createTableSQL);
            
            // Tạo user mặc định (admin/admin123) nếu chưa có
            createDefaultUser();
            
            System.out.println("Database initialized successfully.");
            
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
    
    private void createDefaultUser() {
        String checkSQL = "SELECT COUNT(*) FROM users WHERE username = ?";
        String insertSQL = "INSERT INTO users (username, password_hash, full_name, email) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement checkStmt = conn.prepareStatement(checkSQL)) {
            
            checkStmt.setString(1, "admin");
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) == 0) {
                // Chưa có user admin, tạo mới
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                    insertStmt.setString(1, "admin");
                    insertStmt.setString(2, hashPassword("admin123"));
                    insertStmt.setString(3, "Administrator");
                    insertStmt.setString(4, "admin@example.com");
                    insertStmt.executeUpdate();
                    System.out.println("Default admin user created (admin/admin123)");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error creating default user: " + e.getMessage());
        }
    }
    
    /**
     * Đăng ký user mới
     */
    public boolean registerUser(String username, String password, String fullName, String email) {
        String sql = "INSERT INTO users (username, password_hash, full_name, email) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, hashPassword(password));
            pstmt.setString(3, fullName);
            pstmt.setString(4, email);
            
            pstmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Registration error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Đăng nhập
     */
    public boolean authenticateUser(String username, String password) {
        String sql = "SELECT password_hash FROM users WHERE username = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                return checkPassword(password, storedHash);
            }
            
            return false;
            
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Lấy thông tin user
     */
    public UserInfo getUserInfo(String username) {
        String sql = "SELECT id, username, full_name, email, created_at FROM users WHERE username = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new UserInfo(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("created_at")
                );
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching user info: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Kiểm tra username đã tồn tại chưa
     */
    public boolean isUsernameTaken(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
        }
        
        return false;
    }
    
    // Hash password với BCrypt
    private String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }
    
    // Kiểm tra password
    private boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
    
    /**
     * Class chứa thông tin user
     */
    public static class UserInfo {
        private final int id;
        private final String username;
        private final String fullName;
        private final String email;
        private final String createdAt;
        
        public UserInfo(int id, String username, String fullName, String email, String createdAt) {
            this.id = id;
            this.username = username;
            this.fullName = fullName;
            this.email = email;
            this.createdAt = createdAt;
        }
        
        public int getId() { return id; }
        public String getUsername() { return username; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getCreatedAt() { return createdAt; }
    }
}
