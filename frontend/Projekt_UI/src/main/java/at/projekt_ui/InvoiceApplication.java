package at.projekt_ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InvoiceApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("invoice.fxml"));
            primaryStage.setTitle("Invoice Application");
            primaryStage.setScene(new Scene(root, 1000, 800));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}


// PDF in WEBVIEW ANZEIGEN:
/*
package com.example.pdfviewer;

        import java.nio.file.Path;
        import java.nio.file.Paths;

        import com.dansoftware.pdfdisplayer.JSLogListener;
        import com.dansoftware.pdfdisplayer.PDFDisplayer;
        import javafx.application.Application;
        import javafx.scene.Scene;
        import javafx.scene.control.Button;
        import javafx.scene.layout.VBox;
        import javafx.stage.Stage;

        import java.io.File;

public class PDFViewerApplication extends Application {
    private boolean visible;

    public class PathUtils {

        public static String getRootPath() {
            String currentWorkingDir = System.getProperty("user.dir");
            Path rootPath = Paths.get(currentWorkingDir);
            return rootPath.toString();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        String filePath = PathUtils.getRootPath() + "\\S4-DISYS_05_JavaFXBeginning.pdf";
        System.out.println("Root Path: " + filePath);
        File pdfFile = new File(filePath);
        PDFDisplayer displayer = new PDFDisplayer(pdfFile.toURI().toURL());
        displayer.setSecondaryToolbarToggleVisibility(visible);
        displayer.setVisibilityOf("sidebarToggle", false);

        Button btn = new Button("Hide/Show");
        btn.setOnAction(event -> {
            displayer.setSecondaryToolbarToggleVisibility(visible = !visible);
        });

        JSLogListener.setOutputStream(System.err);

        primaryStage.setScene(new Scene(new VBox(displayer.toNode(), btn)));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
 */
