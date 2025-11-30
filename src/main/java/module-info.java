module com.batallanaval.batallanaval {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.batallanaval.batallanaval to javafx.fxml;
    exports com.batallanaval.batallanaval;
}