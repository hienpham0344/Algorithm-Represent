package com.example.main;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

public final class DashboardApplication extends Application {

    // Kích thước tối thiểu
    private static final double MIN_WINDOW_WIDTH = 900;
    private static final double MIN_WINDOW_HEIGHT = 600;

    @Override
    public void start(Stage stage) throws Exception {
        SceneManager.setPrimaryStage(stage);

        stage.setTitle("Algorithm Visualizer");
        stage.setMinWidth(MIN_WINDOW_WIDTH);
        stage.setMinHeight(MIN_WINDOW_HEIGHT);

        // Đặt lại vị trí và kích thước cửa sổ theo màn hình chính để tránh bị lệch hoặc cắt
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());

        // Bắt đầu ở màn hình đăng nhập
        SceneManager.showLogin();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        var context = SortAlgorithmPresentApplication.context();
        if (context == null) {
            return;
        }
        context.close();
    }
}
