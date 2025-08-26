import utils.Database;

import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        try (Connection conn = Database.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Connexion réussie à la base MySQL !");
            } else {
                System.out.println("❌ Connexion échouée.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la connexion : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
