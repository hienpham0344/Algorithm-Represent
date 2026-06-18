package com.example.main.ui;

import com.example.main.service.StackService;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javafx.stage.FileChooser;

public class StackVisualizerView extends BorderPane {

    private final StackService service = new StackService();

    private TextField inputField;
    private VBox      stackFrame;
    private ScrollPane scrollPane;
    private TextArea  logArea;
    private TextArea  codeArea;
    private Label     statusText;
    private TextArea  explanationArea;
    private boolean   isSimulating = false;

    private Slider    speedSlider;
    private Animation currentAnimation;
    private SequentialTransition batchTransition; // Animation chuỗi (nhiều phần tử)
    private Button btnPause;
//Dữ liệu mã giả tương ứng của các buttons để nạp vào codeArea(mã giả)
    private static final String CODE_IDLE =
            "// Select an action to visualize the Stack pseudocode\n";

    private static final String CODE_PUSH =
            "// Push: Pushes an element onto the top of the stack\n" +
                    "void push(int value) {\n" +
                    "    elements.add(value); // Add element\n" +
                    "    top = value;         // Update new top\n" +
                    "}\n";

    private static final String CODE_POP =
            "// Pop: Removes an element from the top of the stack\n" +
                    "int pop() {\n" +
                    "    if (isEmpty()) return error;\n" +
                    "    int value = elements[size - 1];\n" +
                    "    elements.remove(size - 1);\n" +
                    "    return value;\n" +
                    "}\n";

    private static final String CODE_PEEK =
            "// Peek: Views the top element without removing it\n" +
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

        VBox leftContent = buildLeftPanel();
        leftContent.setPrefWidth(280);
        leftContent.setMinWidth(280);
        leftContent.setMaxWidth(280);
        ScrollPane leftScrollPane = new ScrollPane(leftContent);
        leftScrollPane.setFitToWidth(true);
        leftScrollPane.setFitToHeight(true);
        leftScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        leftScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        leftScrollPane.getStyleClass().add("left-scroll-pane");
        leftScrollPane.setPrefWidth(280);
        leftScrollPane.setMinWidth(280);
        leftScrollPane.setMaxWidth(280);

        setLeft(leftScrollPane);
        setCenter(buildVizArea());
        setBottom(buildBottomPanel());

        // init data


        service.push(15);
        service.push(30);
        service.push(45);
        redrawStack(AnimType.NONE, -1);

        explanationArea.setText(
                "• The stack is initialized by default with 3 elements: 15, 30, and 45.\n" +
                        "• According to the LIFO (Last In, First Out) principle, the element 45 was added last, so it sits at the very top (Top of the Stack).\n" +
                        "• The TOP pointer is currently pointing directly to this element 45."
        );

