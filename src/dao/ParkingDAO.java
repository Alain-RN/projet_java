package dao;

import models.Parking;
import utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParkingDAO {

    // Ajouter un parking
    public void addParking(Parking parking) throws SQLException {
        String sql = "INSERT INTO parking (name, location, capacity) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, parking.getName());
            stmt.setString(2, parking.getLocation());
            stmt.setInt(3, parking.getCapacity());
            stmt.executeUpdate();
        }
    }

    // Récupérer tous les parkings
    public List<Parking> getAllParkings() throws SQLException {
        List<Parking> parkings = new ArrayList<>();
        String sql = "SELECT * FROM parking";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Parking p = new Parking();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setLocation(rs.getString("location"));
                p.setCapacity(rs.getInt("capacity"));
                parkings.add(p);
            }
        }
        return parkings;
    }

    // Vérifier si un parking existe par nom
    public boolean existsByName(String name) throws SQLException {
        String sql = "SELECT COUNT(*) FROM parking WHERE name = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    // Supprimer un parking par ID
    public void deleteParking(int id) throws SQLException {
        String sql = "DELETE FROM parking WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // Mettre à jour un parking
    public void updateParking(Parking parking) throws SQLException {
        String sql = "UPDATE parking SET name = ?, location = ?, capacity = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, parking.getName());
            stmt.setString(2, parking.getLocation());
            stmt.setInt(3, parking.getCapacity());
            stmt.setInt(4, parking.getId());
            stmt.executeUpdate();
        }
    }
}

