package com.example.uaspbo_habittracker_27bulanmei.controller;

import com.example.uaspbo_habittracker_27bulanmei.MainApp;
import com.example.uaspbo_habittracker_27bulanmei.db.DatabaseManager;
import com.example.uaspbo_habittracker_27bulanmei.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private MainApp mainApp;
    private final DatabaseManager dbManager = new DatabaseManager();

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void handleSignIn() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username dan password tidak boleh kosong.");
            return;
        }

        User user = dbManager.signInUser(username, password);

        if (user != null) {
            mainApp.showHabitTrackerScene(user);
        } else {
            errorLabel.setText("Username atau password salah!");
        }
    }

    @FXML
    private void handleGoToSignUp() {
        mainApp.showSignUpScene();
    }
}