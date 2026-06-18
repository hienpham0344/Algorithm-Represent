package com.example.main.view;

import com.example.main.SortAlgorithmPresentApplication;
import com.example.main.dto.request.UserAccountRequest;
import com.example.main.dto.response.UserAccountResponse;
import com.example.main.enums.Role;
import com.example.main.service.UserAccountService;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Màn hình quản lý tài khoản dành cho ADMIN: hiển thị danh sách, sửa mật khẩu / vai trò
 * / trạng thái và xóa tài khoản. Cùng concept (theme tối, style-class qua admin.css)
 * với các page data-structure khác.
 */
public class AccountManagementView extends VBox {

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    private final TableView<UserAccountResponse> table = new TableView<>();
    private final ObservableList<UserAccountResponse> data = FXCollections.observableArrayList();

    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final ComboBox<Role> roleCombo = new ComboBox<>();
    private final TextField statusField = new TextField();
    private final Button saveBtn = button("Thêm mới", "btn-primary");

    private Long editingId = null;

    public AccountManagementView() {
        setSpacing(16);
        setPadding(new Insets(20));
        getStyleClass().add("admin-root");
        getStylesheets().add(getClass().getResource("/styles/admin.css").toExternalForm());

        Label title = new Label("Quản lý tài khoản");
        title.getStyleClass().add("admin-title");

        Label subtitle = new Label("Chọn một tài khoản trong bảng để sửa mật khẩu / vai trò / trạng thái hoặc xóa.");
        subtitle.getStyleClass().add("admin-subtitle");

        buildTable();
        VBox form = buildForm();

        getChildren().addAll(title, subtitle, table, form);
        VBox.setVgrow(table, Priority.ALWAYS);

        reload();
    }

    private void buildTable() {
        TableColumn<UserAccountResponse, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().id())));
        idCol.setPrefWidth(60);

        TableColumn<UserAccountResponse, String> userCol = new TableColumn<>("Tên đăng nhập");
        userCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().username()));
        userCol.setPrefWidth(220);

        TableColumn<UserAccountResponse, String> roleCol = new TableColumn<>("Vai trò");
        roleCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().role() == null ? "" : c.getValue().role().name()));
        roleCol.setPrefWidth(120);

        TableColumn<UserAccountResponse, String> statusCol = new TableColumn<>("Trạng thái");
        statusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().status()));
        statusCol.setPrefWidth(140);

        TableColumn<UserAccountResponse, String> createdCol = new TableColumn<>("Ngày tạo");
        createdCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().createdAt() == null ? "-" : TIME_FORMAT.format(c.getValue().createdAt())));
        createdCol.setPrefWidth(190);

        table.getColumns().add(idCol);
        table.getColumns().add(userCol);
        table.getColumns().add(roleCol);
        table.getColumns().add(statusCol);
        table.getColumns().add(createdCol);
        table.setItems(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("Chưa có tài khoản nào."));
        table.getStyleClass().add("admin-table");

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                fillForm(selected);
            }
        });
    }

    private VBox buildForm() {
        roleCombo.getItems().setAll(Role.values());
        roleCombo.setValue(Role.USER);
        roleCombo.getStyleClass().add("admin-combo");
        roleCombo.getStylesheets().add(getClass().getResource("/styles/admin.css").toExternalForm());
        // Áp style cho popup của ComboBox.
        roleCombo.setStyle("-fx-font-size: 13px;");
        statusField.setPromptText("ACTIVE");
        usernameField.setPromptText("Tên đăng nhập");
        passwordField.setPromptText("Mật khẩu mới (để trống khi sửa = giữ nguyên)");
        usernameField.getStyleClass().add("input-field");
        passwordField.getStyleClass().add("input-field");
        statusField.getStyleClass().add("input-field");

        HBox row1 = new HBox(10, labeled("Tên đăng nhập", usernameField), labeled("Mật khẩu", passwordField));
        HBox row2 = new HBox(10, labeled("Vai trò", roleCombo), labeled("Trạng thái", statusField));
        HBox.setHgrow(usernameField, Priority.ALWAYS);
        HBox.setHgrow(passwordField, Priority.ALWAYS);
        HBox.setHgrow(roleCombo, Priority.ALWAYS);
        HBox.setHgrow(statusField, Priority.ALWAYS);
        roleCombo.setMaxWidth(Double.MAX_VALUE);

        saveBtn.setOnAction(e -> handleSave());

        Button clearBtn = button("Làm mới form", "btn-secondary");
        clearBtn.setOnAction(e -> clearForm());

        Button deleteBtn = button("Xóa", "btn-danger");
        deleteBtn.setOnAction(e -> handleDelete());

        Button reloadBtn = button("Tải lại danh sách", "btn-secondary");
        reloadBtn.setOnAction(e -> reload());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox actions = new HBox(10, saveBtn, clearBtn, deleteBtn, spacer, reloadBtn);
        actions.setAlignment(Pos.CENTER_LEFT);

        VBox form = new VBox(12, row1, row2, actions);
        form.setPadding(new Insets(16));
        form.getStyleClass().add("admin-card");
        return form;
    }

    private VBox labeled(String text, Region field) {
        Label label = new Label(text);
        label.getStyleClass().add("field-label");
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
        saveBtn.setText("Cập nhật");
    }

    private void clearForm() {
        editingId = null;
        usernameField.clear();
        passwordField.clear();
        roleCombo.setValue(Role.USER);
        statusField.clear();
        table.getSelectionModel().clearSelection();
        saveBtn.setText("Thêm mới");
    }

    private void reload() {
        UserAccountService service = service();
        if (service == null) {
            warn("Không kết nối được cơ sở dữ liệu.");
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

    private Button button(String text, String styleClass) {
        Button b = new Button(text);
        b.getStyleClass().add(styleClass);
        return b;
    }
}
