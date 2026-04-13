package com.example.main.service;

import com.example.main.dto.Step;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("quick")
public class QuickSortService implements SortStrategy {

    @Override
    public List<Step> sort(int[] arr, boolean asc) {
        List<Step> steps = new ArrayList<>();
        int[] a = arr.clone();
        quickSort(a, 0, a.length - 1, asc, steps);
        return steps;
    }

    private void quickSort(int[] a, int low, int high, boolean asc, List<Step> steps) {
        if (low < high) {
            int pi = partition(a, low, high, asc, steps);
            steps.add(new Step(a.clone(), pi, pi, "sorted"));
            quickSort(a, low, pi - 1, asc, steps);
            quickSort(a, pi + 1, high, asc, steps);
        } else if (low == high) {
            steps.add(new Step(a.clone(), low, low, "sorted"));
        }
    }

    private int partition(int[] a, int low, int high, boolean asc, List<Step> steps) {
        int pivot = a[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            steps.add(new Step(a.clone(), j, high, "compare"));
            if ((asc && a[j] <= pivot) || (!asc && a[j] >= pivot)) {
                i++;
                int temp = a[i];
                a[i] = a[j];
                a[j] = temp;
                if (i != j) {
                    steps.add(new Step(a.clone(), i, j, "swap"));
                }
            }
        }

        // Place pivot in correct position
        int temp = a[i + 1];
        a[i + 1] = a[high];
        a[high] = temp;
        steps.add(new Step(a.clone(), i + 1, high, "swap"));

        return i + 1;
    }
}
