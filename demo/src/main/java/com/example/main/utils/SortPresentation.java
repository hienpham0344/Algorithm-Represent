package com.example.main.utils;

import com.example.main.dto.Step;
import com.example.main.enums.StepAction;

import java.util.stream.Collectors;

public final class SortPresentation {
    private SortPresentation() {
    }

    public static String markerText(Step step, int index) {
        return step.markers().stream()
                .filter(marker -> marker.index() == index)
                .map(marker -> marker.label())
                .distinct()
                .collect(Collectors.joining(" · "));
    }

    public static boolean showSwapBadge(Step step, int index) {
        return step.action() == StepAction.SWAP
                && step.countsAsSwap()
                && step.affectedIndices().contains(index);
    }
}




