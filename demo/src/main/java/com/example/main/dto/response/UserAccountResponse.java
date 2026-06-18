package com.example.main.dto.response;

import java.time.Instant;

public record UserAccountResponse(
        long id,
        String username,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
}
