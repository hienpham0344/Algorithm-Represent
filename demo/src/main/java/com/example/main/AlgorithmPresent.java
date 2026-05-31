package com.example.main;

import com.example.main.ui.SortVisualizerView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AlgorithmPresent extends Application {

    @Override
    public void start(Stage stage) {
        SortVisualizerView root = new SortVisualizerView();
        Scene scene = new Scene(root, 1400, 860);
        scene.getStylesheets().add(getClass().getResource("/styles/sort.css").toExternalForm());

        stage.setTitle("Sorting Algorithm Visualizer");
        stage.setMinWidth(1200);
        stage.setMinHeight(760);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
