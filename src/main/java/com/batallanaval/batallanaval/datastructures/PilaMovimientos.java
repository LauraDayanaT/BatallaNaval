package com.batallanaval.batallanaval.datastructures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

/**
 * Implementación de una Pila (Stack) para almacenar movimientos del juego.
 * Cumple con el requisito de implementar una estructura de datos explícita.
 * Usa el patrón LIFO (Last-In, First-Out).
 *
 *
 * @version 1.0
 */
public class PilaMovimientos<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<T> elementos;
    private int capacidad;
    private int tope;

    /**
     * Constructor con capacidad por defecto (100 elementos).
     */
    public PilaMovimientos() {
        this(100);
    }

    /**
     * Constructor con capacidad específica.
     *
     * @param capacidad Capacidad máxima de la pila
     * @throws IllegalArgumentException si capacidad es negativa
     */
    public PilaMovimientos(int capacidad) {
        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser positiva");
        }

        this.capacidad = capacidad;
        this.elementos = new ArrayList<>(capacidad);
        this.tope = -1; // Pila vacía
    }

    // ========== OPERACIONES PRINCIPALES DE PILA ==========

    /**
     * Apila (push) un elemento en la cima de la pila.
     *
     * @param elemento Elemento a apilar
     * @return El elemento apilado
     * @throws IllegalStateException si la pila está llena
     */
    public T apilar(T elemento) {
        if (estaLlena()) {
            throw new IllegalStateException("Pila llena. Capacidad: " + capacidad);
        }

        elementos.add(elemento);
        tope++;
        return elemento;
    }

    /**
     * Desapila (pop) el elemento de la cima de la pila.
     *
     * @return Elemento desapilado
     * @throws EmptyStackException si la pila está vacía
     */
    public T desapilar() {
        if (estaVacia()) {
            throw new EmptyStackException();
        }

        T elemento = elementos.remove(tope);
        tope--;
        return elemento;
    }

    /**
     * Observa (peek) el elemento de la cima sin desapilarlo.
     *
     * @return Elemento en la cima
     * @throws EmptyStackException si la pila está vacía
     */
    public T cima() {
        if (estaVacia()) {
            throw new EmptyStackException();
        }

        return elementos.get(tope);
    }

    // ========== OPERACIONES DE CONSULTA ==========

    /**
     * Verifica si la pila está vacía.
     *
     * @return true si está vacía, false en caso contrario
     */
    public boolean estaVacia() {
        return tope == -1;
    }

    /**
     * Verifica si la pila está llena.
     *
     * @return true si está llena, false en caso contrario
     */
    public boolean estaLlena() {
        return tope == capacidad - 1;
    }

    /**
     * Obtiene el tamaño actual de la pila.
     *
     * @return Cantidad de elementos en la pila
     */
    public int tamanio() {
        return tope + 1;
    }

    /**
     * Obtiene la capacidad máxima de la pila.
     *
     * @return Capacidad máxima
     */
    public int getCapacidad() {
        return capacidad;
    }

    // ========== OPERACIONES ADICIONALES ==========

    /**
     * Vacía completamente la pila.
     */
    public void vaciar() {
        elementos.clear();
        tope = -1;
    }

    /**
     * Busca un elemento en la pila.
     *
     * @param elemento Elemento a buscar
     * @return Posición desde la cima (1 = cima) o -1 si no se encuentra
     */
    public int buscar(T elemento) {
        for (int i = tope; i >= 0; i--) {
            if (elementos.get(i).equals(elemento)) {
                return tope - i + 1; // Posición desde la cima
            }
        }
        return -1;
    }

    /**
     * Convierte la pila a una lista (del más antiguo al más reciente).
     *
     * @return Lista con todos los elementos
     */
    public List<T> toList() {
        return new ArrayList<>(elementos);
    }

    /**
     * Convierte la pila a un array.
     *
     * @return Array con todos los elementos
     */
    @SuppressWarnings("unchecked")
    public T[] toArray() {
        return (T[]) elementos.toArray();
    }

    // ========== MÉTODOS ESPECÍFICOS PARA BATALLA NAVAL ==========

    /**
     * Obtiene los últimos N movimientos.
     *
     * @param n Cantidad de movimientos a obtener
     * @return Lista con los últimos n movimientos (del más reciente al más antiguo)
     * @throws IllegalArgumentException si n es negativo
     */
    public List<T> ultimosMovimientos(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n no puede ser negativo");
        }

        n = Math.min(n, tamanio());
        List<T> resultado = new ArrayList<>();

        for (int i = tope; i > tope - n; i--) {
            resultado.add(elementos.get(i));
        }

        return resultado;
    }

    /**
     * Verifica si contiene un elemento específico.
     *
     * @param elemento Elemento a buscar
     * @return true si está en la pila, false en caso contrario
     */
    public boolean contiene(T elemento) {
        return elementos.contains(elemento);
    }

    // ========== MÉTODOS DE DEBUG/LOG ==========

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PilaMovimientos[");
        sb.append("tamaño=").append(tamanio());
        sb.append(", capacidad=").append(capacidad);
        sb.append(", elementos={");

        for (int i = tope; i >= 0; i--) {
            sb.append(elementos.get(i));
            if (i > 0) sb.append(", ");
        }

        sb.append("}]");
        return sb.toString();
    }

    /**
     * Muestra el contenido de la pila de forma legible.
     *
     * @return String formateado con los elementos
     */
    public String mostrarContenido() {
        if (estaVacia()) {
            return "Pila vacía";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== CONTENIDO DE LA PILA ===\n");
        sb.append("Capacidad: ").append(capacidad).append("\n");
        sb.append("Tamaño actual: ").append(tamanio()).append("\n");
        sb.append("Elementos (del más reciente al más antiguo):\n");

        int contador = 1;
        for (int i = tope; i >= 0; i--) {
            sb.append(contador).append(". ").append(elementos.get(i)).append("\n");
            contador++;
        }

        return sb.toString();
    }
}