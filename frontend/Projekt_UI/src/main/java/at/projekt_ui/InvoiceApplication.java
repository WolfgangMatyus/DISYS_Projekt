package at.projekt_ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class InvoiceApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("invoice.fxml"));
        Parent root = loader.load();

        InvoiceController controller = loader.getController();
        controller.setPrimaryStage(primaryStage); // Übergeben der primaryStage an den Controller

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}