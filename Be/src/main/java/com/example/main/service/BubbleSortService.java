package com.example.main.service;


import com.example.main.dto.Step;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("bubble")
public class BubbleSortService implements SortStrategy {

    @Override
    public List<Step> sort(int[] arr, boolean asc) {
        List<Step> steps = new ArrayList<>();
        int[] a = arr.clone();

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length - i - 1; j++) {
                steps.add(new Step(a.clone(), j, j + 1, "compare"));
                if ((asc && a[j] > a[j + 1]) || (!asc && a[j] < a[j + 1])) {
                    int temp = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = temp;
                    steps.add(new Step(a.clone(), j, j + 1, "swap"));
                }
            }
            steps.add(new Step(a.clone(), a.length - i - 1, a.length - i - 1, "sorted"));
        }
        return steps;
    }
}