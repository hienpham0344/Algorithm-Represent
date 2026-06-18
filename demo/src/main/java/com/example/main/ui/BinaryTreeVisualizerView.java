package com.example.main.ui;

import com.example.main.service.BinaryTreeService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.List;

public class BinaryTreeVisualizerView extends BorderPane {

    private final BinaryTreeService service = new BinaryTreeService();
    private TextField inputField;
    private Pane vizPane;
    private TextArea pseudoCodeArea, explanationArea, activityLogArea;
    private Label statusText;
    private Button btnInsert, btnSearch, btnDelete, btnReset, btnTraverse;
    private ComboBox<String> traversalBox;
    private int currentSearchValue = -1, foundValue = -1;

    public BinaryTreeVisualizerView() {
        getStylesheets().add(getClass().getResource("/styles/tree.css").toExternalForm());
        getStyleClass().add("tree-root");

        ScrollPane leftScroll = new ScrollPane(buildLeftPanel());
        leftScroll.setFitToWidth(true);
        leftScroll.setFitToHeight(true);
        leftScroll.setPrefWidth(285);
        leftScroll.setMinWidth(285);
        leftScroll.getStyleClass().add("left-panel");

        setLeft(leftScroll);
        setCenter(buildVizArea());
        setBottom(buildBottomDock());

        service.insert(50);
        service.insert(30);
        service.insert(70);
        redrawTree();

        explanationArea.setText("• Binary Search Tree initialized.\n• Rule: Left < Root < Right.");
        logActivity("[System]: Binary Search Tree initialized. Ready.");
    }

    private VBox buildLeftPanel() {
        VBox panel = new VBox(16);
        panel.setPadding(new Insets(22, 18, 22, 18));
        panel.getStyleClass().add("left-panel");

        Label title = new Label("BINARY SEARCH TREE");
        title.getStyleClass().add("ds-title");
        Label desc = new Label("A node-based data structure where each node has at most two children.");
        desc.getStyleClass().add("ds-desc");

        Label lblOps = new Label("OPERATIONS");
        lblOps.getStyleClass().add("section-label");

        inputField = new TextField();
        inputField.setPromptText("Enter value...");
        inputField.getStyleClass().add("input-field");

        Button btnImport = new Button("Import .txt");
        btnImport.getStyleClass().addAll("btn-action", "btn-reset");
        btnImport.setOnAction(e -> handleImport());

        HBox inputWrapper = new HBox(8, inputField, btnImport);
        HBox.setHgrow(inputField, Priority.ALWAYS);

        GridPane btnGrid = new GridPane();
        btnGrid.setHgap(8);
        btnGrid.setVgap(8);

        btnInsert = createButton("Insert", "btn-insert");
        btnDelete = createButton("Delete", "btn-delete");
        btnSearch = createButton("Search", "btn-search");
        btnReset = createButton("Reset", "btn-reset");

        btnGrid.add(btnInsert, 0, 0);
        btnGrid.add(btnDelete, 1, 0);
        btnGrid.add(btnSearch, 0, 1);
        btnGrid.add(btnReset, 1, 1);

        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(50);
        btnGrid.getColumnConstraints().addAll(col, col);

        traversalBox = new ComboBox<>();
        traversalBox.getItems().addAll("Pre-order (NLR)", "In-order (LNR)", "Post-order (LRN)");
        traversalBox.getSelectionModel().select(0);
        traversalBox.setMaxWidth(Double.MAX_VALUE);
        btnTraverse = createButton("▶ Traverse Tree", "btn-insert");

        Label lblStatusHeader = new Label("SIMULATION STATUS");
        lblStatusHeader.getStyleClass().add("status-header");
        statusText = new Label("Ready.");
        statusText.getStyleClass().add("status-text");
        VBox statusBox = new VBox(8, lblStatusHeader, statusText);
        statusBox.getStyleClass().add("status-box");
        statusBox.setPadding(new Insets(12));
        Button btnNotes = createButton("Notes", "note-button");
        btnNotes.setOnAction(e -> NoteDialog.show(getScene().getWindow(), "Binary Tree"));
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        panel.getChildren().addAll(title, desc, new Separator(), lblOps, inputWrapper, btnGrid,
                new Label("TRAVERSAL"), traversalBox, btnTraverse, statusBox, spacer, btnNotes);

        btnInsert.setOnAction(e -> executeOp("INSERT"));
        btnSearch.setOnAction(e -> executeOp("SEARCH"));
        btnDelete.setOnAction(e -> executeOp("DELETE"));
        btnReset.setOnAction(e -> handleReset());
        btnTraverse.setOnAction(e -> executeTraversal());

        return panel;
    }

