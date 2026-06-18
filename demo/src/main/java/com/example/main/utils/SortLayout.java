package com.example.main.utils;

import javafx.scene.control.ScrollPane;

public final class SortLayout {
    // Giới hạn chiều rộng responsive để các điều khiển vẫn hiển thị đầy đủ khi cửa sổ ở mức tối thiểu 900px
    public static final double CODE_PANEL_MIN_WIDTH = 480;
    public static final double CODE_PANEL_PREF_WIDTH = 520;
    public static final double CODE_PANEL_MAX_WIDTH = 680;
    public static final double SIZE_CARD_MIN_WIDTH = 180;
    public static final double SIZE_CARD_PREF_WIDTH = 240;
    public static final double BUTTONS_CARD_MIN_WIDTH = 340;
    public static final double SWAP_CARD_MIN_WIDTH = 110;
    public static final double SPEED_CARD_MIN_WIDTH = 180;
    public static final double CREATE_CARD_MIN_WIDTH = 130;
    public static final boolean PAGE_FIT_TO_WIDTH = true;
    public static final ScrollPane.ScrollBarPolicy PAGE_VERTICAL_SCROLL_POLICY =
            ScrollPane.ScrollBarPolicy.AS_NEEDED;
    public static final ScrollPane.ScrollBarPolicy PAGE_HORIZONTAL_SCROLL_POLICY =
            ScrollPane.ScrollBarPolicy.NEVER;
    private static final double CHART_HORIZONTAL_PADDING = 32;

    private SortLayout() {
    }

    public static double barWidth(int itemCount) {
        if (itemCount > 20) {
            return 32;
        }
        if (itemCount > 12) {
            return 38;
        }
        if (itemCount > 8) {
            return 40;
        }
        return 44;
    }

    public static double barGap(int itemCount) {
        if (itemCount > 20) {
            return 6;
        }
        if (itemCount > 12) {
            return 7;
        }
        if (itemCount > 8) {
            return 8;
        }
        return 9;
    }

    public static double chartContentWidth(int itemCount) {
        if (itemCount <= 0) {
            return 0;
        }
        return itemCount * barWidth(itemCount)
                + (itemCount - 1) * barGap(itemCount)
                + CHART_HORIZONTAL_PADDING;
    }
}



