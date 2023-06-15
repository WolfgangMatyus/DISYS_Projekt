package at.projekt_ui;

import at.projekt_ui.model.Invoice;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

public class InvoiceController {
    private Stage primaryStage;

    @FXML
    private Label POSTLabel;

    @FXML
    private Label GETLabel;

    @FXML
    private TextField customerIDField;

    @FXML
    private WebView invoiceWebView;

    @FXML
    private WebEngine webEngine;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void initialize() {
        webEngine = invoiceWebView.getEngine();
    }

    @FXML
    private void onGenerateInvoiceClick(ActionEvent event) {
        String customerID = customerIDField.getText();
        String apiUrlCustID = "http://127.0.0.1:5151/api/v1/invoices/" + customerID;
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
                    try (InputStream in = conn.getInputStream()) {
                        Invoice invoice = Invoice.fromInputStream(in);
                        invoiceID[0] = invoice.getInvoiceId();
                        return "INVOICE: " + invoiceID[0];
                    }
                } else {
                    return "HTTP Error: " + responseCode;
                }
            }
        };

        apiCallTask.setOnSucceeded(taskEvent -> {
            String response = apiCallTask.getValue();
            POSTLabel.setText(response);
            startGetRequest(invoiceID[0]);
        });

        apiCallTask.setOnFailed(taskEvent -> {
            Throwable exception = apiCallTask.getException();
            POSTLabel.setText("Exception: " + exception.getMessage());
        });

        Thread apiCallPOSTThread = new Thread(apiCallTask);
        apiCallPOSTThread.start();
    }

    public void startGetRequest(UUID invoiceID) {
        Task<Void> apiCallGETTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int timeout = 10000; // Timeout after 10 seconds
                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < timeout) {
                    String apiUrlInvoiceID = "http://127.0.0.1:5151/api/v1/invoices/" + invoiceID;
                    URL url = new URL(apiUrlInvoiceID);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        try (InputStream inputStream = conn.getInputStream()) {
                            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                            int nRead;
                            byte[] data = new byte[1024];
                            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                                buffer.write(data, 0, nRead);
                            }
                            buffer.flush();
                            byte[] pdfData = buffer.toByteArray();
                            if (pdfData.length > 0) {
                                Platform.runLater(() -> {
                                    String base64EncodedData = Base64.getEncoder().encodeToString(pdfData);
                                    System.out.println(base64EncodedData);
                                    String content = "<embed src=\"data:application/pdf;base64," + base64EncodedData + "\" width=\"100%\" height=\"100%\">";
                                    webEngine.loadContent(content);
                                });
                                break;
                            }
                        }
                    } else {
                        System.out.println("GET Connection Error: " + responseCode);
                    }
                    Thread.sleep(1000);
                }
                return null;
            }
        };

        apiCallGETTask.setOnFailed(taskEvent -> {
            Throwable exception = apiCallGETTask.getException();
            GETLabel.setText("Exception: " + exception.getMessage());
        });

        Thread taskThread = new Thread(apiCallGETTask);
        taskThread.start();
    }
}