package com.example.main.service;

import com.example.main.utils.*;
import com.example.main.dto.*;
import com.example.main.enums.*;

import com.example.main.entity.AlgorithmEntity;
import com.example.main.entity.CodeSnippetEntity;
import com.example.main.repository.AlgorithmRepository;
import com.example.main.repository.CodeSnippetRepository;
import com.example.main.dto.AlgorithmLesson;
import com.example.main.dto.CodeLine;
import com.example.main.dto.CodeSnippet;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AlgorithmDataService {

    private final AlgorithmRepository algorithmRepository;
    private final CodeSnippetRepository codeSnippetRepository;

    public AlgorithmDataService(AlgorithmRepository algorithmRepository,
                                CodeSnippetRepository codeSnippetRepository) {
        this.algorithmRepository = algorithmRepository;
        this.codeSnippetRepository = codeSnippetRepository;
    }

    public AlgorithmLesson getLesson(String algorithmCode) {
        Optional<AlgorithmEntity> entity = algorithmRepository.findByAlgorithmCode(algorithmCode);
        if (entity.isEmpty()) {
            return new AlgorithmLesson("No description available.", "-", "-", "-");
        }
        AlgorithmEntity algo = entity.get();
        return new AlgorithmLesson(
                algo.getOverview() != null ? algo.getOverview() : "No description available.",
                algo.getTimeComplexity() != null ? algo.getTimeComplexity() : "-",
                algo.getSpaceComplexity() != null ? algo.getSpaceComplexity() : "-",
                algo.getStability() != null ? algo.getStability() : "-"
        );
    }

    public Map<String, AlgorithmLesson> getAllLessons() {
        Map<String, AlgorithmLesson> lessons = new LinkedHashMap<>();
        for (AlgorithmEntity algo : algorithmRepository.findAllByOrderByNameAsc()) {
            lessons.put(algo.getAlgorithmCode(), new AlgorithmLesson(
                    algo.getOverview() != null ? algo.getOverview() : "No description available.",
                    algo.getTimeComplexity() != null ? algo.getTimeComplexity() : "-",
                    algo.getSpaceComplexity() != null ? algo.getSpaceComplexity() : "-",
                    algo.getStability() != null ? algo.getStability() : "-"
            ));
        }
        return lessons;
    }

    public CodeSnippet getCodeSnippet(String algorithmCode) {
        List<CodeSnippetEntity> entities =
                codeSnippetRepository.findByAlgorithmCodeOrderByLineOrderAsc(algorithmCode);
        if (entities.isEmpty()) {
            return new CodeSnippet(List.of(CodeLine.text("// No code")));
        }
        List<CodeLine> lines = entities.stream()
                .map(entity -> entity.getLineId() != null
                        ? CodeLine.line(entity.getLineId(), entity.getLineText())
                        : CodeLine.text(entity.getLineText()))
                .toList();
        return new CodeSnippet(lines);
    }

    public Map<String, CodeSnippet> getAllCodeSnippets() {
        Map<String, CodeSnippet> snippets = new LinkedHashMap<>();
        for (AlgorithmEntity algo : algorithmRepository.findAllByOrderByNameAsc()) {
            snippets.put(algo.getAlgorithmCode(), getCodeSnippet(algo.getAlgorithmCode()));
        }
        return snippets;
    }

    public Map<String, String> getAlgorithmNames() {
        Map<String, String> names = new LinkedHashMap<>();
        for (AlgorithmEntity algo : algorithmRepository.findAllByOrderByNameAsc()) {
            names.put(algo.getAlgorithmCode(), algo.getName());
        }
        return names;
    }
}





