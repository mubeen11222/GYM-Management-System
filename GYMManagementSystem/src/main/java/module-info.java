module com.oop.gymmanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.oop.gymmanagementsystem to javafx.fxml;
    opens com.oop.gymmanagementsystem.models to javafx.fxml;
    opens com.oop.gymmanagementsystem.ui to javafx.fxml;

    exports com.oop.gymmanagementsystem;
    exports com.oop.gymmanagementsystem.models;
    exports com.oop.gymmanagementsystem.services;
    exports com.oop.gymmanagementsystem.ui;
    exports com.oop.gymmanagementsystem.exceptions;
    exports com.oop.gymmanagementsystem.utils;
    exports com.oop.gymmanagementsystem.storage;
}