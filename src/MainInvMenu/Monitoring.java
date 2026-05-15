package MainInvMenu;

// Layout & UI Components
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;
import javafx.stage.Stage;

// Data Access & Logic
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import DAO.*;
import DTO.*;
import config.ConnectDB;
import Notification.Notif;
import Login.LoginGUI;

public class Monitoring {

    private ComboBox<String> cbBorrower = new ComboBox<>();
    private ComboBox<Item> cbItems = new ComboBox<>();
    private TextField txtQty = new TextField();
    private DatePicker dpBorrowed = new DatePicker();
    private Label lblDeadlineDisplay = new Label("YYYY-MM-DD"); 
    private DatePicker dpReturned = new DatePicker();
    private TextField txtReceivedBy = new TextField();
    private Label lblStatusDisplay = new Label("NONE");

    private TableView<Monitor> table = new TableView<>();
    private MonitorDAO monitorDAO;
    private ItemDAO itemDAO = new ItemDAO();
    private Notif notif;
    private GridPane formGrid = new GridPane();

    public Parent getView() {
        // Database connection initialization
        try {
            Connection conn = ConnectDB.getConnection();
            monitorDAO = new MonitorDAO(conn);
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }

        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("dashboard-main");

        // --- TOP NAVIGATION (Fixed Header) ---
        HBox topNav = new HBox(15);
        topNav.getStyleClass().add("top-nav");
        topNav.setAlignment(Pos.CENTER);
        topNav.setPrefHeight(65);

        ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/assets/logo.jpg")));
        logo.setFitHeight(50);
        logo.setPreserveRatio(true);

        Label lblTitle = new Label("BORROW MONITORING");
        lblTitle.getStyleClass().add("dashboard-title");

        notif = new Notif(monitorDAO, table);
        
        Region spacerL = new Region();
        Region spacerR = new Region();
        HBox.setHgrow(spacerL, Priority.ALWAYS);
        HBox.setHgrow(spacerR, Priority.ALWAYS);

        topNav.getChildren().addAll(logo, spacerL, lblTitle, spacerR, notif.getNotificationIcon());
        mainLayout.setTop(topNav);

        // --- PERMANENT LEFT SIDEBAR (20% Layout) ---
        VBox sidebar = new VBox(); 
        sidebar.getStyleClass().add("drawer"); 
        sidebar.setPrefWidth(250); // Approximately 20% of standard desktop width
        sidebar.setPadding(new Insets(20, 10, 20, 10));

        Button btnDashboard = new Button("DASHBOARD");
        Button btnStockIn = new Button("STOCK IN");
        Button btnStockOut = new Button("STOCK OUT");
        Button btnReports = new Button("REPORTS");
        Button btnLogout = new Button("LOG OUT"); 

        setHoverIcon(btnDashboard, "/assets/manage.png", "/assets/managehover.png");
        setHoverIcon(btnStockIn, "/assets/stockin.png", "/assets/stockinhover.png");
        setHoverIcon(btnStockOut, "/assets/stockout.png", "/assets/stockouthover.png");
        setHoverIcon(btnReports, "/assets/reports.png", "/assets/reportshover.png");
        setHoverIcon(btnLogout, "/assets/logout.png", "/assets/logouthover.png");

        Button[] navButtons = {btnDashboard, btnStockIn, btnStockOut, btnReports};
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

        // --- SIDEBAR NAVIGATION ACTIONS ---
        btnDashboard.setOnAction(e -> {
            Stage stage = (Stage) mainLayout.getScene().getWindow();
            stage.getScene().setRoot(new Dashboard().getView());
        });
        btnStockIn.setOnAction(e -> {
            Stage stage = (Stage) mainLayout.getScene().getWindow();
            stage.getScene().setRoot(new StockIn(new Dashboard(), mainLayout).getView());
        });
        btnStockOut.setOnAction(e -> {
            Stage stage = (Stage) mainLayout.getScene().getWindow();
            stage.getScene().setRoot(new StockOut(new Dashboard(), mainLayout).getView());
        });
        btnReports.setOnAction(e -> {
            Stage stage = (Stage) mainLayout.getScene().getWindow();
            stage.getScene().setRoot(new Reports(mainLayout).getView());
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

        // --- CENTER CONTENT (Transaction Details & Table - 80% Layout) ---
        VBox centerContent = new VBox(20);
        centerContent.setPadding(new Insets(20));
        centerContent.setAlignment(Pos.TOP_CENTER);

        // Transaction Details Box
        VBox formLayout = new VBox(15);
        formLayout.setAlignment(Pos.CENTER);
        formLayout.getStyleClass().add("table-container"); 
        
        Label headLabel = new Label("Transaction Details");
        headLabel.getStyleClass().add("form-header");

        setupFormGrid();
        
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        Button btnAdd = new Button("Log Borrow");
        Button btnReturnItem = new Button("Process Return"); 
        Button btnDelete = new Button("Delete Data");

        configureActionButtons(btnAdd, btnReturnItem, btnDelete);
        buttonBox.getChildren().addAll(btnAdd, btnReturnItem, btnDelete);

        formLayout.getChildren().addAll(headLabel, formGrid, buttonBox);

        // Table initialization
        setupTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        centerContent.getChildren().addAll(formLayout, table);
        mainLayout.setCenter(centerContent);

        // Functional Logic Initializers
        setupItemComboBox();
        loadItemCombo();
        refreshTable();
        applyFormStyles();

        // Listeners
        dpBorrowed.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) lblDeadlineDisplay.setText(newDate.plusDays(7).toString());
        });
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> populateFields(newVal));

        // Button Actions
        btnAdd.setOnAction(e -> handleAdd());
        btnReturnItem.setOnAction(e -> handleReturnProcess());
        btnDelete.setOnAction(e -> handleDelete());

        return mainLayout;
    }

    private void setupFormGrid() {
        formGrid.setHgap(40); formGrid.setVgap(15);
        formGrid.setAlignment(Pos.CENTER);
        
        cbBorrower.setItems(FXCollections.observableArrayList("CATS Dept", "Yellow Bumblebee Dept", "White Falcon Dept", "Black Panther Dept", "School Admin"));

        Label lblBorrower = new Label("Borrower:");
        Label lblQty = new Label("Quantity:");
        Label lblDeadline = new Label("Deadline:");
        Label lblReceived = new Label("Received By:");
        Label lblItemName = new Label("Item Name:");
        Label lblBorrowedDate = new Label("Borrowed Date:");
        Label lblReturnedDate = new Label("Returned Date:");
        Label lblStatus = new Label("Current Status:");

        Label[] allLabels = {lblBorrower, lblQty, lblDeadline, lblReceived, lblItemName, lblBorrowedDate, lblReturnedDate, lblStatus};
        for (Label l : allLabels) {
            l.getStyleClass().add("monitoring-label");
        }

        formGrid.add(lblBorrower, 0, 0);      formGrid.add(cbBorrower, 1, 0);
        formGrid.add(lblQty, 0, 1);           formGrid.add(txtQty, 1, 1);
        formGrid.add(lblDeadline, 0, 2);      formGrid.add(lblDeadlineDisplay, 1, 2);
        formGrid.add(lblReceived, 0, 3);      formGrid.add(txtReceivedBy, 1, 3);

        formGrid.add(lblItemName, 2, 0);      formGrid.add(cbItems, 3, 0);
        formGrid.add(lblBorrowedDate, 2, 1);  formGrid.add(dpBorrowed, 3, 1);
        formGrid.add(lblReturnedDate, 2, 2);  formGrid.add(dpReturned, 3, 2);
        formGrid.add(lblStatus, 2, 3);        formGrid.add(lblStatusDisplay, 3, 3);
    }

        private void configureActionButtons(Button... btns) {
            String[] icons = {"/assets/add-list.png", "/assets/edit.png", "/assets/returns.png", "/assets/trash.png"};
            for (int i = 0; i < btns.length; i++) {
                btns[i].setGraphic(createIcon(icons[i]));
                btns[i].getStyleClass().add("form-button");
                btns[i].setGraphicTextGap(10);
            }
        }

    // --- EXISTING LOGIC METHODS (Unchanged but retained for functionality) ---
    private void setupTable() {
        TableColumn<Monitor, String> colBorrower = new TableColumn<>("Borrower");
        colBorrower.setCellValueFactory(new PropertyValueFactory<>("borrowerName"));
        TableColumn<Monitor, String> colItem = new TableColumn<>("Item");
        colItem.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        TableColumn<Monitor, Integer> colQty = new TableColumn<>("Quantity");
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        TableColumn<Monitor, Date> colBorrowed = new TableColumn<>("Borrowed");
        colBorrowed.setCellValueFactory(new PropertyValueFactory<>("dateBorrowed"));
        TableColumn<Monitor, Date> colDeadline = new TableColumn<>("Deadline");
        colDeadline.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        TableColumn<Monitor, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().setAll(colBorrower, colItem, colQty, colBorrowed, colDeadline, colStatus);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void handleAdd() {
        try {
            Item selectedItem = cbItems.getValue();
            String borrower = cbBorrower.getValue();
            String qtyStr = txtQty.getText().trim();
            LocalDate borrowedDate = dpBorrowed.getValue();

            if (borrower == null || selectedItem == null || qtyStr.isEmpty() || borrowedDate == null) {
                new Alert(Alert.AlertType.WARNING, "Please fill in all details.").show();
                return;
            }

            int qty = Integer.parseInt(qtyStr);
            if (qty <= selectedItem.getQuantity()) {
                LocalDate deadline = borrowedDate.plusDays(7);
                Monitor m = new Monitor(borrower, selectedItem.getId(), qty, Date.valueOf(borrowedDate), Date.valueOf(deadline), txtReceivedBy.getText());
                if (monitorDAO.logBorrow(m)) {
                    itemDAO.updateStock(selectedItem.getId(), -qty);
                    refreshTable();
                    loadItemCombo();
                    clearFields();
                    notif.refreshNotifications();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Insufficient stock!").show();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    //process borrow returns
    private void handleReturnProcess() {
        Monitor selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) return;

        if (dpReturned.getValue() == null || txtReceivedBy.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Returned Date and Receiver Name are required.").show();
            return;
        }

        try {
            selected.setDateReturned(Date.valueOf(dpReturned.getValue()));
            selected.setReceivedBy(txtReceivedBy.getText());
                
            if (monitorDAO.processReturn(selected)) {
               itemDAO.updateStock(selected.getItemId(), selected.getQuantity());
               refreshTable();
               loadItemCombo();
               clearFields();
               notif.refreshNotifications();
            }
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    private void handleDelete() {
        Monitor selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try { 
                monitorDAO.deleteRecord(selected.getBorrowId()); 
                refreshTable(); 
                clearFields();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void applyFormStyles() {
        cbBorrower.getStyleClass().add("monitoring-input");
        cbItems.getStyleClass().add("monitoring-input");
        txtQty.getStyleClass().add("monitoring-input");
        dpBorrowed.getStyleClass().add("monitoring-input");
        dpReturned.getStyleClass().add("monitoring-input");
        txtReceivedBy.getStyleClass().add("monitoring-input");
        lblDeadlineDisplay.getStyleClass().add("monitoring-display");
        lblStatusDisplay.getStyleClass().add("monitoring-display");
    }

    private void populateFields(Monitor m) {
        if (m != null) {
            txtQty.setText(String.valueOf(m.getQuantity()));
            dpBorrowed.setValue(m.getDateBorrowed().toLocalDate());
            lblDeadlineDisplay.setText(m.getDeadline().toString());
            lblStatusDisplay.setText(m.getStatus());
            txtReceivedBy.setText(m.getReceivedBy() != null ? m.getReceivedBy() : "");
            cbItems.setDisable(false);
        }
    }

    private void clearFields() {
        cbBorrower.getSelectionModel().clearSelection(); txtQty.clear(); txtReceivedBy.clear();
        dpBorrowed.setValue(null); dpReturned.setValue(null);
        lblDeadlineDisplay.setText("YYYY-MM-DD"); lblStatusDisplay.setText("NONE");
        cbItems.setDisable(false);
    }

    private void setupItemComboBox() {
        cbItems.setConverter(new StringConverter<Item>() {
            @Override public String toString(Item item) { return (item == null) ? "" : item.getItemName(); }
            @Override public Item fromString(String string) { return null; }
        });
    }

    private void loadItemCombo() { cbItems.setItems(FXCollections.observableArrayList(itemDAO.getAllItems())); }

    private void refreshTable() {
        try { table.setItems(FXCollections.observableArrayList(monitorDAO.getAllRecords())); } 
        catch (SQLException e) { e.printStackTrace(); }
    }

    private ImageView createIcon(String path) {
        Image img = new Image(getClass().getResourceAsStream(path));
        ImageView view = new ImageView(img);
        view.setFitHeight(18); view.setFitWidth(18); view.setPreserveRatio(true);
        return view;
    }

    private void setHoverIcon(Button button, String normalPath, String hoverPath) {
        Image normalImg = new Image(getClass().getResourceAsStream(normalPath));
        Image hoverImg = new Image(getClass().getResourceAsStream(hoverPath));
        ImageView imageView = new ImageView(normalImg);
        imageView.setFitHeight(50); imageView.setFitWidth(50); imageView.setPreserveRatio(true);
        button.setGraphic(imageView);
        button.hoverProperty().addListener((obs, old, hovered) -> imageView.setImage(hovered ? hoverImg : normalImg));
    }

    private Region createFlexSpacer(double height) {
        Region spacer = new Region();
        spacer.setMinHeight(height);
        return spacer;
    }
}