package com.kopieczek.chester.ui;

import com.kopieczek.chester.core.Board;
import com.kopieczek.chester.core.Game;

import javax.swing.*;

public class ChesterUi extends JFrame {
    private ChesterUi() {
        Game game = new Game(Board.standardSetup());
        add(new GameView(game));
    }

    public static void main(String[] args) {
        ChesterUi ui = new ChesterUi();
        ui.pack();
        ui.setVisible(true);
    }
}
