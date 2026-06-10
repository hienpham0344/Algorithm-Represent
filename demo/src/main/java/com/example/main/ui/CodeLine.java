package com.example.main.ui;

public record CodeLine(String id, String text) {
    public CodeLine {
        text = text == null ? "" : text;
    }

    public static CodeLine line(String id, String text) {
        return new CodeLine(id, text);
    }

    public static CodeLine text(String text) {
        return new CodeLine(null, text);
    }
}
