package com.example.main.view;

import com.example.main.SortAlgorithmPresentApplication;
import com.example.main.dto.request.UserAccountRequest;
import com.example.main.dto.response.UserAccountResponse;
import com.example.main.enums.Role;
import com.example.main.service.UserAccountService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.Instant;
import java.util.Optional;

/**
 * Màn hình quản lý tài khoản dành cho ADMIN: thêm / sửa / xóa dựa trên CRUD database.
 */
public class AccountManagementView extends VBox {

    private final TableView<UserAccountResponse> table = new TableView<>();
    private final ObservableList<UserAccountResponse> data = FXCollections.observableArrayList();

    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final ComboBox<Role> roleCombo = new ComboBox<>();
    private final TextField statusField = new TextField();

    private Long editingId = null;

    public AccountManagementView() {
        setSpacing(16);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #F8FAFC;");

        Label title = new Label("Quản lý tài khoản");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        buildTable();
        VBox form = buildForm();

        getChildren().addAll(title, table, form);
        VBox.setVgrow(table, Priority.ALWAYS);

        reload();
    }

    @SuppressWarnings("unchecked")
    private void buildTable() {
        TableColumn<UserAccountResponse, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(60);

        TableColumn<UserAccountResponse, String> userCol = new TableColumn<>("Tên đăng nhập");
        userCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        userCol.setPrefWidth(220);

        TableColumn<UserAccountResponse, Role> roleCol = new TableColumn<>("Vai trò");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleCol.setPrefWidth(120);

        TableColumn<UserAccountResponse, String> statusCol = new TableColumn<>("Trạng thái");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(140);

        TableColumn<UserAccountResponse, Instant> createdCol = new TableColumn<>("Ngày tạo");
        createdCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        createdCol.setPrefWidth(220);

        table.getColumns().addAll(idCol, userCol, roleCol, statusCol, createdCol);
        table.setItems(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                fillForm(selected);
            }
        });
    }

    private VBox buildForm() {
        roleCombo.getItems().setAll(Role.values());
        roleCombo.setValue(Role.USER);
        statusField.setPromptText("ACTIVE");
        usernameField.setPromptText("Tên đăng nhập");
        passwordField.setPromptText("Mật khẩu (để trống khi sửa = giữ nguyên)");

        HBox row1 = new HBox(10, labeled("Tên đăng nhập", usernameField), labeled("Mật khẩu", passwordField));
        HBox row2 = new HBox(10, labeled("Vai trò", roleCombo), labeled("Trạng thái", statusField));
        HBox.setHgrow(usernameField, Priority.ALWAYS);
        HBox.setHgrow(passwordField, Priority.ALWAYS);
        HBox.setHgrow(roleCombo, Priority.ALWAYS);
        HBox.setHgrow(statusField, Priority.ALWAYS);
        roleCombo.setMaxWidth(Double.MAX_VALUE);

        Button addBtn = primary("Thêm mới");
        addBtn.setOnAction(e -> handleSave());

        Button clearBtn = secondary("Làm mới form");
        clearBtn.setOnAction(e -> clearForm());

        Button deleteBtn = danger("Xóa");
        deleteBtn.setOnAction(e -> handleDelete());

        Button reloadBtn = secondary("Tải lại danh sách");
        reloadBtn.setOnAction(e -> reload());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox actions = new HBox(10, addBtn, clearBtn, deleteBtn, spacer, reloadBtn);
        actions.setAlignment(Pos.CENTER_LEFT);

        VBox form = new VBox(12, row1, row2, actions);
        form.setPadding(new Insets(16));
        form.setStyle("""
                -fx-background-color: white;
                -fx-background-radius: 12;
                -fx-border-radius: 12;
                -fx-border-color: #E2E8F0;
                -fx-border-width: 1;
                """);
        return form;
    }

    private VBox labeled(String text, Region field) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #475569;");
        VBox box = new VBox(4, label, field);
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }

    private void handleSave() {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        if (username.isEmpty()) {
            warn("Tên đăng nhập không được để trống.");
            return;
        }

        Role role = roleCombo.getValue() == null ? Role.USER : roleCombo.getValue();
        String status = statusField.getText() == null || statusField.getText().isBlank()
                ? "ACTIVE" : statusField.getText().trim();
        String password = passwordField.getText();

        UserAccountService service = service();
        if (service == null) {
            warn("Không kết nối được cơ sở dữ liệu.");
            return;
        }

        try {
            if (editingId == null) {
                if (password == null || password.isBlank()) {
                    warn("Cần nhập mật khẩu cho tài khoản mới.");
                    return;
                }
                service.create(new UserAccountRequest(username, password, role, status));
            } else {
                service.update(editingId, new UserAccountRequest(username, password, role, status));
            }
            clearForm();
            reload();
        } catch (Exception ex) {
            warn("Lỗi: " + ex.getMessage());
        }
    }

    private void handleDelete() {
        UserAccountResponse selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            warn("Chọn một tài khoản để xóa.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Xóa tài khoản \"" + selected.username() + "\"?",
                ButtonType.OK, ButtonType.CANCEL);
        confirm.setHeaderText(null);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            UserAccountService service = service();
            if (service == null) {
                warn("Không kết nối được cơ sở dữ liệu.");
                return;
            }
            try {
                service.delete(selected.id());
                clearForm();
                reload();
            } catch (Exception ex) {
                warn("Lỗi: " + ex.getMessage());
            }
        }
    }

    private void fillForm(UserAccountResponse user) {
        editingId = user.id();
        usernameField.setText(user.username());
        passwordField.clear();
        roleCombo.setValue(user.role());
        statusField.setText(user.status());
    }

    private void clearForm() {
        editingId = null;
        usernameField.clear();
        passwordField.clear();
        roleCombo.setValue(Role.USER);
        statusField.clear();
        table.getSelectionModel().clearSelection();
    }

    private void reload() {
        UserAccountService service = service();
        if (service == null) {
            return;
        }
        data.setAll(service.findAll());
    }

    private void warn(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private UserAccountService service() {
        if (SortAlgorithmPresentApplication.context() == null) {
            return null;
        }
        return SortAlgorithmPresentApplication.context().getBean(UserAccountService.class);
    }

    private Button primary(String text) {
        Button b = new Button(text);
        b.setStyle("""
                -fx-background-color: #4F46E5; -fx-text-fill: white; -fx-font-weight: bold;
                -fx-background-radius: 8; -fx-padding: 9 16; -fx-cursor: hand;
                """);
        return b;
    }

    private Button secondary(String text) {
        Button b = new Button(text);
        b.setStyle("""
                -fx-background-color: #E2E8F0; -fx-text-fill: #1E293B; -fx-font-weight: bold;
                -fx-background-radius: 8; -fx-padding: 9 16; -fx-cursor: hand;
                """);
        return b;
    }

    private Button danger(String text) {
        Button b = new Button(text);
        b.setStyle("""
                -fx-background-color: #DC2626; -fx-text-fill: white; -fx-font-weight: bold;
                -fx-background-radius: 8; -fx-padding: 9 16; -fx-cursor: hand;
                """);
        return b;
    }
}
