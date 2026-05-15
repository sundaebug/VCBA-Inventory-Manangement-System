package MainInvMenu;

// data access
import config.ConnectDB;

// javafx sdk
import java.sql.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class StockIn {

    private Parent previousView;
    private Dashboard dashboardController; // Reference to refresh the table

    // Updated Constructor to accept Dashboard instance
    public StockIn(Dashboard dashboard, Parent previous) {
        this.dashboardController = dashboard;
        this.previousView = previous;
    }

    public Parent getView() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("stock-in-container");

        try {
            // style path
            String css = getClass().getResource("/Login/InventoryStyleSheet.css").toExternalForm();
            mainLayout.getStylesheets().add(css);
        } catch (NullPointerException e) {
            System.out.println("CSS File not found.");
        }

        // THE WRAPPER
        VBox formWrapper = new VBox(30);
        formWrapper.getStyleClass().add("form-wrapper");
        formWrapper.setAlignment(Pos.CENTER);
        formWrapper.setMaxWidth(600);
        formWrapper.setMaxHeight(Region.USE_PREF_SIZE);
        formWrapper.setPadding(new Insets(40));

        // HEADER
        Label lblHeader = new Label("STOCK IN TRANSACTION");
        lblHeader.getStyleClass().add("stock-in-title");

        // FORM LAYOUT
        GridPane grid = new GridPane();
        grid.setVgap(20);
        grid.setHgap(25);
        grid.setAlignment(Pos.CENTER);

        Label lblItem = new Label("Select Item:");
        Label lblQty = new Label("Quantity:");
        Label lblReceived = new Label("Received By:");

        lblItem.getStyleClass().add("stock-in-label");
        lblQty.getStyleClass().add("stock-in-label");
        lblReceived.getStyleClass().add("stock-in-label");

        ComboBox<String> comboItems = new ComboBox<>();
        loadItemNames(comboItems);

        comboItems.setPromptText("Select an item");
        comboItems.getStyleClass().add("stock-in-input");

        TextField txtQty = new TextField();
        txtQty.setPromptText("Enter quantity");
        txtQty.getStyleClass().add("stock-in-input");

        TextField txtReceivedBy = new TextField();
        txtReceivedBy.setPromptText("Enter receiver name");
        txtReceivedBy.getStyleClass().add("stock-in-input");

        // form placement
        grid.add(lblItem, 0, 0);
        grid.add(comboItems, 1, 0);

        grid.add(lblQty, 0, 1);
        grid.add(txtQty, 1, 1);

        grid.add(lblReceived, 0, 2);
        grid.add(txtReceivedBy, 1, 2);

        // BUTTONS
        Button btnSave = new Button("Add Stock");
        btnSave.getStyleClass().add("inoutbtn-save");

        Button btnBack = new Button("Back");
        btnBack.getStyleClass().add("inoutbtn-back");

        HBox.setHgrow(btnSave, Priority.ALWAYS);
        HBox.setHgrow(btnBack, Priority.ALWAYS);

        HBox hbBtn = new HBox(15, btnSave, btnBack);
        hbBtn.setAlignment(Pos.CENTER);
        hbBtn.setMaxWidth(400);

        formWrapper.getChildren().addAll(lblHeader, grid, hbBtn);
        mainLayout.setCenter(formWrapper);

        // BUTTON LOGIC
        btnSave.setOnAction(e -> {
            if (comboItems.getValue() == null || txtQty.getText().isEmpty()) {
                showAlert("Error", "Please fill in all fields.");
                return;
            }
            try {
                int addedValue = Integer.parseInt(txtQty.getText());
                processStockIn(comboItems.getValue(), addedValue, txtReceivedBy.getText());
                txtQty.clear();
                txtReceivedBy.clear();
            } catch (NumberFormatException ex) {
                showAlert("Error", "Quantity must be a number!");
            }
        });

        btnBack.setOnAction(e -> {
            if (previousView != null) {
                // REFRESH THE TABLE BEFORE SWITCHING BACK
                if (dashboardController != null) {
                    dashboardController.refreshTable();
                }

                Stage stage = (Stage) btnBack.getScene().getWindow();
                Object tag = previousView.getUserData();

                if ("DASHBOARD".equals(tag)) {
                    stage.setTitle("Dashboard | VCBA Inventory Management");
                } else if ("MONITORING".equals(tag)) {
                    stage.setTitle("Borrow Monitoring | VCBA Inventory Management");
                }

                stage.getScene().setRoot(previousView);
            }
        });

        return mainLayout;
    }

    // comboBox Item Selection
    private void loadItemNames(ComboBox<String> combo) {
        try (Connection conn = ConnectDB.getConnection();
             ResultSet rs = conn.createStatement().executeQuery("SELECT item_name FROM items")) {

            while (rs.next()) combo.getItems().add(rs.getString("item_name"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // add stocks into the table
    private void processStockIn(String itemName, int addedQty, String receiver) {
        String queryFetch = "SELECT id, quantity FROM items WHERE item_name = ?";
        String queryUpdate = "UPDATE items SET quantity = ? WHERE id = ?";
        String queryHistory = "INSERT INTO stock_in_history (item_id, item_name, quantity_before, quantity_added, quantity_total, received_by) VALUES (?, ?, ?, ?, ?, ?)";

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

            int totalQty = currentQty + addedQty;

            try (PreparedStatement ps = conn.prepareStatement(queryUpdate)) {
                ps.setInt(1, totalQty);
                ps.setInt(2, itemId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(queryHistory)) {
                ps.setInt(1, itemId);
                ps.setString(2, itemName);
                ps.setInt(3, currentQty);
                ps.setInt(4, addedQty);
                ps.setInt(5, totalQty);
                ps.setString(6, receiver);
                ps.executeUpdate();
            }

            conn.commit();
            showAlert("Success", "Stock updated! New Balance: " + totalQty);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // alert prompt
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}