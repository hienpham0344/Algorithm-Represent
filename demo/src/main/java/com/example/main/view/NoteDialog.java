package com.example.main.view;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class NoteDialog {

    private static final Map<String, String> NOTES_BY_MODULE = new HashMap<>();

    private NoteDialog() {
    }

    public static void show(Window owner, String moduleName) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Notes");
        dialog.setHeaderText("Notes for " + moduleName);
        if (owner != null) {
            dialog.initOwner(owner);
        }
        styleDialogPane(dialog);

        TextArea noteInput = new TextArea(NOTES_BY_MODULE.getOrDefault(moduleName, ""));
        noteInput.setPromptText("Write your notes here...");
        noteInput.setWrapText(true);
        noteInput.setPrefRowCount(6);
        noteInput.setStyle("""
                -fx-control-inner-background: #020817;
                -fx-background-color: #020817;
                -fx-text-fill: #E2E8F0;
                -fx-prompt-text-fill: #64748B;
                -fx-border-color: #334155;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                -fx-font-family: "Segoe UI";
                """);

        VBox content = new VBox(8, noteInput);
        content.setPadding(new Insets(8, 0, 0, 0));
        dialog.getDialogPane().setContent(content);

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);
        styleDialogButtons(dialog);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == saveButton) {
            NOTES_BY_MODULE.put(moduleName, noteInput.getText());
            showSavedMessage(owner);
        }
    }

    private static void showSavedMessage(Window owner) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notes");
        alert.setHeaderText(null);
        alert.setContentText("Note saved successfully.");
        if (owner != null) {
            alert.initOwner(owner);
        }
        styleDialogPane(alert);
        styleAlertButton(alert);
        alert.showAndWait();
    }

    private static void styleDialogPane(Dialog<?> dialog) {
        dialog.getDialogPane().setStyle("""
                -fx-background-color: #0F172A;
                -fx-border-color: #334155;
                -fx-border-width: 1;
                -fx-font-family: "Segoe UI";
                """);
        dialog.getDialogPane().lookupAll(".label").forEach(node ->
                node.setStyle("-fx-text-fill: #E2E8F0;"));
        if (dialog.getDialogPane().lookup(".header-panel") != null) {
            dialog.getDialogPane().lookup(".header-panel").setStyle("-fx-background-color: #0A1020;");
        }
    }

    private static void styleDialogButtons(Dialog<ButtonType> dialog) {
        Button save = (Button) dialog.getDialogPane().lookupButton(
                dialog.getDialogPane().getButtonTypes().get(0));
        Button cancel = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        stylePrimaryButton(save);
        styleSecondaryButton(cancel);
    }

    private static void styleAlertButton(Alert alert) {
        Button ok = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        stylePrimaryButton(ok);
    }

    private static void stylePrimaryButton(Button button) {
        button.setStyle("""
                -fx-background-color: #4F46E5;
                -fx-text-fill: #FFFFFF;
                -fx-font-weight: bold;
                -fx-background-radius: 8;
                -fx-padding: 8 14;
                -fx-cursor: hand;
                """);
    }

    private static void styleSecondaryButton(Button button) {
        button.setStyle("""
                -fx-background-color: transparent;
                -fx-text-fill: #CBD5E1;
                -fx-border-color: #475569;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                -fx-padding: 8 14;
                -fx-cursor: hand;
                """);
    }
}
