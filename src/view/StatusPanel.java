package view;

import model.ChessPiece;

import javax.swing.*;
import java.awt.*;

public class StatusPanel extends JPanel {
    private JLabel blackScoreLabel;
    private JLabel whiteScoreLabel;

    public StatusPanel(int width, int height) {
        this.setSize(width, height);
        this.setLayout(null);
        this.setVisible(true);

        this.blackScoreLabel = new JLabel("",JLabel.CENTER);
        this.blackScoreLabel.setSize(30,23);
        this.blackScoreLabel.setFont(new Font("Calibri", Font.ITALIC, 23));
        this.blackScoreLabel.setForeground(Color.WHITE);
        add(blackScoreLabel);

        this.whiteScoreLabel = new JLabel("",JLabel.CENTER);
        this.whiteScoreLabel.setSize(30,23);
        this.whiteScoreLabel.setFont(new Font("Calibri", Font.ITALIC, 23));
        this.whiteScoreLabel.setForeground(Color.BLACK);
        add(whiteScoreLabel);

        this.setScoreText(2,2);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(GameFrame.controller.getCurrentPlayer()==ChessPiece.BLACK) {
            g.drawImage(GameFrame.getImage(ChessPiece.WHITE), this.getWidth() / 2 - 8, 5, 50, 50, null);
            g.drawImage(GameFrame.getImage(ChessPiece.BLACK), this.getWidth() / 2 - 42, 5, 50, 50, null);
        }else{
            g.drawImage(GameFrame.getImage(ChessPiece.BLACK), this.getWidth() / 2 - 42, 5, 50, 50, null);
            g.drawImage(GameFrame.getImage(ChessPiece.WHITE), this.getWidth() / 2 - 8, 5, 50, 50, null);
        }
    }

    public void setScoreText(int black, int white) {
        this.blackScoreLabel.setText(String.format("%2d", black));
        this.whiteScoreLabel.setText(String.format("%2d", white));
    }

    public void reSize(){
        blackScoreLabel.setLocation(this.getWidth()/2-36,20);
        whiteScoreLabel.setLocation(this.getWidth()/2+8,20);
    }

}
