package com.example.main.dto;

public record BarMarker(int index, String label) {
    public BarMarker {
        if (index < 0) {
            throw new IllegalArgumentException("index must be non-negative");
        }
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("label must not be blank");
        }
    }
}
