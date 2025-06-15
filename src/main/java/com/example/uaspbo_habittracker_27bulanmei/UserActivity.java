package com.example.uaspbo_habittracker_27bulanmei;

public abstract class UserActivity {
    protected String name;
    protected boolean status;

    public UserActivity(String name) {
        this.name = name;
        this.status = false;
    }

    public String getName() {
        return name;
    }

    public boolean isCompleted() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Method polymorphic yang harus di-override oleh subclass
    public abstract void markCompleted();

    // Bisa juga buat reset status
    public void resetStatus() {
        status = false;
    }
}
