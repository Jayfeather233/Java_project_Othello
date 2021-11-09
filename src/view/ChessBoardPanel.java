package view;

import components.ChessGridComponent;
import model.ChessPiece;

import javax.swing.*;
import java.awt.*;

public class ChessBoardPanel extends JPanel {
    public static final int CHESS_COUNT = 8;
    private ChessGridComponent[][] chessGrids;
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
        undoList=new int[100][10];
        ChessGridComponent.gridSize = length / CHESS_COUNT;
        ChessGridComponent.chessSize = (int) (ChessGridComponent.gridSize * 0.8);
        System.out.printf("width = %d height = %d gridSize = %d chessSize = %d\n",
                width, height, ChessGridComponent.gridSize, ChessGridComponent.chessSize);

        initialChessGrids();//return empty chessboard
        initialGame();//add initial four chess
        initialDirection();//initial Direction

        repaint();
    }

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

    public boolean canClickGrid(int row, int col, ChessPiece currentPlayer) {
        return GameFrame.cheat||chessGrids[row][col].getChessPiece()==ChessPiece.GRAY;
        //return true;
    }
    public void addUndo(int x,int y){
        undoList[undoLength][0]=x;
        undoList[undoLength][1]=y;
        System.out.printf("(%d,%d)(",undoList[undoLength][0],undoList[undoLength][1]);
        for(int i=0;i<8;i++) System.out.printf("%d,",undoList[undoLength][i+2]);
        System.out.println(")");
        ++undoLength;
    }

    public void checkPlaceable(ChessPiece currentPlayer) {
        for (int i = 0; i < CHESS_COUNT; i++) {
            for (int j = 0; j < CHESS_COUNT; j++) {
                if (chessGrids[i][j].getChessPiece() == ChessPiece.GRAY)
                    chessGrids[i][j].setChessPiece(null);
            }
        }
        for (int i = 0; i < CHESS_COUNT; i++) {
            for (int j = 0; j < CHESS_COUNT; j++) {
                if(chessGrids[i][j].getChessPiece()==currentPlayer) {
                    findPut(i,j,currentPlayer);
                }
            }
        }

        repaint();
    }

    private boolean checkBounder(int row ,int col){
        return (0<=row&&row<CHESS_COUNT)&&(0<=col&&col<CHESS_COUNT);
    }

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

    private void findPut(int row, int col, ChessPiece currentPlayer) {
        for(int T=0;T<directionCounter;T++){
            int dx=row+xDirection[T];
            int dy=col+yDirection[T];
            canPut(dx,dy,T,currentPlayer,true);
        }
    }

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
        return t+1;
    }

    public void AIPlay(int level, ChessPiece currentPlayer) {
        int nowPoints=-2147483647,ni=-1,nj=-1;

        ChessPiece[][] bfc=new ChessPiece[CHESS_COUNT][CHESS_COUNT];
        eraseGray(currentPlayer);
        for (int i = 0; i < CHESS_COUNT; i++) {
            for (int j = 0; j < CHESS_COUNT; j++) {
                bfc[i][j]=chessGrids[i][j].getChessPiece();
            }
        }
        for (int i = 0; i < CHESS_COUNT; i++) {
            for (int j = 0; j < CHESS_COUNT; j++) {
                if(chessGrids[i][j].getChessPiece()==ChessPiece.GRAY) {
                    int tmp = think(i, j, level, currentPlayer);
                    for (int i2 = 0; i2 < CHESS_COUNT; i2++) {
                        for (int j2 = 0; j2 < CHESS_COUNT; j2++) {
                            chessGrids[i2][j2].setChessPiece(bfc[i2][j2]);
                        }
                    }
                    if (nowPoints<tmp){
                        nowPoints=tmp;
                        ni=i;
                        nj=j;
                    }
                }
            }
        }
        if(ni==-1) GameFrame.controller.jumpThrough();
        else{
            chessGrids[ni][nj].onMouseClicked();
        }
    }

    private void eraseGray(ChessPiece currentPlayer) {
        for (int i = 0; i < CHESS_COUNT; i++) {
            for (int j = 0; j < CHESS_COUNT; j++) {
                if (chessGrids[i][j].getChessPiece() == ChessPiece.GRAY)
                    chessGrids[i][j].setChessPiece(null);
            }
        }
        checkPlaceable(currentPlayer);
    }

    private int think(int dx, int dy, int level, ChessPiece currentPlayer) {
        //System.out.printf("Thinking... %d\n",level);
        if(level<=0) return 0;

        int dif=doMove(dx,dy,currentPlayer);
        currentPlayer=(currentPlayer==ChessPiece.BLACK ? ChessPiece.WHITE : ChessPiece.BLACK);
        eraseGray(currentPlayer);
        ChessPiece[][] bfc=new ChessPiece[CHESS_COUNT][CHESS_COUNT];
        for (int i = 0; i < CHESS_COUNT; i++) {
            for (int j = 0; j < CHESS_COUNT; j++) {
                bfc[i][j]=chessGrids[i][j].getChessPiece();
            }
        }
        int s=2147483647;
        for (int i = 0; i < CHESS_COUNT; i++) {
            for (int j = 0; j < CHESS_COUNT; j++) {
                if (chessGrids[i][j].getChessPiece() == ChessPiece.GRAY) {
                    s=Math.min(s,think(i,j,level-1,currentPlayer));
                    for (int i2 = 0; i2 < CHESS_COUNT; i2++) {
                        for (int j2 = 0; j2 < CHESS_COUNT; j2++) {
                            chessGrids[i2][j2].setChessPiece(bfc[i2][j2]);
                        }
                    }
                }
            }
        }

        if(s!=2147483647) dif-=s;

        return dif;
    }

    public boolean checkGray() {
        for (int i = 0; i < CHESS_COUNT; i++) {
            for (int j = 0; j < CHESS_COUNT; j++) {
                if (chessGrids[i][j].getChessPiece() == ChessPiece.GRAY) return false;
            }
        }
        return true;
    }
}