        appendLog("[System]: Simulation stack loaded successfully.");
        appendLog("[System]: Ready for operation.");
    }

    private VBox buildLeftPanel() {
        VBox panel = new VBox(14);
        panel.getStyleClass().add("left-panel");
        panel.setPrefWidth(260);
        panel.setMinWidth(260);
        panel.setMaxWidth(260);
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
        //inputField.setMaxWidth(Double.MAX_VALUE);

        Button btnImport = new Button("Import");
        btnImport.getStyleClass().add("import-btn");
        btnImport.setOnAction(e -> handleImport());

        HBox inputWrapper = new HBox(5, inputField, btnImport);

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

                    setStatus("⏸ Simulation paused.");
                } else if (batchTransition.getStatus() == Animation.Status.PAUSED) {
                    batchTransition.play();
                    btnPause.setText("Pause");
                    btnPause.setStyle("");
                    setStatus("▶ Resuming batch push simulation...");
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
                currentAnimation.setRate(newValue.doubleValue()); //Thay tốc độ ngay lập tức
            }
            if (batchTransition != null && batchTransition.getStatus() != Animation.Status.STOPPED) {
                batchTransition.setRate(newValue.doubleValue());
            }


        });
        VBox speedBox = new VBox(5, speedLabel, speedSlider);

        Label statusHeader = new Label("ℹ  SIMULATION STATUS");
        statusHeader.getStyleClass().add("status-header");
        statusText = new Label("System is ready. Please select an operation.");        statusText.getStyleClass().add("status-text");
        statusText.setWrapText(true);

        //Hộp thông báo trạng thái
        VBox statusBox = new VBox(6, statusHeader, statusText);
        statusBox.getStyleClass().add("status-box");
        statusBox.setPadding(new Insets(12, 14, 12, 14));
        VBox.setVgrow(statusBox, Priority.ALWAYS);  //Luôn kéo giãn hộp để lấp đầy khoản trống thừa

        Button btnNotes = makeBtn("Notes", "note-button");
        btnNotes.setMaxWidth(Double.MAX_VALUE);
        btnNotes.setOnAction(e -> NoteDialog.show(getScene().getWindow(), "Stack"));

        panel.getChildren().addAll(
                title, desc, divider(),
                sectionOp, inputLabel, inputWrapper,
                row1, row2,pauseRow, divider(), statusBox, btnNotes
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

        StackPane frameWrapper = new StackPane(stackFrame);
        frameWrapper.setAlignment(Pos.CENTER);


        VBox centerWrapper = new VBox(frameWrapper);
        centerWrapper.setAlignment(Pos.CENTER);
        centerWrapper.setPadding(new Insets(50, 0, 50, 0)); // Tạo khoảng đệm trên/dưới thoáng đãng

        scrollPane = new ScrollPane(centerWrapper);

        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("viz-scroll-pane");

        StackPane wrapper = new StackPane(scrollPane);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.getStyleClass().add("viz-area");
        wrapper.setMinHeight(200);

        VBox.setVgrow(wrapper, Priority.ALWAYS);
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
        // Tính chiều cao cần thiết: mỗi cell cao 52px, spacing 8px, padding 30px
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
        //Label hiển thị số
        Label valLabel = new Label(String.valueOf(value));
        valLabel.getStyleClass().add(isTop ? "stack-cell-value-top" : "stack-cell-value");

        // Ô vuông chứa số (StackPane = xếp chồng, label nằm chính giữa)
        StackPane cell = new StackPane(valLabel);
        cell.getStyleClass().add(isTop ? "stack-cell-top" : "stack-cell");
        cell.setMinWidth(190);
        cell.setMaxWidth(190);
        cell.setPrefWidth(190);
        cell.setPrefHeight(52);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER); // Căn giữa toàn bộ lưới này vào chính giữa màn hình
        grid.setHgap(10);              // Khoảng cách giữa các cột (thay cho khoảng cách HBox cũ)

        ColumnConstraints colLeft = new ColumnConstraints();
        colLeft.setPercentWidth(25); // Chiếm 25% độ rộng hàng cho vùng căn lề trái

        ColumnConstraints colCenter = new ColumnConstraints(190); // Ô dữ liệu giữ nguyên 190px chuẩn

        ColumnConstraints colRight = new ColumnConstraints();
        colRight.setPercentWidth(25); // Chiếm 25% độ rộng hàng cho vùng chứa chữ TOP bên phải

        grid.getColumnConstraints().addAll(colLeft, colCenter, colRight);

        grid.add(cell, 1, 0);
        GridPane.setHalignment(cell, javafx.geometry.HPos.CENTER);

        if (isTop) {
            Label badge = new Label("TOP");
            badge.getStyleClass().add("stack-top-badge");

            grid.add(badge, 2, 0);
            GridPane.setHalignment(badge, javafx.geometry.HPos.LEFT);
            GridPane.setValignment(badge, javafx.geometry.VPos.CENTER);
        }

        HBox row = new HBox(grid);
        row.setAlignment(Pos.CENTER);

        HBox.setHgrow(grid, Priority.ALWAYS);
        row.setMaxWidth(Double.MAX_VALUE);

        return row;
    }
    private HBox buildBottomPanel() {
        // 1. Khối Mã Giả (Pseudo-code)
        HBox codeHeader = panelHeader("<>  PSEUDO-CODE", "Java code");
        codeArea = new TextArea();
        codeArea.getStyleClass().add("code-area");
        codeArea.setText("// Select an action to visualize the Stack pseudocode");
        codeArea.setEditable(false);
        codeArea.setWrapText(true);
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
        HBox.setMargin(clearBtn, new Insets(0, 14, 0, 0));
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
        HBox expHeader = panelHeader("?  EXPLANATION", "Guide");
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
        bottom.setMinHeight(160);
        bottom.setMaxHeight(260);
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
        st.setFromX(1.0); st.setToX(1.5);
        st.setFromY(1.0); st.setToY(1.5);
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
            setStatus("⚠ Please enter a valid integer or sequence of numbers!", false);            return;
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
                setStatus("⚠ Input sequence contains an invalid value: '" + trimmedToken + "'", false);
                return;
            }
        }

        if (validValues.isEmpty()) return;

        isSimulating = true;
        codeArea.setText(CODE_PUSH);
        //inputField.clear(); // Xóa khung nhập sau khi đã nhận dữ liệu thành công

        // 2. Tạo chuỗi hiệu ứng đổ tuần tự từng số vào Ngăn xếp (Stack)
        for (int i = 0; i < validValues.size(); i++) {
            final int val = validValues.get(i);
            final boolean isLast = (i == validValues.size() - 1);

            // Đoạn phim tĩnh (PauseTransition) đóng vai trò kích hoạt logic cho từng phần tử
            PauseTransition step = new PauseTransition(Duration.millis(1400));

            step.setOnFinished(e -> {
                // Kiểm tra xem Ngăn xếp tại thời điểm chèn này đã bị đầy hay chưa
                if (service.size() >= 20) {
                    appendLog("✖ [Error]: Cannot push " + val + ". Stack is full (Max 20 elements).");
                    setStatus("Stack is full. Stopping remaining pushes.", false);

                    // Nếu gặp lỗi đầy bộ nhớ, hủy bỏ toàn bộ các bước chèn phía sau ngay lập tức
                    batchTransition.stop();
                    isSimulating = false;
                    return;
                }

                // Cập nhật lời giải thích tương ứng với phần tử đang được xử lý
                explanationArea.setText(
                        "• Push operation is processing value: " + val + "\n" +
                                "• Step 1: Check memory limits (Current size = " + service.size() + ").\n" +
                                "• Step 2: Allocate a new memory slot to store the value " + val + ".\n" +
                                "• Step 3: Push the element onto the top. According to the LIFO principle, the new element will sit on top and obscure the older elements.\n" +
                                "• Step 4: The TOP pointer is updated to point directly to the newly added value " + val + "."
                );

                appendLog("⚡ [Processing]: Pushing Push(" + val + ") to the top of the Stack...");
                setStatus("Pushing element " + val + "...");

                // Đưa dữ liệu vào service và vẽ lại giao diện với chỉ mục hoạt họa mới nhất
                service.push(val);
                redrawStack(AnimType.PUSH, 0);


                appendLog("✔ [Success]: Pushed " + val + " onto the Stack top successfully.");
                setStatus("Successfully pushed element " + val + ".", true);

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
            appendLog("✖ [Error]: Stack is empty. Cannot Pop!");
            setStatus("Stack is empty, cannot Pop.", false);
            return;
        }
        isSimulating = true;
        codeArea.setText(CODE_POP);
        int topVal = service.toList().get(0);
        explanationArea.setText(
                "• Step 1: Check if the Stack is empty. If it is, an Underflow error will be reported.\n" +
                        "• Step 2: Locate the element currently at the top (Current value is: " + topVal + ").\n" +
                        "• Step 3: Remove the element " + topVal + " from the Stack and free up this memory slot.\n" +
                        "• Step 4: Automatically move the TOP pointer down to the immediate element below."
        );
        appendLog("⚡ [Processing]: Popping " + topVal + " from the Stack...");
        setStatus("Extracting data from the top (POP)...");

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
            appendLog("✔ [Success]: Popped " + topVal + " from the Stack successfully.");
            setStatus("Pop successful.", true);
            isSimulating = false;
        });
    }

    private void handlePeek() {
        if (isSimulating) return;
        if (service.isEmpty()) {
            appendLog("✖ [Error]: Stack is empty. Peek = NULL!");
            setStatus("Stack is empty.", false);
            return;
        }
        isSimulating = true;
        codeArea.setText(CODE_PEEK);
        int peeked = service.toList().get(0);
        explanationArea.setText(
                "• Key Difference: The Peek() command only checks (peeks at) the top value without altering the data structure at all.\n" +
                        "• The system tracks the TOP pointer position to retrieve the value at the topmost slot (Currently showing: " + peeked + ").\n" +
                        "• The element " + peeked + " remains intact on the Stack; the TOP pointer stays in place."
        );
        appendLog("⚡ [Processing]: Reading top value...");
        setStatus("Checking stack top value...");
        redrawStack(AnimType.PEEK, 0);
        PauseTransition pause = new PauseTransition(Duration.millis(1800));
        pause.setOnFinished(e -> {
            appendLog("✔ [Success]: Current top value: " + peeked);
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
                "• The system clears the memory and reloads the initial state.\n" +
                        "• Three default elements (15, 30, 45) are loaded into the container.\n" +
                        "• The TOP pointer returns to manage element 45 at the top."
        );
        redrawStack(AnimType.NONE, -1);
        appendLog("[Log]: Stack has been reset to the default state.");
        setStatus("Reinitialized successfully.", true);
        inputField.clear();
    }
    private void handleImport() {
        if (isSimulating) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select file containing Stack data");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File selectedFile = fileChooser.showOpenDialog(this.getScene().getWindow());

        if (selectedFile != null) {
            // Dùng BufferedReader để đọc file - Cách này tương thích với mọi bản Java
            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(selectedFile))) {

                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append(" ");
                }


                String formattedData = content.toString()
                        .replaceAll("\\s+", ",")
                        .replaceAll(",+", ",")
                        .replaceAll("^,|,$", "");

                inputField.setText(formattedData);
                setStatus("Imported: " + selectedFile.getName(), true);
                appendLog("📂 [Import]: Data loaded from file " + selectedFile.getName());

            } catch (java.io.IOException ex) {
                setStatus("Error reading file!", false);
                appendLog("✖ [Error]: Cannot read file: " + ex.getMessage());
            }
        }
    }
}
