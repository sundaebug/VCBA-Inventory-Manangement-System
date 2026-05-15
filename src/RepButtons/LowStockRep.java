package RepButtons;

//javafx sdk import accessory
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.collections.ObservableList;

public class LowStockRep extends AuditLog {

    /* This is the method Reports.java calls to display,
     the Low Stock data in the right-side content area.
     */
    
    @Override
    public TableView<ObservableList<String>> getTable() {
        TableView<ObservableList<String>> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-font-family: 'Times New Roman';");

        // sql query for table data retrieval
        String query = "SELECT item_name AS 'ITEM NAME', " +
                       "quantity AS 'QUANTITY', " +
                       "category AS 'CATEGORY', " +
                       "CASE " +
                       "  WHEN quantity <= 10 THEN 'CRITICALLY LOW' " +
                       "  WHEN quantity <= 25 THEN 'NEUTRALLY LOW' " +
                       "  ELSE 'LOW' " +
                       "END AS 'STATUS' " +
                       "FROM items WHERE quantity <= 50";

        //loadData method from AuditLog
        loadData(table, query);

        applyStatusColoring(table);

        return table;
    }

    //status coloring
    private void applyStatusColoring(TableView<ObservableList<String>> table) {
        if (table.getColumns().size() >= 4) {
            @SuppressWarnings("unchecked")
            TableColumn<ObservableList<String>, String> statusCol = 
                (TableColumn<ObservableList<String>, String>) table.getColumns().get(3);

            statusCol.setCellFactory(column -> new TableCell<ObservableList<String>, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        // Status styling logic
                        if (item.equals("CRITICALLY LOW")) {
                            setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; " +
                                     "-fx-alignment: CENTER; -fx-font-weight: bold;");
                        } 
                        else if (item.equals("NEUTRALLY LOW")) {
                            setStyle("-fx-background-color: #ffd633; -fx-text-fill: black; " +
                                     "-fx-alignment: CENTER; -fx-font-weight: bold;");
                        } 
                        else if (item.equals("LOW")) {
                            setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                                     "-fx-alignment: CENTER; -fx-font-weight: bold;");
                        } else {
                            setStyle("");
                        }
                    }
                }
            });
        }
    }
}