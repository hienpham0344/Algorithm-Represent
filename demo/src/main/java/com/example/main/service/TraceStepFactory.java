package com.example.main.service;

import com.example.main.dto.BarMarker;
import com.example.main.dto.Step;
import com.example.main.dto.StepAction;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

final class TraceStepFactory {
    private TraceStepFactory() {
    }

    static Step step(int[] array,
                     StepAction action,
                     String codeLineId,
                     String explanation,
                     List<BarMarker> markers,
                     Set<Integer> affected,
                     Set<Integer> sorted) {
        return new Step(array, action, codeLineId, explanation, markers, affected, sorted);
    }

    static List<BarMarker> markers(Object... indexAndLabel) {
        List<BarMarker> markers = new ArrayList<>();
        for (int i = 0; i < indexAndLabel.length; i += 2) {
            int index = (Integer) indexAndLabel[i];
            String label = (String) indexAndLabel[i + 1];
            if (index >= 0) {
                markers.add(new BarMarker(index, label));
            }
        }
        return markers;
    }

    static Set<Integer> indices(int... values) {
        Set<Integer> indices = new LinkedHashSet<>();
        for (int value : values) {
            if (value >= 0) {
                indices.add(value);
            }
        }
        return indices;
    }

    static Set<Integer> allIndices(int length) {
        Set<Integer> indices = new LinkedHashSet<>();
        for (int i = 0; i < length; i++) {
            indices.add(i);
        }
        return indices;
    }
}
