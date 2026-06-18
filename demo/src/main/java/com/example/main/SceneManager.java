package com.example.main;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Quản lý chuyển màn hình giữa Login và Dashboard trên cùng một Stage.
 */
public final class SceneManager {

    private static Stage primaryStage;

    private SceneManager() {
    }

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage primaryStage() {
        return primaryStage;
    }

    public static void showLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource("/fxml/login-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    SceneManager.class.getResource("/styles/login.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource("/fxml/dashboard-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    SceneManager.class.getResource("/styles/dashboard.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();

            root.applyCss();
            root.layout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
