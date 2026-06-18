package com.example.main.session;

import com.example.main.dto.response.UserAccountResponse;
import com.example.main.enums.Role;

/**
 * Giữ thông tin người dùng đang đăng nhập cho toàn ứng dụng JavaFX.
 */
public final class Session {

    private static UserAccountResponse currentUser;

    private Session() {
    }

    public static void setCurrentUser(UserAccountResponse user) {
        currentUser = user;
    }

    public static UserAccountResponse currentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean isAdmin() {
        return currentUser != null && currentUser.role() == Role.ADMIN;
    }

    public static void clear() {
        currentUser = null;
    }
}
