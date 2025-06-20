package com.example.uaspbo_habittracker_27bulanmei.logic;

import com.example.uaspbo_habittracker_27bulanmei.model.Habit;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class StatisticsCalculator {
    public double calculateHistoricalSuccessPercentage(List<Habit> habits, Map<Integer, List<LocalDate>> history) {
        if (habits.isEmpty()) return 0.0;
        long totalActualCompletions = history.values().stream().mapToLong(List::size).sum();
        if (totalActualCompletions == 0) return 0.0;
        long totalOpportunities = 0;
        LocalDate today = LocalDate.now();
        LocalDate firstEverCreationDate = habits.stream()
                .map(Habit::getCreationDate)
                .min(LocalDate::compareTo)
                .orElse(today);
        for (LocalDate day = firstEverCreationDate; !day.isAfter(today); day = day.plusDays(1)) {
            final LocalDate currentDay = day;
            long habitsExistedOnThisDay = habits.stream()
                    .filter(h -> !h.getCreationDate().isAfter(currentDay))
                    .count();
            totalOpportunities += habitsExistedOnThisDay;
        }
        if (totalOpportunities == 0) return 0.0;
        return ((double) totalActualCompletions / totalOpportunities) * 100;
    }

    public int calculateOverallLongestStreak(Set<LocalDate> allCompletionDates) {
        if (allCompletionDates.isEmpty()) return 0;
        List<LocalDate> sortedDates = new ArrayList<>(allCompletionDates);
        Collections.sort(sortedDates);
        return calculateStreakFromList(sortedDates);
    }

    public double calculateOverallWeeklyAverage(Set<LocalDate> allCompletionDates, int totalCompletions) {
        if (allCompletionDates.isEmpty()) return 0.0;
        LocalDate firstDay = Collections.min(allCompletionDates);
        long weeks = ChronoUnit.WEEKS.between(firstDay, LocalDate.now()) + 1;
        return (double) totalCompletions / weeks;
    }

    public double calculateSingleHabitPercentage(Habit habit, List<LocalDate> habitHistory) {
        if (habitHistory == null || habitHistory.isEmpty()) return 0.0;
        LocalDate creationDate = habit.getCreationDate();
        long daysExisted = ChronoUnit.DAYS.between(creationDate, LocalDate.now()) + 1;
        if (daysExisted <= 0) return 0.0;
        return ((double) habitHistory.size() / daysExisted) * 100;
    }

    public int calculateSingleHabitLongestStreak(List<LocalDate> habitHistory) {
        if (habitHistory == null || habitHistory.isEmpty()) return 0;
        List<LocalDate> sortedDates = new ArrayList<>(new HashSet<>(habitHistory));
        Collections.sort(sortedDates);
        return calculateStreakFromList(sortedDates);
    }

    public double calculateSingleHabitWeeklyAverage(List<LocalDate> habitHistory) {
        if (habitHistory == null || habitHistory.isEmpty()) return 0.0;
        LocalDate firstDay = Collections.min(habitHistory);
        long weeks = ChronoUnit.WEEKS.between(firstDay, LocalDate.now()) + 1;
        return (double) habitHistory.size() / weeks;
    }

    private int calculateStreakFromList(List<LocalDate> sortedDates) {
        if (sortedDates.isEmpty()) return 0;
        int longestStreak = 0;
        int currentStreak = 0;
        if(!sortedDates.isEmpty()) {
            longestStreak = 1;
            currentStreak = 1;
        }
        for (int i = 1; i < sortedDates.size(); i++) {
            if (sortedDates.get(i).equals(sortedDates.get(i - 1).plusDays(1))) {
                currentStreak++;
            } else if (!sortedDates.get(i).equals(sortedDates.get(i - 1))) {
                longestStreak = Math.max(longestStreak, currentStreak);
                currentStreak = 1;
            }
        }
        return Math.max(longestStreak, currentStreak);
    }
}