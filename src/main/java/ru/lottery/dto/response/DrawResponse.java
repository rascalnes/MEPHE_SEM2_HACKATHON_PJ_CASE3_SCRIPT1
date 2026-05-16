package ru.lottery.dto.response;

import ru.lottery.model.Draw;
import ru.lottery.model.DrawResult;

import java.sql.*;
import java.time.LocalDateTime;

import static ru.lottery.config.DatabaseConnection.getConnection;

public class DrawResponse {
    private boolean success;
    private String message;
    private DrawData draw;
    private String winningCombo;

    public DrawResponse(boolean success, String message, DrawData draw, String winningCombo) {
        this.success = success;
        this.message = message;
        this.draw = draw;
        this.winningCombo = winningCombo;
    }

    public static DrawResponse success(Draw draw, String message) {
        return new DrawResponse(true, message, DrawData.fromDraw(draw), null);
    }

    public static DrawResponse successWithResult(Draw draw, DrawResult result, String message) {
        DrawResponse response = new DrawResponse(true, message, DrawData.fromDraw(draw), result.getWinningCombo());
        response.setWinningCombo(result.getWinningCombo());
        return response;
    }

    public static DrawResponse error(String message) {
        return new DrawResponse(false, message, null, null);
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public DrawData getDraw() { return draw; }
    public void setDraw(DrawData draw) { this.draw = draw; }

    public String getWinningCombo() { return winningCombo; }
    public void setWinningCombo(String winningCombo) { this.winningCombo = winningCombo; }

    public static class DrawData {
        private Long id;
        private String name;
        private String status;
        private String createdBy;
        private LocalDateTime createdAt;
        private LocalDateTime startedAt;
        private LocalDateTime finishedAt;

        public static DrawData fromDraw(Draw draw) {
            DrawData data = new DrawData();
            data.setId(draw.getId());
            data.setName(draw.getName());
            data.setStatus(draw.getStatus().getValue());
            data.setCreatedBy(draw.getCreatedBy() != null ? draw.getCreatedBy().toString() : null);
            data.setCreatedAt(draw.getCreatedAt());
            data.setStartedAt(draw.getStartedAt());
            data.setFinishedAt(draw.getFinishedAt());
            return data;
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getCreatedBy() { return createdBy; }
        public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        public LocalDateTime getStartedAt() { return startedAt; }
        public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

        public LocalDateTime getFinishedAt() { return finishedAt; }
        public void setFinishedAt(LocalDateTime finishedAt) { this.finishedAt = finishedAt; }
    }

    public Draw save(Draw draw) throws SQLException {
        String sql = "INSERT INTO draws (name, status, created_by, started_at, finished_at) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING id, created_at";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, draw.getName());
            ps.setString(2, draw.getStatus().getValue());

            // Fix: Convert UUID to proper format for PostgreSQL
            if (draw.getCreatedBy() != null) {
                ps.setObject(3, draw.getCreatedBy(), java.sql.Types.OTHER);
            } else {
                ps.setNull(3, java.sql.Types.OTHER);
            }

            if (draw.getStartedAt() != null) {
                ps.setTimestamp(4, Timestamp.valueOf(draw.getStartedAt()));
            } else {
                ps.setNull(4, Types.TIMESTAMP);
            }

            if (draw.getFinishedAt() != null) {
                ps.setTimestamp(5, Timestamp.valueOf(draw.getFinishedAt()));
            } else {
                ps.setNull(5, Types.TIMESTAMP);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    draw.setId(rs.getLong("id"));
                    draw.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                }
            }
        }
        return draw;
    }
}