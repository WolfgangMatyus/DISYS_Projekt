module at.projekt_ui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires jdk.jsobject;
    requires java.scripting;
    requires lombok;
    requires gson;
    requires java.sql;


    opens at.projekt_ui to javafx.fxml;
    exports at.projekt_ui;
    exports at.projekt_ui.controller;
    opens at.projekt_ui.model;
    exports at.projekt_ui.model;
    opens at.projekt_ui.controller to javafx.fxml;
}