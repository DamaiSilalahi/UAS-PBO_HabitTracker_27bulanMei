package com.example.uaspbo_habittracker_27bulanmei.controller;

import com.example.uaspbo_habittracker_27bulanmei.MainApp;
import com.example.uaspbo_habittracker_27bulanmei.db.DatabaseManager;
import com.example.uaspbo_habittracker_27bulanmei.model.User;
import javafx.fxml.FXML;
import javafx.geometry.Insets; // <-- IMPORT YANG DITAMBAHKAN
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class HistoryController {

    @FXML private Label monthYearLabel;
    @FXML private GridPane calendarGrid;
    @FXML private ListView<String> summaryListView;

    private User currentUser;
    private MainApp mainApp;
    private final DatabaseManager dbManager = new DatabaseManager();
    private YearMonth currentMonth;

    public void initialize() {
        currentMonth = YearMonth.now();
    }

    public void initData(User user, MainApp mainApp) {
        this.currentUser = user;
        this.mainApp = mainApp;
        this.currentMonth = YearMonth.now();
        populateView();
    }

    private void populateView() {
        monthYearLabel.setText(currentMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("id")) + " " + currentMonth.getYear());
        populateCalendar();
        populateSummary();
        updateLongTermAchievements();
    }

    private void populateCalendar() {
        calendarGrid.getChildren().clear();
        Set<LocalDate> completedDates = dbManager.getCompletedDatesForMonth(currentUser.getId(), currentMonth);

        // Header hari (Senin, Selasa, ...)
        for (int i = 0; i < 7; i++) {
            // Logika untuk memastikan urutan hari Senin (1) -> Minggu (7)
            DayOfWeek day = DayOfWeek.of(i + 1);
            Label dayLabel = new Label(day.getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("id")));
            dayLabel.setFont(new Font("System Bold", 14));
            VBox dayHeaderBox = new VBox(dayLabel);
            dayHeaderBox.setAlignment(Pos.CENTER);
            calendarGrid.add(dayHeaderBox, i, 0);
        }

        LocalDate firstOfMonth = currentMonth.atDay(1);
        int dayOfWeekOffset = firstOfMonth.getDayOfWeek().getValue() - 1; // 0 untuk Senin, 6 untuk Minggu

        for (int day = 1; day <= currentMonth.lengthOfMonth(); day++) {
            int row = (day + dayOfWeekOffset - 1) / 7 + 1;
            int col = (day + dayOfWeekOffset - 1) % 7;

            Label dayLabel = new Label(String.valueOf(day));
            VBox dayCell = new VBox(dayLabel);
            dayCell.setAlignment(Pos.CENTER);
            dayCell.setPadding(new Insets(5)); // Baris ini butuh 'import javafx.geometry.Insets;'

            // Highlight jika tanggal ada di set
            if (completedDates.contains(currentMonth.atDay(day))) {
                dayCell.setStyle("-fx-background-color: #a8e6cf;"); // Warna hijau pastel
            }

            calendarGrid.add(dayCell, col, row);
        }
    }

    @FXML private VBox badgeBox;

    private void updateLongTermAchievements() {
        badgeBox.getChildren().clear();
        int totalDays = dbManager.getTotalHabitDays(currentUser.getId()); // hitung dari DB berapa hari habit selesai

        if (totalDays >= 365) {
            badgeBox.getChildren().add(new Label("🏆 365 Hari Konsisten!"));
        }
        if (totalDays >= 90) {
            badgeBox.getChildren().add(new Label("🧠 90 Hari Konsisten!"));
        }
        if (totalDays >= 30) {
            badgeBox.getChildren().add(new Label("🏅 30 Hari Konsisten!"));
        }
        if (totalDays < 30) {
            badgeBox.getChildren().add(new Label("💡 Tetap semangat membangun konsistensi!"));
        }
    }


    private void populateSummary() {
        summaryListView.getItems().clear();
        Map<String, Integer> summary = dbManager.getMonthlySummary(currentUser.getId(), currentMonth);
        if (summary.isEmpty()) {
            summaryListView.getItems().add("Tidak ada riwayat bulan ini.");
        } else {
            summary.forEach((habitName, count) ->
                    summaryListView.getItems().add(habitName + ": " + count + " kali")
            );
        }
    }

    @FXML void handlePreviousMonth() {
        currentMonth = currentMonth.minusMonths(1);
        populateView();
    }

    @FXML void handleNextMonth() {
        currentMonth = currentMonth.plusMonths(1);
        populateView();
    }

    @FXML void handleBack() {
        mainApp.showHabitTrackerScene(currentUser);
    }
}


