package com.example.main.service;

import com.example.main.dto.BarMarker;
import com.example.main.dto.Step;
import com.example.main.dto.StepAction;
import com.example.main.ui.CodeSnippets;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SortTraceContractTest {

    private final Map<String, SortStrategy> strategies = SortingRegistry.createStrategies();

    @Test
    void everyTraceStepReferencesDisplayedCodeAndValidMarkers() {
        int[] input = {7, 3, 5, 1};

        for (Map.Entry<String, SortStrategy> entry : strategies.entrySet()) {
            List<Step> steps = entry.getValue().sort(input, true);

            assertFalse(steps.isEmpty(), entry.getKey() + " should create a trace");
            for (Step step : steps) {
                assertTrue(CodeSnippets.containsLine(entry.getKey(), step.codeLineId()),
                        () -> entry.getKey() + " references missing line " + step.codeLineId());
                assertFalse(step.explanation().isBlank(), entry.getKey() + " should explain every step");
                for (BarMarker marker : step.markers()) {
                    assertTrue(marker.index() >= 0 && marker.index() < step.array().length,
                            () -> entry.getKey() + " has invalid marker " + marker);
                    assertFalse(marker.label().isBlank());
                }
                for (int index : step.affectedIndices()) {
                    assertTrue(index >= 0 && index < step.array().length);
                }
                for (int index : step.sortedIndices()) {
                    assertTrue(index >= 0 && index < step.array().length);
                }
            }
        }
    }

    @Test
    void stepDefensivelyCopiesArraysAndCollections() {
        int[] source = {3, 1};
        List<BarMarker> markers = new java.util.ArrayList<>(List.of(new BarMarker(0, "i")));
        java.util.Set<Integer> affected = new java.util.LinkedHashSet<>(List.of(0, 1));
        java.util.Set<Integer> sorted = new java.util.LinkedHashSet<>(List.of(1));

        Step step = new Step(source, StepAction.SWAP, "bubble.swap",
                "Đổi chỗ hai phần tử.", markers, affected, sorted);

        source[0] = 99;
        markers.clear();
        affected.clear();
        sorted.clear();
        int[] returned = step.array();
        returned[0] = 88;

        assertArrayEquals(new int[]{3, 1}, step.array());
        assertEquals(List.of(new BarMarker(0, "i")), step.markers());
        assertEquals(java.util.Set.of(0, 1), step.affectedIndices());
        assertEquals(java.util.Set.of(1), step.sortedIndices());
    }

    @Test
    void swapCountOnlyIncludesRealSwaps() {
        Step realSwap = new Step(new int[]{1, 2}, StepAction.SWAP, "bubble.swap",
                "Đổi chỗ.", List.of(), java.util.Set.of(0, 1), java.util.Set.of());
        Step selfSwap = new Step(new int[]{1}, StepAction.SWAP, "quick.placePivot",
                "Pivot đã đúng vị trí.", List.of(), java.util.Set.of(0), java.util.Set.of());
        Step write = new Step(new int[]{1, 2}, StepAction.WRITE, "merge.writeLeft",
                "Ghi phần tử.", List.of(), java.util.Set.of(0), java.util.Set.of());

        assertTrue(realSwap.countsAsSwap());
        assertFalse(selfSwap.countsAsSwap());
        assertFalse(write.countsAsSwap());
    }

    @Test
    void eachAlgorithmEmitsItsLearningActions() {
        assertActions("bubble", new int[]{3, 1}, StepAction.CONDITION, StepAction.SWAP, StepAction.MARK_SORTED);
        assertActions("selection", new int[]{3, 1}, StepAction.CONDITION, StepAction.VARIABLE_UPDATE, StepAction.SWAP);
        assertActions("insertion", new int[]{3, 1}, StepAction.CONDITION, StepAction.WRITE);
        assertActions("heap", new int[]{1, 3, 2}, StepAction.CONDITION, StepAction.VARIABLE_UPDATE, StepAction.SWAP);
        assertActions("quick", new int[]{3, 1, 2}, StepAction.CONDITION, StepAction.VARIABLE_UPDATE, StepAction.SWAP);
        assertActions("merge", new int[]{3, 1}, StepAction.CONDITION, StepAction.WRITE);
    }

    @Test
    void singleElementAlgorithmsStillEmitCompletion() {
        for (Map.Entry<String, SortStrategy> entry : strategies.entrySet()) {
            List<Step> steps = entry.getValue().sort(new int[]{7}, true);

            assertFalse(steps.isEmpty(), entry.getKey() + " should explain a single element");
            assertEquals(StepAction.COMPLETE, steps.get(steps.size() - 1).action());
            assertArrayEquals(new int[]{7}, steps.get(steps.size() - 1).array());
        }
    }

    @Test
    void quickSortExposesPartitionMarkers() {
        List<Step> steps = strategies.get("quick").sort(new int[]{3, 1, 2}, true);

        assertTrue(steps.stream().anyMatch(step -> hasMarker(step, "pivot")));
        assertTrue(steps.stream().anyMatch(step -> hasMarker(step, "i")));
        assertTrue(steps.stream().anyMatch(step -> hasMarker(step, "j")));
        assertTrue(steps.stream().anyMatch(step -> step.codeLineId().startsWith("quick.partition.")));
    }

    @Test
    void mergeSortUsesWritesInsteadOfSwaps() {
        List<Step> steps = strategies.get("merge").sort(new int[]{4, 1, 3, 2}, true);

        assertTrue(steps.stream().anyMatch(step -> step.action() == StepAction.WRITE));
        assertFalse(steps.stream().anyMatch(Step::countsAsSwap));
        assertTrue(steps.stream().anyMatch(step -> hasMarker(step, "k")));
    }

    @Test
    void codeLineIdsAreStableEvenWhenDisplayLineNumbersDiffer() {
        int conditionLine = CodeSnippets.lineIndex("bubble", "bubble.condition");
        int swapLine = CodeSnippets.lineIndex("bubble", "bubble.swap");

        assertTrue(conditionLine >= 0);
        assertTrue(swapLine >= 0);
        assertNotEquals(conditionLine, swapLine);
    }

    @Test
    void missingActiveCodeLineIsRepresentedByMinusOne() {
        assertEquals(-1, CodeSnippets.lineIndex("bubble", null));
        assertEquals(-1, CodeSnippets.lineIndex("bubble", "unknown.line"));
    }

    private void assertActions(String algorithm, int[] input, StepAction... expected) {
        List<StepAction> actions = strategies.get(algorithm).sort(input, true).stream()
                .map(Step::action)
                .toList();

        for (StepAction action : expected) {
            assertTrue(actions.contains(action), algorithm + " should emit " + action);
        }
        assertEquals(StepAction.COMPLETE, actions.get(actions.size() - 1));
    }

    private boolean hasMarker(Step step, String label) {
        return step.markers().stream().anyMatch(marker -> marker.label().equals(label));
    }
}
