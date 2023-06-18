module at.projekt_ui {
    requires javafx.controls;
    requires PDFViewerFX;
    requires gson;
    requires javafx.fxml;
    requires javafx.web;
    requires okhttp3;
    requires java.sql;

    opens at.projekt_ui to javafx.graphics, javafx.fxml;
    opens at.projekt_ui.model to gson;
    exports at.projekt_ui.model to gson;
}