package components;

import controller.AIThread;
import controller.GameController;
import controller.Time;
import model.ChessPiece;
import view.ChessBoardPanel;
import view.GameFrame;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ChessGridComponent extends BasicComponent {
    public static int isRepainting = 0;
    public static int chessSize;
    public static int gridSize;
    public static boolean AIOn;
    public static boolean netOn=false;

    private static int lastRow = -1, lastCol = -1;

    private ChessPiece chessPiece;
    private final int row;
    private final int col;
    private long lasTime;
    private int isFlip = 0;//1 flip

    public ChessGridComponent(int row, int col) {
        this.setSize(gridSize, gridSize);

        this.row = row;
        this.col = col;
        this.chessPiece = null;
    }

    public void resize(int size) {
        gridSize = size;
        this.setSize(gridSize, gridSize);
    }

    /**
     * 鼠标点击判断
     */
    @Override
    public void onMouseClicked() {
        //System.out.printf("%s clicked (%d, %d)", GameFrame.controller.getCurrentPlayer(), row, col);
        if (GameFrame.controller.canClick(row, col) && !AIOn && !(netOn && GameFrame.controller.getCurrentPlayer()==GameFrame.AIPiece)) {
            if (this.chessPiece == null || this.chessPiece == ChessPiece.GRAY) {//合法落子
                lastCol = col;
                lastRow = row;
                GameController controller = GameFrame.controller;
                ChessBoardPanel panel = GameFrame.controller.getGamePanel();


                int u = panel.doMove(row, col, controller.getCurrentPlayer(), null);//doMove

                controller.countScore(controller.getCurrentPlayer(), u);//countScore plus
                controller.jumpTime = 0;
                controller.countScore(
                        controller.getCurrentPlayer() == ChessPiece.BLACK ?
                                ChessPiece.WHITE : ChessPiece.BLACK, -u + 1);//countScore minus

                panel.addUndo(row, col, controller.getCurrentPlayer());//add Undo

                controller.swapPlayer();
                panel.repaint();//重绘

                if(netOn) GameFrame.send("P "+col+' '+row);


                if (panel.checkGray()) {//没有灰色，跳过落子
                    panel.doJump();
                }

                if (GameFrame.AIPiece == controller.getCurrentPlayer()) {//如果开启AI就让AI跑下一步
                    AIOn = true;
                    Thread a = new Thread(new AIThread(GameFrame.AI_Level, GameFrame.AIPiece));
                    a.start();//Run AI in thread
                }
                GameFrame.setUndoEnabled(true);
            } else
                System.out.println("Illegal in 2");
        } else {
            System.out.printf("Illegal in 1,AIOn=%d\n", AIOn ? 1 : 0);
        }
    }


    public ChessPiece getChessPiece() {
        return chessPiece;
    }

    /**
     * 棋子状态改变，动画开始
     */
    public void setChessPiece(ChessPiece chessPiece) {
        if (chessPiece != ChessPiece.GRAY) {
            if (this.chessPiece != null && this.chessPiece != ChessPiece.GRAY) isFlip = 1;
            else isFlip = 2;
            lasTime = Time.getTime();
        } else isFlip = 0;
        this.chessPiece = chessPiece;
    }

    public static void setLast(int r, int c) {
        lastCol = c;
        lastRow = r;
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

            long u = Time.getTime() - lasTime;
            long lasT = 250;
            if (u > lasT) {
                isFlip = 0;
                u = lasT;
            }

            /*
            这里处理动画
             */
            if (isFlip == 1 && GameFrame.animation) {
                if (u < lasT / 2) {

                    int dx = (int) (8 * u * 2.0 / lasT + 0.5);
                    int cx = (int) (chessSize * (1 - u * 2.0 / lasT) + 0.5) + dx * 2;
                    int cy = (int) (chessSize + u * 30 / lasT);

                    BufferedImage bi = (BufferedImage) GameFrame.getImage(this.chessPiece);
                    if (u != 0)
                        bi = bi.getSubimage(0, 0, (int) (bi.getWidth() * (u * 1.0 / lasT) + 0.5), bi.getHeight());
                    else bi = null;

                    int cx2 = (int) (cx * (u * 1.0 / lasT) + 0.5);

                    g.drawImage(GameFrame.getImage(this.chessPiece == ChessPiece.WHITE ? ChessPiece.BLACK : ChessPiece.WHITE),
                            (gridSize - cx) / 2, (gridSize - cy) / 2,
                            cx, cy, null);
                    g.drawImage(bi,
                            (gridSize - cx) / 2, (gridSize - cy) / 2,
                            cx2, cy, null);

                } else {

                    int dx = (int) (8 * (lasT - u) * 2.0 / lasT + 0.5);
                    int cx = (int) (chessSize * (u * 2.0 / lasT - 1) + 0.5) + dx * 2;
                    int cy = (int) (chessSize + (lasT - u) * 30 / lasT);

                    BufferedImage bi = (BufferedImage) GameFrame.getImage(this.chessPiece == ChessPiece.WHITE ? ChessPiece.BLACK : ChessPiece.WHITE);
                    if (u < lasT) bi = bi.getSubimage((int) (bi.getWidth() * (u * 1.0 / lasT) + 0.5), 0,
                            (int) (bi.getWidth() * (1 - u * 1.0 / lasT) + 0.5), bi.getHeight());
                    else bi = null;

                    int cx2 = (int) (cx * (1 - u * 1.0 / lasT) + 0.5);

                    g.drawImage(GameFrame.getImage(this.chessPiece),
                            (gridSize - cx) / 2, (gridSize - cy) / 2,
                            cx, cy, null);
                    g.drawImage(bi,
                            (gridSize + cx) / 2 - cx2, (gridSize - cy) / 2,
                            cx2, cy, null);
                }
            } else if (isFlip != 0 && GameFrame.animation) {
                g.drawImage(GameFrame.getImage(this.chessPiece),
                        (int) (gridSize - chessSize - (lasT - u) * 15 / lasT) / 2, (int) (gridSize - chessSize - (lasT - u) * 15 / lasT) / 2,
                        (int) (chessSize + (lasT - u) * 15 / lasT), (int) (chessSize + (lasT - u) * 15 / lasT), null);
            } else {
                g.drawImage(GameFrame.getImage(this.chessPiece),
                        (gridSize - chessSize) / 2, (gridSize - chessSize) / 2,
                        chessSize, chessSize, null);
            }

            if (col == lastCol && row == lastRow) {//最后一子突出显示
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
