package models;

public class Car {
    private int id;
    private String plate;       // numéro d'immatriculation
    private String owner;       // propriétaire
    private int duration;       // durée de stationnement en minutes
    private int parkingId;      // id du parking associé

    public Car() {}

    public Car(int id, int parkingId, String plate, String owner, int duration) {
        this.id = id;
        this.plate = plate;
        this.owner = owner;
        this.duration = duration;
        this.parkingId = parkingId;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPlate() { return plate; }
    public void setPlate(String plate) { this.plate = plate; }

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public int getParkingId() { return parkingId; }
    public void setParkingId(int parkingId) { this.parkingId = parkingId; }

    @Override
    public String toString() {
        return plate + " (" + owner + ")";
    }
}
