package com.example.main.ui;

import com.example.main.dto.Step;
import com.example.main.service.SortStrategy;
import com.example.main.service.SortingRegistry;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;


public class SortVisualizerView extends VBox {

    // ── Algorithm display names ──────────────────────────────────────────
    private static final Map<String, String> ALGORITHM_NAMES = Map.of(
            "selection", "Selection Sort",
            "bubble",    "Bubble Sort",
            "insertion", "Insertion Sort",
            "heap",      "Heap Sort",
            "quick",     "Quick Sort",
            "merge",     "Merge Sort"
    );

    // ── Bar colours ──────────────────────────────────────────────────────
    private static final Color COL_DEFAULT = Color.web("#3b82f6");
    private static final Color COL_COMPARE = Color.web("#f59e0b");
    private static final Color COL_SWAP    = Color.web("#ef4444");
    private static final Color COL_SORTED  = Color.web("#10b981");

    // ── State ────────────────────────────────────────────────────────────
    private final Map<String, SortStrategy> strategies = SortingRegistry.createStrategies();
    private final Random random = new Random();

    // Controls
    private final ComboBox<String> algorithmBox = new ComboBox<>();
    private final ComboBox<String> dataSourceBox = new ComboBox<>();
    private final Slider  sizeSlider   = new Slider(2, 15, 8);
    private final Label   sizeRangeLabel = new Label("2 -> 15");
    private final Slider  speedSlider  = new Slider(0.5, 3.0, 1.0);
    private final TextArea manualInput = new TextArea("42, 17, 88, 6, 31, 59, 12, 75");
    private final Label   swapValueLabel = new Label("0");
    private final Label   statusLabel  = new Label("Creating an array to get started.");

    // Buttons
    private final Button sortButton   = new Button("Sort");
    private final Button tangDanBtn   = new Button("Ascending");
    private final Button giamDanBtn   = new Button("Descending");
    private final Button stopButton   = new Button("Stop");
    private final Button resetButton  = new Button("Reset");
    private final Button deleteButton = new Button("Delete");
    private final Button createButton = new Button("Create Array");

    // Panels
    private final HBox  chartPane = new HBox();
    private final VBox  codePane  = new VBox(2);

    // Runtime state
    private Timeline   timeline;
    private int[]      originalArray = new int[0];
    private int[]      currentArray  = new int[0];
    private List<Step> steps         = List.of();
    private int        stepIndex;
    private int        swapCount;
    private boolean    paused;
    private final Set<Integer> sortedIndices = new LinkedHashSet<>();

    // ====================================================================
    public SortVisualizerView() {
        getStyleClass().add("app-root");
        setPadding(new Insets(20, 22, 20, 22));
        setSpacing(14);

        // Title bar
        Label title = new Label("SORTING ALGORITHM VISUALIZER");
        title.getStyleClass().add("page-title");

        // ROW 1: Algorithm | Data source | Array size
        HBox row1 = buildRow1();

        // ROW 2: Buttons | Swap count | Speed
        HBox row2 = buildRow2();

        // ROW 3 (main): Chart (left, grows) + Code panel (right, fixed)
        HBox row3 = buildRow3();
        VBox.setVgrow(row3, Priority.ALWAYS);

        getChildren().addAll(title, row1, row2, row3);

        // Wire up
        applyButtonStyles();
        bindEvents();
        renderCode();
        refreshChart(currentArray, -1, -1, null);
        updateButtons(false);
    }

