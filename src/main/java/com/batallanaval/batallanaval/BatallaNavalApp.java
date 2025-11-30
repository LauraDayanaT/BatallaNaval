package com.batallanaval.batallanaval;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class BatallaNavalApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // La ruta empieza desde resources
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/batallanaval/view/JuegoView.fxml"));
        AnchorPane root = loader.load();

        Scene scene = new Scene(root, 600, 700);
        scene.getStylesheets().add(getClass().getResource("/com/batallanaval/view/estilos.css").toExternalForm());

        stage.setTitle("Batalla Naval");
        stage.setScene(scene);
        stage.show();
    }
}
