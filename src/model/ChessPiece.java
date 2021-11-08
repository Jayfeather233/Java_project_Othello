package model;

import java.awt.*;

public enum ChessPiece {
    BLACK(Color.BLACK), WHITE(Color.WHITE), GRAY(Color.gray);

    private final Color color;

    ChessPiece(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
