package com.example.main.dto.request;

import java.util.Map;

public record AlgorithmRequest(
        String algorithmCode,
        String name,
        Map<String, Object> explanation,
        String timeComplexity,
        String spaceComplexity,
        String memory,
        String overview,
        String stability,
        String category,
        String status
) {
}
