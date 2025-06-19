package com.example.uaspbo_habittracker_27bulanmei.logic;

import com.example.uaspbo_habittracker_27bulanmei.model.Habit;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StatisticsCalculator {

    public double calculateHistoricalSuccessPercentage(List<Habit> habits, Map<Integer, List<LocalDate>> history) {
        if (habits.isEmpty()) return 0.0;

        // 1. Hitung total penyelesaian (Numerator)
        long totalActualCompletions = history.values().stream().mapToLong(List::size).sum();
        if (totalActualCompletions == 0) return 0.0;

        // 2. Hitung total peluang (Denominator)
        long totalOpportunities = 0;
        LocalDate today = LocalDate.now();

        // Cari tanggal paling awal habit dibuat untuk memulai loop
        LocalDate firstEverCreationDate = habits.stream()
                .map(Habit::getCreationDate)
                .min(LocalDate::compareTo)
                .orElse(today);

        // Loop dari hari pertama hingga hari ini
        for (LocalDate day = firstEverCreationDate; !day.isAfter(today); day = day.plusDays(1)) {
            final LocalDate currentDay = day;
            // Hitung berapa banyak habit yang sudah ada pada hari itu
            long habitsExistedOnThisDay = habits.stream()
                    .filter(h -> !h.getCreationDate().isAfter(currentDay))
                    .count();
            totalOpportunities += habitsExistedOnThisDay;
        }

        if (totalOpportunities == 0) return 0.0;

        // 3. Hitung persentase
        return ((double) totalActualCompletions / totalOpportunities) * 100;
    }
}