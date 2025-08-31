package dao;

import models.Car;
import models.User;
import utils.Database;
import utils.Session;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CarDAO {

    // Ajouter une voiture
    public void addCar(Car car) {
        String sql = "INSERT INTO cars (plate, owner_email, duration, parking_id, added_time) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, car.getPlate());
            stmt.setString(2, car.getOwnerEmail());
            stmt.setInt(3, car.getDuration());
            stmt.setInt(4, car.getParkingId());
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

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
                        rs.getInt("duration"),
                        rs.getTimestamp("added_time")
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
                        rs.getInt("duration"),
                        rs.getTimestamp("added_time")
                );
                cars.add(car);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cars;
    }

    // Mettre a jour les données des voiture
    public void updateCar(Car car) {
        String sql = "UPDATE cars SET plate = ?, owner_email = ?, duration = ?, parking_id = ?, added_time = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, car.getPlate());
            stmt.setString(2, car.getOwnerEmail());
            stmt.setInt(3, car.getDuration());
            stmt.setInt(4, car.getParkingId());
            stmt.setInt(6, car.getId());
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public boolean addCarIfValid(Car car, User user) {
        try (Connection conn = Database.getConnection()) {

            // Vérifier si la voiture (plaque) existe déjà dans ce parking
            String checkCarSql = "SELECT COUNT(*) FROM cars WHERE plate = ? AND parking_id = ?";
            try (PreparedStatement checkCarStmt = conn.prepareStatement(checkCarSql)) {
                checkCarStmt.setString(1, car.getPlate());
                checkCarStmt.setInt(2, car.getParkingId());
                ResultSet rsCar = checkCarStmt.executeQuery();
                rsCar.next();
                if (rsCar.getInt(1) > 0) {
                    return false; // voiture déjà dans ce parking
                }
            }

            // Vérifier si la voiture existe dans n'importe quel parking
            String checkGlobalCarSql = "SELECT COUNT(*) FROM cars WHERE plate = ?";
            try (PreparedStatement checkGlobalStmt = conn.prepareStatement(checkGlobalCarSql)) {
                checkGlobalStmt.setString(1, car.getPlate());
                ResultSet rsGlobal = checkGlobalStmt.executeQuery();
                rsGlobal.next();
                if (rsGlobal.getInt(1) > 0) {
                    return false; // voiture déjà dans un autre parking
                }
            }

            if (Session.isAdmin(user)) {
                String checkUserSql = "SELECT COUNT(*) FROM cars WHERE owner_email = ? AND parking_id = ?";
                try (PreparedStatement checkUserStmt = conn.prepareStatement(checkUserSql)) {
                    checkUserStmt.setString(1, car.getOwnerEmail());
                    checkUserStmt.setInt(2, car.getParkingId());
                    ResultSet rsUser = checkUserStmt.executeQuery();
                    rsUser.next();
                    if (rsUser.getInt(1) > 0) {
                        return false; // l'utilisateur a déjà une voiture dans ce parking
                    }
                }
            }

            // Toutes les vérifications sont passées, on ajoute la voiture
            addCar(car);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}

