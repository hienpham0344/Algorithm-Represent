package com.example.main;

import javafx.application.Application;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainLauncherTest {

    @Test
    void mainIsAPlainJavaLauncherAndDashboardOwnsJavaFxLifecycle() {
        assertFalse(Application.class.isAssignableFrom(Main.class));
        assertTrue(Application.class.isAssignableFrom(DashboardApplication.class));
    }
}
