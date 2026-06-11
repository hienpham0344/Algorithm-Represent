package com.example.main.ui;

import javafx.scene.control.ScrollPane;

public final class SortViewContainer extends ScrollPane {

    public SortViewContainer(SortVisualizerView visualizer) {
        super(visualizer);
        setFitToWidth(SortLayout.PAGE_FIT_TO_WIDTH);
        setHbarPolicy(SortLayout.PAGE_HORIZONTAL_SCROLL_POLICY);
        setVbarPolicy(SortLayout.PAGE_VERTICAL_SCROLL_POLICY);
        setPannable(true);
        getStyleClass().add("sort-page-scroll");

        viewportBoundsProperty().addListener((observable, oldBounds, newBounds) ->
                visualizer.setMinHeight(newBounds.getHeight()));
    }
}
