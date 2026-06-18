package com.example.main.utils;

import java.util.Locale;

public final class SortSpeed {
    public static final double MIN_RATE = 0.5;
    public static final double MAX_RATE = 3.0;
    public static final double DEFAULT_RATE = 1.0;
    public static final double BASE_STEP_MILLIS = 700;

    private SortSpeed() {
    }

    public static double timelineRate(double sliderValue) {
        return Math.max(MIN_RATE, Math.min(MAX_RATE, sliderValue));
    }

    public static String label(double sliderValue) {
        return String.format(Locale.ROOT, "%.1fx", timelineRate(sliderValue));
    }
}



