package com.kopieczek.chester.core;

public enum Piece {
    WHITE_PAWN(PieceType.PAWN, Color.WHITE),
    WHITE_KNIGHT(PieceType.KNIGHT, Color.WHITE),
    WHITE_BISHOP(PieceType.BISHOP, Color.WHITE),
    WHITE_ROOK(PieceType.ROOK, Color.WHITE),
    WHITE_QUEEN(PieceType.QUEEN, Color.WHITE),
    WHITE_KING(PieceType.KING, Color.WHITE),

    BLACK_PAWN(PieceType.PAWN, Color.BLACK),
    BLACK_KNIGHT(PieceType.KNIGHT, Color.BLACK),
    BLACK_BISHOP(PieceType.BISHOP, Color.BLACK),
    BLACK_ROOK(PieceType.ROOK, Color.BLACK),
    BLACK_QUEEN(PieceType.QUEEN, Color.BLACK),
    BLACK_KING(PieceType.KING, Color.BLACK);

    private final PieceType type;
    private final Color color;

    Piece(PieceType type, Color color) {
        this.type = type;
        this.color = color;
    }

    public PieceType getType() {
        return type;
    }

    public Color getColor() {
        return color;
    }
}
