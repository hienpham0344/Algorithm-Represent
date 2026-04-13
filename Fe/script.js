/* ════════════════════════════════════════════
   SORTING ALGORITHM VISUALIZER — script.js
   Frontend calls Spring Boot API at /api/sort/{type}
════════════════════════════════════════════ */

const API_BASE = 'http://localhost:8080/api/sort';

// ── DOM refs (matching the user's HTML) ──────
const selAlgorithm = document.getElementById('ChonThuatToan');
const selDataMode = document.getElementById('taoDuLieu');
const rangeSpeed = document.getElementById('rangeSpeed');
const rangeSize = document.getElementById('rangeSize');
const sizeLabel = document.getElementById('size-label');
const vizContainer = document.getElementById('viz-container');
const manualInputs = document.getElementById('manual-inputs');
const codePanel = document.getElementById('code-panel');
const swapCountInput = document.getElementById('swapCount');

const btnSort = document.getElementById('btn-sort');
const btnStop = document.getElementById('btn-stop');
const btnReset = document.getElementById('btn-reset');
const btnAsc = document.getElementById('btn-asc');
const btnDesc = document.getElementById('btn-desc');
const btnDelete = document.getElementById('btn-delete');
const btnCreate = document.getElementById('btn-create');

// ── State ─────────────────────────────────────
let originalArray = [];
let currentArray = [];
let steps = [];
let stepIndex = 0;
let animTimer = null;
let isRunning = false;
let isPaused = false;
let swapCount = 0;
let sortedIndices = new Set();

// ── Java code snippets (per algorithm) ─────────
const JAVA_CODE = {
    bubble: `public void bubbleSort(int[] a, boolean asc) {
    int n = a.length;
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n - i - 1; j++) {
            if (shouldSwap(a[j], a[j+1], asc)) {
                int tmp = a[j];
                a[j] = a[j+1];
                a[j+1] = tmp;
            }
        }
    }
}`,

    selection: `public void selectionSort(int[] a, boolean asc) {
    int n = a.length;
    for (int i = 0; i < n; i++) {
        int target = i;
        for (int j = i+1; j < n; j++) {
            if (shouldSwap(a[j], a[target], asc))
                target = j;
        }
        if (target != i) {
            int tmp = a[i];
            a[i] = a[target];
            a[target] = tmp;
        }
    }
}`,

    insertion: `public void insertionSort(int[] a, boolean asc) {
    int n = a.length;
    for (int i = 1; i < n; i++) {
        int key = a[i];
        int j = i - 1;
        while (j >= 0 && shouldSwap(a[j], key, asc)) {
            a[j+1] = a[j];
            j--;
        }
        a[j+1] = key;
    }
}`,

    heap: `public void heapSort(int[] a, boolean asc) {
    int n = a.length;
    for (int i = n/2 - 1; i >= 0; i--)
        heapify(a, n, i, asc);
    for (int i = n-1; i > 0; i--) {
        int tmp = a[0];
        a[0] = a[i];
        a[i] = tmp;
        heapify(a, i, 0, asc);
    }
}

private void heapify(int[] a, int n, int root, boolean asc) {
    int target = root;
    int l = 2*root+1, r = 2*root+2;
    if (l < n && shouldSwap(a[target], a[l], asc)) target = l;
    if (r < n && shouldSwap(a[target], a[r], asc)) target = r;
    if (target != root) {
        int tmp = a[root];
        a[root] = a[target];
        a[target] = tmp;
        heapify(a, n, target, asc);
    }
}`,

    quick: `public void quickSort(int[] a, int lo, int hi, boolean asc) {
    if (lo < hi) {
        int pi = partition(a, lo, hi, asc);
        quickSort(a, lo, pi - 1, asc);
        quickSort(a, pi + 1, hi, asc);
    }
}

private int partition(int[] a, int lo, int hi, boolean asc) {
    int pivot = a[hi];
    int i = lo - 1;
    for (int j = lo; j < hi; j++) {
        if (!shouldSwap(a[j], pivot, asc)) {
            i++;
            int tmp = a[i];
            a[i] = a[j];
            a[j] = tmp;
        }
    }
    int tmp = a[i+1];
    a[i+1] = a[hi];
    a[hi] = tmp;
    return i + 1;
}`,

    merge: `public void mergeSort(int[] a, int l, int r, boolean asc) {
    if (l < r) {
        int m = (l + r) / 2;
        mergeSort(a, l, m, asc);
        mergeSort(a, m+1, r, asc);
        merge(a, l, m, r, asc);
    }
}

private void merge(int[] a, int l, int m, int r, boolean asc) {
    int[] L = Arrays.copyOfRange(a, l, m+1);
    int[] R = Arrays.copyOfRange(a, m+1, r+1);
    int i = 0, j = 0, k = l;
    while (i < L.length && j < R.length) {
        if (!shouldSwap(L[i], R[j], asc))
            a[k++] = L[i++];
        else
            a[k++] = R[j++];
    }
    while (i < L.length) a[k++] = L[i++];
    while (j < R.length) a[k++] = R[j++];
}`,
};

