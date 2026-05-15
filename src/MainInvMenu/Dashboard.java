package MainInvMenu;

//Layout & UI Components
import javafx.scene.Parent;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.stage.Stage;

//Collections & Binding
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

//Date Handling
import java.sql.Date; 
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.util.StringConverter;

//Data Access
import DAO.ItemDAO;
import DTO.Item;
import Login.LoginGUI;
import java.util.List;


public class Dashboard {

    private TextField txtId = new TextField();
    private TextField txtName = new TextField();
    private TextField txtQty = new TextField();
    private TextField txtPrice = new TextField();
    private TextField txtSupplier = new TextField();
    private DatePicker datePicker = new DatePicker();
    private ComboBox<String> cbCategory = new ComboBox<>();
    private TableView<Item> table = new TableView<>();
    private ItemDAO itemDAO = new ItemDAO();

    public Parent getView() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("dashboard-main");

        // --- TOP NAVIGATION ---
        StackPane topNav = new StackPane();
        topNav.getStyleClass().add("top-nav");
        topNav.setPrefHeight(65); 

     
        ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/assets/logo.jpg")));
        logo.setFitHeight(50);
        logo.setPreserveRatio(true);

        HBox logoContainer = new HBox(logo);
        logoContainer.setAlignment(Pos.CENTER_LEFT);
        logoContainer.setPadding(new Insets(0, 0, 0, 15)); 
        logoContainer.setMouseTransparent(true); 

        Label lblTitle = new Label("INVENTORY MANAGEMENT DASHBOARD");
        lblTitle.getStyleClass().add("dashboard-title");

        StackPane.setAlignment(lblTitle, Pos.CENTER);
        topNav.getChildren().addAll(logoContainer, lblTitle);
        mainLayout.setTop(topNav);

        // --- PERMANENT LEFT SIDEBAR ---
        VBox sidebar = new VBox(); 
        sidebar.getStyleClass().add("drawer"); // Keeping class for styling consistency
        sidebar.setPrefWidth(250);
        sidebar.setPadding(new Insets(20, 10, 20, 10));

        Button btnStockIn = new Button("STOCK IN");
        Button btnStockOut = new Button("STOCK OUT");
        Button btnReports = new Button("REPORTS");
        Button btnMonitoring = new Button("MONITOR");
        Button btnLogout = new Button("LOG OUT"); 

        // Set Icons
        setHoverIcon(btnStockIn, "/assets/stockin.png", "/assets/stockinhover.png");
        setHoverIcon(btnStockOut, "/assets/stockout.png", "/assets/stockouthover.png");
        setHoverIcon(btnReports, "/assets/reports.png", "/assets/reportshover.png");
        setHoverIcon(btnMonitoring, "/assets/monitoring.png", "/assets/monitoringhover.png");
        setHoverIcon(btnLogout, "/assets/logout.png", "/assets/logouthover.png");

        Button[] navButtons = {btnStockIn, btnStockOut, btnReports, btnMonitoring};
        for (Button btn : navButtons) {
            btn.getStyleClass().add("nav-btn");
            btn.setMaxWidth(Double.MAX_VALUE);
            sidebar.getChildren().addAll(btn, createFlexSpacer(15));
        }

        Region megaSpacer = new Region();
        VBox.setVgrow(megaSpacer, Priority.ALWAYS);
        
        btnLogout.getStyleClass().add("nav-btn");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        sidebar.getChildren().addAll(megaSpacer, btnLogout);

        mainLayout.setLeft(sidebar);
        
        // --- NAVIGATION LOGIC ---
        btnStockIn.setOnAction(e -> {
            Stage stage = (Stage) mainLayout.getScene().getWindow();
            stage.getScene().setRoot(new StockIn(this, mainLayout).getView());
        });

        btnStockOut.setOnAction(e -> {
            Stage stage = (Stage) mainLayout.getScene().getWindow();
            stage.getScene().setRoot(new StockOut(this, mainLayout).getView());
        });

        btnReports.setOnAction(e -> {
            Stage stage = (Stage) mainLayout.getScene().getWindow();
            stage.getScene().setRoot(new Reports(mainLayout).getView());
        });

