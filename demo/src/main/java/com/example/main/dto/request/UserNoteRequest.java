package com.example.main.dto.request;

public record UserNoteRequest(
        long userId,
        long algorithmId,
        String content
) {
}
