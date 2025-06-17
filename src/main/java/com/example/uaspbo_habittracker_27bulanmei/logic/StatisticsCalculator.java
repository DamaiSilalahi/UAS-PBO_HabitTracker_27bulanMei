package com.example.uaspbo_habittracker_27bulanmei.logic;

import com.example.uaspbo_habittracker_27bulanmei.model.Habit;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class StatisticsCalculator {

    // --- METHOD UNTUK SEMUA HABIT (OVERALL) ---

    /**
     * Menghitung persentase keberhasilan gabungan dari semua habit.
     * VERSI FINAL YANG SUDAH DIPERBAIKI.
     */
    public double calculateSuccessPercentage(List<Habit> habits, Map<Integer, List<LocalDate>> history) {
        if (habits.isEmpty()) return 0.0;

        long totalPossibleCompletionDays = 0;

        // Iterasi SEMUA habit untuk menghitung total kemungkinan penyelesaian.
        for (Habit habit : habits) {
            // Tanggal mulai habit adalah saat ia dibuat/dimuat.
            LocalDate startDate = habit.getDate();

            // Penyesuaian jika habit baru dibuat hari ini
            if(startDate.isAfter(LocalDate.now())) {
                startDate = LocalDate.now();
            }
            totalPossibleCompletionDays += ChronoUnit.DAYS.between(startDate, LocalDate.now()) + 1;
        }

        long totalActualCompletions = history.values().stream().mapToLong(List::size).sum();

        if (totalPossibleCompletionDays == 0) return 0.0;

        // Pembagian yang benar (misal: 1 dibagi 2 = 50%)
        return ((double) totalActualCompletions / totalPossibleCompletionDays) * 100;
    }

    /**
     * Menghitung streak terpanjang di mana setidaknya satu habit selesai.
     */
    public int calculateLongestStreak(Set<LocalDate> allCompletionDates) {
        if (allCompletionDates.isEmpty()) return 0;
        List<LocalDate> sortedDates = new ArrayList<>(allCompletionDates);
        Collections.sort(sortedDates);
        return calculateStreakFromList(sortedDates);
    }

    /**
     * Menghitung rata-rata penyelesaian habit per minggu secara keseluruhan.
     */
    public double calculateWeeklyAverage(Set<LocalDate> allCompletionDates, int totalCompletions) {
        if (allCompletionDates.isEmpty()) return 0.0;
        LocalDate firstDay = Collections.min(allCompletionDates);
        long weeks = ChronoUnit.WEEKS.between(firstDay, LocalDate.now()) + 1;
        return (double) totalCompletions / weeks;
    }


    // --- METHOD UNTUK SATU HABIT SPESIFIK ---

    /**
     * Menghitung persentase keberhasilan untuk SATU habit spesifik.
     */
    public double calculateSuccessPercentage(Habit habit, List<LocalDate> habitHistory) {
        if (habitHistory == null || habitHistory.isEmpty()) return 0.0;
        LocalDate startDate = Collections.min(habitHistory);
        long daysSinceStart = ChronoUnit.DAYS.between(startDate, LocalDate.now()) + 1;
        if (daysSinceStart <= 0) return 0.0;
        return ((double) habitHistory.size() / daysSinceStart) * 100;
    }

    /**
     * Menghitung streak terpanjang untuk SATU habit spesifik.
     */
    public int calculateLongestStreak(List<LocalDate> habitHistory) {
        if (habitHistory == null || habitHistory.isEmpty()) return 0;
        List<LocalDate> sortedDates = new ArrayList<>(new HashSet<>(habitHistory)); // Hapus duplikat
        Collections.sort(sortedDates);
        return calculateStreakFromList(sortedDates);
    }

    /**
     * Menghitung rata-rata mingguan untuk SATU habit spesifik.
     */
    public double calculateWeeklyAverage(List<LocalDate> habitHistory) {
        if (habitHistory == null || habitHistory.isEmpty()) return 0.0;
        LocalDate firstDay = Collections.min(habitHistory);
        long weeks = ChronoUnit.WEEKS.between(firstDay, LocalDate.now()) + 1;
        return (double) habitHistory.size() / weeks;
    }


    // --- METHOD BANTU (HELPER) ---

    /**
     * Logika inti untuk menghitung streak dari daftar tanggal yang sudah diurutkan.
     */
    private int calculateStreakFromList(List<LocalDate> sortedDates) {
        if (sortedDates.isEmpty()) return 0;
        int longestStreak = 1;
        int currentStreak = 1;
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