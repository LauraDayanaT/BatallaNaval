package com.batallanaval.batallanaval;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class BatallaNavalApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Cargar JuegoView.fxml (el juego completo)
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/batallanaval/batallanaval/view/JuegoView1.fxml")
        );

        Scene scene = new Scene(loader.load());
        stage.setTitle("Batalla Naval - Juego Completo");
        stage.setScene(scene);
        stage.getIcons().add(new javafx.scene.image.Image(
                Objects.requireNonNull(getClass().getResource("/com/batallanaval/batallanaval/view/buque-de-guerra.png")).toExternalForm()));
        stage.show();

        System.out.println("✅ Aplicación iniciada con JuegoView1.fxml");
    }
}