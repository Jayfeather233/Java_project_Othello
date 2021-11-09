package components;

import model.*;
import view.GameFrame;

import javax.swing.*;
import java.awt.*;

public class ChessGridComponent extends BasicComponent {
    public static int chessSize;
    public static int gridSize;
    public static Color gridColor = new Color(255, 150, 50);

    private static int lastRow,lastCol;

    private ChessPiece chessPiece;
    private final int row;
    private final int col;

    public ChessGridComponent(int row, int col) {
        this.setSize(gridSize, gridSize);

        this.row = row;
        this.col = col;
        this.chessPiece=null;
    }

    /**
     * 鼠标点击判断
     */
    @Override
    public void onMouseClicked() {
        System.out.printf("%s clicked (%d, %d)\n", GameFrame.controller.getCurrentPlayer(), row, col);
        if (GameFrame.controller.canClick(row, col)) {
            if (this.chessPiece == null || this.chessPiece == ChessPiece.GRAY) {//合法落子
                lastCol=col;
                lastRow=row;
                int u=GameFrame.controller.getGamePanel().doMove(row,col,GameFrame.controller.getCurrentPlayer());//doMove
                GameFrame.controller.countScore(GameFrame.controller.getCurrentPlayer(),u);//countScore plus
                GameFrame.controller.jumpTime=0;
                GameFrame.controller.countScore(
                        GameFrame.controller.getCurrentPlayer()==ChessPiece.BLACK ?
                                ChessPiece.WHITE : ChessPiece.BLACK,-u+1);//countScore minus

                GameFrame.controller.swapPlayer();
                GameFrame.controller.getGamePanel().addUndo(row,col);//add Undo
                if(GameFrame.controller.getGamePanel().checkGray()){//没有灰色，跳过落子
                    JOptionPane.showMessageDialog(GameFrame.controller.getGamePanel(),
                            (GameFrame.controller.getCurrentPlayer()==ChessPiece.BLACK ? "BLACK" : "WHITE") +
                            " has nowhere to put! JumpThrough.");
                    GameFrame.controller.jumpThrough();
                    if(GameFrame.controller.getGamePanel().checkGray()) {//连续判断
                        JOptionPane.showMessageDialog(GameFrame.controller.getGamePanel(),
                                (GameFrame.controller.getCurrentPlayer() == ChessPiece.BLACK ? "BLACK" : "WHITE") +
                                        " has nowhere to put! JumpThrough.");
                    }
                }
                GameFrame.controller.getGamePanel().repaint();//重绘

                if(GameFrame.AIPiece==GameFrame.controller.getCurrentPlayer()){//如果开启AI就让AI跑下一步
                    GameFrame.controller.getGamePanel().AIPlay(GameFrame.AI_Level,GameFrame.AIPiece);// AIPlay
                }
            }
        }
    }


    public ChessPiece getChessPiece() {
        return chessPiece;
    }

    public void setChessPiece(ChessPiece chessPiece) {
        this.chessPiece = chessPiece;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void drawPiece(Graphics g) {
        g.setColor(gridColor);
        g.fillRect(1, 1, this.getWidth() - 2, this.getHeight() - 2);
        if (this.chessPiece != null) {
            g.setColor(chessPiece.getColor());
            g.fillOval((gridSize - chessSize) / 2, (gridSize - chessSize) / 2, chessSize, chessSize);
            if(col==lastCol&&row==lastRow) {//最后一子突出显示
                g.setColor(Color.RED);
                g.fillRect((gridSize - chessSize / 5) / 2, (gridSize - chessSize / 5) / 2, chessSize / 5, chessSize / 5);
            }
        }
    }


    @Override
    public void paintComponent(Graphics g) {
        super.printComponents(g);
        drawPiece(g);
    }


}
