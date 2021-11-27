package controller;

import components.ChessGridComponent;
import model.ChessPiece;
import model.Step;
import model.UndoList;
import view.ChessBoardPanel;
import view.GameFrame;

import static view.ChessBoardPanel.CHESS_COUNT;

public class AI {
    private static final int INF = 2147483647;
    private static ChessGridComponent[][] chessGrids;
    private static ChessBoardPanel panel;
    private static int[][] panelScore;//棋盘上的权值
    private static Step[][][] history;
    public static int dx,dy;

    public static void setPanel(ChessBoardPanel panel) {
        chessGrids=new ChessGridComponent[8][8];
        for(int i=0;i<8;i++) {
            for (int j = 0; j < 8; j++) {
                chessGrids[i][j]=new ChessGridComponent(i,j);
            }
        }
        AI.panel = panel;
    }
    public static void setPanelScore(int[][] ps){
        panelScore=ps;
    }
    public static void advantageMove(int u,int depth,int index){
        if(index==0) return;
        Step v=history[u][depth][index];
        history[u][depth][index]=history[u][depth][index-1];
        history[u][depth][index-1]=v;
    }
    private static void floatMove(int u,int depth,int index){
        Step s=history[u][depth][index];
        System.arraycopy(history[u][depth], 0, history[u][depth], 1, index - 1 + 1);
        history[u][depth][0]=s;
    }

