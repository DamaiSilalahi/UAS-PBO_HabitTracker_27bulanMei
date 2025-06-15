package com.example.uaspbo_habittracker_27bulanmei;

import java.time.LocalDate;

public class Habit extends UserActivity {
    private LocalDate date;

    public Habit(String name, LocalDate date) {
        super(name);
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public void markCompleted() {
        this.status = true;
        System.out.println(name + " ditandai selesai pada " + date);
    }

    public void resetStatusIfNewDay(LocalDate today) {
        if (!date.equals(today)) {
            resetStatus();
            date = today;
        }
    }
}
