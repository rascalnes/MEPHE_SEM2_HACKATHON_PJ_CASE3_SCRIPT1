package ru.lottery.repository;

import ru.lottery.model.Draw;
import ru.lottery.model.enums.DrawStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DrawRepository extends BaseRepository<Draw> {

    @Override
    protected String getTableName() {
        return "draws";
    }

    @Override
    protected Draw mapRowToEntity(ResultSet rs) throws SQLException {
        Draw draw = new Draw();
        draw.setId(rs.getLong("id"));
        draw.setName(rs.getString("name"));
        draw.setStatus(DrawStatus.fromValue(rs.getString("status")));

        String createdByStr = rs.getString("created_by");
        if (createdByStr != null) {
            draw.setCreatedBy(UUID.fromString(createdByStr));
        }

        draw.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        Timestamp startedAt = rs.getTimestamp("started_at");
        if (startedAt != null) {
            draw.setStartedAt(startedAt.toLocalDateTime());
        }

        Timestamp finishedAt = rs.getTimestamp("finished_at");
        if (finishedAt != null) {
            draw.setFinishedAt(finishedAt.toLocalDateTime());
        }

        return draw;
    }

    @Override
    protected void mapEntityToParams(PreparedStatement ps, Draw draw, boolean isUpdate) throws SQLException {
        ps.setString(1, draw.getName());
        ps.setString(2, draw.getStatus().getValue());
        ps.setObject(3, draw.getCreatedBy());

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

        if (isUpdate && draw.getId() != null) {
            ps.setLong(6, draw.getId());
        }
    }

    public Draw save(Draw draw) throws SQLException {
        String sql = "INSERT INTO draws (name, status, created_by, started_at, finished_at) " +
                "VALUES (?, ?, ?::uuid, ?, ?) RETURNING id, created_at";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, draw.getName());
            ps.setString(2, draw.getStatus().getValue());
            ps.setObject(3, draw.getCreatedBy());

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

    public Draw update(Draw draw) throws SQLException {
        String sql = "UPDATE draws SET name = ?, status = ?, created_by = ?, " +
                "started_at = ?, finished_at = ? WHERE id = ?";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, draw.getName());
            ps.setString(2, draw.getStatus().getValue());

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

            ps.setLong(6, draw.getId());
            ps.executeUpdate();
        }
        return draw;
    }

    public Optional<Draw> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM draws WHERE id = ?";
        return executeQuerySingle(sql, id);
    }

    public List<Draw> findAll() throws SQLException {
        String sql = "SELECT * FROM draws ORDER BY created_at DESC";
        return executeQuery(sql);
    }

    public List<Draw> findByStatus(DrawStatus status) throws SQLException {
        String sql = "SELECT * FROM draws WHERE status = ? ORDER BY created_at DESC";
        return executeQuery(sql, status.getValue());
    }

    public List<Draw> findActiveDraws() throws SQLException {
        return findByStatus(DrawStatus.ACTIVE);
    }

    public boolean updateStatus(Long drawId, DrawStatus newStatus) throws SQLException {
        String sql = "UPDATE draws SET status = ? WHERE id = ?";
        return executeUpdate(sql, newStatus.getValue(), drawId) > 0;
    }

    public boolean updateStartTime(Long drawId, LocalDateTime startTime) throws SQLException {
        String sql = "UPDATE draws SET started_at = ? WHERE id = ?";
        return executeUpdate(sql, Timestamp.valueOf(startTime), drawId) > 0;
    }

    public boolean updateFinishTime(Long drawId, LocalDateTime finishTime) throws SQLException {
        String sql = "UPDATE draws SET finished_at = ? WHERE id = ?";
        return executeUpdate(sql, Timestamp.valueOf(finishTime), drawId) > 0;
    }
}