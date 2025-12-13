package com.batallanaval.batallanaval.controller;

import com.batallanaval.batallanaval.patterns.observer.JuegoObservable;
import com.batallanaval.batallanaval.patterns.observer.ObservadorConsola;
import com.batallanaval.batallanaval.patterns.observer.ObservadorGuardado;
import com.batallanaval.batallanaval.patterns.observer.ObservadorInterfaz;
import com.batallanaval.batallanaval.model.Jugador;
import com.batallanaval.batallanaval.model.Barco;
import com.batallanaval.batallanaval.model.TipoBarco;
import com.batallanaval.batallanaval.patterns.factory.BarcoFactory;
import com.batallanaval.batallanaval.exceptions.PosicionInvalidaException;
import com.batallanaval.batallanaval.exceptions.BarcoFueraLimitesException;
import com.batallanaval.batallanaval.datastructures.PilaMovimientos;
import com.batallanaval.batallanaval.model.Movimiento;
import com.batallanaval.batallanaval.model.Movimiento.TipoResultado;
import com.batallanaval.batallanaval.utils.Figuras2DUtils;
import javafx.animation.PauseTransition;
import javafx.scene.layout.*;
import javafx.util.Duration;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.shape.*;
import javafx.scene.Group;
import javafx.scene.control.Label;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javafx.scene.input.KeyCode;

/**
 * Controlador principal del juego Batalla Naval.
 * Maneja la lógica del juego, interacción del usuario y comunicación entre modelo y vista.
 * Implementa el patrón Observer para notificaciones automáticas y la estructura de datos Pila
 * para el historial de movimientos. Utiliza figuras 2D de JavaFX para la representación visual.
 *
 * @author [Tu Nombre]
 * @version 5.0
 */
public class JuegoController {

    // Variables para manejar el barco que el usuario está arrastrando
    private Barco barcoArrastrado = null;
    private Pane paneBarcoArrastrado = null;

    // Nuevas inyecciones para las coordenadas
    @FXML private HBox coordColumnasJugador;
    @FXML private VBox coordFilasJugador;
    @FXML private HBox coordColumnasOponente;
    @FXML private VBox coordFilasOponente;

    /** Contenedor principal para la gestión del foco y eventos de teclado. */
    @FXML private AnchorPane mainAnchorPane;
    /** Tablero visual donde el jugador coloca sus barcos. */
    @FXML private GridPane tableroJugador;
    /** Panel lateral que contiene los barcos disponibles para arrastrar. */
    @FXML private VBox panelBarcos;
    /** Tablero donde el jugador dispara al oponente. */
    @FXML private GridPane tableroOponente;

    private Jugador jugador;              // Jugador humano
    private Jugador maquina;              // Jugador máquina

    /** Indica la orientación del barco a colocar: true=Horizontal, false=Vertical. */
    private boolean orientacionHorizontal = true;
    /** Indica si el juego ha comenzado (fase de disparos). */
    private boolean juegoIniciado = false;
    /** Indica el turno actual: true=Jugador, false=Máquina. */
    private boolean turnoJugador = true;

    /** Etiqueta de la interfaz para mostrar mensajes al jugador. */
    @FXML private Label lblMensajeJugador;

    // ========== OBSERVER PATTERN ==========
    private JuegoObservable juegoObservable;
    private ObservadorConsola observadorConsola;
    private ObservadorInterfaz observadorInterfaz;
    private ObservadorGuardado observadorGuardado;

    // ========== ESTRUCTURA DE DATOS: PILA ==========
    private PilaMovimientos<Movimiento> pilaMovimientos;

    // ========== INICIALIZACIÓN ==========

