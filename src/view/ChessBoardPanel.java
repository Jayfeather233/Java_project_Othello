package view;

import components.ChessGridComponent;
import model.ChessPiece;

import javax.swing.*;
import java.awt.*;

public class ChessBoardPanel extends JPanel {
    private final int CHESS_COUNT = 8;
    private ChessGridComponent[][] chessGrids;
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
        return chessGrids[row][col].getChessPiece()==ChessPiece.GRAY;
        //return true;
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
                }else{
                    if(ckOnly) chessGrids[dx][dy].setChessPiece(ChessPiece.GRAY);
                    else return false;
                    break;
                }
            }else if(chessGrids[dx][dy].getChessPiece()==currentPlayer){
                if(ckOnly) break;
                else return true;
            }else{
                cnt++;
            }
            dx=dx+xDirection[T];
            dy=dy+yDirection[T];
            System.out.printf("%d %d\n",dx,dy);
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

    public void doMove(int row, int col, ChessPiece currentPlayer) {
        for(int T=0;T<directionCounter;T++){
            int dx=row+xDirection[T];
            int dy=col+yDirection[T];
            if(canPut(dx,dy,T,currentPlayer,false)){
                while(chessGrids[dx][dy].getChessPiece()!=currentPlayer){
                    chessGrids[dx][dy].setChessPiece(currentPlayer);
                    dx+=xDirection[T];
                    dy+=yDirection[T];
                }
            }
        }
        chessGrids[row][col].setChessPiece(currentPlayer);
    }
}
