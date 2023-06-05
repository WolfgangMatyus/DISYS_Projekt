package collector.services;

import collector.model.Charge;
import collector.model.DispatcherCollectorMessage;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class StationService {

    public static ArrayList<Charge> getChargesForCustomerFromDB(String url, String user,String password, int customer_id, UUID invoice_id) {
        ArrayList result = new ArrayList<Charge>();
        try {
            Connection connection = DriverManager.getConnection(url, user, password);

            //Statement statement = connection.createStatement();
            //String query = "SELECT * FROM charge WHERE customer_id=" + customer_id + "AND invoice_id IS NULL;";
            //ResultSet resultSet = statement.executeQuery(query);

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM charge WHERE customer_id= ?;"); // AND invoice_id IS NULL
            statement.setObject(1, customer_id);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                Charge charge = new Charge(
                        resultSet.getInt("id"),
                        resultSet.getInt("customer_id"),
                        resultSet.getDouble("kwh"),
                        invoice_id);

                result.add(charge);

                // used for debugging
                //System.out.println(charge.getId());
                //System.out.println(charge.getCustomerId());
                //System.out.println(charge.getKwh());
                //System.out.println(charge.getInvoiceId());

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


    public static void setInvoiceIdToCharges(String url, String user,String password, int chargeId, UUID invoiceId) {

        try {
            Connection connection = DriverManager.getConnection(url, user, password);

            //Statement statement = connection.createStatement();
            PreparedStatement statement = connection.prepareStatement("UPDATE charge SET invoice_id = ?  WHERE id = ? AND invoice_id IS NULL;");

            statement.setObject(1,invoiceId);
            statement.setObject(2,chargeId);

            //String update = "UPDATE charge SET invoice_id = " + invoiceId + " WHERE id=" + chargeId + " AND invoice_id IS NULL;";
            int rowsAffected = statement.executeUpdate();

            System.out.println("Rows updated: " + rowsAffected);

            // Verbindung und Ressourcen schließen
            //resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
