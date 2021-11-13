package view;

import components.ChessGridComponent;
import model.ChessPiece;

import javax.swing.*;
import java.awt.*;

public class ChessBoardPanel extends JPanel {
    public static final int CHESS_COUNT = 8;
    private ChessGridComponent[][] chessGrids;
    private int[][] panelScore;//棋盘上的权值
    private int[][] undoList;
    private int undoLength=0;
    private int[] xDirection;
    private int[] yDirection;
    private int directionCounter;

    public ChessBoardPanel(int width, int height) {
        this.setVisible(true);
        this.setFocusable(true);
        this.setLayout(null);
        this.setBackground(Color.BLACK);
        int length = Math.min(width, height);
        this.setSize(length, length);
        undoList=new int[100][11];
        panelScore=new int[9][9];
        ChessGridComponent.gridSize = length / CHESS_COUNT;
        ChessGridComponent.chessSize = (int) (ChessGridComponent.gridSize * 0.8);
        System.out.printf("width = %d height = %d gridSize = %d chessSize = %d\n",
                width, height, ChessGridComponent.gridSize, ChessGridComponent.chessSize);

        initialChessGrids();//return empty chessboard
        initialGame();//add initial four chess
        initialDirection();//initial Direction

        repaint();
    }

    /**
     * 初始化方向和棋盘权值
     */
    private void initialDirection() {
        xDirection=new int[8];
        yDirection=new int[8];
        directionCounter=0;
        for(int i=-1;i<=1;++i){
            for(int j=-1;j<=1;++j){
                if(i!=0||j!=0){
                    xDirection[directionCounter]=i;
                    yDirection[directionCounter]=j;
                    ++directionCounter;
                }
            }
        }//记录八个方向的xy偏移量

        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                if(i*(7-i)==0&&j*(7-j)==0)//在角
                    panelScore[i][j]=6;
                else if((i<=1||i>=6)&&(j<=1||j>=6))//在角四周
                    panelScore[i][j]=2;
                else if((i-1)*(j-1)*(6-i)*(6-j)==0)//在第二行
                    panelScore[i][j]=3;
                else//在边和中间
                    panelScore[i][j]=5;
            }
        }
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
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
    }

    /**
     * 是否可放一子。是灰色就可以
     */
    public boolean canClickGrid(int row, int col, ChessPiece currentPlayer) {
        return GameFrame.cheat||chessGrids[row][col].getChessPiece()==ChessPiece.GRAY;
        //return true;
    }

    /**
     * 加入一个Undo
     * @param x 坐标x
     * @param y 坐标y
     */
    public void addUndo(int x,int y){
        undoList[undoLength][0]=x;
        undoList[undoLength][1]=y;
        ++undoLength;
    }

    /**
     * 来一次撤回
     * @return 撤回了多少子
     */
    public int doUndo(){
        if(undoLength<=0) return 0;
        undoLength--;
        ChessPiece cur=chessGrids[undoList[undoLength][0]][undoList[undoLength][1]].getChessPiece();
        int t=0;
        cur=(cur==ChessPiece.BLACK ? ChessPiece.WHITE : ChessPiece.BLACK);
        for(int T=0;T<directionCounter;T++){//将棋子颜色翻转
            int dx=undoList[undoLength][0]+xDirection[T];
            int dy=undoList[undoLength][1]+yDirection[T];
            t+=undoList[undoLength][T+2];
            while(undoList[undoLength][T+2]!=0){
                undoList[undoLength][T+2]--;
                chessGrids[dx][dy].setChessPiece(cur);
                dx+=xDirection[T];
                dy+=yDirection[T];
            }
        }
        chessGrids[undoList[undoLength][0]][undoList[undoLength][1]].setChessPiece(null);

        if(undoLength>0) ChessGridComponent.setLast(undoList[undoLength-1][0],undoList[undoLength-1][1]);//设置上一个落子点（红）
        else ChessGridComponent.setLast(0,0);

        return t;
    }

    /**
     * @return 上一个子是谁下的
     */
    public int getLastUndo(){
        return undoList[undoLength-1][10];
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
    private boolean canPut(int dx, int dy, int T, ChessPiece currentPlayer, boolean ckOnly) {
        int cnt=0;
        while(checkBounder(dx,dy)){
            if(chessGrids[dx][dy].getChessPiece()==null||chessGrids[dx][dy].getChessPiece()==ChessPiece.GRAY){
                if(cnt==0){
                    break;
                }
                if(ckOnly) {
                    chessGrids[dx][dy].setChessPiece(ChessPiece.GRAY);
                    return true;
                }
                else return false;
            }else if(chessGrids[dx][dy].getChessPiece()==currentPlayer){
                if(ckOnly) break;
                else return true;
            }else{
                cnt++;
            }
            dx=dx+xDirection[T];
            dy=dy+yDirection[T];
        }
        return false;
    }

    /**
     * 从checkPlaceable里面分出来的，表示找某个特定点8方向上哪里能放
     */
    private void findPut(int row, int col, ChessPiece currentPlayer) {
        for(int T=0;T<directionCounter;T++){
            int dx=row+xDirection[T];
            int dy=col+yDirection[T];
            canPut(dx,dy,T,currentPlayer,true);
        }
    }

    /**
     * 真正下子下去
     * @return 下这一步共增加几个子
     */
    public int doMove(int row, int col, ChessPiece currentPlayer) {
        int t=0;
        for(int T=0;T<directionCounter;T++){
            int dx=row+xDirection[T];
            int dy=col+yDirection[T];
            undoList[undoLength][T+2]=0;
            if(canPut(dx,dy,T,currentPlayer,false)){
                while(chessGrids[dx][dy].getChessPiece()!=currentPlayer){
                    chessGrids[dx][dy].setChessPiece(currentPlayer);
                    t++;
                    undoList[undoLength][T+2]++;
                    dx+=xDirection[T];
                    dy+=yDirection[T];
                }
            }
        }
        chessGrids[row][col].setChessPiece(currentPlayer);
        undoList[undoLength][10]=currentPlayer==ChessPiece.BLACK ? 1 : 0;
        return t+1;
    }

    /**
     * AI调用入口，函数结束会调用某个格子的onMouseClicked来模拟点击
     * AI思路：遍历所有能下的点，假如我下这里，那么搜索下一个玩家继续移动最优的值，找到这个值的最小，就是我们移动的地方
     * 值：指与对家的得分之差
     * 最优：指机器认为最优，也就是与对家得分之差最大
     *
     * 这个方法的问题在于，因为搜索本身限制，一次递归需遍历64个点，最多时进入10个递归
     * 搜索n次则复杂度 O(64*10^n)
     * 所以只能搜索6~8步，也就3 4回合，
     * 因为子如果到边界或者被围起来，就不会被吃掉，而在开局时搜索不到这一点
     * 并且它是以增加的子为权重，所以不会将子往边界走
     * 所以只要玩家将自己的子往边界引就能赢
     *
     * 现在解决方案是给棋盘权重
     * 它变强了
     *
     * @param level AI等级，也就是搜索深度
     */
    public void AIPlay(int level, ChessPiece currentPlayer) {
        int nowPoints=-2147483647,ni=-1,nj=-1;

        checkPlaceable(currentPlayer);
        int t=0,u=0;
        for (int i = 0; i < CHESS_COUNT; i++) {
            for (int j = 0; j < CHESS_COUNT; j++) {
                if (chessGrids[i][j].getChessPiece() == ChessPiece.GRAY) {
                    t++;
                }else if(chessGrids[i][j].getChessPiece()!=null) {
                    u++;
                }
            }
        }
        for (int i = 0; i < CHESS_COUNT; i++) {
            for (int j = 0; j < CHESS_COUNT; j++) {
                if(chessGrids[i][j].getChessPiece()==ChessPiece.GRAY) {
                    int tmp = (u<7||(50<=u&&t<=5)||52<=u)
                            ? (56<u
                                ? think(i, j, 64-u, currentPlayer,false,currentPlayer)
                                : think(i, j, level+2, currentPlayer,true,currentPlayer))
                            : think(i, j, level, currentPlayer,true,currentPlayer);
                    if (nowPoints<tmp){
                        nowPoints=tmp;
                        ni=i;
                        nj=j;
                    }
                }
            }
        }
        ChessGridComponent.AIOn=false;
        if(ni==-1){
            doJump();
        }
        else{
            System.out.printf("AI play at %d,%d\n",ni,nj);
            chessGrids[ni][nj].onMouseClicked();
        }
    }

    public void doJump() {
        JOptionPane.showMessageDialog(GameFrame.controller.getGamePanel(),
                (GameFrame.controller.getCurrentPlayer() == ChessPiece.BLACK ? "BLACK" : "WHITE") +
                        " has nowhere to put! JumpThrough.");
        GameFrame.controller.jumpThrough();
        if(GameFrame.controller.getGamePanel().checkGray()) {//连续判断
            JOptionPane.showMessageDialog(GameFrame.controller.getGamePanel(),
                    (GameFrame.controller.getCurrentPlayer() == ChessPiece.BLACK ? "BLACK" : "WHITE") +
                            " has nowhere to put! JumpThrough.");
            GameFrame.controller.jumpThrough();
        }
    }

    /**
     * 这里就是递归搜索
     */
    private int think(int dx, int dy, int level, ChessPiece currentPlayer,boolean enableScore,ChessPiece AIPiece) {
        if(dx!=-1) {
            doMove(dx, dy, currentPlayer);
            addUndo(dx, dy);
        }
        if(level==1) {
            int dif=0;
            for (int i = 0; i < CHESS_COUNT; i++) {
                for (int j = 0; j < CHESS_COUNT; j++) {
                    if (chessGrids[i][j].getChessPiece() == AIPiece) {
                        if(enableScore) dif += panelScore[i][j];
                        else dif++;
                    }
                }
            }
            if(dx!=-1) doUndo();
            checkPlaceable(currentPlayer);
            return dif;
        }

        currentPlayer=(currentPlayer==ChessPiece.BLACK ? ChessPiece.WHITE : ChessPiece.BLACK);
        checkPlaceable(currentPlayer);
        int s=-1;
        for (int i = 0; i < CHESS_COUNT; i++) {
            for (int j = 0; j < CHESS_COUNT; j++) {
                if (chessGrids[i][j].getChessPiece() == ChessPiece.GRAY) {
                    if(AIPiece==currentPlayer) s=Math.max(s,think(i,j,level-1,currentPlayer,enableScore,AIPiece));
                    else s=Math.min(s,think(i,j,level-1,currentPlayer,enableScore,AIPiece));
                }
            }
        }
        if(s==-1) s=think(-1,-1,level-1,currentPlayer,enableScore,AIPiece);


        if(dx!=-1) doUndo();
        currentPlayer=(currentPlayer==ChessPiece.BLACK ? ChessPiece.WHITE : ChessPiece.BLACK);
        checkPlaceable(currentPlayer);

        return s;
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

    public void resetUndo() {
        undoLength=0;
    }

    public boolean hasNextUndo() {
        return undoLength>0;
    }
}
