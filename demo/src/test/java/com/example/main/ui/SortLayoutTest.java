package com.example.main.ui;

import javafx.scene.control.ScrollPane;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SortLayoutTest {

    @Test
    void codePanelBalancesReadabilityWithResponsiveLayout() {
        assertEquals(320, SortLayout.CODE_PANEL_MIN_WIDTH);
        assertEquals(400, SortLayout.CODE_PANEL_PREF_WIDTH);
        assertTrue(SortLayout.CODE_PANEL_MAX_WIDTH >= SortLayout.CODE_PANEL_PREF_WIDTH);
    }

    @Test
    void controlCardsFitInsideTheMinimumWindowWidth() {
        double controlsWidth = SortLayout.BUTTONS_CARD_MIN_WIDTH
                + SortLayout.SWAP_CARD_MIN_WIDTH
                + SortLayout.SPEED_CARD_MIN_WIDTH
                + SortLayout.CREATE_CARD_MIN_WIDTH
                + 36;

        assertTrue(controlsWidth <= 856);
    }

    @Test
    void largeArraysKeepReadableBarsAndOverflowHorizontally() {
        assertTrue(SortLayout.barWidth(15) >= 38);
        assertTrue(SortLayout.chartContentWidth(15) >= 650);
    }

    @Test
    void smallArraysKeepTheExistingComfortableBarWidth() {
        assertEquals(44, SortLayout.barWidth(8));
        assertEquals(9, SortLayout.barGap(8));
    }

    @Test
    void sortingScreenUsesAScrollPaneForVerticalOverflow() {
        assertTrue(ScrollPane.class.isAssignableFrom(SortViewContainer.class));
        assertTrue(SortLayout.PAGE_FIT_TO_WIDTH);
        assertEquals(ScrollPane.ScrollBarPolicy.AS_NEEDED, SortLayout.PAGE_VERTICAL_SCROLL_POLICY);
        assertEquals(ScrollPane.ScrollBarPolicy.NEVER, SortLayout.PAGE_HORIZONTAL_SCROLL_POLICY);
    }
}
