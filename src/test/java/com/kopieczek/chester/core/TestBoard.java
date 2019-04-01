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
    public void test_empty_square_has_no_moves() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
        });
        System.out.println(board.getMoves(convert("d1")));
        assertMoves(board, "d1");
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
    public void test_white_knight_moves_in_center_board() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("d5", WHITE_KNIGHT);
        });
        assertMoves(board, "d5", "b6", "c7", "e7", "f6", "f4", "e3", "c3", "b4");
    }

    @Test
    public void test_white_knight_moves_on_file_b() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("b5", WHITE_KNIGHT);
        });
        assertMoves(board, "b5", "a7", "c7", "d6", "d4", "c3", "a3");
    }

    @Test
    public void test_white_knight_moves_on_file_a() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("a5", WHITE_KNIGHT);
        });
        assertMoves(board, "a5", "b7", "c6", "c4", "b3");
    }

    @Test
    public void test_white_knight_moves_on_file_g() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("g5", WHITE_KNIGHT);
        });
        assertMoves(board, "g5", "e6", "f7", "h7", "h3", "f3", "e4");
    }

    @Test
    public void test_white_knight_moves_on_file_h() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("h5", WHITE_KNIGHT);
        });
        assertMoves(board, "h5", "f6", "g7", "g3", "f4");
    }

    @Test
    public void test_white_knight_moves_on_rank_2() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("d2", WHITE_KNIGHT);
        });
        assertMoves(board, "d2", "b3", "c4", "e4", "f3", "f1", "b1");
    }

    @Test
    public void test_white_knight_moves_on_rank_1() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("d1", WHITE_KNIGHT);
        });
        assertMoves(board, "d1", "b2", "c3", "e3", "f2");
    }

    @Test
    public void test_white_knight_moves_on_rank_7() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("d7", WHITE_KNIGHT);
        });
        assertMoves(board, "d7", "b8", "f8", "f6", "e5", "c5", "b6");
    }

    @Test
    public void test_white_knight_moves_on_rank_8() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("d8", WHITE_KNIGHT);
        });
        assertMoves(board, "d8", "f7", "e6", "c6", "b7");
    }

    @Test
    public void test_white_knight_moves_from_a7() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("a7", WHITE_KNIGHT);
        });
        assertMoves(board, "a7", "c8", "c6", "b5");
    }

    @Test
    public void test_white_knight_moves_from_h1() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("h1", WHITE_KNIGHT);
        });
        assertMoves(board, "h1", "f2", "g3");
    }

    @Test
    public void test_white_knight_cannot_move_onto_white_piece() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("h1", WHITE_KNIGHT);
            b.put("f2", WHITE_ROOK);
        });
        assertMoves(board, "h1", "g3");
    }

    @Test
    public void test_white_knight_can_move_onto_black_piece() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("h1", WHITE_KNIGHT);
            b.put("f2", BLACK_ROOK);
        });
        assertMoves(board, "h1", "f2", "g3");
    }

    @Test
    public void test_black_knight_moves_in_center_board() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("d5", BLACK_KNIGHT);
        });
        assertMoves(board, "d5", "b6", "c7", "e7", "f6", "f4", "e3", "c3", "b4");
    }

    @Test
    public void test_black_knight_moves_on_file_b() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("b5", BLACK_KNIGHT);
        });
        assertMoves(board, "b5", "a7", "c7", "d6", "d4", "c3", "a3");
    }

    @Test
    public void test_black_knight_moves_on_file_a() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("a5", BLACK_KNIGHT);
        });
        assertMoves(board, "a5", "b7", "c6", "c4", "b3");
    }

    @Test
    public void test_black_knight_moves_on_file_g() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("g5", BLACK_KNIGHT);
        });
        assertMoves(board, "g5", "e6", "f7", "h7", "h3", "f3", "e4");
    }

    @Test
    public void test_black_knight_moves_on_file_h() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("h5", BLACK_KNIGHT);
        });
        assertMoves(board, "h5", "f6", "g7", "g3", "f4");
    }

    @Test
    public void test_black_knight_moves_on_rank_2() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("d2", BLACK_KNIGHT);
        });
        assertMoves(board, "d2", "b3", "c4", "e4", "f3", "f1", "b1");
    }

    @Test
    public void test_black_knight_moves_on_rank_1() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("d1", BLACK_KNIGHT);
        });
        assertMoves(board, "d1", "b2", "c3", "e3", "f2");
    }

    @Test
    public void test_black_knight_moves_on_rank_7() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("d7", BLACK_KNIGHT);
        });
        assertMoves(board, "d7", "b8", "f8", "f6", "e5", "c5", "b6");
    }

    @Test
    public void test_black_knight_moves_on_rank_8() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("d8", BLACK_KNIGHT);
        });
        assertMoves(board, "d8", "f7", "e6", "c6", "b7");
    }

    @Test
    public void test_black_knight_moves_from_a7() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("a7", BLACK_KNIGHT);
        });
        assertMoves(board, "a7", "c8", "c6", "b5");
    }

    @Test
    public void test_black_knight_moves_from_h1() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("h1", BLACK_KNIGHT);
        });
        assertMoves(board, "h1", "f2", "g3");
    }

    @Test
    public void test_black_knight_cannot_move_onto_black_piece() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("h1", BLACK_KNIGHT);
            b.put("f2", BLACK_ROOK);
        });
        assertMoves(board, "h1", "g3");
    }

    @Test
    public void test_black_knight_can_move_onto_white_piece() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("h1", BLACK_KNIGHT);
            b.put("f2", WHITE_ROOK);
        });
        assertMoves(board, "h1", "f2", "g3");
    }

    @Test
    public void test_white_bishop_moves_on_center_black_square() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("c5", WHITE_BISHOP);
        });
        assertMoves(board, "c5", "b6", "a7", "d6", "e7", "f8", "d4", "e3", "f2", "g1", "b4", "a3");
    }

    @Test
    public void test_white_bishop_moves_on_edge_black_square() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("a5", WHITE_BISHOP);
        });
        assertMoves(board, "a5", "b6", "c7", "d8", "b4", "c3", "d2", "e1");
    }

    @Test
    public void test_white_bishop_moves_on_center_white_square() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("d5", WHITE_BISHOP);
        });
        assertMoves(board, "d5", "c6", "b7", "a8", "e6", "f7", "g8", "e4", "f3", "g2", "h1", "c4", "b3", "a2");
    }

    @Test
    public void test_white_bishop_moves_on_edge_white_square() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("h5", WHITE_BISHOP);
        });
        assertMoves(board, "h5", "g4", "f3", "e2", "d1", "g6", "f7", "e8");
    }

    @Test
    public void test_black_bishop_moves_on_center_black_square() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("c5", BLACK_BISHOP);
        });
        assertMoves(board, "c5", "b6", "a7", "d6", "e7", "f8", "d4", "e3", "f2", "g1", "b4", "a3");
    }

    @Test
    public void test_black_bishop_moves_on_edge_black_square() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("a5", BLACK_BISHOP);
        });
        assertMoves(board, "a5", "b6", "c7", "d8", "b4", "c3", "d2", "e1");
    }

    @Test
    public void test_black_bishop_moves_on_center_white_square() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("d5", BLACK_BISHOP);
        });
        assertMoves(board, "d5", "c6", "b7", "a8", "e6", "f7", "g8", "e4", "f3", "g2", "h1", "c4", "b3", "a2");
    }

    @Test
    public void test_black_bishop_moves_on_edge_white_square() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("h5", BLACK_BISHOP);
        });
        assertMoves(board, "h5", "g4", "f3", "e2", "d1", "g6", "f7", "e8");
    }

    @Test
    public void test_white_bishop_cannot_take_white_piece() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("h1", WHITE_BISHOP);
            b.put("g2", WHITE_KNIGHT);
        });
        assertFalse(hasMove(board, "h1", "g2"));
    }

    @Test
    public void test_white_bishop_can_take_black_piece() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("h1", WHITE_BISHOP);
            b.put("g2", BLACK_KNIGHT);
        });
        assertTrue(hasMove(board, "h1", "g2"));
    }

    @Test
    public void test_black_bishop_cannot_take_black_piece() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("a8", BLACK_BISHOP);
            b.put("b7", BLACK_KNIGHT);
        });
        assertFalse(hasMove(board, "a8", "b7"));
    }

    @Test
    public void test_black_bishop_can_take_white_piece() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("a8", BLACK_BISHOP);
            b.put("b7", WHITE_KNIGHT);
        });
        assertTrue(hasMove(board, "a8", "b7"));
    }

    @Test
    public void test_white_bishop_is_blocked_by_white_piece() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("a8", WHITE_BISHOP);
            b.put("c6", WHITE_ROOK);
        });
        assertMoves(board, "a8", "b7");
    }

    @Test
    public void test_white_bishop_is_blocked_by_black_piece() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("a8", WHITE_BISHOP);
            b.put("c6", BLACK_ROOK);
        });
        assertMoves(board, "a8", "b7", "c6");
    }

    @Test
    public void test_black_bishop_is_blocked_by_black_piece() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("a8", BLACK_BISHOP);
            b.put("c6", BLACK_ROOK);
        });
        assertMoves(board, "a8", "b7");
    }

    @Test
    public void test_black_bishop_is_blocked_by_white_piece() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("a8", BLACK_BISHOP);
            b.put("c6", WHITE_ROOK);
        });
        assertMoves(board, "a8", "b7", "c6");
    }

    @Test
    public void test_white_rook_in_center_board() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("d5", WHITE_ROOK);
        });
        assertMoves(board, "d5", "a5", "b5", "c5", "e5", "f5", "g5", "h5", "d1", "d2", "d3", "d4", "d6", "d7", "d8");
    }

    @Test
    public void test_white_rook_on_rank_1() {
        Board board = setupBoard(b -> {
            addKings(b, "a2", "h8");
            b.put("d1", WHITE_ROOK);
        });
        assertMoves(board, "d1", "a1", "b1", "c1", "e1", "f1", "g1", "h1", "d2", "d3", "d4", "d5", "d6", "d7", "d8");
    }

    @Test
    public void test_white_rook_on_rank_8() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h7");
            b.put("d8", WHITE_ROOK);
        });
        assertMoves(board, "d8", "a8", "b8", "c8", "e8", "f8", "g8", "h8", "d1", "d2", "d3", "d4", "d5", "d6", "d7");
    }

    @Test
    public void test_white_rook_on_file_a() {
        Board board = setupBoard(b -> {
            addKings(b, "b2", "h8");
            b.put("a4", WHITE_ROOK);
        });
        assertMoves(board, "a4", "a1", "a2", "a3", "a5", "a6", "a7", "a8", "b4", "c4", "d4", "e4", "f4", "g4", "h4");
    }

    @Test
    public void test_white_rook_on_file_h() {
        Board board = setupBoard(b -> {
            addKings(b, "a2", "g7");
            b.put("h3", WHITE_ROOK);
        });
        assertMoves(board, "h3", "h1", "h2", "h4", "h5", "h6", "h7", "h8", "a3", "b3", "c3", "d3", "e3", "f3", "g3");
    }

    @Test
    public void test_white_rook_at_h1() {
        Board board = setupBoard(b -> {
            addKings(b, "a2", "g8");
            b.put("h1", WHITE_ROOK);
        });
        assertMoves(board, "h1", "h2", "h3", "h4", "h5", "h6", "h7", "h8", "a1", "b1", "c1", "d1", "e1", "f1", "g1");
    }

    @Test
    public void test_white_rook_blocks_and_takes() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "g8");
            b.put("h4", WHITE_ROOK);
            b.put("h3", BLACK_PAWN);
            b.put("f4", WHITE_KNIGHT);
            b.put("h5", WHITE_BISHOP);
        });
        assertMoves(board, "h4", "h3", "g4");
    }

    @Test
    public void test_black_rook_in_center_board() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("d5", BLACK_ROOK);
        });
        assertMoves(board, "d5", "a5", "b5", "c5", "e5", "f5", "g5", "h5", "d1", "d2", "d3", "d4", "d6", "d7", "d8");
    }

    @Test
    public void test_black_rook_on_rank_1() {
        Board board = setupBoard(b -> {
            addKings(b, "a2", "h8");
            b.put("d1", BLACK_ROOK);
        });
        assertMoves(board, "d1", "a1", "b1", "c1", "e1", "f1", "g1", "h1", "d2", "d3", "d4", "d5", "d6", "d7", "d8");
    }

    @Test
    public void test_black_rook_on_rank_8() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h7");
            b.put("d8", BLACK_ROOK);
        });
        assertMoves(board, "d8", "a8", "b8", "c8", "e8", "f8", "g8", "h8", "d1", "d2", "d3", "d4", "d5", "d6", "d7");
    }

    @Test
    public void test_black_rook_on_file_a() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("a4", BLACK_ROOK);
        });
        assertMoves(board, "a4", "a1", "a2", "a3", "a5", "a6", "a7", "a8", "b4", "c4", "d4", "e4", "f4", "g4", "h4");
    }

    @Test
    public void test_black_rook_on_file_h() {
        Board board = setupBoard(b -> {
            addKings(b, "a2", "g6");
            b.put("h3", BLACK_ROOK);
        });
        assertMoves(board, "h3", "h1", "h2", "h4", "h5", "h6", "h7", "h8", "a3", "b3", "c3", "d3", "e3", "f3", "g3");
    }

    @Test
    public void test_black_rook_at_h1() {
        Board board = setupBoard(b -> {
            addKings(b, "a2", "g8");
            b.put("h1", BLACK_ROOK);
        });
        assertMoves(board, "h1", "h2", "h3", "h4", "h5", "h6", "h7", "h8", "a1", "b1", "c1", "d1", "e1", "f1", "g1");
    }


    @Test
    public void test_black_rook_blocks_and_takes() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "g8");
            b.put("h4", BLACK_ROOK);
            b.put("h3", WHITE_PAWN);
            b.put("f4", BLACK_KNIGHT);
            b.put("h5", BLACK_BISHOP);
        });
        assertMoves(board, "h4", "h3", "g4");
    }

    @Test
    public void test_white_queen_in_center_board() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("c4", WHITE_QUEEN);
        });
        assertMoves(board, "c4",
                "a4", "b4", "d4", "e4", "f4", "g4", "h4",
                "c1", "c2", "c3", "c5", "c6", "c7", "c8",
                "a2", "b3", "d5", "e6", "f7", "g8",
                "a6", "b5", "d3", "e2", "f1");
    }

    @Test
    public void test_white_queen_on_file_a() {
        Board board = setupBoard(b -> {
            addKings(b, "b1", "h8");
            b.put("a5", WHITE_QUEEN);
        });
        assertMoves(board, "a5",
                "a1", "a2", "a3", "a4", "a6", "a7", "a8",
                "b5", "c5", "d5", "e5", "f5", "g5", "h5",
                "b6", "c7", "d8",
                "b4", "c3", "d2", "e1");
    }

    @Test
    public void test_white_queen_on_rank_8() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h7");
            b.put("d8", WHITE_QUEEN);
        });
        assertMoves(board, "d8",
                "a8", "b8", "c8", "e8", "f8", "g8", "h8",
                "d1", "d2", "d3", "d4", "d5", "d6", "d7",
                "c7", "b6", "a5",
                "e7", "f6", "g5", "h4");
    }

    @Test
    public void test_white_queen_on_a8() {
        Board board = setupBoard(b -> {
            addKings(b, "b1", "h7");
            b.put("a8", WHITE_QUEEN);
        });
        assertMoves(board, "a8",
                "b8", "c8", "d8", "e8", "f8", "g8", "h8",
                "a1", "a2", "a3", "a4", "a5", "a6", "a7",
                "b7", "c6", "d5", "e4", "f3", "g2", "h1");
    }

    @Test
    public void test_white_queen_blocks_and_takes() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("d4", WHITE_QUEEN);
            b.put("d5", WHITE_PAWN);
            b.put("d3", BLACK_PAWN);
            b.put("b4", WHITE_BISHOP);
            b.put("f4", BLACK_BISHOP);
            b.put("c3", WHITE_ROOK);
            b.put("e5", BLACK_ROOK);
            b.put("f2", WHITE_KNIGHT);
            b.put("b6", BLACK_KNIGHT);
        });
        assertMoves(board, "d4",
                "c4", "e4", "f4",
                "d3",
                "e5",
                "b6", "c5", "e3");
    }

    @Test
    public void test_black_queen_in_center_board() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("c4", BLACK_QUEEN);
        });
        assertMoves(board, "c4",
                "a4", "b4", "d4", "e4", "f4", "g4", "h4",
                "c1", "c2", "c3", "c5", "c6", "c7", "c8",
                "a2", "b3", "d5", "e6", "f7", "g8",
                "a6", "b5", "d3", "e2", "f1");
    }

    @Test
    public void test_black_queen_on_file_a() {
        Board board = setupBoard(b -> {
            addKings(b, "b1", "h8");
            b.put("a5", BLACK_QUEEN);
        });
        assertMoves(board, "a5",
                "a1", "a2", "a3", "a4", "a6", "a7", "a8",
                "b5", "c5", "d5", "e5", "f5", "g5", "h5",
                "b6", "c7", "d8",
                "b4", "c3", "d2", "e1");
    }

    @Test
    public void test_black_queen_on_rank_8() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h7");
            b.put("d8", BLACK_QUEEN);
        });
        assertMoves(board, "d8",
                "a8", "b8", "c8", "e8", "f8", "g8", "h8",
                "d1", "d2", "d3", "d4", "d5", "d6", "d7",
                "c7", "b6", "a5",
                "e7", "f6", "g5", "h4");
    }

    @Test
    public void test_black_queen_on_a8() {
        Board board = setupBoard(b -> {
            addKings(b, "b1", "h7");
            b.put("a8", BLACK_QUEEN);
        });
        assertMoves(board, "a8",
                "b8", "c8", "d8", "e8", "f8", "g8", "h8",
                "a1", "a2", "a3", "a4", "a5", "a6", "a7",
                "b7", "c6", "d5", "e4", "f3", "g2", "h1");
    }

    @Test
    public void test_black_queen_blocks_and_takes() {
        Board board = setupBoard(b -> {
            addKings(b, "a1", "h8");
            b.put("d4", BLACK_QUEEN);
            b.put("d5", BLACK_PAWN);
            b.put("d3", WHITE_PAWN);
            b.put("b4", BLACK_BISHOP);
            b.put("f4", WHITE_BISHOP);
            b.put("c3", BLACK_ROOK);
            b.put("e5", WHITE_ROOK);
            b.put("f2", BLACK_KNIGHT);
            b.put("b6", WHITE_KNIGHT);
        });
        assertMoves(board, "d4",
                "c4", "e4", "f4",
                "d3",
                "e5",
                "b6", "c5", "e3");
    }

    @Test
    public void test_white_king_move_in_center_board() {
        Board board = setupBoard(b -> {
            addKings(b, "c5", "h8");
        });
        assertMoves(board, "c5", "d5", "d4", "c4", "b4", "b5", "b6", "c6", "d6");
    }

    @Test
    public void test_white_king_move_on_file_a() {
        Board board = setupBoard(b -> {
            addKings(b, "a5", "h8");
        });
        assertMoves(board, "a5", "b5", "b4", "a4", "a6", "b6");
    }

    @Test
    public void test_white_king_move_on_file_h() {
        Board board = setupBoard(b -> {
            addKings(b, "h5", "h8");
        });
        assertMoves(board, "h5", "h6", "h4", "g4", "g5", "g6");
    }

    @Test
    public void test_white_king_move_on_rank_1() {
        Board board = setupBoard(b -> {
            addKings(b, "c1", "h8");
        });
        assertMoves(board, "c1", "d1", "b1", "b2", "c2", "d2");
    }

    @Test
    public void test_white_king_move_on_rank_8() {
        Board board = setupBoard(b -> {
            addKings(b, "c8", "h8");
        });
        assertMoves(board, "c8", "d8", "d7", "c7", "b7", "b8");
    }

    @Test
    public void test_white_king_move_from_a8() {
        Board board = setupBoard(b -> {
            addKings(b, "a8", "h8");
        });
        assertMoves(board, "a8", "b8", "b7", "a7");
    }

    @Test
    public void test_white_king_can_take_black_pieces_from_all_angles() {
        String[] adjacencies = new String[] {"d5", "d4", "c4", "b4", "b5", "b6", "c6", "d6"};
        Board board = setupBoard(b -> {
            addKings(b, "c5", "h8");
            for (String cell : adjacencies) {
                b.put(cell, BLACK_KNIGHT);
            }
        });
        assertMoves(board, "c5", adjacencies);
    }

    @Test
    public void test_white_king_cannot_take_white_pieces() {
        String[] adjacencies = new String[] {"d5", "d4", "c4", "b4", "b5", "b6", "c6", "d6"};
        Board board = setupBoard(b -> {
            addKings(b, "c5", "h8");
            for (String cell : adjacencies) {
                b.put(cell, WHITE_KNIGHT);
            }
        });
        assertMoves(board, "c5");
    }

    @Test
    public void test_black_king_move_in_center_board() {
        Board board = setupBoard(b -> {
            addKings(b, "h8", "c5");
        });
        assertMoves(board, "c5", "d5", "d4", "c4", "b4", "b5", "b6", "c6", "d6");
    }

    @Test
    public void test_black_king_move_on_file_a() {
        Board board = setupBoard(b -> {
            addKings(b, "h8", "a5");
        });
        assertMoves(board, "a5", "b5", "b4", "a4", "a6", "b6");
    }

    @Test
    public void test_black_king_move_on_file_h() {
        Board board = setupBoard(b -> {
            addKings(b, "h8", "h5");
        });
        assertMoves(board, "h5", "h6", "h4", "g4", "g5", "g6");
    }

    @Test
    public void test_black_king_move_on_rank_1() {
        Board board = setupBoard(b -> {
            addKings(b, "h8", "c1");
        });
        assertMoves(board, "c1", "d1", "b1", "b2", "c2", "d2");
    }

    @Test
    public void test_black_king_move_on_rank_8() {
        Board board = setupBoard(b -> {
            addKings(b, "h8", "c8");
        });
        assertMoves(board, "c8", "d8", "d7", "c7", "b7", "b8");
    }

    @Test
    public void test_black_king_move_from_a8() {
        Board board = setupBoard(b -> {
            addKings(b, "h8", "a8");
        });
        assertMoves(board, "a8", "b8", "b7", "a7");
    }

    @Test
    public void test_black_king_can_take_white_pieces_from_all_angles() {
        String[] adjacencies = new String[] {"d5", "d4", "c4", "b4", "b5", "b6", "c6", "d6"};
        Board board = setupBoard(b -> {
            addKings(b, "h8", "c5");
            for (String cell : adjacencies) {
                b.put(cell, WHITE_KNIGHT);
            }
        });
        assertMoves(board, "c5", adjacencies);
    }

    @Test
    public void test_black_king_cannot_take_black_pieces() {
        String[] adjacencies = new String[] {"d5", "d4", "c4", "b4", "b5", "b6", "c6", "d6"};
        Board board = setupBoard(b -> {
            addKings(b, "h8", "c5");
            for (String cell : adjacencies) {
                b.put(cell, BLACK_KNIGHT);
            }
        });
        assertMoves(board, "c5");
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

    private static boolean hasMove(Board b, String from, String to) {
        return b.getMoves(convert(from)).contains(convert(to));
    }
}
