package view;


import controller.GameController;
import model.ChessPiece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameFrame extends JFrame {
    public static GameController controller;
    private ChessBoardPanel chessBoardPanel;
    private StatusPanel statusPanel;
    public static boolean cheat=false;

    public GameFrame(int frameSize) {

        this.setTitle("2021F CS102A Project Reversi");
        this.setLayout(null);

        //获取窗口边框的长度，将这些值加到主窗口大小上，这能使窗口大小和预期相符
        Insets inset = this.getInsets();
        this.setSize(frameSize + inset.left + inset.right, frameSize + inset.top + inset.bottom);

        this.setLocationRelativeTo(null);


        JMenuBar menuBar=new JMenuBar();

        JMenu fileMenu=new JMenu("File");
        JMenu gameMenu=new JMenu("Game");
        JMenu AI=new JMenu("vsAI");

        menuBar.add(fileMenu);
        menuBar.add(gameMenu);
        menuBar.add(AI);

        JMenuItem loadFileMenuItem=new JMenuItem("Load");
        JMenuItem saveFileMenuItem=new JMenuItem("Save");
        fileMenu.add(loadFileMenuItem);
        fileMenu.add(saveFileMenuItem);
        loadFileMenuItem.addActionListener(e -> {
            System.out.println("clicked Load Btn");
            String filePath = JOptionPane.showInputDialog(this, "input the path here");
            if(controller.readFileData(filePath))
                JOptionPane.showMessageDialog(this,"Cannot load the file!");
        });
        saveFileMenuItem.addActionListener(e -> {
            System.out.println("clicked Save Btn");
            String filePath = JOptionPane.showInputDialog(this, "input the path here");
            controller.writeDataToFile(filePath);
        });

        JMenuItem restartMenuItem=new JMenuItem("Restart");
        gameMenu.add(restartMenuItem);
        restartMenuItem.addActionListener(e -> {
            System.out.println(e);
            chessBoardPanel.initialGame();
            controller.resetScore();
            controller.getGamePanel().repaint();
            statusPanel.repaint();
            chessBoardPanel.checkPlaceable(controller.getCurrentPlayer());
        });

        gameMenu.addSeparator();

        JCheckBoxMenuItem cheatMode=new JCheckBoxMenuItem("Cheat mode");
        gameMenu.add(cheatMode);
        cheatMode.addActionListener(e -> {
            System.out.println("Cheat mode "+ (cheat ? "off" : "on"));
            cheat=!cheat;
        });


        chessBoardPanel = new ChessBoardPanel((int) (this.getWidth() * 0.8), (int) (this.getHeight() * 0.7));
        chessBoardPanel.setLocation((this.getWidth() - chessBoardPanel.getWidth()) / 2, (this.getHeight() - chessBoardPanel.getHeight()) / 3);

        statusPanel = new StatusPanel((int) (this.getWidth() * 0.8), (int) (this.getHeight() * 0.1));
        statusPanel.setLocation((this.getWidth() - chessBoardPanel.getWidth()) / 2, 0);
        controller = new GameController(chessBoardPanel, statusPanel);
        controller.setGamePanel(chessBoardPanel);
        chessBoardPanel.checkPlaceable(controller.getCurrentPlayer());

        this.setJMenuBar(menuBar);
        this.add(chessBoardPanel);
        this.add(statusPanel);


        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }
}
