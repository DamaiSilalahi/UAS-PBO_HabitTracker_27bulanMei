package com.example.uaspbo_habittracker_27bulanmei.db;

import com.example.uaspbo_habittracker_27bulanmei.model.Habit;
import com.example.uaspbo_habittracker_27bulanmei.model.User;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:habit_tracker.db";

    private Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void initializeDatabase() {
        String userTableSql = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "username TEXT NOT NULL UNIQUE,"
                + "password TEXT NOT NULL"
                + ");";

        String habitTableSql = "CREATE TABLE IF NOT EXISTS habits ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "user_id INTEGER NOT NULL,"
                + "name TEXT NOT NULL,"
                + "status INTEGER NOT NULL DEFAULT 0,"
                + "last_updated TEXT NOT NULL,"
                + "FOREIGN KEY (user_id) REFERENCES users(id)"
                + ");";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(userTableSql);
            stmt.execute(habitTableSql);
        } catch (SQLException e) {
            System.out.println("Error saat inisialisasi database: " + e.getMessage());
        }
    }

    public boolean signUpUser(String username, String password) {
        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password); // Di dunia nyata, HASH password ini!
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public User signInUser(String username, String password) {
        String sql = "SELECT id, username FROM users WHERE username = ? AND password = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("id"), rs.getString("username"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public ArrayList<Habit> getHabitsForUser(int userId) {
        String sql = "SELECT id, name, status, last_updated FROM habits WHERE user_id = ?";
        ArrayList<Habit> habits = new ArrayList<>();
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Habit habit = new Habit(
                        rs.getInt("id"),
                        userId,
                        rs.getString("name"),
                        LocalDate.parse(rs.getString("last_updated"))
                );
                if (rs.getInt("status") == 1) {
                    habit.markCompletedFromDB();
                }
                habits.add(habit);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return habits;
    }

    public Habit addHabit(Habit habit) {
        String sql = "INSERT INTO habits(user_id, name, status, last_updated) VALUES(?,?,?,?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, habit.getUserId());
            pstmt.setString(2, habit.getName());
            pstmt.setInt(3, habit.isCompleted() ? 1 : 0);
            pstmt.setString(4, habit.getDate().toString());
            pstmt.executeUpdate();

            // Ambil ID yang di-generate oleh DB dan set ke objek habit
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                habit.setId(generatedKeys.getInt(1));
            }
            return habit;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void updateHabit(Habit habit) {
        String sql = "UPDATE habits SET status = ?, last_updated = ? WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, habit.isCompleted() ? 1 : 0);
            pstmt.setString(2, habit.getDate().toString());
            pstmt.setInt(3, habit.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}