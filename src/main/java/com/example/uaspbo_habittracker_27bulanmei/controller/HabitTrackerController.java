package com.example.uaspbo_habittracker_27bulanmei.controller;

import com.example.uaspbo_habittracker_27bulanmei.MainApp;
import com.example.uaspbo_habittracker_27bulanmei.db.DatabaseManager;
import com.example.uaspbo_habittracker_27bulanmei.model.Habit;
import com.example.uaspbo_habittracker_27bulanmei.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.ArrayList;

public class HabitTrackerController {

    @FXML private Label welcomeLabel;
    @FXML private TextField habitInput;
    @FXML private VBox habitDisplayBox;

    private User currentUser;
    private MainApp mainApp;
    private final DatabaseManager dbManager = new DatabaseManager();
    private ArrayList<Habit> habitList = new ArrayList<>();

    // Method ini dipanggil oleh MainApp untuk memberikan data awal
    public void initData(User user, MainApp mainApp) {
        this.currentUser = user;
        this.mainApp = mainApp;
        welcomeLabel.setText("Halo, " + currentUser.getUsername() + "!");
        loadHabits();
    }

    private void loadHabits() {
        habitList = dbManager.getHabitsForUser(currentUser.getId());
        resetAllHabitsIfNewDay(); // Cek jika ada yg perlu direset saat load
        updateHabitDisplay();
    }

    @FXML
    private void handleAddHabit() {
        String habitName = habitInput.getText().trim();
        if (!habitName.isEmpty()) {
            Habit newHabit = new Habit(currentUser.getId(), habitName, LocalDate.now());

            // Simpan ke DB dan perbarui objek dengan ID dari DB
            Habit savedHabit = dbManager.addHabit(newHabit);
            if (savedHabit != null) {
                habitList.add(savedHabit);
                updateHabitDisplay();
                habitInput.clear();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Nama kebiasaan tidak boleh kosong!");
            alert.showAndWait();
        }
    }

    private void updateHabitDisplay() {
        habitDisplayBox.getChildren().clear();
        for (Habit habit : habitList) {
            CheckBox cb = new CheckBox(habit.getName());
            cb.setSelected(habit.isCompleted());

            cb.setOnAction(e -> {
                if (cb.isSelected()) {
                    habit.markCompleted();
                } else {
                    habit.resetStatus();
                }
                // Setiap ada perubahan, update ke database
                dbManager.updateHabit(habit);
            });
            habitDisplayBox.getChildren().add(cb);
        }
    }

    private void resetAllHabitsIfNewDay() {
        LocalDate today = LocalDate.now();
        for (Habit h : habitList) {
            if (!h.getDate().equals(today)) {
                h.resetStatus();
                h.setDate(today);
                dbManager.updateHabit(h); // Simpan status reset ke DB
            }
        }
    }

    @FXML
    private void handleLogout() {
        mainApp.showLoginScene();
    }
}