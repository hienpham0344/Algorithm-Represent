package com.example.main.ui;

import com.example.main.service.StackService;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.List;

public class StackVisualizerView extends BorderPane {

    private final StackService service = new StackService();

    private TextField inputField;
    private VBox      stackFrame;
    private TextArea  logArea;
    private TextArea  codeArea;
    private Label     statusText;
    private boolean   isSimulating = false;


    private static final String CODE_IDLE =
            "// Chọn một hành động để\n// trực quan hóa mã giả\n";

    private static final String CODE_PUSH =
            "// Push: Đẩy phần tử vào đỉnh ngăn xếp\n" +
                    "void push(int value) {\n" +
                    "    elements.add(value); // Thêm phần tử\n" +
                    "    top = value;         // Đỉnh mới cập nhật\n" +
                    "}\n";

    private static final String CODE_POP =
            "// Pop: Lấy phần tử ra khỏi đỉnh\n" +
                    "int pop() {\n" +
                    "    if (isEmpty()) return error;\n" +
                    "    int value = elements[size - 1];\n" +
                    "    elements.remove(size - 1);\n" +
                    "    return value;\n" +
                    "}\n";

    private static final String CODE_PEEK =
            "// Peek: Đọc thử giá trị ở đỉnh, không xóa\n" +
                    "int peek() {\n" +
                    "    if (isEmpty()) return error;\n" +
                    "    return elements[size - 1];\n" +
                    "}\n";

    private enum AnimType { NONE, PUSH, PEEK }

    public StackVisualizerView() {
        getStylesheets().add(
                getClass().getResource("/styles/stack.css").toExternalForm()
        );
        getStyleClass().add("stack-root");
        setLeft(buildLeftPanel());
        setCenter(buildVizArea());

        // init data
        service.push(15);
        service.push(30);
        service.push(45);
        redrawStack(AnimType.NONE, -1);

    }

