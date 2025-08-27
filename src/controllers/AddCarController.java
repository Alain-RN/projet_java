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
        String duration = parkingTimeField.getText().trim();
        String ownerEmail;

        // Vérifie si l'utilisateur est admin
        boolean admin = isAdmin(dashboardController.getUser());

        if (admin) {
            ownerEmail = carOwnerField.getText().trim();
            carOwnerField.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), ownerEmail.isEmpty());
        } else {
            ownerEmail = dashboardController.getUser().getEmail();
        }

        // Validation des champs
        carPlateField.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), carPlate.isEmpty());
        parkingTimeField.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), duration.isEmpty() || !isPositiveInteger(duration));

        if (carPlate.isEmpty() || ownerEmail.isEmpty() || duration.isEmpty() || !isPositiveInteger(duration)) {
            System.out.println("Tsy mety");
            return;
        }


        try {
            Car car = new Car();
            car.setPlate(carPlate);
            car.setDuration(Integer.parseInt(duration));
            car.setOwnerEmail(ownerEmail);
            car.setParkingId(parkingId);

            new CarDAO().addCar(car);
            dashboardController.loadCars();
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
