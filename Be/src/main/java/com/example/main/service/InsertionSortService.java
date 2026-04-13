package com.example.main.service;

import com.example.main.dto.Step;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("insertion")
public class InsertionSortService implements SortStrategy {

    @Override
    public List<Step> sort(int[] arr, boolean asc) {
        List<Step> steps = new ArrayList<>();
        int[] a = arr.clone();

        for (int i = 1; i < a.length; i++) {
            int key = a[i];
            int j = i - 1;

            while ((asc && j >= 0 && a[j] > key) || (!asc && j >= 0 && a[j] < key)) {
                steps.add(new Step(a.clone(), j, j + 1, "compare"));
                a[j + 1] = a[j];
                j--;
            }

            a[j + 1] = key;

            // save step after inserting
            steps.add(new Step(a.clone(), j + 1, i, "swap"));
            steps.add(new Step(a.clone(), i, i, "sorted"));
        }

        return steps;
    }
}