    private Node buildVizArea() {
        vizPane = new Pane();
        vizPane.getStyleClass().add("viz-area");
        vizPane.widthProperty().addListener(o -> redrawTree());

        ScrollPane sp = new ScrollPane(vizPane);
        sp.setFitToHeight(true);
        sp.setFitToWidth(true);
        sp.getStyleClass().add("viz-area");
        return sp;
    }

    private HBox buildBottomDock() {
        HBox dock = new HBox(0);
        dock.getStyleClass().add("bottom-dock");
        dock.setPrefHeight(210);

        VBox codeCol = buildInfoCol("<> PSEUDO-CODE", pseudoCodeArea = new TextArea(), true);
        VBox explCol = buildInfoCol("💡 EXPLANATION", explanationArea = new TextArea(), false);
        VBox logCol = buildInfoCol(">_ ACTIVITY LOG", activityLogArea = new TextArea(), false);

        dock.getChildren().addAll(codeCol, new Separator(), explCol, new Separator(), logCol);
        HBox.setHgrow(codeCol, Priority.ALWAYS);
        HBox.setHgrow(explCol, Priority.ALWAYS);
        HBox.setHgrow(logCol, Priority.ALWAYS);

        return dock;
    }

    private VBox buildInfoCol(String title, TextArea area, boolean isCode) {
        HBox header = new HBox(new Label(title));
        header.getStyleClass().add("panel-header-box");
        area.setEditable(false);
        area.getStyleClass().add(isCode ? "code-area" : "log-area");
        if (!isCode) area.setWrapText(true);

        VBox col = new VBox(header, area);
        VBox.setVgrow(area, Priority.ALWAYS);
        col.setPadding(new Insets(10));
        return col;
    }

    private Button createButton(String text, String style) {
        Button b = new Button(text);
        b.getStyleClass().addAll("btn-action", style);
        b.setMaxWidth(Double.MAX_VALUE);
        return b;
    }

    private void redrawTree() {
        vizPane.getChildren().clear();
        BinaryTreeService.Node root = service.getRoot();
        if (root != null) {
            double startX = vizPane.getWidth() / 2;
            if (startX <= 0) startX = 500;
            drawNode(root, startX, 50, 200);
        }
    }

    private void drawNode(BinaryTreeService.Node node, double x, double y, double hGap) {
        if (node == null) return;

        if (node.left != null) {
            Line line = new Line(x, y, x - hGap, y + 80);
            line.getStyleClass().add("tree-line");
            vizPane.getChildren().add(line);
            drawNode(node.left, x - hGap, y + 80, hGap / 2);
        }

        if (node.right != null) {
            Line line = new Line(x, y, x + hGap, y + 80);
            line.getStyleClass().add("tree-line");
            vizPane.getChildren().add(line);
            drawNode(node.right, x + hGap, y + 80, hGap / 2);
        }

        Circle outerCircle = new Circle(28);
        outerCircle.getStyleClass().add(
                node.value == foundValue ? "tree-node-found-outer" :
                        (node.value == currentSearchValue ? "tree-node-highlight-outer" : "tree-node-outer"));
        Circle innerCircle = new Circle(22);
        innerCircle.getStyleClass().add(node.value == foundValue ? "tree-node-found" :
                (node.value == currentSearchValue ? "tree-node-highlight" : "tree-node"));

        Label l = new Label(String.valueOf(node.value));
        l.getStyleClass().add("tree-value");

        StackPane sp = new StackPane(outerCircle, innerCircle, l);
        
        sp.setLayoutX(x - 28);
        sp.setLayoutY(y - 28);

        vizPane.getChildren().add(sp);
    }