    public static void initScore() {
        panelScore = new int[9][9];
        history=new Step[2][130][64];
        for (int i = 0; i < 130; i++) {
            for (int j = 0; j < 64; j++) {
                history[0][i][j] = new Step(j / 8, j % 8, ChessPiece.BLACK, null);
                history[1][i][j] = new Step(j / 8, j % 8, ChessPiece.BLACK, null);
            }
        }

        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                if(i*(7-i)==0&&j*(7-j)==0)//在角
                    panelScore[i][j]=64;
                else if((i<=1||i>=6)&&(j<=1||j>=6))//在角四周
                    panelScore[i][j]=1;
                else if((i-1)*(j-1)*(6-i)*(6-j)==0)//在第二行
                    panelScore[i][j]=5;
                else if(i*j*(7-i)*(7-j)==0)//在边
                    panelScore[i][j]=8;
                else//在中间
                    panelScore[i][j]=3;
            }
        }
    }

    /**
     * AI调用入口，函数结束会调用某个格子的onMouseClicked来模拟点击
     * AI实现方法为最小最大博弈，加alpha-beta剪枝和历史表
     *
     */
    static int jp;
    public static void AIPlay(int level, ChessPiece currentPlayer) {
        for(int i=0;i<8;i++) {
            for(int j=0;j<8;j++) {
                chessGrids[i][j].setChessPiece(panel.getChessGrids()[i][j].getChessPiece());
            }
        }
        int u = 0;
        for (int i = 0; i < CHESS_COUNT; i++) {
            for (int j = 0; j < CHESS_COUNT; j++) {
                if (chessGrids[i][j].getChessPiece() != ChessPiece.GRAY && chessGrids[i][j].getChessPiece() != null) {
                    u++;
                }
            }
        }
        dx=dy=-1;
        jp=0;
        int tmp = -(50 < u
                    ? think(u, 84 - u, currentPlayer, false, false, currentPlayer,-INF,INF)
                    : think(u, level, currentPlayer, true, true, currentPlayer,-INF,INF));

        ChessGridComponent.AIOn = false;
        //System.out.println("AIOn=false\n");
        if (dx == -1) {
            panel.doJump();
        } else {
            System.out.printf("AI play at %d,%d\n", dx, dy);
            panel.getChessGrids()[dx][dy].onMouseClicked();
        }

    }

    /**
     * 这里就是递归搜索
     */
    private static int think(int depth, int level, ChessPiece currentPlayer, boolean enableScore, boolean enableMove, ChessPiece AIPiece,int alpha,int beta) {

        int max=-2147483647;
        if (level == 0){
            return evaluateBoard(AIPiece, currentPlayer, enableScore, enableMove);
        }
        int nn = currentPlayer == ChessPiece.BLACK ? 1 : 0, v;
        int nx=-1,ny = -1;
        ChessPiece nCur = currentPlayer == ChessPiece.BLACK ? ChessPiece.WHITE : ChessPiece.BLACK;
        for (int T = 0; T < 64; T++) {
            Step s = history[nn][depth][T];
            int i = s.rowIndex, j = s.columnIndex;
            if (checkPlaceable(i,j,currentPlayer)) {
                panel.doMove(i,j,currentPlayer,chessGrids);
                panel.getUndoList().add(i, j, currentPlayer);

                v=-think(depth+1,level-1,nCur,enableScore,enableMove,AIPiece,-beta,-alpha);

                panel.getUndoList().undo(chessGrids);
                jp=0;

                if(v>alpha){
                    if(v>beta){
                        dx=i;dy=j;
                        floatMove(nn,depth,T);
                        return v;
                    }
                    advantageMove(nn,depth,T);
                    alpha=v;
                }
                if(max<v){
                    nx=i;ny=j;
                    max=v;
                }
            }
        }
        if(max==-2147483647){
            jp++;
            if(jp==2) return evaluateBoard(AIPiece, currentPlayer, enableScore, enableMove);
            else max=-think(depth+1,level,nCur,enableScore,enableMove,AIPiece,-beta,-alpha);
        }
        dx=nx;dy=ny;
        return max;
    }

    private static boolean checkPlaceable(int i, int j, ChessPiece currentPlayer) {
        if(chessGrids[i][j].getChessPiece()==ChessPiece.BLACK || chessGrids[i][j].getChessPiece() == ChessPiece.WHITE) return false;
        if(GameFrame.AICheat) return true;
        for(int T=0;T<8;T++){
            if(panel.canPut(i+ UndoList.xDirection[T],j+ UndoList.yDirection[T],T,currentPlayer,false,chessGrids)) return true;
        }
        return false;
    }

    private static int evaluateBoard(ChessPiece AIPiece, ChessPiece currentPlayer, boolean enableScore, boolean enableMove) {
        int dif = 0;

        panel.checkPlaceable(currentPlayer,chessGrids);
        for (int i = 0; i < CHESS_COUNT; i++) {
            for (int j = 0; j < CHESS_COUNT; j++) {
                if (chessGrids[i][j].getChessPiece() == AIPiece) {
                    if (enableScore) dif += panelScore[i][j];
                    else dif++;
                } else if (chessGrids[i][j].getChessPiece() == (AIPiece == ChessPiece.BLACK ? ChessPiece.WHITE : ChessPiece.BLACK)) {
                    if (enableScore) dif -= panelScore[i][j];
                    else dif--;
                }
            }
        }
        if (enableMove) {
            if (AIPiece == currentPlayer) {
                dif += panel.countGray() * 5;
                panel.checkPlaceable(AIPiece == ChessPiece.BLACK ? ChessPiece.WHITE : ChessPiece.BLACK,chessGrids);
                dif -= panel.countGray() * 5;
            } else {
                dif -= panel.countGray() * 5;
                panel.checkPlaceable(AIPiece,chessGrids);
                dif += panel.countGray() * 5;
            }
        }
        return dif*(AIPiece==currentPlayer ? 1 : -1);
    }

    /**
     * 输出棋盘状态
     */
    private static void opt(ChessGridComponent[][] chessGrids) {
        for (int i = 0; i < CHESS_COUNT; i++) {
            for (int j = 0; j < CHESS_COUNT; j++) {
                if(chessGrids[i][j].getChessPiece()== ChessPiece.GRAY){
                    System.out.print("G");
                }else if(chessGrids[i][j].getChessPiece()== ChessPiece.BLACK){
                    System.out.print("B");
                }else if(chessGrids[i][j].getChessPiece()== ChessPiece.WHITE) {
                    System.out.print("W");
                }else System.out.print(" ");
            }
            System.out.print('\n');
        }
    }
}
