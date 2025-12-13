package com.batallanaval.batallanaval.exceptions;

/**
 * Excepción NO MARCADA para cuando un barco se coloca fuera de los límites del tablero.
 *
 *
 * @version 1.0
 */
public class BarcoFueraLimitesException extends RuntimeException {
    private static final long serialVersionUID = 4L;

    private final String tipoBarco;
    private final int tamanho;
    private final int fila;
    private final int columna;
    private final boolean horizontal;

    /**
     * Constructor con todos los detalles.
     *
     * @param tipoBarco Tipo de barco
     * @param tamanho tamanho del barco
     * @param fila Fila donde se intentó colocar
     * @param columna Columna donde se intentó colocar
     * @param horizontal Orientación del barco
     */
    public BarcoFueraLimitesException(String tipoBarco, int tamanho, int fila,
                                      int columna, boolean horizontal) {
        super(String.format("%s (tamanho %d) no cabe en (%d, %d) %s",
                tipoBarco, tamanho, fila, columna,
                horizontal ? "horizontal" : "vertical"));
        this.tipoBarco = tipoBarco;
        this.tamanho = tamanho;
        this.fila = fila;
        this.columna = columna;
        this.horizontal = horizontal;
    }

    /**
     * @return Tipo de barco
     */
    public String getTipoBarco() {
        return tipoBarco;
    }

    /**
     * @return tamanho del barco
     */
    public int gettamanho() {
        return tamanho;
    }

    /**
     * @return Fila del intento fallido
     */
    public int getFila() {
        return fila;
    }

    /**
     * @return Columna del intento fallido
     */
    public int getColumna() {
        return columna;
    }

    /**
     * @return true si era horizontal, false si vertical
     */
    public boolean isHorizontal() {
        return horizontal;
    }

    @Override
    public String toString() {
        return String.format(
                "BarcoFueraLimitesException{tipoBarco=%s, tamanho=%d, fila=%d, columna=%d, horizontal=%b}",
                tipoBarco, tamanho, fila, columna, horizontal
        );
    }
}