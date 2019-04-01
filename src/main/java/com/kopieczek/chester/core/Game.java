package com.kopieczek.chester.core;

public class Game {
    private final Board board;
    private Color activePlayer = Color.WHITE;

    public Game(Board board) {
        this.board = board;
    }

    public Color getActivePlayer() {
        return activePlayer;
    }

    public void move(int from, int to) {
        board.move(from, to);
        activePlayer = activePlayer.inverse();
    }

    public Board getBoard() {
        return board;
    }
}
