package com.example.main.ui;

import java.util.List;

import com.example.main.service.LinkedListService;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class LinkedListController {

    @FXML private TextField inputField;
    @FXML private Pane canvasPane;
    @FXML private TextArea statusArea;
    @FXML private TextArea pseudoCodeArea;
    @FXML private TextArea logArea;
    private final LinkedListService service = new LinkedListService();
    @FXML
    public void initialize() {
        logArea.appendText("\nSwitched to Linked List.");
    }

    @FXML
    private void handleAddHead() {
        try {

        int value = Integer.parseInt(
                inputField.getText()
        );

        service.addHead(value);

        statusArea.setText(
                "Added " + value + " to head."
        );

        logArea.appendText(
                "\n[Add Head]: " + value
        );

        renderList();

        inputField.clear();

    } catch (Exception e) {

        statusArea.setText(
                "Invalid input."
        );
    }
    }

    private void renderList() {

    canvasPane.getChildren().clear();

    List<Integer> values = service.getValues();

    double startX = 80;
    double y = 180;

    for (int i = 0; i < values.size(); i++) {

        StackPane node = createNode(values.get(i));

        node.setLayoutX(startX + i * 130);
        node.setLayoutY(y);

        canvasPane.getChildren().add(node);

        // ARROW
        if (i < values.size() - 1) {

            Text arrow = new Text("→");

            arrow.setFill(Color.web("#8b5cf6"));

            arrow.setStyle("""
                -fx-font-size: 28px;
                -fx-font-weight: bold;
            """);

            arrow.setLayoutX(startX + 92 + i * 130);
            arrow.setLayoutY(y + 34);

            canvasPane.getChildren().add(arrow);
        }
    }

    
    // NULL LABEL
    if (!values.isEmpty()) {

        Text nullText = new Text("NULL");

        nullText.setFill(Color.web("#94a3b8"));

        nullText.setStyle("""
            -fx-font-size: 16px;
            -fx-font-weight: bold;
        """);

        nullText.setLayoutX(startX + values.size() * 130 - 20);
        nullText.setLayoutY(y + 34);

        canvasPane.getChildren().add(nullText);
        }
    }

    private StackPane createNode(int value) {

        Rectangle rect = new Rectangle(90, 48);

        rect.setArcWidth(12);
        rect.setArcHeight(12);

        rect.setFill(Color.web("#111827"));

        rect.setStroke(Color.web("#7c3aed"));

        rect.setStrokeWidth(1.5);

        Line split = new Line(45, 0, 45, 48);

        split.setStroke(Color.web("#334155"));

        Text valueText = new Text(String.valueOf(value));

        valueText.setFill(Color.WHITE);

        valueText.setStyle("""
            -fx-font-size: 18px;
            -fx-font-weight: bold;
        """);

        valueText.setTranslateX(-22);

        Text nextText = new Text("next");

        nextText.setFill(Color.web("#64748b"));

        nextText.setStyle("-fx-font-size: 10px;");

        nextText.setTranslateX(22);

        return new StackPane(
                rect,
                split,
                valueText,
                nextText
        );
    }


    @FXML
    private void handleAddTail() {
        try {

        int value = Integer.parseInt(
                inputField.getText()
        );

        service.addTail(value);

        statusArea.setText(
                "Added " + value + " to tail."
        );

        logArea.appendText(
                "\n[Add Tail]: " + value
        );

        renderList();

        inputField.clear();

    } catch (Exception e) {

        statusArea.setText(
                "Invalid input."
        );
    }
    }

    @FXML
    private void handleDeleteHead() {
        service.deleteHead();
        renderList();
        statusArea.setText("Delete Head clicked.");
    }

    @FXML
    private void handleDeleteTail() {
        service.deleteTail();
        renderList();
        statusArea.setText("Delete Tail clicked.");
    }

    @FXML
    private void handleSearch() {
       try {
        int value = Integer.parseInt(inputField.getText());

        int index = service.search(value);

        if (index != -1) {
            statusArea.setText("Found " + value + " at index " + index + ".");
            logArea.appendText("\n[Search]: Found " + value + " at index " + index);
        } else {
            statusArea.setText(value + " not found in list.");
            logArea.appendText("\n[Search]: " + value + " not found");
        }

        inputField.clear();

        } catch (Exception e) {
            statusArea.setText("Invalid input.");
        }
    }

    @FXML
    private void handleReset() {
         service.reset();

        canvasPane.getChildren().clear();

        statusArea.setText("Linked List has been reset.");
        pseudoCodeArea.setText("// Select an operation to view pseudo-code.");
        logArea.appendText("\n[Reset]: Cleared linked list.");

        inputField.clear();
    }
}