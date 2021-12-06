package model;

import view.GameFrame;

public class Step {
    public int ischeat;
    public int rowIndex;
    public int columnIndex;
    ChessPiece color;
    int[] reserveNum;

    public Step(int rowIndex, int columnIndex, ChessPiece color, int[] reserveNum) {
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.color = color;
        this.reserveNum = reserveNum;
        this.ischeat= GameFrame.cheat?1:0;
    }

    @Override
    public String toString() {
        return String.format("%d %d %s %d \r\n",rowIndex,columnIndex,color.toString(),ischeat);
    }
}
