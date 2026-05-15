package RepButtons;

//data access
import config.ConnectDB;

//javafx sdk
import java.sql.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

public class AuditLog {

    public TableView<ObservableList<String>> getTable() {
        TableView<ObservableList<String>> table = new TableView<>();
        
        table.setStyle("-fx-font-family: 'Times New Roman';");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // sql query to load data for Audit Log
        loadData(table, "SELECT * FROM items");

        return table;
    }

    //table data loader (getter)
    protected void loadData(TableView<ObservableList<String>> table, String query) {
        table.getColumns().clear();
        
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        
        try (Connection conn = ConnectDB.getConnection();
             ResultSet rs = conn.createStatement().executeQuery(query)) {
            
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 0; i < columnCount; i++) {
                final int j = i;
                    String rawName = metaData.getColumnName(i + 1);
                    String formalName = rawName.replace("_", " ").toUpperCase();
                
                TableColumn<ObservableList<String>, String> col = new TableColumn<>(formalName);
                col.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(j)));
                
                table.getColumns().add(col);
            }

            //populate rows
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= columnCount; i++) {
                    String value = rs.getString(i);
                    row.add(value == null ? "" : value);
                }
                
                data.add(row);
            }
            
            table.setItems(data);
            
        } 
        catch (Exception e) { 
            e.printStackTrace(); 
        }
    }
}