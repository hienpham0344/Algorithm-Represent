package com.example.main.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ReportDialog {

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final List<ReportEntry> HISTORY = new ArrayList<>();

    private ReportDialog() {
    }

    public static void show(Window owner, String currentModule) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Reports");
        dialog.setHeaderText("Reports");
        if (owner != null) {
            dialog.initOwner(owner);
        }

        TextArea reportInput = createReportInput();
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getStyleClass().add("reports-tabs");
        tabs.getTabs().addAll(
                new Tab("New reports", buildNewReportTab(currentModule, reportInput)),
                new Tab("History", buildHistoryTab())
        );

        dialog.getDialogPane().setContent(tabs);
        ButtonType submitButton = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButton, ButtonType.CANCEL);
        styleDialogPane(dialog);
        styleTabPane(dialog);
        styleDialogButtons(dialog, submitButton);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == submitButton) {
            HISTORY.add(0, new ReportEntry(
                    LocalDateTime.now().format(TIME_FORMAT),
                    currentModule,
                    reportInput.getText().trim()
            ));
        }
    }

    private static VBox buildNewReportTab(String moduleName, TextArea reportInput) {
        Label moduleLabel = new Label("Module: " + moduleName);
        moduleLabel.setStyle("-fx-text-fill: #A5B4FC; -fx-font-weight: bold;");

        VBox content = new VBox(10, moduleLabel, reportInput);
        content.setPadding(new Insets(14));
        content.setStyle("-fx-background-color: #0F172A;");
        return content;
    }

    private static ScrollPane buildHistoryTab() {
        VBox list = new VBox(10);
        list.setPadding(new Insets(14));
        list.setStyle("-fx-background-color: #0F172A;");

        if (HISTORY.isEmpty()) {
            Label empty = new Label("No reports yet.");
            empty.setStyle("-fx-text-fill: #64748B; -fx-font-weight: bold;");
            list.getChildren().add(empty);
        } else {
            for (ReportEntry entry : HISTORY) {
                list.getChildren().add(createHistoryCard(entry));
            }
        }

        ScrollPane scrollPane = new ScrollPane(list);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #0F172A; -fx-background: #0F172A;");
        return scrollPane;
    }

    private static VBox createHistoryCard(ReportEntry entry) {
        Label meta = new Label(entry.time() + "  |  " + entry.module());
        meta.setStyle("-fx-text-fill: #A5B4FC; -fx-font-size: 11px; -fx-font-weight: bold;");

        Label content = new Label(entry.content().isBlank() ? "(No description)" : entry.content());
        content.setWrapText(true);
        content.setStyle("-fx-text-fill: #E2E8F0; -fx-font-size: 12px;");

        VBox card = new VBox(6, meta, content);
        card.setPadding(new Insets(10));
        card.setStyle("""
                -fx-background-color: #020817;
                -fx-border-color: #334155;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                """);
        return card;
    }

    private static TextArea createReportInput() {
        TextArea reportInput = new TextArea();
        reportInput.setPromptText("Describe what went wrong...");
        reportInput.setWrapText(true);
        reportInput.setPrefRowCount(7);
        reportInput.setStyle("""
                -fx-control-inner-background: #020817;
                -fx-background-color: #020817;
                -fx-text-fill: #E2E8F0;
                -fx-prompt-text-fill: #64748B;
                -fx-border-color: #334155;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                -fx-font-family: "Segoe UI";
                """);
        return reportInput;
    }

    private static void styleDialogPane(Dialog<?> dialog) {
        dialog.getDialogPane().setPrefWidth(560);
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

    private static void styleTabPane(Dialog<?> dialog) {
        dialog.getDialogPane().getStylesheets().add(
                ReportDialog.class.getResource("/styles/report-dialog.css").toExternalForm()
        );
    }

    private static void styleDialogButtons(Dialog<ButtonType> dialog, ButtonType submitButton) {
        Button submit = (Button) dialog.getDialogPane().lookupButton(submitButton);
        Button cancel = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        submit.setStyle("""
                -fx-background-color: #DC2626;
                -fx-text-fill: #FFFFFF;
                -fx-font-weight: bold;
                -fx-background-radius: 8;
                -fx-padding: 8 14;
                -fx-cursor: hand;
                """);
        cancel.setStyle("""
                -fx-background-color: transparent;
                -fx-text-fill: #CBD5E1;
                -fx-border-color: #475569;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                -fx-padding: 8 14;
                -fx-cursor: hand;
                """);
    }

    private record ReportEntry(String time, String module, String content) {
    }
}
