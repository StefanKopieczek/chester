package com.kopieczek.chester.core;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static com.kopieczek.chester.core.CoordConverter.convert;
import static com.kopieczek.chester.core.Piece.BLACK_KNIGHT;
import static com.kopieczek.chester.core.Piece.WHITE_PAWN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TestBoard {
    @Test
    public void test_board_initially_empty() {
        Board board = new Board();
        IntStream.range(0, 64).forEach(cell -> {
            assertFalse(board.get(cell).isPresent());
        });
    }

    @Test
    public void test_put_pawn_a1_and_get() {
        Board board = new Board();
        put(board, "a1", WHITE_PAWN);
        assertBoard(board, expected -> {
            expected.put("a1", WHITE_PAWN);
        });
    }

    @Test
    public void test_put_pawn_c7_and_get() {
        Board board = new Board();
        put(board, "c7", WHITE_PAWN);
        assertBoard(board, expected -> {
            expected.put("c7", WHITE_PAWN);
        });
    }

    @Test
    public void test_put_knight_h4_and_get() {
        Board board = new Board();
        put(board, "h4", BLACK_KNIGHT);
        assertBoard(board, expected -> {
            expected.put("h4", BLACK_KNIGHT);
        });
    }

    @Test
    public void test_put_two_pieces_and_get() {
        Board board = new Board();
        put(board, "h3", WHITE_PAWN);
        put(board, "a1", BLACK_KNIGHT);
        assertBoard(board, expected -> {
            expected.put("a1", BLACK_KNIGHT);
            expected.put("h3", WHITE_PAWN);
        });
    }

    @Test
    public void test_new_piece_replaces_old() {
        Board board = new Board();
        put(board, "c4", WHITE_PAWN);
        put(board, "c4", BLACK_KNIGHT);
        assertBoard(board, expected -> {
            expected.put("c4", BLACK_KNIGHT);
        });
    }

    @Test
    public void test_put_null_removes_piece() {
        Board board = new Board();
        put(board, "g8", BLACK_KNIGHT);
        put(board, "g8", null);
        assertBoard(board, expected -> {});
    }

    @Test
    public void test_move_pawn_from_a1_to_h8() {
        Board board = new Board();
        put(board, "a1", WHITE_PAWN);
        move(board, "a1", "h8");
        assertFalse("a1 should now be empty", get(board, "a1").isPresent());
        assertEquals("h8 should now contain the pawn", Optional.of(WHITE_PAWN), get(board, "h8"));
    }

    @Test
    public void test_move_knight_from_d2_to_c7() {
        Board board = new Board();
        put(board, "d2", BLACK_KNIGHT);
        move(board, "d2", "c7");
        assertFalse("d2 should now be empty", get(board, "d2").isPresent());
        assertEquals("c7 should now contain the pawn", Optional.of(BLACK_KNIGHT), get(board, "c7"));
    }

    private static void assertBoard(Board board, Consumer<Map<String, Piece>> setup) {
        Map<String, Piece> expected = new HashMap<>();
        setup.accept(expected);

        IntStream.range(0, 64).forEach(cell -> {
            Optional<Piece> actualPiece = board.get(cell);
            Optional<Piece> expectedPiece = Optional.ofNullable(expected.getOrDefault(convert(cell), null));
            assertEquals("At cell " + convert(cell), expectedPiece, actualPiece);
        });
    }

    private static Optional<Piece> get(Board b, String cell) {
        return b.get(convert(cell));
    }

    private static void put(Board b, String cell, Piece piece) {
        b.put(convert(cell), piece);
    }

    private static void move(Board b, String from, String to) {
        b.move(convert(from), convert(to));
    }
}
