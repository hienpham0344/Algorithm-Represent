package com.example.main.service;

import com.example.main.utils.*;
import com.example.main.dto.*;
import com.example.main.enums.*;

import com.example.main.entity.AlgorithmEntity;
import com.example.main.entity.CodeSnippetEntity;
import com.example.main.repository.AlgorithmRepository;
import com.example.main.repository.CodeSnippetRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class AlgorithmSeeder {
    private AlgorithmSeeder() {
    }

    public static void seedDefaults(AlgorithmRepository algorithmRepository,
                                    CodeSnippetRepository codeSnippetRepository) {
        seedAlgorithms(algorithmRepository);
        seedCodeSnippets(codeSnippetRepository);
    }

    // ── Algorithm metadata ───────────────────────────────────────────────
    private static void seedAlgorithms(AlgorithmRepository repository) {
        List<AlgorithmEntity> algorithms = List.of(
                algorithm("bubble", "Bubble Sort",
                        "Compare each adjacent pair and swap when out of order. After each pass, the largest or smallest element is pushed to the end of the unsorted region.",
                        "O(n²), best case O(n) with early-exit optimization",
                        "O(1)",
                        "In-place",
                        "Stable"),
                algorithm("selection", "Selection Sort",
                        "Find the best element in the unsorted region and move it to the start of that region.",
                        "O(n²)",
                        "O(1)",
                        "In-place",
                        "Not stable"),
                algorithm("insertion", "Insertion Sort",
                        "Take each element as a key, shift larger or smaller elements to the right, then insert the key into the correct position.",
                        "O(n²), best case O(n)",
                        "O(1)",
                        "In-place",
                        "Stable"),
                algorithm("heap", "Heap Sort",
                        "Build a heap, repeatedly move the priority element at the root to the end, and restore the heap property.",
                        "O(n log n)",
                        "O(log n) due to recursion",
                        "Not stable",
                        "Not stable"),
                algorithm("quick", "Quick Sort",
                        "Choose a pivot, partition the array into two regions around the pivot, then recursively sort each region.",
                        "Average O(n log n), worst case O(n²)",
                        "Average O(log n)",
                        "Not stable",
                        "Not stable"),
                algorithm("merge", "Merge Sort",
                        "Divide the array into halves, sort each half, then merge the two sorted sequences.",
                        "O(n log n)",
                        "O(n)",
                        "Stable",
                        "Stable")
        );
        algorithms.forEach(algo -> {
            if (!repository.existsByAlgorithmCodeIgnoreCase(algo.getAlgorithmCode())) {
                repository.save(algo);
            }
        });
    }

    private static AlgorithmEntity algorithm(
            String code, String name, String overview,
            String timeComplexity, String spaceComplexity,
            String memory, String stability
    ) {
        Map<String, Object> explanation = new LinkedHashMap<>();
        explanation.put("overview", overview);
        explanation.put("renderCode", Map.of(
                "title", name,
                "segments", List.of(
                        "Input overview",
                        "Step-by-step explanation",
                        "Complexity summary"
                )
        ));
        explanation.put("highlight", "Stored as JSON for flexible rendering in UI.");
        AlgorithmEntity entity = new AlgorithmEntity();
        entity.setAlgorithmCode(code);
        entity.setName(name);
        entity.setExplanation(explanation);
        entity.setTimeComplexity(timeComplexity);
        entity.setSpaceComplexity(spaceComplexity);
        entity.setMemory(memory);
        entity.setOverview(overview);
        entity.setStability(stability);
        entity.setCategory("sorting");
        entity.setStatus("active");
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        return entity;
    }

    // ── Code snippets ────────────────────────────────────────────────────
    private static void seedCodeSnippets(CodeSnippetRepository repository) {
        seedSnippet(repository, "bubble", bubbleLines());
        seedSnippet(repository, "selection", selectionLines());
        seedSnippet(repository, "insertion", insertionLines());
        seedSnippet(repository, "heap", heapLines());
        seedSnippet(repository, "quick", quickLines());
        seedSnippet(repository, "merge", mergeLines());
    }

    private static void seedSnippet(CodeSnippetRepository repository,
                                    String algorithmCode, List<String[]> lines) {
        if (repository.existsByAlgorithmCode(algorithmCode)) {
            return;
        }
        for (int i = 0; i < lines.size(); i++) {
            String[] pair = lines.get(i);
            String lineId = pair[0];
            String lineText = pair[1];
            repository.save(new CodeSnippetEntity(algorithmCode, i, lineId, lineText));
        }
    }

    // Each entry: { lineId (null for plain text), lineText }
    private static List<String[]> bubbleLines() {
        List<String[]> lines = new ArrayList<>();
        lines.add(new String[]{"bubble.method", "public void bubbleSort(int[] a, boolean asc) {"});
        lines.add(new String[]{null, "    int n = a.length;"});
        lines.add(new String[]{"bubble.outerLoop", "    for (int i = 0; i < n; i++) {"});
        lines.add(new String[]{"bubble.innerLoop", "        for (int j = 0; j < n - i - 1; j++) {"});
        lines.add(new String[]{"bubble.condition", "            if (shouldSwap(a[j], a[j + 1], asc)) {"});
        lines.add(new String[]{"bubble.swap", "                swap(a, j, j + 1);"});
        lines.add(new String[]{null, "            }"});
        lines.add(new String[]{null, "        }"});
        lines.add(new String[]{"bubble.markSorted", "        // a[n - i - 1] is now in its final position"});
        lines.add(new String[]{null, "    }"});
        lines.add(new String[]{"bubble.complete", "}"});
        return lines;
    }

    private static List<String[]> selectionLines() {
        List<String[]> lines = new ArrayList<>();
        lines.add(new String[]{"selection.method", "public void selectionSort(int[] a, boolean asc) {"});
        lines.add(new String[]{"selection.outerLoop", "    for (int i = 0; i < a.length; i++) {"});
        lines.add(new String[]{"selection.initTarget", "        int target = i;"});
        lines.add(new String[]{"selection.innerLoop", "        for (int j = i + 1; j < a.length; j++) {"});
        lines.add(new String[]{"selection.condition", "            if (shouldSwap(a[j], a[target], asc)) {"});
        lines.add(new String[]{"selection.updateTarget", "                target = j;"});
        lines.add(new String[]{null, "            }"});
        lines.add(new String[]{null, "        }"});
        lines.add(new String[]{"selection.swapCondition", "        if (target != i) {"});
        lines.add(new String[]{"selection.swap", "            swap(a, i, target);"});
        lines.add(new String[]{null, "        }"});
        lines.add(new String[]{"selection.markSorted", "        // a[i] is now in its final position"});
        lines.add(new String[]{null, "    }"});
        lines.add(new String[]{"selection.complete", "}"});
        return lines;
    }

    private static List<String[]> insertionLines() {
        List<String[]> lines = new ArrayList<>();
        lines.add(new String[]{"insertion.method", "public void insertionSort(int[] a, boolean asc) {"});
        lines.add(new String[]{"insertion.outerLoop", "    for (int i = 1; i < a.length; i++) {"});
        lines.add(new String[]{"insertion.key", "        int key = a[i];"});
        lines.add(new String[]{"insertion.initJ", "        int j = i - 1;"});
        lines.add(new String[]{"insertion.condition", "        while (j >= 0 && shouldShift(a[j], key, asc)) {"});
        lines.add(new String[]{"insertion.shift", "            a[j + 1] = a[j];"});
        lines.add(new String[]{"insertion.decrement", "            j--;"});
        lines.add(new String[]{null, "        }"});
        lines.add(new String[]{"insertion.insert", "        a[j + 1] = key;"});
        lines.add(new String[]{null, "    }"});
        lines.add(new String[]{"insertion.complete", "}"});
        return lines;
    }

    private static List<String[]> heapLines() {
        List<String[]> lines = new ArrayList<>();
        lines.add(new String[]{"heap.method", "public void heapSort(int[] a, boolean asc) {"});
        lines.add(new String[]{"heap.buildLoop", "    for (int i = a.length / 2 - 1; i >= 0; i--)"});
        lines.add(new String[]{"heap.buildCall", "        heapify(a, a.length, i, asc);"});
        lines.add(new String[]{"heap.extractLoop", "    for (int i = a.length - 1; i > 0; i--) {"});
        lines.add(new String[]{"heap.swapRoot", "        swap(a, 0, i);"});
        lines.add(new String[]{"heap.markSorted", "        // a[i] is now in its final position"});
        lines.add(new String[]{"heap.extractCall", "        heapify(a, i, 0, asc);"});
        lines.add(new String[]{null, "    }"});
        lines.add(new String[]{"heap.complete", "}"});
        lines.add(new String[]{null, ""});
        lines.add(new String[]{"heapify.method", "private void heapify(int[] a, int n, int root, boolean asc) {"});
        lines.add(new String[]{"heapify.initTarget", "    int target = root;"});
        lines.add(new String[]{"heapify.left", "    int left = 2 * root + 1;"});
        lines.add(new String[]{"heapify.right", "    int right = 2 * root + 2;"});
        lines.add(new String[]{"heapify.leftCondition", "    if (left < n && higherPriority(a[left], a[target], asc))"});
        lines.add(new String[]{"heapify.updateLeft", "        target = left;"});
        lines.add(new String[]{"heapify.rightCondition", "    if (right < n && higherPriority(a[right], a[target], asc))"});
        lines.add(new String[]{"heapify.updateRight", "        target = right;"});
        lines.add(new String[]{"heapify.swapCondition", "    if (target != root) {"});
        lines.add(new String[]{"heapify.swap", "        swap(a, root, target);"});
        lines.add(new String[]{"heapify.recurse", "        heapify(a, n, target, asc);"});
        lines.add(new String[]{null, "    }"});
        lines.add(new String[]{null, "}"});
        return lines;
    }

    private static List<String[]> quickLines() {
        List<String[]> lines = new ArrayList<>();
        lines.add(new String[]{"quick.method", "public void quickSort(int[] a, int low, int high, boolean asc) {"});
        lines.add(new String[]{"quick.condition", "    if (low < high) {"});
        lines.add(new String[]{"quick.partitionCall", "        int pivotIndex = partition(a, low, high, asc);"});
        lines.add(new String[]{"quick.leftCall", "        quickSort(a, low, pivotIndex - 1, asc);"});
        lines.add(new String[]{"quick.rightCall", "        quickSort(a, pivotIndex + 1, high, asc);"});
        lines.add(new String[]{null, "    }"});
        lines.add(new String[]{"quick.singleton", "    // A one-element range is already sorted"});
        lines.add(new String[]{"quick.complete", "}"});
        lines.add(new String[]{null, ""});
        lines.add(new String[]{"quick.partition.method", "private int partition(int[] a, int low, int high, boolean asc) {"});
        lines.add(new String[]{"quick.partition.pivot", "    int pivot = a[high];"});
        lines.add(new String[]{"quick.partition.initI", "    int i = low - 1;"});
        lines.add(new String[]{"quick.partition.loop", "    for (int j = low; j < high; j++) {"});
        lines.add(new String[]{"quick.partition.condition", "        if (belongsBeforePivot(a[j], pivot, asc)) {"});
        lines.add(new String[]{"quick.partition.incrementI", "            i++;"});
        lines.add(new String[]{"quick.partition.swapCondition", "            if (i != j)"});
        lines.add(new String[]{"quick.partition.swap", "                swap(a, i, j);"});
        lines.add(new String[]{null, "        }"});
        lines.add(new String[]{null, "    }"});
        lines.add(new String[]{"quick.partition.placePivot", "    swap(a, i + 1, high);"});
        lines.add(new String[]{"quick.partition.return", "    return i + 1;"});
        lines.add(new String[]{null, "}"});
        return lines;
    }

    private static List<String[]> mergeLines() {
        List<String[]> lines = new ArrayList<>();
        lines.add(new String[]{"merge.method", "public void mergeSort(int[] a, int left, int right, boolean asc) {"});
        lines.add(new String[]{"merge.condition", "    if (left < right) {"});
        lines.add(new String[]{"merge.mid", "        int mid = (left + right) / 2;"});
        lines.add(new String[]{"merge.leftCall", "        mergeSort(a, left, mid, asc);"});
        lines.add(new String[]{"merge.rightCall", "        mergeSort(a, mid + 1, right, asc);"});
        lines.add(new String[]{"merge.mergeCall", "        merge(a, left, mid, right, asc);"});
        lines.add(new String[]{null, "    }"});
        lines.add(new String[]{"merge.complete", "}"});
        lines.add(new String[]{null, ""});
        lines.add(new String[]{"merge.work.method", "private void merge(int[] a, int left, int mid, int right, boolean asc) {"});
        lines.add(new String[]{"merge.work.copy", "    int[] L = copyLeft(a); int[] R = copyRight(a);"});
        lines.add(new String[]{"merge.work.init", "    int i = 0, j = 0, k = left;"});
        lines.add(new String[]{"merge.work.condition", "    while (i < L.length && j < R.length) {"});
        lines.add(new String[]{"merge.work.chooseLeft", "        if (comesFirst(L[i], R[j], asc))"});
        lines.add(new String[]{"merge.work.writeLeft", "            a[k++] = L[i++];"});
        lines.add(new String[]{"merge.work.chooseRight", "        else"});
        lines.add(new String[]{"merge.work.writeRight", "            a[k++] = R[j++];"});
        lines.add(new String[]{null, "    }"});
        lines.add(new String[]{"merge.work.leftRemainder", "    while (i < L.length)"});
        lines.add(new String[]{"merge.work.writeLeftRemainder", "        a[k++] = L[i++];"});
        lines.add(new String[]{"merge.work.rightRemainder", "    while (j < R.length)"});
        lines.add(new String[]{"merge.work.writeRightRemainder", "        a[k++] = R[j++];"});
        lines.add(new String[]{null, "}"});
        return lines;
    }
}

