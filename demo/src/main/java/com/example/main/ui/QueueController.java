package com.example.main.ui;

import com.example.main.service.QueueService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class QueueController {

    @FXML private TextField inputField;
    @FXML private StackPane vizPane;
    @FXML private TextArea  codeArea;
    @FXML private TextArea  logArea;
    @FXML private Label     statusText;

    private final QueueService service = new QueueService();

    @FXML
    public void initialize() {
        // TODO: sẽ implement sau
    }

    // Placeholder để FXML không báo lỗi missing handler
    @FXML public void handleEnqueue() {}
    @FXML public void handleDequeue() {}
    @FXML public void handlePeek()    {}
    @FXML public void handleReset()   {}
    @FXML public void handleClearLog(){}
}