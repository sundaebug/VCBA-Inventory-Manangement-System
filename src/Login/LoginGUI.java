package Login;

//javafx sdk
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.image.Image;

//data access
import MainInvMenu.Dashboard; 

public class LoginGUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        
        //window dimensions
        Scene scene = new Scene(getView(), 1366, 768); 
            //css styling path
            scene.getStylesheets().add(getClass().getResource("InventoryStyleSheet.css").toExternalForm());
        
        //header title & logo
        primaryStage.setTitle("Welcome! | VCBA Inventory Management Log in");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("logo.jpg")));
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(600);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    
    public Parent getView() {
    
       //main container
        HBox root = new HBox();
            root.setAlignment(Pos.CENTER_RIGHT); 
        
        //spacer (left)
        Region leftSpacer = new Region();
            HBox.setHgrow(leftSpacer, Priority.ALWAYS);
            leftSpacer.minWidthProperty().bind(root.widthProperty().multiply(0.5));

       //grid
        GridPane grid = new GridPane();
            grid.getStyleClass().add("login-form");

        ColumnConstraints labelColumn = new ColumnConstraints();
            labelColumn.setMinWidth(Region.USE_PREF_SIZE); 
            labelColumn.setHgrow(Priority.NEVER);          

        ColumnConstraints fieldColumn = new ColumnConstraints();
            fieldColumn.setHgrow(Priority.ALWAYS);      

        grid.getColumnConstraints().addAll(labelColumn, fieldColumn);
        grid.setAlignment(Pos.CENTER);
        grid.setMaxWidth(Region.USE_PREF_SIZE); 
        grid.setVgap(15);
        grid.setHgap(10);

        HBox.setMargin(grid, new Insets(0, 50, 0, 0));

        //UI COMPONENTS 
        Label lblTableTitle = new Label("INVENTORY MANAGEMENT SYSTEM");
            lblTableTitle.getStyleClass().add("login-title");
            grid.add(lblTableTitle, 0, 0, 2, 1);
            GridPane.setHalignment(lblTableTitle, HPos.CENTER);

        Label lblsubTitle = new Label("Villamor College of Business and Arts Inc.");
            lblsubTitle.getStyleClass().add("login-subtitle");
            grid.add(lblsubTitle, 0, 1, 2, 1);
            GridPane.setHalignment(lblsubTitle, HPos.CENTER);
            GridPane.setMargin(lblsubTitle, new Insets(0, 0, 30, 0));
        
        Label lblUser = new Label("Username:");
            lblUser.getStyleClass().add("login-label");
            grid.add(lblUser, 0, 2);

        TextField txtUsername = new TextField();
            txtUsername.setPromptText("Username");
            txtUsername.getStyleClass().add("login-input");
            grid.add(txtUsername, 1, 2);
        
        Label lblPass = new Label("Password:");
            lblPass.getStyleClass().add("login-label");
            grid.add(lblPass, 0, 3);

        PasswordField txtPassword = new PasswordField();
            txtPassword.setPromptText("Password");
            txtPassword.getStyleClass().add("login-input");
            grid.add(txtPassword, 1, 3);
        
        //button
        Button btnLogin = new Button("Login");
            btnLogin.setDefaultButton(true);
            btnLogin.getStyleClass().add("login-button");
        
        Button btnExit = new Button("Exit");
            btnExit.getStyleClass().add("exit-button");

        HBox hbBtn = new HBox(15);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT); 
        hbBtn.getChildren().addAll(btnLogin, btnExit);
        grid.add(hbBtn, 0, 5, 2, 1);
        
        // BUTTON LOGIC
        btnLogin.setOnAction(e -> {
            String user = txtUsername.getText();
            String pass = txtPassword.getText();

            if (validateLogIn.isLogIn(user, pass)) {
     
                Stage stage = (Stage) root.getScene().getWindow();
                stage.setTitle("Dashboard | VCBA Inventory Management");

                    Dashboard dash = new Dashboard();
                    root.getScene().setRoot(dash.getView());
                
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Failed");
                alert.setHeaderText(null);
                alert.setContentText("Invalid Username or Password!");
                alert.show();
            }
        });

        btnExit.setOnAction(e -> System.exit(0));
        
        root.getChildren().addAll(leftSpacer, grid);
        return root;
    }

    //main (execution starts here)
    public static void main(String[] args) {
        launch(args);
    }
}