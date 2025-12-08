package com.batallanaval.batallanaval.controller;

import com.batallanaval.batallanaval.patterns.observer.JuegoObservable;
import com.batallanaval.batallanaval.patterns.observer.ObservadorConsola;
import com.batallanaval.batallanaval.patterns.observer.ObservadorGuardado;
import com.batallanaval.batallanaval.patterns.observer.ObservadorInterfaz;
import com.batallanaval.model.*;
import com.batallanaval.model.TipoBarco;
import com.batallanaval.batallanaval.patterns.factory.BarcoFactory;
import com.batallanaval.batallanaval.exceptions.PosicionInvalidaException;
import com.batallanaval.batallanaval.exceptions.BarcoFueraLimitesException;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Controlador principal del juego Batalla Naval.
 * Maneja la lógica del juego, interacción del usuario y comunicación entre modelo y vista.
 * Implementa el patrón Observer para notificaciones automáticas.
 *
 *
 * @version 3.0
 */
public class JuegoController {

    @FXML
    private GridPane tableroJugador;      // Tablero de posición del jugador humano

    @FXML
    private VBox panelBarcos;             // Panel lateral para seleccionar barcos

    @FXML
    private GridPane tableroOponente;     // Tablero principal para disparar al oponente

    private Jugador jugador;              // Jugador humano
    private Jugador maquina;              // Jugador máquina
    private boolean juegoIniciado = false;
    private boolean turnoJugador = true;  // true = turno jugador, false = turno máquina

    // ========== OBSERVER PATTERN ==========
    private JuegoObservable juegoObservable;
    private ObservadorConsola observadorConsola;
    private ObservadorInterfaz observadorInterfaz;
    private ObservadorGuardado observadorGuardado;

    // ========== INICIALIZACIÓN ==========

    @FXML
    public void initialize() {
        System.out.println("🚀 Inicializando JuegoController...");

        jugador = new Jugador("Humano");
        maquina = new Jugador("Máquina");

        // ========== INICIALIZAR OBSERVER ==========
        inicializarObservadores();
        // ==========================================

        crearTableroVisual();       // Crear tablero de posición del jugador
        crearTableroOponente();     // Crear tablero principal para disparos

        // HU-4: Colocar barcos de la máquina aleatoriamente
        maquina.colocarBarcosAleatoriamente();
        juegoObservable.notificarObservadores(
                JuegoObservable.BARCO_COLOCADO,
                maquina,
                "Máquina colocó sus barcos aleatoriamente"
        );
        System.out.println("🤖 Barcos de la máquina colocados aleatoriamente");

        crearPanelBarcos();         // Crear panel de selección de barcos

        System.out.println("✅ Juego inicializado correctamente");
        System.out.println("🎮 Jugador: " + jugador.getNickname());
        System.out.println("🤖 Máquina: " + maquina.getNickname());
    }

    /**
     * Inicializa el sistema de observadores.
     */
    private void inicializarObservadores() {
        juegoObservable = new JuegoObservable();

        // Crear observadores
        observadorConsola = new ObservadorConsola("Consola");
        observadorInterfaz = new ObservadorInterfaz("Interfaz");
        observadorGuardado = new ObservadorGuardado();

        // Registrar observadores
        juegoObservable.agregarObservador(observadorConsola);
        juegoObservable.agregarObservador(observadorInterfaz);
        juegoObservable.agregarObservador(observadorGuardado);

        System.out.println("👁️ Observadores registrados: " + juegoObservable.cantidadObservadores());
    }

    // ========== CREACIÓN DE TABLEROS VISUALES ==========

    /**
     * Crea el tablero visual 10x10 para el jugador humano.
     * HU-1: Colocación de barcos mediante arrastre.
     */
    public void crearTableroVisual() {
        System.out.println("📐 Creando tablero del jugador (10x10)...");

        for (int fila = 0; fila < 10; fila++) {
            for (int col = 0; col < 10; col++) {
                Pane celda = new Pane();
                celda.setPrefSize(40, 40);
                celda.getStyleClass().add("pane-celda");
                celda.setStyle("-fx-background-color: #87CEEB; -fx-border-color: #4682B4; -fx-border-width: 1;");

                final int f = fila, c = col;
                celda.setOnMouseDragReleased(e -> {
                    if (!juegoIniciado) {
                        colocarBarco(f, c, celda);
                    }
                });

                tableroJugador.add(celda, col, fila);
            }
        }
        System.out.println("✅ Tablero del jugador creado: 100 celdas");
    }

