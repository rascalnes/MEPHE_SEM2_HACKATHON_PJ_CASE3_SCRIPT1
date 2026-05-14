package ru.lottery.repository;

import ru.lottery.model.User;
import ru.lottery.model.enums.UserRole;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class UserRepository extends BaseRepository<User> {

    @Override
    protected String getTableName() {
        return "users";
    }

    @Override
    protected User mapRowToEntity(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(UUID.fromString(rs.getString("id")));
        user.setLogin(rs.getString("login"));
        user.setPassword(rs.getString("password"));
        user.setRole(UserRole.fromValue(rs.getString("role")));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    }

    @Override
    protected void mapEntityToParams(PreparedStatement ps, User user, boolean isUpdate) throws SQLException {
        ps.setString(1, user.getLogin());
        ps.setString(2, user.getPassword());
        ps.setString(3, user.getRole().getValue());
        if (isUpdate) {
            ps.setObject(4, user.getId());
        }
    }

    @Override
    public User save(User user) throws SQLException {
        String sql = "INSERT INTO users (login, password, role) VALUES (?, ?, ?) RETURNING id, created_at";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole().getValue());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user.setId(UUID.fromString(rs.getString("id")));
                    user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                }
            }
        }
        return user;
    }

    public Optional<User> findByLogin(String login) throws SQLException {
        String sql = "SELECT * FROM users WHERE login = ?";
        return executeQuerySingle(sql, login);
    }

    public Optional<User> findById(UUID id) throws SQLException {
        if (id == null) return Optional.empty();
        String sql = "SELECT * FROM users WHERE id = ?::uuid";
        return executeQuerySingle(sql, id.toString());
    }

    public boolean existsByLogin(String login) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE login = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}
