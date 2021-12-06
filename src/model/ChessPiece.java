package model;

import java.awt.*;
import java.util.Arrays;

public enum ChessPiece {
    BLACK(Color.BLACK), WHITE(Color.WHITE), GRAY(Color.gray);

    private final Color color;

    ChessPiece(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        if(color==Color.BLACK)return "1";
        if(color==Color.GRAY)return "0";
        return "2";
    }
}
