package com.example.uaspbo_habittracker_27bulanmei.model;

import java.time.LocalDate;

public class Habit extends UserActivity {
    private int id;
    private int userId;
    private final LocalDate creationDate;
    private LocalDate date;

    public Habit(int id, int userId, String name, LocalDate creationDate, LocalDate lastUpdated) {
        super(name);
        this.id = id;
        this.userId = userId;
        this.creationDate = creationDate;
        this.date = lastUpdated;
    }

    public Habit(int userId, String name, LocalDate creationDate) {
        super(name);
        this.userId = userId;
        this.creationDate = creationDate;
        this.date = creationDate;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public LocalDate getDate() { return date; }
    public LocalDate getCreationDate() { return creationDate; } // Getter yang dibutuhkan

    public void setId(int id) { this.id = id; }
    public void setDate(LocalDate date) { this.date = date; }

    @Override
    public void markCompleted() {
        this.status = true;
    }

    public void markCompletedFromDB() { this.status = true; }
    public void resetStatusIfNewDay(LocalDate today) {
        if (!this.date.equals(today)) {
            resetStatus();
            this.date = today;
        }
    }
}