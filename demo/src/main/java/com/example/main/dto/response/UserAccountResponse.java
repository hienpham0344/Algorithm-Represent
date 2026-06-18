package com.example.main.dto.response;

import java.time.Instant;
import com.example.main.enums.Role;

public record UserAccountResponse(
        long id,
        String username,
        Role role,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
}
