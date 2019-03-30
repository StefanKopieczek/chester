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
            case KNIGHT:
                return getMovesForKnight(cell, piece.getColor());
            default:
                throw new IllegalArgumentException("Unknown piece type " + piece.getType());
        }
    }

    private Collection<Integer> getMovesForPawn(int cell, Color ownColor) {
        List<Integer> moves = new ArrayList<>();
        final int sign = (ownColor == Color.WHITE) ? 1 : -1;
        final int standardMove = cell + sign * 8;
        final int doubleMove = cell + sign * 16;
        final int leftTake = cell - 1 + 8 * sign;
        final int rightTake = cell + 1 + 8 * sign;
        final int startingRank = (ownColor == Color.WHITE) ? 2 : 7;
        final boolean isOnStartingRank = (cell / 8 + 1 == startingRank);

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
        if (!isInLeftFile && !isEmpty(leftTake) && pieces[leftTake].getColor() != ownColor) {
            moves.add(leftTake);
        }
        if (!isInRightFile && !isEmpty(rightTake) && pieces[rightTake].getColor() != ownColor) {
            moves.add(rightTake);
        }

        return moves;
    }

    private Collection<Integer> getMovesForKnight(int cell, Color ownColor) {
        List<Integer> moves = new ArrayList<>();
        int rank = cell / 8;
        int file = cell % 8;
        for (int dX : getKnightDeltas(file)) {
            for (int dY : getKnightDeltas(rank)) {
                if (Math.abs(dX) != Math.abs(dY)) {
                    int target = cell + 8 * dY + dX;
                    if (isEmpty(target) || pieces[target].getColor() != ownColor) {
                        moves.add(target);
                    }
                }
            }
        }
        return moves;
    }

    private static Collection<Integer> getKnightDeltas(int axis) {
        List<Integer> deltas = new ArrayList<>();
        if (axis > 1)
            deltas.add(-2);
        if (axis > 0)
            deltas.add(-1);
        if (axis < 7)
            deltas.add(1);
        if (axis < 6)
            deltas.add(2);
        return deltas;
    }

    private boolean isEmpty(int cell) {
        return pieces[cell] == null;
    }
}
