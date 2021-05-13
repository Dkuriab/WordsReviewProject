module org.controllers {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens org.controllers to javafx.fxml;
    exports org.controllers;
}