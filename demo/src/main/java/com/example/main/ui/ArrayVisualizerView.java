package com.example.main.ui;

import com.example.main.service.ArrayService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class ArrayVisualizerView extends BorderPane {

    private final ArrayService service = new ArrayService();

    private TextField valueField;
    private TextField indexField;
    private HBox arrayFrame;
    private Label statusText;
    private TextArea codeArea;
    private TextArea logArea;
    private int highlightedIndex = -1;

    private static final String CODE_IDLE =
            "// Chọn một thao tác để xem mã giả.\n";

    private static final String CODE_INSERT_END =
            "// Insert End: Thêm phần tử vào cuối mảng\n" +
                    "void insertEnd(int value) {\n" +
                    "    array[size] = value;\n" +
                    "    size++;\n" +
                    "}\n";

    private static final String CODE_DELETE_END =
            "// Delete End: Xóa phần tử cuối mảng\n" +
                    "void deleteEnd() {\n" +
                    "    if (size == 0) return;\n" +
                    "    size--;\n" +
                    "}\n";

    private static final String CODE_INSERT_AT =
            "// Insert at Index: Chèn phần tử tại vị trí index\n" +
                    "void insertAt(int index, int value) {\n" +
                    "    if (index < 0 || index > size) return;\n" +
                    "    for (int i = size; i > index; i--) {\n" +
                    "        array[i] = array[i - 1];\n" +
                    "    }\n" +
                    "    array[index] = value;\n" +
                    "    size++;\n" +
                    "}\n";

    private static final String CODE_DELETE_AT =
            "// Delete at Index: Xóa phần tử tại vị trí index\n" +
                    "void deleteAt(int index) {\n" +
                    "    if (index < 0 || index >= size) return;\n" +
                    "    for (int i = index; i < size - 1; i++) {\n" +
                    "        array[i] = array[i + 1];\n" +
                    "    }\n" +
                    "    size--;\n" +
                    "}\n";

    private static final String CODE_UPDATE_AT =
            "// Update at Index: Cập nhật giá trị tại vị trí index\n" +
                    "void updateAt(int index, int value) {\n" +
                    "    if (index < 0 || index >= size) return;\n" +
                    "    array[index] = value;\n" +
                    "}\n";

    private static final String CODE_SEARCH =
            "// Search: Tìm kiếm giá trị trong mảng\n" +
                    "int search(int value) {\n" +
                    "    for (int i = 0; i < size; i++) {\n" +
                    "        if (array[i] == value) return i;\n" +
                    "    }\n" +
                    "    return -1;\n" +
                    "}\n";

    public ArrayVisualizerView() {
        getStylesheets().add(
                getClass().getResource("/styles/stack.css").toExternalForm()
        );
        getStylesheets().add(
                getClass().getResource("/styles/array.css").toExternalForm()
        );

        getStyleClass().add("array-root");

        // giúp cho screen cuộn xuống được
        ScrollPane leftScrollPane = new ScrollPane(buildLeftPanel());
        leftScrollPane.getStyleClass().add("left-scroll-pane");
        leftScrollPane.setFitToWidth(true);
        leftScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        leftScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        leftScrollPane.setPrefWidth(285);
        leftScrollPane.setMinWidth(285);
        leftScrollPane.setMaxWidth(285);

        setLeft(buildLeftPanel());
        setCenter(buildVizArea());

        //để cho bottom panel không kéo dài qua left panel
        BorderPane mainContent = new BorderPane();
        mainContent.setCenter(buildVizArea());
        mainContent.setBottom(buildBottomPanel());
        //setBottom(buildBottomPanel());

        redrawArray();
        setStatus("[Hệ Thống]: Đã tải xong mảng mô phỏng.");
        setCode(CODE_IDLE);
        addLog("[Hệ Thống] Sẵn sàng hoạt đông");
    }

    private VBox buildLeftPanel() {
        VBox panel = new VBox(14);
        panel.getStyleClass().add("left-panel");
        panel.setPrefWidth(285);
        panel.setMinWidth(285);
        panel.setMaxWidth(285);
        panel.setPadding(new Insets(22, 18, 22, 18));

        Label title = new Label("Mảng (Array)");
        title.getStyleClass().add("ds-title");

        Label desc = new Label(
                "Các phần tử được lưu liền kề nhau trong bộ nhớ. " +
                        "Truy cập nhanh qua chỉ số (O(1)), " +
                        "nhưng việc chèn/xóa ở vị trí bất kỳ sẽ tốn O(N) " +
                        "vì phải dịch chuyển các phần tử phía sau."
        );
        desc.getStyleClass().add("ds-desc");
        desc.setWrapText(true);

        HBox infoRow = new HBox(8);
        infoRow.getChildren().addAll(
                makeInfoCard("ACCESS", "O(1)"),
                makeInfoCard("SEARCH", "O(n)"),
                makeInfoCard("SPACE", "O(n)")
        );

        Label sectionOp = new Label("TÁC VỤ THAO TÁC");
        sectionOp.getStyleClass().add("section-label");

        Label valueLabel = new Label("Giá trị phần tử:");
        valueLabel.getStyleClass().add("input-label");

        valueField = new TextField();
        valueField.setPromptText("Ví dụ: 42");
        valueField.getStyleClass().add("input-field");
        valueField.setMaxWidth(Double.MAX_VALUE);

        Label indexLabel = new Label("Vị trí index:");
        indexLabel.getStyleClass().add("input-label");

        indexField = new TextField();
        indexField.setPromptText("Ví dụ: 2");
        indexField.getStyleClass().add("input-field");
        indexField.setMaxWidth(Double.MAX_VALUE);

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

        Label statusHeader = new Label("ℹ  TRẠNG THÁI MÔ PHỎNG");
        statusHeader.getStyleClass().add("status-header");
        statusText = new Label("Hệ thống đã sẵn sàng. Hãy chọn một thao tác.");
        statusText.getStyleClass().add("status-text");
        statusText.setWrapText(true);

        VBox statusBox = new VBox(6, statusHeader, statusText);
        statusBox.getStyleClass().add("status-box");
        statusBox.setPadding(new Insets(12, 14, 12, 14));
        VBox.setVgrow(statusBox, Priority.ALWAYS);

        panel.getChildren().addAll(
                title, desc, infoRow, divider(),
                sectionOp, valueLabel, valueField,
                indexLabel, indexField,
                hRow(btnInsertEnd, btnDeleteEnd), hRow(btnInsertAt, btnDeleteAt),
                hRow(btnUpdateAt, btnSearch), hRow(btnRandomize, btnReset),
                divider(), statusBox
        );
        return panel;
    }

    private HBox makeInfoCard(String label, String value) {
        Label labelText = new Label(label);
        labelText.getStyleClass().add("array-info-label");

        Label valueText = new Label(value);
        valueText.getStyleClass().add("array-info-value");

        VBox box = new VBox(4, labelText, valueText);
        box.getStyleClass().add("array-info-card");
        box.setAlignment(Pos.CENTER);

        HBox wrapper = new HBox(box);
        HBox.setHgrow(wrapper, Priority.ALWAYS);
        wrapper.setMaxWidth(Double.MAX_VALUE);

        box.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(box, Priority.ALWAYS);

        return wrapper;
    }

    private StackPane buildVizArea() {
        arrayFrame = new HBox(10);
        arrayFrame.setAlignment(Pos.CENTER);
        arrayFrame.getStyleClass().add("array-frame");

        StackPane wrapper = new StackPane(arrayFrame);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.getStyleClass().add("viz-area");

        return wrapper;
    }

    private HBox buildBottomPanel() {
        HBox bottom = new HBox();
        bottom.getStyleClass().add("bottom-panel");
        bottom.setPrefHeight(190);
        bottom.setMinHeight(170);

        VBox codeBox = new VBox();
        codeBox.setPrefWidth(520);
        codeBox.getStyleClass().add("array-bottom-box");

        HBox codeHeader = new HBox();
        codeHeader.getStyleClass().add("panel-header-box");
        codeHeader.setAlignment(Pos.CENTER_LEFT);

        Label codeTitle = new Label("<>  PSEUDO-CODE");
        codeTitle.getStyleClass().add("panel-header-label");

        Region codeSpacer = new Region();
        HBox.setHgrow(codeSpacer, Priority.ALWAYS);

        Label badge = new Label("C++ style");
        badge.getStyleClass().add("panel-lang-badge");

        codeHeader.getChildren().addAll(codeTitle, codeSpacer, badge);

        codeArea = new TextArea();
        codeArea.getStyleClass().add("code-area");
        codeArea.setEditable(false);
        codeArea.setWrapText(false);
        VBox.setVgrow(codeArea, Priority.ALWAYS);

        codeBox.getChildren().addAll(codeHeader, codeArea);

        Region divider = new Region();
        divider.getStyleClass().add("bottom-divider");

        VBox logBox = new VBox();
        logBox.getStyleClass().add("array-bottom-box");
        HBox.setHgrow(logBox, Priority.ALWAYS);

        HBox logHeader = new HBox();
        logHeader.getStyleClass().add("panel-header-box");
        logHeader.setAlignment(Pos.CENTER_LEFT);

        Label logTitle = new Label(">_  ACTIVITY LOG");
        logTitle.getStyleClass().add("panel-header-label");

        Region logSpacer = new Region();
        HBox.setHgrow(logSpacer, Priority.ALWAYS);

        Button clearBtn = new Button("Clear");
        clearBtn.getStyleClass().add("btn-clear-log");
        clearBtn.setOnAction(e -> logArea.clear());

        logHeader.getChildren().addAll(logTitle, logSpacer, clearBtn);

        logArea = new TextArea();
        logArea.getStyleClass().add("log-area");
        logArea.setEditable(false);
        logArea.setWrapText(true);
        VBox.setVgrow(logArea, Priority.ALWAYS);

        logBox.getChildren().addAll(logHeader, logArea);

        bottom.getChildren().addAll(codeBox, divider, logBox);

        return bottom;
    }

    private void handleInsertEnd() {
        Integer value = readValue();
        if (value == null) return;

        ArrayService.Result result = service.insertEnd(value);
        highlightedIndex = result.index() == null ? -1 : result.index();

        setCode(CODE_INSERT_END);
        afterAction(result);
    }

    private void handleDeleteEnd() {
        ArrayService.Result result = service.deleteEnd();
        highlightedIndex = -1;

        setCode(CODE_DELETE_END);
        afterAction(result);
    }

    private void handleInsertAt() {
        Integer value = readValue();
        Integer index = readIndex();

        if (value == null || index == null) return;

        ArrayService.Result result = service.insertAt(index, value);
        highlightedIndex = result.success() && result.index() != null ? result.index() : -1;

        setCode(CODE_INSERT_AT);
        afterAction(result);
    }

    private void handleDeleteAt() {
        Integer index = readIndex();

        if (index == null) return;

        ArrayService.Result result = service.deleteAt(index);
        highlightedIndex = -1;

        setCode(CODE_DELETE_AT);
        afterAction(result);
    }

    private void handleUpdateAt() {
        Integer value = readValue();
        Integer index = readIndex();

        if (value == null || index == null) return;

        ArrayService.Result result = service.updateAt(index, value);
        highlightedIndex = result.success() && result.index() != null ? result.index() : -1;

        setCode(CODE_UPDATE_AT);
        afterAction(result);
    }

    private void handleSearch() {
        Integer value = readValue();

        if (value == null) return;

        ArrayService.Result result = service.search(value);
        highlightedIndex = result.success() && result.index() != null ? result.index() : -1;

        setCode(CODE_SEARCH);
        afterAction(result);
    }

    private void handleRandomize() {
        ArrayService.Result result = service.randomize();
        highlightedIndex = -1;

        setCode(CODE_IDLE);
        afterAction(result);
    }

    private void handleReset() {
        ArrayService.Result result = service.reset();
        highlightedIndex = -1;

        valueField.clear();
        indexField.clear();

        setCode(CODE_IDLE);
        afterAction(result);
    }

    private void afterAction(ArrayService.Result result) {
        setStatus(result.message());
        addLog((result.success() ? "[OK] " : "[ERROR] ") + result.message());
        redrawArray();
    }

    private Integer readValue() {
        String text = valueField.getText().trim();

        if (text.isEmpty()) {
            setStatus("Vui lòng nhập giá trị phần tử.");
            addLog("[ERROR] Chưa nhập giá trị phần tử.");
            return null;
        }

        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            setStatus("Giá trị phần tử phải là số nguyên.");
            addLog("[ERROR] Giá trị phần tử không hợp lệ: " + text);
            return null;
        }
    }

    private Integer readIndex() {
        String text = indexField.getText().trim();

        if (text.isEmpty()) {
            setStatus("Vui lòng nhập index.");
            addLog("[ERROR] Chưa nhập index.");
            return null;
        }

        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            setStatus("Index phải là số nguyên.");
            addLog("[ERROR] Index không hợp lệ: " + text);
            return null;
        }
    }

    private void redrawArray() {
        arrayFrame.getChildren().clear();

        List<Integer> items = service.toList();

        if (items.isEmpty()) {
            Label empty = new Label("Array rỗng");
            empty.getStyleClass().add("stack-empty-label");
            arrayFrame.getChildren().add(empty);
            return;
        }

        for (int i = 0; i < items.size(); i++) {
            VBox cellBox = buildArrayCell(items.get(i), i, i == highlightedIndex);
            arrayFrame.getChildren().add(cellBox);
        }
    }

    private VBox buildArrayCell(int value, int index, boolean highlighted) {
        Label valueLabel = new Label(String.valueOf(value));
        valueLabel.getStyleClass().add("array-cell-value");

        StackPane cell = new StackPane(valueLabel);
        cell.getStyleClass().add(highlighted ? "array-cell-highlight" : "array-cell");
        cell.setPrefWidth(58);
        cell.setPrefHeight(52);

        Label indexLabel = new Label("[" + index + "]");
        indexLabel.getStyleClass().add("array-index-label");

        VBox box = new VBox(7, cell, indexLabel);
        box.setAlignment(Pos.CENTER);

        return box;
    }

    private Button makeBtn(String text, String styleClass) {
        Button b = new Button(text);
        // để chữ dài kh lam cho btn bị to ra
        b.getStyleClass().addAll("array-op-button", styleClass);
        //b.setMaxWidth(Double.MAX_VALUE);
        b.setWrapText(true);
        b.setAlignment(Pos.CENTER);
        return b;
    }

    private HBox hRow(Button a, Button b) {
        HBox row = new HBox(8, a, b);
        // HBox.setHgrow(a, Priority.ALWAYS);
        // HBox.setHgrow(b, Priority.ALWAYS);
        // để cho các btn bằng nhau không bị co giãn theo chữ
        a.setPrefWidth(118);
        b.setPrefWidth(118);
        a.setMinWidth(118);
        b.setMinWidth(118);
        a.setMaxWidth(118);
        b.setMaxWidth(118);

        a.setPrefHeight(38);
        b.setPrefHeight(38);
        a.setMinHeight(38);
        b.setMinHeight(38);
        a.setMaxHeight(38);
        b.setMaxHeight(38);

        return row;
    }

    private Region divider() {
        Region r = new Region();
        r.getStyleClass().add("divider-line");
        r.setMaxWidth(Double.MAX_VALUE);
        return r;
    }

    private void setStatus(String message) {
        statusText.setText(message);
    }

    private void setCode(String code) {
        codeArea.setText(code);
    }

    private void addLog(String message) {
        if (logArea == null) return;
        logArea.appendText(message + "\n");
    }
}