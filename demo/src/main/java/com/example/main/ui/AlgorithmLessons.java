package com.example.main.ui;

import java.util.Map;

public final class AlgorithmLessons {
    private static final Map<String, AlgorithmLesson> LESSONS = Map.of(
            "bubble", new AlgorithmLesson(
                    "Compare each adjacent pair and swap when out of order. After each pass, the largest or smallest element is pushed to the end of the unsorted region.",
                    "O(n²), best case O(n) with early-exit optimization",
                    "O(1)",
                    "Stable"),
            "selection", new AlgorithmLesson(
                    "Find the best element in the unsorted region and move it to the start of that region.",
                    "O(n²)",
                    "O(1)",
                    "Not stable"),
            "insertion", new AlgorithmLesson(
                    "Take each element as a key, shift larger or smaller elements to the right, then insert the key into the correct position.",
                    "O(n²), best case O(n)",
                    "O(1)",
                    "Stable"),
            "heap", new AlgorithmLesson(
                    "Build a heap, repeatedly move the priority element at the root to the end, and restore the heap property.",
                    "O(n log n)",
                    "O(log n) due to recursion",
                    "Not stable"),
            "quick", new AlgorithmLesson(
                    "Choose a pivot, partition the array into two regions around the pivot, then recursively sort each region.",
                    "Average O(n log n), worst case O(n²)",
                    "Average O(log n)",
                    "Not stable"),
            "merge", new AlgorithmLesson(
                    "Divide the array into halves, sort each half, then merge the two sorted sequences.",
                    "O(n log n)",
                    "O(n)",
                    "Stable")
    );

    private AlgorithmLessons() {
    }

    public static AlgorithmLesson get(String algorithm) {
        return LESSONS.getOrDefault(algorithm,
                new AlgorithmLesson("No description available.", "-", "-", "-"));
    }
}
