package com.example.uaspbo_habittracker_27bulanmei.controller;

import com.example.uaspbo_habittracker_27bulanmei.MainApp;
import com.example.uaspbo_habittracker_27bulanmei.db.DatabaseManager;
import com.example.uaspbo_habittracker_27bulanmei.model.User;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class HistoryController {

    @FXML private Label monthYearLabel;
    @FXML private GridPane calendarGrid;

    @FXML private VBox monthlySummaryPanel;
    @FXML private ListView<String> summaryListView;
    @FXML private VBox dailyHabitsPanel;
    @FXML private Label dailyHabitsTitle;
    @FXML private ListView<String> dailyHabitsListView;

    @FXML private VBox badgeBox;

    private User currentUser;
    private MainApp mainApp;
    private final DatabaseManager dbManager = new DatabaseManager();
    private YearMonth currentMonth;

    private LocalDate selectedDate = null;
    private Map<LocalDate, VBox> dayCellMap = new HashMap<>();

    public void initialize() {

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

        populateMonthlySummary();

        updateLongTermAchievements();
    }

    private void populateCalendar() {
        calendarGrid.getChildren().clear();
        dayCellMap.clear();
        Set<LocalDate> completedDates = dbManager.getCompletedDatesForMonth(currentUser.getId(), currentMonth);

        for (int i = 0; i < 7; i++) {
            DayOfWeek day = DayOfWeek.of(i + 1);
            Label dayLabel = new Label(day.getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("id")));
            dayLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #34495e;");

            VBox dayHeaderBox = new VBox(dayLabel);
            dayHeaderBox.setAlignment(Pos.CENTER);
            dayHeaderBox.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 8px 0; -fx-alignment: CENTER; -fx-background-radius: 5px;");
            GridPane.setHgrow(dayHeaderBox, javafx.scene.layout.Priority.ALWAYS);
            calendarGrid.add(dayHeaderBox, i, 0);
        }

        LocalDate firstOfMonth = currentMonth.atDay(1);
        int dayOfWeekOffset = firstOfMonth.getDayOfWeek().getValue() - 1;

        for (int day = 1; day <= currentMonth.lengthOfMonth(); day++) {
            final LocalDate cellDate = currentMonth.atDay(day);
            int row = (day + dayOfWeekOffset - 1) / 7 + 1;
            int col = (day + dayOfWeekOffset - 1) % 7;

            Label dayLabel = new Label(String.valueOf(day));
            dayLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #333333;");

            VBox dayCell = new VBox(dayLabel);
            dayCell.setAlignment(Pos.CENTER);
            dayCell.setPadding(new Insets(7));

            String cellStyle = "-fx-background-color: #ffffff; -fx-border-color: #e0e0e0; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-padding: 7px;";

            if (completedDates.contains(cellDate)) {
                cellStyle = "-fx-background-color: #a8e6cf; -fx-border-color: #7cb8a2; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-padding: 7px;";
            }
            if (cellDate.isEqual(LocalDate.now())) {
                cellStyle = "-fx-background-color: #ffda6a; -fx-border-color: #fcc300; -fx-border-width: 2px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-padding: 7px;";
                if (completedDates.contains(cellDate)) {
                    cellStyle = "-fx-background-color: #a8e6cf; -fx-border-color: #fcc300; -fx-border-width: 2px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-padding: 7px;";
                }
            }

            dayCell.setStyle(cellStyle);

            dayCellMap.put(cellDate, dayCell);

            dayCell.setOnMouseClicked(event -> {
                if (selectedDate != null) {
                    VBox prevSelectedCell = dayCellMap.get(selectedDate);
                    if (prevSelectedCell != null) {
                        String oldCellStyle = "-fx-background-color: #ffffff; -fx-border-color: #e0e0e0; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-padding: 7px;";

                        if (completedDates.contains(selectedDate)) {
                            oldCellStyle = "-fx-background-color: #a8e6cf; -fx-border-color: #7cb8a2; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-padding: 7px;";
                        }
                        if (selectedDate.isEqual(LocalDate.now())) {
                            oldCellStyle = "-fx-background-color: #ffda6a; -fx-border-color: #fcc300; -fx-border-width: 2px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-padding: 7px;";
                            if (completedDates.contains(selectedDate)) {
                                oldCellStyle = "-fx-background-color: #a8e6cf; -fx-border-color: #fcc300; -fx-border-width: 2px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-padding: 7px;";
                            }
                        }
                        prevSelectedCell.setStyle(oldCellStyle);
                    }
                }

                selectedDate = cellDate;

                dayCell.setStyle("-fx-background-color: #add8e6; -fx-border-color: #6a9acb; -fx-border-width: 2px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-padding: 7px;");

                displayHabitsForSelectedDate(selectedDate);
            });

            if (selectedDate != null && cellDate.isEqual(selectedDate)) {
                dayCell.setStyle("-fx-background-color: #add8e6; -fx-border-color: #6a9acb; -fx-border-width: 2px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-padding: 7px;");
            }

            calendarGrid.add(dayCell, col, row);
        }
    }

    private void displayHabitsForSelectedDate(LocalDate date) {
        showDailyHabitsPanel();
        dailyHabitsListView.getItems().clear();
        dailyHabitsListView.setStyle("-fx-background-color: #fefefe; -fx-border-color: #e0e0e0; -fx-border-width: 1px; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        dailyHabitsTitle.setText("Kebiasaan pada " + date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.forLanguageTag("id"))));

        List<String> habitsOnDate = dbManager.getHabitsCompletedOnDate(currentUser.getId(), date);

        if (habitsOnDate.isEmpty()) {
            dailyHabitsListView.getItems().add("Tidak ada kebiasaan yang diselesaikan pada tanggal ini.");
        } else {
            for (String habitName : habitsOnDate) {
                dailyHabitsListView.getItems().add("• " + habitName);
            }
        }
    }

    private void populateMonthlySummary() {
        showMonthlySummaryPanel();
        summaryListView.getItems().clear();
        summaryListView.setStyle("-fx-background-color: #fefefe; -fx-border-color: #e0e0e0; -fx-border-width: 1px; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        Map<String, Integer> summary = dbManager.getMonthlySummary(currentUser.getId(), currentMonth);
        if (summary.isEmpty()) {
            summaryListView.getItems().add("Tidak ada riwayat bulan ini.");
        } else {
            summaryListView.getItems().add("Ringkasan Bulan Ini (" + currentMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("id")) + " " + currentMonth.getYear() + "):");
            summary.forEach((habitName, count) ->
                    summaryListView.getItems().add(habitName + ": " + count + " kali")
            );
        }
    }

    @FXML
    private void handleBackToMonthlySummary() {
        selectedDate = null;
        populateMonthlySummary();
        populateCalendar();
    }

    private void showMonthlySummaryPanel() {
        monthlySummaryPanel.setVisible(true);
        monthlySummaryPanel.setManaged(true);
        dailyHabitsPanel.setVisible(false);
        dailyHabitsPanel.setManaged(false);
    }

    private void showDailyHabitsPanel() {
        monthlySummaryPanel.setVisible(false);
        monthlySummaryPanel.setManaged(false);
        dailyHabitsPanel.setVisible(true);
        dailyHabitsPanel.setManaged(true);
    }

    private HBox createBadge(String text, String iconPath, String backgroundColor, String textColor) {
        ImageView icon = null;
        if (iconPath != null && !iconPath.isEmpty()) {
            try {
                icon = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
                icon.setFitHeight(20);
                icon.setFitWidth(20);
            } catch (Exception e) {
                System.err.println("Error loading icon: " + iconPath + " - " + e.getMessage());
                icon = null;
            }
        }

        Label label = new Label(text);
        label.setStyle("-fx-text-fill: " + textColor + "; -fx-font-size: 13px;");

        HBox badge = new HBox(5);
        if (icon != null) {
            badge.getChildren().add(icon);
        }
        badge.getChildren().add(label);
        badge.setAlignment(Pos.CENTER_LEFT);
        badge.setStyle("-fx-background-color: " + backgroundColor + "; -fx-padding: 8px 15px; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 3, 0, 0, 1); -fx-spacing: 5;");
        return badge;
    }

    private void updateLongTermAchievements() {
        badgeBox.getChildren().clear();
        int totalDays = dbManager.getTotalHabitDays(currentUser.getId());

        if (totalDays >= 365) {
            badgeBox.getChildren().add(createBadge("365 Hari Konsisten!", "/icons/trophy.png", "#FFD700", "#333333"));
        }
        if (totalDays >= 90) {
            badgeBox.getChildren().add(createBadge("90 Hari Konsisten!", "/icons/brain.png", "#C0C0C0", "#333333"));
        }
        if (totalDays >= 30) {
            badgeBox.getChildren().add(createBadge("30 Hari Konsisten!", "/icons/medal.png", "#CD7F32", "#FFFFFF"));
        }
        if (totalDays < 30) {
            Label motivationLabel = new Label("💡 Tetap semangat membangun konsistensi!");
            motivationLabel.setStyle("-fx-text-fill: #1e3d59; -fx-font-size: 14px; -fx-font-weight: bold;");
            badgeBox.getChildren().add(motivationLabel);
        }
    }

    @FXML void handlePreviousMonth() {
        currentMonth = currentMonth.minusMonths(1);
        selectedDate = null;
        populateView();
    }

    @FXML void handleNextMonth() {
        currentMonth = currentMonth.plusMonths(1);
        selectedDate = null;
        populateView();
    }

    @FXML void handleBack() {
        mainApp.showHabitTrackerScene(currentUser);
    }
}