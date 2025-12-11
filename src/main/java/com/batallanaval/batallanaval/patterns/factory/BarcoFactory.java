package com.batallanaval.batallanaval.patterns.factory;

import com.batallanaval.batallanaval.model.Barco;
import com.batallanaval.batallanaval.model.TipoBarco;

/**
 * Patrón de diseño CREACIONAL: Factory Method
 * Responsabilidad: Crear instancias de barcos de manera centralizada.
 *
 *
 * @version 1.0
 */
public class BarcoFactory {

    /**
     * Crea un barco del tipo especificado.
     *
     * @param tipo Tipo de barco a crear
     * @return Nueva instancia de Barco
     */
    public static Barco crearBarco(TipoBarco tipo) {
        switch (tipo) {
            case PORTAVIONES:
                return new Barco(TipoBarco.PORTAVIONES);
            case SUBMARINO:
                return new Barco(TipoBarco.SUBMARINO);
            case DESTRUCTOR:
                return new Barco(TipoBarco.DESTRUCTOR);
            case FRAGATA:
                return new Barco(TipoBarco.FRAGATA);
            default:
                throw new IllegalArgumentException("Tipo de barco desconocido: " + tipo);
        }
    }

    /**
     * Crea un barco basado en su tamaño.
     *
     * @param tamaño Tamaño del barco (1-4)
     * @return Nueva instancia de Barco
     */
    public static Barco crearBarcoPorTamaño(int tamaño) {
        switch (tamaño) {
            case 4:
                return crearBarco(TipoBarco.PORTAVIONES);
            case 3:
                return crearBarco(TipoBarco.SUBMARINO);
            case 2:
                return crearBarco(TipoBarco.DESTRUCTOR);
            case 1:
                return crearBarco(TipoBarco.FRAGATA);
            default:
                throw new IllegalArgumentException("Tamaño de barco inválido: " + tamaño);
        }
    }

    /**
     * Crea todos los barcos necesarios para una flota completa.
     *
     * @return Array con 10 barcos: 1 portaaviones, 2 submarinos, 3 destructores, 4 fragatas
     */
    public static Barco[] crearFlotaCompleta() {
        Barco[] flota = new Barco[10];
        int index = 0;

        // 1 Portaaviones
        flota[index++] = crearBarco(TipoBarco.PORTAVIONES);

        // 2 Submarinos
        flota[index++] = crearBarco(TipoBarco.SUBMARINO);
        flota[index++] = crearBarco(TipoBarco.SUBMARINO);

        // 3 Destructores
        flota[index++] = crearBarco(TipoBarco.DESTRUCTOR);
        flota[index++] = crearBarco(TipoBarco.DESTRUCTOR);
        flota[index++] = crearBarco(TipoBarco.DESTRUCTOR);

        // 4 Fragatas
        flota[index++] = crearBarco(TipoBarco.FRAGATA);
        flota[index++] = crearBarco(TipoBarco.FRAGATA);
        flota[index++] = crearBarco(TipoBarco.FRAGATA);
        flota[index++] = crearBarco(TipoBarco.FRAGATA);

        return flota;
    }

    /**
     * Crea una flota para la máquina (barcos colocados aleatoriamente más tarde).
     *
     * @return Array con 10 barcos
     */
    public static Barco[] crearFlotaMaquina() {
        return crearFlotaCompleta();
    }
}