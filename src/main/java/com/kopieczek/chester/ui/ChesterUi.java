package com.kopieczek.chester.ui;

import com.kopieczek.chester.core.Board;

import javax.swing.*;

public class ChesterUi extends JFrame {
    private ChesterUi() {
        Board board = Board.standardSetup();
        add(new BoardView(board));
    }

    public static void main(String[] args) {
        ChesterUi ui = new ChesterUi();
        ui.pack();
        ui.setVisible(true);
    }
}
