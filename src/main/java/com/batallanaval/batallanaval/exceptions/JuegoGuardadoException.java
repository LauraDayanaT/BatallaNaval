package com.batallanaval.batallanaval.exceptions;

/**
 * Excepción MARCADA para errores relacionados con guardado/carga del juego.
 * Para HU-5 y HU-6: guardado automático y carga de juego.
 *
 *
 * @version 1.0
 */
public class JuegoGuardadoException extends Exception {
    private static final long serialVersionUID = 3L;

    private final String archivo;
    private final String operacion; // "GUARDAR" o "CARGAR"

    /**
     * Constructor con detalles de la operación fallida.
     *
     * @param mensaje Mensaje de error
     * @param archivo Nombre del archivo involucrado
     * @param operación Operación que falló ("GUARDAR" o "CARGAR")
     */
    public JuegoGuardadoException(String mensaje, String archivo, String operacion) {
        super(mensaje);
        this.archivo = archivo;
        this.operacion = operacion;
    }

    /**
     * Constructor simple.
     *
     * @param mensaje Mensaje de error
     */
    public JuegoGuardadoException(String mensaje) {
        super(mensaje);
        this.archivo = "desconocido";
        this.operacion = "desconocida";
    }

    /**
     * @return Nombre del archivo que causó el error
     */
    public String getArchivo() {
        return archivo;
    }

    /**
     * @return Operación que falló
     */
    public String getOperacion() {
        return operacion;
    }

    @Override
    public String toString() {
        return String.format("JuegoGuardadoException{archivo=%s, operacion=%s, mensaje=%s}",
                archivo, operacion, getMessage());
    }
}