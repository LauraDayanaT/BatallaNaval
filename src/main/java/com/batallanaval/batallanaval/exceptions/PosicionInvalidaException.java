package com.batallanaval.batallanaval.exceptions;

/**
 * Excepción NO MARCADA (RuntimeException) que se lanza cuando
 * se intenta acceder a una posición inválida en el tablero.
 *
 *
 * @version 1.0
 */
public class PosicionInvalidaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final int fila;
    private final int columna;

    /**
     * Constructor con mensaje personalizado.
     *
     * @param mensaje Mensaje descriptivo del error
     * @param fila Fila inválida
     * @param columna Columna inválida
     */
    public PosicionInvalidaException(String mensaje, int fila, int columna) {
        super(mensaje);
        this.fila = fila;
        this.columna = columna;
    }

    /**
     * Constructor con fila y columna.
     *
     * @param fila Fila inválida
     * @param columna Columna inválida
     */
    public PosicionInvalidaException(int fila, int columna) {
        super(String.format("Posición inválida: (%d, %d)", fila, columna));
        this.fila = fila;
        this.columna = columna;
    }

    /**
     * @return Fila inválida
     */
    public int getFila() {
        return fila;
    }

    /**
     * @return Columna inválida
     */
    public int getColumna() {
        return columna;
    }

    @Override
    public String toString() {
        return String.format("PosicionInvalidaException{fila=%d, columna=%d, mensaje=%s}",
                fila, columna, getMessage());
    }
}