package com.example.main.service;

import com.example.main.dto.Step;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

class SortStrategyTest {

    private final Map<String, SortStrategy> strategies = SortingRegistry.createStrategies();

    @Test
    void allStrategiesSortAscending() {
        int[] input = {42, 17, 88, 6, 31, 59, 12, 75};
        int[] expected = input.clone();
        Arrays.sort(expected);

        for (Map.Entry<String, SortStrategy> entry : strategies.entrySet()) {
            Step lastStep = lastStep(entry.getValue().sort(input, true));
            assertArrayEquals(expected, lastStep.array, entry.getKey() + " should sort ascending");
        }
    }

    @Test
    void allStrategiesSortDescending() {
        int[] input = {42, 17, 88, 6, 31, 59, 12, 75};
        int[] expected = {88, 75, 59, 42, 31, 17, 12, 6};

        for (Map.Entry<String, SortStrategy> entry : strategies.entrySet()) {
            Step lastStep = lastStep(entry.getValue().sort(input, false));
            assertArrayEquals(expected, lastStep.array, entry.getKey() + " should sort descending");
        }
    }

    @Test
    void allStrategiesHandleSingleElementArray() {
        int[] input = {7};

        for (Map.Entry<String, SortStrategy> entry : strategies.entrySet()) {
            assertDoesNotThrow(() -> entry.getValue().sort(input, true), entry.getKey() + " should handle single value");
        }
    }

    @Test
    void allStrategiesHandleEmptyArray() {
        int[] input = {};

        for (Map.Entry<String, SortStrategy> entry : strategies.entrySet()) {
            assertDoesNotThrow(() -> entry.getValue().sort(input, true), entry.getKey() + " should handle empty input");
        }
    }

    private Step lastStep(java.util.List<Step> steps) {
        assertFalse(steps.isEmpty(), "Strategy should generate at least one step");
        return steps.get(steps.size() - 1);
    }
}
