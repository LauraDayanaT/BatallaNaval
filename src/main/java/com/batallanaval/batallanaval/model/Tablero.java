package com.batallanaval.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Tablero implements Serializable {
    private Barco[][] tablero; // 10x10
    private ArrayList<Barco> barcos;

    public Tablero() {
        tablero = new Barco[10][10];
        barcos = new ArrayList<>();
    }

    public boolean colocarBarco(Barco b, int fila, int col, boolean horizontal) {
        if(horizontal) {
            if(col + b.getTamaño() > 10) return false;
            for(int i=0;i<b.getTamaño();i++)
                if(tablero[fila][col+i] != null) return false;
            for(int i=0;i<b.getTamaño();i++)
                tablero[fila][col+i] = b;
        } else {
            if(fila + b.getTamaño() > 10) return false;
            for(int i=0;i<b.getTamaño();i++)
                if(tablero[fila+i][col] != null) return false;
            for(int i=0;i<b.getTamaño();i++)
                tablero[fila+i][col] = b;
        }
        barcos.add(b);
        return true;
    }

    public Barco[][] getTablero() { return tablero; }
}
