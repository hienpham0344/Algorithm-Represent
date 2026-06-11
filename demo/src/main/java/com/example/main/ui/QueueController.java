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
    @FXML private TextArea explanationArea;
    @FXML private Label statusText;
    @FXML private Slider speedSlider;
    @FXML private Button pauseBtn;
    private SequentialTransition batchTransition;

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
            if (batchTransition != null && batchTransition.getStatus() != Animation.Status.STOPPED) {
                batchTransition.setRate(newVal.doubleValue());
            }
        });
        // init data


        service.enqueue(15);
        service.enqueue(30);
        service.enqueue(45);
        redrawQueue(AnimType.NONE, -1);

        explanationArea.setText(
                "• Hàng đợi (Queue) được khởi tạo mặc định với 3 phần tử: 15, 30, 45.\n" +
                        "• Theo nguyên lý FIFO (Vào trước, Ra trước), phần tử 15 vào đầu tiên nên nằm ở vị trí FRONT (Sẽ được lấy ra trước).\n" +
                        "• Phần tử 45 vào sau cùng tạm thời đứng xếp hàng ở vị trí REAR."
        );

        appendLog("[Hệ Thống]: Đã tải xong ngăn xếp mô phỏng.");
        appendLog("[Hệ Thống]: Sẵn sàng hoạt động.");



    }
        @FXML
        private void handleEnqueue() {
            if (isSimulating) return;
            String txt = inputField.getText().trim();
            if (txt.isEmpty()) return;

            // Tách chuỗi dựa vào dấu phẩy
            String[] tokens = txt.split(",");

            // Tạo một luồng thực thi tuần tự các hiệu ứng chèn phần tử
            batchTransition = new SequentialTransition();
            // Biến tạm để lưu danh sách các số hợp lệ đã parse thành công
            java.util.List<Integer> validValues = new java.util.ArrayList<>();

            // 1. Kiểm tra tính hợp lệ của toàn bộ chuỗi trước khi chạy mô phỏng
            for (String token : tokens) {
                String trimmedToken = token.trim();
                if (trimmedToken.isEmpty()) continue; // Bỏ qua khoảng trắng thừa giữa các dấu phẩy
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
            codeArea.setText(CODE_ENQUEUE);
            inputField.clear(); // Xóa khung nhập sau khi đã nhận dữ liệu thành công

            // 2. Tạo chuỗi hiệu ứng đổ tuần tự từng số vào hàng đợi
            for (int i = 0; i < validValues.size(); i++) {
                final int val = validValues.get(i);
                final boolean isLast = (i == validValues.size() - 1);

                PauseTransition step = new PauseTransition(Duration.millis(1400));

                step.setOnFinished(e -> {
                    // Kiểm tra xem hàng đợi tại thời điểm chèn này đã bị đầy hay chưa
                    if (service.size() >= 10) {
                        appendLog("✖ [Lỗi]: Không thể chèn " + val + ". Hàng đợi đã đầy (Tối đa 10 phần tử).");
                        setStatus("Queue đầy. Dừng chèn các phần tử còn lại.", false);

                        // Nếu gặp lỗi đầy hàng đợi, hủy bỏ toàn bộ các bước chèn phía sau ngay lập tức
                        batchTransition.stop();
                        isSimulating = false;
                        return;
                    }

                    // Cập nhật lời giải thích tương ứng với phần tử đang được xử lý
                    explanationArea.setText(
                            "• Thao tác Enqueue đang xử lý giá trị: " + val + "\n" +
                                    "• Bước 1: Kiểm tra trạng thái isFull() (Hiện tại size = " + service.size() + ").\n" +
                                    "• Bước 2: Tịnh tiến chỉ mục REAR lên vị trí mới để chuẩn bị đón nhận phần tử.\n" +
                                    "• Bước 3: Đưa giá trị " + val + " vào cuối hàng đợi.\n" +
                                    "• Bước 4: Tăng kích thước (size++) của Hàng đợi lên thêm 1."
                    );

                    appendLog("⚡ [Đang xử lý]: Đang Enqueue " + val + " vào cuối hàng đợi (REAR)...");
                    setStatus("Đang Enqueue phần tử " + val + "...");

                    // Đưa dữ liệu vào service và vẽ lại giao diện với chỉ mục hoạt họa mới nhất
                    service.enqueue(val);
                    redrawQueue(AnimType.ENQUEUE, service.size() - 1);

                    appendLog("✔ [Thành công]: Đã thêm phần tử " + val + " vào REAR.");
                    setStatus("Enqueue(" + val + ") thành công.", true);

                    // Nếu đây là phần tử cuối cùng trong dãy, chính thức mở khóa mô phỏng
                    if (isLast) {
                        isSimulating = false;
                        pauseBtn.setText("Pause");
                        pauseBtn.setStyle("");
                    }
                });

                batchTransition.getChildren().add(step);            }

            // Bắt đầu chạy chuỗi hiệu ứng chèn hàng loạt phần tử
            batchTransition.setRate(speedSlider.getValue());
            batchTransition.play();
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

        explanationArea.setText(
                "• Bước 1: Kiểm tra xem Hàng đợi có bị rỗng hay không (isEmpty()).\n" +
                        "• Bước 2: Định vị và lấy ra dữ liệu tại vị trí đầu hàng FRONT (Hiện tại là giá trị: " + frontVal + ").\n" +
                        "• Bước 3: Di chuyển con trỏ FRONT sang vị trí tiếp theo bằng mảng vòng: front = (front + 1) % capacity.\n" +
                        "• Bước 4: Giảm kích thước tổng thể (size--) của Hàng đợi đi 1 đơn vị."
        );

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

        explanationArea.setText(
                "• Lệnh Peek() (hoặc Front()) giúp xem trước thông tin của phần tử đang chuẩn bị xuất xưởng ở đầu hàng.\n" +
                        "• Hệ thống truy cập trực tiếp vào chỉ mục của con trỏ FRONT và trích xuất giá trị: " + frontVal + ".\n" +
                        "• Hành động này hoàn toàn KHÔNG xóa phần tử, vị trí FRONT và REAR được bảo toàn nguyên vẹn."
        );

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

        explanationArea.setText(
                "• Toàn bộ cấu trúc cũ đã bị hủy bỏ để nạp lại trạng thái mô phỏng Hàng đợi mặc định.\n" +
                        "• Hệ thống tự động xếp lại 3 phần tử ban đầu: 15 (FRONT) → 30 → 45 (REAR).\n" +
                        "• Hàng đợi đã sẵn sàng tiếp nhận các phần tử mới đi vào từ REAR."
        );

        logArea.clear();
        appendLog("[Hệ Thống]: Đã nạp lại trạng thái mô phỏng Hàng Đợi mặc định.");
        appendLog("[Hệ Thống]: Khởi tạo 3 phần tử ban đầu: 15 (FRONT) → 30 → 45 (REAR).");
        setStatus("Hệ thống đã sẵn sàng.", true);

        redrawQueue(AnimType.NONE, -1);
    }
    @FXML
    private void handlePause() {
        if (batchTransition != null && isSimulating) {
            if (batchTransition.getStatus() == Animation.Status.RUNNING) {
                batchTransition.pause();
                pauseBtn.setText("Resume");
                pauseBtn.setStyle("-fx-background-color: #EAB308; -fx-text-fill: #FFFFFF;");
                setStatus("⏸ Đã tạm dừng mô phỏng chuỗi Queue.");
            } else if (batchTransition.getStatus() == Animation.Status.PAUSED) {
                batchTransition.play();
                pauseBtn.setText("Pause");
                pauseBtn.setStyle("");
                setStatus("▶ Tiếp tục nạp các phần tử Queue còn lại...");
            }
        }
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