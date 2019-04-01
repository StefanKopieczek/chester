package com.kopieczek.chester.ui;

import com.kopieczek.chester.core.Board;
import com.kopieczek.chester.core.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class BoardView extends JPanel {
    private static final Color WHITE_TILE = new Color(0xffeecc);
    private static final Color BLACK_TILE = new Color(0xcd853f);
    private static final Color MOVE_COLOR = new Color(0x8066b2ff, true);
    private static final Color THREAT_COLOR = new Color(0xa0ff9999, true);

    private static final Map<Piece, BufferedImage> PIECES = ImageLoader.loadImageMap();
    private static final int TILE_SIZE = PIECES.get(Piece.WHITE_PAWN).getHeight();
    private static final int EDGE_SIZE = TILE_SIZE * 8;

    private Board board;
    private Integer selectedTile = null;

    public BoardView(Board board) {
        super();
        this.board = board;
        setPreferredSize(new Dimension(EDGE_SIZE, EDGE_SIZE));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);
        drawMoves(g);
        drawPieces(g);
    }

    private void drawGrid(Graphics g) {
        for (int cell = 0; cell < 64; cell++) {
            Color color = ((cell / 8 + cell) % 2 == 0) ? BLACK_TILE : WHITE_TILE;
            g.setColor(color);
            int pixelX = getPixelXForCell(cell);
            int pixelY = getPixelYForCell(cell);
            g.fillRect(pixelX, pixelY, TILE_SIZE, TILE_SIZE);
        }
    }

    private void drawMoves(Graphics g) {
        if (selectedTile != null) {
            // Highlight moves for selected tile
            Collection<Integer> moves = board.getMoves(selectedTile);
            for (int move : moves) {
                Color color = board.get(move).map(piece -> THREAT_COLOR).orElse(MOVE_COLOR);
                g.setColor(color);
                int pixelX = getPixelXForCell(move);
                int pixelY = getPixelYForCell(move);
                g.fillRect(pixelX, pixelY, TILE_SIZE, TILE_SIZE);
            }

            // Highlight selected tile itself
            g.setColor(MOVE_COLOR);
            int pixelX = getPixelXForCell(selectedTile);
            int pixelY = getPixelYForCell(selectedTile);
            g.fillRect(pixelX, pixelY, TILE_SIZE, TILE_SIZE);
        }
    }

    private void drawPieces(Graphics g) {
        for (int cell = 0; cell < 64; cell++) {
            Optional<Piece> piece = board.get(cell);
            if (piece.isPresent()) {
                int pixelX = getPixelXForCell(cell);
                int pixelY = getPixelYForCell(cell);
                g.drawImage(PIECES.get(piece.get()), pixelX, pixelY, null);
            }
        }
    }

    private static int getPixelXForCell(int cell) {
        int file = cell % 8;
        return file * TILE_SIZE;
    }

    private static int getPixelYForCell(int cell) {
        int rank = cell / 8;
        return (7 - rank) * TILE_SIZE;
    }
}
