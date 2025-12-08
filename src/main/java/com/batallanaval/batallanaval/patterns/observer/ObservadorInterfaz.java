package com.batallanaval.batallanaval.patterns.observer;

import com.batallanaval.model.Jugador;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Observador que actualiza la interfaz gráfica.
 * Se ejecuta en el hilo de JavaFX (Platform.runLater).
 */
public class ObservadorInterfaz implements ObservadorJuego {
    private String nombre;

    public ObservadorInterfaz(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public void actualizar(String evento, Jugador jugador, Object datos) {
        // Ejecutar en el hilo de JavaFX
        Platform.runLater(() -> {
            switch (evento) {
                case "JUEGO_INICIADO":
                    mostrarNotificacion("🎮 Juego Iniciado",
                            "¡Que comience la batalla!", AlertType.INFORMATION);
                    break;

                case "BARCO_COLOCADO":
                    if (datos instanceof String) {
                        mostrarNotificacion("🚢 Barco Colocado",
                                "Barco " + datos + " colocado exitosamente", AlertType.INFORMATION);
                    }
                    break;

                case "DISPARO_REALIZADO":
                    if (datos instanceof String) {
                        String resultado = (String) datos;
                        String titulo = resultado.equals("HUNDIDO") ? "💥 ¡HUNDIDO!" :
                                resultado.equals("TOCADO") ? "🔥 ¡TOCADO!" :
                                        "🌊 AGUA";
                        mostrarNotificacion(titulo,
                                "Disparo: " + resultado, AlertType.INFORMATION);
                    }
                    break;

                case "JUEGO_TERMINADO":
                    if (datos instanceof String) {
                        mostrarNotificacion("🏆 Fin del Juego",
                                (String) datos, AlertType.INFORMATION);
                    }
                    break;

                case "ERROR":
                    if (datos instanceof String) {
                        mostrarNotificacion("❌ Error",
                                (String) datos, AlertType.ERROR);
                    }
                    break;
            }
        });
    }

    /**
     * Muestra una notificación en pantalla.
     */
    private void mostrarNotificacion(String titulo, String mensaje, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.show();
    }

    public String getNombre() {
        return nombre;
    }
}