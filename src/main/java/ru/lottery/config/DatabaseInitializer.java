package ru.lottery.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initTables() {
        String createTicketsTable = """
            CREATE TABLE IF NOT EXISTS tickets (
                id SERIAL PRIMARY KEY,
                draw_id BIGINT NOT NULL,
                user_id BIGINT NOT NULL,
                numbers VARCHAR(255) NOT NULL,
                status VARCHAR(20) DEFAULT 'PENDING'
            )
        """;

        String createDrawResultsTable = """
            CREATE TABLE IF NOT EXISTS draw_results (
                draw_id BIGINT PRIMARY KEY,
                winning_numbers VARCHAR(255) NOT NULL
            )
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createTicketsTable);
            System.out.println("✅ Таблица tickets создана/проверена");

            stmt.execute(createDrawResultsTable);
            System.out.println("✅ Таблица draw_results создана/проверена");

        } catch (SQLException e) {
            System.err.println("❌ Ошибка создания таблиц: " + e.getMessage());
            e.printStackTrace();
        }
    }
}