package ru.lottery.repository;

import ru.lottery.model.Ticket;
import ru.lottery.config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketRepository {

    public Ticket save(Ticket ticket) {
        String sql = "INSERT INTO tickets (draw_id, user_id, numbers, status) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, ticket.getDrawId());
            pstmt.setLong(2, ticket.getUserId());
            pstmt.setString(3, ticket.getNumbers());
            pstmt.setString(4, ticket.getStatus() != null ? ticket.getStatus() : "PENDING");
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) ticket.setId(rs.getLong(1));
            return ticket;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Ticket findById(Long id) {
        String sql = "SELECT * FROM tickets WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Ticket ticket = new Ticket();
                ticket.setId(rs.getLong("id"));
                ticket.setDrawId(rs.getLong("draw_id"));
                ticket.setUserId(rs.getLong("user_id"));
                ticket.setNumbers(rs.getString("numbers"));
                ticket.setStatus(rs.getString("status"));
                return ticket;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Ticket> findByDrawId(Long drawId) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets WHERE draw_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, drawId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Ticket ticket = new Ticket();
                ticket.setId(rs.getLong("id"));
                ticket.setDrawId(rs.getLong("draw_id"));
                ticket.setUserId(rs.getLong("user_id"));
                ticket.setNumbers(rs.getString("numbers"));
                ticket.setStatus(rs.getString("status"));
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    public void update(Ticket ticket) {
        String sql = "UPDATE tickets SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ticket.getStatus());
            pstmt.setLong(2, ticket.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}