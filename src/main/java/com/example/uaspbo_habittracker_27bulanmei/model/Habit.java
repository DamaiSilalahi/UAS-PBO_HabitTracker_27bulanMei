package com.example.uaspbo_habittracker_27bulanmei.model;

import java.time.LocalDate;

public class Habit extends UserActivity {
    private int id;
    private int userId;
    private final LocalDate creationDate; // Tanggal pembuatan yang sebenarnya
    private LocalDate date; // Tanggal last_updated

    // Constructor untuk memuat dari DB
    public Habit(int id, int userId, String name, LocalDate creationDate, LocalDate lastUpdated) {
        super(name);
        this.id = id;
        this.userId = userId;
        this.creationDate = creationDate;
        this.date = lastUpdated;
    }

    // Constructor untuk membuat habit baru
    public Habit(int userId, String name, LocalDate creationDate) {
        super(name);
        this.userId = userId;
        this.creationDate = creationDate;
        this.date = creationDate;
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public LocalDate getDate() { return date; }
    public LocalDate getCreationDate() { return creationDate; } // Getter yang dibutuhkan

    // Setters
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