package com.batallanaval.batallanaval.utils;

import com.batallanaval.batallanaval.exceptions.JuegoGuardadoException;
import com.batallanaval.model.Jugador;
import com.batallanaval.model.Tablero;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

/**
 * Clase para manejar el guardado y carga de juegos.
 * Implementa HU-5: Guardado automático del juego.
 * Implementa HU-6: Cargar juego guardado.
 *
 *
 * @version 1.0
 */
public class ArchivoManager {

    // Constantes para nombres de archivos
    private static final String DIRECTORIO_JUEGOS = "juegos_guardados";
    private static final String ARCHIVO_ULTIMO_JUEGO = "ultimo_juego.ser";
    private static final String ARCHIVO_ESTADISTICAS = "estadisticas.txt";
    private static final String ARCHIVO_CONFIG = "config.properties";

    /**
     * Guarda el estado completo del juego (serializable).
     *
     * @param jugadorHumano Jugador humano
     * @param jugadorMaquina Jugador máquina
     * @param juegoIniciado Estado del juego
     * @param turnoJugador De quién es el turno
     * @throws JuegoGuardadoException si hay error al guardar
     */
    public void guardarJuegoCompleto(Jugador jugadorHumano, Jugador jugadorMaquina,
                                     boolean juegoIniciado, boolean turnoJugador)
            throws JuegoGuardadoException {

        try {
            // Crear directorio si no existe
            crearDirectorioJuegos();

            // Crear objeto de estado del juego
            EstadoJuego estado = new EstadoJuego(
                    jugadorHumano,
                    jugadorMaquina,
                    juegoIniciado,
                    turnoJugador,
                    LocalDateTime.now()
            );

            // Guardar con nombre único basado en fecha
            String nombreArchivo = generarNombreArchivo();
            Path rutaArchivo = Paths.get(DIRECTORIO_JUEGOS, nombreArchivo);

            // Serializar objeto
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(rutaArchivo.toFile()))) {
                oos.writeObject(estado);
            }

            // También guardar como último juego
            guardarUltimoJuego(estado);

            // Guardar estadísticas (archivo plano)
            guardarEstadisticas(jugadorHumano);

