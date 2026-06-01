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

    }
}