package collector.services;

import collector.model.Charge;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class StationService {

    public static ArrayList<Charge> getChargesForCustomerFromDB(String url, String user,String password, int customer_id) {
        ArrayList result = new ArrayList<Charge>();
        try {
            Connection connection = DriverManager.getConnection(url, user, password);

            Statement statement = connection.createStatement();
            String query = "SELECT * FROM charge WHERE customer_id=" + customer_id + " AND invoice_id IS NULL;";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {

                Charge charge = new Charge(
                        resultSet.getInt("id"),
                        resultSet.getInt("customer_id"),
                        resultSet.getDouble("kwh"),
                        resultSet.getString("invoice_id"));

                result.add(charge);

                setInvoiceIdToCharges(url, user, password, charge.getId(), charge.getInvoiceId());

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


    public static void setInvoiceIdToCharges(String url, String user,String password, int chargeId, String invoiceId) {
        try {
            Connection connection = DriverManager.getConnection(url, user, password);

            Statement statement = connection.createStatement();
            String query = "UPDATE charge WHERE id=" + chargeId + " AND invoice_id IS NULL SET invoice_id = " + invoiceId + ";";
            ResultSet resultSet = statement.executeQuery(query);

            // Verbindung und Ressourcen schließen
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
