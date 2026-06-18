package com.example.main.ui;

import com.example.main.utils.SortSpeed;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SortSpeedTest {

    @Test
    void sliderValueMapsDirectlyToTimelineRate() {
        assertEquals(0.5, SortSpeed.timelineRate(0.5));
        assertEquals(1.0, SortSpeed.timelineRate(1.0));
        assertEquals(2.0, SortSpeed.timelineRate(2.0));
        assertEquals(3.0, SortSpeed.timelineRate(3.0));
    }

    @Test
    void outOfRangeValuesAreClamped() {
        assertEquals(0.5, SortSpeed.timelineRate(0.1));
        assertEquals(3.0, SortSpeed.timelineRate(4.0));
    }

    @Test
    void labelShowsOneDecimalPlaceAndMultiplier() {
        assertEquals("0.5x", SortSpeed.label(0.5));
        assertEquals("1.0x", SortSpeed.label(1.0));
        assertEquals("2.5x", SortSpeed.label(2.5));
        assertEquals("3.0x", SortSpeed.label(3.0));
    }
}
