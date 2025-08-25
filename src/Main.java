import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/registerView.fxml"));
        Scene scene = new Scene(loader.load(), 1280, 720);

        Rectangle2D screenBounbds = Screen.getPrimary().getVisualBounds();

        stage.setMinWidth(800);
        stage.setMinHeight(600);

        stage.setMinWidth(screenBounbds.getWidth());
        stage.setMinWidth(screenBounbds.getHeight());

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style/main.css")).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style/form.css")).toExternalForm());

        stage.setTitle("Parcking");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
