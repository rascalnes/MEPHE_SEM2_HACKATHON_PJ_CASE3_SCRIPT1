package ru.lottery.repository;

import ru.lottery.config.DatabaseConnection;
import ru.lottery.model.Ticket;
import ru.lottery.model.enums.TicketStatus;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TicketRepository {

    public Ticket save(Ticket ticket) throws SQLException {
        String sql = "INSERT INTO tickets (draw_id, user_id, numbers, status, purchased_at) VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, ticket.getDrawId());
            ps.setObject(2, ticket.getUserId());
            ps.setString(3, ticket.getNumbers());
            ps.setString(4, ticket.getStatus().name());
            ps.setTimestamp(5, ticket.getPurchasedAt());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) ticket.setId(rs.getLong("id"));
        }
        return ticket;
    }

    public List<Ticket> findByUserId(UUID userId) throws SQLException {
        String sql = "SELECT * FROM tickets WHERE user_id = ? ORDER BY purchased_at DESC";
        return findTicketsByUserIdParam(sql, userId);
    }

    public List<Ticket> findByUserIdAndDrawId(UUID userId, Long drawId) throws SQLException {
        String sql = "SELECT * FROM tickets WHERE user_id = ? AND draw_id = ? ORDER BY purchased_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, userId);
            ps.setLong(2, drawId);
            return extractTickets(ps);
        }
    }

    public List<Ticket> findByDrawId(Long drawId) throws SQLException {
        String sql = "SELECT * FROM tickets WHERE draw_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, drawId);
            return extractTickets(ps);
        }
    }

    public Optional<Ticket> findById(Long ticketId) throws SQLException {
        String sql = "SELECT * FROM tickets WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, ticketId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    public void updateStatus(Long ticketId, TicketStatus status) throws SQLException {
        String sql = "UPDATE tickets SET status = ?, checked_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setLong(2, ticketId);
            ps.executeUpdate();
        }
    }

    private List<Ticket> findTicketsByUserIdParam(String sql, UUID userId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, userId);
            return extractTickets(ps);
        }
    }

    private List<Ticket> extractTickets(PreparedStatement ps) throws SQLException {
        ResultSet rs = ps.executeQuery();
        List<Ticket> tickets = new ArrayList<>();
        while (rs.next()) tickets.add(mapRow(rs));
        return tickets;
    }

    private Ticket mapRow(ResultSet rs) throws SQLException {
        Ticket t = new Ticket();
        t.setId(rs.getLong("id"));
        t.setDrawId(rs.getLong("draw_id"));
        t.setUserId(rs.getObject("user_id", UUID.class));
        t.setNumbers(rs.getString("numbers"));
        t.setStatus(TicketStatus.valueOf(rs.getString("status")));
        t.setCheckedAt(rs.getTimestamp("checked_at"));
        t.setPurchasedAt(rs.getTimestamp("purchased_at"));
        return t;
    }
}