    // ── ROW 1 ─────────────────────────────────────────────────────────────
    private HBox buildRow1() {
        // Algorithm picker
        algorithmBox.getItems().addAll(strategies.keySet());
        algorithmBox.setCellFactory(ignored -> new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : ALGORITHM_NAMES.getOrDefault(item, item));
            }
        });
        algorithmBox.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : ALGORITHM_NAMES.getOrDefault(item, item));
            }
        });
        algorithmBox.getSelectionModel().select("selection");
        algorithmBox.setMaxWidth(Double.MAX_VALUE);

        VBox algoCard = labeledCard("Select a sorting algorithm", algorithmBox);
        HBox.setHgrow(algoCard, Priority.ALWAYS);

        // Data source picker
        dataSourceBox.getItems().addAll("Random", "Manual Entry");
        dataSourceBox.getSelectionModel().select(0);
        dataSourceBox.setMaxWidth(Double.MAX_VALUE);
        manualInput.setPrefRowCount(2);
        manualInput.setWrapText(true);
        manualInput.setPromptText("VD: 5, 1, 9, 3");
        manualInput.setVisible(false);
        manualInput.setManaged(false);
        VBox dataSourceCard = labeledCard("Generate array data", new VBox(8, dataSourceBox, manualInput));
        HBox.setHgrow(dataSourceCard, Priority.ALWAYS);

        // Array size
        sizeSlider.setMajorTickUnit(1);
        sizeSlider.setMinorTickCount(0);
        sizeSlider.setSnapToTicks(true);
        sizeSlider.setMaxWidth(Double.MAX_VALUE);
        HBox sizeHeader = new HBox();
        Label sizeTitle = new Label("ArraySize");
        sizeTitle.getStyleClass().add("card-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        sizeRangeLabel.getStyleClass().add("status-text");
        sizeHeader.getChildren().addAll(sizeTitle, spacer, sizeRangeLabel);

        VBox sizeContent = new VBox(10, sizeHeader, sizeSlider);
        VBox sizeCard = new VBox(sizeContent);
        sizeCard.getStyleClass().add("card");
        sizeCard.setPadding(new Insets(14, 16, 14, 16));
        sizeCard.setPrefWidth(280);
        sizeCard.setMinWidth(240);

        HBox row = new HBox(12, algoCard, dataSourceCard, sizeCard);
        row.setAlignment(Pos.TOP_LEFT);
        return row;
    }

    // ── ROW 2 ─────────────────────────────────────────────────────────────
    private HBox buildRow2() {
        // Buttons grid (2 rows × 3 cols, matching original layout)
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        for (int i = 0; i < 3; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setHgrow(Priority.ALWAYS);
            cc.setFillWidth(true);
            grid.getColumnConstraints().add(cc);
        }
        // Row 0
        grid.add(sortButton,   0, 0);
        grid.add(tangDanBtn,   1, 0);
        grid.add(stopButton,   2, 0);
        // Row 1
        grid.add(resetButton,  0, 1);
        grid.add(giamDanBtn,   1, 1);
        grid.add(deleteButton, 2, 1);
        // Make all buttons fill width
        for (javafx.scene.Node n : grid.getChildren()) {
            if (n instanceof Button b) {
                b.setMaxWidth(Double.MAX_VALUE);
                GridPane.setFillWidth(b, true);
            }
        }

        VBox buttonsCard = new VBox(grid);
        buttonsCard.getStyleClass().add("card");
        buttonsCard.setPadding(new Insets(14, 16, 14, 16));
        HBox.setHgrow(buttonsCard, Priority.ALWAYS);

        // Swap count card
        swapValueLabel.getStyleClass().add("stat-value");
        VBox swapCard = labeledCard("Swap Count", swapValueLabel);
        swapCard.setPrefWidth(180);
        swapCard.setMinWidth(150);

        // Speed card
        speedSlider.setMajorTickUnit(0.5);
        speedSlider.setMinorTickCount(0);
        speedSlider.setMaxWidth(Double.MAX_VALUE);
        VBox speedCard = labeledCard("Sorting Speed", speedSlider);
        speedCard.setPrefWidth(260);
        speedCard.setMinWidth(200);
        HBox.setHgrow(speedCard, Priority.SOMETIMES);

        // Create button
        createButton.setMaxWidth(Double.MAX_VALUE);
        createButton.setPrefHeight(60);
        VBox createCard = new VBox(createButton);
        createCard.getStyleClass().add("card");
        createCard.setPadding(new Insets(14, 16, 14, 16));
        createCard.setAlignment(Pos.CENTER);
        createCard.setPrefWidth(160);
        createCard.setMinWidth(140);

        HBox row = new HBox(12, buttonsCard, swapCard, speedCard, createCard);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    // ── ROW 3 ─────────────────────────────────────────────────────────────
    private HBox buildRow3() {
        // Chart (left, grows)
        chartPane.setSpacing(8);
        chartPane.setAlignment(Pos.BOTTOM_CENTER);
        chartPane.setFillHeight(true);
        chartPane.getStyleClass().add("chart-pane");
        chartPane.setMinHeight(380);
        chartPane.setMaxWidth(Double.MAX_VALUE);

        Label sortLabel = new Label("Sort");
        sortLabel.getStyleClass().add("section-title");

        VBox chartCard = new VBox(10, sortLabel, chartPane);
        chartCard.getStyleClass().add("card");
        chartCard.setPadding(new Insets(16));
        VBox.setVgrow(chartPane, Priority.ALWAYS);
        HBox.setHgrow(chartCard, Priority.ALWAYS);

        // Status bar inside chart card
        statusLabel.getStyleClass().add("status-text");
        chartCard.getChildren().add(statusLabel);

        // Code panel (right, fixed width)
        ScrollPane codeScroll = new ScrollPane(codePane);
        codeScroll.setFitToWidth(false);
        codeScroll.setFitToHeight(true);
        codeScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        codeScroll.getStyleClass().add("code-scroll");
        VBox.setVgrow(codeScroll, Priority.ALWAYS);

        Label codeLabel = new Label("Code java");
        codeLabel.getStyleClass().add("section-title");

        VBox codeCard = new VBox(10, codeLabel, codeScroll);
        codeCard.getStyleClass().add("card");
        codeCard.setPadding(new Insets(16));
        codeCard.setPrefWidth(360);
        codeCard.setMinWidth(300);
        codeCard.setMaxWidth(400);
        VBox.setVgrow(codeScroll, Priority.ALWAYS);

        HBox row = new HBox(12, chartCard, codeCard);
        HBox.setHgrow(chartCard, Priority.ALWAYS);
        VBox.setVgrow(chartCard, Priority.ALWAYS);
        VBox.setVgrow(codeCard, Priority.ALWAYS);
        return row;
    }

    // ── Helpers ──────────────────────────────────────────────────────────
    private VBox labeledCard(String title, javafx.scene.Node content) {
        Label lbl = new Label(title);
        lbl.getStyleClass().add("card-title");
        VBox box = new VBox(10, lbl, content);
        box.getStyleClass().add("card");
        box.setPadding(new Insets(14, 16, 14, 16));
        return box;
    }

    private void applyButtonStyles() {
        sortButton.getStyleClass().add("btn-sort");
        tangDanBtn.getStyleClass().add("btn-sort");
        createButton.getStyleClass().add("btn-create");
    }

    // ── Events ───────────────────────────────────────────────────────────
    private void bindEvents() {
        sizeSlider.valueProperty().addListener((o, ov, nv) ->
                sizeRangeLabel.setText("2 -> " + nv.intValue()));

        dataSourceBox.valueProperty().addListener((o, ov, nv) -> {
            boolean manual = "Manual Entry".equals(nv);
            manualInput.setVisible(manual);
            manualInput.setManaged(manual);
            sizeSlider.setDisable(manual);
        });

        algorithmBox.valueProperty().addListener((o, ov, nv) -> renderCode());

        createButton.setOnAction(e -> createArray());
        deleteButton.setOnAction(e -> deleteArray());
        resetButton.setOnAction(e -> resetArray());
        sortButton.setOnAction(e -> sort(true));
        tangDanBtn.setOnAction(e -> sort(true));
        giamDanBtn.setOnAction(e -> sort(false));
        stopButton.setOnAction(e -> togglePause());
    }

    // ── Array operations ─────────────────────────────────────────────────
    private boolean isManual() { return "Manual Entry".equals(dataSourceBox.getValue()); }

    private void createArray() {
        stopAnimation();
        sortedIndices.clear();
        swapCount = 0; stepIndex = 0;
        swapValueLabel.setText("0");

        int[] array = isManual() ? parseManualInput() : randomArray((int) sizeSlider.getValue());
        if (array.length == 0) {
            statusLabel.setText("Empty or invalid data.");
            currentArray = originalArray = new int[0];
            refreshChart(currentArray, -1, -1, null);
            return;
        }
        originalArray = array.clone();
        currentArray  = array.clone();
        steps = List.of();
        statusLabel.setText("rray is ready — click Sort to start.");
        refreshChart(currentArray, -1, -1, null);
        renderCode();
        updateButtons(false);
    }

    private void sort(boolean ascending) {
        if (currentArray.length == 0) { statusLabel.setText("Please create an array first."); return; }
        stopAnimation();
        sortedIndices.clear();
        swapCount = 0; stepIndex = 0;
        swapValueLabel.setText("0");

        int[] source = isManual() ? parseManualInput() : currentArray.clone();
        if (source.length == 0) { statusLabel.setText("Empty or invalid data."); return; }

        originalArray = source.clone();
        currentArray  = source.clone();

        SortStrategy strategy = strategies.get(algorithmBox.getValue());
        steps = strategy.sort(source, ascending);
        if (steps.isEmpty()) {
            statusLabel.setText("Failed to create animation step.");
            refreshChart(currentArray, -1, -1, null);
            return;
        }
        paused = false;
        statusLabel.setText(ascending ? "Sorting ascending..." : "Sorting descending...");
        stopButton.setText("Stop");
        playTimeline();
    }

    private void playTimeline() {
        updateButtons(true);
        timeline = new Timeline(new KeyFrame(Duration.millis(getDelayMillis()), e -> advanceStep()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void advanceStep() {
        if (stepIndex >= steps.size()) { finishAnimation(); return; }
        Step step = steps.get(stepIndex++);
        currentArray = step.array.clone();
        if ("swap".equals(step.type)) {
            swapCount++;
            swapValueLabel.setText(Integer.toString(swapCount));
        } else if ("sorted".equals(step.type)) {
            sortedIndices.add(step.i);
            sortedIndices.add(step.j);
        }
        refreshChart(currentArray, step.i, step.j, step.type);
        renderCode();
    }

    private void finishAnimation() {
        stopAnimation();
        for (int i = 0; i < currentArray.length; i++) sortedIndices.add(i);
        refreshChart(currentArray, -1, -1, null);
        statusLabel.setText("✓ Completed — " + swapCount + " swaps.");
        updateButtons(false);
    }

    private void togglePause() {
        if (timeline == null) return;
        if (paused) {
            paused = false;
            timeline.play();
            stopButton.setText("Stop");
            statusLabel.setText("Resuming...");
        } else {
            paused = true;
            timeline.pause();
            stopButton.setText("Resume");
            statusLabel.setText("Paused.");
        }
    }

    private void resetArray() {
        stopAnimation();
        currentArray = originalArray.clone();
        sortedIndices.clear();
        steps = List.of(); stepIndex = 0; swapCount = 0;
        swapValueLabel.setText("0");
        refreshChart(currentArray, -1, -1, null);
        renderCode();
        statusLabel.setText(currentArray.length == 0 ? "Nothing to reset." : "Array reset.");
        updateButtons(false);
    }

    private void deleteArray() {
        stopAnimation();
        originalArray = currentArray = new int[0];
        steps = List.of(); sortedIndices.clear();
        stepIndex = 0; swapCount = 0;
        swapValueLabel.setText("0");
        refreshChart(currentArray, -1, -1, null);
        renderCode();
        statusLabel.setText("Array deleted.");
        updateButtons(false);
    }

    private void stopAnimation() {
        if (timeline != null) { timeline.stop(); timeline = null; }
        paused = false;
        stopButton.setText("Stop");
    }

    // ── Parsing / utils ───────────────────────────────────────────────────
    private int[] parseManualInput() {
        String text = manualInput.getText();
        if (text == null || text.isBlank()) return new int[0];
        List<Integer> vals = new ArrayList<>();
        for (String tok : text.split("[,\\s]+")) {
            if (tok.isBlank()) continue;
            try { vals.add(Integer.parseInt(tok.trim())); }
            catch (NumberFormatException ex) { return new int[0]; }
        }
        return vals.stream().mapToInt(Integer::intValue).toArray();
    }

    private int[] randomArray(int size) {
        int[] a = new int[size];
        for (int i = 0; i < size; i++) a[i] = 10 + random.nextInt(271);
        return a;
    }

    private double getDelayMillis() {
        double s = speedSlider.getValue();
        if (s <= 0.5) return 1200;
        if (s <= 1.0) return 700;
        if (s <= 1.5) return 400;
        if (s <= 2.0) return 220;
        if (s <= 2.5) return 120;
        return 45;
    }

    // ── Chart ─────────────────────────────────────────────────────────────
    private void refreshChart(int[] values, int fi, int si, String stepType) {
        chartPane.getChildren().clear();
        if (values.length == 0) {
            Label empty = new Label("No data available — Click \"Create Array\"");
            empty.getStyleClass().add("empty-state");
            chartPane.getChildren().add(empty);
            return;
        }
        int max = Arrays.stream(values).max().orElse(1);
        double barW = values.length > 12 ? 26 : values.length > 8 ? 34 : 44;
        double gap  = values.length > 12 ?  5 : values.length > 8 ?  7 :  9;
        chartPane.setSpacing(gap);

        for (int i = 0; i < values.length; i++) {
            VBox barBox = new VBox(4);
            barBox.setAlignment(Pos.BOTTOM_CENTER);
            barBox.getStyleClass().add("bar-box");
            barBox.setPrefWidth(barW);
            barBox.setMinWidth(barW);

            Text valLabel = new Text(Integer.toString(values[i]));
            valLabel.getStyleClass().add("bar-value");

            double h = Math.max(18, (values[i] * 340.0) / max);
            Color  c = resolveColor(i, fi, si, stepType);

            Rectangle bar = new Rectangle(barW * 0.68, h);
            bar.setArcWidth(7);
            bar.setArcHeight(7);
            bar.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                    new Stop(0, c.brighter()),
                    new Stop(1, c.darker())));

            Label idxLabel = new Label(Integer.toString(i));
            idxLabel.getStyleClass().add("bar-index");

            barBox.getChildren().addAll(valLabel, bar, idxLabel);
            chartPane.getChildren().add(barBox);
        }
    }

    private Color resolveColor(int idx, int fi, int si, String type) {
        if (sortedIndices.contains(idx))                          return COL_SORTED;
        if ("swap".equals(type)    && (idx == fi || idx == si))   return COL_SWAP;
        if ("compare".equals(type) && (idx == fi || idx == si))   return COL_COMPARE;
        return COL_DEFAULT;
    }

    // ── Code panel ────────────────────────────────────────────────────────
    private void renderCode() {
        codePane.getChildren().clear();
        String code = CodeSnippets.JAVA_CODE.getOrDefault(algorithmBox.getValue(), "// Không có code");
        int activeLine = resolveActiveLine();
        String[] lines = code.split("\\R");
        for (int i = 0; i < lines.length; i++) {
            Label line = new Label(String.format("%2d  %s", i + 1, lines[i]));
            line.getStyleClass().add("code-line");
            line.setWrapText(false);
            line.setMinWidth(Region.USE_PREF_SIZE);
            line.setMaxWidth(Region.USE_PREF_SIZE);
            if (i == activeLine) line.getStyleClass().add("code-line-active");
            codePane.getChildren().add(line);
        }
    }

    private int resolveActiveLine() {
        if (stepIndex == 0 || stepIndex > steps.size()) return -1;
        Step step = steps.get(stepIndex - 1);
        return switch (algorithmBox.getValue()) {
            case "bubble"    -> mapStep(step, 3, 5, 1);
            case "selection" -> mapStep(step, 4, 9, 2);
            case "insertion" -> mapStep(step, 4, 6, 1);
            case "heap"      -> mapStep(step, 1, 6, 5);
            case "quick"     -> mapStep(step, 2, 3, 1);
            case "merge"     -> mapStep(step, 4, 8, 1);
            default -> -1;
        };
    }

    private int mapStep(Step step, int cmp, int swp, int srt) {
        return switch (step.type) {
            case "compare" -> cmp;
            case "swap"    -> swp;
            case "sorted"  -> srt;
            default -> -1;
        };
    }

    // ── Button state ──────────────────────────────────────────────────────
    private void updateButtons(boolean running) {
        boolean hasData = currentArray.length > 0;
        sortButton.setDisable(!hasData || running);
        tangDanBtn.setDisable(!hasData || running);
        giamDanBtn.setDisable(!hasData || running);
        createButton.setDisable(running);
        deleteButton.setDisable(!hasData || running);
        resetButton.setDisable(!hasData);
        algorithmBox.setDisable(running);
        dataSourceBox.setDisable(running);
        sizeSlider.setDisable(running || isManual());
        manualInput.setDisable(running);
        stopButton.setDisable(!running);
    }
}