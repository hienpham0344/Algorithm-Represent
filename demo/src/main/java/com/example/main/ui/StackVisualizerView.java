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

//Dữ liệu mã giả ứng của các buttons để nạp vào codeArea(mã giả)
    private static final String CODE_IDLE =
            "// Chọn một hành động để trực quan hóa mã giả\n";

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
        setBottom(buildBottomPanel());

        // init data


        service.push(15);
        service.push(30);
        service.push(45);
        redrawStack(AnimType.NONE, -1);

        appendLog("[Hệ Thống]: Đã tải xong ngăn xếp mô phỏng.");
        appendLog("[Hệ Thống]: Sẵn sàng hoạt động.");
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

        btnPush.setOnAction(e  -> handlePush());
        btnPop.setOnAction(e   -> handlePop());
        btnPeek.setOnAction(e  -> handlePeek());
        btnReset.setOnAction(e -> handleReset());

        HBox row1 = hRow(btnPush, btnPop);
        HBox row2 = hRow(btnPeek, btnReset);

        Label statusHeader = new Label("ℹ  TRẠNG THÁI MÔ PHỎNG");
        statusHeader.getStyleClass().add("status-header");
        statusText = new Label("Hệ thống đã sẵn sàng. Hãy chọn một thao tác.");
        statusText.getStyleClass().add("status-text");
        statusText.setWrapText(true);

        //Hộp thông báo trạng thái
        VBox statusBox = new VBox(6, statusHeader, statusText);
        statusBox.getStyleClass().add("status-box");
        statusBox.setPadding(new Insets(12, 14, 12, 14));
        VBox.setVgrow(statusBox, Priority.ALWAYS);  //Luôn kéo giãn hộp để lấp đầy khoản trống thừa

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


        a.setPrefWidth(0);
        b.setPrefWidth(0);

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
            Label empty = new Label("STACK RỖNG");
            empty.getStyleClass().add("stack-empty-label");

            VBox emptyBox = new VBox(empty);
            emptyBox.setAlignment(Pos.CENTER);

            // Khi rỗng, cố định chiều stackframe
            stackFrame.setPrefHeight(120);
            stackFrame.setMinHeight(120);
            stackFrame.setMaxHeight(120);

            stackFrame.getChildren().add(emptyBox);
            return;
        }
        //Tính toán chiều cao thích nghi
        double calculatedHeight = (items.size() * 52) + ((items.size() - 1) * 8) + 30;

        // Ép stackframe tuân theo calculatedHeight
        stackFrame.setPrefHeight(calculatedHeight);
        stackFrame.setMinHeight(calculatedHeight);
        stackFrame.setMaxHeight(calculatedHeight);

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

        cell.setMinWidth(190);
        cell.setMaxWidth(190);
        cell.setPrefWidth(190);
        cell.setPrefHeight(52);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER); // Căn giữa toàn bộ lưới này vào chính giữa màn hình
        grid.setHgap(10);              // Khoảng cách giữa các cột (thay cho khoảng cách HBox cũ)

        // Định nghĩa kích thước cố định cho 3 cột
        ColumnConstraints colLeft = new ColumnConstraints(80);
        ColumnConstraints colCenter = new ColumnConstraints(190);
        ColumnConstraints colRight = new ColumnConstraints(80);


        grid.getColumnConstraints().addAll(colLeft, colCenter, colRight);


        grid.add(cell, 1, 0);
        GridPane.setHalignment(cell, javafx.geometry.HPos.CENTER);


        if (isTop) {
            Label badge = new Label("TOP (ĐỈNH)");
            badge.getStyleClass().add("stack-top-badge");

            grid.add(badge, 2, 0);
            GridPane.setHalignment(badge, javafx.geometry.HPos.LEFT); // Căn lề trái trong cột của nó
            GridPane.setValignment(badge, javafx.geometry.VPos.CENTER);
        }


        HBox row = new HBox(grid);
        row.setAlignment(Pos.CENTER);
        row.setMaxWidth(Double.MAX_VALUE);

        return row;
    }
    private HBox buildBottomPanel() {
        Label codeHeader = new Label("Pseudo-code");
        codeHeader.getStyleClass().add("panel-header-label");
        Label codeLang = new Label("Java code");
        codeLang.getStyleClass().add("panel-lang-badge");
        Region sp1 = new Region();
        HBox.setHgrow(sp1, Priority.ALWAYS);
        HBox codeHdr = new HBox(codeHeader, sp1, codeLang);
        codeHdr.getStyleClass().add("panel-header-box");
        codeHdr.setAlignment(Pos.CENTER_LEFT);

        codeHdr.setPrefHeight(40);
        codeHdr.setMinHeight(40);
        codeHdr.setMaxHeight(40);

        codeArea = new TextArea(CODE_IDLE);
        codeArea.setEditable(false);
        codeArea.getStyleClass().add("code-area");
        VBox.setVgrow(codeArea, Priority.ALWAYS);

        VBox codePanel = new VBox(codeHdr, codeArea);
        codePanel.getStyleClass().add("bottom-panel");
        HBox.setHgrow(codePanel, Priority.ALWAYS);

        Region vdiv = new Region();
        vdiv.getStyleClass().add("bottom-divider");

        Label logHeader = new Label(" Nhật ký từng bước");
        logHeader.getStyleClass().add("panel-header-label");
        Button btnClear = new Button("🗑 Xóa");
        btnClear.getStyleClass().add("btn-clear-log");
        btnClear.setOnAction(e -> {
            logArea.clear();
            appendLog("[Hệ Thống]: Nhật ký đã được dọn sạch.");
        });
        Region sp2 = new Region();
        HBox.setHgrow(sp2, Priority.ALWAYS);
        HBox logHdr = new HBox(logHeader, sp2, btnClear);
        logHdr.getStyleClass().add("panel-header-box");
        logHdr.setAlignment(Pos.CENTER_LEFT);

        logHdr.setPrefHeight(40);
        logHdr.setMinHeight(40);
        logHdr.setMaxHeight(40);

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.getStyleClass().add("log-area");
        VBox.setVgrow(logArea, Priority.ALWAYS);

        VBox logPanel = new VBox(logHdr, logArea);
        logPanel.getStyleClass().add("bottom-panel");
        HBox.setHgrow(logPanel, Priority.ALWAYS);


        codePanel.setPrefWidth(0);
        logPanel.setPrefWidth(0);

        codePanel.setMaxHeight(Double.MAX_VALUE);
        logPanel.setMaxHeight(Double.MAX_VALUE);

        HBox bottom = new HBox(codePanel, vdiv, logPanel);
        bottom.setPrefHeight(210);

        bottom.setFillHeight(true);

        return bottom;
    }

    private void setStatus(String msg) { setStatus(msg, null); }
    private void setStatus(String msg, Boolean ok) {
        statusText.setText(msg);
        if (ok == null)  statusText.setStyle("-fx-text-fill: #C7D2FE;");
        else if (ok)     statusText.setStyle("-fx-text-fill: #4ADE80;");
        else             statusText.setStyle("-fx-text-fill: #F87171;");
    }
    private void appendLog(String line) { logArea.appendText(line + "\n"); }
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
    private void handlePush() {
        if (isSimulating) return;
        String raw = inputField.getText().trim();
        if (raw.isEmpty()) {
            setStatus("⚠ Vui lòng nhập một số nguyên hợp lệ!", false);
            return;
        }
        try {
            int val = Integer.parseInt(raw);
            if (service.size() >= 8) {
                appendLog("✖ [Lỗi]: Chiều cao ngăn xếp giới hạn 8 phần tử trong demo.");
                setStatus("Stack đã đầy (giới hạn demo: 8 phần tử).", false);
                return;
            }
            isSimulating = true;
            codeArea.setText(CODE_PUSH);
            appendLog("⚡ [Đang xử lý]: Đang Push(" + val + ") vào đỉnh Ngăn xếp...");
            setStatus("Đang chuẩn bị chèn vào...");
            service.push(val);
            redrawStack(AnimType.PUSH, 0);
            appendLog("✔ [Thành công]: Push " + val + " lên đỉnh Ngăn xếp thành công.");
            setStatus("Đã Push thành công.", true);
            inputField.clear();
            isSimulating = false;
        } catch (NumberFormatException ex) {
            setStatus("⚠ Giá trị không hợp lệ. Hãy nhập số nguyên.", false);
        }
    }

    private void handlePop() {
        if (isSimulating) return;
        if (service.isEmpty()) {
            appendLog("✖ [Lỗi]: Ngăn xếp rỗng (Stack Underflow). Không thể Pop!");
            setStatus("Ngăn xếp rỗng, không thể Pop.", false);
            return;
        }
        isSimulating = true;
        codeArea.setText(CODE_POP);
        int topVal = service.toList().get(0);
        appendLog("⚡ [Đang xử lý]: Đang Pop(" + topVal + ") ra khỏi Ngăn xếp...");
        setStatus("Đang trích xuất dữ liệu từ đỉnh (POP)...");

        stackFrame.getChildren().clear();
        List<Integer> items = service.toList();
        HBox topRow = null;
        for (int i = 0; i < items.size(); i++) {
            HBox row = buildCellRow(items.get(i), i == 0);
            stackFrame.getChildren().add(row);
            if (i == 0) topRow = row;
        }
        if (topRow == null) { isSimulating = false; return; }

        final HBox finalTopRow = topRow;
        playPopAnim(finalTopRow, () -> {
            service.pop();
            redrawStack(AnimType.NONE, -1);
            appendLog("✔ [Thành công]: Pop(" + topVal + ") ra khỏi Ngăn xếp thành công.");
            setStatus("Pop thành công.", true);
            isSimulating = false;
        });
    }

    private void handlePeek() {
        if (isSimulating) return;
        if (service.isEmpty()) {
            appendLog("✖ [Lỗi]: Ngăn xếp rỗng. Peek = NULL!");
            setStatus("Ngăn xếp rỗng.", false);
            return;
        }
        isSimulating = true;
        codeArea.setText(CODE_PEEK);
        int peeked = service.toList().get(0);
        appendLog("⚡ [Đang xử lý]: Đang đọc giá trị đỉnh...");
        setStatus("Kiểm tra giá trị đỉnh ngăn xếp...");
        redrawStack(AnimType.PEEK, 0);
        PauseTransition pause = new PauseTransition(Duration.millis(1800));
        pause.setOnFinished(e -> {
            appendLog("✔ [Thành công]: Giá trị đỉnh hiện tại: " + peeked);
            setStatus("Peek: " + peeked, true);
            isSimulating = false;
        });
        pause.play();
    }

    private void handleReset() {
        if (isSimulating) return;
        service.reset();
        service.push(15);
        service.push(30);
        service.push(45);
        codeArea.setText(CODE_IDLE);
        redrawStack(AnimType.NONE, -1);
        appendLog("[Nhật ký]: Đã làm mới Ngăn xếp về trạng thái mặc định.");
        setStatus("Đã khởi tạo lại.", true);
        inputField.clear();
    }
}
