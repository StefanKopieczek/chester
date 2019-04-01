package com.kopieczek.chester.ui;

import com.google.common.base.Suppliers;
import com.kopieczek.chester.core.Board;
import com.kopieczek.chester.core.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class BoardView extends JPanel {
    private static Color WHITE_TILE = new Color(0xffeecc);
    private static Color BLACK_TILE = new Color(0xcd853f);

    private static Map<Piece, BufferedImage> pieces = ImageLoader.loadImageMap();
    private static Supplier<Integer> EDGE_SIZE = Suppliers.memoize(() -> pieces.get(Piece.WHITE_PAWN).getHeight() * 8);
    private static Supplier<Integer> TILE_SIZE = Suppliers.memoize(() -> pieces.get(Piece.WHITE_PAWN).getHeight());
    private Board board;

    public BoardView(Board board) {
        super();
        this.board = board;
        int edgeSize = EDGE_SIZE.get();
        setPreferredSize(new Dimension(edgeSize, edgeSize));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);
        drawPieces(g);
    }

    private void drawGrid(Graphics g) {
        for (int file = 0; file < 8; file++) {
            for (int rank = 0; rank < 8; rank++) {
                Color color = (file + rank) % 2 == 0 ? WHITE_TILE : BLACK_TILE;
                g.setColor(color);
                g.fillRect(TILE_SIZE.get() * file, TILE_SIZE.get() * rank, TILE_SIZE.get(), TILE_SIZE.get());
            }
        }
    }

    private void drawPieces(Graphics g) {
        for (int file = 0; file < 8; file++) {
            for (int rank = 0; rank < 8; rank++) {
                Optional<Piece> piece = board.get(rank * 8 + file);
                if (piece.isPresent()) {
                    int pixelY = TILE_SIZE.get() * rank;
                    int pixelX = TILE_SIZE.get() * file;
                    g.drawImage(pieces.get(piece.get()), pixelX, pixelY, null);
                }
            }
        }
    }
}