        btnMonitoring.setOnAction(e -> {
            Stage stage = (Stage) mainLayout.getScene().getWindow();
            stage.getScene().setRoot(new Monitoring().getView());
        });

        btnLogout.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to log out?");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    Stage stage = (Stage) mainLayout.getScene().getWindow();
                    stage.getScene().setRoot(new LoginGUI().getView());
                }
            });
        });

        // --- CENTER CONTENT (Table & Form) ---
        HBox splitContent = new HBox(20);
        splitContent.setPadding(new Insets(20));

        // LEFT TABLE
        VBox leftLayout = new VBox(10);
        leftLayout.getStyleClass().add("table-container");
        leftLayout.prefWidthProperty().bind(splitContent.widthProperty().multiply(0.80));

        // Table Columns
        TableColumn<Item, String> colName = new TableColumn<>("Item Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        
        TableColumn<Item, String> colCategory = new TableColumn<>("Category");
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        
        TableColumn<Item, Integer> colQty = new TableColumn<>("Quantity");  
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        
        TableColumn<Item, Date> colDate = new TableColumn<>("Date Added");
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateAdded"));

        table.getColumns().setAll(colName, colCategory, colQty, colDate);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS); 
        leftLayout.getChildren().add(table);

        // RIGHT FORM
        VBox rightformLayout = new VBox(25); 
        rightformLayout.getStyleClass().add("right-form-container");
        
        rightformLayout.prefWidthProperty().bind(splitContent.widthProperty().multiply(0.25));
        rightformLayout.setPadding(new Insets(20)); 

        GridPane formGrid = new GridPane();
        formGrid.setVgap(12); 
        formGrid.setHgap(10);

        cbCategory.setItems(FXCollections.observableArrayList("CBM", "Sports Equipment", "Auto Lab", "EIM"));

        
        Control[] formInputs = {txtName, cbCategory, txtQty, txtPrice, txtSupplier, datePicker};
        for (Control input : formInputs) {
            input.getStyleClass().add("form-input");
            
            input.setMaxWidth(Double.MAX_VALUE); 
            GridPane.setHgrow(input, Priority.ALWAYS);
        }

        
        ColumnConstraints column = new ColumnConstraints();
        column.setHgrow(Priority.ALWAYS);
        formGrid.getColumnConstraints().addAll(column);

        
        Label lblName = new Label("Item Name:");
        Label lblCategory = new Label("Category:");
        Label lblQty = new Label("Quantity:");
        Label lblPrice = new Label("Unit Price:");
        Label lblSupplier = new Label("Supplier:");
        Label lblDate = new Label("Date:");

        // Labels & Inputs mapping
        formGrid.add(lblName, 0, 0);     formGrid.add(txtName, 0, 1);
        formGrid.add(lblCategory, 0, 2);  formGrid.add(cbCategory, 0, 3);
        formGrid.add(lblQty, 0, 4);       formGrid.add(txtQty, 0, 5);
        formGrid.add(lblPrice, 0, 6);     formGrid.add(txtPrice, 0, 7);
        formGrid.add(lblSupplier, 0, 8);  formGrid.add(txtSupplier, 0, 9);
        formGrid.add(lblDate, 0, 10);     formGrid.add(datePicker, 0, 11);

        // CRUD Buttons
        Button btnAdd = new Button("Add");
        Button btnUpdate = new Button("Update");
        Button btnDelete = new Button("Delete");

        Button[] actionButtons = {btnAdd, btnUpdate, btnDelete};
        for (Button btn : actionButtons) {
            btn.getStyleClass().add("form-button");
            btn.setMaxWidth(Double.MAX_VALUE); 
            VBox.setVgrow(btn, Priority.ALWAYS);
        }

        btnAdd.setGraphic(createIcon("/assets/add-list.png"));
        btnUpdate.setGraphic(createIcon("/assets/edit.png"));
        btnDelete.setGraphic(createIcon("/assets/trash.png"));

        VBox formButtons = new VBox(10, btnAdd, btnUpdate, btnDelete);
        formButtons.setAlignment(Pos.CENTER);
  
        VBox.setMargin(formButtons, new Insets(15, 0, 0, 0));

        // Add containers to layout
        rightformLayout.getChildren().addAll(formGrid, formButtons);
        splitContent.getChildren().addAll(leftLayout, rightformLayout);
        mainLayout.setCenter(splitContent);
        
        //FORM BUTTON LOGIC
        
        btnAdd.setOnAction(e -> {
            try {
                // Create new Item from form inputs
                Item newItem = new Item(
                    0, // ID is auto-incremented in DB
                    txtName.getText(),
                    cbCategory.getValue(),
                    Integer.parseInt(txtQty.getText()),
                    Double.parseDouble(txtPrice.getText()),
                    txtSupplier.getText(),
                    Date.valueOf(datePicker.getValue())
                );

                itemDAO.AddItem(newItem);
                refreshTable();
                clearFields();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Item added successfully!");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please ensure all fields are filled correctly.");
            }
        });

        // UPDATE BUTTON LOGIC
        btnUpdate.setOnAction(e -> {
            Item selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    selected.setItemName(txtName.getText());
                    selected.setCategory(cbCategory.getValue());
                    selected.setQuantity(Integer.parseInt(txtQty.getText()));
                    selected.setUnitPrice(Double.parseDouble(txtPrice.getText()));
                    selected.setSupplier(txtSupplier.getText());
                    selected.setDateAdded(Date.valueOf(datePicker.getValue()));

                    itemDAO.updateItem(selected);
                    refreshTable();
                    clearFields();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Item updated successfully!");
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid input. Please check your data.");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select an item from the table to update.");
            }
        });

        // DELETE BUTTON LOGIC
        btnDelete.setOnAction(e -> {
            Item selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this item?");
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        itemDAO.deleteItem(selected.getId());
                        refreshTable();
                        clearFields();
                    }
                });
            } else {
                showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select an item from the table to delete.");
            }
        });
        
        // --- TABLE LOGIC ---
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtId.setText(String.valueOf(newVal.getId()));
                txtName.setText(newVal.getItemName());
                cbCategory.setValue(newVal.getCategory());
                txtQty.setText(String.valueOf(newVal.getQuantity()));
                txtPrice.setText(String.valueOf(newVal.getUnitPrice()));
                txtSupplier.setText(newVal.getSupplier());
                datePicker.setValue(newVal.getDateAdded().toLocalDate());
            }
        });

        refreshTable();
        return mainLayout;
    }
    
    private ImageView createIcon(String path) {
        ImageView view = new ImageView(new Image(getClass().getResourceAsStream(path)));
        view.setFitHeight(18); view.setFitWidth(18); view.setPreserveRatio(true);
        return view;
    }

    private void setHoverIcon(Button button, String normalPath, String hoverPath) {
        Image normalImg = new Image(getClass().getResourceAsStream(normalPath));
        Image hoverImg = new Image(getClass().getResourceAsStream(hoverPath));
        ImageView imageView = new ImageView(normalImg);
        imageView.setFitHeight(50); // Adjusted size for sidebar
        imageView.setFitWidth(50);
        imageView.setPreserveRatio(true);
        button.setGraphic(imageView);
        button.hoverProperty().addListener((obs, old, hovered) -> imageView.setImage(hovered ? hoverImg : normalImg));
    }

    private Region createFlexSpacer(double height) {
        Region spacer = new Region();
        spacer.setMinHeight(height);
        return spacer;
    }

    void refreshTable() {
    List<Item> items = itemDAO.getAllItems();
    
    ObservableList<Item> observableList = FXCollections.observableArrayList(items);
    table.setItems(observableList);
    
    // Force the table to refresh its visual state
    table.refresh();
    }

    private void clearFields() {
        txtId.clear(); txtName.clear(); txtQty.clear(); txtPrice.clear(); 
        txtSupplier.clear(); datePicker.setValue(null); cbCategory.getSelectionModel().clearSelection();
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}
}