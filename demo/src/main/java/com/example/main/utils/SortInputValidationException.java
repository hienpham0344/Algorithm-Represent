package com.example.main.utils;

public class SortInputValidationException extends IllegalArgumentException {
    private final String token;
    private final int tokenPosition;

    public SortInputValidationException(String message, String token, int tokenPosition) {
        super(message);
        this.token = token;
        this.tokenPosition = tokenPosition;
    }

    public String token() {
        return token;
    }

    public int tokenPosition() {
        return tokenPosition;
    }
}
