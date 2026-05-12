package ru.lottery.repository;

import ru.lottery.config.DatabaseConnection;
import ru.lottery.model.Draw;
import ru.lottery.model.enums.DrawStatus;

import java.sql.*;
import java.util.Optional;

public class DrawRepository {

    public Optional<Draw> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM draws WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Draw draw = new Draw();
                draw.setId(rs.getLong("id"));
                draw.setName(rs.getString("name"));
                draw.setStatus(DrawStatus.valueOf(rs.getString("status")));
                // Остальные поля можно не заполнять для проверки
                return Optional.of(draw);
            }
        }
        return Optional.empty();
    }
}
