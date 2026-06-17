package com.example.main.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class DashboardController {

    @FXML
    private StackPane contentPane;

    @FXML
    private VBox sidebar;

    @FXML private Button btnSort;
    @FXML private Button btnArray;
    @FXML private Button btnLinkedList;
    @FXML private Button btnStack;
    @FXML private Button btnQueue;
    @FXML private Button btnBinaryTree;

    private boolean sidebarVisible = false;

    @FXML
    public void initialize() {
        // Ẩn sidebar lúc đầu
        sidebar.setManaged(false);
        sidebar.setVisible(false);

        // Load sorting screen mặc định và đặt nó làm active luôn
        openSortingVisualizer();

    }

    @FXML
    public void toggleSidebar() {
        sidebarVisible = !sidebarVisible;
        sidebar.setVisible(sidebarVisible);
        sidebar.setManaged(sidebarVisible);
    }

    // Hàm dùng chung để đổi màu nút đang chọn
    private void setActiveButton(Button activeButton) {
        // Danh sách tất cả các nút sidebar
        Button[] allButtons = {btnSort, btnArray, btnLinkedList, btnStack, btnQueue, btnBinaryTree};

        for (Button btn : allButtons) {
            if (btn != null) {
                // Xóa class active cũ nếu có
                btn.getStyleClass().remove("sidebar-btn-active");
            }
        }

        // Thêm class active cho nút vừa được click
        if (activeButton != null && !activeButton.getStyleClass().contains("sidebar-btn-active")) {
            activeButton.getStyleClass().add("sidebar-btn-active");
        }
    }

    @FXML
    public void openSortingVisualizer() {
        SortVisualizerView visualizer = new SortVisualizerView();
        SortViewContainer container = new SortViewContainer(visualizer);

        container.getStylesheets().add(
                getClass().getResource("/styles/sort.css").toExternalForm()
        );

        contentPane.getChildren().setAll(container);

        setActiveButton(btnSort);

        closeSidebar();
    }

    @FXML
    public void openArray() {
        try {
            Parent view = FXMLLoader.load(
                    getClass().getResource("/fxml/array-view.fxml")
            );
            contentPane.getChildren().setAll(view);
            setActiveButton(btnArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        closeSidebar();
    }

    @FXML
    public void openLinkedList() {
        try {
            Parent view = FXMLLoader.load(
                    getClass().getResource("/fxml/LinkedList.fxml")
            );
            contentPane.getChildren().setAll(view);

            setActiveButton(btnLinkedList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        closeSidebar();
    }

    @FXML
    public void openStack() {
        StackVisualizerView view = new StackVisualizerView();
        contentPane.getChildren().setAll(view);

        setActiveButton(btnStack);

        closeSidebar();
    }

    @FXML
    public void openQueue() {
        try {
            Parent view = FXMLLoader.load(
                    getClass().getResource("/fxml/queue-view.fxml")
            );
            contentPane.getChildren().setAll(view);

            setActiveButton(btnQueue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        closeSidebar();
    }

    @FXML
    public void openBinaryTree() {
        BinaryTreeVisualizerView view = new BinaryTreeVisualizerView();
        contentPane.getChildren().setAll(view);

        setActiveButton(btnBinaryTree);

        closeSidebar();
    }

    private void closeSidebar() {
        sidebar.setVisible(false);
        sidebar.setManaged(false);
        sidebarVisible = false;
    }
}
