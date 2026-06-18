package com.example.main.ui;

import java.util.List;

import com.example.main.service.LinkedListService;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.util.ArrayList;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;

public class LinkedListController {

    @FXML private TextField inputField;
    @FXML private Pane canvasPane;
    @FXML private Label statusLabel;
    @FXML private TextArea pseudoCodeArea;
    @FXML private TextArea logArea;
    @FXML private TextArea explanationArea;
    private final LinkedListService service = new LinkedListService();
    private final List<StackPane> renderedNodes = new ArrayList<>();

    @FXML
    public void initialize() {
        logArea.appendText("\nSwitched to Linked List.");
    }

    @FXML
    private void handleAddHead() {
        try {
        int value = Integer.parseInt(inputField.getText());

        service.addHead(value);

        statusLabel.setText("Added " + value + " to head.");

        pseudoCodeArea.setText("""
            Node newNode = new Node(value);
            newNode.next = head;
            head = newNode;
            """);

        if (explanationArea != null) {
            explanationArea.setText("""
                Step 1: Create a new node with value %d.
                Step 2: Link the new node to the current head.
                Step 3: Move head to the new node.
                """.formatted(value));
        }

        logArea.appendText("\n[Add Head]: " + value);

        renderList();
        inputField.clear();

    } catch (Exception e) {

        statusLabel.setText(
                "Invalid input."
        );
    }
    }


    private void renderList() {
        canvasPane.getChildren().clear();
        renderedNodes.clear();
        int startX = 80;
        int y=180;
        List<Integer> values= service.getValues();
        for (int i = 0; i < values.size(); i++) {
           StackPane node= createNode(values.get(i));
           node.setLayoutX(startX + i * 130);
           node.setLayoutY(y);
           canvasPane.getChildren().add(node);
           renderedNodes.add(node);

           // Arrow
           if(i < values.size()-1) {
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

        if(!values.isEmpty()) {
            Text nullText = new Text("null");
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

        int value = Integer.parseInt(inputField.getText());

        service.addTail(value);

        statusLabel.setText("Added " + value + " to tail.");

        logArea.appendText("\n[Add Tail]: " + value);

        pseudoCodeArea.clear();
        pseudoCodeArea.setText("""
            Node newNode = new Node(value);
            if (head == null) {
                head = newNode;
            } else {
                Node current = head;
                while (current.next != null) {
                    current = current.next;
                }
                current.next = newNode;
            }
            """);
        if (explanationArea != null) {
            explanationArea.setText("""
                Step 1: Create a new node with value %d.
                Step 2: If head == null, head = new node.
                Step 3: Otherwise, traverse from head to the last node.
                Step 4: Link the last node to the new node.
                """.formatted(value));
        }
        

        renderList();

        inputField.clear();

    } catch (Exception e) {

        statusLabel.setText(
                "Invalid input."
        );
    }
    }

    @FXML
    private void handleDeleteHead() {
        service.deleteHead();
        renderList();
        pseudoCodeArea.clear();
        pseudoCodeArea.setText("""
            if (head != null) {
                head = head.next;
            }
            """);
        if (explanationArea != null) {
            explanationArea.setText("""
                Step 1: If head != null, move head to the next node.
                """);
        }
        statusLabel.setText("Delete Head clicked.");
    }

    @FXML
    private void handleDeleteTail() {
        service.deleteTail();
        renderList();

        pseudoCodeArea.clear();
        pseudoCodeArea.setText("""
            if (head == null) return;
            if (head.next == null) {
                head = null;
                return;
            }
            Node current = head;
            while (current.next.next != null) {
                current = current.next;
            }
            current.next = null;
            """);
        
        if (explanationArea != null) {
                explanationArea.setText("""
                    Step 1: If head == null, return.
                    Step 2: If head.next == null, set head = null.
                    Step 3: Traverse from head to the second-to-last node.
                    Step 4: Set the next pointer of the second-to-last node to null.
                    """);
        }

        statusLabel.setText("Delete Tail clicked.");
    }

    private void setNodeColor(StackPane node, String color) {
        Rectangle rect = (Rectangle) node.getChildren().get(0);
        rect.setStroke(Color.web(color));
        rect.setStrokeWidth(3);
    }

   @FXML
    private void handleSearch() {
        try {
            int value = Integer.parseInt(inputField.getText());

            renderList();

            List<Integer> values = service.getValues();
            SequentialTransition sequence = new SequentialTransition();

            int foundIndex = -1;

            for (int i = 0; i < values.size(); i++) {
                final int index = i;
                StackPane node = renderedNodes.get(index);

                PauseTransition check = new PauseTransition(Duration.millis(500));
                check.setOnFinished(e -> {
                    setNodeColor(node, "#facc15");
                    statusLabel.setText("Checking node at index " + index + "...");
                });

                sequence.getChildren().add(check);

                if (values.get(i) == value) {
                    foundIndex = i;
                    break;
                }

                PauseTransition reset = new PauseTransition(Duration.millis(300));
                reset.setOnFinished(e -> setNodeColor(node, "#7c3aed"));
                sequence.getChildren().add(reset);
            }

            int finalFoundIndex = foundIndex;

            sequence.setOnFinished(e -> {
                if (finalFoundIndex != -1) {
                    setNodeColor(renderedNodes.get(finalFoundIndex), "#22c55e");
                    statusLabel.setText("Found " + value + " at index " + finalFoundIndex + ".");
                    logArea.appendText("\n[Search]: Found " + value + " at index " + finalFoundIndex);
                } else {
                    statusLabel.setText(value + " not found in list.");
                    logArea.appendText("\n[Search]: " + value + " not found");
                }
            });

            pseudoCodeArea.setText("""
                Node current = head;
                int index = 0;
                
                while (current != null) {
                    if (current.value == value) {
                        return index;
                    }
                    current = current.next;
                    index++;
                }
                
                return -1;
                """);

            if (explanationArea != null) {
                    explanationArea.setText("""
                Step 1: Initialize current = head and index = 0.
                Step 2: While current != null:
                    - If current.value == value, return index.
                    - Otherwise, move to the next node:
                    current = current.next;
                    index++;
                Step 3: If the value is not found, return -1.
                        """);
            }

            inputField.clear();
            sequence.play();

        } catch (Exception e) {
            statusLabel.setText("Invalid input.");
        }
    }

    @FXML
    private void handleReset() {
         service.reset();

        canvasPane.getChildren().clear();

        statusLabel.setText("Linked List has been reset.");
        pseudoCodeArea.setText("// Select an operation to view pseudo-code.");
        explanationArea.setText("");
        logArea.appendText("\n[Reset]: Cleared linked list.");

        inputField.clear();
    }

    @FXML
    private void handleNotes() {
        NoteDialog.show(inputField.getScene().getWindow(), "Linked List");
    }
}
