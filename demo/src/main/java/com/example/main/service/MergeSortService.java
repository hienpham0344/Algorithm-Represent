package com.example.main.service;

import com.example.main.dto.Step;
import com.example.main.dto.StepAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.example.main.service.TraceStepFactory.allIndices;
import static com.example.main.service.TraceStepFactory.indices;
import static com.example.main.service.TraceStepFactory.markers;
import static com.example.main.service.TraceStepFactory.step;

public class MergeSortService implements SortStrategy {

    @Override
    public List<Step> sort(int[] arr, boolean asc) {
        List<Step> steps = new ArrayList<>();
        int[] a = arr.clone();
        mergeSort(a, 0, a.length - 1, asc, steps);
        steps.add(step(a, StepAction.COMPLETE, "merge.complete",
                "Merge Sort hoàn tất.", List.of(), Set.of(), allIndices(a.length)));
        return steps;
    }

    private void mergeSort(int[] a, int left, int right, boolean asc, List<Step> steps) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(a, left, mid, asc, steps);
            mergeSort(a, mid + 1, right, asc, steps);
            merge(a, left, mid, right, asc, steps);
        }
    }

    private void merge(int[] a, int left, int mid, int right, boolean asc, List<Step> steps) {
        int[] leftValues = java.util.Arrays.copyOfRange(a, left, mid + 1);
        int[] rightValues = java.util.Arrays.copyOfRange(a, mid + 1, right + 1);
        int i = 0;
        int j = 0;
        int k = left;

        while (i < leftValues.length && j < rightValues.length) {
            int leftIndex = left + i;
            int rightIndex = mid + 1 + j;
            boolean chooseLeft = asc ? leftValues[i] <= rightValues[j] : leftValues[i] >= rightValues[j];
            steps.add(step(a, StepAction.CONDITION, "merge.work.condition",
                    "So sánh L[" + i + "] = " + leftValues[i] + " và R[" + j + "] = " + rightValues[j]
                            + "; chọn " + (chooseLeft ? "L" : "R") + " để ghi vào a[" + k + "].",
                    markers(leftIndex, "i", rightIndex, "j", k, "k"),
                    indices(leftIndex, rightIndex, k), Set.of()));

            if (chooseLeft) {
                int value = leftValues[i];
                a[k] = value;
                steps.add(step(a, StepAction.WRITE, "merge.work.writeLeft",
                        "Ghi L[" + i + "] = " + value + " vào a[" + k + "].",
                        markers(leftIndex, "i", rightIndex, "j", k, "k"),
                        indices(k), Set.of()));
                i++;
            } else {
                int value = rightValues[j];
                a[k] = value;
                steps.add(step(a, StepAction.WRITE, "merge.work.writeRight",
                        "Ghi R[" + j + "] = " + value + " vào a[" + k + "].",
                        markers(leftIndex, "i", rightIndex, "j", k, "k"),
                        indices(k), Set.of()));
                j++;
            }
            k++;
        }

        while (i < leftValues.length) {
            int value = leftValues[i];
            int sourceIndex = left + i;
            a[k] = value;
            steps.add(step(a, StepAction.WRITE, "merge.work.writeLeftRemainder",
                    "R không còn phần tử; ghi L[" + i + "] = " + value + " vào a[" + k + "].",
                    markers(sourceIndex, "i", k, "k"), indices(k), Set.of()));
            i++;
            k++;
        }

        while (j < rightValues.length) {
            int value = rightValues[j];
            int sourceIndex = mid + 1 + j;
            a[k] = value;
            steps.add(step(a, StepAction.WRITE, "merge.work.writeRightRemainder",
                    "L không còn phần tử; ghi R[" + j + "] = " + value + " vào a[" + k + "].",
                    markers(sourceIndex, "j", k, "k"), indices(k), Set.of()));
            j++;
            k++;
        }
    }
}