            System.out.println("✅ Juego guardado: " + rutaArchivo);

        } catch (IOException e) {
            throw new JuegoGuardadoException(
                    "Error al guardar el juego: " + e.getMessage(),
                    ARCHIVO_ULTIMO_JUEGO,
                    "GUARDAR"
            );
        }
    }

    /**
     * Carga el último juego guardado.
     *
     * @return Estado del juego guardado
     * @throws JuegoGuardadoException si hay error al cargar o no existe archivo
     */
    public EstadoJuego cargarUltimoJuego() throws JuegoGuardadoException {
        try {
            Path rutaArchivo = Paths.get(DIRECTORIO_JUEGOS, ARCHIVO_ULTIMO_JUEGO);

            if (!Files.exists(rutaArchivo)) {
                throw new JuegoGuardadoException(
                        "No hay juegos guardados",
                        ARCHIVO_ULTIMO_JUEGO,
                        "CARGAR"
                );
            }

            // Deserializar objeto
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(rutaArchivo.toFile()))) {
                EstadoJuego estado = (EstadoJuego) ois.readObject();

                System.out.println("✅ Juego cargado: " + estado.getFechaGuardado());
                return estado;
            }

        } catch (IOException | ClassNotFoundException e) {
            throw new JuegoGuardadoException(
                    "Error al cargar el juego: " + e.getMessage(),
                    ARCHIVO_ULTIMO_JUEGO,
                    "CARGAR"
            );
        }
    }

    /**
     * Guarda las estadísticas del jugador (archivo plano - HU-5).
     *
     * @param jugador Jugador cuyas estadísticas guardar
     * @throws JuegoGuardadoException si hay error
     */
    public void guardarEstadisticas(Jugador jugador) throws JuegoGuardadoException {
        try {
            crearDirectorioJuegos();
            Path rutaArchivo = Paths.get(DIRECTORIO_JUEGOS, ARCHIVO_ESTADISTICAS);

            // Formato: nickname,fecha,barcos_hundidos,barcos_restantes
            String linea = String.format("%s,%s,%d,%d%n",
                    jugador.getNickname(),
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    jugador.getBarcosHundidosEnemigos(),
                    jugador.getBarcosRestantes()
            );

            // Escribir en archivo (append mode)
            Files.write(rutaArchivo, linea.getBytes(),
                    Files.exists(rutaArchivo) ?
                            java.nio.file.StandardOpenOption.APPEND :
                            java.nio.file.StandardOpenOption.CREATE
            );

            System.out.println("📊 Estadísticas guardadas para: " + jugador.getNickname());

        } catch (IOException e) {
            throw new JuegoGuardadoException(
                    "Error al guardar estadísticas: " + e.getMessage(),
                    ARCHIVO_ESTADISTICAS,
                    "GUARDAR"
            );
        }
    }

    /**
     * Carga las estadísticas desde archivo plano.
     *
     * @return Lista de líneas de estadísticas
     * @throws JuegoGuardadoException si hay error
     */
    public String cargarEstadisticas() throws JuegoGuardadoException {
        try {
            Path rutaArchivo = Paths.get(DIRECTORIO_JUEGOS, ARCHIVO_ESTADISTICAS);

            if (!Files.exists(rutaArchivo)) {
                return "No hay estadísticas guardadas";
            }

            return new String(Files.readAllBytes(rutaArchivo));

        } catch (IOException e) {
            throw new JuegoGuardadoException(
                    "Error al cargar estadísticas: " + e.getMessage(),
                    ARCHIVO_ESTADISTICAS,
                    "CARGAR"
            );
        }
    }

    /**
     * Guarda configuración del juego (archivo properties).
     *
     * @param propiedades Propiedades a guardar
     * @throws JuegoGuardadoException si hay error
     */
    public void guardarConfiguracion(Properties propiedades) throws JuegoGuardadoException {
        try {
            crearDirectorioJuegos();
            Path rutaArchivo = Paths.get(DIRECTORIO_JUEGOS, ARCHIVO_CONFIG);

            try (FileOutputStream fos = new FileOutputStream(rutaArchivo.toFile())) {
                propiedades.store(fos, "Configuración del juego Batalla Naval");
            }

            System.out.println("⚙️ Configuración guardada");

        } catch (IOException e) {
            throw new JuegoGuardadoException(
                    "Error al guardar configuración: " + e.getMessage(),
                    ARCHIVO_CONFIG,
                    "GUARDAR"
            );
        }
    }

    /**
     * Carga configuración del juego.
     *
     * @return Propiedades cargadas
     * @throws JuegoGuardadoException si hay error
     */
    public Properties cargarConfiguracion() throws JuegoGuardadoException {
        try {
            Path rutaArchivo = Paths.get(DIRECTORIO_JUEGOS, ARCHIVO_CONFIG);

            if (!Files.exists(rutaArchivo)) {
                // Devolver propiedades por defecto
                return crearConfiguracionPorDefecto();
            }

            Properties propiedades = new Properties();
            try (FileInputStream fis = new FileInputStream(rutaArchivo.toFile())) {
                propiedades.load(fis);
            }

            return propiedades;

        } catch (IOException e) {
            throw new JuegoGuardadoException(
                    "Error al cargar configuración: " + e.getMessage(),
                    ARCHIVO_CONFIG,
                    "CARGAR"
            );
        }
    }

    /**
     * Verifica si existe un juego guardado.
     *
     * @return true si hay juego guardado, false en caso contrario
     */
    public boolean existeJuegoGuardado() {
        Path rutaArchivo = Paths.get(DIRECTORIO_JUEGOS, ARCHIVO_ULTIMO_JUEGO);
        return Files.exists(rutaArchivo);
    }

    /**
     * Elimina todos los juegos guardados.
     */
    public void eliminarJuegosGuardados() throws JuegoGuardadoException {
        try {
            Path directorio = Paths.get(DIRECTORIO_JUEGOS);

            if (Files.exists(directorio)) {
                // Eliminar todos los archivos .ser
                Files.walk(directorio)
                        .filter(path -> path.toString().endsWith(".ser"))
                        .forEach(path -> {
                            try { Files.delete(path); }
                            catch (IOException e) { /* Ignorar */ }
                        });

                System.out.println("🗑️ Juegos guardados eliminados");
            }

        } catch (IOException e) {
            throw new JuegoGuardadoException(
                    "Error al eliminar juegos: " + e.getMessage(),
                    DIRECTORIO_JUEGOS,
                    "ELIMINAR"
            );
        }
    }

    // ========== MÉTODOS PRIVADOS ==========

    private void crearDirectorioJuegos() throws IOException {
        Path directorio = Paths.get(DIRECTORIO_JUEGOS);
        if (!Files.exists(directorio)) {
            Files.createDirectories(directorio);
            System.out.println("📁 Directorio creado: " + directorio.toAbsolutePath());
        }
    }

    private void guardarUltimoJuego(EstadoJuego estado) throws IOException {
        Path rutaArchivo = Paths.get(DIRECTORIO_JUEGOS, ARCHIVO_ULTIMO_JUEGO);

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(rutaArchivo.toFile()))) {
            oos.writeObject(estado);
        }
    }

    private String generarNombreArchivo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        return "juego_" + LocalDateTime.now().format(formatter) + ".ser";
    }

    private Properties crearConfiguracionPorDefecto() {
        Properties propiedades = new Properties();
        propiedades.setProperty("dificultad", "NORMAL");
        propiedades.setProperty("sonido_activado", "true");
        propiedades.setProperty("musica_activada", "true");
        propiedades.setProperty("volumen", "80");
        propiedades.setProperty("mostrar_ayuda", "true");
        return propiedades;
    }

    // ========== CLASE INTERNA PARA ESTADO DEL JUEGO ==========

    /**
     * Clase interna que representa el estado completo del juego.
     * Serializable para poder guardar/cargar.
     */
    public static class EstadoJuego implements Serializable {
        private static final long serialVersionUID = 1L;

        private final Jugador jugadorHumano;
        private final Jugador jugadorMaquina;
        private final boolean juegoIniciado;
        private final boolean turnoJugador;
        private final LocalDateTime fechaGuardado;

        public EstadoJuego(Jugador jugadorHumano, Jugador jugadorMaquina,
                           boolean juegoIniciado, boolean turnoJugador,
                           LocalDateTime fechaGuardado) {
            this.jugadorHumano = jugadorHumano;
            this.jugadorMaquina = jugadorMaquina;
            this.juegoIniciado = juegoIniciado;
            this.turnoJugador = turnoJugador;
            this.fechaGuardado = fechaGuardado;
        }

        // Getters
        public Jugador getJugadorHumano() { return jugadorHumano; }
        public Jugador getJugadorMaquina() { return jugadorMaquina; }
        public boolean isJuegoIniciado() { return juegoIniciado; }
        public boolean isTurnoJugador() { return turnoJugador; }
        public LocalDateTime getFechaGuardado() { return fechaGuardado; }

        @Override
        public String toString() {
            return String.format(
                    "EstadoJuego{fecha=%s, iniciado=%s, turno=%s}",
                    fechaGuardado.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    juegoIniciado ? "Sí" : "No",
                    turnoJugador ? "Jugador" : "Máquina"
            );
        }
    }
}