package com.example.main.service;

import com.example.main.dto.Step;
import com.example.main.dto.StepAction;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.example.main.service.TraceStepFactory.allIndices;
import static com.example.main.service.TraceStepFactory.indices;
import static com.example.main.service.TraceStepFactory.markers;
import static com.example.main.service.TraceStepFactory.step;

public class SelectionSortService implements SortStrategy {

    @Override
    public List<Step> sort(int[] arr, boolean asc) {
        List<Step> steps = new ArrayList<>();
        int[] a = arr.clone();
        Set<Integer> sorted = new LinkedHashSet<>();

        for (int i = 0; i < a.length; i++) {
            int target = i;

            for (int j = i + 1; j < a.length; j++) {
                boolean better = asc ? a[j] < a[target] : a[j] > a[target];
                String relation = asc ? "smaller than" : "larger than";
                steps.add(step(a, StepAction.CONDITION, "selection.condition",
                        "a[" + j + "] = " + a[j] + (better ? " is " : " is not ") + relation
                                + " candidate a[" + target + "] = " + a[target] + ".",
                        markers(i, "i", target, "target", j, "j"),
                        indices(target, j), sorted));

                if (better) {
                    target = j;
                    steps.add(step(a, StepAction.VARIABLE_UPDATE, "selection.updateTarget",
                            "Update target = " + target + " because " + a[target] + " is a better candidate.",
                            markers(i, "i", target, "target", j, "j"),
                            indices(target), sorted));
                }
            }

            if (target != i) {
                int first = a[i];
                int selected = a[target];
                swap(a, i, target);
                steps.add(step(a, StepAction.SWAP, "selection.swap",
                        "Swap a[" + i + "] = " + first + " with a[" + target + "] = " + selected + ".",
                        markers(i, "i", target, "target"),
                        indices(i, target), sorted));
            } else {
                steps.add(step(a, StepAction.CONDITION, "selection.swapCondition",
                        "target equals i, so the current element does not need to be swapped.",
                        markers(i, "i", target, "target"), indices(i), sorted));
            }

            sorted.add(i);
            steps.add(step(a, StepAction.MARK_SORTED, "selection.markSorted",
                    "a[" + i + "] = " + a[i] + " is in its final position.",
                    markers(i, "sorted"), indices(i), sorted));
        }

        steps.add(step(a, StepAction.COMPLETE, "selection.complete",
                "Selection Sort complete.", List.of(), Set.of(), allIndices(a.length)));
        return steps;
    }

    private void swap(int[] a, int left, int right) {
        int temp = a[left];
        a[left] = a[right];
        a[right] = temp;
    }
}
