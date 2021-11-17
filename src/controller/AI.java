package controller;

import components.ChessGridComponent;
import model.ChessPiece;
import view.ChessBoardPanel;

import static view.ChessBoardPanel.CHESS_COUNT;

public class AI {
    private static ChessGridComponent[][] chessGrids;
    private static ChessBoardPanel panel;
    private static int[][] panelScore;//棋盘上的权值

    public static void setPanel(ChessBoardPanel panel) {
        AI.panel = panel;
        AI.chessGrids = panel.getChessGrids();
    }


    public static void initScore() {
        panelScore = new int[9][9];

        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                if(i*(7-i)==0&&j*(7-j)==0)//在角
                    panelScore[i][j]=32;
                else if((i<=1||i>=6)&&(j<=1||j>=6))//在角四周
                    panelScore[i][j]=0;
                else if((i-1)*(j-1)*(6-i)*(6-j)==0)//在第二行
                    panelScore[i][j]=4;
                else if(i*j*(7-i)*(7-j)==0)//在边
                    panelScore[i][j]=6;
                else//在中间
                    panelScore[i][j]=2;
            }
        }
    }

    /**
     * AI调用入口，函数结束会调用某个格子的onMouseClicked来模拟点击
     * AI思路：遍历所有能下的点，假如我下这里，那么搜索下一个玩家继续移动最优的值，找到这个值的最小，就是我们移动的地方
     * 值：指与对家的得分之差
     * 最优：指机器认为最优，也就是与对家得分之差最大
     * <p>
     * 这个方法的问题在于，因为搜索本身限制，一次递归需遍历64个点，最多时进入10个递归
     * 搜索n次则复杂度 O(64*10^n)
     * 所以只能搜索6~8步，也就3 4回合，
     * 搜索权值为棋盘权重
     *
     * @param level AI等级，也就是搜索深度
     */
    public static void AIPlay(int level, ChessPiece currentPlayer) {
        int nowPoints = -2147483647, ni = -1, nj = -1;

        panel.checkPlaceable(currentPlayer);
        ChessGridComponent[][] chessGrids = panel.getChessGrids();
        int t = 0, u = 0;
        for (int i = 0; i < CHESS_COUNT; i++) {
            for (int j = 0; j < CHESS_COUNT; j++) {
                if (chessGrids[i][j].getChessPiece() == ChessPiece.GRAY) {
                    t++;
                } else if (chessGrids[i][j].getChessPiece() != null) {
                    u++;
                }
            }
        }
        for (int i = 0; i < CHESS_COUNT; i++) {
            for (int j = 0; j < CHESS_COUNT; j++) {
                if (chessGrids[i][j].getChessPiece() == ChessPiece.GRAY) {
                    int tmp = (u < 7 || 50 <= u)
                            ? (56 < u
                            ? think(i, j, 64 - u, currentPlayer, false, currentPlayer)
                            : 52 < u
                            ? think(i, j, level + 2, currentPlayer, false, currentPlayer)
                            : think(i, j, level + 2, currentPlayer, true, currentPlayer))
                            : think(i, j, level, currentPlayer, true, currentPlayer);
                    if (nowPoints < tmp) {
                        nowPoints = tmp;
                        ni = i;
                        nj = j;
                    }
                }
            }
        }
        panel.checkPlaceable(currentPlayer);
        ChessGridComponent.AIOn = false;
        if (ni == -1) {
            panel.doJump();
        } else {
            System.out.printf("AI play at %d,%d\n", ni, nj);
            chessGrids[ni][nj].onMouseClicked();
        }
    }

    /**
     * 这里就是递归搜索
     */
    private static int think(int dx, int dy, int level, ChessPiece currentPlayer, boolean enableScore, ChessPiece AIPiece) {
        if (dx != -1) {
            panel.doMove(dx, dy, currentPlayer);
            panel.getUndoList().add(dx, dy, currentPlayer);
        }
        if (level == 1) {
            int dif = 0;
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
            if (enableScore) {
                if (AIPiece == currentPlayer) {
                    dif += panel.countGray() * 5;
                    panel.checkPlaceable(AIPiece == ChessPiece.BLACK ? ChessPiece.WHITE : ChessPiece.BLACK);
                    dif -= panel.countGray() * 5;
                } else {
                    dif -= panel.countGray() * 5;
                    panel.checkPlaceable(AIPiece);
                    dif += panel.countGray() * 5;
                }
            }
            panel.checkPlaceable(currentPlayer);
            if (dx != -1) panel.getUndoList().undo(chessGrids);
            return dif;
        }

        currentPlayer = (currentPlayer == ChessPiece.BLACK ? ChessPiece.WHITE : ChessPiece.BLACK);
        panel.checkPlaceable(currentPlayer);
        int s = -1;
        for (int i = 0; i < CHESS_COUNT; i++) {
            for (int j = 0; j < CHESS_COUNT; j++) {
                if (chessGrids[i][j].getChessPiece() == ChessPiece.GRAY) {
                    if (AIPiece == currentPlayer)
                        s = Math.max(s, think(i, j, level - 1, currentPlayer, enableScore, AIPiece));
                    else s = Math.min(s, think(i, j, level - 1, currentPlayer, enableScore, AIPiece));
                }
            }
        }
        if (s == -1) s = think(-1, -1, level - 1, currentPlayer, enableScore, AIPiece);


        if (dx != -1) panel.getUndoList().undo(chessGrids);
        currentPlayer = (currentPlayer == ChessPiece.BLACK ? ChessPiece.WHITE : ChessPiece.BLACK);
        panel.checkPlaceable(currentPlayer);

        return s;
    }
}
