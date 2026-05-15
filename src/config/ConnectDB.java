package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
    // Replace with your actual database name, username, and password
    private static final String URL = "jdbc:mysql://localhost:3306/your_db";
    private static final String USER = " ";
    private static final String PASSWORD = " "; 

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found.");
        }
    }
}
