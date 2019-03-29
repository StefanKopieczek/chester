package com.kopieczek.chester.core;

public class CoordConverter {
    public static String convert(int boardCoord) {
        String file = Character.toString((char) (97 + boardCoord % 8));
        int rank = (boardCoord / 8) + 1;
        return file + rank;
    }

    public static int convert(String algebraicCoord) {
        int filePart = (int)algebraicCoord.charAt(0) - 97;
        int rankPart = 8 * (Integer.parseInt(algebraicCoord.substring(1)) - 1);
        return filePart + rankPart;
    }
}
