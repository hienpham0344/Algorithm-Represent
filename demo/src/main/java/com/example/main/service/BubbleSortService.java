package com.example.main.service;

import com.example.main.utils.*;
import com.example.main.dto.*;
import com.example.main.enums.*;

import com.example.main.dto.Step;
import com.example.main.enums.StepAction;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.example.main.service.TraceStepFactory.allIndices;
import static com.example.main.service.TraceStepFactory.indices;
import static com.example.main.service.TraceStepFactory.markers;
import static com.example.main.service.TraceStepFactory.step;

public class BubbleSortService implements SortStrategy {

    @Override
    public List<Step> sort(int[] arr, boolean asc) {
        List<Step> steps = new ArrayList<>();
        int[] a = arr.clone();
        Set<Integer> sorted = new LinkedHashSet<>();

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length - i - 1; j++) {
                boolean shouldSwap = asc ? a[j] > a[j + 1] : a[j] < a[j + 1];
                String operator = asc ? ">" : "<";
                steps.add(step(a, StepAction.CONDITION, "bubble.condition",
                        "Compare a[" + j + "] = " + a[j] + " " + operator + " a[" + (j + 1)
                                + "] = " + a[j + 1] + ": condition is " + (shouldSwap ? "true." : "false."),
                        markers(i, "i", j, "j", j + 1, "j + 1"),
                        indices(j, j + 1), sorted));

                if (shouldSwap) {
                    int left = a[j];
                    int right = a[j + 1];
                    swap(a, j, j + 1);
                    steps.add(step(a, StepAction.SWAP, "bubble.swap",
                            "Swap " + left + " and " + right + " at a[" + j + "] and a[" + (j + 1) + "].",
                            markers(i, "i", j, "j", j + 1, "j + 1"),
                            indices(j, j + 1), sorted));
                }
            }

            int finalIndex = a.length - i - 1;
            sorted.add(finalIndex);
            steps.add(step(a, StepAction.MARK_SORTED, "bubble.markSorted",
                    "a[" + finalIndex + "] = " + a[finalIndex] + " is in its final position.",
                    markers(finalIndex, "sorted"), indices(finalIndex), sorted));
        }

        steps.add(step(a, StepAction.COMPLETE, "bubble.complete",
                "Bubble Sort complete.", List.of(), Set.of(), allIndices(a.length)));
        return steps;
    }

    private void swap(int[] a, int left, int right) {
        int temp = a[left];
        a[left] = a[right];
        a[right] = temp;
    }
}


