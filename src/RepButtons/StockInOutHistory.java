package RepButtons;


//javafx sdk
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.layout.Priority;

public class StockInOutHistory extends AuditLog {

    public VBox getFullReportView() {
        
        //Stock In History
        Label lblIn = new Label("STOCK IN RECORDS");
        lblIn.getStyleClass().add("report-section-label");
        
        TableView<ObservableList<String>> tableIn = new TableView<>();
        tableIn.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableIn.getStyleClass().add("history-table");
        
        String queryIn = "SELECT transaction_date AS 'TRANSACTION DATE', item_name AS 'ITEM NAME', " +
                         "quantity_added AS 'QUANTITY ADDED', received_by AS 'RECEIVED BY' " +
                         "FROM stock_in_history ORDER BY transaction_date DESC";
        loadData(tableIn, queryIn);

        //wrap stock in table in a Box
        VBox boxIn = new VBox(tableIn);
        boxIn.getStyleClass().add("table-box-container");
        VBox.setVgrow(tableIn, Priority.ALWAYS);

        //Stock Out History
        Label lblOut = new Label("STOCK OUT RECORDS");
        lblOut.getStyleClass().add("report-section-label");
        
        TableView<ObservableList<String>> tableOut = new TableView<>();
        tableOut.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableOut.getStyleClass().add("history-table");
        
        String queryOut = "SELECT transaction_date AS 'TRANSACTION DATE', item_name AS 'ITEM NAME', " +
                          "quantity_removed AS 'QUANTITY REMOVED', department AS 'DEPARTMENT' " +
                          "FROM stock_out_history ORDER BY transaction_date DESC";
        loadData(tableOut, queryOut);

        //wrap stockout table in a Box
        VBox boxOut = new VBox(tableOut);
        boxOut.getStyleClass().add("table-box-container");
        VBox.setVgrow(tableOut, Priority.ALWAYS);

        //Container Assembly
        VBox container = new VBox(15);
        container.setPadding(new Insets(10));
        container.getChildren().addAll(lblIn, boxIn, lblOut, boxOut);

        VBox.setVgrow(boxIn, Priority.ALWAYS);
        VBox.setVgrow(boxOut, Priority.ALWAYS);

        return container;
    }

    //table viewer
    public TableView<ObservableList<String>> getTable() {
        TableView<ObservableList<String>> tableIn = new TableView<>();
        tableIn.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableIn.getStyleClass().add("history-table");
        String queryIn = "SELECT transaction_date AS 'TRANSACTION DATE', item_name AS 'ITEM NAME', quantity_added AS 'QUANTITY ADDED' FROM stock_in_history ORDER BY transaction_date DESC";
        loadData(tableIn, queryIn);
        return tableIn;
    }
}