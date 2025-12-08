package com.batallanaval.batallanaval.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Priority; // Importar Priority
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Pos;
import javafx.geometry.HPos; // Importar HPos
import javafx.geometry.VPos; // Importar VPos

public class tableroJugadorController {

    @FXML
    private GridPane GridPanel100; // El fx:id de tu GridPane

    // Dimensiones del tablero de juego (10x10 casillas de juego + 1 fila/columna para encabezados)
    private static final int TOTAL_SIZE = 11;
    private static final int GRID_SIZE = 10;

    // TAMAÑO ACTUALIZADO: 60px por celda de juego (antes 50px). 10 celdas * 60px = 600px.
    private static final double CELL_SIZE = 60.0;
    // Tamaño definido para la fila/columna de encabezados (40x40).
    private static final double HEADER_SIZE = 40.0;
    // Tamaño total del GridPane: 600px (juego) + 40px (encabezado) = 640px.

    // Array de letras para los encabezados de columna
    private static final String[] COL_HEADERS = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};

    @FXML
    public void initialize() {
        // 1. Configurar dinámicamente las ColumnConstraints (11 en total)
        for (int i = 0; i < TOTAL_SIZE; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            if (i == 0) {
                // La primera columna (para los números) tiene un ancho fijo de 40px.
                cc.setPrefWidth(HEADER_SIZE);
                cc.setHgrow(Priority.NEVER); // No crece
                cc.setHalignment(HPos.CENTER);
            } else {
                // Las columnas de juego (A-J) tienen un ancho fijo de 60px.
                cc.setPrefWidth(CELL_SIZE);
                cc.setHgrow(Priority.SOMETIMES); // Puede crecer si es necesario, pero el PrefWidth es fijo.
                cc.setHalignment(HPos.CENTER);
            }
            GridPanel100.getColumnConstraints().add(cc);
        }

        // 2. Configurar dinámicamente las RowConstraints (11 en total)
        for (int i = 0; i < TOTAL_SIZE; i++) {
            RowConstraints rc = new RowConstraints();
            if (i == 0) {
                // La primera fila (para las letras) tiene una altura fija de 40px.
                rc.setPrefHeight(HEADER_SIZE);
                rc.setVgrow(Priority.NEVER); // No crece
                rc.setValignment(VPos.CENTER);
            } else {
                // Las filas de juego (1-10) tienen una altura fija de 60px.
                rc.setPrefHeight(CELL_SIZE);
                rc.setVgrow(Priority.SOMETIMES); // Puede crecer si es necesario, pero el PrefHeight es fijo.
                rc.setValignment(VPos.CENTER);
            }
            GridPanel100.getRowConstraints().add(rc);
        }

        // 3. Crear los encabezados de columna (A-J)
        for (int c = 0; c < GRID_SIZE; c++) {
            Label header = new Label(COL_HEADERS[c]);
            header.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            header.setTextFill(Color.WHITE);
            header.setStyle("-fx-effect: dropshadow(gaussian, black, 3, 0.5, 0, 0);");

            GridPane.setConstraints(header, c + 1, 0); // Posición: columna (1 a 10), fila 0
            GridPanel100.getChildren().add(header);
            GridPane.setHalignment(header, HPos.CENTER);
            GridPane.setValignment(header, VPos.CENTER);
        }

        // 4. Crear los encabezados de fila (1-10)
        for (int r = 0; r < GRID_SIZE; r++) {
            Label header = new Label(String.valueOf(r + 1));
            header.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            header.setTextFill(Color.WHITE);
            header.setStyle("-fx-effect: dropshadow(gaussian, black, 3, 0.5, 0, 0);");
            GridPane.setConstraints(header, 0, r + 1); // Posición: columna 0, fila (1 a 10)
            GridPanel100.getChildren().add(header);
            GridPane.setHalignment(header, HPos.CENTER);
            GridPane.setValignment(header, VPos.CENTER);
        }

        // 5. Crear las 100 casillas de juego
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                StackPane cell = createCell(r, c);
                // Posición en el GridPane (se compensa por los encabezados)
                GridPane.setConstraints(cell, c + 1, r + 1);
                GridPanel100.getChildren().add(cell);
            }
        }
    }

    /**
     * Crea un StackPane que representa una casilla del tablero, incluyendo el efecto hover.
     * @param row Fila del juego (0-9)
     * @param col Columna del juego (0-9)
     * @return El StackPane que contiene la casilla.
     */
    private StackPane createCell(int row, int col) {
        StackPane cell = new StackPane();
        // Estilo de la casilla de agua (similar al ejemplo, color celeste)
        final String BASE_STYLE = "-fx-background-color: transparent; -fx-border-color: white; -fx-border-width: 1.5;";
        // Estilo cuando el mouse está encima (un azul ligeramente más claro)
        final String HOVER_STYLE = "-fx-background-color: #004D99; -fx-border-color: #0077BE; -fx-border-width: 1.5;";

        cell.setStyle(BASE_STYLE);
        // Establecer el tamaño preferido de la celda al mismo tamaño que la restricción de fila/columna.
        cell.setPrefSize(CELL_SIZE, CELL_SIZE);
        cell.setAlignment(Pos.CENTER);

        // Evento: Al entrar el mouse, aplica el estilo HOVER_STYLE
        cell.setOnMouseEntered(event -> cell.setStyle(HOVER_STYLE));

        // Evento: Al salir el mouse, regresa al estilo BASE_STYLE
        cell.setOnMouseExited(event -> cell.setStyle(BASE_STYLE));

        // Evento de clic para el tablero principal (el de ataque)
        cell.setOnMouseClicked(event -> handleCellClick(row, col, cell));

        return cell;
    }

    /**
     * Maneja el evento de clic en una casilla (simulando un disparo).
     * Aquí se implementaría la lógica de 'Agua', 'Tocado' o 'Hundido'.
     * @param row Fila del disparo
     * @param col Columna del disparo
     * @param cell La casilla (StackPane) a actualizar
     */
    private void handleCellClick(int row, int col, StackPane cell) {
        String coords = COL_HEADERS[col] + (row + 1);
        System.out.println("¡Disparo en la coordenada: " + coords + "!");

        // ** Aquí va la Lógica del Juego (Agua, Tocado, Hundido) **

        // Ejemplo de visualización de "Agua" (X) si la casilla está vacía
        if (cell.getChildren().isEmpty()) {
            Label missMark = new Label("X");
            missMark.setFont(Font.font("Arial", FontWeight.BOLD, 28));
            missMark.setTextFill(Color.RED);
            cell.getChildren().add(missMark);

            // Después de un "Agua", el turno pasaría al oponente (máquina)
            // ... lógica de cambio de turno
        }
    }
}