package com.example.main.service;

import com.example.main.dto.Step;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
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
            assertArrayEquals(expected, lastStep.array(), entry.getKey() + " should sort ascending");
        }
    }

    @Test
    void allStrategiesSortDescending() {
        int[] input = {42, 17, 88, 6, 31, 59, 12, 75};
        int[] expected = {88, 75, 59, 42, 31, 17, 12, 6};

        for (Map.Entry<String, SortStrategy> entry : strategies.entrySet()) {
            Step lastStep = lastStep(entry.getValue().sort(input, false));
            assertArrayEquals(expected, lastStep.array(), entry.getKey() + " should sort descending");
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

    @Test
    void allStrategiesHandleCommonInputShapesInBothDirections() {
        List<int[]> inputs = List.of(
                new int[]{4, 2, 4, 1, 2},
                new int[]{1, 2, 3, 4, 5},
                new int[]{5, 4, 3, 2, 1}
        );

        for (int[] input : inputs) {
            int[] ascending = input.clone();
            Arrays.sort(ascending);
            int[] descending = reverse(ascending);

            for (Map.Entry<String, SortStrategy> entry : strategies.entrySet()) {
                assertArrayEquals(ascending, lastStep(entry.getValue().sort(input, true)).array(),
                        entry.getKey() + " should handle input shape ascending");
                assertArrayEquals(descending, lastStep(entry.getValue().sort(input, false)).array(),
                        entry.getKey() + " should handle input shape descending");
            }
        }
    }

    private Step lastStep(java.util.List<Step> steps) {
        assertFalse(steps.isEmpty(), "Strategy should generate at least one step");
        return steps.get(steps.size() - 1);
    }

    private int[] reverse(int[] values) {
        int[] reversed = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            reversed[i] = values[values.length - i - 1];
        }
        return reversed;
    }
}
