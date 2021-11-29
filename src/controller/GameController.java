package controller;

import model.ChessPiece;
import view.ChessBoardPanel;
import view.GameFrame;
import view.StatusPanel;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


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
