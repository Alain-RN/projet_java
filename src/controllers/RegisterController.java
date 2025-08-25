package controllers;

import app.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class RegisterController {

    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private Button goLoginButton;

    @FXML
    public void initialize() {
        goLoginButton.setOnAction(e -> mainApp.showLogin());
    }
}
