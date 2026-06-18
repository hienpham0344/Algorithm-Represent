package com.example.main.ui;

import com.example.main.service.DatabaseService;

/**
 * Quản lý session của user hiện tại
 */
public class SessionManager {
    
    private static DatabaseService.UserInfo currentUser;
    
    public static void setCurrentUser(DatabaseService.UserInfo user) {
        currentUser = user;
    }
    
    public static DatabaseService.UserInfo getCurrentUser() {
        return currentUser;
    }
    
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public static void logout() {
        currentUser = null;
    }
    
    public static String getCurrentUsername() {
        return currentUser != null ? currentUser.getUsername() : "Guest";
    }
}
