package com.batallanaval.batallanaval.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa un tablero de juego de Batalla Naval.
 * Implementa el concepto de TDA con encapsulamiento completo y utiliza
 * una clase interna para representar las celdas del tablero.
 *
 * @version 1.0
 */
public class Tablero implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int FILAS = 10;
    private static final int COLUMNAS = 10;

    private Celda[][] celdas;      // Matriz de celdas usando clase interna
    private List<Barco> barcos;    // Lista de barcos colocados

    /**
     * ENUM para el estado de una celda del tablero.
     * Cumple con requisito de usar ENUM para mejor control.
     */
    public enum EstadoCelda {
        AGUA_LIBRE("~", "Agua libre"),           // Agua sin disparar
        AGUA_DISPARADA("X", "Agua disparada"),   // Disparo al agua
        BARCO_INTACTO("B", "Barco intacto"),     // Barco sin tocar
        BARCO_TOCADO("T", "Barco tocado"),       // Barco tocado
        BARCO_HUNDIDO("H", "Barco hundido");     // Barco hundido

        private final String simbolo;
        private final String descripcion;

        EstadoCelda(String simbolo, String descripcion) {
            this.simbolo = simbolo;
            this.descripcion = descripcion;
        }

        public String getSimbolo() {
            return simbolo;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    /**
     * CLASE INTERNA Celda - Representa una celda individual del tablero.
     * Cumple con el requisito de usar clases internas/anidadas.
     * Implementa Serializable para poder guardar el estado del juego.
     */
    public class Celda implements Serializable {
        private static final long serialVersionUID = 2L;

        private EstadoCelda estado;
        private Barco barco;              // Referencia al barco en esta celda (null si no hay)
        private int posicionEnBarco;      // Posición relativa en el barco (-1 si no hay barco)

        /**
         * Constructor por defecto de Celda.
         * Inicializa como agua libre sin barco.
         */
        public Celda() {
            this.estado = EstadoCelda.AGUA_LIBRE;
            this.barco = null;
            this.posicionEnBarco = -1;
        }

        // ========== GETTERS ==========

        public EstadoCelda getEstado() {
            return estado;
        }

        public Barco getBarco() {
            return barco;
        }

        public int getPosicionEnBarco() {
            return posicionEnBarco;
        }

        public boolean tieneBarco() {
            return barco != null;
        }

        public boolean estaDisparada() {
            return estado == EstadoCelda.AGUA_DISPARADA ||
                    estado == EstadoCelda.BARCO_TOCADO ||
                    estado == EstadoCelda.BARCO_HUNDIDO;
        }

        public boolean tieneBarcoIntacto() {
            return barco != null && estado == EstadoCelda.BARCO_INTACTO;
        }

        // ========== SETTERS ==========

        public void setEstado(EstadoCelda estado) {
            this.estado = estado;
        }

        /**
         * Coloca un barco en esta celda.
         *
         * @param barco Barco a colocar
         * @param posicionEnBarco Posición relativa en el barco (0 = inicio, 1, 2, ...)
         */
        public void colocarBarco(Barco barco, int posicionEnBarco) {
            if (barco == null) {
                throw new IllegalArgumentException("El barco no puede ser null");
            }
            this.barco = barco;
            this.posicionEnBarco = posicionEnBarco;
            this.estado = EstadoCelda.BARCO_INTACTO;
        }

        /**
         * Limpia la celda (quita cualquier barco).
         */
        public void limpiar() {
            this.barco = null;
            this.posicionEnBarco = -1;
            this.estado = EstadoCelda.AGUA_LIBRE;
        }

        /**
         * Recibe un disparo en esta celda.
         * @return Resultado del disparo: "AGUA", "TOCADO", "HUNDIDO", o "REPETIDO"
         */
        public String recibirDisparo() {
            // Si ya fue disparada
            if (estaDisparada()) {
                return "REPETIDO";
            }

            // Disparo al agua
            if (!tieneBarco()) {
                this.estado = EstadoCelda.AGUA_DISPARADA;
                return "AGUA";
            }

            // Disparo a barco
            if (barco.recibirDisparo(posicionEnBarco)) {
                if (barco.estaHundido()) {
                    this.estado = EstadoCelda.BARCO_HUNDIDO;
                    return "HUNDIDO";
                } else {
                    this.estado = EstadoCelda.BARCO_TOCADO;
                    return "TOCADO";
                }
            }

            return "REPETIDO"; // No debería llegar aquí
        }

        @Override
        public String toString() {
            return String.format("Celda[estado=%s, barco=%s, pos=%d]",
                    estado.getSimbolo(),
                    barco != null ? barco.getNombre() : "null",
                    posicionEnBarco);
        }
    }

    // ========== CONSTRUCTORES DE TABLERO ==========

    public Tablero() {
        this.celdas = new Celda[FILAS][COLUMNAS];
        this.barcos = new ArrayList<>();
        inicializarCeldas();
    }

    /**
     * Constructor de copia (para TDA-friendly).
     */
    public Tablero(Tablero otro) {
        this.celdas = new Celda[FILAS][COLUMNAS];
        this.barcos = new ArrayList<>(otro.barcos);
        inicializarCeldas();

        // Copiar estado de cada celda
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                Celda celdaOrigen = otro.celdas[i][j];
                if (celdaOrigen.tieneBarco()) {
                    this.celdas[i][j].colocarBarco(
                            celdaOrigen.getBarco(),
                            celdaOrigen.getPosicionEnBarco()
                    );
                    this.celdas[i][j].setEstado(celdaOrigen.getEstado());
                }
            }
        }
    }

    // ========== MÉTODOS PRIVADOS ==========

    private void inicializarCeldas() {
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                celdas[i][j] = new Celda();
            }
        }
    }

    // ========== MÉTODOS PÚBLICOS ==========

    /**
     * Coloca un barco en el tablero en la posición especificada.
     */
    public boolean colocarBarco(Barco barco, int fila, int col, boolean horizontal) {
        // Validar parámetros
        if (barco == null) {
            throw new IllegalArgumentException("El barco no puede ser null");
        }
        if (!estaEnLimites(fila, col)) {
            return false;
        }

        // Validar que el barco quepa en la dirección especificada
        if (horizontal) {
            if (col + barco.gettamanho() > COLUMNAS) {
                return false; // Fuera de límites
            }
            // Verificar superposición
            for (int i = 0; i < barco.gettamanho(); i++) {
                if (celdas[fila][col + i].tieneBarco()) {
                    return false; // Superposición
                }
            }
            // Colocar barco
            for (int i = 0; i < barco.gettamanho(); i++) {
                celdas[fila][col + i].colocarBarco(barco, i);
            }
        } else {
            if (fila + barco.gettamanho() > FILAS) {
                return false; // Fuera de límites
            }
            // Verificar superposición
            for (int i = 0; i < barco.gettamanho(); i++) {
                if (celdas[fila + i][col].tieneBarco()) {
                    return false; // Superposición
                }
            }
            // Colocar barco
            for (int i = 0; i < barco.gettamanho(); i++) {
                celdas[fila + i][col].colocarBarco(barco, i);
            }
        }

        barcos.add(barco);
        return true;
    }

    public String recibirDisparo(int fila, int columna) {
        if (!estaEnLimites(fila, columna)) {
            return "INVALIDO";
        }

        Celda celda = celdas[fila][columna];
        return celda.recibirDisparo();
    }

    public void registrarDisparo(int fila, int columna, String resultado) {
        if (!estaEnLimites(fila, columna)) {
            return;
        }

        Celda celda = celdas[fila][columna];
        switch (resultado.toUpperCase()) {
            case "AGUA":
                celda.setEstado(EstadoCelda.AGUA_DISPARADA);
                break;
            case "TOCADO":
                celda.setEstado(EstadoCelda.BARCO_TOCADO);
                break;
            case "HUNDIDO":
                celda.setEstado(EstadoCelda.BARCO_HUNDIDO);
                break;
        }
    }

    public boolean todosBarcosHundidos() {
        for (Barco barco : barcos) {
            if (!barco.estaHundido()) {
                return false;
            }
        }
        return true;
    }

    public Barco getBarcoEn(int fila, int col) {
        if (!estaEnLimites(fila, col)) {
            return null;
        }
        return celdas[fila][col].getBarco();
    }

    public boolean estaEnLimites(int fila, int col) {
        return fila >= 0 && fila < FILAS && col >= 0 && col < COLUMNAS;
    }

    public boolean estaDisparada(int fila, int col) {
        if (!estaEnLimites(fila, col)) {
            return false;
        }
        return celdas[fila][col].estaDisparada();
    }

    // ========== GETTERS PÚBLICOS ==========

    public int getFilas() {
        return FILAS;
    }

    public int getColumnas() {
        return COLUMNAS;
    }

    public Celda[][] getCeldas() {
        Celda[][] copia = new Celda[FILAS][COLUMNAS];
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                copia[i][j] = celdas[i][j];
            }
        }
        return copia;
    }

    public Celda getCelda(int fila, int col) {
        if (!estaEnLimites(fila, col)) {
            throw new IndexOutOfBoundsException(
                    String.format("Posición (%d, %d) fuera de límites. Límites: (0-%d, 0-%d)",
                            fila, col, FILAS-1, COLUMNAS-1)
            );
        }
        return celdas[fila][col];
    }

    public List<Barco> getBarcos() {
        return new ArrayList<>(barcos);
    }

    // ========== MÉTODOS DE VISUALIZACIÓN ==========

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Encabezado de columnas (A-J)
        sb.append("   ");
        for (int j = 0; j < COLUMNAS; j++) {
            sb.append((char) ('A' + j)).append(" ");
        }
        sb.append("\n");

        // Filas con números
        for (int i = 0; i < FILAS; i++) {
            sb.append(String.format("%2d ", i + 1));
            for (int j = 0; j < COLUMNAS; j++) {
                sb.append(celdas[i][j].getEstado().getSimbolo()).append(" ");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    public String toStringConBarcos() {
        StringBuilder sb = new StringBuilder();

        sb.append("   ");
        for (int j = 0; j < COLUMNAS; j++) {
            sb.append((char) ('A' + j)).append(" ");
        }
        sb.append("\n");

        for (int i = 0; i < FILAS; i++) {
            sb.append(String.format("%2d ", i + 1));
            for (int j = 0; j < COLUMNAS; j++) {
                Celda celda = celdas[i][j];
                if (celda.tieneBarco()) {
                    sb.append(celda.getEstado().getSimbolo()).append(" ");
                } else {
                    sb.append(celda.getEstado().getSimbolo()).append(" ");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}