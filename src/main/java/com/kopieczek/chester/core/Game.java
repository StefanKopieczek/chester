package com.kopieczek.chester.core;

import java.util.*;

import static com.kopieczek.chester.core.GameState.*;

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

    public GameState getState() {
        return getState(board, activePlayer);
    }

    public static GameState getState(Board board, Color activePlayer) {
        Set<Integer> opponentThreats = board.getThreatenedSquares(activePlayer.inverse());
        int ourKing = -1;

        List<Integer> ourMoves = new ArrayList<>();
        for (int cell = 0; cell < 64; cell++) {
            if (!board.get(cell).isPresent()) {
                continue;
            }

            Piece piece = board.get(cell).get();
            if (piece.getColor() != activePlayer) {
                continue;
            }

            if (piece.getType() == PieceType.KING) {
                ourKing = cell;
            }

            ourMoves.addAll(board.getMoves(cell));
        }

        if (ourMoves.size() == 0) {
            if (opponentThreats.contains(ourKing)) {
                return (activePlayer == Color.WHITE) ? BLACK_WINS : WHITE_WINS;
            } else {
                return STALEMATE;
            }
        }
        return PLAYING;
    }
}
