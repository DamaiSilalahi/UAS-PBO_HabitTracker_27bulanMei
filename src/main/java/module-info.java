module com.example.uaspbo_habittracker_27bulanmei {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.uaspbo_habittracker_27bulanmei.controller to javafx.fxml;
    exports com.example.uaspbo_habittracker_27bulanmei;
    exports com.example.uaspbo_habittracker_27bulanmei.model;
}