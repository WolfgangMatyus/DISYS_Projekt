package dispatcher.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StationService {

    public void getStationsFromDB(String url, String user,String password) {
        try {
            Connection connection = DriverManager.getConnection(url, user, password);

            Statement statement = connection.createStatement();
            String query = "SELECT * FROM station";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                // Daten aus dem ResultSet extrahieren
                int id = resultSet.getInt("id");
                String dbUrl = resultSet.getString("db_url");

                System.out.println("ID: " + id + ", dbUrl: " + dbUrl);
            }

            // Verbindung und Ressourcen schließen
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
