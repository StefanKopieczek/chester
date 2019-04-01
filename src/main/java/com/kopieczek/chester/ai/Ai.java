package com.kopieczek.chester.ai;

import com.kopieczek.chester.core.Board;
import com.kopieczek.chester.core.Color;

public interface Ai {
    Move getMove(Board board, Color color);
}
