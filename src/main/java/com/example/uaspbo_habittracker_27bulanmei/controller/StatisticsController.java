package com.example.uaspbo_habittracker_27bulanmei.controller;

import com.example.uaspbo_habittracker_27bulanmei.MainApp;
import com.example.uaspbo_habittracker_27bulanmei.db.DatabaseManager;
import com.example.uaspbo_habittracker_27bulanmei.logic.StatisticsCalculator;
import com.example.uaspbo_habittracker_27bulanmei.model.Habit;
import com.example.uaspbo_habittracker_27bulanmei.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        // --- DEBUG PRINT ---
        System.out.println("DEBUG: Data dimuat. Jumlah habit: " + userHabits.size() + ", Jumlah riwayat: " + userHistory.values().stream().mapToLong(List::size).sum());

        setupComboBox();
        updateStatisticsDisplay();
    }

    private void setupComboBox() {
        habitSelectionComboBox.getItems().add(ALL_HABITS_OPTION);
        for (Habit habit : userHabits) {
            habitSelectionComboBox.getItems().add(habit.getName());
        }
        habitSelectionComboBox.getSelectionModel().select(ALL_HABITS_OPTION);
        habitSelectionComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    // --- DEBUG PRINT ---
                    System.out.println("DEBUG: Pilihan ComboBox berubah ke: " + newValue);
                    updateStatisticsDisplay();
                }
        );
    }

    private void updateStatisticsDisplay() {
        // --- DEBUG PRINT ---
        System.out.println("--- Memulai updateStatisticsDisplay ---");
        String selected = habitSelectionComboBox.getSelectionModel().getSelectedItem();
        System.out.println("DEBUG: Item yang terpilih: " + selected);

        if (selected == null) {
            System.out.println("DEBUG: Tidak ada item terpilih, keluar dari method.");
            return;
        }

        double successPercentage = 0.0;
        int longestStreak = 0;
        double weeklyAvg = 0.0;

        if (selected.equals(ALL_HABITS_OPTION)) {
            System.out.println("DEBUG: Menghitung statistik untuk 'Semua Habit'.");
            Set<LocalDate> uniqueDates = dbManager.getAllUniqueCompletionDates(currentUser.getId());
            int totalCompletions = (int) userHistory.values().stream().mapToLong(List::size).sum();

            successPercentage = calculator.calculateSuccessPercentage(userHabits, userHistory);
            longestStreak = calculator.calculateLongestStreak(uniqueDates);
            weeklyAvg = calculator.calculateWeeklyAverage(uniqueDates, totalCompletions);

        } else {
            System.out.println("DEBUG: Menghitung statistik untuk habit spesifik.");
            Habit selectedHabit = findHabitByName(selected);
            if (selectedHabit != null) {
                System.out.println("DEBUG: Habit ditemukan: " + selectedHabit.getName());
                List<LocalDate> habitHistory = userHistory.getOrDefault(selectedHabit.getId(), new ArrayList<>());

                successPercentage = calculator.calculateSuccessPercentage(selectedHabit, habitHistory);
                longestStreak = calculator.calculateLongestStreak(habitHistory);
                weeklyAvg = calculator.calculateWeeklyAverage(habitHistory);
            } else {
                System.out.println("DEBUG: WARNING! Habit dengan nama '" + selected + "' tidak ditemukan.");
            }
        }

        // --- DEBUG PRINT ---
        System.out.println("DEBUG: Hasil Kalkulasi -> Persentase: " + successPercentage + ", Streak: " + longestStreak + ", Rata-rata: " + weeklyAvg);
        displayResults(successPercentage, longestStreak, weeklyAvg);
        System.out.println("--- Selesai updateStatisticsDisplay ---");
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