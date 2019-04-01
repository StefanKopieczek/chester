package com.kopieczek.chester.core;

import org.junit.Test;

import static com.kopieczek.chester.core.Color.BLACK;
import static com.kopieczek.chester.core.Color.WHITE;
import static com.kopieczek.chester.core.CoordConverter.convert;
import static org.junit.Assert.*;

public class TestGame {
    @Test
    public void test_turn_1_is_white() {
        Game g = new Game(Board.standardSetup());
        assertEquals(WHITE, g.getActivePlayer());
    }

    @Test
    public void test_turn_2_is_black() {
        Game g = new Game(Board.standardSetup());
        g.move(8, 16);
        assertEquals(BLACK, g.getActivePlayer());
    }

    @Test
    public void test_get_active_player_is_idempotent() {
        Game g = new Game(Board.standardSetup());
        g.move(8, 16);
        assertEquals(g.getActivePlayer(), g.getActivePlayer());
    }

    @Test
    public void test_get_board() {
        Board board = Board.standardSetup();
        Game game = new Game(board);
        assertTrue(board == game.getBoard());
    }

    @Test
    public void test_game_state_is_initially_playing() {
        Game game = new Game(Board.standardSetup());
        assertEquals(GameState.PLAYING, game.getState());
    }

    @Test
    public void test_game_state_is_playing_after_one_turn() {
        Game game = new Game(Board.standardSetup());
        game.move(8, 9);
        assertEquals(GameState.PLAYING, game.getState());
    }

    @Test
    public void test_game_state_is_playing_late_game() {
        Game game = new Game(BoardUtils.setupBoard(b -> {
            BoardUtils.addKings(b, "a1", "h8");
            b.put("c3", Piece.WHITE_KNIGHT);
            b.put("c4", Piece.WHITE_ROOK);
            b.put("h7", Piece.BLACK_QUEEN);
            b.put("d8", Piece.BLACK_ROOK);
        }));
        assertEquals(GameState.PLAYING, game.getState());
    }

    @Test
    public void test_game_state_for_white_checkmate() {
        Game game = new Game(BoardUtils.setupBoard(b -> {
            BoardUtils.addKings(b, "a1", "h8");
            b.put("a7", Piece.WHITE_ROOK);
            b.put("b7", Piece.WHITE_QUEEN);
        }));
        assertEquals("Game should initially be in state 'playing'", GameState.PLAYING, game.getState());
        game.move(convert("b7"), convert("b8"));
        assertEquals("White should have just checkmated black", GameState.WHITE_WINS, game.getState());
    }

    @Test
    public void test_game_state_for_black_checkmate() {
        Game game = new Game(BoardUtils.setupBoard(b -> {
            BoardUtils.addKings(b, "h7", "a1");
            b.put("a7", Piece.BLACK_ROOK);
            b.put("b7", Piece.BLACK_QUEEN);
        }));
        assertEquals("Game should initially be in state 'playing'", GameState.PLAYING, game.getState());

        // Make one move as white so that it becomes black's turn
        game.move(convert("h7"), convert("h8"));
        assertEquals("Game should still be in state 'playing'", GameState.PLAYING, game.getState());

        // Now checkmate as black
        game.move(convert("b7"), convert("b8"));
        assertEquals("Black should have just checkmated white", GameState.BLACK_WINS, game.getState());
    }

    @Test
    public void test_check_does_not_end_game() {
        Game game = new Game(BoardUtils.setupBoard(b -> {
            BoardUtils.addKings(b, "a1", "h8");
            b.put("a2", Piece.WHITE_ROOK);
        }));
        game.move(convert("a2"), convert("a8"));
        assertEquals("Game should not have ended", GameState.PLAYING, game.getState());
    }

    @Test
    public void test_white_forcing_stalemate() {
        Game game = new Game(BoardUtils.setupBoard(b -> {
            BoardUtils.addKings(b, "a1", "h8");
            b.put("g6", Piece.WHITE_ROOK);
            b.put("a2", Piece.WHITE_ROOK);
        }));
        game.move(convert("a2"), convert("a7"));
        assertEquals(GameState.STALEMATE, game.getState());
    }

    @Test
    public void test_black_forcing_stalemate() {
        Game game = new Game(BoardUtils.setupBoard(b -> {
            BoardUtils.addKings(b, "h7", "a1");
            b.put("g6", Piece.BLACK_ROOK);
            b.put("a2", Piece.BLACK_ROOK);
        }));
        game.move(convert("h7"), convert("h8"));
        game.move(convert("a2"), convert("a7"));
        assertEquals(GameState.STALEMATE, game.getState());
    }
}
