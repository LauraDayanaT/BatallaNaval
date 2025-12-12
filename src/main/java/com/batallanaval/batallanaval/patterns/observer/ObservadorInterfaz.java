package com.batallanaval.batallanaval.patterns.observer;

import com.batallanaval.batallanaval.model.Jugador;
import javafx.application.Platform;
import java.util.function.Consumer;

/**
 * Observador que actualiza la interfaz gráfica.
 * Utiliza un callback para enviar mensajes al Label del JuegoController.
 */
public class ObservadorInterfaz implements ObservadorJuego {
    private String nombre;
    private final Consumer<String> messageCallback;

    public ObservadorInterfaz(String nombre, Consumer<String> messageCallback) {
        this.nombre = nombre;
        this.messageCallback = messageCallback;
    }

    @Override
    public void actualizar(String evento, Jugador jugador, Object datos) {
        // Ejecutar en el hilo de JavaFX
        Platform.runLater(() -> {
            String mensaje = "";

            switch (evento) {

                case "JUEGO_INICIADO":
                    mensaje = "🎮 ¡Juego Iniciado! Que comience la batalla.";
                    break;

                case "BARCO_COLOCADO":
                    // El controlador (colocarBarco) ya maneja el mensaje detallado con coordenadas.
                    return;

                case "DISPARO_REALIZADO":
                    // El controlador (disparar/turnoMaquina) ya maneja los mensajes detallados con coordenadas.
                    return;

                case "JUEGO_TERMINADO":
                    if (datos instanceof String) {
                        mensaje = "🏆 Fin del Juego. Ganador: " + (String) datos + ".";
                    }
                    break;

                // Este caso es clave para la notificación de la máquina
                case "CAMBIO_TURNO":
                    boolean esTurnoJugador = (boolean) datos;
                    if(esTurnoJugador) {
                        mensaje = "🎯 ¡Es tu turno! Dispara.";
                    } else {
                        // El mensaje de turno de máquina se muestra en turnoMaquina()
                        return;
                    }
                    break;

                case "ERROR":
                    if (datos instanceof String) {
                        mensaje = "❌ ERROR: " + (String) datos;
                    }
                    break;

                case "ADVERTENCIA":
                    if (datos instanceof String) {
                        mensaje = "⚠️ ADVERTENCIA: " + (String) datos;
                    }
                    break;
            }

            // Llamamos al callback solo si hay un mensaje de evento global
            if (!mensaje.isEmpty() && messageCallback != null) {
                this.messageCallback.accept(mensaje);
            }
        });
    }

    public String getNombre() {
        return nombre;
    }
}