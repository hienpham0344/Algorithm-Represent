package com.example.main.service;

import com.example.main.utils.*;
import com.example.main.dto.*;
import com.example.main.enums.*;

import java.util.LinkedHashMap;
import java.util.Map;

public final class SortingRegistry {

    private SortingRegistry() {
    }

    public static Map<String, SortStrategy> createStrategies() {
        Map<String, SortStrategy> strategies = new LinkedHashMap<>();
        strategies.put("selection", new SelectionSortService());
        strategies.put("bubble", new BubbleSortService());
        strategies.put("insertion", new InsertionSortService());
        strategies.put("heap", new HeapSortService());
        strategies.put("quick", new QuickSortService());
        strategies.put("merge", new MergeSortService());
        return strategies;
    }
}

