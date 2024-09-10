module com.example.pillandcapsuleanalyserslc {
    requires javafx.controls;
    requires javafx.fxml;
    requires jmh.core;


    opens com.example.pillandcapsuleanalyserslc to javafx.fxml;
    exports com.example.pillandcapsuleanalyserslc;
}