    /**
     * Crea el tablero 10x10 para el oponente (máquina).
     * HU-2: Disparos mediante clic.
     */
    private void crearTableroOponente() {
        System.out.println("📐 Creando tablero del oponente (10x10)...");

        for (int fila = 0; fila < 10; fila++) {
            for (int col = 0; col < 10; col++) {
                Pane celda = new Pane();
                celda.setPrefSize(40, 40);
                celda.getStyleClass().add("pane-celda");
                celda.setStyle("-fx-background-color: #87CEEB; -fx-border-color: #4682B4; -fx-border-width: 1;");

                final int f = fila, c = col;
                celda.setOnMouseClicked(e -> {
                    if (juegoIniciado && turnoJugador) {
                        disparar(f, c, celda);
                    }
                });

                tableroOponente.add(celda, col, fila);
            }
        }
        System.out.println("✅ Tablero del oponente creado: 100 celdas");
    }

    // ========== PANEL DE BARCOS ==========

    /**
     * Crea el panel lateral con todos los barcos disponibles para colocar.
     * HU-1: Colocación de barcos del jugador humano.
     */
    private void crearPanelBarcos() {
        System.out.println("🚢 Creando panel de barcos...");

        // 1 Portaaviones (tamaño 4)
        Barco portaaviones = BarcoFactory.crearBarco(TipoBarco.PORTAVIONES);
        panelBarcos.getChildren().add(crearBarcoPane(portaaviones));

        // 2 Submarinos (tamaño 3 cada uno)
        Barco sub1 = BarcoFactory.crearBarco(TipoBarco.SUBMARINO);
        panelBarcos.getChildren().add(crearBarcoPane(sub1));

        Barco sub2 = BarcoFactory.crearBarco(TipoBarco.SUBMARINO);
        panelBarcos.getChildren().add(crearBarcoPane(sub2));

        // 3 Destructores (tamaño 2 cada uno)
        Barco dest1 = BarcoFactory.crearBarco(TipoBarco.DESTRUCTOR);
        panelBarcos.getChildren().add(crearBarcoPane(dest1));

        Barco dest2 = BarcoFactory.crearBarco(TipoBarco.DESTRUCTOR);
        panelBarcos.getChildren().add(crearBarcoPane(dest2));

        Barco dest3 = BarcoFactory.crearBarco(TipoBarco.DESTRUCTOR);
        panelBarcos.getChildren().add(crearBarcoPane(dest3));

        // 4 Fragatas (tamaño 1 cada una)
        Barco frag1 = BarcoFactory.crearBarco(TipoBarco.FRAGATA);
        panelBarcos.getChildren().add(crearBarcoPane(frag1));

        Barco frag2 = BarcoFactory.crearBarco(TipoBarco.FRAGATA);
        panelBarcos.getChildren().add(crearBarcoPane(frag2));

        Barco frag3 = BarcoFactory.crearBarco(TipoBarco.FRAGATA);
        panelBarcos.getChildren().add(crearBarcoPane(frag3));

        Barco frag4 = BarcoFactory.crearBarco(TipoBarco.FRAGATA);
        panelBarcos.getChildren().add(crearBarcoPane(frag4));

        System.out.println("✅ Panel de barcos creado: 10 barcos total");
    }

    /**
     * Crea un Pane visual para representar un barco en el panel lateral.
     */
    private Pane crearBarcoPane(Barco barco) {
        Pane pane = new Pane();
        pane.setPrefSize(barco.getTamaño() * 35, 30);
        pane.setStyle("-fx-background-color: #8B4513; -fx-border-color: #654321; -fx-border-width: 2;");

        // Almacenar referencia al objeto Barco en el UserData del Pane
        pane.setUserData(barco);

        // Configurar arrastre del barco
        pane.setOnDragDetected(e -> {
            if (!juegoIniciado) {
                pane.startFullDrag();
            }
        });
        return pane;
    }

    // ========== COLOCACIÓN DE BARCOS (HU-1) ==========