    private void executeOp(String type) {
        foundValue = -1;
        currentSearchValue = -1;
        redrawTree();

        try {
            int val = Integer.parseInt(inputField.getText().trim());

            if (type.equals("SEARCH")) {
                setPseudoCode("function search(node, key):\n  if node is null or node.key == key\n    return node\n  if key < node.key\n    return search(node.left, key)\n  return search(node.right, key)");
                explanationArea.setText(
                        "• SEARCH Operation:\n" +
                                "• Step 1: Start at the Root.\n" +
                                "• Step 2: Compare the target value (" + val + ") with the current node.\n" +
                                "• Step 3: If equal, stop. If less, traverse left. If greater, traverse right.\n" +
                                "• Step 4: Repeat the process until the node is found or a null position is reached."
                );

                BinaryTreeService.SearchResult res = service.search(val);

                animatePath(res.path(), () -> {
                    if (res.success()) foundValue = val;
                    statusText.setText(res.success() ? "Found!" : "Not found!");
                    logActivity(res.message());
                    redrawTree();
                });

            } else if (type.equals("INSERT")) {
                setPseudoCode("function insert(node, key):\n  if node is null return new Node(key)\n  if key < node.key\n    node.left = insert(node.left, key)\n  else\n    node.right = insert(node.right, key)\n  return node");
                explanationArea.setText(
                        "• INSERT Operation:\n" +
                                "• Step 1: Start at the Root. The algorithm works similarly to Search.\n" +
                                "• Step 2: Follow the Left < Parent < Right rule to traverse down the tree.\n" +
                                "• Step 3: Upon reaching an empty spot (Null), create a new node with value " + val + " and link it to its parent."
                );

                BinaryTreeService.SearchResult searchRes = service.search(val);

                animatePath(searchRes.path(), () -> {
                    BinaryTreeService.Result res = service.insert(val);
                    statusText.setText(res.success() ? "Success!" : "Failed!");
                    logActivity(res.message());
                    redrawTree();
                });

            } else if (type.equals("DELETE")) {
                setPseudoCode("function delete(node, key):\n  if node is null return null\n  if key < node.key node.left = delete(node.left, key)\n  else if key > node.key node.right = delete(node.right, key)\n  else:\n    if left is null return right\n    if right is null return left\n    node.key = min(node.right)\n    node.right = delete(node.right, node.key)\n  return node");
                explanationArea.setText(
                        "• DELETE Operation:\n" +
                                "• Step 1: Locate the target node " + val + " to be deleted.\n" +
                                "• Step 2: If it is a leaf node (0 children), delete it directly.\n" +
                                "• Step 3: If it has 1 child, replace the deleted node with that child.\n" +
                                "• Step 4: If it has 2 children, find the minimum node in the Right subtree (Inorder Successor) to replace it, then delete the duplicate at the bottom."
                );

                BinaryTreeService.SearchResult searchRes = service.search(val);

                animatePath(searchRes.path(), () -> {
                    BinaryTreeService.Result res = service.delete(val);
                    statusText.setText(res.success() ? "Success!" : "Failed!");
                    logActivity(res.message());
                    redrawTree();
                });
            }

            inputField.clear();
        } catch (NumberFormatException ex) {
            statusText.setText("Invalid input.");
            logActivity("[Error]: Please enter a valid integer.");
        }
    }

