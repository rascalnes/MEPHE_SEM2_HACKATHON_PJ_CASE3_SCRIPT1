package ru.lottery.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5433/lottery";
    private static final String USER = "postgres";
    private static final String PASSWORD = "password";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Подключение к PostgreSQL установлено!");
            return connection;
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Драйвер PostgreSQL не найден!");
            throw new SQLException("Драйвер не загружен", e);
        }
    }
}