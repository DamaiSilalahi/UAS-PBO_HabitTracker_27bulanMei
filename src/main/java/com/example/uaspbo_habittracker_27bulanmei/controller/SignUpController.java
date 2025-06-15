package com.example.uaspbo_habittracker_27bulanmei.controller;

import com.example.uaspbo_habittracker_27bulanmei.MainApp;
import com.example.uaspbo_habittracker_27bulanmei.db.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SignUpController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private MainApp mainApp;
    private final DatabaseManager dbManager = new DatabaseManager();

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void handleSignUp() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username dan password tidak boleh kosong.");
            return;
        }

        boolean success = dbManager.signUpUser(username, password);

        if (success) {
            // Langsung arahkan ke halaman login setelah berhasil daftar
            mainApp.showLoginScene();
        } else {
            errorLabel.setText("Username sudah digunakan. Coba yang lain.");
        }
    }

    @FXML
    private void handleGoToLogin() {
        mainApp.showLoginScene();
    }
}