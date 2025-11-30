package com.batallanaval.model;

import java.io.Serializable;

public class Barco implements Serializable {
    private String tipo; // Portaaviones, Submarino...
    private int tamaño;  // 1 a 4
    private boolean[] casillas; // true = intacta, false = tocada/hundido

    public Barco(String tipo, int tamaño) {
        this.tipo = tipo;
        this.tamaño = tamaño;
        this.casillas = new boolean[tamaño];
        for(int i=0; i<tamaño; i++) casillas[i] = true;
    }

    public boolean estaHundido() {
        for(boolean c : casillas) if(c) return false;
        return true;
    }

    public boolean disparar(int posicion) {
        if(posicion < 0 || posicion >= tamaño) return false;
        if(casillas[posicion]) {
            casillas[posicion] = false;
            return true;
        }
        return false;
    }

    public String getTipo() { return tipo; }
    public int getTamaño() { return tamaño; }
}
