module at.projekt_ui {
    requires javafx.controls;
    requires PDFViewerFX;
    requires gson;
    requires javafx.fxml;
    requires javafx.web;
    requires okhttp3;

    opens at.projekt_ui to javafx.graphics;
}