    /**
     * Coloca un barco en la posición especificada del tablero del jugador.
     * HU-1: Validación de colocación de barcos.
     */
    private void colocarBarco(int fila, int col, Pane celda) {
        if (panelBarcos.getChildren().isEmpty()) {
            mostrarAlerta(AlertType.WARNING, "Sin barcos", "No hay barcos disponibles para colocar");
            return;
        }

        // Obtener el primer barco del panel lateral
        Pane barcoPane = (Pane) panelBarcos.getChildren().get(0);
        Barco barco = (Barco) barcoPane.getUserData();

        if (barco == null) {
            mostrarAlerta(AlertType.ERROR, "Error", "Barco no encontrado");
            juegoObservable.notificarError("Barco no encontrado en UserData");
            return;
        }

        try {
            // Encontrar el índice del barco en la flota
            int indiceBarco = encontrarIndiceBarco(barco);
            if (indiceBarco == -1) {
                mostrarAlerta(AlertType.ERROR, "Error", "Barco no encontrado en la flota");
                juegoObservable.notificarError("Barco no encontrado en la flota: " + barco.getNombre());
                return;
            }

            // Intentar colocar el barco en el tablero (horizontal por defecto)
            boolean colocado = jugador.colocarBarco(indiceBarco, fila, col, true);

            if (colocado) {
                // Colocación exitosa
                marcarCeldaComoBarco(celda, barco.getTamaño(), true);
                panelBarcos.getChildren().remove(barcoPane);

                // NOTIFICAR OBSERVADORES
                juegoObservable.notificarBarcoColocado(jugador, barco.getNombre(), fila, col);

                System.out.println("✅ Barco colocado: " + barco.getNombre() +
                        " en (" + fila + "," + col + ")");

                // Verificar si todos los barcos están colocados
                if (jugador.todosBarcosColocados()) {
                    juegoObservable.notificarObservadores(
                            JuegoObservable.BARCOS_COLOCADOS_COMPLETOS,
                            jugador,
                            "Todos los barcos colocados"
                    );
                    System.out.println("🎉 ¡Todos los barcos colocados! El juego puede comenzar.");
                    mostrarAlerta(AlertType.INFORMATION, "Listo",
                            "Todos los barcos colocados. Haz clic en 'Iniciar juego'");
                }
            } else {
                // Colocación fallida
                celda.setStyle("-fx-background-color: #FF6B6B; -fx-border-color: #C44;");
                mostrarAlerta(AlertType.WARNING, "Ubicación inválida",
                        "No se puede colocar el barco aquí. Intenta en otra posición.");
                juegoObservable.notificarError("Ubicación inválida para barco: " + barco.getNombre());
            }

        } catch (RuntimeException e) {
            // Captura excepciones no marcadas
            manejarExcepcionColocacion(e, celda);
            juegoObservable.notificarError("Excepción al colocar barco: " + e.getMessage());
        }
    }

    /**
     * Encuentra el índice de un barco en la flota del jugador.
     */
    private int encontrarIndiceBarco(Barco barcoBuscado) {
        int indice = 0;
        for (Barco barco : jugador.getBarcos()) {
            if (!barco.estaColocado() && barco.getTipo() == barcoBuscado.getTipo()) {
                return indice;
            }
            indice++;
        }
        return -1;
    }

    /**
     * Marca una celda (o celdas) como conteniendo un barco.
     */
    private void marcarCeldaComoBarco(Pane celda, int tamaño, boolean horizontal) {
        celda.setStyle("-fx-background-color: #8B4513; -fx-border-color: #654321;");
        // Nota: Para barcos de tamaño > 1, deberíamos marcar múltiples celdas
        // Esto es una simplificación para la demo
    }

    /**
     * Maneja excepciones durante la colocación de barcos.
     */
    private void manejarExcepcionColocacion(RuntimeException e, Pane celda) {
        if (e instanceof PosicionInvalidaException) {
            celda.setStyle("-fx-background-color: #FFA500; -fx-border-color: #FF8C00;");
            mostrarAlerta(AlertType.WARNING, "Posición inválida",
                    "La posición está fuera del tablero.");
        } else if (e instanceof BarcoFueraLimitesException) {
            celda.setStyle("-fx-background-color: #9370DB; -fx-border-color: #7B68EE;");
            mostrarAlerta(AlertType.WARNING, "Barco fuera de límites",
                    "El barco no cabe en esa posición.");
        } else {
            celda.setStyle("-fx-background-color: #A9A9A9;");
            mostrarAlerta(AlertType.ERROR, "Error", e.getMessage());
        }
        System.err.println("❌ Error al colocar barco: " + e.getMessage());
    }

    // ========== DISPAROS (HU-2) ==========

