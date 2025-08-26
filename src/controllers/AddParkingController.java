package controllers;

import dao.ParkingDAO;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Parking;

public class AddParkingController {

    @FXML private TextField parkingNameField;
    @FXML private TextField parkingLocationField;
    @FXML private TextField parkingCapacityField;
    @FXML private Button cancelBtn;

    private DashboardController dashboardController;

    public void setDashboardController(DashboardController controller) {
        this.dashboardController = controller;
    }

    @FXML
    public void initialize() {
        cancelBtn.setOnAction(e -> closeWindow());
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }


    // Ajouter un nouveau parking
    public void addParking() {
        String pName = parkingNameField.getText().trim();
        String pLocation = parkingLocationField.getText().trim();
        String pCapacity = parkingCapacityField.getText().trim();

        parkingNameField.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), pName.isEmpty());

        parkingLocationField.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), pLocation.isEmpty());

        parkingCapacityField.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), pCapacity.isEmpty() || !isPositiveInteger(pCapacity) );

        if ( pName.isEmpty() || pLocation.isEmpty() || pCapacity.isEmpty() || !isPositiveInteger(pCapacity)) {
            return;
        }

        try {
            Parking parking = new Parking();

            parking.setName(pName);
            parking.setLocation(pLocation);
            parking.setCapacity(Integer.parseInt(pCapacity));

            new ParkingDAO().addParking(parking);
            dashboardController.reloadListParking();

            closeWindow();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean isPositiveInteger(String str) {
        if (str == null || str.isEmpty()) return false;

        try {
            int value = Integer.parseInt(str);
            return value >= 5; // strictement supérieur à 0
        } catch (NumberFormatException e) {
            return false; // ce n’est pas un nombre entier valide
        }
    }
}
