package com.batallanaval.batallanaval.patterns.observer;

import com.batallanaval.model.Jugador;
import com.batallanaval.batallanaval.utils.ArchivoManager;

/**
 * Observador que guarda automáticamente el estado del juego.
 * Implementa HU-5: Guardado automático del juego.
 *
 * @author
 * @version 1.0
 */
public class ObservadorGuardado implements ObservadorJuego {
    private ArchivoManager archivoManager;
    private int contadorGuardados = 0;
    private static final int MAX_GUARDADOS_SIMULTANEOS = 10;

    public ObservadorGuardado() {
        this.archivoManager = new ArchivoManager();
    }

    @Override
    public void actualizar(String evento, Jugador jugador, Object datos) {
        // Eventos que activan el guardado automático
        if (debeGuardar(evento)) {
            contadorGuardados++;

            // Evitar guardados demasiado frecuentes
            if (contadorGuardados % 2 == 0) { // Guardar cada 2 eventos importantes
                try {
                    System.out.println("💾 Guardado automático #" + contadorGuardados +
                            " por evento: " + evento);

                    // Aquí deberíamos tener acceso a ambos jugadores y estado del juego
                    // Por ahora, solo registramos el intento
                    registrarIntentoGuardado(evento, jugador, datos);

                } catch (Exception e) {
                    System.err.println("❌ Error en guardado automático: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Determina si un evento debe activar el guardado automático.
     */
    private boolean debeGuardar(String evento) {
        return evento.equals("DISPARO_REALIZADO") ||
                evento.equals("BARCO_COLOCADO") ||
                evento.equals("JUEGO_INICIADO") ||
                evento.equals("JUEGO_TERMINADO") ||
                evento.equals("TURNO_CAMBIADO") ||
                evento.equals("BARCOS_COLOCADOS_COMPLETOS") ||
                evento.equals("JUEGO_REINICIADO");
    }

    /**
     * Registra el intento de guardado (simulación hasta que tengamos acceso completo al juego).
     */
    private void registrarIntentoGuardado(String evento, Jugador jugador, Object datos) {
        String mensaje = String.format(
                "Intento de guardado - Evento: %s, Jugador: %s, Datos: %s",
                evento,
                jugador != null ? jugador.getNickname() : "N/A",
                datos != null ? datos.toString() : "N/A"
        );

        System.out.println("📁 [SIMULACIÓN GUARDADO] " + mensaje);

        // TODO: Implementar guardado real cuando JuegoController proporcione
        // acceso a ambos jugadores y estado del juego
        // Ejemplo futuro:
        // archivoManager.guardarJuegoCompleto(jugadorHumano, jugadorMaquina, juegoIniciado, turnoJugador);
    }

    /**
     * Método para guardar manualmente (será llamado desde JuegoController).
     *
     * @param jugadorHumano Jugador humano
     * @param jugadorMaquina Jugador máquina
     * @param juegoIniciado Estado del juego
     * @param turnoJugador De quién es el turno
     */
    public void guardarManual(Jugador jugadorHumano, Jugador jugadorMaquina,
                              boolean juegoIniciado, boolean turnoJugador) {
        try {
            archivoManager.guardarJuegoCompleto(
                    jugadorHumano, jugadorMaquina, juegoIniciado, turnoJugador
            );
            System.out.println("💾 Guardado manual realizado");
        } catch (Exception e) {
            System.err.println("❌ Error en guardado manual: " + e.getMessage());
        }
    }

    /**
     * Método para cargar juego guardado.
     *
     * @return Estado del juego cargado o null si hay error
     */
    public ArchivoManager.EstadoJuego cargarJuego() {
        try {
            return archivoManager.cargarUltimoJuego();
        } catch (Exception e) {
            System.err.println("❌ Error al cargar juego: " + e.getMessage());
            return null;
        }
    }

    /**
     * Verifica si hay juegos guardados.
     */
    public boolean existeJuegoGuardado() {
        return archivoManager.existeJuegoGuardado();
    }

    /**
     * Obtiene estadísticas guardadas.
     */
    public String obtenerEstadisticas() {
        try {
            return archivoManager.cargarEstadisticas();
        } catch (Exception e) {
            return "Error al cargar estadísticas: " + e.getMessage();
        }
    }

    public int getContadorGuardados() {
        return contadorGuardados;
    }

    @Override
    public String toString() {
        return "ObservadorGuardado{guardados=" + contadorGuardados + "}";
    }
}