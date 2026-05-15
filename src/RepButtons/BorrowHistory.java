package RepButtons;

//data access
import DAO.MonitorDAO;
import DTO.Monitor;
import config.ConnectDB;

//import accessory for components (javafx sdk)
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.Date;

public class BorrowHistory {

    //This method returns the TableView directly to Reports.java
    
    public TableView<Monitor> getTable() {
        
        TableView<Monitor> table = new TableView<>();
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            table.setStyle("-fx-font-family: 'Times New Roman';");

        //Column Definitions
        TableColumn<Monitor, String> colBorrower = new TableColumn<>("BORROWER NAME");
        colBorrower.setCellValueFactory(new PropertyValueFactory<>("borrowerName"));
        
        TableColumn<Monitor, String> colItem = new TableColumn<>("ITEM NAME");
        colItem.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        
        TableColumn<Monitor, Integer> colQty = new TableColumn<>("QUANTITY");
        colQty.setPrefWidth(80);
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        
        TableColumn<Monitor, Date> colBorrowed = new TableColumn<>("BORROW DATE");
        colBorrowed.setCellValueFactory(new PropertyValueFactory<>("dateBorrowed"));
        
        TableColumn<Monitor, Date> colReturned = new TableColumn<>("RETURNED DATE");
        colReturned.setCellValueFactory(new PropertyValueFactory<>("dateReturned"));
        
        TableColumn<Monitor, String> colReceived = new TableColumn<>("RECEIVED BY");
        colReceived.setCellValueFactory(new PropertyValueFactory<>("receivedBy"));

        TableColumn<Monitor, String> colStatus = new TableColumn<>("STATUS");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(colBorrower, colItem, colQty, colBorrowed, colReturned, colReceived, colStatus);

        //Data Loading (try-catch)
        try {
            MonitorDAO dao = new MonitorDAO(ConnectDB.getConnection());
            table.setItems(FXCollections.observableArrayList(dao.getHistoryRecords()));
        }
        catch (Exception e) { 
            e.printStackTrace(); 
        }

        return table;
    }
}