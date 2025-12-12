package com.batallanaval.batallanaval.model;

import com.batallanaval.batallanaval.patterns.composite.FlotaComposite;
import com.batallanaval.batallanaval.patterns.factory.BarcoFactory;
import com.batallanaval.batallanaval.exceptions.BarcoSuperpuestoException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa un jugador en el juego Batalla Naval.
 *
 * Utiliza patrón Composite para manejar la flota de barcos.
 *
 *
 * @version 2.0
 */
public class Jugador implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String nickname;
    private final Tablero tableroPropio;      // Donde coloca sus barcos
    private final Tablero tableroDisparos;    // Donde registra disparos al oponente
    private final FlotaComposite flota;       // Flota usando patrón Composite
    private int barcosHundidosPropios;        // Contador de barcos propios hundidos
    private int barcosHundidosEnemigos;       // Contador de barcos enemigos hundidos
    private List<int[]> impactosRecientes = new ArrayList<>(); // Registra coordenadas [fila, col] de un barco TOCADO
    private List<int[]> posiblesObjetivos = new ArrayList<>(); // Celdas adyacentes pendientes de ataque (Modo Caza)


    /**
     * Constructor principal.
     *
     * @param nickname Nombre del jugador
     */
    public Jugador(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new IllegalArgumentException("El nickname no puede ser vacío");
        }

        this.nickname = nickname;
        this.tableroPropio = new Tablero();
        this.tableroDisparos = new Tablero();
        this.flota = new FlotaComposite("Flota de " + nickname);
        this.barcosHundidosPropios = 0;
        this.barcosHundidosEnemigos = 0;

        inicializarFlota();
    }

    /**
     * Constructor de copia (TDA-friendly).
     *
     * @param otro Jugador a copiar
     */
    public Jugador(Jugador otro) {
        this.nickname = otro.nickname;
        this.tableroPropio = new Tablero(otro.tableroPropio);
        this.tableroDisparos = new Tablero(otro.tableroDisparos);
        this.flota = otro.flota; // Nota: Composite debería tener constructor de copia
        this.barcosHundidosPropios = otro.barcosHundidosPropios;
        this.barcosHundidosEnemigos = otro.barcosHundidosEnemigos;
    }

    // ========== INICIALIZACIÓN ==========

    /**
     * Inicializa la flota con todos los barcos requeridos.
     * Según el enunciado: 1 portaaviones(4), 2 submarinos(3),
     * 3 destructores(2), 4 fragatas(1)
     */
    private void inicializarFlota() {
        // Usar la Factory para crear barcos
        flota.agregar(BarcoFactory.crearBarco(TipoBarco.PORTAVIONES));

        flota.agregar(BarcoFactory.crearBarco(TipoBarco.SUBMARINO));
        flota.agregar(BarcoFactory.crearBarco(TipoBarco.SUBMARINO));

        flota.agregar(BarcoFactory.crearBarco(TipoBarco.DESTRUCTOR));
        flota.agregar(BarcoFactory.crearBarco(TipoBarco.DESTRUCTOR));
        flota.agregar(BarcoFactory.crearBarco(TipoBarco.DESTRUCTOR));

        flota.agregar(BarcoFactory.crearBarco(TipoBarco.FRAGATA));
        flota.agregar(BarcoFactory.crearBarco(TipoBarco.FRAGATA));
        flota.agregar(BarcoFactory.crearBarco(TipoBarco.FRAGATA));
        flota.agregar(BarcoFactory.crearBarco(TipoBarco.FRAGATA));
    }

    // ========== COLOCACIÓN DE BARCOS ==========

    /**
     * Coloca un barco específico de la flota en el tablero.
     * HU-1: Colocación de barcos del jugador humano.
     *
     * @param indiceBarco Índice del barco en la flota (0-9)
     * @param fila Fila inicial (0-9)
     * @param columna Columna inicial (0-9)
     * @param horizontal true para horizontal, false para vertical
     * @return true si se colocó exitosamente
     * @throws IndexOutOfBoundsException si el índice es inválido
     */
    public boolean colocarBarco(int indiceBarco, int fila, int columna, boolean horizontal) {
        List<Barco> barcos = flota.getBarcos();
        if (indiceBarco < 0 || indiceBarco >= barcos.size()) {
            throw new IndexOutOfBoundsException(
                    "Índice de barco inválido: " + indiceBarco + ". Debe ser 0-" + (barcos.size()-1)
            );
        }

        Barco barco = barcos.get(indiceBarco);
        if (tableroPropio.colocarBarco(barco, fila, columna, horizontal)) {
            // Actualizar posición del barco
            barco.setPosicion(fila, columna);
            barco.setHorizontal(horizontal);
            return true;
        }
        return false;
    }

    /**
     * Coloca un barco específico por su tipo.
     *
     * @param tipo Tipo de barco a colocar
     * @param fila Fila inicial
     * @param columna Columna inicial
     * @param horizontal Orientación
     * @return true si se colocó exitosamente
     */
    public boolean colocarBarcoPorTipo(TipoBarco tipo, int fila, int columna, boolean horizontal) {
        for (Barco barco : flota.getBarcos()) {
            if (barco.getTipo() == tipo && !barco.estaColocado()) {
                return colocarBarco(flota.getBarcos().indexOf(barco), fila, columna, horizontal);
            }
        }
        return false;
    }

    /**
     * Coloca todos los barcos de forma aleatoria (para la máquina).
     * HU-4: Implementación de la inteligencia artificial.
     */
    public void colocarBarcosAleatoriamente() {
        for (Barco barco : flota.getBarcos()) {
            if (!barco.estaColocado()) {
                colocarBarcoAleatorio(barco);
            }
        }
    }

    /**
     * Coloca un barco específico en posición aleatoria.
     */
    private void colocarBarcoAleatorio(Barco barco) {
        boolean colocado = false;
        int intentos = 0;
        final int MAX_INTENTOS = 100;

        while (!colocado && intentos < MAX_INTENTOS) {
            int fila = (int) (Math.random() * 10);
            int columna = (int) (Math.random() * 10);
            boolean horizontal = Math.random() > 0.5;

            colocado = tableroPropio.colocarBarco(barco, fila, columna, horizontal);
            if (colocado) {
                barco.setPosicion(fila, columna);
                barco.setHorizontal(horizontal);
            }
            intentos++;
        }
    }

    // ========== DISPAROS ==========

    /**
     * Recibe un disparo en las coordenadas especificadas.
     * HU-2: Lógica de disparos (agua, tocado, hundido).
     *
     * @param fila Fila del disparo
     * @param columna Columna del disparo
     * @return Resultado del disparo: "AGUA", "TOCADO", "HUNDIDO", "REPETIDO", "INVALIDO"
     */
    public String recibirDisparo(int fila, int columna) {
        String resultado = tableroPropio.recibirDisparo(fila, columna);

        if (resultado.equals("HUNDIDO")) {
            barcosHundidosPropios++;
        }

        return resultado;
    }

    /**
     * Realiza un disparo al oponente.
     *
     * @param fila Fila del disparo
     * @param columna Columna del disparo
     * @param oponente Jugador oponente
     * @return Resultado del disparo
     */
    public String realizarDisparo(int fila, int columna, Jugador oponente) {
        // Verificar si ya se disparó aquí
        if (tableroDisparos.estaDisparada(fila, columna)) {
            return "REPETIDO";
        }

        String resultado = oponente.recibirDisparo(fila, columna);

        // Registrar en el tablero de disparos
        tableroDisparos.registrarDisparo(fila, columna, resultado);

        if (resultado.equals("HUNDIDO")) {
            barcosHundidosEnemigos++;
        }

        return resultado;
    }

    /**
     * Realiza un disparo aleatorio (para la máquina).
     * HU-4: Inteligencia artificial de la máquina.
     * @param oponente Jugador oponente
     * @return Array con [fila, columna, resultado]
     */
    /**
     * Realiza un disparo óptimo (para la máquina).
     *
     * Si está en modo 'Caza' (hay objetivos), ataca un objetivo adyacente.
     * Si está en modo 'Búsqueda' (no hay objetivos), dispara aleatoriamente.
     *
     * @param oponente Jugador oponente
     * @return Array con [fila, columna, resultado]
     */
    public int[] realizarDisparoOptimo(Jugador oponente) {
        int fila, columna;
        String resultado;

        // 1. ESTRATEGIA: MODO CAZA
        if (!posiblesObjetivos.isEmpty()) {
            // Toma y remueve el primer objetivo adyacente
            int[] objetivo = posiblesObjetivos.remove(0);
            fila = objetivo[0];
            columna = objetivo[1];
            System.out.println("🤖🎯 Modo Caza: Disparando en (" + fila + "," + columna + ")");

        } else {
            // 2. ESTRATEGIA: MODO BÚSQUEDA (Aleatorio/Random)
            do {
                fila = (int) (Math.random() * 10);
                columna = (int) (Math.random() * 10);
                // Si el disparo es repetido, el bucle lo manejará
            } while (tableroDisparos.estaDisparada(fila, columna)); // Asegura que no sea REPETIDO antes de disparar
            System.out.println("🤖🔍 Modo Búsqueda: Disparando en (" + fila + "," + columna + ")");
        }

        // 3. EJECUTAR DISPARO
        resultado = realizarDisparo(fila, columna, oponente);

        // 4. FEEDBACK DE LA IA (Aprender del disparo)
        if (resultado.equals("TOCADO")) {
            impactosRecientes.add(new int[]{fila, columna});
            // Genera y agrega los 4 vecinos a la cola de objetivos
            agregarAdyacentes(fila, columna);

        } else if (resultado.equals("HUNDIDO")) {
            // Cuando se hunde, limpia el estado para volver a BÚSQUEDA
            impactosRecientes.clear();
            posiblesObjetivos.clear();
            System.out.println("🤖💥 Barco Hundido. Volviendo a modo Búsqueda.");

        } else if (resultado.equals("AGUA") && !impactosRecientes.isEmpty()) {
            // Si estaba en modo Caza (es decir, impactosRecientes no está vacío) y falla (AGUA),
            // No hacemos nada más que eliminarlo de posiblesObjetivos (que ya se hizo arriba)
            // y seguir con el siguiente objetivo en la lista.
        }

        // Mapea el resultado a tu formato [0, 1, 2] para el controlador
        return new int[]{fila, columna, resultado.equals("AGUA") ? 0 :
                resultado.equals("TOCADO") ? 1 : 2};
    }

    /**
     * Genera y agrega las coordenadas adyacentes válidas (no disparadas)
     * al listado de posibles objetivos de caza.
     */
    private void agregarAdyacentes(int r, int c) {
        int[][] direcciones = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}}; // Este, Oeste, Sur, Norte

        for (int[] dir : direcciones) {
            int nuevaFila = r + dir[0];
            int nuevaCol = c + dir[1];

            // 1. Verificar límites del tablero
            if (nuevaFila >= 0 && nuevaFila < 10 && nuevaCol >= 0 && nuevaCol < 10) {

                // 2. Verificar si ya se ha disparado en esa casilla
                if (!tableroDisparos.estaDisparada(nuevaFila, nuevaCol)) {

                    // 3. Verificar si no está ya en la cola de objetivos pendientes
                    boolean existe = false;
                    for(int[] obj : posiblesObjetivos) {
                        if(obj[0] == nuevaFila && obj[1] == nuevaCol) {
                            existe = true;
                            break;
                        }
                    }
                    if(!existe) {
                        posiblesObjetivos.add(new int[]{nuevaFila, nuevaCol});
                    }
                }
            }
        }
    }

    // ========== VERIFICACIONES ==========

    /**
     * Verifica si el jugador ha perdido (toda su flota hundida).
     *
     * @return true si todos los barcos están hundidos
     */
    public boolean haPerdido() {
        return flota.estaCompletamenteHundido();
    }

    /**
     * Verifica si el jugador ha ganado (hundió toda la flota enemiga).
     * Nota: Esto se verifica externamente comparando barcosHundidosEnemigos.
     *
     * @return true si hundió 10 barcos enemigos
     */
    public boolean haGanado() {
        return barcosHundidosEnemigos >= 10;
    }

    /**
     * Verifica si todos los barcos están colocados.
     *
     * @return true si todos los barcos están en el tablero
     */

    public boolean todosBarcosColocados() {
        for (Barco barco : flota.getBarcos()) {
            if (!barco.estaColocado()) {
                return false;
            }
        }
        return true;
    }

    // ========== GETTERS ==========

    public String getNickname() {
        return nickname;
    }

    public Tablero getTableroPropio() {
        return tableroPropio;
    }

    public Tablero getTableroDisparos() {
        return tableroDisparos;
    }

    // Método de compatibilidad (para código existente)
    public Tablero getTablero() {
        return tableroPropio;
    }

    public FlotaComposite getFlota() {
        return flota;
    }

    public List<Barco> getBarcos() {
        return flota.getBarcos();
    }

    public int getBarcosHundidosPropios() {
        return barcosHundidosPropios;
    }

    public int getBarcosHundidosEnemigos() {
        return barcosHundidosEnemigos;
    }

    public int getBarcosRestantes() {
        return 10 - barcosHundidosPropios;
    }

    // ========== MÉTODOS DE VISUALIZACIÓN ==========

    /**
     * Muestra el estado completo del jugador.
     */
    public void mostrarEstado() {
        System.out.println("\n=== JUGADOR: " + nickname + " ===");
        System.out.println("Barcos hundidos propios: " + barcosHundidosPropios);
        System.out.println("Barcos hundidos enemigos: " + barcosHundidosEnemigos);
        System.out.println("Barcos restantes: " + getBarcosRestantes());
        System.out.println("Ha perdido: " + haPerdido());

        flota.mostrarInformacion();
    }

    /**
     * Para HU-3: Muestra el tablero propio con barcos visibles.
     *
     * @return Representación del tablero con barcos
     */
    public String mostrarTableroConBarcos() {
        return tableroPropio.toStringConBarcos();
    }

    @Override
    public String toString() {
        return String.format("Jugador{nombre='%s', barcosRestantes=%d, haPerdido=%s}",
                nickname, getBarcosRestantes(), haPerdido());
    }

    public List<int[]> getImpactosRecientes() {
        return impactosRecientes;
    }

    public void setImpactosRecientes(List<int[]> impactosRecientes) {
        this.impactosRecientes = impactosRecientes;
    }

    public List<int[]> getPosiblesObjetivos() {
        return posiblesObjetivos;
    }

    public void setPosiblesObjetivos(List<int[]> posiblesObjetivos) {
        this.posiblesObjetivos = posiblesObjetivos;
    }
}