package at.projekt_ui;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class InvoiceApplication extends Application {
    private static final String API_URL = "http://127.0.0.1:5151/api/v1/invoices/";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Button callApiButton = new Button("Generate Invoice");
        Label resultLabel = new Label();

        primaryStage.setTitle("Invoice Application");

        // Create instruction field
        Text instructionField = new Text("To create an Invoice of your current open Charges, please enter the Customer ID and click the 'Create Invoice' button.");

        // Create input field for customer ID
        TextField customerIDField = new TextField();
        customerIDField.setPromptText("Customer ID");

        callApiButton.setOnAction(event -> {
            String customerID = customerIDField.getText();
            String apiUrlCustID = API_URL + customerID;

            Task<String> apiCallTask = new Task<String>() {
                @Override
                protected String call() {
                    try {
                        URL url = new URL(apiUrlCustID);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("POST");

                        int responseCode = conn.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            String inputLine;
                            StringBuilder response = new StringBuilder();
                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine);
                            }
                            in.close();
                            return response.toString();
                        } else {
                            return "HTTP Error: " + responseCode;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        return "Exception: " + e.getMessage();
                    }
                }
            };

            apiCallTask.setOnSucceeded(taskEvent -> resultLabel.setText(apiCallTask.getValue()));
            apiCallTask.setOnFailed(taskEvent -> resultLabel.setText(apiCallTask.getException().getMessage()));

            Thread apiCallThread = new Thread(apiCallTask);
            apiCallThread.start();

            // Start periodic GET request thread
            Thread getApiThread = new Thread(() -> {
                try {
                    int timeout = 5000; // Timeout after 5 seconds
                    long startTime = System.currentTimeMillis();
                    while (System.currentTimeMillis() - startTime < timeout) {
                        // Perform GET request
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
                            System.out.println("GET Response: " + response.toString());
                        } else {
                            System.out.println("GET Error: " + responseCode);
                        }

                        // Wait for 1 second before making the next GET request
                        Thread.sleep(1000);

                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });

            getApiThread.start();
        });

        HBox inputBox = new HBox(10);
        inputBox.getChildren().addAll(customerIDField, callApiButton);

        VBox root = new VBox(10);
        root.setPadding(new Insets(28, 0, 0, 28));
        root.getChildren().addAll(instructionField, inputBox, resultLabel);

        Scene scene = new Scene(root, 1000, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}


/*
public class InvoiceApplication extends Application {

    private static final String REST_API_URL = "http://127.0.0.1:5151/api/v1/invoices/"; // Replace with your REST API URL

    private WebView webView; // Declare WebView here

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Invoice Application");

        // Create instruction field
        Text instructionField = new Text("To create an Invoice of your current open Charges, please enter the Customer ID and click the 'Create Invoice' button.");

        // Create input field for customer ID
        TextField customerIDField = new TextField();
        customerIDField.setPromptText("Customer ID");

        // Create button to create invoice
        Button createInvoiceButton = new Button("Generate Invoice");
        createInvoiceButton.setOnAction(event -> {
            String customerID = customerIDField.getText();
            //createInvoice(customerID);
        });

        // Create WebView to display the invoice PDF
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        // Create space for download link
        Hyperlink downloadLink = new Hyperlink();
        downloadLink.setVisited(false);
        downloadLink.setText("Download Invoice");
        downloadLink.setOnAction(event -> {
            // Implement code to handle the download action
            // For example, open a file chooser dialog to save the invoice PDF
        });

        VBox vbox = new VBox(10);

        vbox.getChildren().addAll(instructionField, customerIDField, createInvoiceButton, webView, downloadLink);

        primaryStage.setScene(new Scene(vbox, 1000, 800));
        primaryStage.show();
    }

    public static void createInvoice(){

    }
}
*/

