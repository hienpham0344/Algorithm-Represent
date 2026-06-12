package com.example.main.ui;

import com.example.main.service.ArrayService;
import javafx.animation.*;
import javafx.geometry.Insets; // kiểu giống padding hoặc margin trong css
import javafx.geometry.Pos; // căn vị trí (vd: giữa, trái , phải)
import javafx.scene.Node; // class cha của button, label, textfield, vbox, hbox, pane
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class ArrayVisualizerView extends BorderPane {

    private final ArrayService service = new ArrayService();
    private final List<Button> actionButtons = new ArrayList<>();

    private TextField valueField;
    private TextField indexField;
    private HBox arrayFrame;
    private Label statusText;
    private TextArea codeArea;
    private TextArea explanationArea;
    private TextArea logArea;
    private Slider speedSlider;
    private Timeline animation;
    private int highlightedIndex = -1;
    private int foundIndex = -1;
    private String explanationText = "";

    private static final String CODE_IDLE =
            "// Choose an operation to view pseudo-code.\n";

    private static final String CODE_INSERT_END =
            "// Insert End: append a value to the array\n" +
                    "void insertEnd(int value) {\n" +
                    "    array[size] = value;\n" +
                    "    size++;\n" +
                    "}\n";

    private static final String CODE_DELETE_END =
            "// Delete End: remove the last value\n" +
                    "void deleteEnd() {\n" +
                    "    if (size == 0) return;\n" +
                    "    size--;\n" +
                    "}\n";

    private static final String CODE_INSERT_AT =
            "// Insert at Index: shift values right, then insert\n" +
                    "void insertAt(int index, int value) {\n" +
                    "    if (index < 0 || index > size) return;\n" +
                    "    for (int i = size; i > index; i--) {\n" +
                    "        array[i] = array[i - 1];\n" +
                    "    }\n" +
                    "    array[index] = value;\n" +
                    "    size++;\n" +
                    "}\n";

    private static final String CODE_DELETE_AT =
            "// Delete at Index: shift values left after removal\n" +
                    "void deleteAt(int index) {\n" +
                    "    if (index < 0 || index >= size) return;\n" +
                    "    for (int i = index; i < size - 1; i++) {\n" +
                    "        array[i] = array[i + 1];\n" +
                    "    }\n" +
                    "    size--;\n" +
                    "}\n";

    private static final String CODE_UPDATE_AT =
            "// Update at Index: replace one value\n" +
                    "void updateAt(int index, int value) {\n" +
                    "    if (index < 0 || index >= size) return;\n" +
                    "    array[index] = value;\n" +
                    "}\n";

    private static final String CODE_SEARCH =
            "// Search: scan values from left to right\n" +
                    "int search(int value) {\n" +
                    "    for (int i = 0; i < size; i++) {\n" +
                    "        if (array[i] == value) return i;\n" +
                    "    }\n" +
                    "    return -1;\n" +
                    "}\n";

    public ArrayVisualizerView() {
        getStylesheets().add(getClass().getResource("/styles/array.css").toExternalForm());
        getStyleClass().add("array-root");

        ScrollPane leftScrollPane = new ScrollPane(buildLeftPanel());
        leftScrollPane.getStyleClass().add("left-scroll-pane");
        leftScrollPane.setFitToWidth(true);
        leftScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        leftScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        leftScrollPane.setPrefWidth(285);
        leftScrollPane.setMinWidth(285);
        leftScrollPane.setMaxWidth(285);

        BorderPane mainContent = new BorderPane();
        mainContent.getStyleClass().add("array-main-content");
        // mainContent.setTop(buildMainToolbar());
        mainContent.setCenter(buildVizArea());
        mainContent.setBottom(buildBottomPanel());

        setLeft(leftScrollPane);
        setCenter(mainContent);

        redrawArray();
        setStatus("Ready. Choose an array operation.");
        setCode(CODE_IDLE);
        setExplanation(
                "• The sample array contains 7 values stored in consecutive indexed positions.\n" +
                "• Each value can be accessed directly through its index, from 0 to size - 1.\n" +
                "• Choose an operation to see how the array changes step by step."
        );
        addLog("[SYSTEM] Array visualizer loaded.");
    }

    private VBox buildLeftPanel() {
        VBox panel = new VBox(14);
        panel.getStyleClass().add("left-panel");
        panel.setPrefWidth(285);
        panel.setMinWidth(285);
        panel.setMaxWidth(285);
        panel.setPadding(new Insets(22, 18, 22, 18));

        Label title = new Label("ARRAY");
        title.getStyleClass().add("ds-title");
        title.setWrapText(true);

        Label desc = new Label(
                "Arrays store values in indexed positions. Access by index is O(1), " +
                        "while inserting or deleting in the middle costs O(n) because values must shift."
        );
        desc.getStyleClass().add("ds-desc");
        desc.setWrapText(true);

        Label sectionOp = new Label("OPERATIONS");
        sectionOp.getStyleClass().add("section-label");

        Label valueLabel = new Label("Element Value");
        valueLabel.getStyleClass().add("input-label");

        valueField = new TextField();
        valueField.setPromptText("Example: 42");
        valueField.getStyleClass().add("input-field");
        valueField.setMaxWidth(Double.MAX_VALUE);
        VBox valueBox = new VBox(5, valueLabel, valueField);

        Label indexLabel = new Label("Index");
        indexLabel.getStyleClass().add("input-label");

        indexField = new TextField();
        indexField.setPromptText("Example: 2");
        indexField.getStyleClass().add("input-field");
        indexField.setMaxWidth(Double.MAX_VALUE);
        VBox indexBox = new VBox(5, indexLabel, indexField);

        Region operationGap = new Region();
        operationGap.setPrefHeight(5);

        Region speedGap = new Region();
        speedGap.setPrefHeight(5);

        Button btnInsertEnd = makeBtn("Insert End", "btn-array-insert");
        Button btnDeleteEnd = makeBtn("Delete End", "btn-array-delete");
        Button btnInsertAt = makeBtn("Insert at Index", "btn-array-insert");
        Button btnDeleteAt = makeBtn("Delete at Index", "btn-array-delete");
        Button btnUpdateAt = makeBtn("Update at Index", "btn-array-update");
        Button btnSearch = makeBtn("Search", "btn-array-search");
        Button btnRandomize = makeBtn("Randomize", "btn-array-random");
        Button btnReset = makeBtn("Reset", "btn-reset");

        btnInsertEnd.setOnAction(e -> handleInsertEnd());
        btnDeleteEnd.setOnAction(e -> handleDeleteEnd());
        btnInsertAt.setOnAction(e -> handleInsertAt());
        btnDeleteAt.setOnAction(e -> handleDeleteAt());
        btnUpdateAt.setOnAction(e -> handleUpdateAt());
        btnSearch.setOnAction(e -> handleSearch());
        btnRandomize.setOnAction(e -> handleRandomize());
        btnReset.setOnAction(e -> handleReset());

        Label speedLabel = new Label("Speed:");
        speedLabel.getStyleClass().add("input-label");

        speedSlider = new Slider(0.2, 3.0, 1.0);
        speedSlider.getStyleClass().add("speed-slider");
        speedSlider.setShowTickMarks(true);
        speedSlider.setShowTickLabels(true);
        speedSlider.setMajorTickUnit(1.0);
        speedSlider.setBlockIncrement(0.1);
        speedSlider.setMaxWidth(Double.MAX_VALUE);

        speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (animation != null && animation.getStatus() == Animation.Status.RUNNING) {
                animation.setRate(newValue.doubleValue());
            }
        });
        VBox speedBox = new VBox(5, speedLabel, speedSlider);
        speedBox.setMaxWidth(229);

        Label statusHeader = new Label("SIMULATION STATUS");
        statusHeader.getStyleClass().add("status-header");
        statusText = new Label();
        statusText.getStyleClass().add("status-text");
        statusText.setWrapText(true);

        VBox statusBox = new VBox(6, statusHeader, statusText);
        statusBox.getStyleClass().add("status-box");
        statusBox.setPadding(new Insets(12, 14, 12, 14));
        VBox.setVgrow(statusBox, Priority.ALWAYS);  //Luôn kéo giãn hộp để lấp đầy khoản trống thừa

        panel.getChildren().addAll(
                title, desc, divider(),
                sectionOp, valueBox,
                indexBox, operationGap,
                hRow(btnInsertEnd, btnDeleteEnd),
                hRow(btnInsertAt, btnDeleteAt),
                hRow(btnUpdateAt, btnSearch),
                hRow(btnRandomize, btnReset),
                speedGap, speedBox, divider(), statusBox
        );
        return panel;
    }

