package com.example.main.dto.request;

import java.time.Instant;

public record UserSessionRequest(
        long userId,
        String sessionToken,
        Instant expiresAt
) {
}
