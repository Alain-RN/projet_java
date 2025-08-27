package models;

public class Car {
    private int id;
    private String plate;       // numéro d'immatriculation
    private String ownerEmail;       // propriétaire
    private int duration;       // durée de stationnement en minutes
    private int parkingId;      // id du parking associé

    public Car() {}

    public Car(int id, int parkingId, String plate, String ownerEmail, int duration) {
        this.id = id;
        this.plate = plate;
        this.ownerEmail = ownerEmail;
        this.duration = duration;
        this.parkingId = parkingId;
    }

    // Getters et Setters
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

    @Override
    public String toString() {
        return plate + " (" + ownerEmail + ")";
    }
}
