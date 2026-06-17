package com.example.main.ui;

import com.example.main.dto.BarMarker;
import com.example.main.dto.Step;
import com.example.main.dto.StepAction;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SortPresentationTest {

    @Test
    void groupsMultipleMarkerLabelsAtTheSameBar() {
        Step step = new Step(new int[]{4, 8, 2}, StepAction.CONDITION, "quick.partition.condition",
                "Compare element with pivot.",
                List.of(new BarMarker(1, "i"), new BarMarker(1, "j"), new BarMarker(2, "pivot")),
                Set.of(1, 2), Set.of());

        assertEquals("i · j", SortPresentation.markerText(step, 1));
        assertEquals("pivot", SortPresentation.markerText(step, 2));
        assertEquals("", SortPresentation.markerText(step, 0));
    }

    @Test
    void addsSwapBadgeOnlyToAffectedBarsOfRealSwap() {
        Step swap = new Step(new int[]{1, 4}, StepAction.SWAP, "bubble.swap",
                "Swap.", List.of(), Set.of(0, 1), Set.of());
        Step write = new Step(new int[]{1, 4}, StepAction.WRITE, "merge.writeLeft",
                "Write value.", List.of(), Set.of(0), Set.of());

        assertTrue(SortPresentation.showSwapBadge(swap, 0));
        assertTrue(SortPresentation.showSwapBadge(swap, 1));
        assertFalse(SortPresentation.showSwapBadge(write, 0));
    }

    @Test
    void exposesAlgorithmOverviewBeforeAnimation() {
        AlgorithmLesson lesson = AlgorithmLessons.get("quick");

        assertFalse(lesson.overview().isBlank());
        assertFalse(lesson.timeComplexity().isBlank());
        assertFalse(lesson.spaceComplexity().isBlank());
        assertFalse(lesson.stability().isBlank());
    }
}
