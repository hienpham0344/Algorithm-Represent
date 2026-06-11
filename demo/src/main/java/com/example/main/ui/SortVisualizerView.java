package com.example.main.ui;

import com.example.main.dto.Step;
import com.example.main.dto.StepAction;
import com.example.main.service.SortStrategy;
import com.example.main.service.SortingRegistry;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
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
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
    private static final Color COL_DEFAULT = Color.web(SortTheme.DEFAULT_BAR);
    private static final Color COL_COMPARE = Color.web(SortTheme.COMPARE_BAR);
    private static final Color COL_SWAP    = Color.web(SortTheme.SWAP_BAR);
    private static final Color COL_SORTED  = Color.web(SortTheme.SORTED_BAR);

    // ── State ────────────────────────────────────────────────────────────
    private final Map<String, SortStrategy> strategies = SortingRegistry.createStrategies();
    private final Random random = new Random();

    // Controls
    private final ComboBox<String> algorithmBox = new ComboBox<>();
    private final ComboBox<String> dataSourceBox = new ComboBox<>();
    private final Slider  sizeSlider   = new Slider(2, 15, 8);
    private final Label   sizeRangeLabel = new Label("2 -> 15");
    private final Slider  speedSlider =
            new Slider(SortSpeed.MIN_RATE, SortSpeed.MAX_RATE, SortSpeed.DEFAULT_RATE);
    private final Label   speedValueLabel = new Label(SortSpeed.label(SortSpeed.DEFAULT_RATE));
    private final TextArea manualInput = new TextArea("42, 17, 88, 6, 31, 59, 12, 75");
    private final Button importButton = new Button("Import TXT");
    private final Label importStatusLabel = new Label();
    private final VBox manualInputPanel = new VBox(8);
    private final Label   swapValueLabel = new Label("0");
    private final Label   statusLabel  = new Label("Creating an array to get started.");
    private final Label   explanationTitle = new Label("Giải thích thuật toán");
    private final Label   explanationText = new Label();
    private final Label   lessonFacts = new Label();

    // Buttons
    private final Button sortButton   = new Button("Sort");
    private final Button tangDanBtn   = new Button("Ascending");
    private final Button giamDanBtn   = new Button("Descending");
    private final Button stopButton   = new Button("Stop");
    private final Button resetButton  = new Button("Reset");
    private final Button deleteButton = new Button("Delete");
    private final Button createButton = new Button("Create Array");

    private String importedFileName;
    private boolean updatingManualInput;

    // Panels
    private final HBox  chartPane = new HBox();
    private final ScrollPane chartScroll = new ScrollPane(chartPane);
    private final VBox  codePane  = new VBox(2);
    private final ScrollPane codeScroll = new ScrollPane(codePane);

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
        updateLesson();
        renderCode(null);
        refreshChart(currentArray, null);
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
        importStatusLabel.getStyleClass().add("import-status");
        importStatusLabel.setWrapText(true);
        HBox importRow = new HBox(8, importButton, importStatusLabel);
        importRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(importStatusLabel, Priority.ALWAYS);
        manualInputPanel.getChildren().addAll(manualInput, importRow);
        manualInputPanel.setVisible(false);
        manualInputPanel.setManaged(false);
        VBox dataSourceCard = labeledCard(
                "Generate array data",
                new VBox(8, dataSourceBox, manualInputPanel));
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
        speedSlider.setBlockIncrement(0.5);
        speedSlider.setSnapToTicks(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setShowTickLabels(true);
        speedSlider.setMaxWidth(Double.MAX_VALUE);
        speedValueLabel.getStyleClass().add("speed-value");
        Label speedTitle = new Label("Sorting Speed");
        speedTitle.getStyleClass().add("card-title");
        Region speedSpacer = new Region();
        HBox.setHgrow(speedSpacer, Priority.ALWAYS);
        HBox speedHeader = new HBox(speedTitle, speedSpacer, speedValueLabel);
        speedHeader.setAlignment(Pos.CENTER_LEFT);

        VBox speedCard = new VBox(10, speedHeader, speedSlider);
        speedCard.getStyleClass().add("card");
        speedCard.setPadding(new Insets(14, 16, 14, 16));
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
        chartPane.setMinHeight(280);
        chartPane.setMaxWidth(Double.MAX_VALUE);

        chartScroll.setFitToWidth(true);
        chartScroll.setFitToHeight(true);
        chartScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        chartScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        chartScroll.setPannable(true);
        chartScroll.setMinHeight(315);
        chartScroll.getStyleClass().add("chart-scroll");

        Label sortLabel = new Label("Sort");
        sortLabel.getStyleClass().add("section-title");

        explanationTitle.getStyleClass().add("explanation-title");
        explanationText.getStyleClass().add("explanation-text");
        explanationText.setWrapText(true);
        lessonFacts.getStyleClass().add("lesson-facts");
        lessonFacts.setWrapText(true);
        VBox explanationCard = new VBox(6, explanationTitle, explanationText, lessonFacts);
        explanationCard.getStyleClass().add("explanation-card");

        VBox chartCard = new VBox(10, sortLabel, chartScroll, explanationCard);
        chartCard.getStyleClass().add("card");
        chartCard.setPadding(new Insets(16));
        VBox.setVgrow(chartScroll, Priority.ALWAYS);
        HBox.setHgrow(chartCard, Priority.ALWAYS);

        // Status bar inside chart card
        statusLabel.getStyleClass().add("status-text");
        chartCard.getChildren().add(statusLabel);

        // Code panel (right, fixed width)
        codeScroll.setFitToWidth(false);
        codeScroll.setFitToHeight(false);
        codeScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        codeScroll.getStyleClass().add("code-scroll");
        VBox.setVgrow(codeScroll, Priority.ALWAYS);

        Label codeLabel = new Label("Code java");
        codeLabel.getStyleClass().add("section-title");

        VBox codeCard = new VBox(10, codeLabel, codeScroll);
        codeCard.getStyleClass().add("card");
        codeCard.setPadding(new Insets(16));
        codeCard.setPrefWidth(SortLayout.CODE_PANEL_PREF_WIDTH);
        codeCard.setMinWidth(SortLayout.CODE_PANEL_MIN_WIDTH);
        codeCard.setMaxWidth(SortLayout.CODE_PANEL_MAX_WIDTH);
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
        sortButton.getStyleClass().add(SortTheme.PRIMARY_BUTTON);
        tangDanBtn.getStyleClass().add(SortTheme.PRIMARY_BUTTON);
        giamDanBtn.getStyleClass().add(SortTheme.PRIMARY_BUTTON);
        createButton.getStyleClass().add(SortTheme.SUCCESS_BUTTON);
        importButton.getStyleClass().add(SortTheme.INFO_BUTTON);
        stopButton.getStyleClass().add(SortTheme.DANGER_BUTTON);
        deleteButton.getStyleClass().add(SortTheme.DANGER_BUTTON);
        resetButton.getStyleClass().add(SortTheme.SECONDARY_BUTTON);
    }

    // ── Events ───────────────────────────────────────────────────────────
    private void bindEvents() {
        sizeSlider.valueProperty().addListener((o, ov, nv) ->
                sizeRangeLabel.setText("2 -> " + nv.intValue()));

        speedSlider.valueProperty().addListener((o, ov, nv) -> {
            double rate = SortSpeed.timelineRate(nv.doubleValue());
            speedValueLabel.setText(SortSpeed.label(rate));
            if (timeline != null) {
                timeline.setRate(rate);
            }
        });

        dataSourceBox.valueProperty().addListener((o, ov, nv) -> {
            boolean manual = "Manual Entry".equals(nv);
            manualInputPanel.setVisible(manual);
            manualInputPanel.setManaged(manual);
            sizeSlider.setDisable(manual);
        });

        manualInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!updatingManualInput && importedFileName != null) {
                importStatusLabel.setText(importedFileName + " \u00B7 Edited");
            }
        });

        algorithmBox.valueProperty().addListener((o, ov, nv) -> {
            updateLesson();
            renderCode(null);
        });

        importButton.setOnAction(e -> importTextFile());
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
        int[] array;
        try {
            array = isManual()
                    ? SortInputParser.parse(manualInput.getText())
                    : randomArray((int) sizeSlider.getValue());
        } catch (SortInputValidationException exception) {
            showError("Invalid manual input", exception.getMessage());
            return;
        }

        stopAnimation();
        sortedIndices.clear();
        swapCount = 0; stepIndex = 0;
        swapValueLabel.setText("0");

        originalArray = array.clone();
        currentArray  = array.clone();
        steps = List.of();
        statusLabel.setText("Array is ready - click Sort to start.");
        refreshChart(currentArray, null);
        updateLesson();
        renderCode(null);
        updateButtons(false);
    }

    private void sort(boolean ascending) {
        if (currentArray.length == 0) { statusLabel.setText("Please create an array first."); return; }

        int[] source;
        try {
            source = isManual()
                    ? SortInputParser.parse(manualInput.getText())
                    : currentArray.clone();
        } catch (SortInputValidationException exception) {
            showError("Invalid manual input", exception.getMessage());
            return;
        }

        stopAnimation();
        sortedIndices.clear();
        swapCount = 0; stepIndex = 0;
        swapValueLabel.setText("0");

        originalArray = source.clone();
        currentArray  = source.clone();

        SortStrategy strategy = strategies.get(algorithmBox.getValue());
        steps = strategy.sort(source, ascending);
        if (steps.isEmpty()) {
            statusLabel.setText("Failed to create animation step.");
            refreshChart(currentArray, null);
            return;
        }
        paused = false;
        statusLabel.setText(ascending ? "Sorting ascending..." : "Sorting descending...");
        stopButton.setText("Stop");
        playTimeline();
    }

    private void playTimeline() {
        updateButtons(true);
        timeline = new Timeline(new KeyFrame(
                Duration.millis(SortSpeed.BASE_STEP_MILLIS),
                e -> advanceStep()
        ));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setRate(SortSpeed.timelineRate(speedSlider.getValue()));
        timeline.play();
    }

    private void advanceStep() {
        if (stepIndex >= steps.size()) { finishAnimation(); return; }
        Step step = steps.get(stepIndex++);
        currentArray = step.array();
        if (step.countsAsSwap()) {
            swapCount++;
            swapValueLabel.setText(Integer.toString(swapCount));
        }
        sortedIndices.clear();
        sortedIndices.addAll(step.sortedIndices());
        refreshChart(currentArray, step);
        showStepExplanation(step);
        renderCode(step.codeLineId());
    }

    private void finishAnimation() {
        stopAnimation();
        for (int i = 0; i < currentArray.length; i++) sortedIndices.add(i);
        refreshChart(currentArray, null);
        explanationTitle.setText("Hoàn tất");
        explanationText.setText("Mảng đã được sắp xếp. Tổng số lần đổi chỗ thực: " + swapCount + ".");
        lessonFacts.setText("Đã thực hiện " + steps.size() + " bước mô phỏng.");
        statusLabel.setText("Completed - " + swapCount + " swaps.");
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
        refreshChart(currentArray, null);
        updateLesson();
        renderCode(null);
        statusLabel.setText(currentArray.length == 0 ? "Nothing to reset." : "Array reset.");
        updateButtons(false);
    }

    private void deleteArray() {
        stopAnimation();
        originalArray = currentArray = new int[0];
        steps = List.of(); sortedIndices.clear();
        stepIndex = 0; swapCount = 0;
        swapValueLabel.setText("0");
        refreshChart(currentArray, null);
        updateLesson();
        renderCode(null);
        statusLabel.setText("Array deleted.");
        updateButtons(false);
    }

    private void stopAnimation() {
        if (timeline != null) { timeline.stop(); timeline = null; }
        paused = false;
        stopButton.setText("Stop");
    }

    // ── Import / utils ────────────────────────────────────────────────────
    private void importTextFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Import array data");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt"));

        Window owner = getScene() == null ? null : getScene().getWindow();
        File selectedFile = chooser.showOpenDialog(owner);
        if (selectedFile == null) {
            return;
        }

        try {
            String content = Files.readString(selectedFile.toPath(), StandardCharsets.UTF_8);
            int[] values = SortInputParser.parse(content);
            updatingManualInput = true;
            try {
                manualInput.setText(SortInputParser.normalize(values));
            } finally {
                updatingManualInput = false;
            }
            importedFileName = selectedFile.getName();
            importStatusLabel.setText(
                    importedFileName + " \u00B7 " + values.length + " values");
            statusLabel.setText("TXT data imported. Click Create Array to use it.");
        } catch (SortInputValidationException exception) {
            showError("Invalid TXT data", exception.getMessage());
        } catch (IOException exception) {
            showError(
                    "Unable to import TXT",
                    "The selected file could not be read as UTF-8.\n" + exception.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        if (getScene() != null && getScene().getWindow() != null) {
            alert.initOwner(getScene().getWindow());
        }
        alert.showAndWait();
    }

    private int[] randomArray(int size) {
        int[] a = new int[size];
        for (int i = 0; i < size; i++) a[i] = 10 + random.nextInt(271);
        return a;
    }

    // ── Chart ─────────────────────────────────────────────────────────────
    private void refreshChart(int[] values, Step step) {
        chartPane.getChildren().clear();
        if (values.length == 0) {
            chartPane.setMinWidth(0);
            chartPane.setPrefWidth(Region.USE_COMPUTED_SIZE);
            Label empty = new Label("No data available - Click \"Create Array\"");
            empty.getStyleClass().add("empty-state");
            chartPane.getChildren().add(empty);
            return;
        }
        int max = Arrays.stream(values).max().orElse(1);
        double barW = SortLayout.barWidth(values.length);
        double gap = SortLayout.barGap(values.length);
        double contentWidth = SortLayout.chartContentWidth(values.length);
        chartPane.setMinWidth(contentWidth);
        chartPane.setPrefWidth(contentWidth);
        chartPane.setSpacing(gap);

        for (int i = 0; i < values.length; i++) {
            VBox barBox = new VBox(4);
            barBox.setAlignment(Pos.BOTTOM_CENTER);
            barBox.getStyleClass().add("bar-box");
            barBox.setPrefWidth(barW);
            barBox.setMinWidth(barW);

            Label markerLabel = new Label(step == null ? "" : SortPresentation.markerText(step, i));
            markerLabel.getStyleClass().add("bar-marker");
            markerLabel.setMinHeight(18);

            boolean showSwap = step != null && SortPresentation.showSwapBadge(step, i);
            Label swapBadge = new Label(showSwap ? "SWAP" : "");
            if (showSwap) {
                swapBadge.getStyleClass().add("swap-badge");
            }
            swapBadge.setMinHeight(18);

            Text valLabel = new Text(Integer.toString(values[i]));
            valLabel.getStyleClass().add("bar-value");

            double h = Math.max(18, (values[i] * 230.0) / max);
            Color  c = resolveColor(i, step);

            Rectangle bar = new Rectangle(barW * 0.68, h);
            bar.setArcWidth(7);
            bar.setArcHeight(7);
            bar.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                    new Stop(0, c.brighter()),
                    new Stop(1, c.darker())));

            Label idxLabel = new Label(Integer.toString(i));
            idxLabel.getStyleClass().add("bar-index");

            barBox.getChildren().addAll(markerLabel, swapBadge, valLabel, bar, idxLabel);
            chartPane.getChildren().add(barBox);
        }
    }

    private Color resolveColor(int idx, Step step) {
        if (step != null && step.affectedIndices().contains(idx)) {
            if (step.action() == StepAction.SWAP) return COL_SWAP;
            if (step.action() == StepAction.CONDITION || step.action() == StepAction.WRITE) return COL_COMPARE;
        }
        if (sortedIndices.contains(idx)) return COL_SORTED;
        return COL_DEFAULT;
    }

    // ── Code panel ────────────────────────────────────────────────────────
    private void renderCode(String activeLineId) {
        codePane.getChildren().clear();
        List<CodeLine> lines = CodeSnippets.get(algorithmBox.getValue()).lines();
        int activeLine = CodeSnippets.lineIndex(algorithmBox.getValue(), activeLineId);
        for (int i = 0; i < lines.size(); i++) {
            CodeLine codeLine = lines.get(i);
            Label line = new Label(String.format("%2d  %s", i + 1, codeLine.text()));
            line.getStyleClass().add("code-line");
            line.setWrapText(false);
            line.setMinWidth(Region.USE_PREF_SIZE);
            line.setMaxWidth(Region.USE_PREF_SIZE);
            if (i == activeLine) line.getStyleClass().add("code-line-active");
            codePane.getChildren().add(line);
        }
        if (activeLine >= 0 && lines.size() > 1) {
            double position = (double) activeLine / (lines.size() - 1);
            Platform.runLater(() -> codeScroll.setVvalue(position));
        }
    }

    private void updateLesson() {
        AlgorithmLesson lesson = AlgorithmLessons.get(algorithmBox.getValue());
        explanationTitle.setText("Cách hoạt động");
        explanationText.setText(lesson.overview());
        lessonFacts.setText("Thời gian: " + lesson.timeComplexity()
                + "   |   Bộ nhớ: " + lesson.spaceComplexity()
                + "   |   " + lesson.stability());
    }

    private void showStepExplanation(Step step) {
        explanationTitle.setText(switch (step.action()) {
            case CONDITION -> "Kiểm tra điều kiện";
            case VARIABLE_UPDATE -> "Cập nhật biến";
            case SWAP -> step.countsAsSwap() ? "Đổi chỗ" : "Đặt phần tử";
            case WRITE -> "Ghi giá trị";
            case MARK_SORTED -> "Cố định vị trí";
            case COMPLETE -> "Hoàn tất";
        });
        explanationText.setText(step.explanation());
        lessonFacts.setText("Bước " + stepIndex + " / " + steps.size());
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
        importButton.setDisable(running);
        stopButton.setDisable(!running);
    }
}
