package utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager {
    private String url;

    public DatabaseManager(String dbPath) {
        this.url = "jdbc:sqlite:" + dbPath;
    }

    public void connect() {
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                var statement = conn.createStatement();
                statement.execute("CREATE TABLE IF NOT EXISTS players (id INTEGER PRIMARY KEY, name TEXT)");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void storePlayerName(String playerName) {
        String sql = "INSERT INTO players(name) VALUES(?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, playerName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
