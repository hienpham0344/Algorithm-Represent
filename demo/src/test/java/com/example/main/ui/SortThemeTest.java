package com.example.main.ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SortThemeTest {

    @Test
    void exposesNavyIndigoBarPalette() {
        assertEquals("#4F46E5", SortTheme.DEFAULT_BAR);
        assertEquals("#F59E0B", SortTheme.COMPARE_BAR);
        assertEquals("#EF4444", SortTheme.SWAP_BAR);
        assertEquals("#10B981", SortTheme.SORTED_BAR);
    }

    @Test
    void exposesSemanticButtonStyleClasses() {
        assertEquals("btn-sort", SortTheme.PRIMARY_BUTTON);
        assertEquals("btn-create", SortTheme.SUCCESS_BUTTON);
        assertEquals("btn-import", SortTheme.INFO_BUTTON);
        assertEquals("btn-danger", SortTheme.DANGER_BUTTON);
        assertEquals("btn-secondary", SortTheme.SECONDARY_BUTTON);
    }
}
