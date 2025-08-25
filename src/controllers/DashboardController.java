package controllers;

import app.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import model.Vehicle;

public class DashboardController {

    private MainApp mainApp;

    @FXML
    private Label lblVehiclesCount;

    @FXML
    private TableView<Vehicle> vehicleTable;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
