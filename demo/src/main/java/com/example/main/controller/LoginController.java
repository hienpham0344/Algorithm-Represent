package com.example.main.controller;

import com.example.main.SceneManager;
import com.example.main.SortAlgorithmPresentApplication;
import com.example.main.dto.response.UserAccountResponse;
import com.example.main.service.UserAccountService;
import com.example.main.session.Session;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.Optional;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;

    @FXML
    public void handleLogin() {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Vui lòng nhập tên đăng nhập và mật khẩu.");
            return;
        }

        UserAccountService service = userAccountService();
        if (service == null) {
            showError("Không kết nối được cơ sở dữ liệu.");
            return;
        }

        Optional<UserAccountResponse> result = service.authenticate(username, password);
        if (result.isEmpty()) {
            showError("Sai tên đăng nhập hoặc mật khẩu.");
            passwordField.clear();
            return;
        }

        Session.setCurrentUser(result.get());
        SceneManager.showDashboard();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private UserAccountService userAccountService() {
        if (SortAlgorithmPresentApplication.context() == null) {
            return null;
        }
        return SortAlgorithmPresentApplication.context().getBean(UserAccountService.class);
    }
}
