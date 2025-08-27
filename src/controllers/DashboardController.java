package controllers;

import app.MainApp;
import dao.CarDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
import java.util.Optional;

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
        loadUser();
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

    public User loadUser() {
        this.user = Session.getUser();
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

            buttonContainer.getChildren().removeIf(node ->
                    node instanceof Button && node.getStyleClass().contains("delete-button")
            );

        }
        return this.user;
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


                // Bouton delete avec image
                Button deleteButton = new Button();
                deleteButton.setStyle(
                        "-fx-background-color: transparent;" +
                                "-fx-cursor: hand;" +
                                "-fx-padding: 5;" +
                                "-fx-background-radius: 5;"
                );

                ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/delete.png")));
                deleteIcon.setFitHeight(10);
                deleteIcon.setFitWidth(10);
                deleteButton.setGraphic(deleteIcon);

                // Action clic sur bouton delete
                deleteButton.setOnAction(e -> {
                    showDeleteParkingPopup(parking, parkingDAO);
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

                if( user != null && isAdmin(user)) {
                    parkingRow.getChildren().addAll(parkingLabel, spacer, deleteButton);
                } else {
                    parkingRow.getChildren().addAll(parkingLabel, spacer);
                }

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
                    showEditCarPopup(car, carDAO);
                });

                // Bouton Supprimer
                Button deleteBtn = new Button();
                ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/delete.png")));
                deleteIcon.setFitWidth(14);
                deleteIcon.setFitHeight(14);
                deleteBtn.setGraphic(deleteIcon);
                deleteBtn.setStyle("-fx-background-color: transparent;");
                deleteBtn.setOnAction(e -> {
                    showDeleteCarPopup(car, carDAO);
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

    public void setUser(User user) {
        this.user = user;
    }


    /**
     * Affiche un popup pour modifier les informations d'une voiture.
     *
     * @param parking La voiture à supprimer
     * @param parkingDAO L'objet DAO du ParkingDAO
     */
    private void showDeleteParkingPopup(Parking parking, ParkingDAO parkingDAO) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initStyle(StageStyle.TRANSPARENT);

        VBox root = new VBox(20);
        root.setStyle(
                "-fx-background-color: white;" +
                        "-fx-padding: 20;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #ccc;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 1;"
        );
        root.setAlignment(Pos.CENTER);

        Label message = new Label("Voulez-vous vraiment supprimer le parking \"" + parking.getName() + "\" ?");
        message.setStyle("-fx-font-size: 14px; -fx-text-fill: #333; -fx-font-weight: bold;");
        message.setWrapText(true);
        message.setMaxWidth(250);

        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);

        Button btnYes = new Button("Oui");
        btnYes.setStyle(
                "-fx-background-color: #ff4d4f;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 5;" +
                        "-fx-padding: 5 15;"
        );

        Button btnNo = new Button("Non");
        btnNo.setStyle(
                "-fx-background-color: #ccc;" +
                        "-fx-text-fill: #333;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 5;" +
                        "-fx-padding: 5 15;"
        );

        buttons.getChildren().addAll(btnYes, btnNo);
        root.getChildren().addAll(message, buttons);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        popupStage.setScene(scene);
        popupStage.setResizable(false);

        // Actions boutons
        btnYes.setOnAction(ev -> {
            try {
                parkingDAO.deleteParking(parking.getId());
                loadParkings();
                loadCars();
                popupStage.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        btnNo.setOnAction(ev -> popupStage.close());

        popupStage.showAndWait();
    }


    /**
     * Affiche un popup pour modifier les informations d'une voiture.
     *
     * @param car La voiture à modifier
     * @param carDAO L'objet DAO pour effectuer la mise à jour
     */
    private void showEditCarPopup(Car car, CarDAO carDAO) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initStyle(StageStyle.TRANSPARENT);

        VBox root = new VBox(15);
        root.setStyle(
                "-fx-background-color: white;" +
                        "-fx-padding: 20;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #ccc;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 1;"
        );

        root.setAlignment(Pos.CENTER);

        Label title = new Label("Modifier la voiture");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Champs
        Label plateLabel = new Label("Plaque :");
        javafx.scene.control.TextField plateField = new javafx.scene.control.TextField(car.getPlate());

        Label ownerLabel = new Label("Propriétaire :");
        javafx.scene.control.TextField ownerField = new javafx.scene.control.TextField(car.getOwnerEmail());

        Label durationLabel = new Label("Durée (min) :");
        javafx.scene.control.TextField durationField = new javafx.scene.control.TextField(String.valueOf(car.getDuration()));

        VBox fields = new VBox(10, plateLabel, plateField, ownerLabel, ownerField, durationLabel, durationField);

        fields.setMaxWidth(300);

        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);

        Button btnSave = new Button("Enregistrer");
        btnSave.setStyle(
                "-fx-background-color: #4CAF50;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 5;" +
                        "-fx-padding: 5 15;"
        );

        Button btnCancel = new Button("Annuler");
        btnCancel.setStyle(
                "-fx-background-color: #ccc;" +
                        "-fx-text-fill: #333;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 5;" +
                        "-fx-padding: 5 15;"
        );

        buttons.getChildren().addAll(btnSave, btnCancel);

        root.getChildren().addAll(title, fields, buttons);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        popupStage.setScene(scene);
        popupStage.setResizable(false);

        // Actions boutons
        btnSave.setOnAction(ev -> {
            try {
                // Validation simple
                String plate = plateField.getText().trim();
                String owner = ownerField.getText().trim();
                String durationStr = durationField.getText().trim();
                if (plate.isEmpty() || owner.isEmpty() || durationStr.isEmpty()) return;

                int duration = Integer.parseInt(durationStr);

                // Mise à jour de la voiture
                car.setPlate(plate);
                car.setOwnerEmail(owner);
                car.setDuration(duration);

                carDAO.updateCar(car); // méthode DAO pour modifier la voiture
                loadCars();
                popupStage.close();
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        });

        btnCancel.setOnAction(ev -> popupStage.close());

        popupStage.showAndWait();
    }
    public void showDeleteCarPopup(Car car, CarDAO carDAO) {
        Stage popupStage = new Stage();
        popupStage.initStyle(StageStyle.TRANSPARENT);
        popupStage.initModality(Modality.APPLICATION_MODAL);

        // Texte de confirmation
        Label message = new Label("Voulez-vous vraiment supprimer cette voiture ?");
        message.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

        // Boutons
        Button yesBtn = new Button("Oui");
        Button noBtn = new Button("Non");

        yesBtn.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-padding: 8 20; -fx-background-radius: 5;");
        noBtn.setStyle("-fx-background-color: #ccc; -fx-text-fill: black; -fx-padding: 8 20; -fx-background-radius: 5;");

        HBox buttons = new HBox(10, yesBtn, noBtn);
        buttons.setAlignment(Pos.CENTER);

        VBox root = new VBox(15, message, buttons);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10; -fx-border-color: #ccc; -fx-border-radius: 10;");

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        popupStage.setScene(scene);

        // Action des boutons
        yesBtn.setOnAction(e -> {
            try {
                carDAO.deleteCar(car.getId());
                loadCars(); // recharge la liste des voitures
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                popupStage.close();
            }
        });

        noBtn.setOnAction(e -> popupStage.close());

        popupStage.showAndWait();
    }
}