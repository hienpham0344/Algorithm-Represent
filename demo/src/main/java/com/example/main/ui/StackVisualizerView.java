package com.example.main.ui;

import com.example.main.service.StackService;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class StackVisualizerView extends BorderPane {

    private final StackService service = new StackService();

    private TextField inputField;
    private VBox      stackFrame;
    private TextArea  logArea;
    private TextArea  codeArea;
    private Label     statusText;
    private TextArea  explanationArea;
    private boolean   isSimulating = false;

    private Slider    speedSlider;
    private Animation currentAnimation;
    private SequentialTransition batchTransition;
    private Button               btnPause;
//Dữ liệu mã giả tương ứng của các buttons để nạp vào codeArea(mã giả)
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

        explanationArea.setText(
                "• Ngăn xếp (Stack) được khởi tạo mặc định với 3 phần tử: 15, 30, 45.\n" +
                        "• Theo nguyên lý LIFO, phần tử 45 được đưa vào cuối cùng nên nó nằm ở trên cùng (Đỉnh Ngăn xếp).\n" +
                        "• Con trỏ TOP hiện tại đang chỉ thẳng vào phần tử số 45 này."
        );

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

        Label title = new Label("STACK (LIFO)");
        title.getStyleClass().add("ds-title");
        title.setWrapText(true);

        Label desc = new Label(
                "Operates on the 'Last In, First Out' (LIFO) principle. " +
                        "The two most basic operations are Push (add to the top) and Pop (remove from the top)."
        );
        desc.getStyleClass().add("ds-desc");
        desc.setWrapText(true);

        Label sectionOp = new Label("OPERATIONS");
        sectionOp.getStyleClass().add("section-label");

        Label inputLabel = new Label("Element Value:");
        inputLabel.getStyleClass().add("input-label");

        inputField = new TextField();
        inputField.setPromptText("Example: 42");
        inputField.getStyleClass().add("input-field");
        inputField.setMaxWidth(Double.MAX_VALUE);

        Button btnPush  = makeBtn("Push", "btn-push");
        Button btnPop   = makeBtn("Pop",   "btn-pop");
        Button btnPeek  = makeBtn("Peek","btn-peek");
        Button btnReset = makeBtn("Reset",   "btn-reset");

        btnPush.setOnAction(e  -> handlePush());
        btnPop.setOnAction(e   -> handlePop());
        btnPeek.setOnAction(e  -> handlePeek());
        btnReset.setOnAction(e -> handleReset());

        HBox row1 = hRow(btnPush, btnPop);
        HBox row2 = hRow(btnPeek, btnReset);

        btnPause = makeBtn("Pause", "btn-pause");
        btnPause.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnPause, Priority.ALWAYS);

        HBox pauseRow = new HBox(btnPause);
        pauseRow.setAlignment(Pos.CENTER);

        btnPause.setOnAction(e -> {
            if (batchTransition != null && isSimulating) {
                if (batchTransition.getStatus() == Animation.Status.RUNNING) {
                    batchTransition.pause();
                    btnPause.setText("Resume");
                    btnPause.setStyle("-fx-background-color: #EAB308; -fx-text-fill: #FFFFFF;");

                    setStatus("⏸ Đã tạm dừng mô phỏng.");
                } else if (batchTransition.getStatus() == Animation.Status.PAUSED) {
                    batchTransition.play();
                    btnPause.setText("Pause");
                    btnPause.setStyle("");
                    setStatus("▶ Đang tiếp tục mô phỏng loạt số...");
                }
            }
        });

        Label speedLabel = new Label("⏱ Speed:");
        speedLabel.getStyleClass().add("input-label");

        speedSlider = new Slider(0.2, 3.0, 1.0);
        speedSlider.getStyleClass().add("speed-slider");
        speedSlider.setShowTickMarks(true);
        speedSlider.setShowTickLabels(true);
        speedSlider.setMajorTickUnit(1.0);
        speedSlider.setBlockIncrement(0.1);
        speedSlider.setMaxWidth(Double.MAX_VALUE);

        speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (currentAnimation != null && currentAnimation.getStatus() == Animation.Status.RUNNING) {
                currentAnimation.setRate(newValue.doubleValue());
            }
        });
        VBox speedBox = new VBox(5, speedLabel, speedSlider);

        Label statusHeader = new Label("ℹ  SIMULATION STATUS");
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
                row1, row2,pauseRow,speedBox, divider(), statusBox
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
            Label empty = new Label("STACK EMPTY");
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
            Label badge = new Label("TOP");
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
        // 1. Khối Mã Giả (Pseudo-code)
        HBox codeHeader = panelHeader("<>  PSEUDO-CODE", "Java code");
        codeArea = new TextArea();
        codeArea.getStyleClass().add("code-area");
        codeArea.setText("// Chọn 1 hành động để trực quan hóa mã giả");
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

        // 2. Khối Nhật Ký (Activity Log)
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

        Region divider2 = new Region();
        divider2.getStyleClass().add("bottom-divider");

        // 3.Khối Giải thích
        HBox expHeader = panelHeader("?  EXPLANATION", "Hướng dẫn");
        explanationArea = new TextArea();
        explanationArea.getStyleClass().add("explanation-area");
        explanationArea.setEditable(false);
        explanationArea.setWrapText(true);
        VBox.setVgrow(explanationArea, Priority.ALWAYS);
        VBox.setMargin(explanationArea, new Insets(12, 12, 12, 12));
        VBox expBox = new VBox(expHeader, explanationArea);
        expBox.getStyleClass().add("bottom-section");
        HBox.setHgrow(expBox, Priority.ALWAYS);
        expBox.setPrefWidth(0);
        expBox.setMaxHeight(Double.MAX_VALUE);



        HBox bottom = new HBox(codeBox, divider, expBox, divider2, logBox);
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
        currentAnimation = tl;
        tl.setRate(speedSlider.getValue());
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

        currentAnimation = st;
        st.setRate(speedSlider.getValue());
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
        currentAnimation = tl;
        tl.setRate(speedSlider.getValue());
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
            setStatus("⚠ Vui lòng nhập một số nguyên hoặc một dãy số hợp lệ!", false);
            return;
        }

        // Tách chuỗi dựa vào dấu phẩy
        String[] tokens = raw.split(",");

        // Tạo một luồng thực thi tuần tự các hiệu ứng chèn phần tử
        batchTransition = new SequentialTransition();

        // Biến tạm để lưu danh sách các số hợp lệ đã parse thành công
        List<Integer> validValues = new ArrayList<>();

        // 1. Kiểm tra tính hợp lệ của toàn bộ chuỗi trước khi chạy mô phỏng
        for (String token : tokens) {
            String trimmedToken = token.trim();
            if (trimmedToken.isEmpty()) continue;
            try {
                int val = Integer.parseInt(trimmedToken);
                validValues.add(val);
            } catch (NumberFormatException ex) {
                setStatus("⚠ Dãy nhập vào chứa giá trị không hợp lệ: '" + trimmedToken + "'", false);
                return;
            }
        }

        if (validValues.isEmpty()) return;

        isSimulating = true;
        codeArea.setText(CODE_PUSH);
        inputField.clear(); // Xóa khung nhập sau khi đã nhận dữ liệu thành công

        // 2. Tạo chuỗi hiệu ứng đổ tuần tự từng số vào Ngăn xếp (Stack)
        for (int i = 0; i < validValues.size(); i++) {
            final int val = validValues.get(i);
            final boolean isLast = (i == validValues.size() - 1);

            // Đoạn phim tĩnh (PauseTransition) đóng vai trò kích hoạt logic cho từng phần tử
            PauseTransition step = new PauseTransition(Duration.millis(1400));

            step.setOnFinished(e -> {
                // Kiểm tra xem Ngăn xếp tại thời điểm chèn này đã bị đầy hay chưa
                if (service.size() >= 8) {
                    appendLog("✖ [Lỗi]: Không thể đẩy " + val + ". Stack đã đầy (Tối đa 8 phần tử).");
                    setStatus("Stack đầy. Dừng đẩy các phần tử còn lại.", false);

                    // Nếu gặp lỗi đầy bộ nhớ, hủy bỏ toàn bộ các bước chèn phía sau ngay lập tức
                    batchTransition.stop();
                    isSimulating = false;
                    return;
                }

                // Cập nhật lời giải thích tương ứng với phần tử đang được xử lý
                explanationArea.setText(
                        "• Thao tác Push đang xử lý giá trị: " + val + "\n" +
                                "• Bước 1: Kiểm tra giới hạn bộ nhớ (Hiện tại size = " + service.size() + ").\n" +
                                "• Bước 2: Cấp phát ô nhớ mới để lưu giá trị " + val + ".\n" +
                                "• Bước 3: Đẩy phần tử vào đỉnh. Theo nguyên lý LIFO, phần tử mới sẽ nằm trên cùng và che khuất phần tử cũ.\n" +
                                "• Bước 4: Con trỏ TOP được cập nhật bám sát lên để quản lý giá trị " + val + " vừa thêm."
                );

                appendLog("⚡ [Đang xử lý]: Đang Push(" + val + ") vào đỉnh Ngăn xếp...");
                setStatus("Đang Push phần tử " + val + "...");

                // Đưa dữ liệu vào service và vẽ lại giao diện với chỉ mục hoạt họa mới nhất
                service.push(val);
                redrawStack(AnimType.PUSH, 0);

                appendLog("✔ [Thành công]: Push " + val + " lên đỉnh Ngăn xếp thành công.");
                setStatus("Đã Push thành công phần tử " + val + ".", true);

                // Nếu đây là phần tử cuối cùng trong dãy, chính thức mở khóa mô phỏng
                if (isLast) {
                    isSimulating = false;
                    btnPause.setText("Pause");
                    btnPause.setStyle("");
                }
            });

            batchTransition.getChildren().add(step);
        }

        // Bắt đầu chạy chuỗi hiệu ứng chèn hàng loạt phần tử vào Stack
        batchTransition.setRate(speedSlider.getValue());
        batchTransition.play();
    }

    private void handlePop() {
        if (isSimulating) return;
        if (service.isEmpty()) {
            appendLog("✖ [Lỗi]: Ngăn xếp rỗng (Stack Empty). Không thể Pop!");
            setStatus("Ngăn xếp rỗng, không thể Pop.", false);
            return;
        }
        isSimulating = true;
        codeArea.setText(CODE_POP);
        int topVal = service.toList().get(0);
        explanationArea.setText(
                "• Bước 1: Kiểm tra xem Ngăn xếp có trống không. Nếu trống sẽ báo lỗi Underflow.\n" +
                        "• Bước 2: Định vị phần tử đang nằm ở đỉnh trên cùng (Giá trị hiện tại là: " + topVal + ").\n" +
                        "• Bước 3: Thực hiện nhấc phần tử " + topVal + " ra khỏi Ngăn xếp và giải phóng ô nhớ này.\n" +
                        "• Bước 4: Tự động dịch chuyển hạ con trỏ TOP xuống phần tử liền kề phía dưới."
        );
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
        explanationArea.setText(
                "• Điểm khác biệt: Lệnh Peek() chỉ kiểm tra (đọc trộm) giá trị đỉnh mà hoàn toàn không làm thay đổi cấu trúc dữ liệu.\n" +
                        "• Hệ thống lần theo vị trí con trỏ TOP để lấy ra giá trị tại ô trên cùng (Đang hiển thị số: " + peeked + ").\n" +
                        "• Phần tử " + peeked + " vẫn nằm nguyên vẹn trên Ngăn xếp, con trỏ TOP giữ nguyên vị trí."
        );
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
        explanationArea.setText(
                "• Hệ thống thực hiện dọn sạch bộ nhớ và nạp lại trạng thái ban đầu.\n" +
                        "• Ba phần tử mặc định (15, 30, 45) được đưa vào khung chứa.\n" +
                        "• Con trỏ TOP quay trở lại quản lý phần tử số 45 nằm ở đỉnh."
        );
        redrawStack(AnimType.NONE, -1);
        appendLog("[Nhật ký]: Đã làm mới Ngăn xếp về trạng thái mặc định.");
        setStatus("Đã khởi tạo lại.", true);
        inputField.clear();
    }
}
