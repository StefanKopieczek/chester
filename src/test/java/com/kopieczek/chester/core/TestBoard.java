package com.kopieczek.chester.core;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static com.kopieczek.chester.core.CoordConverter.convert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TestBoard {
    private static Piece WHITE_PAWN = ImmutablePiece.builder()
            .type(PieceType.PAWN)
            .color(Color.WHITE)
            .build();

    private static Piece BLACK_KNIGHT = ImmutablePiece.builder()
            .type(PieceType.KNIGHT)
            .color(Color.BLACK)
            .build();

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
        board.put(convert("a1"), WHITE_PAWN);
        assertBoard(board, expected -> {
            expected.put("a1", WHITE_PAWN);
        });
    }

    @Test
    public void test_put_pawn_c7_and_get() {
        Board board = new Board();
        board.put(convert("c7"), WHITE_PAWN);
        assertBoard(board, expected -> {
            expected.put("c7", WHITE_PAWN);
        });
    }

    @Test
    public void test_put_knight_h4_and_get() {
        Board board = new Board();
        board.put(convert("h4"), BLACK_KNIGHT);
        assertBoard(board, expected -> {
            expected.put("h4", BLACK_KNIGHT);
        });
    }

    @Test
    public void test_put_two_pieces_and_get() {
        Board board = new Board();
        board.put(convert("h3"), WHITE_PAWN);
        board.put(convert("a1"), BLACK_KNIGHT);
        assertBoard(board, expected -> {
            expected.put("a1", BLACK_KNIGHT);
            expected.put("h3", WHITE_PAWN);
        });
    }

    @Test
    public void test_new_piece_replaces_old() {
        Board board = new Board();
        board.put(convert("c4"), WHITE_PAWN);
        board.put(convert("c4"), BLACK_KNIGHT);
        assertBoard(board, expected -> {
            expected.put("c4", BLACK_KNIGHT);
        });
    }

    @Test
    public void test_put_null_removes_piece() {
        Board board = new Board();
        board.put(convert("g8"), BLACK_KNIGHT);
        board.put(convert("g8"), null);
        assertBoard(board, expected -> {});
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
}
