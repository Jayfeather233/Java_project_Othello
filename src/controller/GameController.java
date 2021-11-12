package controller;

import model.ChessPiece;
import view.*;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class GameController {


    private ChessBoardPanel gamePanel;
    private StatusPanel statusPanel;
    private ChessPiece currentPlayer;
    private int blackScore;
    private int whiteScore;
    public int jumpTime=0;

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
    public void resetScore(){
        this.currentPlayer = ChessPiece.BLACK;
        blackScore = 2;
        whiteScore = 2;
        statusPanel.setPlayerText(currentPlayer.name());
        statusPanel.setScoreText(blackScore, whiteScore);
    }

    /**
     * 交换黑白
     */
    public void swapPlayer() {
        currentPlayer = (currentPlayer == ChessPiece.BLACK) ? ChessPiece.WHITE : ChessPiece.BLACK;
        statusPanel.setPlayerText(currentPlayer.name());
        statusPanel.setScoreText(blackScore, whiteScore);
        checkPlaceable(currentPlayer);//交换完后重新计算能下的位置
    }


    /**
     * 加分
     * @param cur 要加分的颜色
     * @param u 要加的分数，是负数就减
     */
    public void countScore(ChessPiece cur,int u) {
        if (cur == ChessPiece.BLACK) {
            blackScore+=u;
        } else {
            whiteScore+=u;
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


    public boolean readFileData(String fileName) {
        List<String> fileData = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                fileData.add(line);
            }
            fileData.forEach(System.out::println);
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }
    }

    public void writeDataToFile(String fileName) {
    }

    /**
     * 这个地能不能点
     */
    public boolean canClick(int row, int col) {
        return gamePanel.canClickGrid(row, col, currentPlayer);
    }

    /**
     * 重新计算能放的位置（灰）
     */
    public void checkPlaceable(ChessPiece currentPlayer) {
        gamePanel.checkPlaceable(currentPlayer);
    }

    /**
     * 跳过一回合
     * 连续两回合则结束游戏
     */
    public void jumpThrough(){
        jumpTime++;
        swapPlayer();
        System.out.println("jump");
        if(jumpTime==2){
            endGame();
        }
    }

    public void endGame() {
        JOptionPane.showMessageDialog(gamePanel,(blackScore!=whiteScore)?((blackScore>whiteScore ? "BLACK" : "WHITE") + " WINS!") : "DRAW!");
    }
}
