package com.example.main.dto.request;

public record UserReportRequest(
        Long userId,
        String module,
        String content
) {
}