//    private HBox buildMainToolbar() {
//        Label speedLabel = new Label("Speed");
//        speedLabel.getStyleClass().add("input-label");
//        HBox.setMargin(speedLabel, new Insets(0, 0, 18, 0));
//
//        speedSlider = new Slider(0.2, 3.0, 1.0);
//        speedSlider.getStyleClass().add("speed-slider");
//        speedSlider.setShowTickMarks(true);
//        speedSlider.setShowTickLabels(true);
//        speedSlider.setMajorTickUnit(1.0);
//        speedSlider.setBlockIncrement(0.1);
//        speedSlider.setPrefWidth(210);
//
//        speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
//            if (animation != null && animation.getStatus() == Animation.Status.RUNNING) {
//                animation.setRate(newValue.doubleValue());
//            }
//        });
//
//        Region spacer = new Region();
//        HBox.setHgrow(spacer, Priority.ALWAYS);
//        HBox toolbar = new HBox(10, spacer,  speedLabel, speedSlider);
//        toolbar.getStyleClass().add("array-toolbar");
//        toolbar.setAlignment(Pos.CENTER_RIGHT);
//        return toolbar;
//    }

    private HBox makeInfoCard(String label, String value) {
        Label labelText = new Label(label);
        labelText.getStyleClass().add("array-info-label");

        Label valueText = new Label(value);
        valueText.getStyleClass().add("array-info-value");

        VBox box = new VBox(4, labelText, valueText);
        box.getStyleClass().add("array-info-card");
        box.setAlignment(Pos.CENTER);
        box.setMaxWidth(Double.MAX_VALUE);

        HBox wrapper = new HBox(box);
        wrapper.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(box, Priority.ALWAYS);
        HBox.setHgrow(wrapper, Priority.ALWAYS);
        return wrapper;
    }

    private Node buildVizArea() {
        arrayFrame = new HBox(10);
        arrayFrame.setAlignment(Pos.CENTER);
        arrayFrame.getStyleClass().add("array-frame");

        StackPane wrapper = new StackPane(arrayFrame);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.getStyleClass().add("viz-area");

        ScrollPane scrollPane = new ScrollPane(wrapper);
        scrollPane.getStyleClass().add("array-viz-scroll");
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return scrollPane;
    }

    private HBox buildBottomPanel() {
        HBox codeHeader = panelHeader("<>  PSEUDO-CODE", "Java code");
        codeArea = new TextArea();
        codeArea.getStyleClass().add("code-area");
        codeArea.setEditable(false);
        codeArea.setWrapText(false);
        VBox.setVgrow(codeArea, Priority.ALWAYS);
        VBox.setMargin(codeArea, new Insets(12, 12, 12, 12));
        VBox codeBox = new VBox(codeHeader, codeArea);
        codeBox.getStyleClass().add("bottom-section");
        HBox.setHgrow(codeBox, Priority.ALWAYS);
        codeBox.setPrefWidth(0);
        codeBox.setMaxHeight(Double.MAX_VALUE);

        Region divider = new Region();
        divider.getStyleClass().add("bottom-divider");

        HBox explanationHeader = panelHeader("?  EXPLANATION", "Guide");
        explanationArea = new TextArea();
        explanationArea.getStyleClass().add("explanation-area");
        explanationArea.setEditable(false);
        explanationArea.setWrapText(true);
        VBox.setVgrow(explanationArea, Priority.ALWAYS);
        VBox.setMargin(explanationArea, new Insets(12));
        VBox explanationBox = new VBox(explanationHeader, explanationArea);
        explanationBox.getStyleClass().add("bottom-section");
        HBox.setHgrow(explanationBox, Priority.ALWAYS);
        explanationBox.setPrefWidth(0);
        explanationBox.setMaxHeight(Double.MAX_VALUE);

        Region divider2 = new Region();
        divider2.getStyleClass().add("bottom-divider");

        HBox logHeader = panelHeader(">_  ACTIVITY LOG", null);
        Button clearBtn = new Button("Clear");
        clearBtn.getStyleClass().add("btn-clear-log");
        clearBtn.setOnAction(e -> logArea.clear());
        logHeader.getChildren().add(clearBtn);

        logArea = new TextArea();
        logArea.getStyleClass().add("log-area");
        logArea.setEditable(false);
        logArea.setWrapText(true);
        VBox.setVgrow(logArea, Priority.ALWAYS);
        VBox.setMargin(logArea, new Insets(12, 12, 12, 12));
        VBox logBox = new VBox(logHeader, logArea);
        logBox.getStyleClass().add("bottom-section");
        HBox.setHgrow(logBox, Priority.ALWAYS);
        logBox.setPrefWidth(0);
        logBox.setMaxHeight(Double.MAX_VALUE);

        HBox bottom = new HBox(codeBox, divider, explanationBox, divider2, logBox);
        bottom.getStyleClass().add("bottom-dock");
        bottom.setPrefHeight(210);
        bottom.setMinHeight(180);
        bottom.setFillHeight(true);
        return bottom;
    }

    private HBox panelHeader(String title, String badgeText) {
        HBox header = new HBox();
        header.getStyleClass().add("panel-header-box");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPrefHeight(40);
        header.setMinHeight(40);
        header.setMaxHeight(40);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("panel-header-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(titleLabel, spacer);

        if (badgeText != null) {
            Label badge = new Label(badgeText);
            badge.getStyleClass().add("panel-lang-badge");
            header.getChildren().add(badge);
        }
        return header;
    }

    private void handleInsertEnd() {
        Integer value = readValue();
        if (value == null) return;

        setCode(CODE_INSERT_END);
        stopAnimation();
        setControlsDisabled(true);
        foundIndex = -1;
        setStatus("Appending value at the end...");
        setExplanation(
                "• Insert End places " + value + " in the first free position.\n" +
                "• No existing value needs to move because the new value is appended after the current last element.\n" +
                "• The array size increases from " + service.size() + " to " + (service.size() + 1) + "."
        );
        addLog("[STEP] Appending value at the end...");

        ArrayService.Result result = service.insertEnd(value);
        highlightedIndex = result.index() == null ? -1 : result.index();
        redrawArray();

        animation = new Timeline(new KeyFrame(Duration.millis(320), e -> {
            stopAnimation();
            afterAction(result);
            setControlsDisabled(false);
        }));
        animation.setRate(speedSlider.getValue());
        animation.play();
    }

    private void handleDeleteEnd() {
        setCode(CODE_DELETE_END);
        foundIndex = -1;
        int lastIndex = service.size() - 1;
        setExplanation(lastIndex >= 0
                ? "• Delete End targets the last occupied index, " + lastIndex + ".\n" +
                  "• The value at this index is removed without shifting any earlier values.\n" +
                  "• The array size decreases by one."
                : "• Delete End cannot run because the array has no values to remove.");
        runOperationAnimation("Removing the last value...", lastIndex >= 0 ? List.of(lastIndex) : List.of(), () -> {
            ArrayService.Result result = service.deleteEnd();
            highlightedIndex = -1;
            afterAction(result);
        });
    }

    private void handleInsertAt() {
        Integer value = readValue();
        Integer index = readIndex();
        if (value == null || index == null) return;

        setCode(CODE_INSERT_AT);
        foundIndex = -1;
        if (index < 0 || index > service.size()) {
            setExplanation(
                    "• Insert at Index accepts an index from 0 through " + service.size() + ".\n" +
                    "• Index " + index + " is outside that valid range, so the array remains unchanged."
            );
            afterAction(service.insertAt(index, value));
            return;
        }

        setExplanation(
                "• To insert " + value + " at index " + index + ", values from that index onward must shift one position right.\n" +
                "• Shifting starts at the end to avoid overwriting existing values.\n" +
                "• After space is available, the new value is written at index " + index + "."
        );
        runOperationAnimation("Shifting values to the right...", indicesDescending(service.size() - 1, index), () -> {
            ArrayService.Result result = service.insertAt(index, value);
            highlightedIndex = result.success() && result.index() != null ? result.index() : -1;
            afterAction(result);
        });
    }

    private void handleDeleteAt() {
        Integer index = readIndex();
        if (index == null) return;

        setCode(CODE_DELETE_AT);
        foundIndex = -1;
        if (index < 0 || index >= service.size()) {
            setExplanation(
                    "• Delete at Index requires an occupied index from 0 through " + (service.size() - 1) + ".\n" +
                    "• Index " + index + " is invalid, so no value is removed."
            );
            afterAction(service.deleteAt(index));
            return;
        }

        setExplanation(
                "• The value at index " + index + " is selected for removal.\n" +
                "• Every value to its right shifts one position left to close the gap.\n" +
                "• The final duplicate slot is discarded and the array size decreases by one."
        );
        runOperationAnimation("Shifting values to the left...", indicesAscending(index, service.size() - 1), () -> {
            ArrayService.Result result = service.deleteAt(index);
            highlightedIndex = -1;
            afterAction(result);
        });
    }

    private void handleUpdateAt() {
        Integer value = readValue();
        Integer index = readIndex();
        if (value == null || index == null) return;

        setCode(CODE_UPDATE_AT);
        foundIndex = -1;
        if (index < 0 || index >= service.size()) {
            setExplanation(
                    "• Update requires an occupied index from 0 through " + (service.size() - 1) + ".\n" +
                    "• Index " + index + " is invalid, so the array remains unchanged."
            );
            afterAction(service.updateAt(index, value));
            return;
        }

        int oldValue = service.toList().get(index);
        setExplanation(
                "• Direct index access locates position " + index + " in O(1) time.\n" +
                "• The old value " + oldValue + " is replaced by " + value + ".\n" +
                "• No other array element needs to move."
        );
        runOperationAnimation("Updating one indexed value...", List.of(index), () -> {
            ArrayService.Result result = service.updateAt(index, value);
            highlightedIndex = result.success() && result.index() != null ? result.index() : -1;
            afterAction(result);
        });
    }

    private void handleSearch() {
        Integer value = readValue();
        if (value == null) return;

        setCode(CODE_SEARCH);
        if (service.isEmpty()) {
            setExplanation("• Linear search cannot scan an empty array because there are no values to compare.");
            afterAction(service.search(value));
            return;
        }

        int targetIndex = service.indexOf(value);
        int end = targetIndex >= 0 ? targetIndex : service.size() - 1;
        setExplanation(
                "• Linear search compares " + value + " with each array element from left to right.\n" +
                "• The scan stops as soon as a matching value is found.\n" +
                "• If every index is checked without a match, the search reports that the value is absent."
        );
        runOperationAnimation("Scanning values from left to right...", indicesAscending(0, end), () -> {
            ArrayService.Result result = service.search(value);
            foundIndex = result.success() && result.index() != null ? result.index() : -1;
            highlightedIndex = -1;
            afterAction(result);
        });
    }

    private void handleRandomize() {
        stopAnimation();
        ArrayService.Result result = service.randomize();
        highlightedIndex = -1;
        foundIndex = -1;
        setCode(CODE_IDLE);
        setExplanation(
                "• Randomize clears the current values and generates a new array with 7 random integers.\n" +
                "• The indexes are rebuilt consecutively from 0 to 6.\n" +
                "• This creates a fresh data set for trying the array operations."
        );
        afterAction(result);
    }

    private void handleReset() {
        stopAnimation();
        ArrayService.Result result = service.reset();
        highlightedIndex = -1;
        foundIndex = -1;
        valueField.clear();
        indexField.clear();
        setCode(CODE_IDLE);
        setExplanation(
                "• Reset discards the current array and restores the original sample values.\n" +
                "• Highlighted and search-result states are cleared.\n" +
                "• The array is ready for a new simulation."
        );
        afterAction(result);
    }

    private void runOperationAnimation(String message, List<Integer> indices, Runnable onFinished) {
        stopAnimation();
        setControlsDisabled(true);
        foundIndex = -1;
        setStatus(message);
        addLog("[STEP] " + message);

        if (indices.isEmpty()) {
            onFinished.run();
            setControlsDisabled(false);
            return;
        }

        final int[] step = {0};
        animation = new Timeline(new KeyFrame(Duration.millis(320), e -> {
            if (step[0] >= indices.size()) {
                stopAnimation();
                onFinished.run();
                setControlsDisabled(false);
                return;
            }

            highlightedIndex = indices.get(step[0]++);
            explanationArea.setText(explanationText +
                    "\n\n• Current step: processing index " + highlightedIndex + ".");
            redrawArray();
        }));
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.setRate(speedSlider.getValue());
        animation.play();
    }

    private void stopAnimation() {
        if (animation != null) {
            animation.stop();
            animation = null;
        }
    }

    private List<Integer> indicesAscending(int start, int end) {
        List<Integer> indices = new ArrayList<>();
        for (int i = Math.max(0, start); i <= end; i++) {
            indices.add(i);
        }
        return indices;
    }

    private List<Integer> indicesDescending(int start, int end) {
        List<Integer> indices = new ArrayList<>();
        for (int i = start; i >= Math.max(0, end); i--) {
            indices.add(i);
        }
        return indices;
    }

    private void afterAction(ArrayService.Result result) {
        setStatus(result.message());
        addLog((result.success() ? "[OK] " : "[ERROR] ") + result.message());
        redrawArray();
    }

    private Integer readValue() {
        String text = valueField.getText().trim();
        if (text.isEmpty()) {
            setStatus("Enter a value first.");
            setExplanation("• This operation needs an integer value. Enter a value, then run the operation again.");
            addLog("[ERROR] Missing value.");
            return null;
        }

        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            setStatus("Value must be an integer.");
            setExplanation("• Array values must be integers. \"" + text + "\" cannot be converted to an integer.");
            addLog("[ERROR] Invalid value: " + text);
            return null;
        }
    }

    private Integer readIndex() {
        String text = indexField.getText().trim();
        if (text.isEmpty()) {
            setStatus("Enter an index first.");
            setExplanation("• This operation needs an index. Enter an integer index, then run the operation again.");
            addLog("[ERROR] Missing index.");
            return null;
        }

        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            setStatus("Index must be an integer.");
            setExplanation("• An array index must be an integer. \"" + text + "\" is not a valid index.");
            addLog("[ERROR] Invalid index: " + text);
            return null;
        }
    }

    private void redrawArray() {
        arrayFrame.getChildren().clear();

        List<Integer> items = service.toList();
        if (items.isEmpty()) {
            Label empty = new Label("Array is empty");
            empty.getStyleClass().add("array-empty-label");
            arrayFrame.getChildren().add(empty);
            return;
        }

        for (int i = 0; i < items.size(); i++) {
            arrayFrame.getChildren().add(buildArrayCell(items.get(i), i, i == highlightedIndex, i == foundIndex));
        }
    }

    private VBox buildArrayCell(int value, int index, boolean highlighted, boolean found) {
        Label valueLabel = new Label(String.valueOf(value));
        valueLabel.getStyleClass().add("array-cell-value");

        StackPane cell = new StackPane(valueLabel);
        cell.getStyleClass().add(found ? "array-cell-found" : highlighted ? "array-cell-highlight" : "array-cell");
        cell.setPrefWidth(58);
        cell.setPrefHeight(52);

        Label indexLabel = new Label("[" + index + "]");
        indexLabel.getStyleClass().add("array-index-label");

        VBox box = new VBox(7, cell, indexLabel);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private Button makeBtn(String text, String styleClass) {
        Button button = new Button(text);
        button.getStyleClass().addAll("array-op-button", styleClass);
        button.setWrapText(true);
        button.setAlignment(Pos.CENTER);
        actionButtons.add(button);
        return button;
    }

    private HBox hRow(Button a, Button b) {
        HBox row = new HBox(8, a, b);
        a.setPrefSize(118, 38);
        b.setPrefSize(118, 38);
        return row;
    }

    private Region divider() {
        Region region = new Region();
        region.getStyleClass().add("divider-line");
        region.setMaxWidth(Double.MAX_VALUE);
        return region;
    }

    private void setControlsDisabled(boolean disabled) {
        actionButtons.forEach(button -> button.setDisable(disabled));
        valueField.setDisable(disabled);
        indexField.setDisable(disabled);
    }

    private void setStatus(String message) {
        statusText.setText(message);
    }

    private void setCode(String code) {
        codeArea.setText(code);
    }

    private void setExplanation(String explanation) {
        explanationText = explanation;
        if (explanationArea != null) {
            explanationArea.setText(explanation);
        }
    }

    private void addLog(String message) {
        if (logArea == null) return;
        logArea.appendText(message + "\n");
        logArea.positionCaret(logArea.getText().length());
    }
}
