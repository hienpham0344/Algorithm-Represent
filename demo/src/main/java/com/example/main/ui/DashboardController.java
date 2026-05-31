
package com.example.main.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class DashboardController {

    @FXML
    private StackPane contentPane;

    @FXML
    private VBox sidebar;

    private boolean sidebarVisible = false;

    @FXML
    public void initialize() {

        // Ẩn sidebar lúc đầu
        sidebar.setManaged(false);
        sidebar.setVisible(false);

        // Load sorting screen mặc định
        openSortingVisualizer();
    }

    @FXML
    public void toggleSidebar() {

        sidebarVisible = !sidebarVisible;

        sidebar.setVisible(sidebarVisible);
        sidebar.setManaged(sidebarVisible);
    }

    @FXML
    public void openSortingVisualizer() {

        SortVisualizerView visualizer = new SortVisualizerView();

        visualizer.getStylesheets().add(
                getClass().getResource("/styles/app.css").toExternalForm()
        );

        contentPane.getChildren().setAll(visualizer);

        // tự đóng sidebar sau khi chọn
        sidebar.setVisible(false);
        sidebar.setManaged(false);
        sidebarVisible = false;
    }

    @FXML
    public void openArray() {
        System.out.println("Array");
    }

    @FXML
    public void openLinkedList() {
        System.out.println("LinkedList");
    }

    @FXML
    public void openStack() {
        StackVisualizerView view = new StackVisualizerView();
        contentPane.getChildren().setAll(view);

        sidebar.setVisible(false);
        sidebar.setManaged(false);
        sidebarVisible = false;
    }

    @FXML
    public void openQueue() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/queue-view.fxml")
            );
            Parent view = loader.load();
            view.getStylesheets().add(
                    getClass().getResource("/styles/queue.css").toExternalForm()
            );
            contentPane.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sidebar.setVisible(false);
        sidebar.setManaged(false);
        sidebarVisible = false;
    }

    @FXML
    public void openBinaryTree() {
        BinaryTreeVisualizerView view = new BinaryTreeVisualizerView();
        contentPane.getChildren().setAll(view);

        sidebar.setVisible(false);
        sidebar.setManaged(false);
        sidebarVisible = false;
    }
}

