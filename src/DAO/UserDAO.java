package DAO;

import config.ConnectDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import DTO.User;

public class UserDAO {

   
    public void addUser(User user) {
        String SQL = "INSERT INTO users (username, pass, role, fullname) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getPass());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getFullName());

            stmt.executeUpdate();
            System.out.println("User Registered Successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    public void updateUser(User user) {
        String SQL = "UPDATE users SET username=?, pass=?, role=?, fullname=? WHERE id=?";
        
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getPass());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getFullName());
            stmt.setInt(5, user.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User Profile Updated!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    public void deleteUser(int id) {
        String SQL = "DELETE FROM users WHERE id = ?";
        
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("User Deleted Successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}