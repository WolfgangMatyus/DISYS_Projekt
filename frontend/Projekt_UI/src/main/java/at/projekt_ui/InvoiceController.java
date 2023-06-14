package at.projekt_ui;

import at.projekt_ui.model.Invoice;
import com.google.gson.Gson;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;

import java.io.*;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Base64;
import java.util.UUID;

public class InvoiceController{

    @FXML
    private static final String API_URL = "http://127.0.0.1:5151/api/v1/invoices/";

    @FXML
    private Label POSTLabel;

    @FXML
    private Label GETLabel;

    @FXML
    private TextField customerIDField;

    @FXML
    private WebEngine webEngine;


    @FXML
    private void onGenerateInvoiceClick(ActionEvent event) {
        String customerID = customerIDField.getText();
        // Perform API call and handle the result
        String apiUrlCustID = API_URL + customerID;
        UUID[] invoiceID = {null};

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
                        //response.append(inputLine);
                        Invoice invoice = new Gson().fromJson(inputLine, Invoice.class);

                        invoiceID[0] = invoice.getInvoiceId();
                        response.append("INVOICE: " + invoiceID[0]);
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
        startGetRequest(invoiceID);
    }

    public void startGetRequest(UUID[] invoiceID) {

        Task<String> apiCallGETTask = new Task<String>() {
            @Override
            protected String call() throws Exception {
                int timeout = 10000; // Timeout after 10 seconds
                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < timeout) {
                    String apiUrlCustID = API_URL + invoiceID[0];
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

                        // Save the PDF data to a temporary file
                        File tempFile = File.createTempFile("temp", ".pdf");
                        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
                            outputStream.write(Base64.getDecoder().decode(responseData));
                        }

                        // Load the temporary PDF file into the WebView using WebEngine
                            webEngine.load("https://www.google.com/");

                    } else {
                        System.out.println("GET Connection Error: " + responseCode);
                    }

                    // Wait for 1 second before making the next GET request
                    Thread.sleep(1000);
                }
                return null;
            }
        };

        apiCallGETTask.setOnSucceeded(taskEvent -> {
            String response = apiCallGETTask.getValue();
            GETLabel.setText(response);
        });

        apiCallGETTask.setOnFailed(taskEvent -> {
            Throwable exception = apiCallGETTask.getException();
            GETLabel.setText("Exception: " + exception.getMessage());
        });
        // Start the task in a separate thread
        Thread taskThread = new Thread(apiCallGETTask);
        taskThread.start();
    }
}