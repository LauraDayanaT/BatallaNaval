package com.batallanaval.batallanaval;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BatallaNavalApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/batallanaval/batallanaval/view/JuegoView.fxml")
        );

        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }
}
