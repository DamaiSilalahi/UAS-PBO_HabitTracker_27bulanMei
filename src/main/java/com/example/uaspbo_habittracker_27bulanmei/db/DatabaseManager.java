package com.example.uaspbo_habittracker_27bulanmei.db;

import com.example.uaspbo_habittracker_27bulanmei.model.Habit;
import com.example.uaspbo_habittracker_27bulanmei.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth; // <-- IMPORT BARU
import java.util.ArrayList;
import java.util.HashMap;    // <-- IMPORT BARU
import java.util.HashSet;    // <-- IMPORT BARU
import java.util.Map;        // <-- IMPORT BARU
import java.util.Set;        // <-- IMPORT BARU
import java.util.List;

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

        // KODE BARU DITAMBAHKAN DI SINI
        String historyTableSql = "CREATE TABLE IF NOT EXISTS habit_history ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "habit_id INTEGER NOT NULL,"
                + "completion_date TEXT NOT NULL,"
                + "FOREIGN KEY (habit_id) REFERENCES habits(id) ON DELETE CASCADE,"
                + "UNIQUE(habit_id, completion_date)"
                + ");";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(userTableSql);
            stmt.execute(habitTableSql);
            stmt.execute(historyTableSql); // <-- EKSEKUSI QUERY BARU
        } catch (SQLException e) {
            System.out.println("Error saat inisialisasi database: " + e.getMessage());
        }
    }

    // Method untuk mencatat penyelesaian habit
    public void logHabitCompletion(int habitId, LocalDate date) {
        String sql = "INSERT OR IGNORE INTO habit_history(habit_id, completion_date) VALUES(?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, habitId);
            pstmt.setString(2, date.toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Method untuk mengambil semua tanggal di mana ada habit yang selesai
    public Set<LocalDate> getCompletedDatesForMonth(int userId, YearMonth month) {
        Set<LocalDate> completedDates = new HashSet<>();
        String sql = "SELECT DISTINCT hh.completion_date FROM habit_history hh " +
                "JOIN habits h ON hh.habit_id = h.id " +
                "WHERE h.user_id = ? AND strftime('%Y-%m', hh.completion_date) = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, month.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                completedDates.add(LocalDate.parse(rs.getString("completion_date")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return completedDates;
    }

    // Method untuk mendapatkan ringkasan (contoh: per bulan)
    public Map<String, Integer> getMonthlySummary(int userId, YearMonth month) {
        Map<String, Integer> summary = new HashMap<>();
        String sql = "SELECT h.name, COUNT(hh.id) as count FROM habit_history hh " +
                "JOIN habits h ON hh.habit_id = h.id " +
                "WHERE h.user_id = ? AND strftime('%Y-%m', hh.completion_date) = ? " +
                "GROUP BY h.name ORDER BY count DESC";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, month.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                summary.put(rs.getString("name"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return summary;
    }

    // ... Sisa dari method-method Anda (signUpUser, signInUser, dll.) tidak perlu diubah ...
    // ... dan diletakkan di bawah sini ...
    public boolean signUpUser(String username, String password) {
        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
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

    public Map<Integer, List<LocalDate>> getHabitCompletionLog(int userId) {
        Map<Integer, List<LocalDate>> logMap = new HashMap<>();
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT habit_id, completion_date FROM habit_history WHERE user_id = ?")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int habitId = rs.getInt("habit_id");
                LocalDate date = rs.getDate("completion_date").toLocalDate();
                logMap.computeIfAbsent(habitId, k -> new ArrayList<>()).add(date);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logMap;
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

    public int getTotalHabitDays(int userId) {
        String query = """
        SELECT COUNT(DISTINCT completion_date)
        FROM habit_history
        WHERE habit_id IN (
            SELECT id FROM habits WHERE user_id = ?
        )
    """;

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Method untuk mengambil semua riwayat penyelesaian per habit ID
    public Map<Integer, List<LocalDate>> getHabitCompletionHistory(int userId) {
        Map<Integer, List<LocalDate>> historyMap = new HashMap<>();
        String sql = "SELECT h.id as habit_id, hh.completion_date " +
                "FROM habit_history hh JOIN habits h ON hh.habit_id = h.id " +
                "WHERE h.user_id = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int habitId = rs.getInt("habit_id");
                LocalDate date = LocalDate.parse(rs.getString("completion_date"));
                historyMap.computeIfAbsent(habitId, k -> new ArrayList<>()).add(date);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return historyMap;
    }

    // Method untuk mengambil semua tanggal unik di mana ada habit yang selesai
    public Set<LocalDate> getAllUniqueCompletionDates(int userId) {
        Set<LocalDate> dates = new HashSet<>();
        String sql = "SELECT DISTINCT hh.completion_date FROM habit_history hh " +
                "JOIN habits h ON hh.habit_id = h.id WHERE h.user_id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                dates.add(LocalDate.parse(rs.getString("completion_date")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return dates;
    }

    public void removeHabitCompletionLog(int habitId, LocalDate date) {
        String sql = "DELETE FROM habit_history WHERE habit_id = ? AND completion_date = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, habitId);
            pstmt.setString(2, date.toString());

            // --- DEBUG PRINT ---
            System.out.println("DEBUG [DBManager]: Menjalankan SQL -> " + sql + " dengan habit_id=" + habitId + ", completion_date=" + date.toString());

            int rowsAffected = pstmt.executeUpdate();

            // --- DEBUG PRINT ---
            System.out.println("DEBUG [DBManager]: Jumlah baris yang terhapus dari habit_history: " + rowsAffected);

        } catch (SQLException e) {
            System.out.println("Error saat menghapus riwayat habit: " + e.getMessage());
        }
    }


}