    /**
     * Realiza un disparo en la posición especificada del tablero del oponente.
     * HU-2: Lógica de disparos (agua, tocado, hundido).
     */
    private void disparar(int fila, int col, Pane celda) {
        if (!juegoIniciado) {
            mostrarAlerta(AlertType.WARNING, "Juego no iniciado",
                    "Coloca todos tus barcos y haz clic en 'Iniciar juego'");
            return;
        }

        if (!turnoJugador) {
            mostrarAlerta(AlertType.INFORMATION, "Turno de la máquina",
                    "Espera tu turno");
            return;
        }

        System.out.println("🎯 Jugador dispara en (" + fila + "," + col + ")");

        // Realizar disparo
        String resultado = jugador.realizarDisparo(fila, col, maquina);

        // NOTIFICAR OBSERVADORES DEL DISPARO
        juegoObservable.notificarDisparo(jugador, resultado, fila, col);

        // Actualizar interfaz según resultado
        switch (resultado) {
            case "AGUA":
                celda.setStyle("-fx-background-color: #1E90FF; -fx-border-color: #0000FF;");
                celda.getStyleClass().add("agua");
                System.out.println("🌊 AGUA en (" + fila + "," + col + ")");
                turnoJugador = false; // Pasa turno a la máquina
                juegoObservable.notificarCambioTurno(false); // Notificar cambio de turno
                turnoMaquina(); // La máquina dispara
                break;

            case "TOCADO":
                celda.setStyle("-fx-background-color: #FF4500; -fx-border-color: #FF0000;");
                celda.getStyleClass().add("tocado");
                System.out.println("🔥 TOCADO en (" + fila + "," + col + ")");
                // Jugador sigue disparando
                break;

            case "HUNDIDO":
                celda.setStyle("-fx-background-color: #8B0000; -fx-border-color: #660000;");
                celda.getStyleClass().add("hundido");
                System.out.println("💥 HUNDIDO en (" + fila + "," + col + ")");
                // Verificar si ganó
                verificarFinJuego();
                break;

            case "REPETIDO":
                System.out.println("⚠️ Ya disparaste aquí");
                juegoObservable.notificarObservadores(
                        JuegoObservable.ADVERTENCIA,
                        jugador,
                        "Disparo repetido en (" + fila + "," + col + ")"
                );
                break;

            case "INVALIDO":
                System.out.println("❌ Disparo inválido");
                juegoObservable.notificarError("Disparo inválido en (" + fila + "," + col + ")");
                break;
        }

        celda.setDisable(true);
    }

    /**
     * Turno de la máquina para disparar.
     * HU-4: Inteligencia artificial de la máquina.
     */
    private void turnoMaquina() {
        if (!juegoIniciado || maquina.haPerdido() || jugador.haPerdido()) {
            return;
        }

        System.out.println("🤖 Turno de la máquina...");

        // NOTIFICAR CAMBIO DE TURNO
        juegoObservable.notificarCambioTurno(false);

        // La máquina realiza un disparo aleatorio
        int[] resultado = maquina.realizarDisparoAleatorio(jugador);
        int fila = resultado[0];
        int columna = resultado[1];
        int tipoResultado = resultado[2]; // 0=agua, 1=tocado, 2=hundido

        // Encontrar la celda correspondiente en el tablero del jugador
        Pane celda = encontrarCeldaTableroJugador(fila, columna);

        if (celda != null) {
            switch (tipoResultado) {
                case 0: // AGUA
                    celda.setStyle("-fx-background-color: #1E90FF; -fx-border-color: #0000FF;");
                    System.out.println("🤖🌊 La máquina disparó AGUA en (" + fila + "," + columna + ")");
                    turnoJugador = true; // Vuelve turno al jugador
                    juegoObservable.notificarCambioTurno(true); // Notificar cambio de turno
                    juegoObservable.notificarDisparo(maquina, "AGUA", fila, columna);
                    break;

                case 1: // TOCADO
                    celda.setStyle("-fx-background-color: #FF4500; -fx-border-color: #FF0000;");
                    System.out.println("🤖🔥 La máquina TOCÓ en (" + fila + "," + columna + ")");
                    juegoObservable.notificarDisparo(maquina, "TOCADO", fila, columna);
                    // La máquina sigue disparando
                    turnoMaquina();
                    break;

                case 2: // HUNDIDO
                    celda.setStyle("-fx-background-color: #8B0000; -fx-border-color: #660000;");
                    System.out.println("🤖💥 La máquina HUNDIÓ en (" + fila + "," + columna + ")");
                    juegoObservable.notificarDisparo(maquina, "HUNDIDO", fila, columna);
                    // Verificar si la máquina ganó
                    verificarFinJuego();
                    break;
            }
            celda.setDisable(true);
        }
    }

    /**
     * Encuentra una celda en el tablero del jugador por coordenadas.
     */
    private Pane encontrarCeldaTableroJugador(int fila, int columna) {
        for (javafx.scene.Node node : tableroJugador.getChildren()) {
            if (GridPane.getRowIndex(node) == fila && GridPane.getColumnIndex(node) == columna) {
                return (Pane) node;
            }
        }
        return null;
    }

