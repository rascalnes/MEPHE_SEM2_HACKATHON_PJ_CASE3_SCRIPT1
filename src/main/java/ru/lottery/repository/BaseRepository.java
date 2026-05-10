package ru.lottery.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lottery.config.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class BaseRepository<T> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected abstract String getTableName();
    protected abstract T mapRowToEntity(ResultSet rs) throws SQLException;
    protected abstract void mapEntityToParams(PreparedStatement ps, T entity, boolean isUpdate) throws SQLException;

    // Helper method to get connection
    protected Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    // Execute query with parameters
    protected PreparedStatement prepareStatement(String sql, Object... params) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
        return ps;
    }

    // Execute update (INSERT, UPDATE, DELETE)
    protected int executeUpdate(String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = prepareStatement(sql, params)) {
            return ps.executeUpdate();
        }
    }

    // Execute query and return list of entities
    protected List<T> executeQuery(String sql, Object... params) throws SQLException {
        List<T> results = new ArrayList<>();
        try (PreparedStatement ps = prepareStatement(sql, params);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(mapRowToEntity(rs));
            }
        }
        return results;
    }

    // Execute query and return single entity
    protected Optional<T> executeQuerySingle(String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = prepareStatement(sql, params);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return Optional.of(mapRowToEntity(rs));
            }
        }
        return Optional.empty();
    }

    // Generate INSERT statement automatically
    protected String generateInsertStatement(String... columns) {
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(getTableName()).append(" (");
        sql.append(String.join(", ", columns));
        sql.append(") VALUES (");
        sql.append("?, ".repeat(columns.length).trim().replaceAll(",$", ""));
        sql.append(")");
        return sql.toString();
    }

    // Generate SELECT by ID statement
    protected String generateSelectByIdStatement() {
        return "SELECT * FROM " + getTableName() + " WHERE id = ?";
    }

    // Generate SELECT all statement
    protected String generateSelectAllStatement() {
        return "SELECT * FROM " + getTableName();
    }

    // Generate DELETE statement
    protected String generateDeleteStatement() {
        return "DELETE FROM " + getTableName() + " WHERE id = ?";
    }

    // CRUD operations
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

    // Transaction management
    public void executeInTransaction(TransactionOperation operation) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            operation.execute(conn);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    logger.error("Rollback failed", rollbackEx);
                }
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