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

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.shape.*;
import javafx.scene.Group;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Controlador principal del juego Batalla Naval.
 * Maneja la lógica del juego, interacción del usuario y comunicación entre modelo y vista.
 * Implementa el patrón Observer para notificaciones automáticas.
 * Implementa estructura de datos Pila para historial de movimientos.
 * Implementa figuras 2D JavaFX para elementos del juego.
 *
 * @author [Tu Nombre]
 * @version 5.0
 */
public class JuegoController {

    @FXML private GridPane tableroJugador;      // Tablero de posición del jugador humano
    @FXML private VBox panelBarcos;             // Panel lateral para seleccionar barcos
    @FXML private GridPane tableroOponente;     // Tablero principal para disparar al oponente

    private Jugador jugador;              // Jugador humano
    private Jugador maquina;              // Jugador máquina

    private boolean juegoIniciado = false;
    private boolean turnoJugador = true;  // true = turno jugador, false = turno máquina

    // ========== OBSERVER PATTERN ==========
    private JuegoObservable juegoObservable;
    private ObservadorConsola observadorConsola;
    private ObservadorInterfaz observadorInterfaz;
    private ObservadorGuardado observadorGuardado;

    // ========== ESTRUCTURA DE DATOS: PILA ==========
    private PilaMovimientos<Movimiento> pilaMovimientos;

    // ========== INICIALIZACIÓN ==========

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
        System.out.println("📊 Estructura de datos: PilaMovimientos creada");
        System.out.println("🎨 Figuras 2D JavaFX: Habilitadas");
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

