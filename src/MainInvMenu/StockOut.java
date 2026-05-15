package MainInvMenu;

//data access
import config.ConnectDB;

//javafx sdk
import java.sql.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class StockOut {
    
    private Parent previousView;
    private Dashboard dashboardController; // Added field for reference
    
    // Updated Constructor to accept Dashboard
    public StockOut(Dashboard dashboard, Parent previous) { 
        this.dashboardController = dashboard;
        this.previousView = previous; 
    }

    public Parent getView() {
        
        //mainLayout
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("stock-in-container");
        
        // style path
        try {
            String css = getClass().getResource("/Login/InventoryStyleSheet.css").toExternalForm();
            mainLayout.getStylesheets().add(css);
        } 
        catch (NullPointerException e) {
            System.out.println("CSS File not found.");
        }

        //THE WRAPPER
        VBox formWrapper = new VBox(30);
        formWrapper.getStyleClass().add("form-wrapper"); 
        formWrapper.setAlignment(Pos.CENTER);
        formWrapper.setMaxWidth(600); 
        formWrapper.setMaxHeight(Region.USE_PREF_SIZE); 
        formWrapper.setPadding(new Insets(50));

        //HEADER
        Label lblHeader = new Label("STOCK RELEASE (STOCK OUT)");
        lblHeader.getStyleClass().add("stock-in-title");

        //FORM LAYOUT
        GridPane grid = new GridPane();
        grid.setVgap(20);
        grid.setHgap(25);
        grid.setAlignment(Pos.CENTER);

        //Labels 
        Label lblItem = new Label("Item:");
        Label lblQty = new Label("Quantity:");
        Label lblDept = new Label("Department:");
        
        lblItem.getStyleClass().add("stock-in-label");
        lblQty.getStyleClass().add("stock-in-label");
        lblDept.getStyleClass().add("stock-in-label");

        //Inputs 
        ComboBox<String> comboItems = new ComboBox<>();
        loadItemNames(comboItems);
        comboItems.setPromptText("Select Item");
        comboItems.getStyleClass().add("stock-in-input");

        TextField txtQty = new TextField();
        txtQty.setPromptText("Enter quantity");
        txtQty.getStyleClass().add("stock-in-input");

        TextField txtDept = new TextField();
        txtDept.setPromptText("Enter department name");
        txtDept.getStyleClass().add("stock-in-input");

        //form placements
        grid.add(lblItem, 0, 0);      
        grid.add(comboItems, 1, 0);
        
        grid.add(lblQty, 0, 1);       
        grid.add(txtQty, 1, 1);
        
        grid.add(lblDept, 0, 2);      
        grid.add(txtDept, 1, 2);

        //BUTTONS
        Button btnSave = new Button("Release Stock");
        btnSave.getStyleClass().add("inoutbtn-save");

        Button btnBack = new Button("Back");
        btnBack.getStyleClass().add("inoutbtn-back");

        btnSave.setPrefWidth(200); 
        btnBack.setPrefWidth(200);

        HBox.setHgrow(btnSave, Priority.ALWAYS);
        HBox.setHgrow(btnBack, Priority.ALWAYS);

        HBox hbBtn = new HBox(15, btnSave, btnBack);
        hbBtn.setAlignment(Pos.CENTER);
        hbBtn.setMaxWidth(415); 

        //layout Assembly
        formWrapper.getChildren().addAll(lblHeader, grid, hbBtn);
        mainLayout.setCenter(formWrapper);

        //BUTTON LOGIC
        btnSave.setOnAction(e -> {
            try {
                int removedQty = Integer.parseInt(txtQty.getText());
                processStockOut(comboItems.getValue(), removedQty, txtDept.getText());
                txtQty.clear();
                txtDept.clear();
            } 
            catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Format Error", "Invalid quantity.");
            }
        });

        btnBack.setOnAction(e -> {
            if (previousView != null) {
                // REFRESH TABLE BEFORE EXITING
                if (dashboardController != null) {
                    dashboardController.refreshTable();
                }

                Stage stage = (Stage) btnBack.getScene().getWindow();
                Object tag = previousView.getUserData();
                
                if ("MONITORING".equals(tag)) {
                    stage.setTitle("Borrow Monitoring | VCBA Inventory Management");
                } else if ("DASHBOARD".equals(tag)) {
                    stage.setTitle("Dashboard | VCBA Inventory Management");
                }

                // Return to the previous screen
                stage.getScene().setRoot(previousView);
            }
        });

        return mainLayout;
    }

    //load ComboBox Item Selection
    private void loadItemNames(ComboBox<String> combo) {
        try (Connection conn = ConnectDB.getConnection();
             ResultSet rs = conn.createStatement().executeQuery("SELECT item_name FROM items")) {
            
            while (rs.next()) combo.getItems().add(rs.getString("item_name"));
        } 
        catch (SQLException e) { e.printStackTrace(); }
    }

    //process release stock
    private void processStockOut(String itemName, int removedQty, String department) {
        String queryFetch = "SELECT id, quantity FROM items WHERE item_name = ?";
        String queryUpdate = "UPDATE items SET quantity = ? WHERE id = ?";
        String queryHistory = "INSERT INTO stock_out_history (item_id, item_name, quantity_before, quantity_removed, quantity_total, department) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectDB.getConnection()) {
            conn.setAutoCommit(false);
            int itemId = 0, currentQty = 0;

            try (PreparedStatement ps = conn.prepareStatement(queryFetch)) {
                ps.setString(1, itemName);
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    itemId = rs.getInt("id");
                    currentQty = rs.getInt("quantity");
                }
            }

            // Formula Logic: Quantity_total = Quantity_current - Quantity_removed
            if (currentQty < removedQty) {
                showAlert(Alert.AlertType.WARNING, "Insufficient Stock", "Only " + currentQty + " available.");
                return;
            }

            int totalQty = currentQty - removedQty;

            try (PreparedStatement ps = conn.prepareStatement(queryUpdate)) {
                ps.setInt(1, totalQty);
                ps.setInt(2, itemId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(queryHistory)) {
                ps.setInt(1, itemId);
                ps.setString(2, itemName);
                ps.setInt(3, currentQty);
                ps.setInt(4, removedQty);
                ps.setInt(5, totalQty);
                ps.setString(6, department);
                ps.executeUpdate();
            }

            conn.commit();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Items released successfully!\nBalance: " + totalQty);
        } 
        catch (SQLException e) { e.printStackTrace(); }
    }

    //alert prompt
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}