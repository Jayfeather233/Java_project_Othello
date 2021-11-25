package view;

import components.ChessGridComponent;
import controller.AI;
import controller.GameController;
import controller.Trainer;
import model.ChessPiece;
import model.UndoList;

import javax.swing.*;
import java.awt.*;

public class ChessBoardPanel extends JPanel {
    public static final int CHESS_COUNT = 8;
    private ChessGridComponent[][] chessGrids;
    UndoList undoList;

    public ChessBoardPanel(int width, int height) {
        this.setVisible(true);
        this.setFocusable(true);
        this.setLayout(null);
        this.setBackground(Color.BLACK);
        int length = Math.min(width, height);
        this.setSize(length, length);
        ChessGridComponent.gridSize = length / CHESS_COUNT;
        ChessGridComponent.chessSize = (int) (ChessGridComponent.gridSize * 0.8);
        System.out.printf("width = %d height = %d gridSize = %d chessSize = %d\n",
                width, height, ChessGridComponent.gridSize, ChessGridComponent.chessSize);

        initialChessGrids();//return empty chessboard
        initialGame();//add initial four chess
        undoList=new UndoList();
        AI.setPanel(this);
        AI.initScore();

        repaint();
    }

    /**
     * set an empty chessboard
     */
    public void initialChessGrids() {
        chessGrids = new ChessGridComponent[CHESS_COUNT][CHESS_COUNT];

        //draw all chess grids
        for (int i = 0; i < CHESS_COUNT; i++) {
            for (int j = 0; j < CHESS_COUNT; j++) {
                ChessGridComponent gridComponent = new ChessGridComponent(i, j);
                gridComponent.setLocation(j * ChessGridComponent.gridSize, i * ChessGridComponent.gridSize);
                chessGrids[i][j] = gridComponent;
                this.add(chessGrids[i][j]);
            }
        }
    }

    /**
     * initial origin four chess
     */
    public void initialGame() {
        System.out.println("initialing...");
        for (int i = 0; i < CHESS_COUNT; i++) {
            for (int j = 0; j < CHESS_COUNT; j++) {
                chessGrids[i][j].setChessPiece(null);
            }
        }
        ChessGridComponent.setLast(-1,-1);
        chessGrids[3][3].setChessPiece(ChessPiece.BLACK);
        chessGrids[3][4].setChessPiece(ChessPiece.WHITE);
        chessGrids[4][3].setChessPiece(ChessPiece.WHITE);
        chessGrids[4][4].setChessPiece(ChessPiece.BLACK);

        repaint();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(GameFrame.getPanelImage(),0, 0, this.getWidth(), this.getHeight(),null);
    }

    /**
     * 是否可放一子。是灰色就可以
     */
    public boolean canClickGrid(int row, int col) {
        return GameFrame.cheat||chessGrids[row][col].getChessPiece()==ChessPiece.GRAY;
        //return true;
    }

    /**
     * 用灰色列举出当前玩家能放的位置
     * @param currentPlayer 当前玩家颜色
     */
    public void checkPlaceable(ChessPiece currentPlayer) {
        for (int i = 0; i < CHESS_COUNT; i++) {//首先清除棋盘上灰色，因为我们要重新生成它
            for (int j = 0; j < CHESS_COUNT; j++) {
                if (chessGrids[i][j].getChessPiece() == ChessPiece.GRAY)
                    chessGrids[i][j].setChessPiece(null);
            }
        }
        for (int i = 0; i < CHESS_COUNT; i++) {//对每一个有棋子的点从它向8个方向扩展找哪里可放
            for (int j = 0; j < CHESS_COUNT; j++) {
                if(chessGrids[i][j].getChessPiece()==currentPlayer) {
                    findPut(i,j,currentPlayer);
                }
            }
        }
    }

    /**
     * 对某个坐标检测是否超出边界
     */
    private boolean checkBounder(int row ,int col){
        return (0<=row&&row<CHESS_COUNT)&&(0<=col&&col<CHESS_COUNT);
    }

    /**
     * 在坐标dx,dy的方向T上检测这个地方能不能放  T是之前initialDirection中的
     * @param ckOnly 如果为真是从一个放了棋的地方反推空地能不能放，并标记灰色
     *               如果为假是判断这个空地能不能放
     */
    public boolean canPut(int dx, int dy, int T, ChessPiece currentPlayer, boolean ckOnly) {
        int cnt=0;
        while(checkBounder(dx,dy)){
            if(chessGrids[dx][dy].getChessPiece()==null||chessGrids[dx][dy].getChessPiece()==ChessPiece.GRAY){
                if(cnt==0){
                    break;
                }
                if(ckOnly) {
                    chessGrids[dx][dy].setChessPiece(ChessPiece.GRAY);
                    return true;
                }else return false;
            }else if(chessGrids[dx][dy].getChessPiece()==currentPlayer){
                if(ckOnly) break;
                else{
                    return cnt != 0;
                }
            }else{
                cnt++;
            }
            dx=dx+UndoList.xDirection[T];
            dy=dy+UndoList.yDirection[T];
        }
        return false;
    }

    /**
     * 从checkPlaceable里面分出来的，表示找某个特定点8方向上哪里能放
     */
    private void findPut(int row, int col, ChessPiece currentPlayer) {
        for(int T=0;T<UndoList.directionCounter;T++){
            int dx=row+UndoList.xDirection[T];
            int dy=col+UndoList.yDirection[T];
            canPut(dx,dy,T,currentPlayer,true);
        }
    }

