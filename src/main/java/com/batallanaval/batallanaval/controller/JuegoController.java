package com.batallanaval.batallanaval.controller;

import com.batallanaval.model.*;
import com.batallanaval.model.TipoBarco;  // Import del ENUM
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Controlador principal del juego Batalla Naval.
 * Maneja la lógica del juego, interacción del usuario y comunicación entre modelo y vista.
 * Implementa la historia de usuario HU-1 (colocación de barcos) y HU-2 (realización de disparos).
 *
 * @author [Tu Nombre]
 * @version 1.0
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

    /**
     * Método de inicialización llamado automáticamente por JavaFX.
     * Configura los componentes iniciales del juego.
     */
    public void initialize() {
        jugador = new Jugador("Humano");
        maquina = new Jugador("Máquina");

        crearTableroVisual();       // Crear tablero de posición del jugador
        crearTableroOponente();     // Crear tablero principal para disparos
        colocarBarcosMaquina();     // Colocar flota de la máquina aleatoriamente
        crearPanelBarcos();         // Crear panel de selección de barcos
    }

    /**
     * Crea el tablero visual 10x10 para el jugador humano.
     * Cada celda es un Pane con eventos de arrastre para colocar barcos.
     */
    public void crearTableroVisual() {
        for (int fila = 0; fila < 10; fila++) {
            for (int col = 0; col < 10; col++) {
                Pane celda = new Pane();
                celda.setPrefSize(30, 30);
                celda.getStyleClass().add("pane-celda");

                final int f = fila, c = col;
                celda.setOnMouseDragReleased(e -> colocarBarco(f, c, celda));

                tableroJugador.add(celda, col, fila);
            }
        }
    }

    /**
     * Crea el tablero 10x10 para el oponente (máquina).
     * Cada celda tiene evento de clic para realizar disparos.
     */
    private void crearTableroOponente() {
        for (int fila = 0; fila < 10; fila++) {
            for (int col = 0; col < 10; col++) {
                Pane celda = new Pane();
                celda.setPrefSize(30, 30);
                celda.getStyleClass().add("pane-celda");

                final int f = fila, c = col;
                celda.setOnMouseClicked(e -> disparar(f, c, celda));

                tableroOponente.add(celda, col, fila);
            }
        }
    }

    /**
     * Crea el panel lateral con todos los barcos disponibles para colocar.
     * Cada barco se representa como un Pane arrastrable.
     * HU-1: Colocación de barcos del jugador humano.
     */
    private void crearPanelBarcos() {
        // 1 Portaaviones (tamaño 4)
        Barco portaaviones = new Barco(TipoBarco.PORTAVIONES);
        panelBarcos.getChildren().add(crearBarcoPane(portaaviones));

        // 2 Submarinos (tamaño 3 cada uno)
        Barco sub1 = new Barco(TipoBarco.SUBMARINO);
        panelBarcos.getChildren().add(crearBarcoPane(sub1));

        Barco sub2 = new Barco(TipoBarco.SUBMARINO);
        panelBarcos.getChildren().add(crearBarcoPane(sub2));

        // 3 Destructores (tamaño 2 cada uno)
        Barco dest1 = new Barco(TipoBarco.DESTRUCTOR);
        panelBarcos.getChildren().add(crearBarcoPane(dest1));

        Barco dest2 = new Barco(TipoBarco.DESTRUCTOR);
        panelBarcos.getChildren().add(crearBarcoPane(dest2));

        Barco dest3 = new Barco(TipoBarco.DESTRUCTOR);
        panelBarcos.getChildren().add(crearBarcoPane(dest3));

        // 4 Fragatas (tamaño 1 cada una)
        Barco frag1 = new Barco(TipoBarco.FRAGATA);
        panelBarcos.getChildren().add(crearBarcoPane(frag1));

        Barco frag2 = new Barco(TipoBarco.FRAGATA);
        panelBarcos.getChildren().add(crearBarcoPane(frag2));

        Barco frag3 = new Barco(TipoBarco.FRAGATA);
        panelBarcos.getChildren().add(crearBarcoPane(frag3));

        Barco frag4 = new Barco(TipoBarco.FRAGATA);
        panelBarcos.getChildren().add(crearBarcoPane(frag4));
    }

    /**
     * Crea un Pane visual para representar un barco en el panel lateral.
     *
     * @param barco El objeto Barco a representar visualmente
     * @return Pane configurado con el tamaño y eventos adecuados
     */
    private Pane crearBarcoPane(Barco barco) {
        Pane pane = new Pane();
        pane.setPrefSize(barco.getTamaño() * 30, 30);
        pane.getStyleClass().add("barco");

        // Almacenar referencia al objeto Barco en el UserData del Pane
        pane.setUserData(barco);

        // Configurar arrastre del barco
        pane.setOnDragDetected(e -> pane.startFullDrag());
        return pane;
    }

    /**
     * Coloca un barco en la posición especificada del tablero del jugador.
     * HU-1: Validación de colocación de barcos.
     *
     * @param fila Fila donde se quiere colocar el barco (0-9)
     * @param col Columna donde se quiere colocar el barco (0-9)
     * @param celda Celda visual donde se soltó el barco
     */
    private void colocarBarco(int fila, int col, Pane celda) {
        if (panelBarcos.getChildren().isEmpty()) {
            System.out.println("⚠️ No hay barcos disponibles para colocar");
            return;
        }

        // Obtener el primer barco del panel lateral
        Pane barcoPane = (Pane) panelBarcos.getChildren().get(0);

        // Recuperar el objeto Barco almacenado en UserData
        Barco barco = (Barco) barcoPane.getUserData();

        if (barco == null) {
            System.err.println("❌ Error: Barco no encontrado en UserData");
            return;
        }

        // Intentar colocar el barco en el tablero (horizontal por defecto)
        if (jugador.getTablero().colocarBarco(barco, fila, col, true)) {
            // Colocación exitosa
            celda.getStyleClass().add("barco");
            panelBarcos.getChildren().remove(barcoPane);
            System.out.println("✅ Barco colocado: " + barco.getNombre() + " en (" + fila + "," + col + ")");
        } else {
            // Colocación fallida (superposición o fuera de límites)
            celda.setStyle("-fx-background-color: red;");
            System.out.println("❌ No se pudo colocar el barco en (" + fila + "," + col + ")");
        }
    }

    /**
     * Realiza un disparo en la posición especificada del tablero del oponente.
     * HU-2: Lógica de disparos (agua, tocado, hundido).
     *
     * @param fila Fila del disparo (0-9)
     * @param col Columna del disparo (0-9)
     * @param celda Celda visual donde se hizo clic
     */
    private void disparar(int fila, int col, Pane celda) {
        // Obtener la matriz de barcos del tablero de la máquina
        Barco[][] t = maquina.getTablero().getTablero();
        Barco b = t[fila][col];

        if (b == null) {
            // Disparo al agua
            celda.getStyleClass().add("agua");
            celda.setDisable(true);
            System.out.println("🌊 AGUA en (" + fila + "," + col + ")");

            // Aquí debería pasar el turno a la máquina (por implementar)
            // turnoMaquina();
        } else {
            // Hay un barco en la posición
            boolean tocado = false;

            // Buscar la posición relativa en el barco
            // NOTA: Esta lógica necesita mejorar para encontrar la posición exacta
            for (int i = 0; i < b.getTamaño(); i++) {
                if (b.recibirDisparo(i)) {
                    tocado = true;
                    break;
                }
            }

            if (b.estaHundido()) {
                // Barco hundido
                celda.getStyleClass().add("hundido");
                System.out.println("💥 HUNDIDO " + b.getNombre() + " en (" + fila + "," + col + ")");

                // El jugador sigue disparando
                // TODO: Verificar si ganó el juego
            } else if (tocado) {
                // Barco tocado
                celda.getStyleClass().add("tocado");
                System.out.println("🔥 TOCADO en (" + fila + "," + col + ")");

                // El jugador sigue disparando
            }
            celda.setDisable(true);
        }
    }

    /**
     * Coloca aleatoriamente todos los barcos de la máquina en su tablero.
     * HU-4: Implementación de la inteligencia artificial de la máquina.
     */
    private void colocarBarcosMaquina() {
        System.out.println("🤖 Colocando barcos de la máquina...");

        // 1 Portaaviones
        colocarBarcoAleatorio(maquina, new Barco(TipoBarco.PORTAVIONES));

        // 2 Submarinos
        colocarBarcoAleatorio(maquina, new Barco(TipoBarco.SUBMARINO));
        colocarBarcoAleatorio(maquina, new Barco(TipoBarco.SUBMARINO));

        // 3 Destructores
        colocarBarcoAleatorio(maquina, new Barco(TipoBarco.DESTRUCTOR));
        colocarBarcoAleatorio(maquina, new Barco(TipoBarco.DESTRUCTOR));
        colocarBarcoAleatorio(maquina, new Barco(TipoBarco.DESTRUCTOR));

        // 4 Fragatas
        colocarBarcoAleatorio(maquina, new Barco(TipoBarco.FRAGATA));
        colocarBarcoAleatorio(maquina, new Barco(TipoBarco.FRAGATA));
        colocarBarcoAleatorio(maquina, new Barco(TipoBarco.FRAGATA));
        colocarBarcoAleatorio(maquina, new Barco(TipoBarco.FRAGATA));

        System.out.println("✅ Barcos de la máquina colocados");
    }

    /**
     * Coloca un barco en una posición aleatoria válida del tablero.
     *
     * @param jugador Jugador (máquina) en cuyo tablero colocar el barco
     * @param barco Barco a colocar
     */
    private void colocarBarcoAleatorio(Jugador jugador, Barco barco) {
        boolean colocado = false;
        int intentos = 0;
        final int MAX_INTENTOS = 100; // Evitar bucle infinito

        while (!colocado && intentos < MAX_INTENTOS) {
            int fila = (int) (Math.random() * 10);
            int col = (int) (Math.random() * 10);
            boolean horizontal = Math.random() > 0.5;

            colocado = jugador.getTablero().colocarBarco(barco, fila, col, horizontal);
            intentos++;

            if (colocado) {
                System.out.println("   • " + barco.getNombre() + " colocado en (" +
                        fila + "," + col + ") " +
                        (horizontal ? "horizontal" : "vertical"));
            }
        }

        if (!colocado) {
            System.err.println("⚠️ No se pudo colocar " + barco.getNombre() + " después de " + MAX_INTENTOS + " intentos");
        }
    }
}
