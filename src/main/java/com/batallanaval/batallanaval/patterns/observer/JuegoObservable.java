package com.batallanaval.batallanaval.patterns.observer;

import com.batallanaval.batallanaval.model.Jugador;

/**
 * Clase que combina el SujetoJuego con constantes de eventos.
 * Facilita el uso del patrón Observer en el juego.
 */
public class JuegoObservable extends SujetoJuego {

    // ========== CONSTANTES DE EVENTOS ==========

    // Eventos de colocación de barcos
    public static final String BARCO_COLOCADO = "BARCO_COLOCADO";
    public static final String BARCO_MOVIDO = "BARCO_MOVIDO";
    public static final String BARCOS_COLOCADOS_COMPLETOS = "BARCOS_COLOCADOS_COMPLETOS";

    // Eventos de disparos
    public static final String DISPARO_REALIZADO = "DISPARO_REALIZADO";
    public static final String DISPARO_AGUA = "DISPARO_AGUA";
    public static final String DISPARO_TOCADO = "DISPARO_TOCADO";
    public static final String DISPARO_HUNDIDO = "DISPARO_HUNDIDO";

    // Eventos de turnos
    public static final String TURNO_CAMBIADO = "TURNO_CAMBIADO";
    public static final String TURNO_JUGADOR = "TURNO_JUGADOR";
    public static final String TURNO_MAQUINA = "TURNO_MAQUINA";

    // Eventos de juego
    public static final String JUEGO_INICIADO = "JUEGO_INICIADO";
    public static final String JUEGO_PAUSADO = "JUEGO_PAUSADO";
    public static final String JUEGO_REINICIADO = "JUEGO_REINICIADO";
    public static final String JUEGO_TERMINADO = "JUEGO_TERMINADO";

    // Eventos de resultados
    public static final String JUGADOR_GANO = "JUGADOR_GANO";
    public static final String MAQUINA_GANO = "MAQUINA_GANO";
    public static final String EMPATE = "EMPATE";

    // Eventos de error
    public static final String ERROR = "ERROR";
    public static final String ADVERTENCIA = "ADVERTENCIA";
    public static final String INFORMACION = "INFORMACION";

    // ========== MÉTODOS CONVENCIONALES ==========

    /**
     * Notifica que un barco fue colocado.
     */
    public void notificarBarcoColocado(Jugador jugador, String nombreBarco, int fila, int columna) {
        String datos = String.format("%s en (%d,%d)", nombreBarco, fila, columna);
        notificarObservadores(BARCO_COLOCADO, jugador, datos);
    }

    /**
     * Notifica que se realizó un disparo.
     */
    public void notificarDisparo(Jugador jugador, String resultado, int fila, int columna) {
        String datos = String.format("%s en (%d,%d)", resultado, fila, columna);
        notificarObservadores(DISPARO_REALIZADO, jugador, datos);

        // Notificar evento específico según resultado
        switch (resultado) {
            case "AGUA":
                notificarObservadores(DISPARO_AGUA, jugador, datos);
                break;
            case "TOCADO":
                notificarObservadores(DISPARO_TOCADO, jugador, datos);
                break;
            case "HUNDIDO":
                notificarObservadores(DISPARO_HUNDIDO, jugador, datos);
                break;
        }
    }

    /**
     * Notifica el inicio del juego.
     */
    public void notificarJuegoIniciado() {
        notificarObservadores(JUEGO_INICIADO, null, "El juego ha comenzado");
    }

    /**
     * Notifica el fin del juego.
     */
    public void notificarJuegoTerminado(String ganador) {
        notificarObservadores(JUEGO_TERMINADO, null, "Ganador: " + ganador);

        if (ganador.contains("Humano")) {
            notificarObservadores(JUGADOR_GANO, null, "¡El jugador humano ganó!");
        } else {
            notificarObservadores(MAQUINA_GANO, null, "La máquina ganó");
        }
    }

    /**
     * Notifica un error.
     */
    public void notificarError(String mensajeError) {
        notificarObservadores(ERROR, null, mensajeError);
    }

    /**
     * Notifica un cambio de turno.
     */
    public void notificarCambioTurno(boolean esTurnoJugador) {
        String datos = esTurnoJugador ? "Turno del jugador" : "Turno de la máquina";
        notificarObservadores(TURNO_CAMBIADO, null, datos);

        if (esTurnoJugador) {
            notificarObservadores(TURNO_JUGADOR, null, datos);
        } else {
            notificarObservadores(TURNO_MAQUINA, null, datos);
        }
    }
}