    private void executeTraversal() {
        String selection = traversalBox.getValue();
        String type = "NLR";
        if (selection.contains("LNR")) type = "LNR";
        else if (selection.contains("LRN")) type = "LRN";

        foundValue = -1;
        currentSearchValue = -1;
        redrawTree();

        String pseudoCode = "";
        if (type.equals("NLR")) {
            pseudoCode = "function preOrder(node):\n  if node == null return\n  visit(node)\n  preOrder(node.left)\n  preOrder(node.right)";
        } else if (type.equals("LNR")) {
            pseudoCode = "function inOrder(node):\n  if node == null return\n  inOrder(node.left)\n  visit(node)\n  inOrder(node.right)";
        } else if (type.equals("LRN")) {
            pseudoCode = "function postOrder(node):\n  if node == null return\n  postOrder(node.left)\n  postOrder(node.right)\n  visit(node)";
        }
        setPseudoCode(pseudoCode);

        explanationArea.setText(
                "• TRAVERSAL Operation (" + type + "):\n" +
                        "• The recursive algorithm will visit all nodes in the tree according to the specified order.\n" +
                        "• The currently visited nodes will be highlighted.\n" +
                        "• The final sequence will be printed in the Activity Log below once completed."
        );

        BinaryTreeService.SearchResult res = service.traverse(type);
        if (!res.success()) {
            statusText.setText(res.message());
            logActivity("[Traverse]: " + res.message());
            return;
        }

        statusText.setText("Traversing: " + type + "...");
        logActivity("[Traverse]: Starting " + type + " traversal.");

        animatePath(res.path(), () -> {
            statusText.setText("Traversal complete!");
            logActivity(res.message() + "\n>> Result: " + res.path().toString());
            redrawTree();
        });
    }

    private void animatePath(List<Integer> path, Runnable onComplete) {
        setControlsDisabled(true);

        double delayMs = 800;

        Timeline timeline = new Timeline();

        for (int i = 0; i < path.size(); i++) {
            int val = path.get(i);

            KeyFrame kf = new KeyFrame(Duration.millis(i * delayMs), e -> {
                currentSearchValue = val;
                redrawTree();
                logActivity("[Traverse]: Visiting node " + val + "...");
            });
            timeline.getKeyFrames().add(kf);
        }

        KeyFrame end = new KeyFrame(Duration.millis(path.size() * delayMs), e -> {
            currentSearchValue = -1;
            onComplete.run();
            setControlsDisabled(false);
        });
        timeline.getKeyFrames().add(end);

        timeline.play();
    }

    private void setControlsDisabled(boolean disabled) {
        btnInsert.setDisable(disabled);
        btnSearch.setDisable(disabled);
        btnDelete.setDisable(disabled);
        btnReset.setDisable(disabled);
        inputField.setDisable(disabled);
        btnTraverse.setDisable(disabled);
        traversalBox.setDisable(disabled);
    }

    private void logActivity(String msg) {
        activityLogArea.appendText(msg + "\n");
    }

    private void handleReset() {
        service.clear();
        foundValue = -1;
        currentSearchValue = -1;
        logActivity("Cleared the entire tree (Reset).");
        setPseudoCode("function reset():\n  tree.root = null");
        explanationArea.setText(
                "• The previous tree structure has been destroyed.\n" +
                        "• The current tree is empty (Root = Null).\n" +
                        "• You can start Inserting new elements to rebuild the tree."
        );
        redrawTree();
    }
    private void handleImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select file containing Binary Tree data");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File selectedFile = fileChooser.showOpenDialog(this.getScene().getWindow());

        if (selectedFile != null) {
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
                statusText.setText("Imported: " + selectedFile.getName());
                logActivity("📂 [Import]: Data loaded from file " + selectedFile.getName());

            } catch (java.io.IOException ex) {
                statusText.setText("Error reading file.");
                logActivity("✖ [Error]: Cannot read file: " + ex.getMessage());
            }
        }
    }
    private void setPseudoCode(String code) {
        pseudoCodeArea.setText(code);
    }
}
