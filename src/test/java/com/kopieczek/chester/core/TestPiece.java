package com.kopieczek.chester.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestPiece {
    @Test
    public void test_get_piece_type() {
        for (PieceType type : PieceType.values()) {
            for (Color color : Color.values()) {
                Piece piece = ImmutablePiece.builder().type(type).color(color).build();
                assertEquals(type, piece.type());
            }
        }
    }

    @Test
    public void test_get_piece_color() {
        for (PieceType type : PieceType.values()) {
            for (Color color : Color.values()) {
                Piece piece = ImmutablePiece.builder().type(type).color(color).build();
                assertEquals(color, piece.color());
            }
        }
    }
}
