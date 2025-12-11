package com.batallanaval.batallanaval.patterns.observer;

import com.batallanaval.batallanaval.model.Jugador;
import java.util.ArrayList;
import java.util.List;

/**
 * Sujeto (Subject) que mantiene una lista de observadores
 * y los notifica cuando ocurren cambios.
 */
public class SujetoJuego {
    private List<ObservadorJuego> observadores;

    public SujetoJuego() {
        this.observadores = new ArrayList<>();
    }

    /**
     * Agrega un observador a la lista.
     *
     * @param observador Observador a agregar
     */
    public void agregarObservador(ObservadorJuego observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    /**
     * Elimina un observador de la lista.
     *
     * @param observador Observador a eliminar
     */
    public void eliminarObservador(ObservadorJuego observador) {
        observadores.remove(observador);
    }

    /**
     * Notifica a todos los observadores sobre un evento.
     *
     * @param evento Tipo de evento
     * @param jugador Jugador involucrado (puede ser null)
     * @param datos Datos adicionales
     */
    public void notificarObservadores(String evento, Jugador jugador, Object datos) {
        for (ObservadorJuego observador : observadores) {
            observador.actualizar(evento, jugador, datos);
        }
    }

    /**
     * @return Cantidad de observadores registrados
     */
    public int cantidadObservadores() {
        return observadores.size();
    }

    /**
     * Limpia todos los observadores.
     */
    public void limpiarObservadores() {
        observadores.clear();
    }
}