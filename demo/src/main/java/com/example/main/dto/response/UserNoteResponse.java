package com.example.main.dto.response;

import java.time.Instant;

public record UserNoteResponse(
        long id,
        long userId,
        long algorithmId,
        String content,
        Instant createdAt,
        Instant updatedAt
) {
}
