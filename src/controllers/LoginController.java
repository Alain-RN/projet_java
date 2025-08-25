package controllers;


import javafx.fxml.FXML;
import app.MainApp;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    public MainApp mainApp;
    public TextField emailField;
    public PasswordField passwordField;
    public Label loginMessage;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private Button goRegisterButton;

    @FXML
    private Button loginButton;

    @FXML
    public void initialize() {

        loginButton.setOnAction(e -> {
            if ("".equals(emailField.getText()) && "".equals(passwordField.getText())) {
                mainApp.showDashboard();
            } else {
                loginMessage.setText("Email ou mot de passe incorrect");
            }
        });

        goRegisterButton.setOnAction(e -> mainApp.showRegister());
    }
}

