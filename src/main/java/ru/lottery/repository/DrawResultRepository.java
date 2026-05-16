package ru.lottery.repository;

import ru.lottery.model.DrawResult;

import java.sql.*;
import java.util.Optional;

public class DrawResultRepository extends BaseRepository<DrawResult> {

    @Override
    protected String getTableName() {
        return "draw_results";
    }

    @Override
    protected DrawResult mapRowToEntity(ResultSet rs) throws SQLException {
        DrawResult result = new DrawResult();
        result.setId(rs.getLong("id"));
        result.setDrawId(rs.getLong("draw_id"));
        result.setWinningCombo(rs.getString("winning_combo"));
        result.setGeneratedAt(rs.getTimestamp("generated_at").toLocalDateTime());
        return result;
    }

    @Override
    protected void mapEntityToParams(PreparedStatement ps, DrawResult result, boolean isUpdate) throws SQLException {
        ps.setLong(1, result.getDrawId());
        ps.setString(2, result.getWinningCombo());

        if (isUpdate && result.getId() != null) {
            ps.setLong(3, result.getId());
        }
    }

    public DrawResult save(DrawResult result) throws SQLException {
        String sql = "INSERT INTO draw_results (draw_id, winning_combo) VALUES (?, ?) RETURNING id, generated_at";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setLong(1, result.getDrawId());
            ps.setString(2, result.getWinningCombo());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result.setId(rs.getLong("id"));
                    result.setGeneratedAt(rs.getTimestamp("generated_at").toLocalDateTime());
                }
            }
        }
        return result;
    }

    public Optional<DrawResult> findByDrawId(Long drawId) throws SQLException {
        String sql = "SELECT * FROM draw_results WHERE draw_id = ? ORDER BY generated_at DESC LIMIT 1";
        return executeQuerySingle(sql, drawId);
    }

    public boolean existsByDrawId(Long drawId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM draw_results WHERE draw_id = ?";
        try (PreparedStatement ps = prepareStatement(sql, drawId);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}