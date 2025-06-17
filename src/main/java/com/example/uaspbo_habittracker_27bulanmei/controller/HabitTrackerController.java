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
import java.util.List;

public class HabitTrackerController {

    @FXML private Label welcomeLabel;
    @FXML private TextField habitInput;
    @FXML private VBox habitDisplayBox;
    @FXML private VBox achievementBox;
    @FXML private Label achievementMessage;

    private User currentUser;
    private MainApp mainApp;
    private final DatabaseManager dbManager = new DatabaseManager();
    private ArrayList<Habit> habitList = new ArrayList<>();

    public void initData(User user, MainApp mainApp) {
        this.currentUser = user;
        this.mainApp = mainApp;
        welcomeLabel.setText("Halo, " + currentUser.getUsername() + "!");
        loadHabits();
    }

    private void loadHabits() {
        habitList = dbManager.getHabitsForUser(currentUser.getId());
        resetAllHabitsIfNewDay();
        updateHabitDisplay();
        updateAchievementMessage();
    }

    @FXML
    private void handleAddHabit() {
        String habitName = habitInput.getText().trim();
        if (!habitName.isEmpty()) {
            Habit newHabit = new Habit(currentUser.getId(), habitName, LocalDate.now());
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
                LocalDate today = LocalDate.now(); // Gunakan satu variabel agar konsisten
                if (cb.isSelected()) {
                    habit.markCompleted();
                    dbManager.logHabitCompletion(habit.getId(), today);
                } else {
                    habit.resetStatus();
                    // --- DEBUG PRINT ---
                    System.out.println("DEBUG [Controller]: Mencoba menghapus riwayat untuk habit ID: " + habit.getId() + " pada tanggal: " + today);
                    dbManager.removeHabitCompletionLog(habit.getId(), today);
                }
                dbManager.updateHabit(habit);
                updateAchievementMessage();
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
                dbManager.updateHabit(h);
            }
        }
    }

    private void updateAchievementMessage() {
        List<Habit> habits = dbManager.getHabitsForUser(currentUser.getId());
        LocalDate today = LocalDate.now();
        int total = habits.size();
        int completedCount = 0;

        for (Habit h : habits) {
            h.resetStatusIfNewDay(today);
            if (h.isCompleted() && h.getDate().equals(today)) {
                completedCount++;
            }
        }

        if (total == 0) {
            achievementMessage.setText("Tambahkan kebiasaan terlebih dahulu.");
            return;
        }

        double completionRatio = (double) completedCount / total;

        if (completedCount == total) {
            achievementMessage.setText("🔥 Keren! Semua habit selesai hari ini! Kamu luar biasa!");
        } else if (completionRatio >= 0.75) {
            achievementMessage.setText("👏 Hampir selesai! Sedikit lagi menuju sempurna!");
        } else if (completionRatio >= 0.5) {
            achievementMessage.setText("👍 Sudah lebih dari setengah selesai, tetap semangat!");
        } else if (completedCount >= 1) {
            achievementMessage.setText("💡 Kamu sudah mulai, teruskan kebiasaan baik ini!");
        } else {
            achievementMessage.setText("💪 Ayo mulai checklist kebiasaanmu hari ini!");
        }
    }

    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        return label;
    }


    @FXML
    private void handleLogout() {
        mainApp.showLoginScene();
    }

    @FXML
    private void handleViewHistory() {
        mainApp.showHistoryScene(currentUser);
    }

    @FXML
    private void handleViewStatistics() {
        mainApp.showStatisticsScene(currentUser);
    }
}
