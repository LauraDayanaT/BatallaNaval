package com.batallanaval.batallanaval.exceptions;

/**
 * Excepción MARCADA (Checked Exception) que se lanza cuando
 * se intenta colocar un barco en una posición ya ocupada.
 *
 *
 * @version 1.0
 */
public class BarcoSuperpuestoException extends Exception {
    private static final long serialVersionUID = 2L;

    private final String tipoBarco;
    private final int fila;
    private final int columna;

    /**
     * Constructor con detalles del error.
     *
     * @param tipoBarco Tipo de barco que causó la superposición
     * @param fila Fila donde se intentó colocar
     * @param columna Columna donde se intentó colocar
     */
    public BarcoSuperpuestoException(String tipoBarco, int fila, int columna) {
        super(String.format("No se puede colocar %s en (%d, %d): posición ya ocupada",
                tipoBarco, fila, columna));
        this.tipoBarco = tipoBarco;
        this.fila = fila;
        this.columna = columna;
    }

    /**
     * Constructor con mensaje personalizado.
     *
     * @param mensaje Mensaje descriptivo
     * @param tipoBarco Tipo de barco
     * @param fila Fila
     * @param columna Columna
     */
    public BarcoSuperpuestoException(String mensaje, String tipoBarco, int fila, int columna) {
        super(mensaje);
        this.tipoBarco = tipoBarco;
        this.fila = fila;
        this.columna = columna;
    }

    /**
     * @return Tipo de barco que causó la excepción
     */
    public String getTipoBarco() {
        return tipoBarco;
    }

    /**
     * @return Fila del conflicto
     */
    public int getFila() {
        return fila;
    }

    /**
     * @return Columna del conflicto
     */
    public int getColumna() {
        return columna;
    }

    @Override
    public String toString() {
        return String.format("BarcoSuperpuestoException{tipoBarco=%s, fila=%d, columna=%d}",
                tipoBarco, fila, columna);
    }
}