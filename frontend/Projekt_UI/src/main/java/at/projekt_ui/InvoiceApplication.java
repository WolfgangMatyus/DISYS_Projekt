package at.projekt_ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.util.Base64;

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
        Button createInvoiceButton = new Button("Create Invoice");
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
