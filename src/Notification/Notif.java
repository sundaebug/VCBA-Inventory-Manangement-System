package Notification;

//javafx sdk
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

//data access
import DTO.Monitor;
import DAO.MonitorDAO;

public class Notif {

    private StackPane container;
    private StackPane badgeLayer;
    private Label lblBadge;
    private Popup popup; 
    private VBox notificationContainer; 
    private MonitorDAO monitorDAO;
    private TableView<Monitor> targetTable;

    private boolean isRead = false;
    private int lastAlertCount = 0;

    //constructor
    public Notif(MonitorDAO monitorDAO, TableView<Monitor> targetTable) {
        this.monitorDAO = monitorDAO;
        this.targetTable = targetTable;
        initializeUI();
        refreshNotifications(); 
    }

    private void initializeUI() {
        
        //Bell Button
        Button btnBell = new Button("🔔");
        btnBell.getStyleClass().add("bell-button");

        //Notification Badge
        lblBadge = new Label("0");
        lblBadge.getStyleClass().add("notification-badge");

        badgeLayer = new StackPane(lblBadge);
        badgeLayer.setMouseTransparent(true);
        badgeLayer.getStyleClass().add("badge-layer");
        
        //Main Container & Alignment
        container = new StackPane(btnBell, badgeLayer);
        container.setAlignment(Pos.CENTER);
    
        StackPane.setAlignment(badgeLayer, Pos.TOP_RIGHT);
        StackPane.setMargin(badgeLayer, new Insets(0, 5, 0, 0));

        //scrollable popup
        popup = new Popup();
        popup.setAutoHide(true);
        notificationContainer = new VBox(5);
        notificationContainer.getStyleClass().add("notif-popup-container");
        notificationContainer.setPrefWidth(225); // Width used for centering math

        ScrollPane scrollPane = new ScrollPane(notificationContainer);
        scrollPane.setPrefHeight(250); 
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("notif-scroll-pane");

        popup.getContent().add(scrollPane);

        //bell action
        btnBell.setOnAction(e -> {
            if (popup.isShowing()) {
                popup.hide();
            } else {
                javafx.geometry.Bounds bounds = btnBell.localToScreen(btnBell.getBoundsInLocal());

                double bellCenter = (bounds.getMinX() + bounds.getMaxX()) / 2;
                double popupX = bellCenter - 175; // 
                double popupY = bounds.getMaxY() + 5;

                popup.show(btnBell, popupX, popupY);
                
                isRead = true;
                badgeLayer.setVisible(false);
            }
        });
    }

    //notif refresher
    public void refreshNotifications() {
        try {
            List<Monitor> allRecords = monitorDAO.getAllRecords();
            LocalDate today = LocalDate.now();

            List<Monitor> alerts = allRecords.stream()
                    .filter(m -> m.getDateReturned() == null)
                    .filter(m -> {
                        LocalDate deadline = m.getDeadline().toLocalDate();
                        
                        return deadline.isBefore(today) || deadline.isEqual(today) || deadline.isEqual(today.plusDays(1));
                    })
                    .collect(Collectors.toList());

            int currentCount = alerts.size();
                lblBadge.setText(String.valueOf(currentCount));

            if (currentCount > lastAlertCount) {
                isRead = false;
            }
            lastAlertCount = currentCount;
            badgeLayer.setVisible(currentCount > 0 && !isRead);

            notificationContainer.getChildren().clear();

            if (alerts.isEmpty()) {
                notificationContainer.getChildren().add(new Label("No urgent notifications."));
            } 
            else {
                
                for (Monitor m : alerts) {
                    LocalDate deadline = m.getDeadline().toLocalDate();
                    long daysUntil = ChronoUnit.DAYS.between(today, deadline);

                    String message;
                    String color = "#333333";

                    if (daysUntil < 0) {
                        message = "🚨 | URGENT!! OVERDUE\n" + m.getBorrowerName() + " - " + m.getItemName();
                        color = "#e74c3c";
                    } else if (daysUntil == 0) {
                        message = "⚠ | WARNING! Due Today\n" + m.getBorrowerName() + " - " + m.getItemName();
                        color = "#f39c12";
                    } else {
                        message = "⌛ | REMINDER: Due Tomorrow\n" + m.getBorrowerName() + " - " + m.getItemName();
                        color = "#2ecc71";
                    }

                    Button itemBtn = new Button(message);
                    itemBtn.setMaxWidth(Double.MAX_VALUE);
                    itemBtn.setAlignment(Pos.CENTER_LEFT);
                    itemBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #f4f4f4; -fx-border-width: 0 0 1 0; " +
                                   "-fx-text-fill: " + color + "; -fx-font-size: 12px; -fx-cursor: hand;");

                    //notif selected (active)
                    itemBtn.setOnAction(ev -> {
                        targetTable.getSelectionModel().select(m);
                        targetTable.scrollTo(m);
                        popup.hide();
                    });

                    notificationContainer.getChildren().add(itemBtn);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    //notif icon getter
    public StackPane getNotificationIcon() {
        return container;
    }
}