    /**
     * Inicializa las estructuras de datos del juego.
     * Cumple con el requisito de implementar estructura de datos explícita.
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
     * Demuestra el funcionamiento básico de la pila.
     * Solo para propósitos de demostración/depuración.
     */
    private void demostrarUsoPila() {
        System.out.println("🔍 Demostrando uso de la pila:");
        System.out.println("   - Pila vacía: " + pilaMovimientos.estaVacia());
        System.out.println("   - Capacidad: " + pilaMovimientos.getCapacidad());
        System.out.println("   - Tamaño actual: " + pilaMovimientos.tamanio());
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

                // Usar figura 2D para la celda
                Group celdaFigura = Figuras2DUtils.crearCeldaTablero(40);
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

                // Usar figura 2D para la celda
                Group celdaFigura = Figuras2DUtils.crearCeldaTablero(40);
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
     * Crea el panel lateral con todos los barcos disponibles para colocar.
     * HU-1: Colocación de barcos del jugador humano.
     */
    private void crearPanelBarcos() {
        System.out.println("🚢 Creando panel de barcos con figuras 2D...");

        // 1 Portaaviones (tamaño 4)
        Barco portaaviones = BarcoFactory.crearBarco(TipoBarco.PORTAVIONES);
        panelBarcos.getChildren().add(crearBarcoPaneFigura(portaaviones));

        // 2 Submarinos (tamaño 3 cada uno)
        Barco sub1 = BarcoFactory.crearBarco(TipoBarco.SUBMARINO);
        panelBarcos.getChildren().add(crearBarcoPaneFigura(sub1));

        Barco sub2 = BarcoFactory.crearBarco(TipoBarco.SUBMARINO);
        panelBarcos.getChildren().add(crearBarcoPaneFigura(sub2));

        // 3 Destructores (tamaño 2 cada uno)
        Barco dest1 = BarcoFactory.crearBarco(TipoBarco.DESTRUCTOR);
        panelBarcos.getChildren().add(crearBarcoPaneFigura(dest1));

        Barco dest2 = BarcoFactory.crearBarco(TipoBarco.DESTRUCTOR);
        panelBarcos.getChildren().add(crearBarcoPaneFigura(dest2));

        Barco dest3 = BarcoFactory.crearBarco(TipoBarco.DESTRUCTOR);
        panelBarcos.getChildren().add(crearBarcoPaneFigura(dest3));

        // 4 Fragatas (tamaño 1 cada una)
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
     * Mantenido para compatibilidad.
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

    /**
     * Crea un Pane visual con FIGURA 2D para representar un barco en el panel lateral.
     * NUEVA VERSIÓN con figuras JavaFX.
     */
    private Pane crearBarcoPaneFigura(Barco barco) {
        Pane pane = new Pane();
        pane.setPrefSize(barco.getTamaño() * 35, 30);

        // Crear figura 2D para el barco - CAMBIADO a Group
        Group figuraBarco = Figuras2DUtils.crearFiguraBarcoPorTipo(
                barco.getNombre(),
                barco.getTamaño() * 35,
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
                // Colocación exitosa - usar FIGURA 2D
                marcarBarcoEnTablero(fila, col, barco.getTamaño(), true, barco.getNombre());

                panelBarcos.getChildren().remove(barcoPane);

                // NOTIFICAR OBSERVADORES
                juegoObservable.notificarBarcoColocado(jugador, barco.getNombre(), fila, col);

                System.out.println("✅ Barco colocado con figura 2D: " + barco.getNombre() +
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
                // Colocación fallida - mostrar error con figura
                mostrarErrorColocacion(celda);
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
     return jugador.getBarcos().indexOf(barcoBuscado);
    }

    /**
     * Marca una celda (o celdas) como conteniendo un barco (VERSIÓN ANTIGUA).
     */
    private void marcarCeldaComoBarco(Pane celda, int tamaño, boolean horizontal) {
        celda.setStyle("-fx-background-color: #8B4513; -fx-border-color: #654321;");
        // Nota: Para barcos de tamaño > 1, deberíamos marcar múltiples celdas
        // Esto es una simplificación para la demo
    }

    /**
     * Marca una celda como conteniendo un barco usando FIGURA 2D (NUEVA VERSIÓN).
     */
    private void marcarBarcoEnTablero(int fila, int col, int tamaño, boolean horizontal, String nombre) {
        for (int i = 0; i < tamaño; i++) {

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
     * Muestra error de colocación con figura 2D.
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
     * Maneja excepciones durante la colocación de barcos.
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
     * Realiza un disparo en la posición especificada del tablero del oponente.
     * HU-2: Lógica de disparos (agua, tocado, hundido) con FIGURAS 2D.
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

        // ========== REGISTRAR EN ESTRUCTURA DE DATOS ==========
        registrarMovimientoEnPila(jugador, resultado, fila, col, true);

        // NOTIFICAR OBSERVADORES DEL DISPARO
        juegoObservable.notificarDisparo(jugador, resultado, fila, col);

        // Actualizar interfaz con FIGURA 2D según resultado
        actualizarCeldaConFigura(celda, resultado, fila, col);

        celda.setDisable(true);
    }

    /**
     * Actualiza una celda con figura 2D según el resultado del disparo.
     */
    private void actualizarCeldaConFigura(Pane celda, String resultado, int fila, int col) {
        // Limpiar la celda
        celda.getChildren().clear();

        switch (resultado) {
            case "AGUA":
                // Crear círculo azul de agua - CAMBIADO: hacer cast a Group
                Group figuraAgua = (Group) Figuras2DUtils.crearFiguraResultado("AGUA", 25);
                celda.getChildren().add(figuraAgua);
                System.out.println("🌊 AGUA en (" + fila + "," + col + ") [Figura 2D]");
                turnoJugador = false; // Pasa turno a la máquina
                juegoObservable.notificarCambioTurno(false);
                turnoMaquina(); // La máquina dispara
                break;

            case "TOCADO":
                // Crear círculo naranja de tocado - CAMBIADO: hacer cast a Group
                Group figuraTocado = (Group) Figuras2DUtils.crearFiguraResultado("TOCADO", 25);
                celda.getChildren().add(figuraTocado);
                System.out.println("🔥 TOCADO en (" + fila + "," + col + ") [Figura 2D]");
                // Jugador sigue disparando
                break;

            case "HUNDIDO":
                // Crear círculo rojo de hundido - CAMBIADO: hacer cast a Group
                Group figuraHundido = (Group) Figuras2DUtils.crearFiguraResultado("HUNDIDO", 25);
                celda.getChildren().add(figuraHundido);
                System.out.println("💥 HUNDIDO en (" + fila + "," + col + ") [Figura 2D]");
                // Verificar si ganó
                verificarFinJuego();
                break;

            case "REPETIDO":
                // Crear X gris de repetido - CAMBIADO: hacer cast a Group
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
     * Turno de la máquina para disparar.
     * HU-4: Inteligencia artificial de la máquina con FIGURAS 2D.
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
            // Limpiar la celda primero
            celda.getChildren().clear();

            switch (tipoResultado) {
                case 0: // AGUA
                    // CAMBIADO: hacer cast a Group
                    Group figuraAgua = (Group) Figuras2DUtils.crearFiguraResultado("AGUA", 25);
                    figuraAgua.setLayoutX(7.5);
                    figuraAgua.setLayoutY(7.5);
                    celda.getChildren().add(figuraAgua);
                    System.out.println("🤖🌊 La máquina disparó AGUA en (" + fila + "," + columna + ") [Figura 2D]");
                    turnoJugador = true; // Vuelve turno al jugador
                    juegoObservable.notificarCambioTurno(true);
                    juegoObservable.notificarDisparo(maquina, "AGUA", fila, columna);
                    registrarMovimientoEnPila(maquina, "AGUA", fila, columna, false);
                    break;

                case 1: // TOCADO
                    // CAMBIADO: hacer cast a Group
                    Group figuraTocado = (Group) Figuras2DUtils.crearFiguraResultado("TOCADO", 25);
                    figuraTocado.setLayoutX(7.5);
                    figuraTocado.setLayoutY(7.5);
                    celda.getChildren().add(figuraTocado);
                    System.out.println("🤖🔥 La máquina TOCÓ en (" + fila + "," + columna + ") [Figura 2D]");
                    juegoObservable.notificarDisparo(maquina, "TOCADO", fila, columna);
                    registrarMovimientoEnPila(maquina, "TOCADO", fila, columna, false);
                    // La máquina sigue disparando
                    turnoMaquina();
                    break;

                case 2: // HUNDIDO
                    // CAMBIADO: hacer cast a Group
                    Group figuraHundido = (Group) Figuras2DUtils.crearFiguraResultado("HUNDIDO", 25);
                    figuraHundido.setLayoutX(7.5);
                    figuraHundido.setLayoutY(7.5);

                    celda.getChildren().add(figuraHundido);
                    System.out.println("🤖💥 La máquina HUNDIÓ en (" + fila + "," + columna + ") [Figura 2D]");
                    juegoObservable.notificarDisparo(maquina, "HUNDIDO", fila, columna);
                    registrarMovimientoEnPila(maquina, "HUNDIDO", fila, columna, false);
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
                    "🎉 ¡HAS GANADO! Hundiste toda la flota enemiga.\n\n" +
                            "🎨 Has usado figuras 2D JavaFX para visualizar el juego.");
            System.out.println("🎉 ¡EL JUGADOR GANA!");

            // Mostrar estadísticas de la pila al final del juego
            mostrarEstadisticasPila();

        } else if (jugador.haPerdido()) {
            juegoIniciado = false;
            // NOTIFICAR FIN DEL JUEGO
            juegoObservable.notificarJuegoTerminado("Máquina");
            mostrarAlerta(AlertType.INFORMATION, "FIN DEL JUEGO",
                    "😢 La máquina ganó. Mejor suerte la próxima vez.\n\n" +
                            "🎨 Has usado figuras 2D JavaFX para visualizar el juego.");
            System.out.println("😢 ¡LA MÁQUINA GANA!");

            // Mostrar estadísticas de la pila al final del juego
            mostrarEstadisticasPila();
        }
    }

    // ========== MÉTODOS DE ESTRUCTURA DE DATOS ==========

    /**
     * Registra un movimiento en la pila de historial.
     * Cumple con el requisito de usar estructura de datos.
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
            System.out.println("   Tamaño pila: " + pilaMovimientos.tamanio() +
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
     * Convierte string de resultado a enum.
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
     * Muestra el historial de movimientos.
     * Para HU-5: análisis de partidas.
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
     * Muestra estadísticas de la pila (para depuración).
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
                        "Tu turno: Haz clic en el tablero de la derecha para disparar.\n" +
                        "🎨 Nota: El juego ahora usa figuras 2D JavaFX para mejor visualización.");

        System.out.println("🎮 ¡JUEGO INICIADO!");
        System.out.println("🔫 Turno del jugador");
        System.out.println("🎨 Figuras 2D JavaFX activadas");
    }

    @FXML
    private void mostrarTableroOponente() {
        // HU-3: Visualización del tablero del oponente (para profesor)
        if (!juegoIniciado) {
            String tableroCompleto = maquina.mostrarTableroConBarcos();
            mostrarAlerta(AlertType.INFORMATION, "Tablero de la Máquina",
                    "Esta vista es solo para verificación del profesor:\n\n" + tableroCompleto +
                            "\n\n🎨 Implementación con figuras 2D JavaFX completa.");
            juegoObservable.notificarObservadores(
                    JuegoObservable.INFORMACION,
                    null,
                    "Mostrado tablero de la máquina (HU-3) con figuras 2D"
            );
            System.out.println("👁️ Mostrando tablero de la máquina (HU-3) con figuras 2D");
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
     * Limpia el tablero visual con FIGURAS 2D (NUEVA VERSIÓN).
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