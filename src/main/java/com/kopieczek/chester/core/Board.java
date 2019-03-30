package com.kopieczek.chester.core;

import java.util.*;

public class Board {
    private final Piece[] pieces = new Piece[64];

    public Optional<Piece> get(int cell) {
        return Optional.ofNullable(pieces[cell]);
    }

    public void put(int cell, Piece piece) {
        pieces[cell] = piece;
    }

    public void move(int from, int to) {
        pieces[to] = pieces[from];
        pieces[from] = null;
    }

    public Collection<Integer> getMoves(int cell) {
        return get(cell).map(piece -> getMovesForOccupiedCell(cell, piece))
                        .orElse(Collections.emptyList());
    }

    private Collection<Integer> getMovesForOccupiedCell(int cell, Piece piece) {
        switch (piece.getType()) {
            case PAWN:
                return getMovesForPawn(cell, piece.getColor());
            default:
                throw new IllegalArgumentException("Unknown piece type " + piece.getType());
        }
    }

    private Collection<Integer> getMovesForPawn(int cell, Color color) {
        List<Integer> moves = new ArrayList<>();
        final int sign = (color == Color.WHITE) ? 1 : -1;
        final int standardMove = cell + sign * 8;
        final int doubleMove = cell + sign * 16;
        final int leftTake = cell - 1 + 8 * sign;
        final int rightTake = cell + 1 + 8 * sign;
        final int startingRank = (color == Color.WHITE) ? 2 : 7;
        final boolean isOnStartingRank = (cell / 8 + 1 == startingRank);
        final Color enemy = (color == Color.WHITE) ? Color.BLACK : Color.WHITE;

        if (standardMove < 0 || standardMove > 63) {
            // Pawn is on last rank; no moves are possible
            return moves;
        }

        if (isEmpty(standardMove)) {
            // Pawn has a clear cell directly ahead, so non-taking moves are possible
            moves.add(standardMove);
            if (isOnStartingRank && isEmpty(doubleMove)) {
                moves.add(doubleMove);
            }
        }

        // Add in 'taking' moves if any are possible
        boolean isInLeftFile = cell % 8 == 0;
        boolean isInRightFile = cell % 8 == 7;
        if (!isInLeftFile && !isEmpty(leftTake) && pieces[leftTake].getColor() == enemy) {
            moves.add(leftTake);
        }
        if (!isInRightFile && !isEmpty(rightTake) && pieces[rightTake].getColor() == enemy) {
            moves.add(rightTake);
        }

        return moves;
    }

    private boolean isEmpty(int cell) {
        return pieces[cell] == null;
    }
}
