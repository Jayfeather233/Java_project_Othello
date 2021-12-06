package model;

import components.ChessGridComponent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class UndoList {
    public ArrayList<Step> stepList;
    private int[] tmp;
    public static int[] xDirection;
    public static int[] yDirection;
    public static int directionCounter;

    public UndoList() {
        stepList = new ArrayList<>(0);
        initialDirection();
        tmp = new int[8];
    }

    private void initialDirection() {
        xDirection = new int[8];
        yDirection = new int[8];
        directionCounter = 0;
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if (i != 0 || j != 0) {
                    xDirection[directionCounter] = i;
                    yDirection[directionCounter] = j;
                    ++directionCounter;
                }
            }
        }//记录八个方向的xy偏移量
    }

    public void add(int dx, int dy, ChessPiece currentPlayer) {
        int[] tt = new int[8];
        System.arraycopy(tmp, 0, tt, 0, 8);
        stepList.add(new Step(dx, dy, currentPlayer, tt));
        tmp[0] = tmp[1] = tmp[2] = tmp[3] =
                tmp[4] = tmp[5] = tmp[6] = tmp[7] = 0;
        //System.out.printf("%s",stepList.get(stepList.size()-1).toString());
    }

    public int undo(ChessGridComponent[][] chessGrids) {
        if (stepList.size() == 0) return 0;
        Step u = stepList.remove(stepList.size() - 1);

        int t = 0;
        ChessPiece cur = (u.color == ChessPiece.BLACK ? ChessPiece.WHITE : ChessPiece.BLACK);
        //将棋子颜色翻转
        for (int T = 0; T < directionCounter; T++) {
            int dx = u.rowIndex + xDirection[T];
            int dy = u.columnIndex + yDirection[T];
            t += u.reserveNum[T];
            while (u.reserveNum[T] != 0) {
                u.reserveNum[T]--;
                chessGrids[dx][dy].setChessPiece(cur);
                dx += xDirection[T];
                dy += yDirection[T];
            }
        }
        chessGrids[u.rowIndex][u.columnIndex].setChessPiece(null);

        return t;
    }

    public ChessPiece getLastColor() {
        return stepList.get(stepList.size() - 1).color;
    }

    public void setReserveNum(int position, int number) {
        tmp[position] = number;
    }

    public void addReserveNum(int position, int number) {
        tmp[position] += number;
    }

    public void resetUndoList() {
        stepList.clear();
    }

    public boolean hasNext() {
        return stepList.size() != 0;
    }

    @Override
    public String toString() {
        int siz=stepList.size();
        String s;
        if(siz==0||stepList.get(stepList.size() - 1).color.getColor()==Color.WHITE)s="BLACK\r\n";else s="WHITE\r\n";
        s = String.format("%s%d \r\n",s, stepList.size());
        for (Step step : stepList) {
            s = String.format("%s%s", s, step.toString());
        }
        return s;
    }
}