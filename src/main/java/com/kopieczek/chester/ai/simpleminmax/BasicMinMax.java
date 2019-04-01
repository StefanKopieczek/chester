package com.kopieczek.chester.ai.simpleminmax;

import com.kopieczek.chester.ai.Ai;
import com.kopieczek.chester.ai.Move;
import com.kopieczek.chester.core.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class BasicMinMax implements Ai {
    private static final int DEPTH = 3;
    private static final Integer STATE_SEARCH_CUTOFF = 12;

    @Override
    public Move getMove(Board board, Color color) {
        return minmax(board, color, DEPTH).move;
    }

    public MoveWithScore minmax(Board board, Color color, int depth) {
        List<MoveWithScore> candidates = new ArrayList<>();
        for (Move move : getAllMoves(board, color)) {
            Piece moved = board.get(move.from).get();
            Optional<Piece> maybeTaken = board.get(move.to);
            board.move(move.from, move.to);

            int score;
            if (depth == 0) {
                score = applyHeuristic(board, color);
            } else {
                score = -minmax(board, color.inverse(), depth - 1).score;
            }

            board.put(move.from, moved);
            board.put(move.to, maybeTaken.orElse(null));
            candidates.add(new MoveWithScore(move, score));
        }

        candidates.sort(Comparator.comparing(moveWithScore -> -moveWithScore.score));

        if (candidates.size() == 0) {
            // No moves possible, so the game must be over. Score the end state.
            GameState state = Game.getState(board, color);
            int score = 0;
            if (state.equals(GameState.WHITE_WINS)) {
                score = (color == Color.WHITE) ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            } else if (state.equals(GameState.BLACK_WINS)) {
                score = (color == Color.BLACK) ? Integer.MIN_VALUE : Integer.MIN_VALUE;
            } else if (state.equals(GameState.STALEMATE)) {
                score = -5;
            } else {
                throw new IllegalStateException("No candidate moves but not in any end state. Programmer error?");
            }
            return new MoveWithScore(null, score);
        } else {
            return candidates.get(0);
        }
    }

    // Hack hack hack
    private GameState guessState(Board board, Color color) {
        if (STATE_SEARCH_CUTOFF != null) {
            AtomicInteger white = new AtomicInteger();
            AtomicInteger black = new AtomicInteger();
            for (int cell = 0; cell < 64; cell++) {
                board.get(cell).ifPresent(piece -> {
                    if (piece.getColor() == Color.WHITE) {
                        white.incrementAndGet();
                    } else {
                        black.incrementAndGet();
                    }
                });
            }
            if (white.get() > STATE_SEARCH_CUTOFF || black.get() > STATE_SEARCH_CUTOFF) {
                // Too many pieces; don't do a checkmate search
                return GameState.PLAYING;
            }
        }

        return Game.getState(board, color);
    }

    private List<Move> getAllMoves(Board board, Color color) {
        List<Move> moves = new ArrayList<>();
        for (int cell = 0; cell < 64; cell++) {
            final int from = cell;
            board.get(cell).ifPresent(piece -> {
                if (piece.getColor() == color) {
                    for (int to : board.getMoves(from)) {
                        moves.add(new Move(from, to));
                    }
                }
            });
        }
        return moves;
    }

    private int applyHeuristic(Board board, Color color) {
        int score = 0;
        for (int cell = 0; cell < 64; cell++) {
            score += board.get(cell).map(piece -> getScore(piece, color)).orElse(0);
        }
        return score;
    }

    private int getScore(Piece piece, Color ownColor) {
        int rawScore;
        switch (piece.getType()) {
            case PAWN:
                rawScore = 1;
                break;
            case KNIGHT:
                rawScore = 3;
                break;
            case BISHOP:
                rawScore = 3;
                break;
            case ROOK:
                rawScore = 7;
                break;
            case QUEEN:
                rawScore = 9;
                break;
            default:
                rawScore = 0;
                break;
        }

        if (piece.getColor() == ownColor) {
            return rawScore;
        } else {
            return -rawScore;
        }
    }

    private static class MoveWithScore {
        final Move move;
        final int score;

        MoveWithScore(Move move, int score) {
            this.move = move;
            this.score = score;
        }
    }
}
