package com.tugasakhir;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://mensapp_mirrorshot:5b054938058dfebc0b46bab6ae94422d581bfb73@s9u.h.filess.io:3307/mensapp_mirrorshot";
    private static final String USER = "mensapp_mirrorshot";
    private static final String PASS = "5b054938058dfebc0b46bab6ae94422d581bfb73";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public static int authenticateUser(String username, String password) {
        try (Connection connection = connect()) {
            String query = "SELECT id FROM user WHERE username = ? AND password = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);
                statement.setString(2, password);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("id");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    public static void simpanKeDatabase(LocalDate tanggal, int userId) throws SQLException {
        String query = "INSERT INTO mens (mens_date, user_id) VALUES (?, ?)";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, Date.valueOf(tanggal));
            statement.setInt(2, userId);
            statement.executeUpdate();
        }
    }

    public static List<MenstruationRecord> getMenstruationRecords(int userId) throws SQLException {
        List<MenstruationRecord> records = new ArrayList<>();
        String query = "SELECT u.username, m.mens_date FROM mens m JOIN user u ON m.user_id = u.id WHERE m.user_id = ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String username = resultSet.getString("username");
                    LocalDate mensDate = resultSet.getDate("mens_date").toLocalDate();
                    records.add(new MenstruationRecord(userId, username, mensDate));
                }
            }
        }
        return records;
    }
}

class MenstruationRecord {
    private int userId;
    private String username;
    private LocalDate mensDate;

    public MenstruationRecord(int userId, String username, LocalDate mensDate) {
        this.userId = userId;
        this.username = username;
        this.mensDate = mensDate;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public LocalDate getMensDate() {
        return mensDate;
    }
}