    /**
     * Método de inicialización llamado automáticamente por JavaFX al cargar el FXML.
     * Configura el modelo, inicializa observadores y estructuras de datos, y construye la interfaz.
     */
    @FXML
    public void initialize() {
        System.out.println("🚀 Inicializando JuegoController...");

        jugador = new Jugador("Humano");
        maquina = new Jugador("Máquina");

        // ========== INICIALIZAR OBSERVER ==========
        inicializarObservadores();

        // ========== INICIALIZAR ESTRUCTURAS DE DATOS ==========
        inicializarEstructurasDatos();
        // ==========================================

        crearTableroVisual();       // Crear tablero de posición del jugador
        crearTableroOponente();     // Crear tablero principal para disparos

        // LLAMADA A LAS COORDENADAS
        crearCoordenadasVisuales();

        // HU-4: Colocar barcos de la máquina aleatoriamente
        maquina.colocarBarcosAleatoriamente();
        juegoObservable.notificarObservadores(
                JuegoObservable.BARCO_COLOCADO,
                maquina,
                "Máquina colocó sus barcos aleatoriamente"
        );
        System.out.println("🤖 Barcos de la máquina colocados aleatoriamente");

        crearPanelBarcos();         // Crear panel de selección de barcos

        // ==========================================================
        // ** NUEVO: CONFIGURACIÓN DE EVENTOS DE TECLADO (ROTACIÓN) **
        // ==========================================================

        // 1. Asegurar que el AnchorPane pueda recibir foco
        if (mainAnchorPane != null) {
            mainAnchorPane.setFocusTraversable(true);
            mainAnchorPane.requestFocus(); // Darle foco inicial

            // 2. Configurar el evento de presionar tecla
            mainAnchorPane.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.R) {
                    alternarOrientacion();
                }
            });
            System.out.println("✅ Evento de tecla 'R' para rotación configurado.");
        } else {
            System.err.println("❌ Error: mainAnchorPane no está inyectado. La rotación no funcionará.");
        }
        // ==========================================================


        System.out.println("✅ Juego inicializado correctamente");
        System.out.println("🎮 Jugador: " + jugador.getNickname());
        System.out.println("🤖 Máquina: " + maquina.getNickname());
        System.out.println("📊 Estructura de datos: PilaMovimientos creada");
        System.out.println("🎨 Figuras 2D JavaFX: Habilitadas");
    }

    /**
     * Actualiza el mensaje visible para el jugador en la etiqueta lblMensajeJugador.
     * @param mensaje El texto a mostrar.
     */
    private void mostrarMensaje(String mensaje) {
        if (lblMensajeJugador != null) {
            lblMensajeJugador.setText(mensaje);
        }
    }

    /**
     * Inicializa el sistema de observadores, creando e inyectando el callback
     * del método mostrarMensaje al ObservadorInterfaz.
     */
    private void inicializarObservadores() {
        juegoObservable = new JuegoObservable();

        // Crear observadores. Se pasa this::mostrarMensaje al ObservadorInterfaz.
        observadorConsola = new ObservadorConsola("Consola");
        observadorInterfaz = new ObservadorInterfaz("Interfaz", this::mostrarMensaje);
        observadorGuardado = new ObservadorGuardado();

        // Registrar observadores
        juegoObservable.agregarObservador(observadorConsola);
        juegoObservable.agregarObservador(observadorInterfaz);
        juegoObservable.agregarObservador(observadorGuardado);

        System.out.println("👁️ Observadores registrados: " + juegoObservable.cantidadObservadores());
    }

    /**
     * Crea las etiquetas A-J (columnas) y 1-10 (filas) para ambos tableros visuales.
     */
    private void crearCoordenadasVisuales() {
        System.out.println("🏷️ Creando coordenadas visuales...");

        // Coordenadas de las Columnas (A-J)
        for (int i = 0; i < 10; i++) {
            // **Declaración de 'letra' dentro del bucle**
            String letra = String.valueOf((char) ('A' + i));

            Label colLabel = new Label(letra);
            colLabel.setPrefSize(30.0, 25.0);
            colLabel.getStyleClass().add("coord-label-col");
            coordColumnasJugador.getChildren().add(colLabel);

            Label colLabelOponente = new Label(letra);
            colLabelOponente.setPrefSize(30.0, 25.0);
            colLabelOponente.getStyleClass().add("coord-label-col");
            coordColumnasOponente.getChildren().add(colLabelOponente);
        }

        // Coordenadas de las Filas (1-10)
        for (int i = 1; i <= 10; i++) {
            // **Declaración de 'numero' dentro del bucle**
            String numero = String.valueOf(i);

            Label filaLabel = new Label(numero);
            filaLabel.setPrefSize(25.0, 30.0);
            filaLabel.getStyleClass().add("coord-label-fila");
            coordFilasJugador.getChildren().add(filaLabel);

            Label filaLabelOponente = new Label(numero);
            filaLabelOponente.setPrefSize(25.0, 30.0);
            filaLabelOponente.getStyleClass().add("coord-label-fila");
            coordFilasOponente.getChildren().add(filaLabelOponente);
        }
        System.out.println("✅ Coordenadas A-J y 1-10 creadas.");
    }


    /**
     * Método auxiliar para configurar eventos de teclado en la Scene.
     * @param scene La escena principal de JavaFX.
     */
    public void configurarEventosTeclado(javafx.scene.Scene scene) {
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.R) {
                alternarOrientacion();
            }
        });
    }

    /**
     * Alterna la orientación del barco actualmente arrastrado entre Horizontal y Vertical.
     */
    public void alternarOrientacion() {
        if (juegoIniciado) return;

        orientacionHorizontal = !orientacionHorizontal;
        String orientacion = orientacionHorizontal ? "Horizontal (H)" : "Vertical (V)";

        // Usar el nuevo Label en lugar de la Alerta
        mostrarMensaje("Rotación: " + orientacion);
    }

    /**
     * Inicializa la estructura de datos Pila para el historial de movimientos.
     */
    private void inicializarEstructurasDatos() {
        System.out.println("📊 Inicializando estructuras de datos...");

        // Crear pila con capacidad para 200 movimientos (más que suficiente)
        pilaMovimientos = new PilaMovimientos<>(200);

        System.out.println("✅ Pila de movimientos creada. Capacidad: " +
                pilaMovimientos.getCapacidad());

        // Ejemplo de uso básico (para demostración)
        demostrarUsoPila();
    }

    /**
     * Demuestra el funcionamiento básico de la pila (solo para depuración).
     */
    private void demostrarUsoPila() {
        System.out.println("🔍 Demostrando uso de la pila:");
        System.out.println("   - Pila vacía: " + pilaMovimientos.estaVacia());
        System.out.println("   - Capacidad: " + pilaMovimientos.getCapacidad());
        System.out.println("   - tamanho actual: " + pilaMovimientos.tamanio());
    }

    // ========== CREACIÓN DE TABLEROS VISUALES ==========

    /**
     * Crea el tablero visual 10x10 para el jugador humano, configurando el evento de soltar
     * el arrastre para la colocación de barcos.
     */
    public void crearTableroVisual() {
        System.out.println("📐 Creando tablero del jugador (10x10)...");

        for (int fila = 0; fila < 10; fila++) {
            for (int col = 0; col < 10; col++) {
                Pane celda = new Pane();
                celda.setPrefSize(30, 30);
                celda.getStyleClass().add("pane-celda");

                // Usar figura 2D para la celda
                Group celdaFigura = Figuras2DUtils.crearCeldaTablero(30);
                celda.getChildren().add(celdaFigura);

                final int f = fila, c = col;
                celda.setOnMouseDragReleased(e -> {
                    if (!juegoIniciado) {
                        colocarBarco(f, c, celda);
                    }
                });

                tableroJugador.add(celda, col, fila);
            }
        }
        System.out.println("✅ Tablero del jugador creado: 100 celdas con figuras 2D");
    }

    /**
     * Crea el tablero 10x10 para el oponente (máquina), configurando el evento de clic
     * para realizar disparos.
     */
    private void crearTableroOponente() {
        System.out.println("📐 Creando tablero del oponente (10x10)...");

        for (int fila = 0; fila < 10; fila++) {
            for (int col = 0; col < 10; col++) {
                Pane celda = new Pane();
                celda.setPrefSize(30, 30);
                celda.getStyleClass().add("pane-celda");

                // Usar figura 2D para la celda
                Group celdaFigura = Figuras2DUtils.crearCeldaTablero(30);
                celda.getChildren().add(celdaFigura);

                final int f = fila, c = col;
                celda.setOnMouseClicked(e -> {
                    if (juegoIniciado && turnoJugador) {
                        disparar(f, c, celda);
                    }
                });

                tableroOponente.add(celda, col, fila);
            }
        }
        System.out.println("✅ Tablero del oponente creado: 100 celdas con figuras 2D");
    }

    // ========== PANEL DE BARCOS ==========

    /**
     * Crea el panel lateral con todos los barcos disponibles para colocar, generando un Pane
     * con figura 2D para cada uno.
     */
    private void crearPanelBarcos() {
        System.out.println("🚢 Creando panel de barcos con figuras 2D...");

        // 1 Portaaviones (tamanho 4)
        Barco portaaviones = BarcoFactory.crearBarco(TipoBarco.PORTAVIONES);
        panelBarcos.getChildren().add(crearBarcoPaneFigura(portaaviones));

        // 2 Submarinos (tamanho 3 cada uno)
        Barco sub1 = BarcoFactory.crearBarco(TipoBarco.SUBMARINO);
        panelBarcos.getChildren().add(crearBarcoPaneFigura(sub1));

        Barco sub2 = BarcoFactory.crearBarco(TipoBarco.SUBMARINO);
        panelBarcos.getChildren().add(crearBarcoPaneFigura(sub2));

        // 3 Destructores (tamanho 2 cada uno)
        Barco dest1 = BarcoFactory.crearBarco(TipoBarco.DESTRUCTOR);
        panelBarcos.getChildren().add(crearBarcoPaneFigura(dest1));

        Barco dest2 = BarcoFactory.crearBarco(TipoBarco.DESTRUCTOR);
        panelBarcos.getChildren().add(crearBarcoPaneFigura(dest2));

        Barco dest3 = BarcoFactory.crearBarco(TipoBarco.DESTRUCTOR);
        panelBarcos.getChildren().add(crearBarcoPaneFigura(dest3));

        // 4 Fragatas (tamanho 1 cada una)
        Barco frag1 = BarcoFactory.crearBarco(TipoBarco.FRAGATA);
        panelBarcos.getChildren().add(crearBarcoPaneFigura(frag1));

        Barco frag2 = BarcoFactory.crearBarco(TipoBarco.FRAGATA);
        panelBarcos.getChildren().add(crearBarcoPaneFigura(frag2));

        Barco frag3 = BarcoFactory.crearBarco(TipoBarco.FRAGATA);
        panelBarcos.getChildren().add(crearBarcoPaneFigura(frag3));

        Barco frag4 = BarcoFactory.crearBarco(TipoBarco.FRAGATA);
        panelBarcos.getChildren().add(crearBarcoPaneFigura(frag4));

        System.out.println("✅ Panel de barcos creado: 10 barcos con figuras 2D");
    }

    /**
     * Crea un Pane visual para representar un barco en el panel lateral (VERSIÓN ANTIGUA).
     * @param barco El objeto Barco.
     * @return El Pane visual.
     */
    private Pane crearBarcoPane(Barco barco) {
        Pane pane = new Pane();
        pane.setPrefSize(barco.gettamanho() * 35, 30);
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

    /**
     * Crea un Pane visual con FIGURA 2D para representar un barco en el panel lateral.
     * Configura el evento onDragDetected para capturar el barco que se está arrastrando.
     * @param barco El objeto Barco.
     * @return El Pane visual.
     */
    private Pane crearBarcoPaneFigura(Barco barco) {
        Pane pane = new Pane();
        pane.setPrefSize(barco.gettamanho() * 35, 30);

        // Crear figura 2D para el barco - CAMBIADO a Group
        Group figuraBarco = Figuras2DUtils.crearFiguraBarcoPorTipo(
                barco.getNombre(),
                barco.gettamanho() * 35,
                30,
                true  // Horizontal por defecto en el panel
        );

        // Ajustar posición de la figura
        figuraBarco.setLayoutX(2.5);
        figuraBarco.setLayoutY(2.5);

        // Agregar figura al Pane
        pane.getChildren().add(figuraBarco);

        // Almacenar referencia al objeto Barco en el UserData del Pane
        pane.setUserData(barco);

        // Configurar arrastre del barco
        pane.setOnDragDetected(e -> {
            if (!juegoIniciado) {
                pane.startFullDrag();
                System.out.println("🚢 Arrastrando barco: " + barco.getNombre() + " (Figura 2D)");

                // Guardar la referencia al barco y su pane al iniciar el arrastre
                this.barcoArrastrado = barco;
                this.paneBarcoArrastrado = pane;
            }
        });

        // Efecto hover en la figura (aplicar al primer Shape del Group)
        if (!figuraBarco.getChildren().isEmpty() && figuraBarco.getChildren().get(0) instanceof Shape) {
            Figuras2DUtils.aplicarEfectoHover((Shape) figuraBarco.getChildren().get(0));
        }

        return pane;
    }

    // ========== COLOCACIÓN DE BARCOS (HU-1) ==========

    /**
     * Coloca el barco arrastrado en la posición especificada del tablero del jugador.
     * Utiliza las referencias almacenadas en barcoArrastrado y paneBarcoArrastrado.
     * @param fila Fila de colocación (0-9).
     * @param col Columna de colocación (0-9).
     * @param celda La celda de la interfaz donde se soltó el arrastre.
     */
    private void colocarBarco(int fila, int col, Pane celda) {

        // Usar el barco y el pane de las variables de estado (arrastre libre)
        Barco barco = this.barcoArrastrado;
        Pane barcoPane = this.paneBarcoArrastrado;

        if (barco == null || barcoPane == null) {
            // Error si no se arrastró un barco correctamente
            mostrarMensaje("🚢 Error: Selecciona un barco válido del panel para colocar.");
            return;
        }


        try {
            // Encontrar el índice del barco en la flota
            int indiceBarco = encontrarIndiceBarco(barco);
            if (indiceBarco == -1) {
                mostrarMensaje("❌ Error: Barco no encontrado en la flota. Intenta reiniciar.");
                juegoObservable.notificarError("Barco no encontrado en la flota: " + barco.getNombre());
                return;
            }

            // Usar la variable de estado de orientación
            boolean horizontal = this.orientacionHorizontal;
            boolean colocado = jugador.colocarBarco(indiceBarco, fila, col, horizontal);

            if (colocado) {
                // Colocación exitosa - usar FIGURA 2D
                marcarBarcoEnTablero(fila, col, barco.gettamanho(), horizontal, barco.getNombre());

                // Remover el Pane que fue arrastrado y limpiar referencias
                panelBarcos.getChildren().remove(barcoPane);
                this.barcoArrastrado = null;
                this.paneBarcoArrastrado = null;


                // NOTIFICAR OBSERVADORES
                juegoObservable.notificarBarcoColocado(jugador, barco.getNombre(), fila, col);

                // Generar mensaje detallado (Columna Letra, Fila Número)
                char letraColumna = (char) ('A' + col);
                int numeroFila = fila + 1;
                String mensajeColocacion = String.format("✅ Barco %s colocado en %c%d.",
                        barco.getNombre(), letraColumna, numeroFila);


                System.out.println(mensajeColocacion + " Orientación: " + (horizontal ? "Horizontal" : "Vertical"));

                // Verificar si todos los barcos están colocados
                if (jugador.todosBarcosColocados()) {
                    juegoObservable.notificarObservadores(
                            JuegoObservable.BARCOS_COLOCADOS_COMPLETOS,
                            jugador,
                            "Todos los barcos colocados"
                    );
                    System.out.println("🎉 ¡Todos los barcos colocados! El juego puede comenzar.");

                    // Mensaje final de listo
                    mostrarMensaje(mensajeColocacion + " 🎉 ¡Listo! Clic en 'INICIAR JUEGO' para empezar.");
                } else {
                    // Mensaje de éxito parcial con coordenadas y recordatorio de rotación
                    String orientacionStr = horizontal ? "Horizontal (R)" : "Vertical (R)";
                    mostrarMensaje(mensajeColocacion + " Sigue colocando barcos. Orientación: " + orientacionStr);
                }
            } else {


                mostrarMensaje("❌ Ubicación inválida. No cabe o está superpuesto.");
                juegoObservable.notificarError("Ubicación inválida para barco: " + barco.getNombre());
            }

        } catch (RuntimeException e) {
            // Captura excepciones y usa el Label
            manejarExcepcionColocacion(e, celda);

            mostrarMensaje("❌ Error al colocar barco: " + e.getMessage());
            juegoObservable.notificarError("Excepción al colocar barco: " + e.getMessage());
        }
    }

    /**
     * Encuentra el índice de un barco en la flota del jugador.
     * @param barcoBuscado El barco a buscar.
     * @return El índice del barco en la flota, o -1 si no se encuentra.
     */
    private int encontrarIndiceBarco(Barco barcoBuscado) {
        return jugador.getBarcos().indexOf(barcoBuscado);
    }

    /**
     * Marca una celda (o celdas) como conteniendo un barco (VERSIÓN ANTIGUA).
     * @param celda El Pane de la celda.
     * @param tamanho tamanho del barco.
     * @param horizontal Orientación del barco.
     */
    private void marcarCeldaComoBarco(Pane celda, int tamanho, boolean horizontal) {
        celda.setStyle("-fx-background-color: #8B4513; -fx-border-color: #654321;");
        // Nota: Para barcos de tamanho > 1, deberíamos marcar múltiples celdas
    }

    /**
     * Marca una celda como conteniendo un barco usando FIGURA 2D.
     * @param fila Fila de inicio.
     * @param col Columna de inicio.
     * @param tamanho tamanho del barco.
     * @param horizontal Orientación del barco.
     * @param nombre Nombre del barco para elegir la figura.
     */
    private void marcarBarcoEnTablero(int fila, int col, int tamanho, boolean horizontal, String nombre) {
        for (int i = 0; i < tamanho; i++) {

            int f = horizontal ? fila : fila + i;
            int c = horizontal ? col + i : col;

            Pane celda = obtenerCelda(tableroJugador, f, c);

            if (celda != null) {
                celda.getChildren().clear();

                Group parte = Figuras2DUtils.crearFiguraBarcoPorTipo(
                        nombre,
                        25,
                        25,
                        horizontal
                );

                celda.getChildren().add(parte);
            }
        }
    }

    /**
     * Obtiene el Pane (celda) de un GridPane en la posición (fila, columna) específica.
     * @param grid El GridPane donde buscar.
     * @param fila Fila (índice Y).
     * @param columna Columna (índice X).
     * @return El Pane de la celda, o null si no se encuentra.
     */
    private Pane obtenerCelda(GridPane grid, int fila, int columna) {
        for (javafx.scene.Node n : grid.getChildren()) {
            Integer r = GridPane.getRowIndex(n);
            Integer c = GridPane.getColumnIndex(n);
            if (r != null && c != null && r == fila && c == columna) {
                return (Pane) n;
            }
        }
        return null;
    }



    /**
     * Muestra error de colocación con una figura X roja en la celda.
     * @param celda El Pane de la celda.
     */
    private void mostrarErrorColocacion(Pane celda) {
        celda.getChildren().clear();

        // Crear X roja con figuras 2D
        Line linea1 = new Line(5, 5, 35, 35);
        Line linea2 = new Line(35, 5, 5, 35);

        linea1.setStroke(javafx.scene.paint.Color.RED);
        linea1.setStrokeWidth(3);
        linea2.setStroke(javafx.scene.paint.Color.RED);
        linea2.setStrokeWidth(3);

        celda.getChildren().addAll(linea1, linea2);
    }

    /**
     * Maneja excepciones de Posición/Límite inválido durante la colocación de barcos,
     * mostrando figuras 2D de advertencia.
     * @param e La excepción Runtime capturada.
     * @param celda La celda donde ocurrió el error.
     */
    private void manejarExcepcionColocacion(RuntimeException e, Pane celda) {
        celda.getChildren().clear();

        if (e instanceof PosicionInvalidaException) {
            // Triángulo amarillo de advertencia
            Polygon triangulo = new Polygon();
            triangulo.getPoints().addAll(
                    20.0, 5.0,
                    5.0, 35.0,
                    35.0, 35.0
            );
            triangulo.setFill(javafx.scene.paint.Color.YELLOW);
            triangulo.setStroke(javafx.scene.paint.Color.ORANGE);
            triangulo.setStrokeWidth(2);
            celda.getChildren().add(triangulo);

            mostrarAlerta(AlertType.WARNING, "Posición inválida",
                    "La posición está fuera del tablero.");
        } else if (e instanceof BarcoFueraLimitesException) {
            // Círculo morado de límite
            Circle circulo = new Circle(20, 20, 15);
            circulo.setFill(javafx.scene.paint.Color.PURPLE);
            circulo.setStroke(javafx.scene.paint.Color.DARKVIOLET);
            circulo.setStrokeWidth(2);
            celda.getChildren().add(circulo);

            mostrarAlerta(AlertType.WARNING, "Barco fuera de límites",
                    "El barco no cabe en esa posición.");
        } else {
            // Cuadrado gris de error genérico
            Rectangle rectangulo = new Rectangle(5, 5, 30, 30);
            rectangulo.setFill(javafx.scene.paint.Color.GRAY);
            rectangulo.setStroke(javafx.scene.paint.Color.DARKGRAY);
            rectangulo.setStrokeWidth(2);
            celda.getChildren().add(rectangulo);

            mostrarAlerta(AlertType.ERROR, "Error", e.getMessage());
        }
        System.err.println("❌ Error al colocar barco: " + e.getMessage());
    }

    // ========== DISPAROS (HU-2) ==========

    /**
     * Realiza un disparo del jugador en la posición especificada del tablero del oponente.
     * Maneja el flujo de turnos según el resultado (AGUA, TOCADO, HUNDIDO).
     * @param fila Fila del disparo.
     * @param col Columna del disparo.
     * @param celda La celda de la interfaz donde se hizo clic.
     */
    private void disparar(int fila, int col, Pane celda) {
        if (!juegoIniciado) {
            mostrarMensaje("⚠️ Juego no iniciado. Coloca todos tus barcos y haz clic en 'Iniciar juego'.");
            return;
        }

        if (!turnoJugador) {
            mostrarMensaje("⏳ Turno de la máquina. ¡Espera!");
            return;
        }

        System.out.println("🎯 Jugador dispara en (" + fila + "," + col + ")");

        // Realizar disparo
        String resultado = jugador.realizarDisparo(fila, col, maquina);

        // Generación del mensaje detallado (Columna Letra, Fila Número)
        char letraColumna = (char) ('A' + col);
        int numeroFila = fila + 1;
        String coordenada = String.format("%c%d", letraColumna, numeroFila);


        // ========== REGISTRAR Y NOTIFICAR ==========
        registrarMovimientoEnPila(jugador, resultado, fila, col, true);
        juegoObservable.notificarDisparo(jugador, resultado, fila, col);

        // Actualizar interfaz con FIGURA 2D según resultado
        actualizarCeldaConFigura(celda, resultado, fila, col);
        celda.setDisable(true); // Deshabilita la celda disparada

        // 1. Manejar el flujo de juego y mostrar mensajes en el Label
        if (resultado.equals("TOCADO")) {
            System.out.println("🔥 TOCADO. Tienes otro turno. Haz clic de nuevo.");
            mostrarMensaje("🔥 ¡TOCADO en " + coordenada + "! Sigue disparando, tienes turno extra.");

        } else if (resultado.equals("HUNDIDO")) {
            System.out.println("🎉 HUNDIDO. Tienes otro turno. Haz clic de nuevo.");
            mostrarMensaje("💥 ¡HUNDIDO en " + coordenada + "! Tienes turno extra.");
            verificarFinJuego();

        } else if (resultado.equals("AGUA")) {
            System.out.println("🌊 AGUA. Turno de la máquina.");
            mostrarMensaje("🌊 ¡Agua en " + coordenada + "! Fallaste. Turno de la Máquina 🤖.");

            // 2. CAMBIO DE TURNO A LA MÁQUINA
            turnoJugador = false;
            juegoObservable.notificarCambioTurno(false);

            // DELAY: Esperar 500ms para que el jugador vea el splash de agua.
            PauseTransition postDelay = new PauseTransition(Duration.millis(500));
            postDelay.setOnFinished(e -> {
                turnoMaquina();
            });
            postDelay.play();

        } else if (resultado.equals("REPETIDO")) {
            System.out.println("⚠️ Ya Disparaste aquí. Intenta de nuevo.");
            mostrarMensaje("⚠️ ¡Ya disparaste en " + coordenada + "! Intenta en otra posición.");
        }
    }


    /**
     * Actualiza una celda en el tablero del oponente con una figura 2D según el resultado del disparo.
     * @param celda El Pane de la celda disparada.
     * @param resultado Resultado del disparo ("AGUA", "TOCADO", "HUNDIDO", etc.).
     * @param fila Fila del disparo.
     * @param col Columna del disparo.
     */
    private void actualizarCeldaConFigura(Pane celda, String resultado, int fila, int col) {
        // Limpiar la celda
        celda.getChildren().clear();

        switch (resultado) {
            case "AGUA":
                // Crear círculo azul de agua
                Group figuraAgua = (Group) Figuras2DUtils.crearFiguraResultado("AGUA", 25);
                celda.getChildren().add(figuraAgua);
                System.out.println("🌊 AGUA en (" + fila + "," + col + ") [Figura 2D]");
                turnoJugador = false; // Pasa turno a la máquina
                juegoObservable.notificarCambioTurno(false);

                break;

            case "TOCADO":
                // Crear círculo naranja de tocado
                Group figuraTocado = (Group) Figuras2DUtils.crearFiguraResultado("TOCADO", 25);
                celda.getChildren().add(figuraTocado);
                System.out.println("🔥 TOCADO en (" + fila + "," + col + ") [Figura 2D]");
                // Jugador sigue disparando
                break;

            case "HUNDIDO":
                // Crear círculo rojo de hundido
                Group figuraHundido = (Group) Figuras2DUtils.crearFiguraResultado("HUNDIDO", 25);
                celda.getChildren().add(figuraHundido);
                System.out.println("💥 HUNDIDO en (" + fila + "," + col + ") [Figura 2D]");
                // Verificar si ganó
                verificarFinJuego();
                break;

            case "REPETIDO":
                // Crear X gris de repetido
                Group figuraRepetido = (Group) Figuras2DUtils.crearFiguraResultado("REPETIDO", 25);
                celda.getChildren().add(figuraRepetido);
                System.out.println("⚠️ Ya disparaste aquí [Figura 2D]");
                juegoObservable.notificarObservadores(
                        JuegoObservable.ADVERTENCIA,
                        jugador,
                        "Disparo repetido en (" + fila + "," + col + ")"
                );
                break;

            case "INVALIDO":
                // Crear círculo negro de inválido
                Circle circuloInvalido = new Circle(20, 20, 15);
                circuloInvalido.setFill(javafx.scene.paint.Color.BLACK);
                circuloInvalido.setStroke(javafx.scene.paint.Color.DARKGRAY);
                circuloInvalido.setStrokeWidth(2);
                celda.getChildren().add(circuloInvalido);
                System.out.println("❌ Disparo inválido [Figura 2D]");
                juegoObservable.notificarError("Disparo inválido en (" + fila + "," + col + ")");
                break;
        }
    }

    /**
     * Turno de la máquina para disparar. Implementa la lógica de la IA.
     * Muestra el resultado del disparo en el Label y maneja los delays.
     */
    private void turnoMaquina() {
        if (!juegoIniciado || maquina.haPerdido() || jugador.haPerdido()) {
            return;
        }

        // NOTIFICAR CAMBIO DE TURNO (esto puede ser opcional si ya está en otro lado)
        juegoObservable.notificarCambioTurno(false);
        mostrarMensaje("🤖 Turno de la Máquina... ⏳"); // Mensaje de "pensamiento"

        // DELAY PRINCIPAL (PAUSA INICIAL): 1.2 segundos para el 'pensamiento'
        PauseTransition delayPrincipal = new PauseTransition(Duration.seconds(1.2));

        delayPrincipal.setOnFinished(event -> {

            // La máquina realiza el disparo óptimo
            int[] resultado = maquina.realizarDisparoOptimo(jugador);
            int fila = resultado[0];
            int columna = resultado[1];
            int tipoResultado = resultado[2]; // 0=agua, 1=tocado, 2=hundido

            // Generar mensaje detallado (Columna Letra, Fila Número)
            char letraColumna = (char) ('A' + columna);
            int numeroFila = fila + 1;
            String coordenada = String.format("%c%d", letraColumna, numeroFila);
            String mensajeMaquina;

            Pane celda = encontrarCeldaTableroJugador(fila, columna);

            if (celda != null) {
                celda.getChildren().clear();

                // Lógica de visualización y flujo de turno
                switch (tipoResultado) {
                    case 0: // AGUA
                        Group figuraAgua = (Group) Figuras2DUtils.crearFiguraResultado("AGUA", 25);
                        figuraAgua.setLayoutX(7.5);
                        figuraAgua.setLayoutY(7.5);
                        celda.getChildren().add(figuraAgua);
                        System.out.println("🤖🌊 La máquina disparó AGUA en (" + fila + "," + columna + ") [Figura 2D]");

                        // Mostrar mensaje en el Label
                        mensajeMaquina = "💧 La máquina falló en " + coordenada + ". ¡Es tu turno!";
                        mostrarMensaje(mensajeMaquina);

                        // 1. Finaliza el turno de la máquina
                        turnoJugador = true;
                        juegoObservable.notificarCambioTurno(true);
                        juegoObservable.notificarDisparo(maquina, "AGUA", fila, columna);
                        registrarMovimientoEnPila(maquina, "AGUA", fila, columna, false);
                        break;

                    case 1: // TOCADO
                        Group figuraTocado = (Group) Figuras2DUtils.crearFiguraResultado("TOCADO", 25);
                        figuraTocado.setLayoutX(7.5);
                        figuraTocado.setLayoutY(7.5);
                        celda.getChildren().add(figuraTocado);
                        System.out.println("🤖🔥 La máquina TOCÓ en (" + fila + "," + columna + ") [Figura 2D]");

                        // Mostrar mensaje en el Label
                        mensajeMaquina = "🤖 ¡Te han TOCADO en " + coordenada + "! La máquina tiene otro turno.";
                        mostrarMensaje(mensajeMaquina);

                        juegoObservable.notificarDisparo(maquina, "TOCADO", fila, columna);
                        registrarMovimientoEnPila(maquina, "TOCADO", fila, columna, false);

                        // Agregar un delay a la llamada recursiva para el siguiente impacto
                        PauseTransition delayEntreImpactos = new PauseTransition(Duration.millis(500));
                        delayEntreImpactos.setOnFinished(e -> turnoMaquina());
                        delayEntreImpactos.play();
                        break;

                    case 2: // HUNDIDO
                        Group figuraHundido = (Group) Figuras2DUtils.crearFiguraResultado("HUNDIDO", 25);
                        figuraHundido.setLayoutX(7.5);
                        figuraHundido.setLayoutY(7.5);
                        celda.getChildren().add(figuraHundido);
                        System.out.println("🤖💥 La máquina HUNDIÓ en (" + fila + "," + columna + ") [Figura 2D]");

                        // Mostrar mensaje en el Label
                        mensajeMaquina = "💀 ¡HUNDIDO en " + coordenada + "! La máquina tiene otro turno.";
                        mostrarMensaje(mensajeMaquina);

                        juegoObservable.notificarDisparo(maquina, "HUNDIDO", fila, columna);
                        registrarMovimientoEnPila(maquina, "HUNDIDO", fila, columna, false);

                        verificarFinJuego();

                        // 3. La máquina sigue disparando si el juego no terminó
                        if(juegoIniciado) {
                            // Agregar un delay a la llamada recursiva tras hundir
                            PauseTransition delayTrasHundir = new PauseTransition(Duration.millis(500));
                            delayTrasHundir.setOnFinished(e -> turnoMaquina());
                            delayTrasHundir.play();
                        } else {
                            // Si el juego termina (máquina gana)
                            turnoJugador = false;
                            juegoObservable.notificarCambioTurno(false);
                        }
                        break;
                }
                celda.setDisable(true);
            }
        });

        delayPrincipal.play();
    }

    /**
     * Encuentra una celda en el tablero del jugador por coordenadas (índice 0-9).
     * @param fila Fila del tablero.
     * @param columna Columna del tablero.
     * @return El Pane de la celda.
     */
    private Pane encontrarCeldaTableroJugador(int fila, int columna) {
        return obtenerCelda(tableroJugador, fila, columna);
    }

    // ========== VERIFICACIÓN DE FIN DE JUEGO ==========

    /**
     * Verifica si el juego ha terminado (si la flota del jugador o la máquina ha sido hundida).
     */
    /**
     * Verifica si el juego ha terminado.
     */
    /**
     * Verifica si el juego ha terminado.
     * Notifica al sistema de Observadores el resultado final (victoria o derrota).
     */
    private void verificarFinJuego() {
        if (maquina.haPerdido()) {
            juegoIniciado = false;

            // NOTIFICAR FIN DEL JUEGO: Esto activará el 'case "JUEGO_TERMINADO"' en ObservadorInterfaz,
            // el cual ahora muestra la ventana Alert con el mensaje de victoria.
            juegoObservable.notificarJuegoTerminado("Jugador Humano");

            System.out.println("🎉 ¡EL JUGADOR GANA!");

            // Mostrar estadísticas de la pila al final del juego
            mostrarEstadisticasPila();

        } else if (jugador.haPerdido()) {
            juegoIniciado = false;

            // NOTIFICAR FIN DEL JUEGO: Esto activará el 'case "JUEGO_TERMINADO"' en ObservadorInterfaz,
            // el cual ahora muestra la ventana Alert con el mensaje de derrota.
            juegoObservable.notificarJuegoTerminado("Máquina");

            System.out.println("😢 ¡LA MÁQUINA GANA!");

            // Mostrar estadísticas de la pila al final del juego
            mostrarEstadisticasPila();
        }
    }

    // ========== MÉTODOS DE ESTRUCTURA DE DATOS ==========

    /**
     * Registra un movimiento en la pila de historial (PilaMovimientos).
     * @param jugador El jugador que realizó el movimiento.
     * @param resultadoStr El resultado del disparo ("AGUA", "TOCADO", etc.).
     * @param fila Fila del movimiento.
     * @param col Columna del movimiento.
     * @param esTurnoJugador Indica si fue el turno del jugador humano.
     */
    private void registrarMovimientoEnPila(Jugador jugador, String resultadoStr,
                                           int fila, int col, boolean esTurnoJugador) {
        try {
            // Convertir resultado string a enum
            TipoResultado resultado = convertirResultado(resultadoStr);

            // Crear objeto Movimiento
            Movimiento movimiento = new Movimiento(
                    jugador.getNickname(),
                    fila,
                    col,
                    resultado,
                    esTurnoJugador
            );

            // Apilar el movimiento
            pilaMovimientos.apilar(movimiento);

            System.out.println("📝 Movimiento registrado en pila: " + movimiento);
            System.out.println("   tamanho pila: " + pilaMovimientos.tamanio() +
                    "/" + pilaMovimientos.getCapacidad());

            // Notificar a observadores
            juegoObservable.notificarObservadores(
                    "MOVIMIENTO_REGISTRADO",
                    jugador,
                    movimiento.toDetailedString()
            );

        } catch (Exception e) {
            System.err.println("❌ Error al registrar movimiento en pila: " + e.getMessage());
            juegoObservable.notificarError("Error en estructura de datos: " + e.getMessage());
        }
    }

    /**
     * Convierte la cadena de resultado a su equivalente Enum TipoResultado.
     * @param resultadoStr La cadena de resultado.
     * @return El enum TipoResultado correspondiente.
     */
    private TipoResultado convertirResultado(String resultadoStr) {
        switch (resultadoStr.toUpperCase()) {
            case "AGUA": return TipoResultado.AGUA;
            case "TOCADO": return TipoResultado.TOCADO;
            case "HUNDIDO": return TipoResultado.HUNDIDO;
            case "REPETIDO": return TipoResultado.REPETIDO;
            case "INVALIDO": return TipoResultado.INVALIDO;
            default: return TipoResultado.INVALIDO;
        }
    }

    /**
     * Muestra el historial de los últimos 20 movimientos registrados en la pila.
     */
    @FXML
    private void mostrarHistorialMovimientos() {
        if (pilaMovimientos == null || pilaMovimientos.estaVacia()) {
            mostrarAlerta(AlertType.INFORMATION, "Historial Vacío",
                    "No hay movimientos registrados aún.");
            return;
        }

        StringBuilder historial = new StringBuilder();
        historial.append("=== HISTORIAL DE MOVIMIENTOS ===\n");
        historial.append("Total movimientos: ").append(pilaMovimientos.tamanio()).append("\n\n");

        // Obtener últimos 20 movimientos
        List<Movimiento> ultimos = pilaMovimientos.ultimosMovimientos(20);

        int contador = 1;
        for (Movimiento movimiento : ultimos) {
            historial.append(contador).append(". ").append(movimiento.toDetailedString()).append("\n");
            contador++;
        }

        if (pilaMovimientos.tamanio() > 20) {
            historial.append("\n... y ").append(pilaMovimientos.tamanio() - 20)
                    .append(" movimientos más.");
        }

        // Mostrar en alerta
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Historial de Movimientos");
        alert.setHeaderText("Estructura de Datos: Pila (LIFO)");
        alert.setContentText(historial.toString());
        alert.setWidth(600);
        alert.setHeight(400);
        alert.showAndWait();
    }

    /**
     * Muestra estadísticas de la pila (conteo por resultado).
     */
    public void mostrarEstadisticasPila() {
        if (pilaMovimientos == null) {
            System.out.println("❌ Pila no inicializada");
            return;
        }

        System.out.println("\n=== ESTADÍSTICAS DE PILA ===");
        System.out.println(pilaMovimientos.mostrarContenido());

        // Contar movimientos por tipo
        List<Movimiento> todos = pilaMovimientos.toList();
        Map<TipoResultado, Integer> conteo = new HashMap<>();

        for (TipoResultado tipo : TipoResultado.values()) {
            conteo.put(tipo, 0);
        }

        for (Movimiento mov : todos) {
            conteo.put(mov.getResultado(), conteo.get(mov.getResultado()) + 1);
        }

        System.out.println("\nConteo por resultado:");
        for (Map.Entry<TipoResultado, Integer> entry : conteo.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
    }

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Muestra una alerta emergente en pantalla (usado para Historial y Fin de Juego).
     * @param tipo Tipo de alerta (INFORMATION, WARNING, ERROR).
     * @param titulo Título de la alerta.
     * @param mensaje Contenido del mensaje.
     */
    private void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // ========== BOTONES DE CONTROL ==========

    /**
     * Inicia la fase de disparos si todos los barcos han sido colocados.
     * Deshabilita el panel de colocación.
     */
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
                        "Tu turno: Haz clic en el tablero de la derecha para disparar.\n");

        System.out.println("🎮 ¡JUEGO INICIADO!");
        System.out.println("🔫 Turno del jugador");
        System.out.println("🎨 Figuras 2D JavaFX activadas");
    }

    /**
     * Muestra el tablero completo del oponente (solo visible antes de iniciar el juego).
     */
    @FXML
    private void mostrarTableroOponente() {
        // HU-3: Visualización del tablero del oponente (para profesor)
        if (!juegoIniciado) {
            String tableroCompleto = maquina.mostrarTableroConBarcos();

            // =================================================================
            // CAMBIO CLAVE: Crear Alert manualmente para aplicar estilo monoespaciado
            // =================================================================
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Tablero de la Máquina");
            alert.setHeaderText("Esta vista es solo para verificación De Los Barcos Enemigos:");

            // Crea un Label para contener el texto del tablero
            Label contentLabel = new Label(tableroCompleto);

            // ** Aplicar estilo CSS para forzar una fuente monoespaciada **
            contentLabel.setStyle("-fx-font-family: 'Courier New', Monospaced; -fx-font-size: 14px;");

            // Envolver el Label en un contenedor para que se muestre correctamente
            VBox dialogContent = new VBox(10);
            dialogContent.getChildren().add(contentLabel);

            // Establecer el contenido gráfico del Alert
            alert.getDialogPane().setContent(dialogContent);

            // Ajustar el tamaño del diálogo para que quepa el tablero
            alert.getDialogPane().setPrefWidth(350);
            alert.getDialogPane().setPrefHeight(350);

            alert.showAndWait();
            // =================================================================

            juegoObservable.notificarObservadores(
                    JuegoObservable.INFORMACION,
                    null,
                    "Mostrado tablero de la máquina (HU-3) con figuras 2D"
            );
            System.out.println("👁️ Mostrando tablero de la máquina (HU-3) con figuras 2D");
        } else {
            // Mantener la alerta de advertencia simple
            mostrarAlerta(AlertType.WARNING, "No disponible",
                    "Esta opción solo está disponible antes de iniciar el juego.");
        }
    }

    /**
     * Reinicia el juego, reestablece los jugadores, limpia los tableros y recrea los barcos.
     */
    @FXML
    private void reiniciarJuego() {
        // NOTIFICAR REINICIO
        juegoObservable.notificarObservadores(
                JuegoObservable.JUEGO_REINICIADO,
                null,
                "Juego reiniciado"
        );

        // Reiniciar todo el juego
        jugador = new Jugador("Humano");
        maquina = new Jugador("Máquina");
        maquina.colocarBarcosAleatoriamente();

        // Reiniciar la pila de movimientos
        if (pilaMovimientos != null) {
            pilaMovimientos.vaciar();
            System.out.println("🗑️ Pila de movimientos vaciada");
        }

        juegoIniciado = false;
        turnoJugador = true;
        panelBarcos.setDisable(false);

        // Limpiar tableros visuales con figuras 2D
        limpiarTableroVisualFiguras(tableroJugador);
        limpiarTableroVisualFiguras(tableroOponente);

        // Recrear panel de barcos con figuras 2D
        panelBarcos.getChildren().clear();
        crearPanelBarcos();

        // Notificar a los observadores
        juegoObservable.notificarObservadores(
                "JUEGO_REINICIADO_COMPLETO",
                jugador,
                "Juego completamente reiniciado con figuras 2D"
        );

        System.out.println("🔄 Juego reiniciado");
        System.out.println("📊 Estructura de datos: PilaMovimientos reiniciada");
        System.out.println("🎨 Figuras 2D JavaFX: Reiniciadas");
    }

    /**
     * Limpia el tablero visual (VERSIÓN ANTIGUA).
     * @param tablero El GridPane a limpiar.
     */
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

    /**
     * Limpia el tablero visual y recrea la figura 2D de celda vacía en cada Pane.
     * @param tablero El GridPane a limpiar.
     */
    private void limpiarTableroVisualFiguras(GridPane tablero) {
        for (javafx.scene.Node node : tablero.getChildren()) {
            if (node instanceof Pane) {
                Pane celda = (Pane) node;
                // Limpiar todas las figuras
                celda.getChildren().clear();

                // Agregar figura 2D de celda vacía
                Group celdaFigura = Figuras2DUtils.crearCeldaTablero(40);
                celda.getChildren().add(celdaFigura);

                celda.setDisable(false);
            }
        }
        System.out.println("🧹 Tablero limpiado y figuras 2D reiniciadas");
    }
}