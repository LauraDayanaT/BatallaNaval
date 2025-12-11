package com.batallanaval.batallanaval.patterns.observer;

import com.batallanaval.batallanaval.model.Jugador;

/**
 * Observador que muestra eventos en la consola.
 * Útil para depuración y registro de partidas.
 */
public class ObservadorConsola implements ObservadorJuego {
    private String nombre;

    public ObservadorConsola(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public void actualizar(String evento, Jugador jugador, Object datos) {
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("[").append(nombre).append("] ");
        mensaje.append("Evento: ").append(evento);

        if (jugador != null) {
            mensaje.append(" | Jugador: ").append(jugador.getNickname());
        }

        if (datos != null) {
            mensaje.append(" | Datos: ").append(datos.toString());
        }

        System.out.println(mensaje.toString());

        // También podríamos registrar en un archivo de log
        registrarEnLog(mensaje.toString());
    }

    /**
     * Registra el evento en un archivo de log (opcional).
     */
    private void registrarEnLog(String mensaje) {
        // TODO: Implementar registro en archivo
        // Para HU-5: Guardado automático
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return "ObservadorConsola{" + "nombre='" + nombre + "'}";
    }
}