package models;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Car {
    private int id;
    private String plate;       // numéro d'immatriculation
    private String ownerEmail;       // propriétaire
    private int parkingId;      // id du parking associé
    private int duration;       // durée de stationnement en minutes
    private Timestamp added_time;

    public Car() {}

    public Car(int id, int parkingId, String plate, String ownerEmail, int duration, Timestamp added_time) {
        this.id = id;
        this.plate = plate;
        this.ownerEmail = ownerEmail;
        this.duration = duration;
        this.parkingId = parkingId;
        this.added_time = added_time;
    }

    public boolean isDurationElapsed() {
        // Heure actuelle
        LocalDateTime now = LocalDateTime.now();

        // Convertir Timestamp en LocalDateTime
        LocalDateTime added = added_time.toLocalDateTime();

        // Calculer la durée écoulée en minutes
        long minutesElapsed = Duration.between(added, now).toMinutes();

        // Vérifier si le temps autorisé est dépassé
        return minutesElapsed >= duration;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPlate() { return plate; }
    public void setPlate(String plate) { this.plate = plate; }

    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public int getParkingId() { return parkingId; }
    public void setParkingId(int parkingId) { this.parkingId = parkingId; }

    public Timestamp getAdded_time() {
        return added_time;
    }

    public void setAdded_time(Timestamp added_time) {
        this.added_time = added_time;
    }

    @Override
    public String toString() {
        return plate + " (" + ownerEmail + ")";
    }
}
