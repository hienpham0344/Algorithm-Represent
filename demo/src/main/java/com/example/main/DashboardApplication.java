package com.example.main;

import com.example.main.service.DatabaseService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public final class DashboardApplication extends Application {

    // Kích thước tối thiểu
    private static final double MIN_WINDOW_WIDTH = 900;
    private static final double MIN_WINDOW_HEIGHT = 600;

    @Override
    public void start(Stage stage) throws Exception {
        // Khởi tạo database
        DatabaseService.getInstance();
        
        // Bắt đầu từ login screen
        Parent root = FXMLLoader.load(
                DashboardApplication.class.getResource("/fxml/login-view.fxml")
        );

        Scene scene = new Scene(root, 800, 600);

        stage.setTitle("Algorithm Visualizer - Login");
        stage.setScene(scene);
        stage.setMinWidth(500);
        stage.setMinHeight(500);
        stage.centerOnScreen();
        stage.show();
    }
}