// ── Speed mapping (delay in ms per step) ──────
function getDelay() {
    const v = parseFloat(rangeSpeed.value);
    // Lower value = slower. Map: 0.5→1200, 1→600, 1.5→350, 2→200, 2.5→100, 3→30
    if (v <= 0.5) return 1200;
    if (v <= 1.0) return 600;
    if (v <= 1.5) return 350;
    if (v <= 2.0) return 200;
    if (v <= 2.5) return 100;
    return 30;
}

// ── Array helpers ──────────────────────────────
function randomInt(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

function generateArray(size) {
    const arr = [];
    for (let i = 0; i < size; i++) arr.push(randomInt(10, 280));
    return arr;
}

function readManualArray() {
    return Array.from(manualInputs.querySelectorAll('.manual-input'))
        .map(el => parseInt(el.value) || randomInt(10, 280));
}

// ── Render bars ────────────────────────────────
function renderBars(arr, highlights = {}) {
    vizContainer.innerHTML = '';
    const max = Math.max(...arr, 1);

    arr.forEach((val, idx) => {
        const wrapper = document.createElement('div');
        wrapper.className = 'bar-wrapper';
        wrapper.style.position = 'relative';

        const bar = document.createElement('div');
        bar.className = 'bar';
        bar.style.height = `${(val / max) * 100}%`;

        // apply state class
        if (sortedIndices.has(idx)) {
            bar.classList.add('bar--sorted');
        } else if (highlights[idx] === 'swap') {
            bar.classList.add('bar--swap');
        } else if (highlights[idx] === 'compare') {
            bar.classList.add('bar--compare');
        }

        const label = document.createElement('span');
        label.className = 'bar__label';
        label.textContent = val;

        wrapper.appendChild(bar);
        wrapper.appendChild(label);
        vizContainer.appendChild(wrapper);
    });
}

// ── Manual input slots ─────────────────────────
function renderManualInputs(size) {
    manualInputs.innerHTML = '';
    for (let i = 0; i < size; i++) {
        const inp = document.createElement('input');
        inp.type = 'number';
        inp.className = 'manual-input';
        inp.min = 1; inp.max = 999;
        inp.placeholder = `#${i + 1}`;
        inp.value = randomInt(10, 280);
        manualInputs.appendChild(inp);
    }
}

// ── Code panel rendering ───────────────────────
function renderCode(algorithmKey, activeLine = -1) {
    const code = JAVA_CODE[algorithmKey] ?? '// Select an algorithm';
    const lines = code.split('\n');
    codePanel.innerHTML = '';
    lines.forEach((line, idx) => {
        const span = document.createElement('span');
        span.className = 'code-line' + (idx === activeLine ? ' code-line--active' : '');

        let html = line.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
        html = html
            .replace(/\b(public|private|void|int|boolean|for|if|while|return)\b/g, '<span class="kw">$1</span>')
            .replace(/\b(shouldSwap|swap|heapify|partition|merge|mergeSort|quickSort|heapSort|insertionSort|selectionSort|bubbleSort)\b(?=\s*\()/g, '<span class="fn">$1</span>')
            .replace(/(\/\/.*)$/g, '<span class="com">$1</span>');

        span.innerHTML = html;
        codePanel.appendChild(span);
    });
}

const ALGO_LINE_MAP = {
    bubble: { compare: 4, swap: 6, sorted: 2 },
    selection: { compare: 5, swap: 10, sorted: 2 },
    insertion: { compare: 5, swap: 6, sorted: 2 },
    heap: { compare: 15, swap: 19, sorted: 4 },
    quick: { compare: 12, swap: 15, sorted: 3 },
    merge: { compare: 14, swap: 15, sorted: 2 }
};

// Exact heuristic: map step object directly to Java code lines
function getActiveLine(algorithm, step) {
    if (!step) return -1;
    const map = ALGO_LINE_MAP[algorithm];
    if (!map) return -1;
    return map[step.type] ?? -1;
}

// ── Update swap count display ──────────────────
function updateSwapCount() {
    swapCountInput.value = swapCount;
}

// ── API call ───────────────────────────────────
async function fetchSteps(algorithm, array, asc) {
    vizContainer.style.opacity = '0.4';
    try {
        const resp = await fetch(`${API_BASE}/${algorithm}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ array, asc }),
        });
        if (!resp.ok) {
            const msg = await resp.text();
            throw new Error(msg || `HTTP ${resp.status}`);
        }
        return await resp.json();
    } catch (err) {
        alert('API error: ' + err.message);
        return null;
    } finally {
        vizContainer.style.opacity = '1';
    }
}

// ── Animation engine ───────────────────────────
function applyStep(idx) {
    if (idx >= steps.length) {
        finishAnimation();
        return;
    }

    const step = steps[idx];
    const arr = step.array;
    currentArray = [...arr];

    // update stats
    if (step.type === 'swap') {
        swapCount++;
        updateSwapCount();
    } else if (step.type === 'sorted') {
        sortedIndices.add(step.i);
        sortedIndices.add(step.j);
    }

    // highlights
    const hl = {};
    if (step.type !== 'sorted') {
        hl[step.i] = step.type;
        hl[step.j] = step.type;
    }

    renderBars(arr, hl);

    const activeLine = getActiveLine(selAlgorithm.value, step);
    renderCode(selAlgorithm.value, activeLine);
}

function setUIState(sorting, paused = false) {
    btnSort.disabled = sorting;
    btnAsc.disabled = sorting;
    btnDesc.disabled = sorting;
    btnReset.disabled = sorting && !paused;
    btnDelete.disabled = sorting;
    btnCreate.disabled = sorting;
    selAlgorithm.disabled = sorting;
    selDataMode.disabled = sorting;
    rangeSize.disabled = sorting;
}

function startAnimation() {
    if (stepIndex >= steps.length) { finishAnimation(); return; }
    isRunning = true;
    isPaused = false;
    setUIState(true, false);
    btnStop.disabled = false;
    btnStop.textContent = 'Stop';
    btnStop.style.backgroundColor = '#e74c3c'; // red

    function tick() {
        if (!isRunning || isPaused) return;
        if (stepIndex >= steps.length) { finishAnimation(); return; }
        applyStep(stepIndex++);
        animTimer = setTimeout(tick, getDelay());
    }
    tick();
}

function finishAnimation() {
    isRunning = false;
    isPaused = false;
    setUIState(false);
    btnStop.disabled = true;
    btnStop.textContent = 'Stop';
    btnStop.style.backgroundColor = '';
    clearTimeout(animTimer);
    // Mark all as sorted
    if (currentArray.length > 0) {
        currentArray.forEach((_, i) => sortedIndices.add(i));
        renderBars(currentArray, {});
    }
    renderCode(selAlgorithm.value, -1);
}

function resetToOriginal() {
    clearTimeout(animTimer);
    isRunning = false;
    isPaused = false;
    setUIState(false);
    btnStop.disabled = true;
    btnStop.textContent = 'Stop';
    btnStop.style.backgroundColor = '';
    steps = [];
    stepIndex = 0;
    swapCount = 0;
    sortedIndices.clear();
    updateSwapCount();
    currentArray = [...originalArray];
    renderBars(currentArray, {});
    renderCode(selAlgorithm.value, -1);
}

// ── Event handlers ─────────────────────────────

// Sort button — start animated sort (ascending by default)
btnSort.addEventListener('click', async () => {
    if (isRunning) return;
    const arr = selDataMode.value === 'manual' ? readManualArray() : [...currentArray];
    if (arr.length === 0) { alert('Mảng trống! Hãy bấm Create trước.'); return; }
    originalArray = [...arr];
    currentArray = [...arr];
    sortedIndices.clear();
    swapCount = 0; stepIndex = 0;
    updateSwapCount();

    const algo = selAlgorithm.value;

    const fetched = await fetchSteps(algo, arr, true);
    if (!fetched) return;

    steps = fetched;
    renderCode(algo, -1);
    startAnimation();
});

// Stop (pause/resume)
btnStop.addEventListener('click', () => {
    if (isRunning && !isPaused) {
        isPaused = true;
        setUIState(true, true);
        btnStop.textContent = 'Continue';
        btnStop.style.backgroundColor = '#2ecc71'; // green
        clearTimeout(animTimer);
    } else if (isPaused) {
        isPaused = false;
        setUIState(true, false);
        btnStop.textContent = 'Stop';
        btnStop.style.backgroundColor = '#e74c3c'; // red
        startAnimation();
    }
});

// Reset
btnReset.addEventListener('click', () => resetToOriginal());

// Tăng dần (Ascending) — sort immediately ascending
btnAsc.addEventListener('click', async () => {
    if (isRunning) return;
    if (currentArray.length === 0) { alert('Mảng trống!'); return; }
    clearTimeout(animTimer);
    isRunning = false;
    isPaused = false;
    swapCount = 0;
    sortedIndices.clear();
    stepIndex = 0;
    updateSwapCount();

    const arr = selDataMode.value === 'manual' ? readManualArray() : [...currentArray];
    originalArray = [...arr];
    currentArray = [...arr];

    const fetched = await fetchSteps(selAlgorithm.value, arr, true);
    if (!fetched) return;

    steps = fetched;
    renderCode(selAlgorithm.value, -1);
    startAnimation();
});

// Giảm dần (Descending) — sort immediately descending
btnDesc.addEventListener('click', async () => {
    if (isRunning) return;
    if (currentArray.length === 0) { alert('Mảng trống!'); return; }
    clearTimeout(animTimer);
    isRunning = false;
    isPaused = false;
    swapCount = 0;
    sortedIndices.clear();
    stepIndex = 0;
    updateSwapCount();

    const arr = selDataMode.value === 'manual' ? readManualArray() : [...currentArray];
    originalArray = [...arr];
    currentArray = [...arr];

    const fetched = await fetchSteps(selAlgorithm.value, arr, false);
    if (!fetched) return;

    steps = fetched;
    renderCode(selAlgorithm.value, -1);
    startAnimation();
});

// Delete array
btnDelete.addEventListener('click', () => {
    if (isRunning) return;
    clearTimeout(animTimer); isRunning = false; isPaused = false;
    btnStop.disabled = true;
    btnStop.style.backgroundColor = '';
    btnStop.textContent = 'Stop';
    steps = []; originalArray = []; currentArray = [];
    sortedIndices.clear();
    swapCount = 0; stepIndex = 0;
    updateSwapCount();
    vizContainer.innerHTML = '';
});

// Create array
btnCreate.addEventListener('click', () => {
    if (isRunning) return;
    clearTimeout(animTimer); isRunning = false; isPaused = false;
    btnStop.disabled = true;
    btnStop.style.backgroundColor = '';
    btnStop.textContent = 'Stop';
    const size = parseInt(rangeSize.value);
    if (selDataMode.value === 'manual') {
        renderManualInputs(size);
        manualInputs.style.display = 'flex';
        return;
    }
    manualInputs.style.display = 'none';
    originalArray = generateArray(size);
    currentArray = [...originalArray];
    sortedIndices.clear();
    swapCount = 0; stepIndex = 0;
    steps = [];
    updateSwapCount();
    renderBars(currentArray, {});
});

// Algorithm change → update code panel
selAlgorithm.addEventListener('change', () => {
    renderCode(selAlgorithm.value, -1);
});

// Data mode change
selDataMode.addEventListener('change', () => {
    const isManual = selDataMode.value === 'manual';
    manualInputs.style.display = isManual ? 'flex' : 'none';
    if (isManual) renderManualInputs(parseInt(rangeSize.value));
});

// Size slider label
rangeSize.addEventListener('input', () => {
    sizeLabel.textContent = rangeSize.value;
});

// ── Init ───────────────────────────────────────
(function init() {
    btnStop.disabled = true;
    renderCode(selAlgorithm.value, -1);
    // Removed initial array generation per user request
    originalArray = [];
    currentArray = [];
    updateSwapCount();
    sizeLabel.textContent = rangeSize.value;
})();
