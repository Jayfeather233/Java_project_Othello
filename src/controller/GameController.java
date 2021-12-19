package controller;

import model.ChessPiece;
import view.ChessBoardPanel;
import view.GameFrame;
import view.StatusPanel;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class GameController {
    public static int whoWin = 0;//0:No 1:black 2:white 3:draw


    private ChessBoardPanel gamePanel;
    private StatusPanel statusPanel;
    private ChessPiece currentPlayer;
    private int blackScore;
    private int whiteScore;
    public int jumpTime = 0;

    public GameController(ChessBoardPanel gamePanel, StatusPanel statusPanel) {
        this.gamePanel = gamePanel;
        this.statusPanel = statusPanel;
        this.currentPlayer = ChessPiece.BLACK;
        blackScore = 2;
        whiteScore = 2;
    }

    /**
     * 重新开始中重设分数
     */
    public void resetScore() {
        this.currentPlayer = ChessPiece.BLACK;
        blackScore = 2;
        whiteScore = 2;
        statusPanel.setScoreText(blackScore, whiteScore);
    }

    /**
     * 交换黑白
     */
    public void swapPlayer() {
        currentPlayer = (currentPlayer == ChessPiece.BLACK) ? ChessPiece.WHITE : ChessPiece.BLACK;
        statusPanel.setScoreText(blackScore, whiteScore);
        statusPanel.repaint();
        gamePanel.checkPlaceable(currentPlayer, null);
    }


    /**
     * 加分
     *
     * @param cur 要加分的颜色
     * @param u   要加的分数，是负数就减
     */
    public void countScore(ChessPiece cur, int u) {
        if (cur == ChessPiece.BLACK) {
            blackScore += u;
        } else {
            whiteScore += u;
        }
    }


    public ChessPiece getCurrentPlayer() {
        return currentPlayer;
    }

    public ChessBoardPanel getGamePanel() {
        return gamePanel;
    }


    public void setGamePanel(ChessBoardPanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    private static int getNextInt(Scanner u){
        if(u.hasNextInt()) return u.nextInt();
        return -17456321;
    }

    public int readFileData(String fileName) {
        int size = fileName.length() - 1;
        if (size < 4) return 104;
        if (fileName.charAt(size) != 't' || fileName.charAt(size - 1) != 'x' || fileName.charAt(size - 2) != 't' || fileName.charAt(size - 3) != '.')
            return 104;
        fileName = "save\\" + fileName;
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader in = new BufferedReader(fileReader);
            Scanner S=new Scanner(in);
            int n = getNextInt(S), m = getNextInt(S);
            int[][] panel = new int[8][8];
            if (m == -1) return 106;
            if (n != 8 || m != 8) return 101;
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    panel[i][j] = getNextInt(S);
                    if (panel[i][j] < 0) return 106;
                    if (panel[i][j] > 2) return 102;
                    System.out.printf("%d ",panel[i][j]);
                }
                System.out.println("");
            }
            if(!S.hasNext()) return 106;
            String nowPlayer=S.next();
            if(!nowPlayer.equals("WHITE")&&!nowPlayer.equals("BLACK"))
                return 103;
            n = getNextInt(S);
            if (n < 0) return 106;
            int[] x = new int[n], y = new int[n], color = new int[n], cheat = new int[n];
            int[][] pael = new int[8][8];
            int[] dirx = new int[]{1, 1, 1, 0, -1, -1, -1, 0};
            int[] diry = new int[]{-1, 0, 1, 1, 1, 0, -1, -1};
            for (int i = 0; i < 8; i++) for (int j = 0; j < 8; j++) pael[i][j] = 0;
            pael[3][4] = pael[4][3] = 2;
            pael[3][3] = pael[4][4] = 1;
            for (int i = 0; i < n; i++) {
                x[i] = getNextInt(S);
                y[i] = getNextInt(S);
                color[i] = getNextInt(S);
                if (i == 0 && color[i] == 2) return 105;
                cheat[i] = getNextInt(S);
                if(x[i]==-17456321||y[i]==-17456321) return 106;
                if (x[i] > 7 || x[i] < 0 || y[i] > 7 || y[i] < 0)
                    return 105;
                if( color[i] > 2 || color[i] < 0 || cheat[i] > 1 || cheat[i] < 0 )
                    return 106;
                if (pael[x[i]][y[i]] > 0) return 105;
                int suc = 0;
                if (i > 0 && color[i] == color[i - 1]) {
                    for (int xx = 0; xx < 8; xx++)
                        for (int yy = 0; yy < 8; yy++)
                            for (int dir = 0; dir < 8; dir++) {
                                int xnow = xx + dirx[dir], ynow = yy + diry[dir], cntnow = 0;
                                while (xnow >= 0 && xnow < 8 && ynow >= 0 && ynow < 8) {
                                    if (pael[xnow][ynow] == 0) break;
                                    if (pael[xnow][ynow] == color[i]) {
                                        cntnow = 1;
                                        break;
                                    }
                                    xnow += dirx[dir];
                                    ynow += diry[dir];
                                }
                                if (cntnow == 1) {
                                    xnow = x[i] + dirx[dir];
                                    ynow = y[i] + diry[dir];
                                    while (pael[xnow][ynow] == 3 - color[i]) {
                                        suc = 1;
                                        xnow += dirx[dir];
                                        ynow += diry[dir];
                                    }
                                }
                            }
                }
                if (suc == 1 && cheat[i] == 0) return 105;
                for (int dir = 0; dir < 8; dir++) {
                    int xnow = x[i] + dirx[dir], ynow = y[i] + diry[dir], cntnow = 0;
                    while (xnow >= 0 && xnow < 8 && ynow >= 0 && ynow < 8) {
                        if (pael[xnow][ynow] == 0) break;
                        if (pael[xnow][ynow] == color[i]) {
                            cntnow = 1;
                            break;
                        }
                        xnow += dirx[dir];
                        ynow += diry[dir];
                    }
                    if (cntnow == 1) {
                        xnow = x[i] + dirx[dir];
                        ynow = y[i] + diry[dir];
                        while (pael[xnow][ynow] == 3 - color[i]) {
                            suc = 1;
                            pael[xnow][ynow] = color[i];
                            xnow += dirx[dir];
                            ynow += diry[dir];
                        }
                    }
                }
                if (suc == 0 && cheat[i] == 0) return 105;
                else pael[x[i]][y[i]] = color[i];
            }
            for (int xx = 0; xx < 8; xx++)
                for (int yy = 0; yy < 8; yy++) if (pael[xx][yy] != panel[xx][yy]) return 105;
            m = getNextInt(S);
            if (m > -1) return 106;
            gamePanel.initialGame();
            resetScore();
            boolean cheatnow=GameFrame.cheat;
            GameFrame.cheat=true;
            gamePanel.getUndoList().resetUndoList();
            gamePanel.checkPlaceable(currentPlayer, null);
            for (int i = 0; i < n; i++) gamePanel.getChessGrids()[x[i]][y[i]].onMouseClicked();
            GameFrame.cheat=cheatnow;
            JOptionPane.showMessageDialog(null, "Loaded successfully", "Hint", JOptionPane.PLAIN_MESSAGE);
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 106;
        }
    }

    public void writeDataToFile(String fileName) throws IOException {
        String name = fileName + ".txt";
        fileName = "save\\" + fileName + ".txt";
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            int n = JOptionPane.showConfirmDialog(null, String.format("%s already exists, overwrite it or not", name), "hint", JOptionPane.YES_NO_OPTION);
            if (n == 0) {
                FileOutputStream f1 = new FileOutputStream(fileName);
                byte[] byt = gamePanel.toString().getBytes(StandardCharsets.UTF_8);
                f1.write(byt);
                JOptionPane.showMessageDialog(null, "Saved successfully", "Hint", JOptionPane.PLAIN_MESSAGE);
                f1.close();
            }
        } catch (IOException e) {
            FileOutputStream f1 = new FileOutputStream(fileName);
            byte[] byt = gamePanel.toString().getBytes(StandardCharsets.UTF_8);
            f1.write(byt);
            JOptionPane.showMessageDialog(null, "Saved successfully", "Hint", JOptionPane.PLAIN_MESSAGE);
            f1.close();
        }
    }

    /**
     * 这个地能不能点
     */
    public boolean canClick(int row, int col) {
        return gamePanel.canClickGrid(row, col);
    }

    /**
     * 跳过一回合
     * 连续两回合则结束游戏
     */
    public void jumpThrough() {
        jumpTime++;
        swapPlayer();
        System.out.println("jump");
        if (jumpTime == 2) {
            endGame();
        }
    }

    public void endGame() {
        if (!Trainer.on)
            JOptionPane.showMessageDialog(gamePanel, (blackScore != whiteScore) ? ((blackScore > whiteScore ? "BLACK" : "WHITE") + " WINS!") : "DRAW!");

        whoWin = (blackScore != whiteScore) ? ((blackScore > whiteScore ? 1 : 2)) : 3;

        GameFrame.controller.getGamePanel().initialGame();
        GameFrame.controller.resetScore();
        GameFrame.controller.getGamePanel().repaint();
        GameFrame.controller.getGamePanel().getUndoList().resetUndoList();
        GameFrame.controller.getGamePanel().checkPlaceable(GameFrame.controller.getCurrentPlayer(), null);
        statusPanel.repaint();
    }
}
