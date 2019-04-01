package com.kopieczek.chester.core;

import org.junit.Test;

import static com.kopieczek.chester.core.Color.BLACK;
import static com.kopieczek.chester.core.Color.WHITE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
}
