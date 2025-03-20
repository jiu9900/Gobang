module com.example.wuzi {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.wuzi to javafx.fxml;
    exports com.example.wuzi;
}