    private VBox buildLeftPanel() {
        VBox panel = new VBox(14);
        panel.getStyleClass().add("left-panel");
        panel.setPrefWidth(265);
        panel.setMinWidth(265);
        panel.setMaxWidth(265);
        panel.setPadding(new Insets(22, 18, 22, 18));

        Label title = new Label("Ngăn xếp (Stack - LIFO)");
        title.getStyleClass().add("ds-title");
        title.setWrapText(true);

        Label desc = new Label(
                "Hoạt động theo nguyên lý 'Vào sau – Ra trước' " +
                        "(Last In, First Out). Hai thao tác cơ bản nhất là " +
                        "Push (Thêm vào đỉnh) và Pop (Lấy ra từ đỉnh)."
        );
        desc.getStyleClass().add("ds-desc");
        desc.setWrapText(true);

        Label sectionOp = new Label("TÁC VỤ THAO TÁC");
        sectionOp.getStyleClass().add("section-label");

        Label inputLabel = new Label("Giá trị phần tử (Số nguyên):");
        inputLabel.getStyleClass().add("input-label");

        inputField = new TextField();
        inputField.setPromptText("Ví dụ: 42");
        inputField.getStyleClass().add("input-field");
        inputField.setMaxWidth(Double.MAX_VALUE);

        Button btnPush  = makeBtn("Push (Đẩy vào)", "btn-push");
        Button btnPop   = makeBtn("Pop (Lấy ra)",   "btn-pop");
        Button btnPeek  = makeBtn("Xem đỉnh (Peek)","btn-peek");
        Button btnReset = makeBtn("Khởi tạo lại",   "btn-reset");

        HBox row1 = hRow(btnPush, btnPop);
        HBox row2 = hRow(btnPeek, btnReset);

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
                title, desc, divider(),
                sectionOp, inputLabel, inputField,
                row1, row2, divider(), statusBox
        );
        return panel;
    }

    private Region divider() {
        Region r = new Region();
        r.getStyleClass().add("divider-line");
        r.setMaxWidth(Double.MAX_VALUE);
        return r;
    }

    private Button makeBtn(String text, String styleClass) {
        Button b = new Button(text);
        b.getStyleClass().add(styleClass);
        b.setMaxWidth(Double.MAX_VALUE);
        return b;
    }

    private HBox hRow(Button a, Button b) {
        HBox row = new HBox(8, a, b);
        HBox.setHgrow(a, Priority.ALWAYS);
        HBox.setHgrow(b, Priority.ALWAYS);
        return row;
    }
    private StackPane buildVizArea() {
        stackFrame = new VBox(8);
        stackFrame.setAlignment(Pos.CENTER);
        stackFrame.getStyleClass().add("stack-frame");

        StackPane wrapper = new StackPane(stackFrame);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.getStyleClass().add("viz-area");
        return wrapper;
    }

    private void redrawStack(AnimType type, int animIdx) {
        stackFrame.getChildren().clear();

        List<Integer> items = service.toList();

        if (items.isEmpty()) {
            Label empty = new Label("Stack Rỗng");
            empty.getStyleClass().add("stack-empty-label");
            empty.setPadding(new Insets(24, 0, 24, 0));
            stackFrame.getChildren().add(empty);
            return;
        }

        for (int i = 0; i < items.size(); i++) {
            HBox row = buildCellRow(items.get(i), i == 0);
            stackFrame.getChildren().add(row);

            if (i == animIdx) {
                switch (type) {
                    case PUSH -> playPushAnim(row);
                    case PEEK -> playPeekAnim(row);
                    default   -> {}
                }
            }
        }

    }

    private HBox buildCellRow(int value, boolean isTop) {
        Label valLabel = new Label(String.valueOf(value));
        valLabel.getStyleClass().add(isTop ? "stack-cell-value-top" : "stack-cell-value");

        StackPane cell = new StackPane(valLabel);
        cell.getStyleClass().add(isTop ? "stack-cell-top" : "stack-cell");
        cell.setPrefWidth(190);
        cell.setPrefHeight(52);

        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        if (isTop) {
            Label badge = new Label("TOP (ĐỈNH)");
            badge.getStyleClass().add("stack-top-badge");
            row.getChildren().addAll(cell, badge);
        } else {
            row.getChildren().add(cell);
        }
        return row;
    }
    private void playPushAnim(HBox row) {
        row.setScaleX(0); row.setScaleY(0); row.setOpacity(0);
        Timeline tl = new Timeline(
                kf(0,   row.scaleXProperty(), 0,    Interpolator.LINEAR),
                kf(0,   row.scaleYProperty(), 0,    Interpolator.LINEAR),
                kf(0,   row.opacityProperty(), 0,   Interpolator.LINEAR),
                kf(200, row.scaleXProperty(), 1.15, Interpolator.EASE_OUT),
                kf(200, row.scaleYProperty(), 1.15, Interpolator.EASE_OUT),
                kf(200, row.opacityProperty(), 1.0, Interpolator.LINEAR),
                kf(320, row.scaleXProperty(), 1.0,  Interpolator.EASE_IN),
                kf(320, row.scaleYProperty(), 1.0,  Interpolator.EASE_IN)
        );
        tl.play();
    }

    private void playPeekAnim(HBox row) {
        if (row.getChildren().isEmpty()) return;
        var cell = row.getChildren().get(0);
        ScaleTransition st = new ScaleTransition(Duration.millis(400), cell);
        st.setFromX(1.0); st.setToX(1.1);
        st.setFromY(1.0); st.setToY(1.1);
        st.setCycleCount(4);
        st.setAutoReverse(true);
        st.setInterpolator(Interpolator.EASE_BOTH);
        st.play();
    }

    private void playPopAnim(HBox targetRow, Runnable onDone) {
        Timeline tl = new Timeline(
                kf(0,   targetRow.translateYProperty(), 0,   Interpolator.LINEAR),
                kf(0,   targetRow.scaleXProperty(),     1.0, Interpolator.LINEAR),
                kf(0,   targetRow.scaleYProperty(),     1.0, Interpolator.LINEAR),
                kf(0,   targetRow.opacityProperty(),    1.0, Interpolator.LINEAR),
                kf(350, targetRow.translateYProperty(), -28, Interpolator.EASE_IN),
                kf(350, targetRow.scaleXProperty(),    0.85, Interpolator.EASE_IN),
                kf(350, targetRow.scaleYProperty(),    0.85, Interpolator.EASE_IN),
                kf(350, targetRow.opacityProperty(),    0.0, Interpolator.EASE_IN)
        );
        tl.setOnFinished(e -> onDone.run());
        tl.play();
    }

    private KeyFrame kf(double ms, javafx.beans.value.WritableValue<Number> prop,
                        double val, Interpolator interp) {
        return new KeyFrame(Duration.millis(ms), new KeyValue(prop, val, interp));
    }
}
