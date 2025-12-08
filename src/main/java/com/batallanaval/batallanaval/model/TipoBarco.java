package com.batallanaval.model;

public enum TipoBarco {
    PORTAVIONES(4, "Portaaviones", "🛳️"),
    SUBMARINO(3, "Submarino", "🚤"),
    DESTRUCTOR(2, "Destructor", "⚓"),
    FRAGATA(1, "Fragata", "⛵");

    private final int tamaño;
    private final String nombre;
    private final String icono;

    TipoBarco(int tamaño, String nombre, String icono) {
        this.tamaño = tamaño;
        this.nombre = nombre;
        this.icono = icono;
    }

    public int getTamaño() {
        return tamaño;
    }

    public String getNombre() {
        return nombre;
    }

    public String getIcono() {
        return icono;
    }

    // Método estático para obtener por nombre
    public static TipoBarco fromNombre(String nombre) {
        for (TipoBarco tipo : values()) {
            if (tipo.getNombre().equalsIgnoreCase(nombre)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de barco no válido: " + nombre);
    }
}