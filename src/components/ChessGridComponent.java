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
    private int row;
    private int col;

    public ChessGridComponent(int row, int col) {
        this.setSize(gridSize, gridSize);

        this.row = row;
        this.col = col;
        this.chessPiece=null;
    }

    @Override
    public void onMouseClicked() {
        System.out.printf("%s clicked (%d, %d)\n", GameFrame.controller.getCurrentPlayer(), row, col);
        if (GameFrame.controller.canClick(row, col)) {
            if (this.chessPiece == null || this.chessPiece == ChessPiece.GRAY) {
                lastCol=col;
                lastRow=row;
                System.out.println("OP");
                //this.chessPiece = GameFrame.controller.getCurrentPlayer();
                int u=GameFrame.controller.getGamePanel().doMove(row,col,GameFrame.controller.getCurrentPlayer());
                GameFrame.controller.countScore(GameFrame.controller.getCurrentPlayer(),u);
                GameFrame.controller.jumpTime=0;
                GameFrame.controller.countScore(GameFrame.controller.getCurrentPlayer()==ChessPiece.BLACK ? ChessPiece.WHITE : ChessPiece.BLACK,-u+1);
                GameFrame.controller.swapPlayer();
                GameFrame.controller.getGamePanel().addUndo(row,col);
                if(GameFrame.controller.getGamePanel().checkGray()){
                    JOptionPane.showMessageDialog(GameFrame.controller.getGamePanel(),
                            (GameFrame.controller.getCurrentPlayer()==ChessPiece.BLACK ? "BLACK" : "WHITE") +
                            " has nowhere to put! JumpThrough.");
                    GameFrame.controller.jumpThrough();
                    if(GameFrame.controller.getGamePanel().checkGray()) {
                        JOptionPane.showMessageDialog(GameFrame.controller.getGamePanel(),
                                (GameFrame.controller.getCurrentPlayer() == ChessPiece.BLACK ? "BLACK" : "WHITE") +
                                        " has nowhere to put! JumpThrough.");
                    }
                }
                GameFrame.controller.getGamePanel().repaint();

                if(GameFrame.AIPiece==GameFrame.controller.getCurrentPlayer()){
                    GameFrame.controller.getGamePanel().AIPlay(GameFrame.AI_Level,GameFrame.AIPiece);
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
            if(col==lastCol&&row==lastRow) {
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
