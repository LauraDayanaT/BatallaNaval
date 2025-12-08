package com.batallanaval.batallanaval.patterns.observer;

import com.batallanaval.model.Jugador;

/**
 * Patrón de diseño COMPORTAMIENTO: Observer
 * Interfaz para objetos que observan cambios en el juego.
 *
 *
 * @version 1.0
 */
public interface ObservadorJuego {

    /**
     * Método llamado cuando ocurre un cambio en el juego.
     *
     * @param evento Tipo de evento que ocurrió
     * @param jugador Jugador que realizó la acción (puede ser null)
     * @param datos Datos adicionales del evento
     */
    void actualizar(String evento, Jugador jugador, Object datos);
}