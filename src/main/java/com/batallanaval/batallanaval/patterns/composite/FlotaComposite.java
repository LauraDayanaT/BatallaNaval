package com.batallanaval.batallanaval.patterns.composite;

import com.batallanaval.batallanaval.model.Barco;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa una flota compuesta por múltiples barcos.
 * Implementa el patrón Composite.
 */
public class FlotaComposite implements ComponenteFlota {
    private String nombre;
    private List<Barco> barcos;

    public FlotaComposite(String nombre) {
        this.nombre = nombre;
        this.barcos = new ArrayList<>();
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public int gettamanhoTotal() {
        int total = 0;
        for (Barco barco : barcos) {
            total += barco.gettamanho();
        }
        return total;
    }

    @Override
    public int getCantidadBarcos() {
        return barcos.size();
    }

    @Override
    public boolean estaCompletamenteHundido() {
        for (Barco barco : barcos) {
            if (!barco.estaHundido()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void agregar(Barco barco) {
        if (barco != null) {
            barcos.add(barco);
        }
    }

    @Override
    public void mostrarInformacion() {
        System.out.println("=== FLOTA: " + nombre + " ===");
        System.out.println("Barcos: " + getCantidadBarcos());
        System.out.println("tamanho total: " + gettamanhoTotal() + " casillas");
        System.out.println("Hundida completamente: " + estaCompletamenteHundido());

        for (Barco barco : barcos) {
            System.out.println("  - " + barco.getNombre() +
                    " (tamanho: " + barco.gettamanho() +
                    ", Hundido: " + barco.estaHundido() + ")");
        }
    }

    // Métodos adicionales útiles
    public List<Barco> getBarcos() {
        return new ArrayList<>(barcos);
    }

    public boolean contieneBarco(Barco barco) {
        return barcos.contains(barco);
    }

    public void removerBarco(Barco barco) {
        barcos.remove(barco);
    }

    public int barcosHundidos() {
        int count = 0;
        for (Barco barco : barcos) {
            if (barco.estaHundido()) {
                count++;
            }
        }
        return count;
    }

    public int barcosIntactos() {
        int count = 0;
        for (Barco barco : barcos) {
            if (!barco.estaHundido()) {
                count++;
            }
        }
        return count;
    }
}