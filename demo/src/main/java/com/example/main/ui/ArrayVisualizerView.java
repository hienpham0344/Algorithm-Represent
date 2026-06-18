package com.example.main.ui;

import com.example.main.service.ArrayService;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.geometry.Pos; // căn vị trí (vd: giữa, trái , phải)
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrayVisualizerView {

    private final ArrayService service = new ArrayService();
    private final List<Button> actionButtons = new ArrayList<>();

    @FXML private TextField valueField;
    @FXML private TextField indexField;
    @FXML private HBox arrayFrame;
    @FXML private Label statusText;
    @FXML private TextArea codeArea;
    @FXML private TextArea explanationArea;
    @FXML private TextArea logArea;
    @FXML private Button btnInsertEnd;
    @FXML private Button btnDeleteEnd;
    @FXML private Button btnInsertAt;
    @FXML private Button btnDeleteAt;
    @FXML private Button btnUpdateAt;
    @FXML private Button btnSearch;
    @FXML private Button btnRandomize;
    @FXML private Button btnReset;
    @FXML private Button btnClearLog;
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

    @FXML
    private void initialize() {
        initializeControls();
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

    private void initializeControls() {
        actionButtons.addAll(Arrays.asList(
                btnInsertEnd,
                btnDeleteEnd,
                btnInsertAt,
                btnDeleteAt,
                btnUpdateAt,
                btnSearch,
                btnRandomize,
                btnReset
        ));
        btnInsertEnd.setOnAction(e -> handleInsertEnd());
        btnDeleteEnd.setOnAction(e -> handleDeleteEnd());
        btnInsertAt.setOnAction(e -> handleInsertAt());
        btnDeleteAt.setOnAction(e -> handleDeleteAt());
        btnUpdateAt.setOnAction(e -> handleUpdateAt());
        btnSearch.setOnAction(e -> handleSearch());
        btnRandomize.setOnAction(e -> handleRandomize());
        btnReset.setOnAction(e -> handleReset());
        btnClearLog.setOnAction(e -> handleClearLog());
    }

    @FXML
    private void handleInsertEnd() {
        List<Integer> values = readValues();
        if (values == null) return;

        setCode(CODE_INSERT_END);
        if (service.isFull()) {
            setExplanation(
                    "• The array has reached its maximum capacity of " + ArrayService.MAX_CAPACITY + " values.\n" +
                    "• Delete a value before inserting another one."
            );
            afterAction(service.insertEnd(values.get(0)));
            return;
        }

        stopAnimation();
        setControlsDisabled(true);
        foundIndex = -1;
        valueField.clear();
        setStatus("Appending values at the end...");
        setExplanation(
                "• Insert End accepts one integer or a comma-separated list.\n" +
                "• Each value is appended sequentially without shifting existing values.\n" +
                "• The operation stops if the array reaches its capacity of " + ArrayService.MAX_CAPACITY + " values."
        );
        addLog("[STEP] Appending " + values.size() + " value(s) at the end...");

        final int[] step = {0};
        animation = new Timeline(new KeyFrame(Duration.millis(320), e -> {
            if (step[0] >= values.size() || service.isFull()) {
                boolean stoppedByCapacity = step[0] < values.size();
                int insertedCount = step[0];
                stopAnimation();
                highlightedIndex = -1;
                redrawArray();

                if (stoppedByCapacity) {
                    setStatus("Array full. Inserted " + insertedCount + " value(s); remaining values were skipped.");
                    addLog("[ERROR] Array reached the maximum capacity of " + ArrayService.MAX_CAPACITY + ".");
                } else {
                    setStatus("Inserted " + insertedCount + " value(s) at the end.");
                    addLog("[OK] Batch Insert End completed.");
                }
                setControlsDisabled(false);
                return;
            }

            int value = values.get(step[0]++);
            ArrayService.Result result = service.insertEnd(value);
            highlightedIndex = result.index() == null ? -1 : result.index();
            explanationArea.setText(explanationText +
                    "\n\n• Current step: appending " + value + " at index " + highlightedIndex + ".");
            addLog("[OK] " + result.message());
            redrawArray();
        }));
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
    }

    @FXML
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

    @FXML
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

        if (service.isFull()) {
            setExplanation(
                    "• The array has reached its maximum capacity of " + ArrayService.MAX_CAPACITY + " values.\n" +
                    "• Delete a value before inserting another one."
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

    @FXML
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
        runOperationAnimation("Shifting values to the left...", indicesAscending(index, service.size() - 2), () -> {
            ArrayService.Result result = service.deleteAt(index);
            highlightedIndex = -1;
            afterAction(result);
        });
    }

    @FXML
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

    @FXML
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

    @FXML
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

    @FXML
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

    private List<Integer> readValues() {
        String text = valueField.getText().trim();
        if (text.isEmpty()) {
            setStatus("Enter one or more values first.");
            setExplanation("• Insert End accepts integers separated by commas, for example: 10, 20, 30.");
            addLog("[ERROR] Missing value.");
            return null;
        }

        List<Integer> values = new ArrayList<>();
        for (String token : text.split(",", -1)) {
            String value = token.trim();
            if (value.isEmpty()) {
                setStatus("The value list contains an empty item.");
                setExplanation("• Every item between commas must be an integer. Example: 10, 20, 30.");
                addLog("[ERROR] Invalid value list: " + text);
                return null;
            }

            try {
                values.add(Integer.parseInt(value));
            } catch (NumberFormatException e) {
                setStatus("Value must be an integer: " + value);
                setExplanation("• Array values must be integers. \"" + value + "\" cannot be converted to an integer.");
                addLog("[ERROR] Invalid value: " + value);
                return null;
            }
        }
        return values;
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

    @FXML
    private void handleClearLog() {
        logArea.clear();
    }
    @FXML
    private void handleImport() {

        if (animation != null && animation.getStatus() == Animation.Status.RUNNING) {
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn file dữ liệu Array");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File selectedFile = fileChooser.showOpenDialog(valueField.getScene().getWindow());

        if (selectedFile != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append(" ");
                }

                // Xử lý dữ liệu: lọc khoảng trắng và chuyển thành dấu phẩy chuẩn format
                String formattedData = content.toString()
                        .replaceAll("\\s+", ",")
                        .replaceAll(",+", ",")
                        .replaceAll("^,|,$", "");

                valueField.setText(formattedData);

                // Cập nhật trạng thái thông báo và log
                if (statusText != null) {
                    statusText.setText("Đã import: " + selectedFile.getName());
                }
                if (logArea != null) {
                    logArea.appendText("📂 [Import]: Đã nạp dữ liệu từ file " + selectedFile.getName() + "\n");
                }

            } catch (IOException ex) {
                if (statusText != null) {
                    statusText.setText("Lỗi khi đọc file!");
                }
                if (logArea != null) {
                    logArea.appendText("✖ [Error]: Không thể đọc file: " + ex.getMessage() + "\n");
                }
            }
        }
    }
}
