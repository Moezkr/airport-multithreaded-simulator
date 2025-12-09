module com.gestiontache.airportmanagement {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;


    exports com.gestiontache.airportmanagement.monitor;
    exports com.gestiontache.airportmanagement.semaphore;
    exports com.gestiontache.airportmanagement.gui;


    opens com.gestiontache.airportmanagement.gui to javafx.fxml;

    opens com.gestiontache.airportmanagement to javafx.fxml;
}