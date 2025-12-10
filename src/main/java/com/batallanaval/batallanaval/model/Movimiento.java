package com.batallanaval.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Clase que representa un movimiento (disparo) en el juego.
 * Se almacena en la PilaMovimientos para historial.
 *
 *
 * @version 1.0
 */
public class Movimiento implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum TipoResultado {
        AGUA, TOCADO, HUNDIDO, REPETIDO, INVALIDO
    }

    private final String jugador;          // Nombre del jugador que disparó
    private final int fila;               // Fila del disparo (0-9)
    private final int columna;            // Columna del disparo (0-9)
    private final TipoResultado resultado; // Resultado del disparo
    private final LocalDateTime fechaHora; // Cuándo se realizó
    private final boolean turnoJugador;   // true si era turno del jugador humano

    /**
     * Constructor principal.
     */
    public Movimiento(String jugador, int fila, int columna,
                      TipoResultado resultado, boolean turnoJugador) {
        if (jugador == null || jugador.trim().isEmpty()) {
            throw new IllegalArgumentException("El jugador no puede ser vacío");
        }
        if (fila < 0 || fila > 9 || columna < 0 || columna > 9) {
            throw new IllegalArgumentException(
                    "Coordenadas inválidas: (" + fila + "," + columna + ")");
        }

        this.jugador = jugador;
        this.fila = fila;
        this.columna = columna;
        this.resultado = resultado;
        this.turnoJugador = turnoJugador;
        this.fechaHora = LocalDateTime.now();
    }

    // ========== GETTERS ==========

    public String getJugador() {
        return jugador;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    public TipoResultado getResultado() {
        return resultado;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public boolean isTurnoJugador() {
        return turnoJugador;
    }

    /**
     * Convierte coordenadas numéricas a formato de tablero (A1, B5, etc).
     */
    public String getCoordenadasFormatoTablero() {
        char letraColumna = (char) ('A' + columna);
        return letraColumna + String.valueOf(fila + 1);
    }

    /**
     * Obtiene un emoji representativo del resultado.
     */
    public String getEmojiResultado() {
        switch (resultado) {
            case AGUA: return "🌊";
            case TOCADO: return "🔥";
            case HUNDIDO: return "💥";
            case REPETIDO: return "⚠️";
            case INVALIDO: return "❌";
            default: return "❓";
        }
    }

    // ========== MÉTODOS DE UTILIDAD ==========

    /**
     * Verifica si este movimiento fue exitoso (tocado o hundido).
     */
    public boolean fueExitoso() {
        return resultado == TipoResultado.TOCADO || resultado == TipoResultado.HUNDIDO;
    }

    /**
     * Verifica si este movimiento fue realizado por el jugador humano.
     */
    public boolean esDelJugadorHumano() {
        return jugador.equalsIgnoreCase("Humano");
    }

    /**
     * Verifica si este movimiento fue realizado por la máquina.
     */
    public boolean esDeLaMaquina() {
        return jugador.equalsIgnoreCase("Máquina");
    }

    @Override
    public String toString() {
        return String.format(
                "%s disparó en %s -> %s %s (Turno: %s)",
                jugador,
                getCoordenadasFormatoTablero(),
                resultado,
                getEmojiResultado(),
                turnoJugador ? "Jugador" : "Máquina"
        );
    }

    /**
     * Representación detallada para historial.
     */
    public String toDetailedString() {
        return String.format(
                "[%s] %s | Coord: %s | Resultado: %s %s | %s",
                fechaHora.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")),
                jugador,
                getCoordenadasFormatoTablero(),
                resultado,
                getEmojiResultado(),
                turnoJugador ? "Turno Jugador" : "Turno Máquina"
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Movimiento other = (Movimiento) obj;
        return this.fila == other.fila &&
                this.columna == other.columna &&
                this.jugador.equals(other.jugador) &&
                this.resultado == other.resultado;
    }

    @Override
    public int hashCode() {
        int result = jugador.hashCode();
        result = 31 * result + fila;
        result = 31 * result + columna;
        result = 31 * result + resultado.hashCode();
        return result;
    }
}