package com.example.main.ui;

import com.example.main.DashboardApplication;
import com.example.main.service.DatabaseService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final DatabaseService dbService = DatabaseService.getInstance();

    @FXML
    public void initialize() {
        // Enter key trên password field sẽ trigger login
        passwordField.setOnAction(event -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        // Authenticate
        if (dbService.authenticateUser(username, password)) {
            // Login thành công
            hideError();
            
            // Lưu thông tin user hiện tại
            DatabaseService.UserInfo userInfo = dbService.getUserInfo(username);
            SessionManager.setCurrentUser(userInfo);
            
            // Chuyển sang dashboard
            openDashboard();
        } else {
            showError("Invalid username or password.");
        }
    }

    @FXML
    private void handleShowRegister() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register-view.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load registration screen.");
        }
    }

    private void openDashboard() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            
            // Load dashboard
            FXMLLoader loader = new FXMLLoader(DashboardApplication.class.getResource("/fxml/dashboard-view.fxml"));
            Scene scene = new Scene(loader.load(), 1280, 720);
            scene.getStylesheets().add(DashboardApplication.class.getResource("/styles/dashboard.css").toExternalForm());
            
            stage.setTitle("Algorithm Visualizer - Welcome " + SessionManager.getCurrentUser().getFullName());
            stage.setScene(scene);
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load dashboard.");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setManaged(true);
        errorLabel.setVisible(true);
    }

    private void hideError() {
        errorLabel.setManaged(false);
        errorLabel.setVisible(false);
    }
}
