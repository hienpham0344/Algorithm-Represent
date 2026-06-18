package com.example.main.ui;

import com.example.main.service.DatabaseService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;

    private final DatabaseService dbService = DatabaseService.getInstance();

    @FXML
    public void initialize() {
        // Enter key trigger registration
        confirmPasswordField.setOnAction(event -> handleRegister());
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validation
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Username and password are required.");
            return;
        }

        if (username.length() < 3) {
            showError("Username must be at least 3 characters.");
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }

        // Kiểm tra username đã tồn tại chưa
        if (dbService.isUsernameTaken(username)) {
            showError("Username already exists. Please choose another.");
            return;
        }

        // Validate email format (basic)
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            showError("Invalid email format.");
            return;
        }

        // Register user
        boolean success = dbService.registerUser(username, password, fullName, email);

        if (success) {
            showSuccess("Account created successfully! Redirecting to login...");
            
            // Delay 1.5 giây rồi chuyển về login
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(this::handleBackToLogin);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            
        } else {
            showError("Registration failed. Please try again.");
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login-view.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().clear();
        messageLabel.getStyleClass().add("error-label");
        messageLabel.setManaged(true);
        messageLabel.setVisible(true);
    }

    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().clear();
        messageLabel.getStyleClass().add("success-label");
        messageLabel.setManaged(true);
        messageLabel.setVisible(true);
    }
}
