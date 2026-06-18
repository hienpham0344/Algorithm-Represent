package com.example.main.dto;

import com.example.main.SortAlgorithmPresentApplication;
import com.example.main.service.AlgorithmDataService;

import java.util.List;

public final class CodeSnippets {

    private CodeSnippets() {
    }

    public static CodeSnippet get(String algorithm) {
        if (SortAlgorithmPresentApplication.context() != null) {
            AlgorithmDataService service = SortAlgorithmPresentApplication.context().getBean(AlgorithmDataService.class);
            return service.getCodeSnippet(algorithm);
        }
        return new CodeSnippet(List.of(CodeLine.text("// No code")));
    }

    public static boolean containsLine(String algorithm, String lineId) {
        return lineIndex(algorithm, lineId) >= 0;
    }

    public static int lineIndex(String algorithm, String lineId) {
        if (lineId == null) {
            return -1;
        }
        List<CodeLine> lines = get(algorithm).lines();
        for (int index = 0; index < lines.size(); index++) {
            if (lineId.equals(lines.get(index).id())) {
                return index;
            }
        }
        return -1;
    }
}

