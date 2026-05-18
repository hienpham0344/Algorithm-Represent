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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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

public class SortVisualizerView extends HBox {

    private static final Map<String, String> ALGORITHM_NAMES = Map.of(
            "selection", "Selection Sort",
            "bubble", "Bubble Sort",
            "insertion", "Insertion Sort",
            "heap", "Heap Sort",
            "quick", "Quick Sort",
            "merge", "Merge Sort"
    );

    private final Map<String, SortStrategy> strategies = SortingRegistry.createStrategies();
    private final Random random = new Random();

    private final ComboBox<String> algorithmBox = new ComboBox<>();
    private final Slider sizeSlider = new Slider(2, 20, 8);
    private final Label sizeLabel = new Label("8");
    private final Slider speedSlider = new Slider(0.5, 3.0, 1.0);
    private final Label speedValue = new Label("1.0x");
    private final Label swapValue = new Label("0");
    private final Label arraySummary = new Label("No array");
    private final TextArea manualInput = new TextArea("42, 17, 88, 6, 31, 59, 12, 75");
    private final HBox chartPane = new HBox();
    private final VBox codePane = new VBox(4);
    private final Label statusLabel = new Label("Create an array to begin.");
    private final Button sortButton = new Button("Sort");
    private final Button ascButton = new Button("Ascending");
    private final Button descButton = new Button("Descending");
    private final Button stopButton = new Button("Pause");
    private final Button resetButton = new Button("Reset");
    private final Button deleteButton = new Button("Delete");
    private final Button createButton = new Button("Create");
    private final RadioButton randomMode = new RadioButton("Random array");
    private final RadioButton manualMode = new RadioButton("Manual input");

    private Timeline timeline;
    private int[] originalArray = new int[0];
    private int[] currentArray = new int[0];
    private List<Step> steps = List.of();
    private int stepIndex;
    private int swapCount;
    private boolean paused;
    private final Set<Integer> sortedIndices = new LinkedHashSet<>();

    public SortVisualizerView() {
        getStyleClass().add("app-root");
        setPadding(new Insets(24));
        setSpacing(18);

        VBox leftPanel = buildLeftPanel();
        VBox rightPanel = buildRightPanel();
        VBox centerPanel = buildCenterPanel();

        HBox.setHgrow(centerPanel, Priority.ALWAYS);
        getChildren().addAll(leftPanel, centerPanel, rightPanel);

        bindEvents();
        renderCode();
        refreshChart(currentArray, -1, -1, null);
        updateModeVisibility();
        updateButtons(false);
    }

