package ru.lottery.repository;

import ru.lottery.config.DatabaseConnection;
import ru.lottery.model.DrawResult;

import java.sql.*;
import java.util.Optional;

public class DrawResultRepository {
    public DrawResult save(DrawResult result) throws SQLException {
        String sql = "INSERT INTO draw_results (draw_id, winning_combo, generated_at) VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, result.getDrawId());
            ps.setString(2, result.getWinningCombo());
            ps.setTimestamp(3, result.getGeneratedAt());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) result.setId(rs.getLong("id"));
        }
        return result;
    }

    public Optional<DrawResult> findByDrawId(Long drawId) throws SQLException {
        String sql = "SELECT * FROM draw_results WHERE draw_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, drawId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                DrawResult dr = new DrawResult();
                dr.setId(rs.getLong("id"));
                dr.setDrawId(rs.getLong("draw_id"));
                dr.setWinningCombo(rs.getString("winning_combo"));
                dr.setGeneratedAt(rs.getTimestamp("generated_at"));
                return Optional.of(dr);
            }
        }
        return Optional.empty();
    }
}
