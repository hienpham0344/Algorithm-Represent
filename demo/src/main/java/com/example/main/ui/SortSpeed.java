package com.example.main.ui;

import java.util.Locale;

final class SortSpeed {
    static final double MIN_RATE = 0.5;
    static final double MAX_RATE = 3.0;
    static final double DEFAULT_RATE = 1.0;
    static final double BASE_STEP_MILLIS = 700;

    private SortSpeed() {
    }

    static double timelineRate(double sliderValue) {
        return Math.max(MIN_RATE, Math.min(MAX_RATE, sliderValue));
    }

    static String label(double sliderValue) {
        return String.format(Locale.ROOT, "%.1fx", timelineRate(sliderValue));
    }
}
