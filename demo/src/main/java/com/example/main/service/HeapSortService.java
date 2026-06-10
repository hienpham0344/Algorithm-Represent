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

public class HeapSortService implements SortStrategy {

    @Override
    public List<Step> sort(int[] arr, boolean asc) {
        List<Step> steps = new ArrayList<>();
        int[] a = arr.clone();
        Set<Integer> sorted = new LinkedHashSet<>();

        for (int i = a.length / 2 - 1; i >= 0; i--) {
            heapify(a, a.length, i, asc, steps, sorted);
        }

        for (int i = a.length - 1; i > 0; i--) {
            int rootValue = a[0];
            int endValue = a[i];
            swap(a, 0, i);
            steps.add(step(a, StepAction.SWAP, "heap.swapRoot",
                    "Đưa gốc heap " + rootValue + " về a[" + i + "] và chuyển " + endValue + " lên gốc.",
                    markers(0, "root", i, "i"), indices(0, i), sorted));

            sorted.add(i);
            steps.add(step(a, StepAction.MARK_SORTED, "heap.markSorted",
                    "a[" + i + "] = " + a[i] + " đã ở vị trí cuối cùng.",
                    markers(i, "đã xếp"), indices(i), sorted));
            heapify(a, i, 0, asc, steps, sorted);
        }

        if (a.length > 0) {
            sorted.add(0);
        }
        steps.add(step(a, StepAction.COMPLETE, "heap.complete",
                "Heap Sort hoàn tất.", List.of(), Set.of(), allIndices(a.length)));
        return steps;
    }

    private void heapify(int[] a,
                         int n,
                         int root,
                         boolean asc,
                         List<Step> steps,
                         Set<Integer> sorted) {
        int target = root;
        int left = 2 * root + 1;
        int right = 2 * root + 2;

        if (left < n) {
            boolean better = higherPriority(a[left], a[target], asc);
            steps.add(step(a, StepAction.CONDITION, "heapify.leftCondition",
                    "So sánh con trái a[" + left + "] = " + a[left] + " với target a[" + target + "] = "
                            + a[target] + ": " + (better ? "chọn con trái." : "giữ target."),
                    markers(root, "root", target, "target", left, "left"),
                    indices(target, left), sorted));
            if (better) {
                target = left;
                steps.add(step(a, StepAction.VARIABLE_UPDATE, "heapify.updateLeft",
                        "Cập nhật target = left = " + left + ".",
                        markers(root, "root", target, "target", left, "left"),
                        indices(target), sorted));
            }
        }

        if (right < n) {
            boolean better = higherPriority(a[right], a[target], asc);
            steps.add(step(a, StepAction.CONDITION, "heapify.rightCondition",
                    "So sánh con phải a[" + right + "] = " + a[right] + " với target a[" + target + "] = "
                            + a[target] + ": " + (better ? "chọn con phải." : "giữ target."),
                    markers(root, "root", target, "target", right, "right"),
                    indices(target, right), sorted));
            if (better) {
                target = right;
                steps.add(step(a, StepAction.VARIABLE_UPDATE, "heapify.updateRight",
                        "Cập nhật target = right = " + right + ".",
                        markers(root, "root", target, "target", right, "right"),
                        indices(target), sorted));
            }
        }

        boolean needsSwap = target != root;
        steps.add(step(a, StepAction.CONDITION, "heapify.swapCondition",
                needsSwap ? "target khác root nên cần đổi chỗ để khôi phục heap."
                        : "target bằng root, nhánh này đã thỏa tính chất heap.",
                markers(root, "root", target, "target"),
                indices(root, target), sorted));

        if (needsSwap) {
            int rootValue = a[root];
            int targetValue = a[target];
            swap(a, root, target);
            steps.add(step(a, StepAction.SWAP, "heapify.swap",
                    "Đổi chỗ root " + rootValue + " với target " + targetValue + ".",
                    markers(root, "root", target, "target"),
                    indices(root, target), sorted));
            heapify(a, n, target, asc, steps, sorted);
        }
    }

    private boolean higherPriority(int candidate, int current, boolean asc) {
        return asc ? candidate > current : candidate < current;
    }

    private void swap(int[] a, int left, int right) {
        int temp = a[left];
        a[left] = a[right];
        a[right] = temp;
    }
}
