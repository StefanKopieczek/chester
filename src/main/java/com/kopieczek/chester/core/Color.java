package com.kopieczek.chester.core;

public enum Color {
    WHITE,
    BLACK;

    public Color inverse() {
        return (this == WHITE) ? BLACK : WHITE;
    }
}
