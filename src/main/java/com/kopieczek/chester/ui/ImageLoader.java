package com.kopieczek.chester.ui;

import com.google.common.collect.ImmutableList;
import com.kopieczek.chester.core.Piece;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kopieczek.chester.core.Piece.*;

public class ImageLoader {
    private static List<Piece> ORDER_OF_PIECES = ImmutableList.of(
            BLACK_QUEEN, BLACK_KING, BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_PAWN,
            WHITE_QUEEN, WHITE_KING, WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_PAWN
    );

    private static BufferedImage getPieceGrid() {
        try {
            return ImageIO.read(ImageLoader.class.getClassLoader().getResourceAsStream("pieces_array.png"));
        } catch (Exception e) {
            throw new RuntimeException("Unable to load image data for pieces", e);
        }
    }

    public static Map<Piece, BufferedImage> loadImageMap() {
        Map<Piece, BufferedImage> imageMap = new HashMap<>();
        BufferedImage tileGrid = getPieceGrid();
        int tileWidth = tileGrid.getWidth() / 6;
        int tileHeight = tileGrid.getHeight() / 2;

        int tileX = 0;
        int tileY = 0;
        for (Piece piece : ORDER_OF_PIECES) {
            imageMap.put(piece, getTile(tileGrid, tileWidth, tileHeight, tileX, tileY));
            tileX++;
            if (tileX == 6) {
                tileX = 0;
                tileY++;
            }
        }

        return imageMap;
    }

    private static BufferedImage getTile(BufferedImage tilemap, int tileWidth, int tileHeight, int tileX, int tileY) {
        int pixelX = tileWidth * tileX;
        int pixelY = tileHeight * tileY;
        return tilemap.getSubimage(pixelX, pixelY, tileWidth, tileHeight);
    }
}
