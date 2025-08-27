package controllers;

import app.MainApp;
import dao.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import models.User;
import utils.Session;

public class RegisterController {

    public TextField fullNameField;
    public TextField emailField;
    public PasswordField passwordField;
    public PasswordField confirmPasswordField;
    public Label registerMessage;

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

    public void register() {
        String email = emailField.getText().trim();
        String userName = fullNameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassw = confirmPasswordField.getText().trim();

        // 1️⃣ Vérification des champs vides
        if (email.isEmpty() || password.isEmpty() || userName.isEmpty() || confirmPassw.isEmpty()) {
            registerMessage.setText("Veuillez remplir tous les champs !");
            return;
        }

        // 2️⃣ Vérification correspondance mot de passe
        if (!password.equals(confirmPassw)) {
            registerMessage.setText("Les mots de passe ne correspondent pas !");
            return;
        }

        // Vérification email valide
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            registerMessage.setText("Email invalide !");
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();

            // Vérifier si l'email existe déjà
            if (userDAO.existsByEmail(email)) {
                registerMessage.setText("Un compte avec cet email existe déjà !");
                return; // On stoppe l'inscription
            }

            // Créer et enregistrer l'utilisateur
            User user = new User();
            user.setUsername(userName);
            user.setEmail(email);
            user.setPassword(password); // → idéalement hasher le mot de passe ici

            userDAO.addUser(user);

            // Sauvegarder la session
            Session.setUser(user);

            // Rediriger vers le dashboard
            mainApp.showDashboard(Session.getUser());

        } catch (Exception e) {
            e.printStackTrace();
            registerMessage.setText("Impossible de se connecter à la base de données !");
        }
    }

}
