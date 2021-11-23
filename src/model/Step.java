package model;

public class Step {
    public int rowIndex;
    public int columnIndex;
    ChessPiece color;
    int[] reserveNum;

    public Step(int rowIndex, int columnIndex, ChessPiece color, int[] reserveNum) {
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.color = color;
        this.reserveNum = reserveNum;
    }
}
