package com.example.main.ui;

import com.example.main.service.BinaryTreeService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.util.List;

public class BinaryTreeVisualizerView extends BorderPane {

    private final BinaryTreeService service = new BinaryTreeService();
    private TextField inputField;
    private Pane vizPane;
    private TextArea pseudoCodeArea;
    private TextArea activityLogArea;
    private Label statusText;

    private Button btnInsert, btnSearch, btnDelete, btnReset, btnTraverse;
    private ComboBox<String> traversalBox;
    private Slider speedSlider;

    private int currentSearchValue = -1;
    private int foundValue = -1;

    public BinaryTreeVisualizerView() {
        getStylesheets().add(getClass().getResource("/styles/tree.css").toExternalForm());
        getStyleClass().add("tree-root");

        setLeft(buildLeftPanel());
        setCenter(buildCenterArea());

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
        Label desc = new Label("Trái < Cha < Phải. Hỗ trợ Tìm kiếm, Thêm, Xóa và Duyệt toàn bộ cây.");
        desc.getStyleClass().add("ds-desc");
        desc.setWrapText(true);

        Label lblOps = new Label("☷ THAO TÁC NÚT (CẦN NHẬP SỐ)");
        lblOps.getStyleClass().add("section-label");

        inputField = new TextField();
        inputField.setPromptText("Nhập một số (VD: 55)...");
        inputField.getStyleClass().add("input-field");

        btnInsert = createButton("+ Thêm nút (Insert)", "btn-purple");
        btnInsert.setOnAction(e -> executeOp("INSERT"));

        btnSearch = createButton("⌕ Tìm 1 nút (Search)", "btn-green");
        btnSearch.setOnAction(e -> executeOp("SEARCH"));

        btnDelete = createButton("🗑 Xóa nút (Delete)", "btn-red");
        btnDelete.setOnAction(e -> executeOp("DELETE"));

        VBox opsBox = new VBox(8, inputField, btnInsert, btnSearch, btnDelete);

        Label lblTraverse = new Label("☍ DUYỆT CÂY (TRAVERSAL)");
        lblTraverse.getStyleClass().add("section-label");

        traversalBox = new ComboBox<>();
        traversalBox.getItems().addAll("Pre-order (NLR)", "In-order (LNR)", "Post-order (LRN)");
        traversalBox.getSelectionModel().select(0);
        traversalBox.setMaxWidth(Double.MAX_VALUE);

        btnTraverse = createButton("▶ Duyệt", "btn-purple");
        btnTraverse.setOnAction(e -> executeTraversal());

        VBox traverseBox = new VBox(8, traversalBox, btnTraverse);

        btnReset = createButton("⟳ Xóa làm lại (Reset)", "btn-gray");
        btnReset.setOnAction(e -> {
            service.clear();
            foundValue = -1;
            currentSearchValue = -1;
            logActivity("Đã xóa toàn bộ cây (Reset).");
            setPseudoCode("function reset():\n  tree.root = null");
            redrawTree();
        });

        Label lblStatus = new Label("∿ TRẠNG THÁI (STATUS)");
        lblStatus.getStyleClass().add("section-label");

        statusText = new Label("Sẵn sàng hoạt động.");
        statusText.setStyle("-fx-text-fill: #a78bfa; -fx-font-family: 'Courier New';");
        VBox statusBox = new VBox(statusText);
        statusBox.setStyle("-fx-background-color: #1e1b4b; -fx-background-radius: 8; -fx-padding: 10;");

        panel.getChildren().addAll(
                title, desc,
                lblOps, opsBox,
                lblTraverse, traverseBox,
                btnReset,
                lblStatus, statusBox
        );
        return panel;
    }

    private Button createButton(String text, String colorClass) {
        Button btn = new Button(text);
        btn.getStyleClass().addAll("btn-action", colorClass);
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    private VBox buildCenterArea() {
        speedSlider = new Slider(0.5, 3.0, 1.5);
        speedSlider.setPrefWidth(120);

        Label speedLabel = new Label("⏱ Speed:");
        speedLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 12px; -fx-font-weight: bold;");

        HBox topBar = new HBox(8, speedLabel, speedSlider);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(16, 24, 0, 0));

        vizPane = new Pane();
        vizPane.setMinSize(3000, 1500);
        vizPane.setPrefSize(3000, 1500);
        vizPane.setStyle("-fx-background-color: #0f172a;");

        ScrollPane scrollPane = new ScrollPane(vizPane);
        scrollPane.setPannable(true);
        javafx.application.Platform.runLater(() -> scrollPane.setHvalue(0.5));
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        VBox canvasContainer = new VBox(topBar, scrollPane);
        canvasContainer.getStyleClass().add("viz-area");
        VBox.setVgrow(canvasContainer, Priority.ALWAYS);

        HBox bottomPanels = new HBox();
        bottomPanels.setMinHeight(220);
        bottomPanels.setMaxHeight(220);
        bottomPanels.setPrefHeight(220);

        VBox pseudoBox = new VBox();
        pseudoBox.getStyleClass().add("bottom-panel");
        HBox.setHgrow(pseudoBox, Priority.ALWAYS);
        Label lblPseudo = new Label("< > PSEUDO-CODE");
        lblPseudo.getStyleClass().add("panel-header");
        pseudoCodeArea = new TextArea();
        pseudoCodeArea.getStyleClass().add("code-area");
        pseudoCodeArea.setEditable(false);
        VBox.setVgrow(pseudoCodeArea, Priority.ALWAYS);
        pseudoBox.getChildren().addAll(lblPseudo, pseudoCodeArea);

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

        return new VBox(canvasContainer, bottomPanels);
    }

