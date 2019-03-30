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
        if (isEmpty(cell)) {
            return moves;
        }

        else if (cell >= 56) {
            // Top row; cannot move up
        } else if (!isEmpty(cell + 8)) {
            // Cannot move onto or through a piece ahead
        } else if (cell / 8 == 1 && isEmpty(cell + 16)) {
            moves.add(cell + 8);
            moves.add(cell + 16);
        } else {
            moves.add(cell + 8);
        }

        if (cell <= 55) {
            if (cell % 8 != 0 && !isEmpty(cell + 7) && pieces[cell+7].getColor() == Color.BLACK) {
                moves.add(cell + 7);
            }
            if (cell % 8 != 7 && !isEmpty(cell + 9) && pieces[cell+9].getColor() == Color.BLACK) {
                moves.add(cell + 9);
            }
        }

        return moves;
    }

    private boolean isEmpty(int cell) {
        return pieces[cell] == null;
    }
}
