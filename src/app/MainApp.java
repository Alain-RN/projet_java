package app;

import controllers.DashboardController;
import controllers.LoginController;
import controllers.RegisterController;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.User;

import java.util.Objects;

public class MainApp extends Application {

    private int vSlide = 350;

    private Stage primaryStage;
    private Parent loginRoot;
    private Parent registerRoot;
    private Parent dashboardRoot;

    private LoginController loginController;
    private RegisterController registerController;
    private DashboardController dashboardController;


    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;

        //  Précharger Login
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/view/loginView.fxml"));
        loginRoot = loginLoader.load();
        loginController = loginLoader.getController();
        loginController.setMainApp(this);

        //  Précharger Register
        FXMLLoader registerLoader = new FXMLLoader(getClass().getResource("/view/registerView.fxml"));
        registerRoot = registerLoader.load();
        registerController = registerLoader.getController();
        registerController.setMainApp(this);

        // Précharger Dashboard
        FXMLLoader dashboardLoader = new FXMLLoader(getClass().getResource("/view/dashboardView.fxml"));
        dashboardRoot = dashboardLoader.load();
        dashboardController = dashboardLoader.getController();
        dashboardController.setMainApp(this);


        //  Créer la scène initiale
        Scene scene = new Scene(loginRoot, 1280, 720);

        // Ajouter les styles CSS
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style/main.css")).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style/form.css")).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style/dashboard.css")).toExternalForm());

        // Définir taille minimale et maximale
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setMinWidth(1100);
        stage.setMinHeight(700);
        stage.setMaxWidth(screenBounds.getWidth());
        stage.setMaxHeight(screenBounds.getHeight());

        // Titre et affichage
        stage.setTitle("Parking");
        stage.setScene(scene);
        stage.show();
    }

    // Méthodes pour changer de vue
    public void showLogin() {
        setRootWithTransition(loginRoot, true);
    }

    public void showRegister() {
        setRootWithTransition(registerRoot, true);
    }

    public void showDashboard(User user) {
        dashboardController.setUser(user);
        dashboardController.loadUser();
        dashboardController.loadParkings("");
        dashboardController.loadCars("");
        primaryStage.getScene().setRoot(dashboardRoot);
    }


    private void setRootWithTransition(Parent newRoot, boolean leftToRight) {
        VBox oldForm = (VBox) ((AnchorPane) primaryStage.getScene().getRoot()).lookup("#formContainer");
        VBox newForm = (VBox) ((AnchorPane) newRoot).lookup("#formContainer");

        double width = primaryStage.getWidth();
        double direction = leftToRight ? 1 : -1;

        // Préparer le nouveau formulaire
        newForm.setOpacity(0.0);
        newForm.setTranslateX(direction * width);

        // Fade out du formulaire actuel
        FadeTransition fadeOut = new FadeTransition(Duration.millis(vSlide), oldForm);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        TranslateTransition slideOut = new TranslateTransition(Duration.millis(vSlide), oldForm);
        slideOut.setFromX(0);
        slideOut.setToX(-direction * width);
        slideOut.setInterpolator(Interpolator.EASE_BOTH);

        fadeOut.setOnFinished(event -> {
            // Changer le root
            primaryStage.getScene().setRoot(newRoot);

            // Slide + fade in du nouveau formulaire
            TranslateTransition slideIn = new TranslateTransition(Duration.millis(vSlide), newForm);
            slideIn.setFromX(direction * width);
            slideIn.setToX(0);
            slideIn.setInterpolator(Interpolator.EASE_BOTH);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(vSlide), newForm);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);

            slideIn.play();
            fadeIn.play();
        });

        slideOut.play();
        fadeOut.play();
    }




    public static void main(String[] args) {
        launch(args);
    }
}
