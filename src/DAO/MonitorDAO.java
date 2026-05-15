package DAO;

import DTO.Monitor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MonitorDAO {
    private Connection conn;

    public MonitorDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean logBorrow(Monitor monitor) throws SQLException {
        // Removed received_by from the SQL insert
        String sql = "INSERT INTO monitoring (borrower_name, item_id, quantity, date_borrowed, date_to_return) "
                   + "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, monitor.getBorrowerName());
            ps.setInt(2, monitor.getItemId()); 
            ps.setInt(3, monitor.getQuantity());
            ps.setDate(4, monitor.getDateBorrowed());
            ps.setDate(5, monitor.getDeadline());
            return ps.executeUpdate() > 0;
        }
    }

    public List<Monitor> getAllRecords() throws SQLException {
        List<Monitor> list = new ArrayList<>();
        String sql = "SELECT m.*, i.item_name, " +
                     "CASE " +
                     "  WHEN m.date_to_return < CURRENT_DATE THEN 'OVERDUE' " +
                     "  WHEN m.date_to_return = CURRENT_DATE THEN 'DUE' " +
                     "  ELSE 'ONGOING' " +
                     "END AS live_status " +
                     "FROM monitoring m JOIN items i ON m.item_id = i.id";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Monitor(
                    rs.getInt("borrow_id"),
                    rs.getString("borrower_name"),
                    rs.getInt("item_id"),
                    rs.getString("item_name"), 
                    rs.getInt("quantity"),
                    rs.getDate("date_borrowed"),
                    rs.getDate("date_to_return"),
                    null, // date_returned not in monitoring anymore
                    null, // received_by not in monitoring anymore
                    rs.getString("live_status")
                ));
            }
        }
        return list;
    }

    // New method to process a return and move it to history
    public boolean processReturn(Monitor monitor) throws SQLException {
        String insertHistory = "INSERT INTO borrow_history (borrower_name, item_id, quantity, date_borrowed, date_to_return, date_returned, received_by) "
                             + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        conn.setAutoCommit(false);
        try {
            // 1. Insert into history
            try (PreparedStatement ps = conn.prepareStatement(insertHistory)) {
                ps.setString(1, monitor.getBorrowerName());
                ps.setInt(2, monitor.getItemId());
                ps.setInt(3, monitor.getQuantity());
                ps.setDate(4, monitor.getDateBorrowed());
                ps.setDate(5, monitor.getDeadline());
                ps.setDate(6, monitor.getDateReturned());
                ps.setString(7, monitor.getReceivedBy());
                ps.executeUpdate();
            }
            
            // 2. Delete from active monitoring
            deleteRecord(monitor.getBorrowId());
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public List<Monitor> getHistoryRecords() throws SQLException {
        List<Monitor> list = new ArrayList<>();
        String sql = "SELECT h.*, i.item_name FROM borrow_history h JOIN items i ON h.item_id = i.id";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Monitor(
                    rs.getInt("history_id"), rs.getString("borrower_name"), rs.getInt("item_id"),
                    rs.getString("item_name"), rs.getInt("quantity"), rs.getDate("date_borrowed"),
                    rs.getDate("date_to_return"), rs.getDate("date_returned"), rs.getString("received_by"), "RETURNED"
                ));
            }
        }
        return list;
    }

    public boolean updateRecord(Monitor monitor) throws SQLException {
        String sql = "UPDATE monitoring SET borrower_name=?, quantity=?, date_borrowed=?, date_to_return=? WHERE borrow_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, monitor.getBorrowerName());
            ps.setInt(2, monitor.getQuantity());
            ps.setDate(3, monitor.getDateBorrowed());
            ps.setDate(4, monitor.getDeadline());
            ps.setInt(5, monitor.getBorrowId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteRecord(int borrowId) throws SQLException {
        String sql = "DELETE FROM monitoring WHERE borrow_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, borrowId);
            return ps.executeUpdate() > 0;
        }
    }
}