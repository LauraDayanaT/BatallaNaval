package com.batallanaval.batallanaval.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Clase que representa un barco en el juego Batalla Naval.
 * Implementa el concepto de TDA (Tipo de Dato Abstracto) con encapsulamiento completo.
 *
 * @author [Tu Nombre]
 * @version 1.0
 */
public class Barco implements Serializable {
    private static final long serialVersionUID = 1L;

    private final TipoBarco tipo;          // Tipo de barco (INMUTABLE)
    private final int tamanho;              // tamanho en casillas (INMUTABLE)
    private final boolean[] casillas;      // Estado de cada casilla
    private boolean horizontal;            // Orientación
    private int filaInicio;                // Fila inicial (-1 si no colocado)
    private int columnaInicio;             // Columna inicial (-1 si no colocado)
    private boolean hundido;               // Estado de hundimiento

    /**
     * Constructor principal que crea un barco de un tipo específico.
     *
     * @param tipo Tipo de barco según el ENUM TipoBarco
     * @throws IllegalArgumentException si tipo es null
     */
    public Barco(TipoBarco tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de barco no puede ser null");
        }

        this.tipo = tipo;
        this.tamanho = tipo.gettamanho();
        this.casillas = new boolean[tamanho];
        Arrays.fill(this.casillas, true); // Todas las casillas intactas al inicio

        this.horizontal = true; // Por defecto horizontal
        this.filaInicio = -1;   // No colocado aún
        this.columnaInicio = -1;
        this.hundido = false;
    }

    /**
     * Constructor alternativo para compatibilidad con código existente.
     *
     * @param nombre Nombre del barco (se convierte a TipoBarco)
     * @param tamanho tamaño del barco
     * @deprecated Usar Barco(TipoBarco tipo) en su lugar
     */
    @Deprecated
    public Barco(String nombre, int tamanho) {
        this(TipoBarco.fromNombre(nombre));
    }

    /**
     * Verifica si el barco ha sido completamente hundido.
     *
     * @return true si todas las casillas han sido tocadas, false en caso contrario
     */
    public boolean estaHundido() {
        if (!hundido) {
            for (boolean casilla : casillas) {
                if (casilla) {
                    return false; // Hay al menos una casilla intacta
                }
            }
            hundido = true; // Actualizar estado
        }
        return hundido;
    }

    /**
     * Recibe un disparo en una posición específica del barco.
     *
     * @param posicion Posición relativa en el barco (0 a tamaño-1)
     * @return true si el disparo tocó una casilla intacta, false si ya estaba tocada
     * @throws IndexOutOfBoundsException si la posición está fuera de rango
     */
    public boolean recibirDisparo(int posicion) {
        if (posicion < 0 || posicion >= tamanho) {
            throw new IndexOutOfBoundsException(
                    "Posición " + posicion + " fuera de rango. tamaño: " + tamanho
            );
        }

        if (casillas[posicion]) {
            casillas[posicion] = false;
            return true; // Tocado
        }
        return false; // Ya estaba tocado
    }

    /**
     * Recibe un disparo en coordenadas absolutas del tablero.
     *
     * @param fila Fila absoluta en el tablero
     * @param columna Columna absoluta en el tablero
     * @return true si el disparo tocó una casilla intacta, false en caso contrario
     * @throws IllegalStateException si el barco no está colocado aún
     */
    public boolean recibirDisparoEnCoordenada(int fila, int columna) {
        if (!estaColocado()) {
            throw new IllegalStateException("El barco no está colocado en el tablero");
        }

        int posicionRelativa = calcularPosicionRelativa(fila, columna);
        if (posicionRelativa >= 0) {
            return recibirDisparo(posicionRelativa);
        }
        return false; // El disparo no está en este barco
    }

    /**
     * Calcula la posición relativa en el barco basado en coordenadas absolutas.
     *
     * @param fila Fila absoluta
     * @param columna Columna absoluta
     * @return Posición relativa o -1 si no corresponde al barco
     */
    private int calcularPosicionRelativa(int fila, int columna) {
        if (horizontal) {
            if (fila == filaInicio && columna >= columnaInicio &&
                    columna < columnaInicio + tamanho) {
                return columna - columnaInicio;
            }
        } else {
            if (columna == columnaInicio && fila >= filaInicio &&
                    fila < filaInicio + tamanho) {
                return fila - filaInicio;
            }
        }
        return -1; // No es este barco
    }

    // === GETTERS (INMUTABLES) ===

    /**
     * @return Tipo de barco
     */
    public TipoBarco getTipo() {
        return tipo;
    }

    /**
     * @return tamaño del barco en casillas
     */
    public int gettamanho() {
        return tamanho;
    }

    /**
     * @return Nombre del barco
     */
    public String getNombre() {
        return tipo.getNombre();
    }

    /**
     * @return Copia defensiva del array de casillas
     */
    public boolean[] getEstadoCasillas() {
        return casillas.clone(); // Copia defensiva
    }

    /**
     * @return true si el barco está colocado en el tablero
     */
    public boolean estaColocado() {
        return filaInicio >= 0 && columnaInicio >= 0;
    }

    /**
     * @return true si el barco está en orientación horizontal
     */
    public boolean isHorizontal() {
        return horizontal;
    }

    /**
     * @return Fila inicial de colocación
     */
    public int getFilaInicio() {
        return filaInicio;
    }

    /**
     * @return Columna inicial de colocación
     */
    public int getColumnaInicio() {
        return columnaInicio;
    }

    // === SETTERS CON VALIDACIÓN ===

    /**
     * Establece la orientación del barco.
     *
     * @param horizontal true para horizontal, false para vertical
     */
    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }

    /**
     * Establece la posición inicial del barco.
     *
     * @param filaInicio Fila inicial
     * @param columnaInicio Columna inicial
     * @throws IllegalArgumentException si las coordenadas son inválidas
     */
    public void setPosicion(int filaInicio, int columnaInicio) {
        if (filaInicio < 0 || columnaInicio < 0) {
            throw new IllegalArgumentException(
                    "Las coordenadas no pueden ser negativas"
            );
        }
        this.filaInicio = filaInicio;
        this.columnaInicio = columnaInicio;
    }

    /**
     * Obtiene la representación visual de una casilla específica.
     *
     * @param posicion Posición en el barco
     * @return "I" para intacta, "T" para tocada, "H" para hundida
     */
    public String getRepresentacionCasilla(int posicion) {
        if (posicion < 0 || posicion >= tamanho) {
            return " ";
        }
        if (estaHundido()) {
            return "H";
        }
        return casillas[posicion] ? "I" : "T";
    }

    @Override
    public String toString() {
        return String.format("%s (tamaño: %d) - %s %s",
                tipo.getNombre(),
                tamanho,
                estaColocado() ? "Colocado" : "No colocado",
                estaHundido() ? "HUNDIDO" : "Activo"
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Barco other = (Barco) obj;
        return this.tipo == other.tipo &&
                this.filaInicio == other.filaInicio &&
                this.columnaInicio == other.columnaInicio;
    }

    @Override
    public int hashCode() {
        int result = tipo.hashCode();
        result = 31 * result + filaInicio;
        result = 31 * result + columnaInicio;
        return result;
    }
}