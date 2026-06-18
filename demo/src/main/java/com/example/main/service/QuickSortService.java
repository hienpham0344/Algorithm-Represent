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

public class QuickSortService implements SortStrategy {

    @Override
    public List<Step> sort(int[] arr, boolean asc) {
        List<Step> steps = new ArrayList<>();
        int[] a = arr.clone();
        Set<Integer> sorted = new LinkedHashSet<>();
        quickSort(a, 0, a.length - 1, asc, steps, sorted);
        steps.add(step(a, StepAction.COMPLETE, "quick.complete",
                "Quick Sort complete.", List.of(), Set.of(), allIndices(a.length)));
        return steps;
    }

    private void quickSort(int[] a,
                           int low,
                           int high,
                           boolean asc,
                           List<Step> steps,
                           Set<Integer> sorted) {
        if (low < high) {
            int pivotIndex = partition(a, low, high, asc, steps, sorted);
            sorted.add(pivotIndex);
            steps.add(step(a, StepAction.MARK_SORTED, "quick.partition.return",
                    "Pivot at a[" + pivotIndex + "] = " + a[pivotIndex] + " is in its final position.",
                    markers(pivotIndex, "pivot"), indices(pivotIndex), sorted));
            quickSort(a, low, pivotIndex - 1, asc, steps, sorted);
            quickSort(a, pivotIndex + 1, high, asc, steps, sorted);
        } else if (low == high) {
            sorted.add(low);
            steps.add(step(a, StepAction.MARK_SORTED, "quick.singleton",
                    "Region contains only a[" + low + "], so this element is already sorted.",
                    markers(low, "sorted"), indices(low), sorted));
        }
    }

    private int partition(int[] a,
                          int low,
                          int high,
                          boolean asc,
                          List<Step> steps,
                          Set<Integer> sorted) {
        int pivot = a[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            boolean belongsBefore = asc ? a[j] <= pivot : a[j] >= pivot;
            steps.add(step(a, StepAction.CONDITION, "quick.partition.condition",
                    "Compare a[" + j + "] = " + a[j] + " with pivot = " + pivot
                            + ": element " + (belongsBefore ? "belongs" : "does not belong") + " in the left region.",
                    markers(i, "i", j, "j", high, "pivot"),
                    indices(j, high), sorted));

            if (belongsBefore) {
                i++;
                steps.add(step(a, StepAction.VARIABLE_UPDATE, "quick.partition.incrementI",
                        "Increment i to " + i + " to expand the left region of the pivot.",
                        markers(i, "i", j, "j", high, "pivot"),
                        indices(i, j, high), sorted));

                boolean needsSwap = i != j;
                steps.add(step(a, StepAction.CONDITION, "quick.partition.swapCondition",
                        needsSwap ? "i differs from j, move the current element into the left region."
                                : "i equals j, the element is already in the correct region.",
                        markers(i, "i", j, "j", high, "pivot"),
                        indices(i, j), sorted));
                if (needsSwap) {
                    int left = a[i];
                    int current = a[j];
                    swap(a, i, j);
                    steps.add(step(a, StepAction.SWAP, "quick.partition.swap",
                            "Swap " + left + " and " + current + " at i = " + i + ", j = " + j + ".",
                            markers(i, "i", j, "j", high, "pivot"),
                            indices(i, j), sorted));
                }
            }
        }

        int pivotIndex = i + 1;
        int displaced = a[pivotIndex];
        swap(a, pivotIndex, high);
        steps.add(step(a, StepAction.SWAP, "quick.partition.placePivot",
                pivotIndex == high
                        ? "Pivot is already at a[" + high + "]; no actual swap occurred."
                        : "Place pivot " + pivot + " at a[" + pivotIndex + "] and move " + displaced + " to a[" + high + "].",
                markers(pivotIndex, "i + 1", high, "pivot"),
                indices(pivotIndex, high), sorted));
        return pivotIndex;
    }

    private void swap(int[] a, int left, int right) {
        int temp = a[left];
        a[left] = a[right];
        a[right] = temp;
    }
}


