package controllers;

import app.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.Car;
import models.Parking;
import models.User;
import utils.Session;
import dao.ParkingDAO;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class DashboardController {

    public VBox buttonContainer;
    public Label capacity;
    public Label carNbr;
    private MainApp mainApp;
    private int parkingId;

    @FXML private Button addParking;
    @FXML private Label lblVehiclesCount;
    @FXML private Label userName;
    @FXML private Label userInitial;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void initialize() {
        loadParkings();
        capacity.setText(Integer.toString(parkingId));
        carNbr.setText(Integer.toString(parkingId));
    }

    public void reloadListParking() {
        loadParkings();
    }

    @FXML
    private void openAddParkingPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/addParkingPopup.fxml"));
            Parent root = loader.load();

            AddParkingController addParkingController = loader.getController();
            addParkingController.setDashboardController(this);

            Stage popupStage = new Stage();
            Scene scene = new Scene(root, 320, 400);

            popupStage.initStyle(StageStyle.UNDECORATED);
            popupStage.initStyle(StageStyle.TRANSPARENT);

            scene.setFill(Color.TRANSPARENT);

            popupStage.setScene(scene);
            popupStage.setResizable(false);
            popupStage.initModality(Modality.APPLICATION_MODAL);

            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openAddCarPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/addCarPopup.fxml"));
            Parent root = loader.load();

            AddCarController addCarController = loader.getController();

            Stage popupStage = new Stage();
            Scene scene = new Scene(root, 320, 400);

            popupStage.initStyle(StageStyle.UNDECORATED);
            popupStage.initStyle(StageStyle.TRANSPARENT);

            scene.setFill(Color.TRANSPARENT);

            popupStage.setScene(scene);
            popupStage.setResizable(false);
            popupStage.initModality(Modality.APPLICATION_MODAL);

            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadUser() {
        User user = Session.getUser();
        if (user != null) {
            String initial = user.getUsername().substring(0, 1).toUpperCase();
            userInitial.setText(initial);
            userName.setText(user.getEmail());

            // Teste si admin
            if (isAdmin(user)) {
                addParking.setVisible(true);
                addParking.setManaged(true);
            } else {
                addParking.setVisible(false);
                addParking.setManaged(false);
            }
        }
    }
    private boolean isAdmin(User user) {
        return "admin".equals(user.getUsername())
                && "1234".equals(user.getPassword())
                && user.getId() == 1;
    }

    public void logout() {
        Session.setUser(null);
        mainApp.showLogin();
    }

    public void loadParkings() {
        try {
            buttonContainer.getChildren().clear();
            ParkingDAO parkingDAO = new ParkingDAO();
            List<Parking> parkings = parkingDAO.getAllParkings();

            if( !parkings.isEmpty() ) {
                parkingId = parkings.get(0).getId();
            }

            for (Parking parking : parkings) {
                Button btn = new Button(parking.getName() + " (" + parking.getCapacity() + " places)");
                btn.setPrefWidth(Double.MAX_VALUE);
                btn.getStyleClass().add("parking-button");

                btn.setOnAction(e -> {
                    parkingId = parking.getId();
                    showParkingDetails(parking);
                });

                buttonContainer.getChildren().add(btn);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showParkingDetails(Parking parking) {
        capacity.setText(Integer.toString(parking.getCapacity()));
        carNbr.setText(Integer.toString(parking.getId()));
    }

    public int getParkingId() {
        return parkingId;
    }
}
