module com.batallanaval.batallanaval {
    // Necesario para las clases de JavaFX (como Application)
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics; // Si se usa alguna clase de JavaFX Graphics

    // DEBES ABRIR el paquete que contiene la clase BatallaNavalApp (para Launcher)
    opens com.batallanaval.batallanaval to javafx.fxml, javafx.graphics;

    // DEBES ABRIR el paquete que contiene tus VISTAS FXML (para FXMLLoader)
    opens com.batallanaval.batallanaval.view to javafx.fxml;

    // Y DEBES ABRIR el paquete que contiene tus CONTROLADORES (PARA EL ERROR ACTUAL)
    // La línea que probablemente estaba mal y que debes corregir:
    opens com.batallanaval.batallanaval.controller to javafx.fxml;

    // ... otras declaraciones si las hay, como exports ...
}