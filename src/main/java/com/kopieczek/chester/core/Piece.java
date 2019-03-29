package com.kopieczek.chester.core;

import org.immutables.value.Value;

@Value.Immutable
public interface Piece {
    PieceType type();
    Color color();
}
