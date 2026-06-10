package com.example.main.ui;

import java.util.List;

public record CodeSnippet(List<CodeLine> lines) {
    public CodeSnippet {
        lines = List.copyOf(lines);
    }
}
