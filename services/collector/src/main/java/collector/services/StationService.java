package collector.services;

import collector.model.Charge;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class StationService {

    public static ArrayList<Charge> getChargesForCustomerFromDB(String url, String user,String password, int customer_id, UUID invoice_id) {
        ArrayList result = new ArrayList<Charge>();
        try {
            Connection connection = DriverManager.getConnection(url, user, password);

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM charge WHERE customer_id= ? AND invoice_id IS null;"); // AND invoice_id IS NULL
            statement.setObject(1, customer_id);
            ResultSet resultSet = statement.executeQuery();
            System.out.println(resultSet);
            while (resultSet.next()) {

                Charge charge = new Charge(
                        resultSet.getInt("id"),
                        resultSet.getInt("customer_id"),
                        resultSet.getDouble("kwh"),
                        invoice_id,
                        resultSet.getDouble("price"));

                result.add(charge);

                setInvoiceIdToCharges(url, user, password, charge.getId(), charge.getInvoiceId());
            }

            // close connections
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

            PreparedStatement statement = connection.prepareStatement("UPDATE charge SET invoice_id = ?  WHERE id = ? AND invoice_id IS NULL;");

            statement.setObject(1,invoiceId);
            statement.setObject(2,chargeId);

            int rowsAffected = statement.executeUpdate();

            System.out.println("Rows updated: " + rowsAffected);

            // close connections
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
