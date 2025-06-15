package com.example.uaspbo_habittracker_27bulanmei.model;

import java.time.LocalDate;

public class Habit extends UserActivity {
    private int id; // ID dari database
    private int userId;
    private LocalDate date;

    // Constructor untuk memuat habit dari database
    public Habit(int id, int userId, String name, LocalDate date) {
        super(name);
        this.id = id;
        this.userId = userId;
        this.date = date;
    }

    // Constructor untuk membuat habit baru (sebelum disimpan ke DB)
    public Habit(int userId, String name, LocalDate date) {
        super(name);
        this.userId = userId;
        this.date = date;
        // id akan di-generate oleh database
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public LocalDate getDate() { return date; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setDate(LocalDate date) { this.date = date; }

    @Override
    public void markCompleted() {
        this.status = true;
        System.out.println(name + " ditandai selesai pada " + date);
    }

    // Dipakai saat memuat dari DB agar tidak spam console
    public void markCompletedFromDB() {
        this.status = true;
    }

    public void resetStatusIfNewDay(LocalDate today) {
        if (!this.date.equals(today)) {
            resetStatus();
            this.date = today;
        }
    }
}