package com.example.main.ui;

import java.util.List;
import java.util.Map;

import static com.example.main.ui.CodeLine.line;
import static com.example.main.ui.CodeLine.text;

public final class CodeSnippets {

    private static final Map<String, CodeSnippet> SNIPPETS = Map.of(
            "bubble", snippet(
                    line("bubble.method", "public void bubbleSort(int[] a, boolean asc) {"),
                    text("    int n = a.length;"),
                    line("bubble.outerLoop", "    for (int i = 0; i < n; i++) {"),
                    line("bubble.innerLoop", "        for (int j = 0; j < n - i - 1; j++) {"),
                    line("bubble.condition", "            if (shouldSwap(a[j], a[j + 1], asc)) {"),
                    line("bubble.swap", "                swap(a, j, j + 1);"),
                    text("            }"),
                    text("        }"),
                    line("bubble.markSorted", "        // a[n - i - 1] is now in its final position"),
                    text("    }"),
                    line("bubble.complete", "}")),
            "selection", snippet(
                    line("selection.method", "public void selectionSort(int[] a, boolean asc) {"),
                    line("selection.outerLoop", "    for (int i = 0; i < a.length; i++) {"),
                    line("selection.initTarget", "        int target = i;"),
                    line("selection.innerLoop", "        for (int j = i + 1; j < a.length; j++) {"),
                    line("selection.condition", "            if (shouldSwap(a[j], a[target], asc)) {"),
                    line("selection.updateTarget", "                target = j;"),
                    text("            }"),
                    text("        }"),
                    line("selection.swapCondition", "        if (target != i) {"),
                    line("selection.swap", "            swap(a, i, target);"),
                    text("        }"),
                    line("selection.markSorted", "        // a[i] is now in its final position"),
                    text("    }"),
                    line("selection.complete", "}")),
            "insertion", snippet(
                    line("insertion.method", "public void insertionSort(int[] a, boolean asc) {"),
                    line("insertion.outerLoop", "    for (int i = 1; i < a.length; i++) {"),
                    line("insertion.key", "        int key = a[i];"),
                    line("insertion.initJ", "        int j = i - 1;"),
                    line("insertion.condition", "        while (j >= 0 && shouldShift(a[j], key, asc)) {"),
                    line("insertion.shift", "            a[j + 1] = a[j];"),
                    line("insertion.decrement", "            j--;"),
                    text("        }"),
                    line("insertion.insert", "        a[j + 1] = key;"),
                    text("    }"),
                    line("insertion.complete", "}")),
            "heap", snippet(
                    line("heap.method", "public void heapSort(int[] a, boolean asc) {"),
                    line("heap.buildLoop", "    for (int i = a.length / 2 - 1; i >= 0; i--)"),
                    line("heap.buildCall", "        heapify(a, a.length, i, asc);"),
                    line("heap.extractLoop", "    for (int i = a.length - 1; i > 0; i--) {"),
                    line("heap.swapRoot", "        swap(a, 0, i);"),
                    line("heap.markSorted", "        // a[i] is now in its final position"),
                    line("heap.extractCall", "        heapify(a, i, 0, asc);"),
                    text("    }"),
                    line("heap.complete", "}"),
                    text(""),
                    line("heapify.method", "private void heapify(int[] a, int n, int root, boolean asc) {"),
                    line("heapify.initTarget", "    int target = root;"),
                    line("heapify.left", "    int left = 2 * root + 1;"),
                    line("heapify.right", "    int right = 2 * root + 2;"),
                    line("heapify.leftCondition", "    if (left < n && higherPriority(a[left], a[target], asc))"),
                    line("heapify.updateLeft", "        target = left;"),
                    line("heapify.rightCondition", "    if (right < n && higherPriority(a[right], a[target], asc))"),
                    line("heapify.updateRight", "        target = right;"),
                    line("heapify.swapCondition", "    if (target != root) {"),
                    line("heapify.swap", "        swap(a, root, target);"),
                    line("heapify.recurse", "        heapify(a, n, target, asc);"),
                    text("    }"),
                    text("}")),
            "quick", snippet(
                    line("quick.method", "public void quickSort(int[] a, int low, int high, boolean asc) {"),
                    line("quick.condition", "    if (low < high) {"),
                    line("quick.partitionCall", "        int pivotIndex = partition(a, low, high, asc);"),
                    line("quick.leftCall", "        quickSort(a, low, pivotIndex - 1, asc);"),
                    line("quick.rightCall", "        quickSort(a, pivotIndex + 1, high, asc);"),
                    text("    }"),
                    line("quick.singleton", "    // A one-element range is already sorted"),
                    line("quick.complete", "}"),
                    text(""),
                    line("quick.partition.method", "private int partition(int[] a, int low, int high, boolean asc) {"),
                    line("quick.partition.pivot", "    int pivot = a[high];"),
                    line("quick.partition.initI", "    int i = low - 1;"),
                    line("quick.partition.loop", "    for (int j = low; j < high; j++) {"),
                    line("quick.partition.condition", "        if (belongsBeforePivot(a[j], pivot, asc)) {"),
                    line("quick.partition.incrementI", "            i++;"),
                    line("quick.partition.swapCondition", "            if (i != j)"),
                    line("quick.partition.swap", "                swap(a, i, j);"),
                    text("        }"),
                    text("    }"),
                    line("quick.partition.placePivot", "    swap(a, i + 1, high);"),
                    line("quick.partition.return", "    return i + 1;"),
                    text("}")),
            "merge", snippet(
                    line("merge.method", "public void mergeSort(int[] a, int left, int right, boolean asc) {"),
                    line("merge.condition", "    if (left < right) {"),
                    line("merge.mid", "        int mid = (left + right) / 2;"),
                    line("merge.leftCall", "        mergeSort(a, left, mid, asc);"),
                    line("merge.rightCall", "        mergeSort(a, mid + 1, right, asc);"),
                    line("merge.mergeCall", "        merge(a, left, mid, right, asc);"),
                    text("    }"),
                    line("merge.complete", "}"),
                    text(""),
                    line("merge.work.method", "private void merge(int[] a, int left, int mid, int right, boolean asc) {"),
                    line("merge.work.copy", "    int[] L = copyLeft(a); int[] R = copyRight(a);"),
                    line("merge.work.init", "    int i = 0, j = 0, k = left;"),
                    line("merge.work.condition", "    while (i < L.length && j < R.length) {"),
                    line("merge.work.chooseLeft", "        if (comesFirst(L[i], R[j], asc))"),
                    line("merge.work.writeLeft", "            a[k++] = L[i++];"),
                    line("merge.work.chooseRight", "        else"),
                    line("merge.work.writeRight", "            a[k++] = R[j++];"),
                    text("    }"),
                    line("merge.work.leftRemainder", "    while (i < L.length)"),
                    line("merge.work.writeLeftRemainder", "        a[k++] = L[i++];"),
                    line("merge.work.rightRemainder", "    while (j < R.length)"),
                    line("merge.work.writeRightRemainder", "        a[k++] = R[j++];"),
                    text("}"))
    );

    private CodeSnippets() {
    }

    public static CodeSnippet get(String algorithm) {
        return SNIPPETS.getOrDefault(algorithm, new CodeSnippet(List.of(text("// Không có code"))));
    }

    public static boolean containsLine(String algorithm, String lineId) {
        return lineIndex(algorithm, lineId) >= 0;
    }

    public static int lineIndex(String algorithm, String lineId) {
        if (lineId == null) {
            return -1;
        }
        List<CodeLine> lines = get(algorithm).lines();
        for (int index = 0; index < lines.size(); index++) {
            if (lineId.equals(lines.get(index).id())) {
                return index;
            }
        }
        return -1;
    }

    private static CodeSnippet snippet(CodeLine... lines) {
        return new CodeSnippet(List.of(lines));
    }
}
