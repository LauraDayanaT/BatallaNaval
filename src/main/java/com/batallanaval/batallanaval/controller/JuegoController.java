package com.batallanaval.batallanaval.controller;

import com.batallanaval.model.*;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class JuegoController {

    @FXML
    private GridPane tableroJugador;

    @FXML
    private VBox panelBarcos;

    @FXML
    private GridPane tableroOponente;

    private Jugador jugador;
    private Jugador maquina;

    // ✅ Solo un initialize
    public void initialize() {
        jugador = new Jugador("Humano");
        maquina = new Jugador("Máquina");

        crearTableroVisual();
        crearTableroOponente();
        colocarBarcosMaquina(); // aquí se coloca la flota de la máquina
        crearPanelBarcos();
    }

    // Crear tablero 10x10 jugador
    public void crearTableroVisual() {
        for (int fila = 0; fila < 10; fila++) {
            for (int col = 0; col < 10; col++) {
                Pane celda = new Pane();
                celda.setPrefSize(30, 30);
                celda.getStyleClass().add("pane-celda");

                final int f = fila, c = col;
                celda.setOnMouseDragReleased(e -> colocarBarco(f, c, celda));

                tableroJugador.add(celda, col, fila);
            }
        }
    }

    // Crear tablero 10x10 oponente
    private void crearTableroOponente() {
        for (int fila = 0; fila < 10; fila++) {
            for (int col = 0; col < 10; col++) {
                Pane celda = new Pane();
                celda.setPrefSize(30, 30);
                celda.getStyleClass().add("pane-celda");

                final int f = fila, c = col;
                celda.setOnMouseClicked(e -> disparar(f, c, celda));

                tableroOponente.add(celda, col, fila);
            }
        }
    }

    // Crear panel lateral con barcos
    private void crearPanelBarcos() {
        Barco portaaviones = new Barco("Portaaviones", 4);
        Pane pPorta = crearBarcoPane(portaaviones);
        panelBarcos.getChildren().add(pPorta);

        Barco sub1 = new Barco("Submarino", 3);
        panelBarcos.getChildren().add(crearBarcoPane(sub1));

        Barco sub2 = new Barco("Submarino", 3);
        panelBarcos.getChildren().add(crearBarcoPane(sub2));

        // destructores y fragatas se agregan igual...
    }

    private Pane crearBarcoPane(Barco barco) {
        Pane pane = new Pane();
        pane.setPrefSize(barco.getTamaño() * 30, 30);
        pane.getStyleClass().add("barco");

        pane.setOnDragDetected(e -> pane.startFullDrag());
        return pane;
    }

    private void colocarBarco(int fila, int col, Pane celda) {
        if (panelBarcos.getChildren().isEmpty()) return;

        Pane barcoPane = (Pane) panelBarcos.getChildren().get(0);
        int tamaño = (int) (barcoPane.getPrefWidth() / 30);
        Barco barco = new Barco("Portaaviones", tamaño);

        if (jugador.getTablero().colocarBarco(barco, fila, col, true)) {
            celda.getStyleClass().add("barco");
            panelBarcos.getChildren().remove(barcoPane);
        } else {
            celda.setStyle("-fx-background-color: red;");
        }
    }

    // HU-2: disparos
    private void disparar(int fila, int col, Pane celda) {
        Barco[][] t = maquina.getTablero().getTablero();
        Barco b = t[fila][col];

        if (b == null) {
            celda.getStyleClass().add("agua");
        } else {
            boolean tocado = false;
            for (int i = 0; i < b.getTamaño(); i++) {
                if (b.disparar(i)) {
                    tocado = true;
                    break;
                }
            }
            if (b.estaHundido()) {
                celda.getStyleClass().add("hundido");
            } else if (tocado) {
                celda.getStyleClass().add("tocado");
            }
        }
        celda.setDisable(true);
    }

    // HU-2: colocar barcos de la máquina
    private void colocarBarcosMaquina() {
        // Ejemplo: 1 portaaviones
        Barco porta = new Barco("Portaaviones", 4);
        colocarBarcoAleatorio(maquina, porta);

        // 2 submarinos
        colocarBarcoAleatorio(maquina, new Barco("Submarino", 3));
        colocarBarcoAleatorio(maquina, new Barco("Submarino", 3));

        // 3 destructores
        colocarBarcoAleatorio(maquina, new Barco("Destructor", 2));
        colocarBarcoAleatorio(maquina, new Barco("Destructor", 2));
        colocarBarcoAleatorio(maquina, new Barco("Destructor", 2));

        // 4 fragatas
        colocarBarcoAleatorio(maquina, new Barco("Fragata", 1));
        colocarBarcoAleatorio(maquina, new Barco("Fragata", 1));
        colocarBarcoAleatorio(maquina, new Barco("Fragata", 1));
        colocarBarcoAleatorio(maquina, new Barco("Fragata", 1));
    }

    private void colocarBarcoAleatorio(Jugador jugador, Barco barco) {
        boolean colocado = false;
        while (!colocado) {
            int fila = (int) (Math.random() * 10);
            int col = (int) (Math.random() * 10);
            boolean horizontal = Math.random() > 0.5;
            colocado = jugador.getTablero().colocarBarco(barco, fila, col, horizontal);
        }
    }
}

