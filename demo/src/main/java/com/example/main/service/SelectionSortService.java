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
                String relation = asc ? "nhỏ hơn" : "lớn hơn";
                steps.add(step(a, StepAction.CONDITION, "selection.condition",
                        "a[" + j + "] = " + a[j] + (better ? " " : " không ") + relation
                                + " ứng viên a[" + target + "] = " + a[target] + ".",
                        markers(i, "i", target, "target", j, "j"),
                        indices(target, j), sorted));

                if (better) {
                    target = j;
                    steps.add(step(a, StepAction.VARIABLE_UPDATE, "selection.updateTarget",
                            "Cập nhật target = " + target + " vì " + a[target] + " là ứng viên phù hợp hơn.",
                            markers(i, "i", target, "target", j, "j"),
                            indices(target), sorted));
                }
            }

            if (target != i) {
                int first = a[i];
                int selected = a[target];
                swap(a, i, target);
                steps.add(step(a, StepAction.SWAP, "selection.swap",
                        "Đổi chỗ a[" + i + "] = " + first + " với a[" + target + "] = " + selected + ".",
                        markers(i, "i", target, "target"),
                        indices(i, target), sorted));
            } else {
                steps.add(step(a, StepAction.CONDITION, "selection.swapCondition",
                        "target bằng i nên phần tử hiện tại không cần đổi chỗ.",
                        markers(i, "i", target, "target"), indices(i), sorted));
            }

            sorted.add(i);
            steps.add(step(a, StepAction.MARK_SORTED, "selection.markSorted",
                    "a[" + i + "] = " + a[i] + " đã ở vị trí cuối cùng.",
                    markers(i, "đã xếp"), indices(i), sorted));
        }

        steps.add(step(a, StepAction.COMPLETE, "selection.complete",
                "Selection Sort hoàn tất.", List.of(), Set.of(), allIndices(a.length)));
        return steps;
    }

    private void swap(int[] a, int left, int right) {
        int temp = a[left];
        a[left] = a[right];
        a[right] = temp;
    }
}
