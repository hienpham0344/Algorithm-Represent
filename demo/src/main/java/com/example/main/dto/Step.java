package com.example.main.dto;

import com.example.main.enums.StepAction;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class Step {
    private final int[] array;
    private final StepAction action;
    private final String codeLineId;
    private final String explanation;
    private final List<BarMarker> markers;
    private final Set<Integer> affectedIndices;
    private final Set<Integer> sortedIndices;

    public Step(int[] array,
                StepAction action,
                String codeLineId,
                String explanation,
                List<BarMarker> markers,
                Set<Integer> affectedIndices,
                Set<Integer> sortedIndices) {
        this.array = Objects.requireNonNull(array, "array").clone();
        this.action = Objects.requireNonNull(action, "action");
        this.codeLineId = requireText(codeLineId, "codeLineId");
        this.explanation = requireText(explanation, "explanation");
        this.markers = List.copyOf(Objects.requireNonNull(markers, "markers"));
        this.affectedIndices = Set.copyOf(new LinkedHashSet<>(
                Objects.requireNonNull(affectedIndices, "affectedIndices")));
        this.sortedIndices = Set.copyOf(new LinkedHashSet<>(
                Objects.requireNonNull(sortedIndices, "sortedIndices")));
    }

    public int[] array() {
        return array.clone();
    }

    public StepAction action() {
        return action;
    }

    public String codeLineId() {
        return codeLineId;
    }

    public String explanation() {
        return explanation;
    }

    public List<BarMarker> markers() {
        return markers;
    }

    public Set<Integer> affectedIndices() {
        return affectedIndices;
    }

    public Set<Integer> sortedIndices() {
        return sortedIndices;
    }

    public boolean countsAsSwap() {
        return action == StepAction.SWAP && affectedIndices.size() > 1;
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }
}
