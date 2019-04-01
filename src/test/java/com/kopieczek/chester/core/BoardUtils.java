package com.kopieczek.chester.core;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.kopieczek.chester.core.CoordConverter.convert;
import static com.kopieczek.chester.core.Piece.BLACK_KING;
import static com.kopieczek.chester.core.Piece.WHITE_KING;

public class BoardUtils {
    static Board setupBoard(Consumer<Map<String, Piece>> setup) {
        Map<String, Piece> schema = new HashMap<>();
        setup.accept(schema);
        Board board = new Board();
        schema.entrySet().forEach(entry -> {
            board.put(convert(entry.getKey()), entry.getValue());
        });
        return board;
    }

    static void addKings(Map<String, Piece> schema, String whiteKing, String blackKing) {
        schema.put(whiteKing, WHITE_KING);
        schema.put(blackKing, BLACK_KING);
    }
}
