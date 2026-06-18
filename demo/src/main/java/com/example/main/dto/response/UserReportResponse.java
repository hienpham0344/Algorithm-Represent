package com.example.main.dto.response;

import java.time.Instant;

public record UserReportResponse(
        long id,
        long userId,
        String username,
        String module,
        String content,
        Instant createdAt
) {
}
