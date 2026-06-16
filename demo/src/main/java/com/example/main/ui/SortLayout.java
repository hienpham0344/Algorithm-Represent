package com.example.main.ui;

import javafx.scene.control.ScrollPane;

final class SortLayout {
    // Giới hạn chiều rộng responsive để các điều khiển vẫn hiển thị đầy đủ khi cửa sổ ở mức tối thiểu 900px
    static final double CODE_PANEL_MIN_WIDTH = 320;
    static final double CODE_PANEL_PREF_WIDTH = 400;
    static final double CODE_PANEL_MAX_WIDTH = 520;
    static final double SIZE_CARD_MIN_WIDTH = 180;
    static final double SIZE_CARD_PREF_WIDTH = 240;
    static final double BUTTONS_CARD_MIN_WIDTH = 340;
    static final double SWAP_CARD_MIN_WIDTH = 110;
    static final double SPEED_CARD_MIN_WIDTH = 180;
    static final double CREATE_CARD_MIN_WIDTH = 130;
    static final boolean PAGE_FIT_TO_WIDTH = true;
    static final ScrollPane.ScrollBarPolicy PAGE_VERTICAL_SCROLL_POLICY =
            ScrollPane.ScrollBarPolicy.AS_NEEDED;
    static final ScrollPane.ScrollBarPolicy PAGE_HORIZONTAL_SCROLL_POLICY =
            ScrollPane.ScrollBarPolicy.NEVER;
    private static final double CHART_HORIZONTAL_PADDING = 32;

    private SortLayout() {
    }

    static double barWidth(int itemCount) {
        if (itemCount > 12) {
            return 38;
        }
        if (itemCount > 8) {
            return 40;
        }
        return 44;
    }

    static double barGap(int itemCount) {
        if (itemCount > 12) {
            return 7;
        }
        if (itemCount > 8) {
            return 8;
        }
        return 9;
    }

    static double chartContentWidth(int itemCount) {
        if (itemCount <= 0) {
            return 0;
        }
        return itemCount * barWidth(itemCount)
                + (itemCount - 1) * barGap(itemCount)
                + CHART_HORIZONTAL_PADDING;
    }
}
