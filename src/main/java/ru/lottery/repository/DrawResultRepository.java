package ru.lottery.repository;

import ru.lottery.model.DrawResult;
import ru.lottery.config.DatabaseConnection;

import java.sql.*;

public class DrawResultRepository {

    public void save(DrawResult result) {
        String sql = "INSERT INTO draw_results (draw_id, winning_numbers) VALUES (?, ?) ON CONFLICT (draw_id) DO UPDATE SET winning_numbers = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, result.getDrawId());
            pstmt.setString(2, result.getWinningNumbers());
            pstmt.setString(3, result.getWinningNumbers());
            pstmt.executeUpdate();

            System.out.println("✅ Результат тиража #" + result.getDrawId() + " сохранён в БД");

        } catch (SQLException e) {
            System.err.println("❌ Ошибка сохранения результата: " + e.getMessage());
        }
    }

    public DrawResult findByDrawId(Long drawId) {
        String sql = "SELECT * FROM draw_results WHERE draw_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, drawId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                DrawResult result = new DrawResult();
                result.setDrawId(rs.getLong("draw_id"));
                result.setWinningNumbers(rs.getString("winning_numbers"));
                return result;
            }

        } catch (SQLException e) {
            System.err.println("❌ Ошибка поиска результата: " + e.getMessage());
        }
        return null;
    }

    public void update(DrawResult result) {
        save(result);
    }
}