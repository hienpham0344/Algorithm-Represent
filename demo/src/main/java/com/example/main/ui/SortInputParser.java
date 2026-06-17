package com.example.main.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

final class SortInputParser {
    static final int MIN_ITEMS = 2;
    static final int MAX_ITEMS = 25;
    static final int MIN_VALUE = 1;
    static final int MAX_VALUE = 280;

    private SortInputParser() {
    }

    static int[] parse(String text) {
        String content = removeBom(text);
        List<String> tokens = Arrays.stream(content.split("[,\\s]+"))
                .map(String::trim)
                .filter(token -> !token.isEmpty())
                .toList();

        List<Integer> values = new ArrayList<>(tokens.size());
        for (int index = 0; index < tokens.size(); index++) {
            String token = tokens.get(index);
            int position = index + 1;
            int value;
            try {
                value = Integer.parseInt(token);
            } catch (NumberFormatException exception) {
                throw new SortInputValidationException(
                        "Invalid integer '" + token + "' at position " + position + ".",
                        token,
                        position);
            }

            if (value < MIN_VALUE || value > MAX_VALUE) {
                throw new SortInputValidationException(
                        "Value '" + token + "' at position " + position
                                + " must be between " + MIN_VALUE + " and " + MAX_VALUE + ".",
                        token,
                        position);
            }
            values.add(value);
        }

        if (values.size() < MIN_ITEMS) {
            throw new SortInputValidationException(
                    "Enter at least " + MIN_ITEMS + " values.", null, -1);
        }
        if (values.size() > MAX_ITEMS) {
            throw new SortInputValidationException(
                    "Enter at most " + MAX_ITEMS + " values.", null, -1);
        }

        return values.stream().mapToInt(Integer::intValue).toArray();
    }

    static String normalize(int[] values) {
        return Arrays.stream(values)
                .mapToObj(Integer::toString)
                .collect(Collectors.joining(", "));
    }

    private static String removeBom(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }
        return text.charAt(0) == '\uFEFF' ? text.substring(1) : text;
    }
}

class SortInputValidationException extends IllegalArgumentException {
    private final String token;
    private final int tokenPosition;

    SortInputValidationException(String message, String token, int tokenPosition) {
        super(message);
        this.token = token;
        this.tokenPosition = tokenPosition;
    }

    String token() {
        return token;
    }

    int tokenPosition() {
        return tokenPosition;
    }
}
