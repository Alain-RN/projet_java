package controllers;

import dao.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.User;
import app.MainApp;
import utils.Session;


public class LoginController {

    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label loginMessage;

    @FXML
    private Button goRegisterButton;

    @FXML
    private Button loginButton;

    @FXML
    private void login() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if(email.isEmpty() || password.isEmpty()) {
            loginMessage.setText("Veuillez remplir tous les champs !");
            return;
        }

        if(!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            loginButton.setText("Email invalide !");
            return;
        }

        try {
            User user = new UserDAO().authenticate(email, password);
            if (user != null) {
                Session.setUser(user);
                loginMessage.setText("");
                mainApp.showDashboard(Session.getUser());
            } else {
                loginMessage.setText("Email ou mot de passe incorrect !");
            }
        } catch (Exception e) {
            e.printStackTrace();
            loginMessage.setText("Impossible de se connecter à la base de données !");
        }
    }

    @FXML
    private void goRegister() {
        // Ouvrir la fenêtre d'inscription
        mainApp.showRegister();
    }
}
