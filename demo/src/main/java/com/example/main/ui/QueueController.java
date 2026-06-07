package com.example.main.ui;

import com.example.main.service.QueueService;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class QueueController implements Initializable {

    @FXML private TextField inputField;
    @FXML private HBox queueFrame;
    @FXML private TextArea codeArea;
    @FXML private TextArea logArea;
    @FXML private Label statusText;
    @FXML private Slider speedSlider;

    private final QueueService service = new QueueService();
    private boolean isSimulating = false;
    private Animation currentAnimation;

    private static final String CODE_IDLE = "// Chọn một hành động để trực quan hóa mã giả của Hàng đợi\n";
    private static final String CODE_ENQUEUE =
            "void enqueue(int value) {\n" +
                    "    if (isFull()) return error;\n" +
                    "    rear = (rear + 1) % capacity;\n" +
                    "    elements[rear] = value;\n" +
                    "    size++;\n" +
                    "}\n";
    private static final String CODE_DEQUEUE =
            "int dequeue() {\n" +
                    "    if (isEmpty()) return error;\n" +
                    "    int value = elements[front];\n" +
                    "    front = (front + 1) % capacity;\n" +
                    "    size--;\n" +
                    "    return value;\n" +
                    "}\n";
    private static final String CODE_PEEK =
            "int peek() {\n" +
                    "    if (isEmpty()) return error;\n" +
                    "    return elements[front];\n" +
                    "}\n";

    private enum AnimType { NONE, ENQUEUE, PEEK }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (currentAnimation != null && currentAnimation.getStatus() == Animation.Status.RUNNING) {
                currentAnimation.setRate(newVal.doubleValue());
            }
        });
        // init data


        service.enqueue(15);
        service.enqueue(30);
        service.enqueue(45);
        redrawQueue(AnimType.NONE, -1);

        appendLog("[Hệ Thống]: Đã tải xong ngăn xếp mô phỏng.");
        appendLog("[Hệ Thống]: Sẵn sàng hoạt động.");



    }
        @FXML
    private void handleEnqueue() {
        if (isSimulating) return;
        String txt = inputField.getText().trim();
        if (txt.isEmpty()) return;

        try {
            int val = Integer.parseInt(txt);
            if (service.size() >= 10) {
                appendLog("✖ [Lỗi]: Chiều dài hàng đợi giới hạn 10 phần tử trong demo.");
                setStatus("Queue đã đầy (giới hạn demo: 10 phần tử).", false);
                return;
            }

            isSimulating = true;
            codeArea.setText(CODE_ENQUEUE);
            appendLog("⚡ [Đang xử lý]: Đang Enqueue " + val + " vào cuối hàng đợi (REAR)...");
            setStatus("Đang thực hiện Enqueue...");

            service.enqueue(val);
            redrawQueue(AnimType.ENQUEUE, service.size() - 1);

            PauseTransition pause = new PauseTransition(Duration.millis(1200));
            pause.setOnFinished(e -> {
                appendLog("✔ [Thành công]: Đã thêm phần tử " + val + " vào REAR.");
                setStatus("Enqueue(" + val + ") thành công.", true);
                isSimulating = false;
            });
            pause.play();

        } catch (NumberFormatException ex) {

            setStatus("⚠ Giá trị không hợp lệ. Hãy nhập số nguyên.", false);
        }
    }

    @FXML
    private void handleDequeue() {
        if (isSimulating) return;
        if (service.isEmpty()) {
            appendLog("✖ [Lỗi]: Hàng đợi trống (Queue Empty). Không thể Dequeue!");
            setStatus("Hàng đợi rỗng.", false);
            return;
        }

        isSimulating = true;
        codeArea.setText(CODE_DEQUEUE);
        int frontVal = service.toList().get(0);
        appendLog("⚡ [Đang xử lý]: Đang rút phần tử " + frontVal + " ra khỏi đầu hàng (FRONT)...");
        setStatus("Đang thực hiện Dequeue...");

        VBox frontNode = (VBox) queueFrame.getChildren().get(0);


        ScaleTransition st = new ScaleTransition(Duration.millis(450), frontNode);
        st.setToX(0); st.setToY(0);
        FadeTransition ft = new FadeTransition(Duration.millis(450), frontNode);
        ft.setToValue(0);
        ParallelTransition pt = new ParallelTransition(st, ft);

        pt.setOnFinished(e -> {
            service.dequeue();
            redrawQueue(AnimType.NONE, -1);
            appendLog("✔ [Thành công]: Đã rút thành công " + frontVal + " ra khỏi FRONT.");
            setStatus("Dequeue thành công.", true);
            isSimulating = false;
        });

        currentAnimation = pt;
        pt.setRate(speedSlider.getValue());
        pt.play();
    }

    @FXML
    private void handlePeek() {
        if (isSimulating) return;
        if (service.isEmpty()) {
            appendLog("✖ [Lỗi]: Hàng đợi rỗng (Queue Empty). Không thể Peek!");
            setStatus("Hàng đợi rỗng.", false);
            return;
        }

        isSimulating = true;
        codeArea.setText(CODE_PEEK);
        int frontVal = service.toList().get(0);
        appendLog("⚡ [Đang xử lý]: Đang đọc giá trị phần tử FRONT...");
        setStatus("Kiểm tra giá trị FRONT...");

        redrawQueue(AnimType.PEEK, 0);

        PauseTransition pause = new PauseTransition(Duration.millis(1500));
        pause.setOnFinished(e -> {
            appendLog("✔ [Thành công]: Giá trị ở FRONT hiện tại là: " + frontVal);
            setStatus("Front Value: " + frontVal, true);
            isSimulating = false;
        });
        pause.play();
    }

    @FXML
    private void handleReset() {
        if (isSimulating) return;
        service.reset();
        service.enqueue(15);
        service.enqueue(30);
        service.enqueue(45);

        codeArea.setText(CODE_IDLE);
        logArea.clear();
        appendLog("[Hệ Thống]: Đã nạp lại trạng thái mô phỏng Hàng Đợi mặc định.");
        appendLog("[Hệ Thống]: Khởi tạo 3 phần tử ban đầu: 15 (FRONT) → 30 → 45 (REAR).");
        setStatus("Hệ thống đã sẵn sàng.", true);

        redrawQueue(AnimType.NONE, -1);
    }

    @FXML
    private void handleClearLog() {
        logArea.clear();
        appendLog("[Hệ Thống]: Nhật ký đã được dọn sạch.");
    }

    private void redrawQueue(AnimType type, int animIdx) {
        queueFrame.getChildren().clear();
        List<Integer> items = service.toList();

        if (items.isEmpty()) {
            Label empty = new Label("QUEUE RỖNG (EMPTY)");
            empty.getStyleClass().add("queue-empty-label");

            // Khi rỗng, chiếc máng ngang có kích thước cố định ngắn
            queueFrame.setPrefWidth(160);
            queueFrame.setMinWidth(160);
            queueFrame.setMaxWidth(160);
            return;
        }


        double calculatedWidth = (items.size() * 75) + ((items.size() - 1) * 12) + 40;

        queueFrame.setPrefWidth(calculatedWidth);
        queueFrame.setMinWidth(calculatedWidth);
        queueFrame.setMaxWidth(calculatedWidth);

        for (int i = 0; i < items.size(); i++) {
            boolean isFront = (i == 0);
            boolean isRear  = (i == items.size() - 1);
            VBox cellNode = buildCellNode(items.get(i), isFront, isRear);
            queueFrame.getChildren().add(cellNode);

        }
    }

    private VBox buildCellNode(int value, boolean isFront, boolean isRear) {
        VBox cell = new VBox(4);
        cell.setAlignment(Pos.CENTER);

        String tagText = "";
        if (isFront && isRear) tagText = "FRONT / REAR";
        else if (isFront)      tagText = "FRONT (ĐẦU)";
        else if (isRear)       tagText = "REAR (CUỐI)";

        Label topLabel = new Label(tagText);
        topLabel.getStyleClass().add(isFront ? "queue-tag-front" : (isRear ? "queue-tag-rear" : "queue-tag-mid"));

        StackPane cellBox = new StackPane(new Label(String.valueOf(value)));
        cellBox.getStyleClass().add("queue-cell-box");
        if (isFront) cellBox.getStyleClass().add("queue-cell-front-border");

        cell.getChildren().addAll(topLabel, cellBox);
        return cell;
    }

    private void appendLog(String msg) { logArea.appendText(msg + "\n"); }

    private void setStatus(String text) {
        statusText.setText(text);
        statusText.setStyle("-fx-text-fill: #94A3B8;");
    }

    private void setStatus(String text, boolean success) {
        statusText.setText(text);
        statusText.setStyle("-fx-text-fill: " + (success ? "#34D399;" : "#F87171;"));
    }
}