    /**
     * 真正下子下去
     * @return 下这一步共增加几个子
     */
    public int doMove(int row, int col, ChessPiece currentPlayer) {
        int t=0;
        for(int T=0;T<UndoList.directionCounter;T++){
            int dx=row+UndoList.xDirection[T];
            int dy=col+UndoList.yDirection[T];
            undoList.setReserveNum(T,0);
            if(canPut(dx,dy,T,currentPlayer,false)){
                while(chessGrids[dx][dy].getChessPiece()!=currentPlayer){
                    chessGrids[dx][dy].setChessPiece(currentPlayer);
                    t++;
                    undoList.addReserveNum(T,1);
                    dx+=UndoList.xDirection[T];
                    dy+=UndoList.yDirection[T];
                }
            }
        }
        chessGrids[row][col].setChessPiece(currentPlayer);
        return t+1;
    }


    public void doJump() {
        if(!Trainer.on)
        JOptionPane.showMessageDialog(GameFrame.controller.getGamePanel(),
                (GameFrame.controller.getCurrentPlayer() == ChessPiece.BLACK ? "BLACK" : "WHITE") +
                        " has nowhere to put! JumpThrough.");
        GameFrame.controller.jumpThrough();
        if(GameFrame.controller.getGamePanel().checkGray()) {//连续判断
            if(!Trainer.on)
            JOptionPane.showMessageDialog(GameFrame.controller.getGamePanel(),
                    (GameFrame.controller.getCurrentPlayer() == ChessPiece.BLACK ? "BLACK" : "WHITE") +
                            " has nowhere to put! JumpThrough.");
            GameFrame.controller.jumpThrough();
        }
        checkPlaceable(GameFrame.controller.getCurrentPlayer());
        repaint();
    }


    public int countGray() {
        int u=0;
        for (int i = 0; i < CHESS_COUNT; i++) {
            for (int j = 0; j < CHESS_COUNT; j++) {
                if (chessGrids[i][j].getChessPiece() == ChessPiece.GRAY)
                    u++;
            }
        }
        return u;
    }

    /**
     * 判断是否存在灰色，作为一方跳过本回合的依据
     */
    public boolean checkGray() {
        for (int i = 0; i < CHESS_COUNT; i++) {
            for (int j = 0; j < CHESS_COUNT; j++) {
                if(GameFrame.cheat&&chessGrids[i][j].getChessPiece()==null) return false;
                if (chessGrids[i][j].getChessPiece() == ChessPiece.GRAY) return false;
            }
        }
        return true;
    }

    public UndoList getUndoList() {
        return undoList;
    }

    /**
     * 来一次撤回
     * @return 撤回棋子数
     */
    public int undo() {
        return undoList.undo(chessGrids);
    }

    public boolean hasNextUndo(){
        return undoList.hasNext();
    }

    public void addUndo(int row, int col, ChessPiece currentPlayer) {
        undoList.add(row,col,currentPlayer);
    }

    /**
     * 玩家调用的撤回。撤回并更新分数
     */
    public void doUndo() {
        ChessPiece cur;
        int u;
        GameController controller=GameFrame.controller;
        while(controller.getGamePanel().hasNextUndo()&&GameFrame.AIPiece==controller.getGamePanel().getUndoList().getLastColor()) {
            cur = GameFrame.AIPiece;
            undoWithScore(cur, controller);
        }
        cur=controller.getGamePanel().getUndoList().getLastColor();
        undoWithScore(cur, controller);
        controller.getGamePanel().repaint();
    }

    private void undoWithScore(ChessPiece cur, GameController controller) {
        int u;
        u = undo();
        controller.countScore(cur==ChessPiece.BLACK ? ChessPiece.WHITE : ChessPiece.BLACK,u);
        controller.countScore(cur,-u-1);
        if(cur!=controller.getCurrentPlayer())
            controller.swapPlayer();
        else
            controller.getGamePanel().checkPlaceable(controller.getCurrentPlayer());
        System.out.println(u);
    }

    public ChessGridComponent[][] getChessGrids() {
        return chessGrids;
    }


    public void flipX() {
        ChessPiece s;
        for(int i=0;i<8;i++){
            for(int j=0;j<4;j++){
                s=chessGrids[i][j].getChessPiece();
                chessGrids[i][j].setChessPiece(chessGrids[i][7-j].getChessPiece());
                chessGrids[i][7-j].setChessPiece(s);
            }
        }
        ChessGridComponent.setLast(ChessGridComponent.getLastRow(),7-ChessGridComponent.getLastCol());
        repaint();
    }

    public void flipY() {
        ChessPiece s;
        for(int i=0;i<4;i++){
            for(int j=0;j<8;j++){
                s=chessGrids[i][j].getChessPiece();
                chessGrids[i][j].setChessPiece(chessGrids[7-i][j].getChessPiece());
                chessGrids[7-i][j].setChessPiece(s);
            }
        }
        ChessGridComponent.setLast(7-ChessGridComponent.getLastRow(),ChessGridComponent.getLastCol());
        repaint();
    }

    public void reSize(int width, int height) {
        int length = Math.min(width, height);
        this.setSize(length, length);
        ChessGridComponent.gridSize = (int)((length+0.5) / CHESS_COUNT);
        ChessGridComponent.chessSize = (int) (ChessGridComponent.gridSize * 0.8);

        for(int i=0;i<CHESS_COUNT;i++){
            for(int j=0;j<CHESS_COUNT;j++){
                chessGrids[i][j].resize(ChessGridComponent.gridSize);
                chessGrids[i][j].setLocation(j * ChessGridComponent.gridSize, i * ChessGridComponent.gridSize);
            }
        }
    }
}
