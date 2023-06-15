package at.projekt_ui;

import com.dansoftware.pdfdisplayer.JSLogListener;
import com.dansoftware.pdfdisplayer.PDFDisplayer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import okhttp3.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class InvoiceApplication extends Application {
    private boolean visible;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Button btn = new Button("Generate Invoice");

        btn.setOnAction(event -> {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://127.0.0.1:5151/api/v1/invoices/5b39c892-c740-47c2-b4a4-d64d6835e516")
                    //.url("http://127.0.0.1:5151/api/v1/invoices/{invoiceID}")
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

                            PDFDisplayer displayer = new PDFDisplayer(tempFilePath.toUri().toURL());
                            displayer.setSecondaryToolbarToggleVisibility(visible);
                            displayer.setVisibilityOf("sidebarToggle", false);

                            primaryStage.setScene(new Scene(new VBox(displayer.toNode())));
                            primaryStage.show();
                        }
                    } else {
                        System.out.println("Failed to retrieve invoice: " + response.code());
                    }
                }
            });
        });

        JSLogListener.setOutputStream(System.err);

        primaryStage.setScene(new Scene(new VBox(btn)));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


