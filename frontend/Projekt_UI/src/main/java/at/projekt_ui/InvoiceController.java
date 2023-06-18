package at.projekt_ui;

import at.projekt_ui.model.Invoice;
import com.dansoftware.pdfdisplayer.JSLogListener;
import com.dansoftware.pdfdisplayer.PDFDisplayer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import okhttp3.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;
import java.util.UUID;




public class InvoiceController implements Initializable {

    private Stage primaryStage;

    private boolean visible = true;
    private boolean pdfReceived = false;

    @FXML
    private Label POSTLabel;

    @FXML
    private Label GETLabel;

    @FXML
    private TextField customerIDField;

    @FXML
    private VBox pdfContainer;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialisierungscode hier
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

    public void addPDFDisplayerToContainer(PDFDisplayer displayer) {
        pdfContainer.getChildren().setAll(displayer.toNode());
    }

    public void startGetRequest(UUID invoiceID) {
        Task<Void> apiCallGETTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int timeout = 10000; // Timeout after 10 seconds
                long startTime = System.currentTimeMillis();

                while (System.currentTimeMillis() - startTime < timeout && !pdfReceived) {

                    OkHttpClient client = new OkHttpClient();
                    String apiUrlInvoiceID = "http://127.0.0.1:5151/api/v1/invoices/" + invoiceID;
                    URL url = new URL(apiUrlInvoiceID);
                    System.out.println(url);
                    Request request = new Request.Builder()
                            .url(url)
                            .build();
                    client.newCall(request).enqueue(new Callback() {

                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                try (InputStream inputStream = response.body().byteStream()) {
                                    Path tempFilePath = Files.createTempFile("invoice_", ".pdf");
                                    Files.copy(inputStream, tempFilePath, StandardCopyOption.REPLACE_EXISTING);

                                    Platform.runLater(() -> {
                                        PDFDisplayer displayer = null;
                                        try {
                                            displayer = new PDFDisplayer(tempFilePath.toUri().toURL());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        displayer.setSecondaryToolbarToggleVisibility(visible);
                                        displayer.setVisibilityOf("sidebarToggle", false);

                                        primaryStage.setScene(new Scene(new VBox(displayer.toNode())));
                                        primaryStage.show();

                                        addPDFDisplayerToContainer(displayer);

                                        pdfReceived = true;
                                    });
                                }
                            } else {
                                System.out.println("Failed to retrieve invoice: " + response.code());
                            }
                        }
                    });
                    Thread.sleep(1000);
                }
                return null;
            }
        };

        JSLogListener.setOutputStream(System.err);

        primaryStage.setScene(new Scene(new VBox()));
        primaryStage.show();

        apiCallGETTask.setOnFailed(taskEvent -> {
            Throwable exception = apiCallGETTask.getException();
            GETLabel.setText("Exception: " + exception.getMessage());
        });

        Thread taskThread = new Thread(apiCallGETTask);
        taskThread.start();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}