package com.example.main.ui;

import com.example.main.service.BinaryTreeService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class BinaryTreeVisualizerView extends BorderPane {

    private final BinaryTreeService service = new BinaryTreeService();

    private TextField inputField;
    private Pane vizPane;
    private TextArea pseudoCodeArea;
    private TextArea activityLogArea;
    private Label statusText;

    public BinaryTreeVisualizerView() {
        getStylesheets().add(
                getClass().getResource("/styles/tree.css").toExternalForm()
        );
        getStyleClass().add("tree-root");

        // Set Layout
        setLeft(buildLeftPanel());
        setCenter(buildCenterArea());

        // Khởi tạo cây mẫu giống trong ảnh của bạn
        logActivity("[System]: Binary Search Tree initialized. Ready.");
        service.insert(50);
        service.insert(30);
        service.insert(70);
        service.insert(20);
        service.insert(40);
        service.insert(60);
        service.insert(80);
        redrawTree();
    }

    private VBox buildLeftPanel() {
        VBox panel = new VBox(16);
        panel.getStyleClass().add("left-panel");
        panel.setPrefWidth(280);
        panel.setPadding(new Insets(20));

        Label title = new Label("Binary Search Tree");
        title.getStyleClass().add("ds-title");
        Label desc = new Label("Left child < Parent < Right child. Efficient search, insert, and delete. Average case O(log N).");
        desc.getStyleClass().add("ds-desc");
        desc.setWrapText(true);

        Label lblOps = new Label("☷ OPERATIONS");
        lblOps.getStyleClass().add("section-label");

        inputField = new TextField();
        inputField.setPromptText("e.g. 55");
        inputField.getStyleClass().add("input-field");

        // Buttons
        Button btnInsert = createButton("+ Insert Node", "btn-purple");
        btnInsert.setOnAction(e -> executeOp("INSERT"));

        Button btnSearch = createButton("⌕ Search Node", "btn-green");
        btnSearch.setOnAction(e -> executeOp("SEARCH"));

        Button btnDelete = createButton("🗑 Delete Node", "btn-red");
        btnDelete.setOnAction(e -> executeOp("DELETE"));

        Button btnReset = createButton("⟳ Reset", "btn-gray");
        btnReset.setOnAction(e -> {
            service.clear();
            logActivity("Đã xóa toàn bộ cây (Reset).");
            setPseudoCode("// Cây đã được làm sạch.");
            redrawTree();
        });

        // Grid 2x2 cho các nút
        GridPane btnGrid = new GridPane();
        btnGrid.setHgap(10); btnGrid.setVgap(10);
        btnGrid.add(btnInsert, 0, 0); btnGrid.add(btnSearch, 1, 0);
        btnGrid.add(btnDelete, 0, 1); btnGrid.add(btnReset, 1, 1);

        // Buộc các nút giãn đều
        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(50);
        btnGrid.getColumnConstraints().addAll(cc, cc);

        Label lblStatus = new Label("∿ SIMULATION STATUS");
        lblStatus.getStyleClass().add("section-label");

        statusText = new Label("Ready to start simulation.");
        statusText.setStyle("-fx-text-fill: #a78bfa; -fx-font-family: 'Courier New';");
        VBox statusBox = new VBox(statusText);
        statusBox.setStyle("-fx-background-color: #1e1b4b; -fx-background-radius: 8; -fx-padding: 10;");

        panel.getChildren().addAll(title, desc, lblOps, inputField, btnGrid, lblStatus, statusBox);
        return panel;
    }

    private Button createButton(String text, String colorClass) {
        Button btn = new Button(text);
        btn.getStyleClass().addAll("btn-action", colorClass);
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    private VBox buildCenterArea() {
        // Top: Canvas vẽ cây
        vizPane = new Pane();
        StackPane canvasWrapper = new StackPane(vizPane);
        canvasWrapper.getStyleClass().add("viz-area");
        VBox.setVgrow(canvasWrapper, Priority.ALWAYS); // Chiếm toàn bộ không gian trống

        // Bottom: Pseudo-code & Activity Log
        HBox bottomPanels = new HBox();
        bottomPanels.setPrefHeight(200);

        // Pseudo Code Panel
        VBox pseudoBox = new VBox();
        pseudoBox.getStyleClass().add("bottom-panel");
        HBox.setHgrow(pseudoBox, Priority.ALWAYS);
        Label lblPseudo = new Label("< > PSEUDO-CODE");
        lblPseudo.getStyleClass().add("panel-header");
        pseudoCodeArea = new TextArea("// Select an operation to view pseudo-code.");
        pseudoCodeArea.getStyleClass().add("code-area");
        pseudoCodeArea.setEditable(false);
        VBox.setVgrow(pseudoCodeArea, Priority.ALWAYS);
        pseudoBox.getChildren().addAll(lblPseudo, pseudoCodeArea);

        // Activity Log Panel
        VBox logBox = new VBox();
        logBox.getStyleClass().add("bottom-panel");
        HBox.setHgrow(logBox, Priority.ALWAYS);
        Label lblLog = new Label(">_ ACTIVITY LOG");
        lblLog.getStyleClass().add("panel-header");
        activityLogArea = new TextArea();
        activityLogArea.getStyleClass().add("log-area");
        activityLogArea.setEditable(false);
        VBox.setVgrow(activityLogArea, Priority.ALWAYS);
        logBox.getChildren().addAll(lblLog, activityLogArea);

        bottomPanels.getChildren().addAll(pseudoBox, logBox);

        return new VBox(canvasWrapper, bottomPanels);
    }

    private void executeOp(String type) {
        try {
            int val = Integer.parseInt(inputField.getText().trim());
            BinaryTreeService.Result res = null;

            switch (type) {
                case "INSERT" -> {
                    res = service.insert(val);
                    setPseudoCode("function insert(node, key):\n  if node is null return new Node(key)\n  if key < node.key\n    node.left = insert(node.left, key)\n  else\n    node.right = insert(node.right, key)\n  return node");
                }
                case "DELETE" -> {
                    res = service.delete(val);
                    setPseudoCode("function delete(node, key):\n  if node is null return null\n  if key < node.key node.left = delete(node.left, key)\n  else if key > node.key node.right = delete(node.right, key)\n  else:\n    if left is null return right\n    if right is null return left\n    node.key = min(node.right)\n    node.right = delete(node.right, node.key)\n  return node");
                }
                case "SEARCH" -> {
                    res = service.search(val);
                    setPseudoCode("function search(node, key):\n  if node is null or node.key == key\n    return node\n  if key < node.key\n    return search(node.left, key)\n  return search(node.right, key)");
                }
            }

            if (res != null) {
                statusText.setText(res.success() ? "Thành công!" : "Thất bại!");
                logActivity(res.message());
            }

            inputField.clear();
            redrawTree();
        } catch (NumberFormatException ex) {
            statusText.setText("Lỗi đầu vào.");
            logActivity("[Error]: Vui lòng nhập số nguyên hợp lệ.");
        }
    }

    private void logActivity(String msg) {
        activityLogArea.appendText(msg + "\n");
    }

    private void setPseudoCode(String code) {
        pseudoCodeArea.setText(code);
    }

    private void redrawTree() {
        vizPane.getChildren().clear();
        BinaryTreeService.Node root = service.getRoot();
        if (root != null) {
            // Canh giữa tương đối so với màn hình (X=500, Y=40)
            drawNode(root, 500, 40, 200);
        }
    }

    private void drawNode(BinaryTreeService.Node node, double x, double y, double hGap) {
        if (node == null) return;

        if (node.left != null) {
            double nextX = x - hGap;
            double nextY = y + 70;
            Line line = new Line(x, y, nextX, nextY);
            line.getStyleClass().add("tree-line");
            vizPane.getChildren().add(line);
            drawNode(node.left, nextX, nextY, hGap / 2);
        }

        if (node.right != null) {
            double nextX = x + hGap;
            double nextY = y + 70;
            Line line = new Line(x, y, nextX, nextY);
            line.getStyleClass().add("tree-line");
            vizPane.getChildren().add(line);
            drawNode(node.right, nextX, nextY, hGap / 2);
        }

        Circle circle = new Circle(22);
        circle.getStyleClass().add("tree-node");

        Label lbl = new Label(String.valueOf(node.value));
        lbl.getStyleClass().add("tree-value");

        StackPane nodeView = new StackPane(circle, lbl);
        nodeView.setLayoutX(x - 22);
        nodeView.setLayoutY(y - 22);

        vizPane.getChildren().add(nodeView);
    }
}