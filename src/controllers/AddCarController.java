package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddCarController {

    public TextField carOwnerField;
    public Label titleAddCar;
    private int timeMinutes = 60;
    public Button cancelBtn;
    public TextField parkingTimeField;
    private int parkingId;

    private void closeWindow() {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void initialize() {
        cancelBtn.setOnAction(e -> closeWindow());
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

    public void setParkingId(int id) {
        titleAddCar.setText(Integer.toString(id));
    }
}
