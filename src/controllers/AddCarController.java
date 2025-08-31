package controllers;

import dao.CarDAO;
import dao.ParkingDAO;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Car;
import models.Parking;
import models.User;

public class AddCarController {

    @FXML private TextField carOwnerField;
    @FXML private TextField carPlateField;
    @FXML private TextField parkingTimeField;

    public Label titleAddCar;
    private int timeMinutes = 60;
    public Button cancelBtn;
    private int parkingId;
    private User user;
    private DashboardController dashboardController;

    private void closeWindow() {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void initialize() {
        cancelBtn.setOnAction(e -> closeWindow());
    }

    public void setDashboardController(DashboardController controller) {
        this.dashboardController = controller;
    }

    public void decrTime() {
        if (timeMinutes != 10) {
            timeMinutes --;
            parkingTimeField.setText(String.valueOf(timeMinutes));
        }
    }

    public void incrTime() {
        if (timeMinutes != 1440) {
            timeMinutes ++;
            parkingTimeField.setText(String.valueOf(timeMinutes));
        }
    }

    public void addCar() {
        String carPlate = carPlateField.getText().trim();
        String durationStr = parkingTimeField.getText().trim();
        String ownerEmail;

        boolean admin = isAdmin(dashboardController.getUser());

        // Déterminer le propriétaire
        if (admin) {
            ownerEmail = carOwnerField.getText().trim();
            carOwnerField.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), ownerEmail.isEmpty() || !ownerEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$") );
        } else {
            ownerEmail = dashboardController.getUser().getEmail();
        }

        // Validation des champs
        boolean invalidPlate = carPlate.isEmpty();
        boolean invalidDuration = durationStr.isEmpty() || !isPositiveInteger(durationStr);

        carPlateField.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), invalidPlate);
        parkingTimeField.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), invalidDuration);

        if ( invalidPlate || ownerEmail.isEmpty() || invalidDuration || !ownerEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$") ) {
            return;
        }

        try {
            CarDAO carDAO = new CarDAO();

            Car car = new Car();
            car.setPlate(carPlate);
            car.setDuration(Integer.parseInt(durationStr));
            car.setOwnerEmail(ownerEmail);
            car.setParkingId(parkingId);

            // Vérifie si la voiture peut être ajoutée
            boolean added = carDAO.addCarIfValid(car, dashboardController.getUser());

            if (!added) {
                carPlateField.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), true);
                return;
            }

            // Recharge la liste et ferme le popup
            dashboardController.loadCars("");
            closeWindow();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean isPositiveInteger(String str) {
        if (str == null || str.isEmpty()) return false;

        try {
            int value = Integer.parseInt(str);
            return value >= 5; // Supérieur ou egale a 5
        } catch (NumberFormatException e) {
            return false; // ce n’est pas un nombre entier valide
        }
    }

    private boolean isAdmin(User user) {
        return "admin".equals(user.getUsername())
                && "1234".equals(user.getPassword())
                && user.getId() == 1;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setParkingId(int parkingId) {
        this.parkingId = parkingId;
    }

    public void hideOwnerField(User user) {
        if (isAdmin(user)) {
            carOwnerField.setVisible(true);
            carOwnerField.setManaged(true);
        } else {
            carOwnerField.setVisible(false);
            carOwnerField.setManaged(false);
        }
    }

}
