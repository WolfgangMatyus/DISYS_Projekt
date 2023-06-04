package dispatcher.services;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import dispatcher.model.Station;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class StationService {

    public static ArrayList<Station> getStationsFromDB(String url, String user, String password) {

        ArrayList result = new ArrayList<Station>();
        try {
            Connection connection = DriverManager.getConnection(url, user, password);

            Statement statement = connection.createStatement();
            String query = "SELECT * FROM station";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                // Daten aus dem ResultSet extrahieren

                Station station = new Station();
                station.setId(resultSet.getInt("id"));
                station.setUrl(resultSet.getString("db_url"));

                result.add(station);
                System.out.println("ID: " + station.getId() + ", dbUrl: " + station.getUrl());
            }

            // Verbindung und Ressourcen schließen
            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }



}
