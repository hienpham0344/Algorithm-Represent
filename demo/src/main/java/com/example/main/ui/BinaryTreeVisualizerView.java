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
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        panel.getChildren().addAll(title, desc, new Separator(), lblOps, inputField, btnGrid,
                new Label("TRAVERSAL"), traversalBox, btnTraverse, statusBox, spacer);

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

        Circle c = new Circle(22);
        c.getStyleClass().add(node.value == foundValue ? "tree-node-found" :
                (node.value == currentSearchValue ? "tree-node-highlight" : "tree-node"));

        Label l = new Label(String.valueOf(node.value));
        l.getStyleClass().add("tree-value");

        StackPane sp = new StackPane(c, l);
        sp.setLayoutX(x - 22);
        sp.setLayoutY(y - 22);

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
                        "• Thao tác TÌM KIẾM (Search):\n" +
                                "• Bước 1: Bắt đầu từ gốc (Root).\n" +
                                "• Bước 2: So sánh giá trị cần tìm (" + val + ") với nút hiện tại.\n" +
                                "• Bước 3: Nếu bằng thì dừng lại. Nếu nhỏ hơn thì rẽ qua trái, lớn hơn thì rẽ qua phải.\n" +
                                "• Bước 4: Lặp lại quá trình cho đến khi tìm thấy nút hoặc gặp vị trí rỗng (Null)."
                );

                BinaryTreeService.SearchResult res = service.search(val);

                animatePath(res.path(), () -> {
                    if (res.success()) foundValue = val;
                    statusText.setText(res.success() ? "Đã tìm thấy!" : "Không tìm thấy!");
                    logActivity(res.message());
                    redrawTree();
                });

            } else if (type.equals("INSERT")) {
                setPseudoCode("function insert(node, key):\n  if node is null return new Node(key)\n  if key < node.key\n    node.left = insert(node.left, key)\n  else\n    node.right = insert(node.right, key)\n  return node");
                explanationArea.setText(
                        "• Thao tác THÊM NÚT (Insert):\n" +
                                "• Bước 1: Khởi hành từ gốc. Thuật toán hoạt động tương tự như Tìm kiếm.\n" +
                                "• Bước 2: Đi theo quy luật Trái < Cha < Phải để dò đường đi xuống dưới.\n" +
                                "• Bước 3: Ngay khi gặp một vị trí trống (Null), tạo một nút chứa số " + val + " tại vị trí đó và liên kết với cha."
                );

                BinaryTreeService.SearchResult searchRes = service.search(val);

                animatePath(searchRes.path(), () -> {
                    BinaryTreeService.Result res = service.insert(val);
                    statusText.setText(res.success() ? "Thành công!" : "Thất bại!");
                    logActivity(res.message());
                    redrawTree();
                });

            } else if (type.equals("DELETE")) {
                setPseudoCode("function delete(node, key):\n  if node is null return null\n  if key < node.key node.left = delete(node.left, key)\n  else if key > node.key node.right = delete(node.right, key)\n  else:\n    if left is null return right\n    if right is null return left\n    node.key = min(node.right)\n    node.right = delete(node.right, node.key)\n  return node");
                explanationArea.setText(
                        "• Thao tác XÓA NÚT (Delete):\n" +
                                "• Bước 1: Dò tìm vị trí của nút " + val + " cần xóa.\n" +
                                "• Bước 2: Nếu là nút lá (0 con), xóa trực tiếp.\n" +
                                "• Bước 3: Nếu có 1 con, kéo đứa con đó lên thay thế vị trí nút bị xóa.\n" +
                                "• Bước 4: Nếu có 2 con, tìm nút Nhỏ Nhất của nhánh Bên Phải để đưa lên thế chỗ, sau đó xóa nút thế chỗ ở dưới cùng."
                );

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

        explanationArea.setText(
                "• Thao tác DUYỆT CÂY (" + type + "):\n" +
                        "• Thuật toán đệ quy sẽ đi qua tất cả các nút trên cây theo thứ tự chỉ định.\n" +
                        "• Các nút đang được quét sẽ hiển thị màu sáng (Highlight).\n" +
                        "• Mảng kết quả sau khi duyệt xong sẽ được in ra tại bảng Nhật ký (Log) bên cạnh."
        );

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

        double delayMs = 800;

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

    private void logActivity(String msg) {
        activityLogArea.appendText(msg + "\n");
    }

    private void handleReset() {
        service.clear();
        foundValue = -1;
        currentSearchValue = -1;
        logActivity("Đã xóa toàn bộ cây (Reset).");
        setPseudoCode("function reset():\n  tree.root = null");
        explanationArea.setText(
                "• Toàn bộ cấu trúc cây cũ đã bị hủy bỏ.\n" +
                        "• Cây hiện tại đang rỗng (Root = Null).\n" +
                        "• Bạn có thể bắt đầu Thêm (Insert) các phần tử mới để xây dựng lại cây."
        );
        redrawTree();
    }

    private void setPseudoCode(String code) {
        pseudoCodeArea.setText(code);
    }
}