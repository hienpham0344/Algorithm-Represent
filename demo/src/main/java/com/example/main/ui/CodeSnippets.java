package com.example.main.ui;

import java.util.Map;

public final class CodeSnippets {

    public static final Map<String, String> JAVA_CODE = Map.of(
            "bubble", """
                    public void bubbleSort(int[] a, boolean asc) {
                        int n = a.length;
                        for (int i = 0; i < n; i++) {
                            for (int j = 0; j < n - i - 1; j++) {
                                if (shouldSwap(a[j], a[j + 1], asc)) {
                                    int tmp = a[j];
                                    a[j] = a[j + 1];
                                    a[j + 1] = tmp;
                                }
                            }
                        }
                    }
                    """,
            "selection", """
                    public void selectionSort(int[] a, boolean asc) {
                        int n = a.length;
                        for (int i = 0; i < n; i++) {
                            int target = i;
                            for (int j = i + 1; j < n; j++) {
                                if (shouldSwap(a[j], a[target], asc)) {
                                    target = j;
                                }
                            }
                            if (target != i) {
                                int tmp = a[i];
                                a[i] = a[target];
                                a[target] = tmp;
                            }
                        }
                    }
                    """,
            "insertion", """
                    public void insertionSort(int[] a, boolean asc) {
                        for (int i = 1; i < a.length; i++) {
                            int key = a[i];
                            int j = i - 1;
                            while (j >= 0 && shouldSwap(a[j], key, asc)) {
                                a[j + 1] = a[j];
                                j--;
                            }
                            a[j + 1] = key;
                        }
                    }
                    """,
            "heap", """
                    public void heapSort(int[] a, boolean asc) {
                        for (int i = a.length / 2 - 1; i >= 0; i--) {
                            heapify(a, a.length, i, asc);
                        }
                        for (int i = a.length - 1; i > 0; i--) {
                            int tmp = a[0];
                            a[0] = a[i];
                            a[i] = tmp;
                            heapify(a, i, 0, asc);
                        }
                    }
                    """,
            "quick", """
                    public void quickSort(int[] a, int low, int high, boolean asc) {
                        if (low < high) {
                            int pivotIndex = partition(a, low, high, asc);
                            quickSort(a, low, pivotIndex - 1, asc);
                            quickSort(a, pivotIndex + 1, high, asc);
                        }
                    }
                    """,
            "merge", """
                    public void mergeSort(int[] a, int left, int right, boolean asc) {
                        if (left < right) {
                            int mid = (left + right) / 2;
                            mergeSort(a, left, mid, asc);
                            mergeSort(a, mid + 1, right, asc);
                            merge(a, left, mid, right, asc);
                        }
                    }
                    """
    );

    private CodeSnippets() {
    }
}
