package com.example.main.view;

import com.example.main.SortAlgorithmPresentApplication;
import com.example.main.dto.response.UserReportResponse;
import com.example.main.service.ReportService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Màn hình xem toàn bộ report của người dùng dành cho ADMIN.
 * Cùng concept (theme tối, style-class qua admin.css) với {@link AccountManagementView}
 * và các page data-structure khác.
 */
public class ReportManagementView extends VBox {

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    private final TableView<UserReportResponse> table = new TableView<>();
    private final ObservableList<UserReportResponse> data = FXCollections.observableArrayList();
    private final FilteredList<UserReportResponse> filtered = new FilteredList<>(data, r -> true);
    private final TextField searchField = new TextField();
    private final Label countLabel = new Label();

    public ReportManagementView() {
        setSpacing(16);
        setPadding(new Insets(20));
        getStyleClass().add("admin-root");
        getStylesheets().add(getClass().getResource("/styles/admin.css").toExternalForm());

        Label title = new Label("Tất cả báo cáo");
        title.getStyleClass().add("admin-title");

        Label subtitle = new Label("Toàn bộ báo cáo người dùng gửi từ các module. Dùng ô tìm kiếm để lọc nhanh.");
        subtitle.getStyleClass().add("admin-subtitle");

        HBox toolbar = buildToolbar();
        buildTable();

        getChildren().addAll(title, subtitle, toolbar, table);
        VBox.setVgrow(table, Priority.ALWAYS);

        reload();
    }

    private HBox buildToolbar() {
        searchField.setPromptText("Tìm theo người dùng, module hoặc nội dung...");
        searchField.setPrefWidth(360);
        searchField.getStyleClass().add("input-field");
        searchField.textProperty().addListener((obs, old, keyword) -> applyFilter(keyword));

        countLabel.getStyleClass().add("count-label");

        Button reloadBtn = button("Tải lại", "btn-secondary");
        reloadBtn.setOnAction(e -> reload());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox toolbar = new HBox(10, searchField, countLabel, spacer, reloadBtn);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        return toolbar;
    }

    private void buildTable() {
        TableColumn<UserReportResponse, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().id())));
        idCol.setPrefWidth(60);

        TableColumn<UserReportResponse, String> timeCol = new TableColumn<>("Thời gian");
        timeCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().createdAt() == null ? "-" : TIME_FORMAT.format(c.getValue().createdAt())));
        timeCol.setPrefWidth(170);

        TableColumn<UserReportResponse, String> userCol = new TableColumn<>("Người dùng");
        userCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().username()));
        userCol.setPrefWidth(160);

        TableColumn<UserReportResponse, String> moduleCol = new TableColumn<>("Module");
        moduleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().module()));
        moduleCol.setPrefWidth(150);

        TableColumn<UserReportResponse, String> contentCol = new TableColumn<>("Nội dung");
        contentCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().content()));
        contentCol.setPrefWidth(360);
        contentCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String value, boolean empty) {
                super.updateItem(value, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(value == null || value.isBlank() ? "(Không có mô tả)" : value);
                }
                setWrapText(true);
            }
        });

        table.getColumns().add(idCol);
        table.getColumns().add(timeCol);
        table.getColumns().add(userCol);
        table.getColumns().add(moduleCol);
        table.getColumns().add(contentCol);
        table.setItems(filtered);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("Chưa có báo cáo nào."));
        table.getStyleClass().add("admin-table");
    }

    private void applyFilter(String keyword) {
        String needle = keyword == null ? "" : keyword.trim().toLowerCase();
        filtered.setPredicate(report -> {
            if (needle.isEmpty()) {
                return true;
            }
            return contains(report.username(), needle)
                    || contains(report.module(), needle)
                    || contains(report.content(), needle);
        });
        updateCount();
    }

    private boolean contains(String value, String needle) {
        return value != null && value.toLowerCase().contains(needle);
    }

    private void reload() {
        ReportService service = service();
        if (service == null) {
            warn("Không kết nối được cơ sở dữ liệu.");
            return;
        }
        data.setAll(service.findAll());
        updateCount();
    }

    private void updateCount() {
        countLabel.setText(filtered.size() + " / " + data.size() + " báo cáo");
    }

    private void warn(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private ReportService service() {
        if (SortAlgorithmPresentApplication.context() == null) {
            return null;
        }
        return SortAlgorithmPresentApplication.context().getBean(ReportService.class);
    }

    private Button button(String text, String styleClass) {
        Button b = new Button(text);
        b.getStyleClass().add(styleClass);
        return b;
    }
}