    private VBox buildLeftPanel() {
        VBox panel = new VBox(16);
        panel.setPrefWidth(320);
        panel.setMinWidth(320);
        panel.setMaxWidth(320);

        Label title = new Label("Sorting Algorithm Visualizer");
        title.getStyleClass().add("page-title");
        title.setWrapText(true);
        title.setMaxWidth(Double.MAX_VALUE);

        VBox algorithmCard = card("Algorithm", algorithmBox);
        algorithmBox.getItems().addAll(strategies.keySet());
        algorithmBox.setCellFactory(ignored -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : ALGORITHM_NAMES.getOrDefault(item, item));
            }
        });
        algorithmBox.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : ALGORITHM_NAMES.getOrDefault(item, item));
            }
        });
        algorithmBox.getSelectionModel().select("selection");

        ToggleGroup modeGroup = new ToggleGroup();
        randomMode.setToggleGroup(modeGroup);
        manualMode.setToggleGroup(modeGroup);
        randomMode.setSelected(true);
        VBox modeBox = new VBox(10, randomMode, manualMode, manualInput);
        manualInput.setPrefRowCount(4);
        manualInput.setWrapText(true);
        manualInput.setPromptText("Example: 5, 1, 9, 3");
        VBox inputCard = card("Data Source", modeBox);

        sizeSlider.setMajorTickUnit(1);
        sizeSlider.setMinorTickCount(0);
        sizeSlider.setSnapToTicks(true);
        VBox sizeCard = card("Array Size", new VBox(8, sizeLabel, sizeSlider));

        speedSlider.setMajorTickUnit(0.5);
        speedSlider.setMinorTickCount(0);
        VBox speedCard = card("Speed", new VBox(8, speedValue, speedSlider));

        HBox row1 = new HBox(10, sortButton, stopButton);
        HBox row2 = new HBox(10, ascButton, descButton);
        HBox row3 = new HBox(10, resetButton, deleteButton);
        row1.getChildren().forEach(node -> HBox.setHgrow(node, Priority.ALWAYS));
        row2.getChildren().forEach(node -> HBox.setHgrow(node, Priority.ALWAYS));
        row3.getChildren().forEach(node -> HBox.setHgrow(node, Priority.ALWAYS));
        createButton.setMaxWidth(Double.MAX_VALUE);
        VBox actionCard = card("Controls", new VBox(10, createButton, row1, row2, row3));

        VBox statsCard = card("Swap Count", swapValue);
        VBox summaryCard = card("Array Snapshot", arraySummary);
        VBox legendCard = card("Legend", new VBox(
                8,
                buildLegendItem("Blue", "Current values"),
                buildLegendItem("Amber", "Comparing"),
                buildLegendItem("Orange", "Swapping"),
                buildLegendItem("Green", "Sorted")
        ));
        VBox statusCard = card("Status", statusLabel);

        panel.getChildren().addAll(title, algorithmCard, inputCard, sizeCard, speedCard, statsCard, actionCard, summaryCard, legendCard, statusCard);
        return panel;
    }

    private VBox buildCenterPanel() {
        VBox panel = new VBox(16);
        panel.setPrefWidth(760);
        panel.setMinWidth(520);

        Label heading = new Label("Visualization");
        heading.getStyleClass().add("section-title");

        chartPane.getStyleClass().add("chart-pane");
        chartPane.setSpacing(12);
        chartPane.setAlignment(Pos.BOTTOM_CENTER);
        chartPane.setFillHeight(true);
        chartPane.setMinHeight(520);
        chartPane.setPrefHeight(520);
        chartPane.setMaxWidth(Double.MAX_VALUE);

        StackPane chartCard = new StackPane(chartPane);
        chartCard.getStyleClass().add("card");
        chartCard.setPadding(new Insets(18));
        chartCard.setAlignment(Pos.BOTTOM_CENTER);
        VBox.setVgrow(chartCard, Priority.ALWAYS);
        HBox.setHgrow(chartCard, Priority.ALWAYS);

        panel.getChildren().addAll(heading, chartCard);
        return panel;
    }

    private VBox buildRightPanel() {
        VBox panel = new VBox(16);
        panel.setPrefWidth(360);
        panel.setMinWidth(360);
        panel.setMaxWidth(360);

        Label heading = new Label("Java Code");
        heading.getStyleClass().add("section-title");

        ScrollPane scrollPane = new ScrollPane(codePane);
        scrollPane.setFitToWidth(false);
        scrollPane.setFitToHeight(true);
        ScrollPane.ScrollBarPolicy horizontalPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED;
        scrollPane.setHbarPolicy(horizontalPolicy);
        scrollPane.getStyleClass().add("code-scroll");
        scrollPane.setPrefHeight(640);

        VBox codeCard = new VBox(scrollPane);
        codeCard.getStyleClass().add("card");
        codeCard.setPadding(new Insets(18));
        VBox.setVgrow(codeCard, Priority.ALWAYS);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        panel.getChildren().addAll(heading, codeCard);
        return panel;
    }

    private VBox card(String title, javafx.scene.Node content) {
        Label label = new Label(title);
        label.getStyleClass().add("card-title");
        VBox box = new VBox(10, label, content);
        box.getStyleClass().add("card");
        box.setPadding(new Insets(16));
        return box;
    }

    private HBox buildLegendItem(String tone, String text) {
        Label swatch = new Label(tone);
        swatch.getStyleClass().addAll("legend-chip", "legend-chip-" + tone.toLowerCase());
        Label description = new Label(text);
        description.getStyleClass().add("legend-text");
        HBox row = new HBox(10, swatch, description);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private void bindEvents() {
        sizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> sizeLabel.setText(Integer.toString(newValue.intValue())));
        speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> speedValue.setText(String.format("%.1fx", newValue.doubleValue())));
        algorithmBox.valueProperty().addListener((observable, oldValue, newValue) -> renderCode());
        randomMode.selectedProperty().addListener((observable, oldValue, newValue) -> updateModeVisibility());
        manualMode.selectedProperty().addListener((observable, oldValue, newValue) -> updateModeVisibility());

        createButton.setOnAction(event -> createArray());
        deleteButton.setOnAction(event -> deleteArray());
        resetButton.setOnAction(event -> resetArray());
        sortButton.setOnAction(event -> sort(true));
        ascButton.setOnAction(event -> sort(true));
        descButton.setOnAction(event -> sort(false));
        stopButton.setOnAction(event -> togglePause());
    }

    private void updateModeVisibility() {
        manualInput.setManaged(manualMode.isSelected());
        manualInput.setVisible(manualMode.isSelected());
    }

    private void createArray() {
        stopAnimation();
        sortedIndices.clear();
        swapCount = 0;
        stepIndex = 0;
        swapValue.setText("0");

        int[] array = manualMode.isSelected() ? parseManualInput() : randomArray((int) sizeSlider.getValue());
        if (array.length == 0) {
            statusLabel.setText("Manual input is empty or invalid.");
            currentArray = new int[0];
            originalArray = new int[0];
            refreshChart(currentArray, -1, -1, null);
            return;
        }

        originalArray = array.clone();
        currentArray = array.clone();
        steps = List.of();
        statusLabel.setText("Array ready.");
        refreshChart(currentArray, -1, -1, null);
        updateArraySummary(currentArray);
        renderCode();
        updateButtons(false);
    }

    private void sort(boolean ascending) {
        if (currentArray.length == 0) {
            statusLabel.setText("Create an array first.");
            return;
        }

        stopAnimation();
        sortedIndices.clear();
        swapCount = 0;
        stepIndex = 0;
        swapValue.setText("0");

        int[] source = manualMode.isSelected() ? parseManualInput() : currentArray.clone();
        if (source.length == 0) {
            statusLabel.setText("Manual input is empty or invalid.");
            return;
        }

        originalArray = source.clone();
        currentArray = source.clone();
        updateArraySummary(currentArray);

        SortStrategy strategy = strategies.get(algorithmBox.getValue());
        steps = strategy.sort(source, ascending);
        if (steps.isEmpty()) {
            statusLabel.setText("No animation steps generated.");
            refreshChart(currentArray, -1, -1, null);
            return;
        }

        paused = false;
        statusLabel.setText(ascending ? "Sorting ascending..." : "Sorting descending...");
        stopButton.setText("Pause");
        playTimeline();
    }

    private void playTimeline() {
        updateButtons(true);
        timeline = new Timeline(new KeyFrame(Duration.millis(getDelayMillis()), event -> advanceStep()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void advanceStep() {
        if (stepIndex >= steps.size()) {
            finishAnimation();
            return;
        }

        Step step = steps.get(stepIndex++);
        currentArray = step.array.clone();

        if ("swap".equals(step.type)) {
            swapCount++;
            swapValue.setText(Integer.toString(swapCount));
        } else if ("sorted".equals(step.type)) {
            sortedIndices.add(step.i);
            sortedIndices.add(step.j);
        }

        refreshChart(currentArray, step.i, step.j, step.type);
        updateArraySummary(currentArray);
        renderCode();
    }

    private void finishAnimation() {
        stopAnimation();
        for (int i = 0; i < currentArray.length; i++) {
            sortedIndices.add(i);
        }
        refreshChart(currentArray, -1, -1, null);
        updateArraySummary(currentArray);
        statusLabel.setText("Completed.");
        updateButtons(false);
    }

    private void togglePause() {
        if (timeline == null) {
            return;
        }

        if (paused) {
            paused = false;
            timeline.play();
            stopButton.setText("Pause");
            statusLabel.setText("Animation resumed.");
        } else {
            paused = true;
            timeline.pause();
            stopButton.setText("Resume");
            statusLabel.setText("Animation paused.");
        }
    }

    private void resetArray() {
        stopAnimation();
        currentArray = originalArray.clone();
        sortedIndices.clear();
        steps = List.of();
        stepIndex = 0;
        swapCount = 0;
        swapValue.setText("0");
        refreshChart(currentArray, -1, -1, null);
        updateArraySummary(currentArray);
        renderCode();
        statusLabel.setText(currentArray.length == 0 ? "Nothing to reset." : "Array reset.");
        updateButtons(false);
    }

    private void deleteArray() {
        stopAnimation();
        originalArray = new int[0];
        currentArray = new int[0];
        steps = List.of();
        sortedIndices.clear();
        stepIndex = 0;
        swapCount = 0;
        swapValue.setText("0");
        refreshChart(currentArray, -1, -1, null);
        updateArraySummary(currentArray);
        renderCode();
        statusLabel.setText("Array deleted.");
        updateButtons(false);
    }

    private void stopAnimation() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
        paused = false;
        stopButton.setText("Pause");
    }

    private int[] parseManualInput() {
        String text = manualInput.getText();
        if (text == null || text.isBlank()) {
            return new int[0];
        }

        List<Integer> values = new ArrayList<>();
        for (String token : text.split("[,\\s]+")) {
            if (token.isBlank()) {
                continue;
            }
            try {
                values.add(Integer.parseInt(token.trim()));
            } catch (NumberFormatException ex) {
                return new int[0];
            }
        }

        return values.stream().mapToInt(Integer::intValue).toArray();
    }

    private int[] randomArray(int size) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = 10 + random.nextInt(271);
        }
        return array;
    }

    private void updateArraySummary(int[] values) {
        if (values.length == 0) {
            arraySummary.setText("No array");
            return;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(values[i]);
        }
        builder.append("]");
        arraySummary.setText(builder.toString());
    }

    private double getDelayMillis() {
        double speed = speedSlider.getValue();
        if (speed <= 0.5) {
            return 1200;
        }
        if (speed <= 1.0) {
            return 700;
        }
        if (speed <= 1.5) {
            return 400;
        }
        if (speed <= 2.0) {
            return 220;
        }
        if (speed <= 2.5) {
            return 120;
        }
        return 45;
    }

    private void refreshChart(int[] values, int firstIndex, int secondIndex, String stepType) {
        chartPane.getChildren().clear();
        if (values.length == 0) {
            Label empty = new Label("No data");
            empty.getStyleClass().add("empty-state");
            chartPane.getChildren().add(empty);
            return;
        }

        int max = Arrays.stream(values).max().orElse(1);
        for (int i = 0; i < values.length; i++) {
            VBox barBox = new VBox(8);
            barBox.setAlignment(Pos.BOTTOM_CENTER);
            barBox.getStyleClass().add("bar-box");
            double boxWidth = values.length > 14 ? 32 : 46;
            barBox.setPrefWidth(boxWidth);
            barBox.setMinWidth(boxWidth);

            Text valueLabel = new Text(Integer.toString(values[i]));
            valueLabel.getStyleClass().add("bar-value");

            Rectangle bar = new Rectangle();
            bar.setArcWidth(14);
            bar.setArcHeight(14);
            bar.setWidth(values.length > 14 ? 20 : 28);
            bar.setHeight(Math.max(24, (values[i] * 400.0) / max));
            bar.setFill(resolveBarColor(i, firstIndex, secondIndex, stepType));

            Label indexLabel = new Label(Integer.toString(i));
            indexLabel.getStyleClass().add("bar-index");
            barBox.getChildren().addAll(valueLabel, bar, indexLabel);
            chartPane.getChildren().add(barBox);
        }
    }

    private Color resolveBarColor(int index, int firstIndex, int secondIndex, String stepType) {
        if (sortedIndices.contains(index)) {
            return Color.web("#1f9d7a");
        }
        if ("swap".equals(stepType) && (index == firstIndex || index == secondIndex)) {
            return Color.web("#e85d3f");
        }
        if ("compare".equals(stepType) && (index == firstIndex || index == secondIndex)) {
            return Color.web("#f0b429");
        }
        return Color.web("#3b6cff");
    }

    private void renderCode() {
        codePane.getChildren().clear();
        String code = CodeSnippets.JAVA_CODE.getOrDefault(algorithmBox.getValue(), "// No code");
        int activeLine = resolveActiveLine();
        String[] lines = code.split("\\R");

        for (int i = 0; i < lines.length; i++) {
            Label line = new Label(String.format("%2d  %s", i + 1, lines[i]));
            line.getStyleClass().add("code-line");
            line.setWrapText(false);
            line.setMinWidth(Region.USE_PREF_SIZE);
            line.setMaxWidth(Region.USE_PREF_SIZE);
            if (i == activeLine) {
                line.getStyleClass().add("code-line-active");
            }
            codePane.getChildren().add(line);
        }
    }

    private int resolveActiveLine() {
        if (stepIndex == 0 || stepIndex > steps.size()) {
            return -1;
        }

        Step step = steps.get(stepIndex - 1);
        return switch (algorithmBox.getValue()) {
            case "bubble" -> mapStep(step, 3, 5, 1);
            case "selection" -> mapStep(step, 4, 9, 2);
            case "insertion" -> mapStep(step, 4, 6, 1);
            case "heap" -> mapStep(step, 1, 6, 5);
            case "quick" -> mapStep(step, 2, 3, 1);
            case "merge" -> mapStep(step, 4, 8, 1);
            default -> -1;
        };
    }

    private int mapStep(Step step, int compareLine, int swapLine, int sortedLine) {
        return switch (step.type) {
            case "compare" -> compareLine;
            case "swap" -> swapLine;
            case "sorted" -> sortedLine;
            default -> -1;
        };
    }

    private void updateButtons(boolean running) {
        boolean hasData = currentArray.length > 0;
        sortButton.setDisable(!hasData || running);
        ascButton.setDisable(!hasData || running);
        descButton.setDisable(!hasData || running);
        createButton.setDisable(running);
        deleteButton.setDisable(!hasData || running);
        resetButton.setDisable(!hasData);
        algorithmBox.setDisable(running);
        sizeSlider.setDisable(running || manualMode.isSelected());
        manualInput.setDisable(running);
        randomMode.setDisable(running);
        manualMode.setDisable(running);
        stopButton.setDisable(!running);
    }
}
