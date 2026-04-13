package com.example.main.service;

import com.example.main.dto.Step;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("selection")
public class SelectionSortService implements SortStrategy {

    @Override
    public List<Step> sort(int[] arr, boolean asc) {
        List<Step> steps = new ArrayList<>();
        int[] a = arr.clone();

        for (int i = 0; i < a.length; i++) {
            int min = i;

            for (int j = i + 1; j < a.length; j++) {
                steps.add(new Step(a.clone(), min, j, "compare"));
                if ((asc && a[j] < a[min]) || (!asc && a[j] > a[min])) {
                    min = j;
                }
            }

            if (min != i) {
                int temp = a[i];
                a[i] = a[min];
                a[min] = temp;

                steps.add(new Step(a.clone(), i, min, "swap"));
            }
            steps.add(new Step(a.clone(), i, i, "sorted"));
        }

        return steps;
    }
}