package com.example.uaspbo_habittracker_27bulanmei;

import com.example.uaspbo_habittracker_27bulanmei.controller.HabitTrackerController;
import com.example.uaspbo_habittracker_27bulanmei.controller.LoginController;
import com.example.uaspbo_habittracker_27bulanmei.controller.SignUpController;
import com.example.uaspbo_habittracker_27bulanmei.db.DatabaseManager;
import com.example.uaspbo_habittracker_27bulanmei.model.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Inisialisasi Database saat aplikasi pertama kali jalan
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.initializeDatabase();

        // Tampilkan halaman login pertama kali
        showLoginScene();
    }

    public void showLoginScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();

            LoginController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.setTitle("Habit Tracker - Login");
            primaryStage.setScene(new Scene(root, 300, 300));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showSignUpScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SignUp.fxml"));
            Parent root = loader.load();

            SignUpController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.setTitle("Habit Tracker - Sign Up");
            primaryStage.setScene(new Scene(root, 300, 300));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void showHabitTrackerScene(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("HabitTracker.fxml"));
            Parent root = loader.load();

            HabitTrackerController controller = loader.getController();
            controller.initData(user, this); // Mengirim data user dan mainApp ke controller

            primaryStage.setTitle("Habit Tracker");
            primaryStage.setScene(new Scene(root, 350, 400));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}