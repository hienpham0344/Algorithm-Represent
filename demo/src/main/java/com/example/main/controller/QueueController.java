package com.example.main.controller;

import com.example.main.service.QueueService;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class QueueController implements Initializable {

    @FXML
    private TextField inputField;
    @FXML
    private HBox queueFrame;
    @FXML
    private ScrollPane vizScrollPane;
    @FXML
    private TextArea codeArea;
    @FXML
    private TextArea logArea;
    @FXML
    private TextArea explanationArea;
    @FXML
    private Label statusText;
    @FXML
    private Slider speedSlider;
    @FXML
    private Button pauseBtn;
    private SequentialTransition batchTransition;

    private final QueueService service = new QueueService();
    private boolean isSimulating = false;
    private Animation currentAnimation;

    private static final String CODE_IDLE = "//Select an action to visualize the Queue pseudocode\n";
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

    private enum AnimType {NONE, ENQUEUE, PEEK}

    @Override
    public void initialize(URL location, ResourceBundle resources) {

//        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
//            if (currentAnimation != null && currentAnimation.getStatus() == Animation.Status.RUNNING) {
//                currentAnimation.setRate(newVal.doubleValue());
//            }
//            if (batchTransition != null && batchTransition.getStatus() != Animation.Status.STOPPED) {
//                batchTransition.setRate(newVal.doubleValue());
//            }
//        });

        // init data
        service.enqueue(15);
        service.enqueue(30);
        service.enqueue(45);
        redrawQueue(AnimType.NONE, -1);

        explanationArea.setText(
                "• The Queue is initialized by default with 3 elements: 15, 30, and 45.\n" +
                        "According to the FIFO (First-In, First-Out) principle, since the element 15 enters first, it is positioned at the FRONT (and will be removed first).\n" +
                        "The element 45, entering last, temporarily waits in line at the REAR position."
        );

        appendLog("[System]: Simulated queue loaded successfully.");
        appendLog("[System]: Ready for operation.");


    }

    @FXML
    private void handleEnqueue() {
        if (isSimulating) return;
        String txt = inputField.getText().trim();
        if (txt.isEmpty()) {
            setStatus("⚠ Please enter a valid integer or sequence of numbers!", false);
            return;
        }

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
                setStatus("⚠ The input sequence contains an invalid value. '" + trimmedToken + "'", false);
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
                if (service.size() >= 20) {
                    appendLog("✖ [Error]: Cannot enqueue " + val + ". Queue is full (Maximum 20 elements).");
                    setStatus("Queue is full. Stopping insertion of the remaining elements.", false);

                    // Nếu gặp lỗi đầy hàng đợi, hủy bỏ toàn bộ các bước chèn phía sau ngay lập tức
                    batchTransition.stop();
                    isSimulating = false;
                    return;
                }

                // Cập nhật lời giải thích tương ứng với phần tử đang được xử lý
                explanationArea.setText(
                        "• Enqueue operation is processing value: " + val + "\n" +
                                "• Step 1: Check isFull() status (Current size = " + service.size() + ").\n" +
                                "• Step 2: Advance the REAR index to the new position to prepare for the element.\n" +
                                "• Step 3: Insert the value " + val + " at the end of the queue.\n" +
                                "• Step 4: Increment the queue size (size++)."
                );

                appendLog("⚡ [Processing]: Enqueuing " + val + " to the end of the queue (REAR)...");
                setStatus("Enqueuing element " + val + "...");

                // Đưa dữ liệu vào service và vẽ lại giao diện với chỉ mục hoạt họa mới nhất
                service.enqueue(val);
                redrawQueue(AnimType.ENQUEUE, service.size() - 1);

                appendLog("✔ [Success]: Element " + val + " added to REAR.");
                setStatus("Enqueue(" + val + ") successful.", true);

                // Nếu đây là phần tử cuối cùng trong dãy, chính thức mở khóa mô phỏng
                if (isLast) {
                    isSimulating = false;
                    pauseBtn.setText("Pause");
                    pauseBtn.setStyle("");
                }
            });

            batchTransition.getChildren().add(step);
        }

        // Bắt đầu chạy chuỗi hiệu ứng chèn hàng loạt phần tử
        //batchTransition.setRate(speedSlider.getValue());
        batchTransition.play();
    }

    @FXML
    private void handleDequeue() {
        if (isSimulating) return;
        if (service.isEmpty()) {
            appendLog("✖ [Error]: Queue is empty. Cannot Dequeue!");
            setStatus("Queue is empty.", false);
            return;
        }

        isSimulating = true;
        codeArea.setText(CODE_DEQUEUE);
        int frontVal = service.toList().get(0);

        explanationArea.setText(
                "• Step 1: Check if the Queue is empty (isEmpty()).\n" +
                        "• Step 2: Locate and retrieve the data at the FRONT position (Current value: " + frontVal + ").\n" +
                        "• Step 3: Move the FRONT pointer to the next position using a circular array: front = (front + 1) % capacity.\n" +
                        "• Step 4: Decrement the overall queue size (size--)."
        );

        appendLog("⚡ [Processing]: Dequeuing element " + frontVal + " from the FRONT...");
        setStatus("Dequeuing...");

        VBox frontNode = (VBox) queueFrame.getChildren().get(0);


        ScaleTransition st = new ScaleTransition(Duration.millis(450), frontNode);
        st.setToX(0);
        st.setToY(0);
        FadeTransition ft = new FadeTransition(Duration.millis(450), frontNode);
        ft.setToValue(0);
        ParallelTransition pt = new ParallelTransition(st, ft);

        pt.setOnFinished(e -> {
            service.dequeue();
            redrawQueue(AnimType.NONE, -1);
            appendLog("✔ [Success]: Successfully dequeued " + frontVal + " from FRONT.");
            setStatus("Dequeue successful.", true);
            isSimulating = false;
        });

        currentAnimation = pt;
        //pt.setRate(speedSlider.getValue());
        pt.play();
    }

    @FXML
    private void handlePeek() {
        if (isSimulating) return;
        if (service.isEmpty()) {
            appendLog("✖ [Error]: Queue is empty. Cannot Peek!");
            setStatus("Queue is empty.", false);
            return;
        }

        isSimulating = true;
        codeArea.setText(CODE_PEEK);
        int frontVal = service.toList().get(0);

        explanationArea.setText(
                "• The Peek() (or Front()) command allows you to preview the element that is about to be removed at the front of the queue.\n" +
                        "• The system directly accesses the FRONT pointer index and extracts the value: " + frontVal + ".\n" +
                        "• This action does NOT delete the element; the FRONT and REAR positions remain completely intact."
        );

        appendLog("⚡ [Processing]: Reading the FRONT element value...");
        setStatus("Checking FRONT value...");

        redrawQueue(AnimType.PEEK, 0);

        PauseTransition pause = new PauseTransition(Duration.millis(1500));
        pause.setOnFinished(e -> {
            appendLog("✔ [Success]: Current value at FRONT is: " + frontVal);
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
                "• The entire old structure has been cleared to reload the default Queue simulation state.\n" +
                        "• The system automatically enqueued the 3 initial elements: 15 (FRONT) → 30 → 45 (REAR).\n" +
                        "• The Queue is now ready to accept new elements entering from the REAR."
        );

        logArea.clear();
        appendLog("[System]: Default Queue simulation state reloaded.");
        appendLog("[System]: Initialized 3 initial elements: 15 (FRONT) → 30 → 45 (REAR).");
        setStatus("System is ready.", true);

        redrawQueue(AnimType.NONE, -1);
    }

    @FXML
    private void handlePause() {
        if (batchTransition != null && isSimulating) {
            if (batchTransition.getStatus() == Animation.Status.RUNNING) {
                batchTransition.pause();
                pauseBtn.setText("Resume");
                pauseBtn.setStyle("-fx-background-color: #EAB308; -fx-text-fill: #FFFFFF;");
                setStatus("⏸ Simulation paused.");
            } else if (batchTransition.getStatus() == Animation.Status.PAUSED) {
                batchTransition.play();
                pauseBtn.setText("Pause");
                pauseBtn.setStyle("");
                setStatus("▶ Resuming Queue enqueues...");
            }
        }
    }

    @FXML
    private void handleClearLog() {
        logArea.clear();
        appendLog("[System]: Log has been cleared.");
    }

    private void redrawQueue(AnimType type, int animIdx) {
        queueFrame.getChildren().clear();
        List<Integer> items = service.toList();

        if (items.isEmpty()) {
            Label empty = new Label("QUEUE EMPTY");
            empty.getStyleClass().add("queue-empty-label");

            // Khi rỗng, chiếc máng ngang có kích thước cố định ngắn
            queueFrame.setPrefWidth(160);
            queueFrame.setMinWidth(160);
            queueFrame.setMaxWidth(160);
            queueFrame.getChildren().add(empty);
            return;
        }


        double calculatedWidth = (items.size() * 75) + ((items.size() - 1) * 12) + 40;

        queueFrame.setPrefWidth(calculatedWidth);
        queueFrame.setMinWidth(calculatedWidth);
        queueFrame.setMaxWidth(calculatedWidth);

        for (int i = 0; i < items.size(); i++) {
            boolean isFront = (i == 0);
            boolean isRear = (i == items.size() - 1);
            VBox cellNode = buildCellNode(items.get(i), isFront, isRear);
            queueFrame.getChildren().add(cellNode);

            if (i == animIdx) {
                switch (type) {
                    case ENQUEUE -> playEnqueueAnim(cellNode);
                    case PEEK -> playPeekAnim(cellNode);
                    default -> {
                    }
                }
            }

        }
    }

    private VBox buildCellNode(int value, boolean isFront, boolean isRear) {
        VBox cell = new VBox(4);
        cell.setAlignment(Pos.CENTER);

        String tagText = "";
        if (isFront && isRear) tagText = "FRONT / REAR";
        else if (isFront) tagText = "FRONT";
        else if (isRear) tagText = "REAR";

        Label topLabel = new Label(tagText);
        topLabel.getStyleClass().add(isFront ? "queue-tag-front" : (isRear ? "queue-tag-rear" : "queue-tag-mid"));

        StackPane cellBox = new StackPane(new Label(String.valueOf(value)));
        cellBox.getStyleClass().add("queue-cell-box");
        if (isFront) cellBox.getStyleClass().add("queue-cell-front-border");

        cell.getChildren().addAll(topLabel, cellBox);
        return cell;
    }

    private void appendLog(String msg) {
        logArea.appendText(msg + "\n");
    }

    private void setStatus(String text) {
        statusText.setText(text);
        statusText.setStyle("-fx-text-fill: #94A3B8;");
    }

    private void setStatus(String text, boolean success) {
        statusText.setText(text);
        statusText.setStyle("-fx-text-fill: " + (success ? "#34D399;" : "#F87171;"));
    }

    private void playEnqueueAnim(VBox node) {
        node.setScaleX(0);
        node.setScaleY(0);
        node.setOpacity(0);
        Timeline tl = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(node.scaleXProperty(), 0),
                        new KeyValue(node.scaleYProperty(), 0),
                        new KeyValue(node.opacityProperty(), 0)
                ),
                new KeyFrame(Duration.millis(200),
                        new KeyValue(node.scaleXProperty(), 1.15, Interpolator.EASE_OUT),
                        new KeyValue(node.scaleYProperty(), 1.15, Interpolator.EASE_OUT),
                        new KeyValue(node.opacityProperty(), 1.0)
                ),
                new KeyFrame(Duration.millis(320),
                        new KeyValue(node.scaleXProperty(), 1.0, Interpolator.EASE_IN),
                        new KeyValue(node.scaleYProperty(), 1.0, Interpolator.EASE_IN)
                )
        );
        currentAnimation = tl;
        //tl.setRate(speedSlider.getValue());
        tl.play();
    }

    private void playPeekAnim(VBox node) {
        ScaleTransition st = new ScaleTransition(Duration.millis(400), node);
        st.setFromX(1.0);
        st.setToX(1.1);
        st.setFromY(1.0);
        st.setToY(1.1);
        st.setCycleCount(4);
        st.setAutoReverse(true);
        st.setInterpolator(Interpolator.EASE_BOTH);
        currentAnimation = st;
        //st.setRate(speedSlider.getValue());
        st.play();
    }
    @FXML
    private void handleImport() {
        if (isSimulating) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn file dữ liệu Queue");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File selectedFile = fileChooser.showOpenDialog(inputField.getScene().getWindow());

        if (selectedFile != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append(" ");
                }

                // Xử lý dữ liệu: thay khoảng trắng/xuống dòng bằng dấu phẩy
                String formattedData = content.toString()
                        .replaceAll("\\s+", ",")
                        .replaceAll(",+", ",")
                        .replaceAll("^,|,$", "");

                inputField.setText(formattedData);
                statusText.setText("Đã import: " + selectedFile.getName());
                logArea.appendText("📂 [Import]: Đã nạp dữ liệu từ file " + selectedFile.getName() + "\n");

            } catch (IOException ex) {
                statusText.setText("Lỗi khi đọc file!");
                logArea.appendText("✖ [Error]: Không thể đọc file: " + ex.getMessage() + "\n");
            }
        }
    }
}