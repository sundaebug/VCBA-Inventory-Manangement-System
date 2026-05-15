package MainInvMenu;

//javafx sdk
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

//data access
import RepButtons.*;

public class Reports {

    private VBox contentDisplay = new VBox(15); 
    private Label lblDynamicTitle = new Label("SELECT A REPORT");
    private Parent previousView;

    //constructor for back button to return from previous opened window
    public Reports(Parent previous) { 
        this.previousView = previous; 
    }

    public Parent getView() {
        HBox root = new HBox();
        root.setPrefSize(1200, 700);
        root.getStyleClass().add("reports-main");
        
        //styling path
        root.getStylesheets().add(getClass().getResource("/Login/InventoryStyleSheet.css").toExternalForm());

        //LEFT MENU
        VBox sideMenu = new VBox();
        sideMenu.setMinWidth(300);
        sideMenu.setPrefWidth(300);
        sideMenu.getStyleClass().add("side-menu");

        Label lblTitle = new Label("INVENTORY\nREPORTS");
        lblTitle.getStyleClass().add("sidebar-title");

        //spacers for equal alignment
        Region s1 = new Region(); VBox.setVgrow(s1, Priority.ALWAYS);
        Region s2 = new Region(); VBox.setVgrow(s2, Priority.ALWAYS);
        Region s3 = new Region(); VBox.setVgrow(s3, Priority.ALWAYS);
        Region s4 = new Region(); VBox.setVgrow(s4, Priority.ALWAYS);
        Region s5 = new Region(); VBox.setVgrow(s5, Priority.ALWAYS);

        Button btnAudit = new Button("Audit Log");
        Button btnLowStock = new Button("Low Stock");
        Button btnHistory = new Button("Stock In/Out History");
        Button btnBorrowHistory = new Button("Borrow History");
        Button btnBack = new Button("Back");

        Button[] navButtons = {btnAudit, btnLowStock, btnHistory, btnBorrowHistory, btnBack};
        for (Button btn : navButtons) { btn.getStyleClass().add("report-btn"); }

        sideMenu.getChildren().addAll(lblTitle, s1, btnAudit, s2, btnLowStock, 
                                        s3, btnHistory, s4, btnBorrowHistory, s5, btnBack);

        //RIGHT CONTENT AREA
        contentDisplay.getStyleClass().add("content-area");
        HBox.setHgrow(contentDisplay, Priority.ALWAYS);

        lblDynamicTitle.getStyleClass().add("table-title");
        contentDisplay.getChildren().add(lblDynamicTitle);

        //BUTTON ACTIONS
        btnAudit.setOnAction(e -> updateDisplay("Audit Log", new AuditLog().getTable()));
        btnLowStock.setOnAction(e -> updateDisplay("Low Stock", new LowStockRep().getTable()));
        btnHistory.setOnAction(e -> updateDisplayManual("STOCK IN/OUT HISTORY", new StockInOutHistory().getFullReportView()));
        btnBorrowHistory.setOnAction(e -> updateDisplay("Borrow History", new BorrowHistory().getTable()));

        //back button LOGIC
        btnBack.setOnAction(e -> {
            if (previousView != null) {
                Stage stage = (Stage) btnBack.getScene().getWindow();

                //object tag
                Object tag = previousView.getUserData();

                if ("MONITORING".equals(tag)) {
                    stage.setTitle("Borrow Monitoring | VCBA Inventory Management");
                }
                else if ("DASHBOARD".equals(tag)) {
                    stage.setTitle("Dashboard | VCBA Inventory Management");
                }

                // Return to the previous screen
                stage.getScene().setRoot(previousView);
        }
        });

        root.getChildren().addAll(sideMenu, contentDisplay);
        return root;
    }

    //display table update
    private void updateDisplay(String title, TableView<?> table) {
    contentDisplay.getChildren().clear();
    lblDynamicTitle.setText(title.toUpperCase());
    contentDisplay.getChildren().add(lblDynamicTitle);

    if (table != null) {
        //outer box container
        VBox tableBox = new VBox(table);
        tableBox.getStyleClass().add("table-container"); 

        VBox.setVgrow(table, Priority.ALWAYS);

        VBox.setVgrow(tableBox, Priority.ALWAYS);
        
        contentDisplay.getChildren().add(tableBox);
    }
}
    
    private void updateDisplayManual(String title, VBox customView) {
        contentDisplay.getChildren().clear();
        lblDynamicTitle.setText(title.toUpperCase());
        contentDisplay.getChildren().addAll(lblDynamicTitle, customView);
        VBox.setVgrow(customView, Priority.ALWAYS);
    }
}