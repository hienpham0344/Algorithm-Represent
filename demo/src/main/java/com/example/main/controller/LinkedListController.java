package com.example.main.controller;

import java.util.List;

import com.example.main.service.LinkedListService;
import com.example.main.view.NoteDialog;
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
import javafx.stage.FileChooser;
import javafx.util.Duration;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.regex.Pattern;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;

public class LinkedListController {

    @FXML private TextField inputField;
    @FXML private TextField indexField;
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
    private void handleInsertAtIndex() {
        try {
            int value = Integer.parseInt(inputField.getText());
            int index = Integer.parseInt(indexField.getText());

            if (index < 0) {
                statusLabel.setText("Index must be >= 0.");
                return;
            }

            if (index > service.getSize()) {
                statusLabel.setText("Index out of bounds. Max index: " + service.getSize());
                return;
            }

            boolean success = service.insertAtIndex(index, value);

            if (success) {
                statusLabel.setText("Inserted " + value + " at index " + index + ".");
                logArea.appendText("\n[Insert At Index]: " + value + " at index " + index);

                pseudoCodeArea.setText("""
                    if (index == 0) {
                        addHead(value);
                        return;
                    }
                    
                    Node newNode = new Node(value);
                    Node current = head;
                    int currentIndex = 0;
                    
                    while (currentIndex < index - 1) {
                        current = current.next;
                        currentIndex++;
                    }
                    
                    newNode.next = current.next;
                    current.next = newNode;
                    """);

                if (explanationArea != null) {
                    explanationArea.setText("""
                        Step 1: If index == 0, insert at head.
                        Step 2: Create a new node with value %d.
                        Step 3: Traverse to node at position (index - 1).
                        Step 4: Link new node to the next node.
                        Step 5: Link previous node to the new node.
                        """.formatted(value));
                }

                renderList();
                inputField.clear();
                indexField.clear();
            } else {
                statusLabel.setText("Failed to insert at index " + index + ".");
            }

        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid input. Enter both value and index.");
        }
    }

    @FXML
    private void handleDeleteAtIndex() {
        try {
            int index = Integer.parseInt(indexField.getText());

            if (index < 0) {
                statusLabel.setText("Index must be >= 0.");
                return;
            }

            if (index >= service.getSize()) {
                statusLabel.setText("Index out of bounds. Max index: " + (service.getSize() - 1));
                return;
            }

            boolean success = service.deleteAtIndex(index);

            if (success) {
                statusLabel.setText("Deleted node at index " + index + ".");
                logArea.appendText("\n[Delete At Index]: Removed node at index " + index);

                pseudoCodeArea.setText("""
                    if (index == 0) {
                        head = head.next;
                        return;
                    }
                    
                    Node current = head;
                    int currentIndex = 0;
                    
                    while (currentIndex < index - 1) {
                        current = current.next;
                        currentIndex++;
                    }
                    
                    current.next = current.next.next;
                    """);

                if (explanationArea != null) {
                    explanationArea.setText("""
                        Step 1: If index == 0, delete head.
                        Step 2: Traverse to node at position (index - 1).
                        Step 3: Link current node to the node after the target.
                        Step 4: The target node is now removed from the chain.
                        """);
                }

                renderList();
                indexField.clear();
            } else {
                statusLabel.setText("Failed to delete at index " + index + ".");
            }

        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid index input.");
        }
    }

    @FXML
    private void handleNotes() {
        NoteDialog.show(inputField.getScene().getWindow(), "Linked List");
    }

    @FXML
    private void handleImportTxt() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Import Linked List from .txt");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text files", "*.txt"));

        File file = chooser.showOpenDialog(inputField.getScene().getWindow());
        if (file == null) {
            return;
        }

        try {
            String raw = Files.readString(file.toPath());
            // Tách theo dấu phẩy, khoảng trắng, xuống dòng, tab...
            String[] tokens = Pattern.compile("[\\s,;]+").split(raw.trim());

            int imported = 0;
            int skipped = 0;
            for (String token : tokens) {
                if (token.isBlank()) {
                    continue;
                }
                try {
                    int value = Integer.parseInt(token.trim());
                    service.addTail(value);
                    imported++;
                } catch (NumberFormatException ex) {
                    skipped++;
                }
            }

            renderList();
            statusLabel.setText("Imported " + imported + " value(s) from "
                    + file.getName() + (skipped > 0 ? " (" + skipped + " bỏ qua)" : "") + ".");
            logArea.appendText("\n[Import .txt]: " + imported + " value(s) from " + file.getName()
                    + (skipped > 0 ? ", skipped " + skipped : ""));
        } catch (Exception e) {
            statusLabel.setText("Không đọc được file: " + e.getMessage());
            logArea.appendText("\n[Import .txt]: lỗi đọc file " + file.getName());
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
        indexField.clear();
    }
}