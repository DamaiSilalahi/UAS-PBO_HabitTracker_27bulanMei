package com.example.uaspbo_habittracker_27bulanmei;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class HabitTrackerApp extends Application {

    private HabitManager habitManager = new HabitManager();
    private VBox habitDisplayBox;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Habit Tracker");

        TextField habitInput = new TextField();
        habitInput.setPromptText("Masukkan nama kebiasaan");

        Button addButton = new Button("Tambah Habit");

        habitDisplayBox = new VBox(5);
        habitDisplayBox.setPadding(new Insets(10));

        addButton.setOnAction(e -> {
            String habitName = habitInput.getText().trim();
            if (!habitName.isEmpty()) {
                Habit newHabit = new Habit(habitName, LocalDate.now());
                habitManager.addHabit(newHabit);
                updateHabitDisplay();
                habitInput.clear();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Nama kebiasaan tidak boleh kosong!");
                alert.showAndWait();
            }
        });

        // Tombol reset manual untuk demo reset tiap hari
        Button resetButton = new Button("Reset Habit (Simulasi Hari Baru)");
        resetButton.setOnAction(e -> {
            habitManager.resetAllHabitsIfNewDay();
            updateHabitDisplay();
        });

        HBox inputLayout = new HBox(10, habitInput, addButton);
        inputLayout.setPadding(new Insets(10));

        VBox mainLayout = new VBox(10, inputLayout, resetButton, new ScrollPane(habitDisplayBox));
        mainLayout.setPadding(new Insets(10));

        Scene scene = new Scene(mainLayout, 400, 350);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateHabitDisplay() {
        habitDisplayBox.getChildren().clear();
        for (Habit habit : habitManager.getHabitList()) {
            CheckBox cb = new CheckBox(habit.getName());
            cb.setSelected(habit.isCompleted());

            cb.setOnAction(e -> {
                if (cb.isSelected()) {
                    habit.markCompleted();
                } else {
                    habit.resetStatus();
                }
            });

            habitDisplayBox.getChildren().add(cb);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
