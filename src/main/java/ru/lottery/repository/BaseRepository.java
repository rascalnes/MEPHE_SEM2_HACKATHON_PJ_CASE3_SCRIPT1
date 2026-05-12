package ru.lottery.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lottery.config.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class BaseRepository<T> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected abstract String getTableName();
    protected abstract T mapRowToEntity(ResultSet rs) throws SQLException;
    protected abstract void mapEntityToParams(PreparedStatement ps, T entity, boolean isUpdate) throws SQLException;

    protected Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    protected int executeUpdate(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeUpdate();
        }
    }

    protected List<T> executeQuery(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<T> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(mapRowToEntity(rs));
                }
                return results;
            }
        }
    }

    protected Optional<T> executeQuerySingle(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToEntity(rs));
                }
            }
        }
        return Optional.empty();
    }

    protected String generateInsertStatement(String... columns) {
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(getTableName()).append(" (");
        sql.append(String.join(", ", columns));
        sql.append(") VALUES (");
        sql.append("?, ".repeat(columns.length).replaceAll(", $", ""));
        sql.append(")");
        return sql.toString();
    }

    protected String generateSelectByIdStatement() {
        return "SELECT * FROM " + getTableName() + " WHERE id = ?";
    }

    protected String generateSelectAllStatement() {
        return "SELECT * FROM " + getTableName();
    }

    protected String generateDeleteStatement() {
        return "DELETE FROM " + getTableName() + " WHERE id = ?";
    }

    public T save(T entity) throws SQLException {
        throw new UnsupportedOperationException("Implement in subclass");
    }

    public Optional<T> findById(Long id) throws SQLException {
        String sql = generateSelectByIdStatement();
        return executeQuerySingle(sql, id);
    }

    public List<T> findAll() throws SQLException {
        String sql = generateSelectAllStatement();
        return executeQuery(sql);
    }

    public boolean delete(Long id) throws SQLException {
        String sql = generateDeleteStatement();
        return executeUpdate(sql, id) > 0;
    }

    public void executeInTransaction(TransactionOperation operation) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            operation.execute(conn);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException rollbackEx) { logger.error("Rollback failed", rollbackEx); }
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    @FunctionalInterface
    public interface TransactionOperation {
        void execute(Connection conn) throws SQLException;
    }
}
