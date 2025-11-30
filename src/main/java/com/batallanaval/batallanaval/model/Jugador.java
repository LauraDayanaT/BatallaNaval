package com.batallanaval.model;

import java.io.Serializable;

public class Jugador implements Serializable {
    private String nickname;
    private Tablero tablero;

    public Jugador(String nickname) {
        this.nickname = nickname;
        this.tablero = new Tablero();
    }

    public String getNickname() { return nickname; }
    public Tablero getTablero() { return tablero; }
}
