package controllers;

import app.MainApp;
import dao.CarDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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
import java.sql.SQLException;
import java.util.List;

public class DashboardController {

    public VBox buttonContainer;
    public Label capacity;
    public Label carNbr;
    public VBox listeCarContainer;
    public Button addCarBtn;
    private MainApp mainApp;
    private int parkingId;
    private User user;
    private int totalCar;
    private Parking parkingTmp;

    @FXML private Button addParking;
    @FXML private Label lblVehiclesCount;
    @FXML private Label userName;
    @FXML private Label userInitial;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void initialize() {
        loadParkings();
        loadCars();
    }

    public void reloadList() {
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
            addCarController.setDashboardController(this);

            addCarController.hideOwnerField(user);
            addCarController.setParkingId(parkingId);
            if (user != null) {
                addCarController.setUser(user);
            }

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
        user = Session.getUser();
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
                loadCars();
                parkingTmp = parkings.get(0);
                showParkingDetails(parkings.get(0), totalCar);
            }

            for (Parking parking : parkings) {
                // Conteneur horizontal pour le nom + bouton delete
                HBox parkingRow = new HBox(10);
                parkingRow.setAlignment(Pos.CENTER_LEFT);
                parkingRow.setStyle("-fx-padding: 6 14 6 14; -fx-background-color: #f5f5f5; -fx-background-radius: 8;");

                // Label du parking
                Label parkingLabel = new Label(parking.getName() + " (" + parking.getCapacity() + " places)");
                parkingLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #333; -fx-font-weight: bold;");
//                System.out.print(user.getEmail());

                // Bouton delete avec image
                Button deleteButton = new Button();
                deleteButton.setStyle(
                        "-fx-background-color: transparent;" +
                                "-fx-cursor: hand;" +
                                "-fx-padding: 5;" +
                                "-fx-background-radius: 5;"
                );

                ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/delete.png")));
                deleteIcon.setFitHeight(12);
                deleteIcon.setFitWidth(12);
                deleteButton.setGraphic(deleteIcon);

                // Action clic sur bouton delete
                deleteButton.setOnAction(e -> {
                    try {
                        parkingDAO.deleteParking(parking.getId());
                        buttonContainer.getChildren().remove(parkingRow);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });

                // Clic sur le label = afficher détails
                parkingLabel.setOnMouseClicked(e -> {
                    parkingId = parking.getId();
                    parkingTmp = parking;
                    loadCars();
                    showParkingDetails(parking, totalCar);
                });

                // Ajouter label + bouton à la ligne
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                parkingRow.getChildren().addAll(parkingLabel, spacer, deleteButton);

                // Ajouter la ligne dans le container
                buttonContainer.getChildren().add(parkingRow);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showParkingDetails(Parking parking, int totalCar) {
        if(parking == null) {
            return;
        }

        capacity.setText(Integer.toString(parking.getCapacity()));
        carNbr.setText(Integer.toString(totalCar));
        if (totalCar >= parking.getCapacity()) {
            addCarBtn.setDisable(true);   // désactive le bouton
            addCarBtn.setText("Parking plein"); // facultatif, pour montrer visuellement
        } else {
            addCarBtn.setDisable(false);  // réactive le bouton
            addCarBtn.setText("Ajouter voiture");
        }
    }

    public void loadCars() {
        try {
            listeCarContainer.getChildren().clear();
            CarDAO carDAO = new CarDAO();
            List<Car> cars = carDAO.getCarsByParking(parkingId);

            totalCar = cars.toArray().length;

            showParkingDetails(parkingTmp, totalCar);

            if (cars.isEmpty()) {
                Label emptyLabel = new Label("Aucune voiture dans ce parking.");
                emptyLabel.setStyle(
                                "-fx-text-fill: #888888; " +
                                "-fx-font-size: 16px; " +
                                "-fx-padding: 20;"
                );
                emptyLabel.setMaxWidth(Double.MAX_VALUE);
                emptyLabel.setAlignment(Pos.CENTER); // centre le texte horizontalement
                listeCarContainer.setAlignment(Pos.CENTER); // centre le contenu dans le VBox
                listeCarContainer.getChildren().add(emptyLabel);
                return;
            }


            for (Car car : cars) {
                HBox carRow = new HBox();
                carRow.setSpacing(10);
                carRow.setStyle("-fx-padding: 10; -fx-background-color: #f5f5f5; -fx-background-radius: 8;");
                carRow.setAlignment(Pos.CENTER_LEFT);

                // Plaque
                Label plateLabel = new Label(car.getPlate());
                plateLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
                plateLabel.setPrefWidth(150);

                // Propriétaire
                Label ownerLabel = new Label(car.getOwnerEmail());
                ownerLabel.setPrefWidth(200);

                // Durée
                Label durationLabel = new Label(car.getDuration() + " min");
                durationLabel.setPrefWidth(100);

                // Bouton Modifier
                Button editBtn = new Button();
                ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/edit.png")));
                editIcon.setFitWidth(20);
                editIcon.setFitHeight(20);
                editBtn.setGraphic(editIcon);
                editBtn.setStyle("-fx-background-color: transparent;");
                editBtn.setOnAction(e -> {
                    // action pour modifier la voiture
                });

                // Bouton Supprimer
                Button deleteBtn = new Button();
                ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/delete.png")));
                deleteIcon.setFitWidth(14);
                deleteIcon.setFitHeight(14);
                deleteBtn.setGraphic(deleteIcon);
                deleteBtn.setStyle("-fx-background-color: transparent;");
                deleteBtn.setOnAction(e -> {
                    // action pour supprimer la voiture
                });

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                carRow.getChildren().addAll(plateLabel, ownerLabel, durationLabel, spacer, editBtn, deleteBtn);

                listeCarContainer.getChildren().add(carRow);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getParkingId() {
        return parkingId;
    }

    public User getUser() {
        return user;
    }
}