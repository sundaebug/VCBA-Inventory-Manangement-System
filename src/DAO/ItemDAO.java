package DAO;

import config.ConnectDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import DTO.Item;

public class ItemDAO {
    
        public void AddItem(Item item) {
        String SQL = "INSERT INTO items (item_name, category, quantity, unit_price, supplier, date_added) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            Connection conn= ConnectDB.getConnection();
            PreparedStatement stmt = conn.prepareStatement(SQL);
           
            stmt.setString(1, item.getItemName());
            stmt.setString(2, item.getCategory());
            stmt.setInt(3, item.getQuantity());
            stmt.setDouble(4, item.getUnitPrice());
            stmt.setString(5, item.getSupplier());
            stmt.setDate(6, item.getDateAdded());
           
            stmt.executeUpdate();

            System.out.println("\nItem Added Successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
        
    public List<Item> getAllItems() {
        List<Item> itemList = new ArrayList<>();
        String SQL = "SELECT * FROM items";

        try (Connection conn = ConnectDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {

            while (rs.next()) {
                Item item = new Item(
                    rs.getInt("id"),
                    rs.getString("item_name"),
                    rs.getString("category"),
                    rs.getInt("quantity"),
                    rs.getDouble("unit_price"),
                    rs.getString("supplier"),
                    rs.getDate("date_added")
                );
                itemList.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return itemList;
    }
        
        public void updateItem(Item item) {
        // We use the 'id' to find the specific record to update
        String SQL = "UPDATE items SET item_name=?, category=?, quantity=?, unit_price=?, supplier=?, date_added=? WHERE id=?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setString(1, item.getItemName());
            stmt.setString(2, item.getCategory());
            stmt.setInt(3, item.getQuantity());
            stmt.setDouble(4, item.getUnitPrice());
            stmt.setString(5, item.getSupplier());
            stmt.setDate(6, item.getDateAdded());
            stmt.setInt(7, item.getId()); // The WHERE clause ID

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Item Updated Successfully!");
            } else {
                System.out.println("Update failed: Item ID not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteItem(int id) {
        String SQL = "DELETE FROM items WHERE id = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Item Deleted Successfully!");
            } else {
                System.out.println("Delete failed: Item ID not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }    
        
    public boolean updateStock(int itemId, int amount) {
        String sql = "UPDATE items SET quantity = quantity + ? WHERE id = ?";
        try (Connection conn = config.ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, amount);
            ps.setInt(2, itemId);
            return ps.executeUpdate() > 0;
        } 
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }   
    
}