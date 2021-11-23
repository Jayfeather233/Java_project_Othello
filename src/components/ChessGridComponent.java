package components;

import controller.GameController;
import model.*;
import controller.AIThread;
import view.ChessBoardPanel;
import view.GameFrame;

import java.awt.*;

public class ChessGridComponent extends BasicComponent {
    public static int isRepainting=0;
    public static int chessSize;
    public static int gridSize;
    public static Color gridColor = new Color(255, 150, 50);
    public static boolean AIOn;

    private static int lastRow=-1,lastCol=-1;

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
        System.out.printf("%s clicked (%d, %d)", GameFrame.controller.getCurrentPlayer(), row, col);
        if (GameFrame.controller.canClick(row, col) && !AIOn) {
            if (this.chessPiece == null || this.chessPiece == ChessPiece.GRAY) {//合法落子
                lastCol=col;
                lastRow=row;
                GameController controller=GameFrame.controller;
                ChessBoardPanel panel=GameFrame.controller.getGamePanel();


                int u=panel.doMove(row,col,controller.getCurrentPlayer());//doMove

                controller.countScore(controller.getCurrentPlayer(),u);//countScore plus
                controller.jumpTime=0;
                controller.countScore(
                        controller.getCurrentPlayer()==ChessPiece.BLACK ?
                                ChessPiece.WHITE : ChessPiece.BLACK,-u+1);//countScore minus

                panel.addUndo(row,col,controller.getCurrentPlayer());//add Undo

                controller.swapPlayer();
                panel.repaint();//重绘
                System.out.println("Repaint");


                if(panel.checkGray()){//没有灰色，跳过落子
                    panel.doJump();
                }

                if(GameFrame.AIPiece==controller.getCurrentPlayer()){//如果开启AI就让AI跑下一步
                    AIOn=true;
                    Thread a=new Thread(new AIThread(GameFrame.AI_Level, GameFrame.AIPiece));
                    a.start();//Run AI in thread
                }
                GameFrame.setUndoEnabled(true);
            }else
                System.out.println("Illegal in 2");
        }else
            System.out.printf("Illegal in 1,AIOn=%d\n",AIOn ? 1 : 0);
    }


    public ChessPiece getChessPiece() {
        return chessPiece;
    }

    public void setChessPiece(ChessPiece chessPiece) {
        this.chessPiece = chessPiece;
    }

    public static void setLast(int r,int c){
        lastCol=c;
        lastRow=r;
    }
    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public static int getLastCol() {
        return lastCol;
    }

    public static int getLastRow() {
        return lastRow;
    }

    public void drawPiece(Graphics g) {

        if (this.chessPiece != null) {

            g.drawImage(GameFrame.getImage(this.chessPiece),(gridSize - chessSize) / 2, (gridSize - chessSize) / 2, chessSize, chessSize,null);

            if(col==lastCol&&row==lastRow) {//最后一子突出显示
                g.setColor(Color.RED);
                g.fillRect((gridSize - chessSize / 5) / 2, (gridSize - chessSize / 5) / 2, chessSize / 5, chessSize / 5);
            }
        }
    }


    @Override
    public synchronized void paintComponent(Graphics g) {
        isRepainting++;
        super.printComponents(g);
        drawPiece(g);
        isRepainting--;
    }


}
