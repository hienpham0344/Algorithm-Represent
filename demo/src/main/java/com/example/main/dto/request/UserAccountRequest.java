package com.example.main.dto.request;

import com.example.main.enums.Role;

public record UserAccountRequest(
        String username,
        String password,
        Role role,
        String status
) {
}
