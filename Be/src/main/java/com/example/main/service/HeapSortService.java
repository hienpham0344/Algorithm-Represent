package com.example.main.service;

import com.example.main.dto.Step;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("heap")
public class HeapSortService implements SortStrategy {

    @Override
    public List<Step> sort(int[] arr, boolean asc) {
        List<Step> steps = new ArrayList<>();
        int[] a = arr.clone();
        int n = a.length;

        // Build heap (max-heap for asc, min-heap for desc)
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(a, n, i, asc, steps);
        }

        // Extract elements one by one
        for (int i = n - 1; i > 0; i--) {
            // Move current root to end
            int temp = a[0];
            a[0] = a[i];
            a[i] = temp;
            steps.add(new Step(a.clone(), 0, i, "swap"));
            steps.add(new Step(a.clone(), i, i, "sorted"));

            heapify(a, i, 0, asc, steps);
        }
        steps.add(new Step(a.clone(), 0, 0, "sorted"));

        return steps;
    }

    private void heapify(int[] a, int n, int root, boolean asc, List<Step> steps) {
        int target = root;
        int left = 2 * root + 1;
        int right = 2 * root + 2;

        if (left < n) {
            steps.add(new Step(a.clone(), target, left, "compare"));
            if ((asc && a[left] > a[target]) || (!asc && a[left] < a[target])) {
                target = left;
            }
        }

        if (right < n) {
            steps.add(new Step(a.clone(), target, right, "compare"));
            if ((asc && a[right] > a[target]) || (!asc && a[right] < a[target])) {
                target = right;
            }
        }

        if (target != root) {
            int temp = a[root];
            a[root] = a[target];
            a[target] = temp;
            steps.add(new Step(a.clone(), root, target, "swap"));

            heapify(a, n, target, asc, steps);
        }
    }
}
