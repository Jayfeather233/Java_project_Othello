package view;


import controller.GameController;
import model.ChessPiece;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    public static GameController controller;
    private ChessBoardPanel chessBoardPanel;
    private StatusPanel statusPanel;
    public static boolean cheat=false;
    public static ChessPiece AIPiece=null;
    public static int AI_Level=1;

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
        JMenu AIMenu=new JMenu("vsAI");

        menuBar.add(fileMenu);
        menuBar.add(gameMenu);
        menuBar.add(AIMenu);

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

        JCheckBoxMenuItem AIMode=new JCheckBoxMenuItem("AI mode");
        AIMenu.add(AIMode);
        AIMode.addActionListener(e -> {
            System.out.println("AI mode "+ (AIPiece==null ? "on" : "off"));
            if(AIPiece==null){
                AIPiece=controller.getCurrentPlayer();
            }else{
                AIPiece=null;
            }
        });
        AIMenu.addSeparator();

        JRadioButtonMenuItem AILevel1=new JRadioButtonMenuItem("Easy");
        JRadioButtonMenuItem AILevel2=new JRadioButtonMenuItem("Normal");
        JRadioButtonMenuItem AILevel3=new JRadioButtonMenuItem("Hard");
        AIMenu.add(AILevel1);
        AIMenu.add(AILevel2);
        AIMenu.add(AILevel3);
        AILevel1.addActionListener(e -> AI_Level=1);
        AILevel2.addActionListener(e -> AI_Level=2);
        AILevel3.addActionListener(e -> AI_Level=4);
        ButtonGroup AILevel=new ButtonGroup();
        AILevel.add(AILevel1);
        AILevel.add(AILevel2);
        AILevel.add(AILevel3);
        AILevel1.setSelected(true);



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
