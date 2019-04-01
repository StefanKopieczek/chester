package com.kopieczek.chester.ui;

import com.kopieczek.chester.ai.Ai;
import com.kopieczek.chester.ai.Move;
import com.kopieczek.chester.ai.RandomAi;
import com.kopieczek.chester.core.Game;
import com.kopieczek.chester.core.GameState;
import com.kopieczek.chester.core.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class GameView extends JPanel {
    private static final Color WHITE_TILE = new Color(0xffeecc);
    private static final Color BLACK_TILE = new Color(0xcd853f);
    private static final Color MOVE_COLOR = new Color(0x8066b2ff, true);
    private static final Color THREAT_COLOR = new Color(0xa0ff9999, true);

    private static final Map<Piece, BufferedImage> PIECES = ImageLoader.loadImageMap();
    private static final int TILE_SIZE = PIECES.get(Piece.WHITE_PAWN).getHeight();
    private static final int EDGE_SIZE = TILE_SIZE * 8;

    private Game game;
    private Integer selectedTile = null;
    private Ai enemy = new RandomAi();

    public GameView(Game game) {
        super();
        this.game = game;
        setPreferredSize(new Dimension(EDGE_SIZE, EDGE_SIZE));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                GameView.this.onClick(e);
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);
        drawMoves(g);
        drawPieces(g);
        drawMessages(g);
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
            Collection<Integer> moves = game.getBoard().getMoves(selectedTile);
            for (int move : moves) {
                Color color = game.getBoard().get(move).map(piece -> THREAT_COLOR).orElse(MOVE_COLOR);
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
            Optional<Piece> piece = game.getBoard().get(cell);
            if (piece.isPresent()) {
                int pixelX = getPixelXForCell(cell);
                int pixelY = getPixelYForCell(cell);
                g.drawImage(PIECES.get(piece.get()), pixelX, pixelY, null);
            }
        }
    }

    private void drawMessages(Graphics g) {
        switch (game.getState()) {
            case STALEMATE:
                renderBanner(g, "Stalemate!");
                break;
            case WHITE_WINS:
                renderBanner(g, "Checkmate – white wins!");
                break;
            case BLACK_WINS:
                renderBanner(g, "Checkmate - black wins!");
                break;
        }
    }

    private void renderBanner(Graphics g, String message) {
        Graphics2D g2d = (Graphics2D)g;

        Font font = new Font("Arial", Font.BOLD, 40);
        GlyphVector glyphVector = font.createGlyphVector(g2d.getFontRenderContext(), message);
        int glyphWidth = glyphVector.getOutline().getBounds().width;
        int paddingX = (EDGE_SIZE - glyphWidth) / 2;
        Shape textShape = glyphVector.getOutline(paddingX, (float)EDGE_SIZE / 2 - 5);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.draw(textShape);
        g2d.setColor(Color.RED);
        g2d.fill(textShape);
    }

    private void onClick(MouseEvent e) {
        if (game.getState() == GameState.PLAYING) {
            int targetCell = getCellForPixels(e.getX(), e.getY());
            if (selectedTile == null) {
                trySelect(targetCell);
            } else {
                tryMove(targetCell);
            }
        }
    }

    private void trySelect(int targetCell) {
        Optional<Piece> target = game.getBoard().get(targetCell);
        com.kopieczek.chester.core.Color targetColor = target.map(Piece::getColor).orElse(null);
        if (targetColor == game.getActivePlayer()) {
            selectedTile = targetCell;
        }
        repaint();
    }

    private void tryMove(int target) {
        if (game.getBoard().getMoves(selectedTile).contains(target)) {
            game.move(selectedTile, target);
            selectedTile = null;
            repaint();
            Move aiMove = enemy.getMove(game.getBoard(), com.kopieczek.chester.core.Color.BLACK);
            game.move(aiMove.from, aiMove.to);
        }

        selectedTile = null;
        repaint();
    }

    private static int getPixelXForCell(int cell) {
        int file = cell % 8;
        return file * TILE_SIZE;
    }

    private static int getPixelYForCell(int cell) {
        int rank = cell / 8;
        return (7 - rank) * TILE_SIZE;
    }

    private static int getCellForPixels(int pixelX, int pixelY) {
        int file = pixelX / TILE_SIZE;
        int rank = 7 - (pixelY) / TILE_SIZE;
        return 8 * rank + file;
    }
}
