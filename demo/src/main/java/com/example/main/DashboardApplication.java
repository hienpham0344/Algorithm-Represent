package com.example.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class DashboardApplication extends Application {

    // Kích thước tối thiểu
    private static final double MIN_WINDOW_WIDTH = 900;
    private static final double MIN_WINDOW_HEIGHT = 600;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(
                DashboardApplication.class.getResource("/fxml/dashboard-view.fxml")
        );

        Scene scene = new Scene(root);
        scene.getStylesheets().add(
                DashboardApplication.class.getResource("/styles/dashboard.css").toExternalForm()
        );

        stage.setTitle("Algorithm Visualizer");
        stage.setScene(scene);
        stage.setMinWidth(MIN_WINDOW_WIDTH);
        stage.setMinHeight(MIN_WINDOW_HEIGHT);
        stage.show();
    }
}