    // ========== VERIFICACIÓN DE FIN DE JUEGO ==========

    /**
     * Verifica si el juego ha terminado.
     */
    private void verificarFinJuego() {
        if (maquina.haPerdido()) {
            juegoIniciado = false;
            // NOTIFICAR FIN DEL JUEGO
            juegoObservable.notificarJuegoTerminado("Jugador Humano");
            mostrarAlerta(AlertType.INFORMATION, "¡FELICIDADES!",
                    "🎉 ¡HAS GANADO! Hundiste toda la flota enemiga.");
            System.out.println("🎉 ¡EL JUGADOR GANA!");
        } else if (jugador.haPerdido()) {
            juegoIniciado = false;
            // NOTIFICAR FIN DEL JUEGO
            juegoObservable.notificarJuegoTerminado("Máquina");
            mostrarAlerta(AlertType.INFORMATION, "FIN DEL JUEGO",
                    "😢 La máquina ganó. Mejor suerte la próxima vez.");
            System.out.println("😢 ¡LA MÁQUINA GANA!");
        }
    }

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Muestra una alerta en pantalla.
     */
    private void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // ========== BOTONES DE CONTROL ==========

    @FXML
    private void iniciarJuego() {
        if (!jugador.todosBarcosColocados()) {
            mostrarAlerta(AlertType.WARNING, "Barcos incompletos",
                    "Debes colocar todos tus barcos antes de comenzar.");
            juegoObservable.notificarError("Intento de iniciar juego con barcos incompletos");
            return;
        }

        juegoIniciado = true;
        turnoJugador = true;

        // Deshabilitar panel de barcos
        panelBarcos.setDisable(true);

        // NOTIFICAR INICIO DEL JUEGO
        juegoObservable.notificarJuegoIniciado();
        juegoObservable.notificarCambioTurno(true);

        mostrarAlerta(AlertType.INFORMATION, "¡COMIENZA EL JUEGO!",
                "El juego ha comenzado. ¡Buena suerte!\n\n" +
                        "Tu turno: Haz clic en el tablero de la derecha para disparar.");

        System.out.println("🎮 ¡JUEGO INICIADO!");
        System.out.println("🔫 Turno del jugador");
    }

    @FXML
    private void mostrarTableroOponente() {
        // HU-3: Visualización del tablero del oponente (para profesor)
        if (!juegoIniciado) {
            String tableroCompleto = maquina.mostrarTableroConBarcos();
            mostrarAlerta(AlertType.INFORMATION, "Tablero de la Máquina",
                    "Esta vista es solo para verificación del profesor:\n\n" + tableroCompleto);
            juegoObservable.notificarObservadores(
                    JuegoObservable.INFORMACION,
                    null,
                    "Mostrado tablero de la máquina (HU-3)"
            );
            System.out.println("👁️ Mostrando tablero de la máquina (HU-3)");
        } else {
            mostrarAlerta(AlertType.WARNING, "No disponible",
                    "Esta opción solo está disponible antes de iniciar el juego.");
        }
    }

    @FXML
    private void reiniciarJuego() {
        // NOTIFICAR REINICIO
        juegoObservable.notificarObservadores(
                JuegoObservable.JUEGO_REINICIADO,
                null,
                "Juego reiniciado"
        );

        // Reiniciar todo el juego
        // Nota: No llamamos initialize() directamente para evitar problemas con JavaFX
        // En su lugar, recreamos los objetos principales

        jugador = new Jugador("Humano");
        maquina = new Jugador("Máquina");
        maquina.colocarBarcosAleatoriamente();

        juegoIniciado = false;
        turnoJugador = true;
        panelBarcos.setDisable(false);

        // Limpiar tableros visuales
        limpiarTableroVisual(tableroJugador);
        limpiarTableroVisual(tableroOponente);

        // Recrear panel de barcos
        panelBarcos.getChildren().clear();
        crearPanelBarcos();

        // Notificar a los observadores
        juegoObservable.notificarObservadores(
                "JUEGO_REINICIADO_COMPLETO",
                jugador,
                "Juego completamente reiniciado"
        );

        System.out.println("🔄 Juego reiniciado");
    }

    private void limpiarTableroVisual(GridPane tablero) {
        for (javafx.scene.Node node : tablero.getChildren()) {
            if (node instanceof Pane) {
                Pane celda = (Pane) node;
                celda.setStyle("-fx-background-color: #87CEEB; -fx-border-color: #4682B4; -fx-border-width: 1;");
                celda.setDisable(false);
                celda.getStyleClass().removeAll("agua", "tocado", "hundido");
            }
        }
    }
}