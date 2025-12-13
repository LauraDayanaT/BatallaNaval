package com.batallanaval.batallanaval.patterns.composite;

import com.batallanaval.batallanaval.model.Barco;

/**
 * Patrón de diseño ESTRUCTURAL: Composite
 * Interfaz común para barcos individuales y grupos de barcos (flotas).
 *
 *
 * @version 1.0
 */
public interface ComponenteFlota {

    /**
     * Obtiene el nombre del componente.
     */
    String getNombre();

    /**
     * Obtiene el tamanho total (casillas ocupadas).
     */
    int gettamanhoTotal();

    /**
     * Obtiene la cantidad de barcos en este componente.
     */
    int getCantidadBarcos();

    /**
     * Verifica si el componente está completamente hundido.
     */
    boolean estaCompletamenteHundido();

    /**
     * Agrega un barco al componente (solo para composites).
     */
    void agregar(Barco barco);

    /**
     * Muestra información del componente.
     */
    void mostrarInformacion();
}