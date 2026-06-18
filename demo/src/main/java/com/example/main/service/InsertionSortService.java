package com.example.main.service;

import com.example.main.utils.*;
import com.example.main.dto.*;
import com.example.main.enums.*;

import com.example.main.dto.Step;
import com.example.main.enums.StepAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.example.main.service.TraceStepFactory.allIndices;
import static com.example.main.service.TraceStepFactory.indices;
import static com.example.main.service.TraceStepFactory.markers;
import static com.example.main.service.TraceStepFactory.step;

public class InsertionSortService implements SortStrategy {

    @Override
    public List<Step> sort(int[] arr, boolean asc) {
        List<Step> steps = new ArrayList<>();
        int[] a = arr.clone();

        for (int i = 1; i < a.length; i++) {
            int key = a[i];
            int j = i - 1;

            while (true) {
                boolean inRange = j >= 0;
                boolean shouldShift = inRange && (asc ? a[j] > key : a[j] < key);
                String detail = inRange
                        ? "a[" + j + "] = " + a[j] + (shouldShift ? " must" : " does not need to")
                                + " shift past key = " + key + "."
                        : "j = -1, stop searching for the insert position of key = " + key + ".";
                steps.add(step(a, StepAction.CONDITION, "insertion.condition", detail,
                        markers(i, "i", j, "j", i, "key"),
                        indices(j, i), Set.of()));

                if (!shouldShift) {
                    break;
                }

                int moved = a[j];
                a[j + 1] = a[j];
                steps.add(step(a, StepAction.WRITE, "insertion.shift",
                        "Shift " + moved + " from a[" + j + "] to a[" + (j + 1) + "].",
                        markers(i, "i", j, "j", j + 1, "j + 1"),
                        indices(j, j + 1), Set.of()));
                j--;
                steps.add(step(a, StepAction.VARIABLE_UPDATE, "insertion.decrement",
                        "Decrement j to " + j + " to continue searching for the position of key = " + key + ".",
                        markers(i, "i", j, "j", i, "key"),
                        indices(j, i), Set.of()));
            }

            a[j + 1] = key;
            steps.add(step(a, StepAction.WRITE, "insertion.insert",
                    "Write key = " + key + " into a[" + (j + 1) + "].",
                    markers(i, "i", j + 1, "key"),
                    indices(j + 1), Set.of()));
        }

        steps.add(step(a, StepAction.COMPLETE, "insertion.complete",
                "Insertion Sort complete.", List.of(), Set.of(), allIndices(a.length)));
        return steps;
    }
}


