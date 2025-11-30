package com.batallanaval.controller;

import com.batallanaval.model.*;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class JuegoController {

    @FXML
    private GridPane tableroJugador;

    private Jugador jugador;

    public void initialize() {
        jugador = new Jugador("Humano");
        crearTablero(tableroJugador, jugador.getTablero());
    }

    private void crearTablero(GridPane grid, Tablero tablero) {
        for (int fila = 0; fila < 10; fila++) {
            for (int col = 0; col < 10; col++) {
                Pane celda = new Pane();
                celda.setPrefSize(30, 30);
                celda.setStyle("-fx-border-color: black; -fx-background-color: lightgrey;");
                grid.add(celda, col, fila);
            }
        }
    }
}
