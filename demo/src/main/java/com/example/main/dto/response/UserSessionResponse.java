package com.example.main.dto.response;

import java.time.Instant;

public record UserSessionResponse(
        long id,
        long userId,
        String sessionToken,
        Instant expiresAt,
        Instant revokedAt,
        Instant createdAt
) {
}
