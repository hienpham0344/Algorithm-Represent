package com.example.main.controller;

import com.example.main.dto.SortRequest;
import com.example.main.dto.Step;
import com.example.main.service.SortStrategy;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/sort")
public class SortController {

    private final Map<String, SortStrategy> strategies;

    public SortController(Map<String, SortStrategy> strategies) {
        this.strategies = strategies;
    }

    @PostMapping("/{type}")
    public List<Step> sort(
            @PathVariable String type,
            @RequestBody SortRequest request) {

        SortStrategy strategy = strategies.get(type);

        if (strategy == null) {
            throw new RuntimeException("Not supported: " + type);
        }
        return strategy.sort(request.array, request.asc);
    }
}

