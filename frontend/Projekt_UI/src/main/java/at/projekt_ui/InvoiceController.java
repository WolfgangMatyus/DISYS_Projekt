package at.projekt_ui;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;

public class InvoiceController {

    @FXML
    private static final String API_URL = "http://127.0.0.1:5151/api/v1/invoices/";

    @FXML
    private Label POSTLabel;

    @FXML
    private Label GETLabel;

    @FXML
    private TextField customerIDField;

    @FXML
    private void onGenerateInvoiceClick(ActionEvent event) {
        String customerID = customerIDField.getText();
        // Perform API call and handle the result
        String apiUrlCustID = API_URL + customerID;

        Task<String> apiCallTask = new Task<String>() {
            @Override
            protected String call() throws Exception {
                URL url = new URL(apiUrlCustID);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);


                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append("Response: ").append(inputLine);
                    }
                    in.close();
                    return response.toString();
                } else {
                    return "HTTP Error: " + responseCode;
                }

            }
        };

        apiCallTask.setOnSucceeded(taskEvent -> {
            String response = apiCallTask.getValue();
            POSTLabel.setText(response);
        });

        apiCallTask.setOnFailed(taskEvent -> {
            Throwable exception = apiCallTask.getException();
            POSTLabel.setText("Exception: " + exception.getMessage());
        });

        Thread apiCallPOSTThread = new Thread(apiCallTask);
        apiCallPOSTThread.start();

        Task<Void> apiCallGETTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int timeout = 5000; // Timeout after 5 seconds
                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < timeout) {
                    URL url = new URL(apiUrlCustID);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                        String responseData = response.toString();
                        System.out.println("GET Response: " + responseData);
                        updateMessage(responseData);
                    } else {
                        System.out.println("GET Connection Error: " + responseCode);
                    }

                    // Wait for 1 second before making the next GET request
                    Thread.sleep(1000);
                }

                return null;
            }
        };

        GETLabel.textProperty().bind(apiCallGETTask.messageProperty());

        apiCallGETTask.setOnSucceeded(taskEvent -> {
            GETLabel.textProperty().unbind();
            GETLabel.setText("ERROR: 404 Invoice Not Found.");
        });

        apiCallGETTask.setOnFailed(taskEvent -> {
            GETLabel.textProperty().unbind();
            Throwable exception = apiCallGETTask.getException();
            GETLabel.setText("Exception: " + exception.getMessage());
        });

        Thread apiCallGETThread = new Thread(apiCallGETTask);
        apiCallGETThread.start();
    }
}