package com.kopieczek.chester.core;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Board {
    private final Piece[] pieces = new Piece[64];

    public static Board standardSetup() {
        Board board = new Board();
        board.put(0, Piece.WHITE_ROOK);
        board.put(1, Piece.WHITE_KNIGHT);
        board.put(2, Piece.WHITE_BISHOP);
        board.put(3, Piece.WHITE_QUEEN);
        board.put(4, Piece.WHITE_KING);
        board.put(5, Piece.WHITE_BISHOP);
        board.put(6, Piece.WHITE_KNIGHT);
        board.put(7, Piece.WHITE_ROOK);
        board.put(56, Piece.BLACK_ROOK);
        board.put(57, Piece.BLACK_KNIGHT);
        board.put(58, Piece.BLACK_BISHOP);
        board.put(59, Piece.BLACK_QUEEN);
        board.put(60, Piece.BLACK_KING);
        board.put(61, Piece.BLACK_BISHOP);
        board.put(62, Piece.BLACK_KNIGHT);
        board.put(63, Piece.BLACK_ROOK);

        for (int file = 0; file < 8; file++) {
            board.put(8 + file, Piece.WHITE_PAWN);
            board.put(48 + file, Piece.BLACK_PAWN);
        }

        return board;
    }

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

    // Visible for testing
    Collection<Integer> getMovesForOccupiedCell(int cell, Piece piece) {
        return getMovesForOccupiedCellWithoutThreatChecks(cell, piece).stream()
                .filter(move -> !movePutsSelfInCheck(cell, move))
                .collect(Collectors.toList());
    }

    private Collection<Integer> getMovesForOccupiedCellWithoutThreatChecks(int cell, Piece piece) {
        switch (piece.getType()) {
            case PAWN:
                return getMovesForPawn(cell, piece.getColor());
            case KNIGHT:
                return getMovesForKnight(cell, piece.getColor());
            case BISHOP:
                return getMovesForBishop(cell, piece.getColor());
            case ROOK:
                return getMovesForRook(cell, piece.getColor());
            case QUEEN:
                return getMovesForQueen(cell, piece.getColor());
            case KING:
                return getMovesForKing(cell, piece.getColor());
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

    private Collection<Integer> getMovesForBishop(int cell, Color ownColor) {
        List<Integer> moves = new ArrayList<>();
        final int[] deltas = new int[] {-1, 1};
        final int rank = cell / 8;
        final int file = cell % 8;

        for (int fileDelta : deltas) {
            for (int rankDelta : deltas) {
                // Add all moves in the direction determined by this given fileDelta and rankDelta, continuing
                // until we hit a blocking piece, or reach the edge of the board.
                int newRank = rank;
                int newFile = file;
                while (true) {
                    newRank += rankDelta;
                    newFile += fileDelta;
                    if (newRank < 0 || newRank > 7 || newFile < 0 || newFile > 7) {
                        break;
                    }

                    int newCell = newRank * 8 + newFile;
                    if (isEmpty(newCell)) {
                        moves.add(newCell);
                    } else if (pieces[newCell].getColor() != ownColor) {
                        // This cell is a capture; add the move but don't continue further in this direction
                        moves.add(newCell);
                        break;
                    } else {
                        // This cell is occupied by an allied piece; we cannot move here and cannot continue
                        // further in this direction
                        break;
                    }
                }
            }
        }

        return moves;
    }

    private Collection<Integer> getMovesForRook(int cell, Color ownColor) {
        List<Integer> moves = new ArrayList<>();

        // Left ray
        for (int target = cell - 1; target >= (cell / 8) * 8; target--) {
            if (isEmpty(target)) {
                moves.add(target);
                continue;
            } else if (pieces[target].getColor() != ownColor) {
                moves.add(target);
            }
            break;
        }

        // Right ray
        for (int target = cell + 1; target < (cell / 8) * 8 + 8; target++) {
            if (isEmpty(target)) {
                moves.add(target);
                continue;
            } else if (pieces[target].getColor() != ownColor) {
                moves.add(target);
            }
            break;
        }

        // Bottom ray
        for (int target = cell - 8; target >= 0; target -= 8) {
            if (isEmpty(target)) {
                moves.add(target);
                continue;
            } else if (pieces[target].getColor() != ownColor) {
                moves.add(target);
            }
            break;
        }

        // Top ray
        for (int target = cell + 8; target < 64; target += 8) {
            if (isEmpty(target)) {
                moves.add(target);
                continue;
            } else if (pieces[target].getColor() != ownColor) {
                moves.add(target);
            }
            break;
        }

        return moves;
    }

    private Collection<Integer> getMovesForQueen(int cell, Color ownColor) {
        List<Integer> moves = new ArrayList<>();
        moves.addAll(getMovesForBishop(cell, ownColor));
        moves.addAll(getMovesForRook(cell, ownColor));
        return moves;
    }

    private Collection<Integer> getMovesForKing(int cell, Color ownColor) {
        int rank = cell / 8;
        int file = cell % 8;
        boolean includeLeft = file > 0;
        boolean includeRight = file < 7;
        boolean includeBottom = rank > 0;
        boolean includeTop = rank < 7;

        List<Integer> moves = new ArrayList<>();
        if (includeLeft && includeTop)
            moves.add(cell + 7);
        if (includeTop)
            moves.add(cell + 8);
        if (includeTop && includeRight)
            moves.add(cell + 9);
        if (includeRight)
            moves.add(cell + 1);
        if (includeRight && includeBottom)
            moves.add(cell - 7);
        if (includeBottom)
            moves.add(cell - 8);
        if (includeLeft && includeBottom)
            moves.add(cell - 9);
        if (includeLeft)
            moves.add(cell - 1);

        Predicate<Integer> isValidMove = square -> isEmpty(square) || pieces[square].getColor() != ownColor;
        moves = moves.stream().filter(isValidMove).collect(Collectors.toList());
        return moves;
    }

    private boolean movePutsSelfInCheck(int from, int to) {
        Piece mover = pieces[from];
        Piece maybeTaken = pieces[to];

        // Temporarily put the board into the position in question to check the threats
        pieces[from] = null;
        pieces[to] = mover;

        Predicate<Integer> containsOwnKing = cell ->
                !isEmpty(cell) &&
                pieces[cell].getColor() == mover.getColor() &&
                pieces[cell].getType() == PieceType.KING;

        boolean result = getThreatenedSquares(mover.getColor().inverse())
                    .stream()
                    .anyMatch(containsOwnKing);

        // Return the board to its original position
        pieces[from] = mover;
        pieces[to] = maybeTaken;

        return result;
    }

    public Set<Integer> getThreatenedSquares(Color color) {
        Set<Integer> result = new HashSet<>();
        for (int cell = 0; cell < 64; cell++) {
            if (!isEmpty(cell) && pieces[cell].getColor() == color) {
                result.addAll(getMovesForOccupiedCellWithoutThreatChecks(cell, pieces[cell]));
            }
        }
        return result;
    }

    private boolean isEmpty(int cell) {
        return pieces[cell] == null;
    }
}
