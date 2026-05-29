package com.example.main.ui;

import com.example.main.service.StackService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class StackVisualizerView extends BorderPane {

    private final StackService service = new StackService();

    private TextField inputField;
    private VBox      stackFrame;
    private TextArea  logArea;
    private TextArea  codeArea;
    private Label     statusText;
    private boolean   isSimulating = false;


    private static final String CODE_IDLE =
            "// Chọn một hành động để\n// trực quan hóa mã giả\n";

    private static final String CODE_PUSH =
            "// Push: Đẩy phần tử vào đỉnh ngăn xếp\n" +
                    "void push(int value) {\n" +
                    "    elements.add(value); // Thêm phần tử\n" +
                    "    top = value;         // Đỉnh mới cập nhật\n" +
                    "}\n";

    private static final String CODE_POP =
            "// Pop: Lấy phần tử ra khỏi đỉnh\n" +
                    "int pop() {\n" +
                    "    if (isEmpty()) return error;\n" +
                    "    int value = elements[size - 1];\n" +
                    "    elements.remove(size - 1);\n" +
                    "    return value;\n" +
                    "}\n";

    private static final String CODE_PEEK =
            "// Peek: Đọc thử giá trị ở đỉnh, không xóa\n" +
                    "int peek() {\n" +
                    "    if (isEmpty()) return error;\n" +
                    "    return elements[size - 1];\n" +
                    "}\n";

    private enum AnimType { NONE, PUSH, PEEK }

    public StackVisualizerView() {
        getStylesheets().add(
                getClass().getResource("/styles/stack.css").toExternalForm()
        );
        getStyleClass().add("stack-root");
        setLeft(buildLeftPanel());  // thêm dòng này
    }

    private VBox buildLeftPanel() {
        VBox panel = new VBox(14);
        panel.getStyleClass().add("left-panel");
        panel.setPrefWidth(265);
        panel.setMinWidth(265);
        panel.setMaxWidth(265);
        panel.setPadding(new Insets(22, 18, 22, 18));

        Label title = new Label("Ngăn xếp (Stack - LIFO)");
        title.getStyleClass().add("ds-title");
        title.setWrapText(true);

        Label desc = new Label(
                "Hoạt động theo nguyên lý 'Vào sau – Ra trước' " +
                        "(Last In, First Out). Hai thao tác cơ bản nhất là " +
                        "Push (Thêm vào đỉnh) và Pop (Lấy ra từ đỉnh)."
        );
        desc.getStyleClass().add("ds-desc");
        desc.setWrapText(true);

        Label sectionOp = new Label("TÁC VỤ THAO TÁC");
        sectionOp.getStyleClass().add("section-label");

        Label inputLabel = new Label("Giá trị phần tử (Số nguyên):");
        inputLabel.getStyleClass().add("input-label");

        inputField = new TextField();
        inputField.setPromptText("Ví dụ: 42");
        inputField.getStyleClass().add("input-field");
        inputField.setMaxWidth(Double.MAX_VALUE);

        Button btnPush  = makeBtn("Push (Đẩy vào)", "btn-push");
        Button btnPop   = makeBtn("Pop (Lấy ra)",   "btn-pop");
        Button btnPeek  = makeBtn("Xem đỉnh (Peek)","btn-peek");
        Button btnReset = makeBtn("Khởi tạo lại",   "btn-reset");

        HBox row1 = hRow(btnPush, btnPop);
        HBox row2 = hRow(btnPeek, btnReset);

        Label statusHeader = new Label("ℹ  TRẠNG THÁI MÔ PHỎNG");
        statusHeader.getStyleClass().add("status-header");
        statusText = new Label("Hệ thống đã sẵn sàng. Hãy chọn một thao tác.");
        statusText.getStyleClass().add("status-text");
        statusText.setWrapText(true);

        VBox statusBox = new VBox(6, statusHeader, statusText);
        statusBox.getStyleClass().add("status-box");
        statusBox.setPadding(new Insets(12, 14, 12, 14));
        VBox.setVgrow(statusBox, Priority.ALWAYS);

        panel.getChildren().addAll(
                title, desc, divider(),
                sectionOp, inputLabel, inputField,
                row1, row2, divider(), statusBox
        );
        return panel;
    }

    private Region divider() {
        Region r = new Region();
        r.getStyleClass().add("divider-line");
        r.setMaxWidth(Double.MAX_VALUE);
        return r;
    }

    private Button makeBtn(String text, String styleClass) {
        Button b = new Button(text);
        b.getStyleClass().add(styleClass);
        b.setMaxWidth(Double.MAX_VALUE);
        return b;
    }

    private HBox hRow(Button a, Button b) {
        HBox row = new HBox(8, a, b);
        HBox.setHgrow(a, Priority.ALWAYS);
        HBox.setHgrow(b, Priority.ALWAYS);
        return row;
    }
}
