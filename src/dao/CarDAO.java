package dao;

import models.Car;
import utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarDAO {

    // Ajouter une voiture
    public void addCar(Car car) {
        String sql = "INSERT INTO cars (plate, owner_email, duration, parking_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, car.getPlate());
            stmt.setString(2, car.getOwnerEmail());
            stmt.setInt(3, car.getDuration());
            stmt.setInt(4, car.getParkingId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Récupérer toutes les voitures
    public List<Car> getAllCars() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Car car = new Car(
                        rs.getInt("id"),
                        rs.getInt("parking_id"),
                        rs.getString("plate"),
                        rs.getString("owner_email"),
                        rs.getInt("duration")
                );
                cars.add(car);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cars;
    }

    // Récupérer les voitures par parking
    public List<Car> getCarsByParking(int parkingId) {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars WHERE parking_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, parkingId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Car car = new Car(
                        rs.getInt("id"),
                        rs.getInt("parking_id"),
                        rs.getString("plate"),
                        rs.getString("owner_email"),
                        rs.getInt("duration")
                );
                cars.add(car);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cars;
    }

    // Supprimer une voiture
    public void deleteCar(int id) {
        String sql = "DELETE FROM cars WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