    private void executeOp(String type) {
        foundValue = -1;
        currentSearchValue = -1;
        redrawTree();

        try {
            int val = Integer.parseInt(inputField.getText().trim());

            if (type.equals("SEARCH")) {
                setPseudoCode("function search(node, key):\n  if node is null or node.key == key\n    return node\n  if key < node.key\n    return search(node.left, key)\n  return search(node.right, key)");
                BinaryTreeService.SearchResult res = service.search(val);

                animatePath(res.path(), () -> {
                    if (res.success()) foundValue = val;
                    statusText.setText(res.success() ? "Đã tìm thấy!" : "Không tìm thấy!");
                    logActivity(res.message());
                    redrawTree();
                });

            } else if (type.equals("INSERT")) {
                setPseudoCode("function insert(node, key):\n  if node is null return new Node(key)\n  if key < node.key\n    node.left = insert(node.left, key)\n  else\n    node.right = insert(node.right, key)\n  return node");
                BinaryTreeService.SearchResult searchRes = service.search(val);

                animatePath(searchRes.path(), () -> {
                    BinaryTreeService.Result res = service.insert(val);
                    statusText.setText(res.success() ? "Thành công!" : "Thất bại!");
                    logActivity(res.message());
                    redrawTree();
                });

            } else if (type.equals("DELETE")) {
                setPseudoCode("function delete(node, key):\n  if node is null return null\n  if key < node.key node.left = delete(node.left, key)\n  else if key > node.key node.right = delete(node.right, key)\n  else:\n    if left is null return right\n    if right is null return left\n    node.key = min(node.right)\n    node.right = delete(node.right, node.key)\n  return node");
                BinaryTreeService.SearchResult searchRes = service.search(val);

                animatePath(searchRes.path(), () -> {
                    BinaryTreeService.Result res = service.delete(val);
                    statusText.setText(res.success() ? "Thành công!" : "Thất bại!");
                    logActivity(res.message());
                    redrawTree();
                });
            }

            inputField.clear();
        } catch (NumberFormatException ex) {
            statusText.setText("Lỗi đầu vào.");
            logActivity("[Error]: Vui lòng nhập số nguyên hợp lệ.");
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

        BinaryTreeService.SearchResult res = service.traverse(type);
        if (!res.success()) {
            statusText.setText(res.message());
            logActivity("[Traverse]: " + res.message());
            return;
        }

        statusText.setText("Đang duyệt: " + type + "...");
        logActivity("[Traverse]: Bắt đầu duyệt " + type + ".");

        animatePath(res.path(), () -> {
            statusText.setText("Duyệt xong!");
            logActivity(res.message() + "\n>> Kết quả: " + res.path().toString());
            redrawTree();
        });
    }

    private void animatePath(List<Integer> path, Runnable onComplete) {
        setControlsDisabled(true);

        double speed = speedSlider.getValue();
        double delayMs = 1200 / speed;

        Timeline timeline = new Timeline();

        for (int i = 0; i < path.size(); i++) {
            int val = path.get(i);

            KeyFrame kf = new KeyFrame(Duration.millis(i * delayMs), e -> {
                currentSearchValue = val;
                redrawTree();
                logActivity("[Traverse]: Đang thăm/duyệt qua nút " + val + "...");
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

    private void redrawTree() {
        vizPane.getChildren().clear();
        BinaryTreeService.Node root = service.getRoot();
        if (root != null) {
            drawNode(root, 1500, 40, 800);
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

        if (node.value == foundValue) {
            circle.getStyleClass().add("tree-node-found");
        } else if (node.value == currentSearchValue) {
            circle.getStyleClass().add("tree-node-highlight");
        } else {
            circle.getStyleClass().add("tree-node");
        }

        Label lbl = new Label(String.valueOf(node.value));
        lbl.getStyleClass().add("tree-value");

        StackPane nodeView = new StackPane(circle, lbl);
        nodeView.setLayoutX(x - 22);
        nodeView.setLayoutY(y - 22);

        vizPane.getChildren().add(nodeView);
    }

    private void logActivity(String msg) {
        activityLogArea.appendText(msg + "\n");
    }

    private void setPseudoCode(String code) {
        pseudoCodeArea.setText(code);
    }
}