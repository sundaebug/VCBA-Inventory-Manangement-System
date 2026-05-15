package Login;

//sql connections
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//data access
import config.ConnectDB;

public class validateLogIn {
   
    //log in validation
    public static boolean isLogIn(String username, String password) {
        
        //sql query
        String sql = "SELECT * FROM users WHERE username = ? AND pass = ?";

        //try-catch for exclusive connection (closed after every pass/transaction).
        try (Connection conn = ConnectDB.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
                return rs.next(); 

        } catch (SQLException e) {
            
            e.printStackTrace();
                return false;
        }
    }
}