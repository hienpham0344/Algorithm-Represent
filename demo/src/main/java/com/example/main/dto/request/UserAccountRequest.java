package com.example.main.dto.request;

public record UserAccountRequest(
        String username,
        String password,
        String status
) {
}
