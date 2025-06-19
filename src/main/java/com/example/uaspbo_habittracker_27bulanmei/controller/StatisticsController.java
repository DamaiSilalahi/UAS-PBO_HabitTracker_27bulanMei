package com.example.uaspbo_habittracker_27bulanmei.controller;

import com.example.uaspbo_habittracker_27bulanmei.MainApp;
import com.example.uaspbo_habittracker_27bulanmei.db.DatabaseManager;
import com.example.uaspbo_habittracker_27bulanmei.logic.StatisticsCalculator;
import com.example.uaspbo_habittracker_27bulanmei.model.Habit;
import com.example.uaspbo_habittracker_27bulanmei.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.time.LocalDate; // <-- TAMBAHKAN BARIS INI
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatisticsController {

    @FXML private ComboBox<String> habitSelectionComboBox;
    @FXML private Label percentageLabel;
    @FXML private Label streakLabel;
    @FXML private Label weeklyAverageLabel;

    private User currentUser;
    private MainApp mainApp;
    private final DatabaseManager dbManager = new DatabaseManager();
    private final StatisticsCalculator calculator = new StatisticsCalculator();

    private List<Habit> userHabits;
    private Map<Integer, List<LocalDate>> userHistory;
    private static final String ALL_HABITS_OPTION = "Semua Habit";

    public void initData(User user, MainApp mainApp) {
        this.currentUser = user;
        this.mainApp = mainApp;

        this.userHabits = dbManager.getHabitsForUser(currentUser.getId());
        this.userHistory = dbManager.getHabitCompletionHistory(currentUser.getId());

        setupComboBox();
        updateStatisticsDisplay();
    }

    private void setupComboBox() {
        habitSelectionComboBox.getItems().clear();
        habitSelectionComboBox.getItems().add(ALL_HABITS_OPTION);
        for (Habit habit : userHabits) {
            habitSelectionComboBox.getItems().add(habit.getName());
        }
        habitSelectionComboBox.getSelectionModel().select(ALL_HABITS_OPTION);
        habitSelectionComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> updateStatisticsDisplay()
        );
    }

    private void updateStatisticsDisplay() {
        String selected = habitSelectionComboBox.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        double successPercentage = 0.0;
        // ... (sisa kode tidak berubah)

        if (selected.equals(ALL_HABITS_OPTION)) {
            successPercentage = calculator.calculateHistoricalSuccessPercentage(userHabits, userHistory);
        } else {
            // ...
        }

        displayResults(successPercentage, 0, 0.0);
    }

    private void displayResults(double percentage, int streak, double average) {
        percentageLabel.setText(String.format("%.1f%%", percentage));
        streakLabel.setText(streak + " hari");
        weeklyAverageLabel.setText(String.format("%.1f kali", average));
    }

    private Habit findHabitByName(String name) {
        for (Habit habit : userHabits) {
            if (habit.getName().equals(name)) {
                return habit;
            }
        }
        return null;
    }

    @FXML
    private void handleBack() {
        mainApp.showHabitTrackerScene(currentUser);
    }
}