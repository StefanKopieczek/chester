package com.kopieczek.chester.core;

import org.junit.Test;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static com.kopieczek.chester.core.CoordConverter.convert;
import static com.kopieczek.chester.core.Piece.*;
import static org.junit.Assert.*;

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

    @Test
    public void test_white_pawn_moves_in_center_board() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("c4", WHITE_PAWN);
        });
        assertMoves(board, "c4", "c5");
    }

    @Test
    public void test_white_pawn_moves_on_rank_7() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("b7", WHITE_PAWN);
        });
        assertMoves(board, "b7", "b8");
    }

    @Test
    public void test_white_pawn_moves_on_rank_8() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("d8", WHITE_PAWN);
        });
        assertMoves(board, "d8");
    }

    @Test
    public void test_white_pawn_moves_on_rank_1() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("e1", WHITE_PAWN);
        });
        assertMoves(board, "e1", "e2");
    }

    @Test
    public void test_white_pawn_moves_on_rank_2() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("a2", WHITE_PAWN);
        });
        assertMoves(board, "a2", "a3", "a4");
    }

    @Test
    public void test_white_pawn_cannot_move_onto_white_piece() {
        Board board = setupBoard(b -> {
            addKings(b, "a2", "h8");
            b.put("a1", WHITE_PAWN);
        });
        assertMoves(board, "a1");
    }

    @Test
    public void test_white_pawn_cannot_move_onto_white_piece_when_moving_two() {
        Board board = setupBoard(b -> {
            addKings(b, "a4", "h8");
            b.put("a2", WHITE_PAWN);
        });
        assertMoves(board, "a2", "a3");
    }

    @Test
    public void test_white_pawn_cannot_jump_over_white_piece() {
        Board board = setupBoard(b -> {
            addKings(b, "a3", "h8");
            b.put("a2", WHITE_PAWN);
        });
        assertMoves(board, "a2");
    }

    @Test
    public void test_white_pawn_cannot_move_onto_black_piece() {
        Board board = setupBoard(b -> {
            addKings(b, "h8", "a2");
            b.put("a1", WHITE_PAWN);
        });
        assertMoves(board, "a1");
    }

    @Test
    public void test_white_pawn_cannot_move_onto_black_piece_when_moving_two() {
        Board board = setupBoard(b -> {
            addKings(b, "h8", "a4");
            b.put("a2", WHITE_PAWN);
        });
        assertMoves(board, "a2", "a3");
    }

    @Test
    public void test_white_pawn_cannot_jump_over_black_piece() {
        Board board = setupBoard(b -> {
            addKings(b, "h8", "a3");
            b.put("a2", WHITE_PAWN);
        });
        assertMoves(board, "a2");
    }

    @Test
    public void test_white_pawn_can_take_black_piece_from_rank_1_on_left() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("c1", WHITE_PAWN);
            b.put("b2", BLACK_PAWN);
        });
        assertMoves(board, "c1", "b2", "c2");
    }

    @Test
    public void test_white_pawn_can_take_black_piece_from_rank_1_on_right() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("c1", WHITE_PAWN);
            b.put("d2", BLACK_PAWN);
        });
        assertMoves(board, "c1", "c2", "d2");
    }

    @Test
    public void test_white_pawn_cannot_take_white_piece_on_left() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("c1", WHITE_PAWN);
            b.put("b2", WHITE_KNIGHT);
        });
        assertMoves(board, "c1", "c2");
    }

    @Test
    public void test_white_pawn_cannot_take_white_piece_on_right() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("c1", WHITE_PAWN);
            b.put("d2", WHITE_KNIGHT);
        });
        assertMoves(board, "c1", "c2");
    }

    @Test
    public void test_white_pawn_can_threaten_two_black_pieces_at_once_from_row_1() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("d1", WHITE_PAWN);
            b.put("c2", BLACK_PAWN);
            b.put("e2", BLACK_PAWN);
        });
        assertMoves(board, "d1", "c2", "d2", "e2");
    }

    @Test
    public void test_all_four_moves_of_white_pawn() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("b2", WHITE_PAWN);
            b.put("a3", BLACK_KNIGHT);
            b.put("c3", BLACK_ROOK);
        });
        assertMoves(board, "b2", "a3", "b3", "b4", "c3");
    }

    @Test
    public void test_white_pawn_cannot_take_wrapping_around_left_edge_of_board() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("a4", WHITE_PAWN);
            b.put("h4", BLACK_PAWN);
        });
        assertMoves(board, "a4", "a5");
    }

    @Test
    public void test_white_pawn_cannot_take_wrapping_around_right_edge_of_board() {
        Board board = setupBoard(b -> {
            addKings(b, "a2", "h8");
            b.put("h4", WHITE_PAWN);
            b.put("a6", BLACK_PAWN);
        });
        assertMoves(board, "h4", "h5");
    }

    @Test
    public void test_black_pawn_moves_in_center_board() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("c4", BLACK_PAWN);
        });
        assertMoves(board, "c4", "c3");
    }

    @Test
    public void test_black_pawn_moves_on_rank_2() {
        Board board = setupBoard(b -> {
            addKings(b, "a8", "h8");
            b.put("b2", BLACK_PAWN);
        });
        assertMoves(board, "b2", "b1");
    }

    @Test
    public void test_black_pawn_moves_on_rank_1() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("d1", BLACK_PAWN);
        });
        assertMoves(board, "d1");
    }

    @Test
    public void test_black_pawn_moves_on_rank_8() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("e8", BLACK_PAWN);
        });
        assertMoves(board, "e8", "e7");
    }

    @Test
    public void test_black_pawn_moves_on_rank_7() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("a7", BLACK_PAWN);
        });
        assertMoves(board, "a7", "a6", "a5");
    }

    @Test
    public void test_black_pawn_cannot_move_onto_black_piece() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h7");
            b.put("h8", BLACK_PAWN);
        });
        assertMoves(board, "h8");
    }

    @Test
    public void test_black_pawn_cannot_move_onto_black_piece_when_moving_two() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h5");
            b.put("h7", BLACK_PAWN);
        });
        assertMoves(board, "h7", "h6");
    }

    @Test
    public void test_black_pawn_cannot_jump_over_black_piece() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h6");
            b.put("h7", BLACK_PAWN);
        });
        assertMoves(board, "h7");
    }

    @Test
    public void test_black_pawn_cannot_move_onto_white_piece() {
        Board board = setupBoard(b -> {
            addKings(b, "h7", "a1");
            b.put("h8", BLACK_PAWN);
        });
        assertMoves(board, "h8");
    }

    @Test
    public void test_black_pawn_cannot_move_onto_white_piece_when_moving_two() {
        Board board = setupBoard(b -> {
            addKings(b, "h5", "a1");
            b.put("h7", BLACK_PAWN);
        });
        assertMoves(board, "h7", "h6");
    }

    @Test
    public void test_black_pawn_cannot_jump_over_white_piece() {
        Board board = setupBoard(b -> {
            addKings(b, "h6", "a1");
            b.put("h7", BLACK_PAWN);
        });
        assertMoves(board, "h7");
    }

    @Test
    public void test_black_pawn_can_take_white_piece_from_rank_1_on_right() {
        Board board = setupBoard(b -> {
            addKings(b, "a8", "h8");
            b.put("c1", WHITE_PAWN);
            b.put("b2", BLACK_PAWN);
        });
        assertMoves(board, "b2", "c1", "b1");
    }

    @Test
    public void test_black_pawn_can_take_white_piece_from_rank_1_on_left() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("c1", WHITE_PAWN);
            b.put("d2", BLACK_PAWN);
        });
        assertMoves(board, "d2", "c1", "d1");
    }

    @Test
    public void test_black_pawn_cannot_take_black_piece_on_right() {
        Board board = setupBoard(b -> {
            addKings(b, "a8", "h8");
            b.put("b2", BLACK_PAWN);
            b.put("c1", BLACK_KNIGHT);
        });
        assertMoves(board, "b2", "b1");
    }

    @Test
    public void test_black_pawn_cannot_take_black_piece_on_left() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("d2", BLACK_PAWN);
            b.put("c1", BLACK_KNIGHT);
        });
        assertMoves(board, "d2", "d1");
    }

    @Test
    public void test_black_pawn_can_threaten_two_white_pieces_at_once_from_row_8() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("d8", BLACK_PAWN);
            b.put("c7", WHITE_PAWN);
            b.put("e7", WHITE_PAWN);
        });
        assertMoves(board, "d8", "c7", "d7", "e7");
    }

    @Test
    public void test_all_four_moves_of_black_pawn() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("b7", BLACK_PAWN);
            b.put("a6", WHITE_KNIGHT);
            b.put("c6", WHITE_ROOK);
        });
        assertMoves(board, "b7", "a6", "b6", "b5", "c6");
    }

    @Test
    public void test_black_pawn_cannot_take_wrapping_around_right_edge_of_board() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("a4", WHITE_PAWN);
            b.put("h4", BLACK_PAWN);
        });
        assertMoves(board, "h4", "h3");
    }

    @Test
    public void test_black_pawn_cannot_take_wrapping_around_left_edge_of_board() {
        Board board = setupBoard(b -> {
            addKings(b, "a2", "h8");
            b.put("h4", WHITE_PAWN);
            b.put("a6", BLACK_PAWN);
        });
        assertMoves(board, "a6", "a5");
    }

    @Test
    public void test_empty_square_has_no_moves() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
        });
        System.out.println(board.getMoves(convert("d1")));
        assertMoves(board, "d1");
    }

    private static Board setupBoard(Consumer<Map<String, Piece>> setup) {
        Map<String, Piece> schema = new HashMap<>();
        setup.accept(schema);
        Board board = new Board();
        schema.entrySet().forEach(entry -> {
            board.put(convert(entry.getKey()), entry.getValue());
        });
        return board;
    }

    private static void addKings(Map<String, Piece> schema, String whiteKing, String blackKing) {
        schema.put(whiteKing, WHITE_KING);
        schema.put(blackKing, BLACK_KING);
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

    private static void assertMoves(Board b, String cell, String... expectedMoves) {
        List<Integer> result = new ArrayList<>(b.getMoves(convert(cell)));
        String[] actualMoves = new String[result.size()];
        for (int idx = 0; idx < actualMoves.length; idx++) {
            actualMoves[idx] = convert(result.get(idx));
        }

        Arrays.sort(expectedMoves);
        Arrays.sort(actualMoves);
        assertArrayEquals(expectedMoves, actualMoves);
    }
}
