package com.example.main.service;

import com.example.main.dto.Step;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("merge")
public class MergeSortService implements SortStrategy {

    @Override
    public List<Step> sort(int[] arr, boolean asc) {
        List<Step> steps = new ArrayList<>();
        int[] a = arr.clone();
        mergeSort(a, 0, a.length - 1, asc, steps);
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
        int n1 = mid - left + 1;
        int n2 = right - mid;

        int[] L = new int[n1];
        int[] R = new int[n2];

        System.arraycopy(a, left, L, 0, n1);
        System.arraycopy(a, mid + 1, R, 0, n2);

        int i = 0, j = 0, k = left;

        while (i < n1 && j < n2) {
            steps.add(new Step(a.clone(), left + i, mid + 1 + j, "compare"));
            if ((asc && L[i] <= R[j]) || (!asc && L[i] >= R[j])) {
                a[k] = L[i];
                i++;
            } else {
                a[k] = R[j];
                j++;
            }
            steps.add(new Step(a.clone(), k, k, "swap"));
            k++;
        }

        while (i < n1) {
            a[k] = L[i];
            steps.add(new Step(a.clone(), k, k, "swap"));
            i++; k++;
        }

        while (j < n2) {
            a[k] = R[j];
            steps.add(new Step(a.clone(), k, k, "swap"));
            j++; k++;
        }

        // Mark the merged range as sorted
        for (int s = left; s <= right; s++) {
            steps.add(new Step(a.clone(), s, s, "sorted"));
        }
    }
}
