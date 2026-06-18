package com.example.main.dto.response;

import java.time.Instant;
import java.util.Map;

public record AlgorithmResponse(
        long id,
        String algorithmCode,
        String name,
        Map<String, Object> explanation,
        String timeComplexity,
        String spaceComplexity,
        String memory,
        String overview,
        String stability,
        String category,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
}
