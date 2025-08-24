package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    @FXML
    private void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Veuillez remplir tous les champs !");
        } else if (username.equals("admin") && password.equals("1234")) {
            statusLabel.setText("Connexion réussie !");
        } else {
            statusLabel.setText("Nom d'utilisateur ou mot de passe incorrect !");
        }
    }
}
