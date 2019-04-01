package com.kopieczek.chester.ai;

import com.kopieczek.chester.core.Board;
import com.kopieczek.chester.core.Color;
import com.kopieczek.chester.core.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class RandomAi implements Ai {
    private Random r = new Random();

    @Override
    public Move getMove(Board board, Color color) {
        List<Integer> piecesWithMoves = getPiecesWithMoves(board, color);
        int from = piecesWithMoves.get(r.nextInt(piecesWithMoves.size()));
        List<Integer> moves = new ArrayList<>(board.getMoves(from));
        int to = moves.get(r.nextInt(moves.size()));
        return new Move(from, to);
    }

    private List<Integer> getPiecesWithMoves(Board board, Color color) {
        List<Integer> pieces = new ArrayList<>();
        for (int cell = 0; cell < 64; cell++) {
            Optional<Piece> maybePiece = board.get(cell);
            if (maybePiece.isPresent() && maybePiece.get().getColor() == color && board.getMoves(cell).size() > 0) {
                pieces.add(cell);
            }
        }
        return pieces;
    }
}
