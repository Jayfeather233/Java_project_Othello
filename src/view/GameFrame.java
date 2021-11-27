package view;


import components.ChessGridComponent;
import controller.*;
import model.ChessPiece;
import model.Step;
import model.UndoList;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GameFrame extends JFrame {
    public static GameController controller;
    public static boolean animation=true;
    private ChessBoardPanel chessBoardPanel;
    private StatusPanel statusPanel;
    private BackGroundPanel backGroundPanel;
    public static boolean cheat=false,AICheat=false;
    public static ChessPiece AIPiece=null;
    public static int AI_Level=1;
    private static Image blackChess;
    private static Image whiteChess;
    private static Image grayChess;
    private static Image panelImage;
    private static Image panelBackGroundImage;
    private static JMenuItem undoMenuItem;

    public static Image getImage(ChessPiece u){
        if(u==ChessPiece.BLACK) return blackChess;
        else if(u==ChessPiece.WHITE) return whiteChess;
        else return grayChess;
    }

    public GameFrame(int frameSize) {

        this.setTitle("2021F CS102A Project Reversi");
        this.setLayout(null);



        //创建菜单栏MenuBar
        JMenuBar menuBar=new JMenuBar();

        JMenu fileMenu=new JMenu("File");
        JMenu gameMenu=new JMenu("Game");
        JMenu AIMenu=new JMenu("vsAI");
        JMenu panelMenu=new JMenu("Panel");

        fileMenu.setMnemonic(KeyEvent.VK_F);
        gameMenu.setMnemonic(KeyEvent.VK_G);
        AIMenu.setMnemonic(KeyEvent.VK_A);
        panelMenu.setMnemonic(KeyEvent.VK_P);

        menuBar.add(fileMenu);
        menuBar.add(gameMenu);
        menuBar.add(AIMenu);
        menuBar.add(panelMenu);

        JMenuItem loadFileMenuItem=new JMenuItem("Load");
        JMenuItem saveFileMenuItem=new JMenuItem("Save");
        fileMenu.add(loadFileMenuItem);
        fileMenu.add(saveFileMenuItem);
        loadFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        saveFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
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
        undoMenuItem=new JMenuItem("Undo");
        JMenuItem surrenderMenuItem=new JMenuItem("Surrender");
        JMenuItem reverseX=new JMenuItem("Horizontal Flip");
        JMenuItem reverseY=new JMenuItem("Vertical Flip");

        gameMenu.add(restartMenuItem);
        gameMenu.add(undoMenuItem);
        gameMenu.add(surrenderMenuItem);
        gameMenu.add(reverseX);
        gameMenu.add(reverseY);
        restartMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,InputEvent.CTRL_DOWN_MASK));
        undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,InputEvent.CTRL_DOWN_MASK));

        restartMenuItem.addActionListener(e -> {
            System.out.println(e);
            chessBoardPanel.initialGame();
            controller.resetScore();
            controller.getGamePanel().repaint();
            controller.getGamePanel().getUndoList().resetUndoList();
            chessBoardPanel.checkPlaceable(controller.getCurrentPlayer(),null);
            setUndoEnabled(false);
            statusPanel.repaint();
        });
        setUndoEnabled(false);
        undoMenuItem.addActionListener(e -> {
            GameFrame.controller.getGamePanel().doUndo();
            if(!GameFrame.controller.getGamePanel().hasNextUndo()){
                setUndoEnabled(false);
            }
            UndoList uu=controller.getGamePanel().undoList;
            if(uu.stepList.size()!=0) ChessGridComponent.setLast(uu.stepList.get(uu.stepList.size()-1).rowIndex,uu.stepList.get(uu.stepList.size()-1).columnIndex);//设置上一个落子点（红）
            else ChessGridComponent.setLast(0,0);
        });
        surrenderMenuItem.addActionListener(e->{
            JOptionPane.showMessageDialog(controller.getGamePanel(),(controller.getCurrentPlayer()==ChessPiece.WHITE ? "BLACK" : "WHITE") + " WINS!");
            restartMenuItem.doClick();
        });
        reverseX.addActionListener(e -> chessBoardPanel.flipX());
        reverseY.addActionListener(e -> chessBoardPanel.flipY());

        gameMenu.addSeparator();

        JCheckBoxMenuItem cheatMode=new JCheckBoxMenuItem("Cheat mode");
        JCheckBoxMenuItem AICheatMode=new JCheckBoxMenuItem("AI Cheat mode");
        gameMenu.add(cheatMode);
        gameMenu.add(AICheatMode);
        cheatMode.addActionListener(e -> {
            System.out.println("Cheat mode "+ (cheat ? "off" : "on"));
            cheat=!cheat;
            controller.getGamePanel().checkPlaceable(controller.getCurrentPlayer(),null);
            if(cheat) {
                AICheatMode.setSelected(true);
                AICheat = true;
            }

            if(controller.getGamePanel().checkGray()){//没有灰色，跳过落子
                controller.getGamePanel().doJump();
            }

            if(GameFrame.AIPiece==GameFrame.controller.getCurrentPlayer()){//如果开启AI就让AI跑下一步
                ChessGridComponent.AIOn=true;
                Thread a=new Thread(new AIThread(GameFrame.AI_Level, GameFrame.AIPiece));
                a.start();//Run AI in thread
            }
        });
        AICheatMode.addActionListener(e -> AICheat=!AICheat);

        JCheckBoxMenuItem AIMode=new JCheckBoxMenuItem("AI mode");
        AIMenu.add(AIMode);
        AIMode.addActionListener(e -> {
            System.out.println("AI mode "+ (AIPiece==null ? "on" : "off"));
            if(AIPiece==null){
                AIPiece=controller.getCurrentPlayer() == ChessPiece.BLACK ? ChessPiece.WHITE : ChessPiece.BLACK;
            }else{
                AIPiece=null;
            }
        });
        AIMenu.addSeparator();

        JRadioButtonMenuItem AILevel1=new JRadioButtonMenuItem("Easy");
        JRadioButtonMenuItem AILevel2=new JRadioButtonMenuItem("Normal");
        JRadioButtonMenuItem AILevel3=new JRadioButtonMenuItem("Hard");
        JRadioButtonMenuItem AILevel4=new JRadioButtonMenuItem("Very Hard");
        JRadioButtonMenuItem AILevel5=new JRadioButtonMenuItem("Very Slow Hard");
        AIMenu.add(AILevel1);
        AIMenu.add(AILevel2);
        AIMenu.add(AILevel3);
        AIMenu.add(AILevel4);
        AIMenu.add(AILevel5);
        AILevel1.addActionListener(e -> AI_Level=2);
        AILevel2.addActionListener(e -> AI_Level=4);
        AILevel3.addActionListener(e -> AI_Level=8);
        AILevel4.addActionListener(e -> AI_Level=10);
        AILevel5.addActionListener(e -> AI_Level=12);
        ButtonGroup AILevel=new ButtonGroup();
        AILevel.add(AILevel1);
        AILevel.add(AILevel2);
        AILevel.add(AILevel3);
        AILevel.add(AILevel4);
        AILevel.add(AILevel5);
        AILevel1.setSelected(true);

        AIMenu.addSeparator();

        JCheckBoxMenuItem AIPlay=new JCheckBoxMenuItem("AI play itself");
        //AIMenu.add(AIPlay);
        AIPlay.addActionListener(e -> {
            if(AIPlay.getState()) {
                Thread a=new Thread(new TrainerThread());
                a.start();
            }
        });


        JCheckBoxMenuItem animate=new JCheckBoxMenuItem("Enable animation");
        panelMenu.add(animate);
        panelMenu.addSeparator();
        animate.addActionListener(e -> animation=animate.getState());
        animate.setSelected(true);

        JMenuItem backColor=new JMenuItem("Choose background color");
        JMenuItem panelColor=new JMenuItem("Choose status panel color");
        panelMenu.add(backColor);
        panelMenu.add(panelColor);
        backColor.addActionListener(e-> this.getContentPane().setBackground(JColorChooser.showDialog(this,"Choose background color",Color.lightGray)));
        panelColor.addActionListener(e-> statusPanel.setBackground(JColorChooser.showDialog(this,"Choose status panel color",Color.lightGray)));

        //菜单栏到此结束
        //获取窗口边框的长度，将这些值加到主窗口大小上，这能使窗口大小和预期相符
        Insets inset = this.getInsets();
        this.setSize(frameSize + inset.left + inset.right, frameSize + inset.top + inset.bottom);


        this.setLocationRelativeTo(null);

        try {
            blackChess= ImageIO.read(new File("resource\\black.png"));
            whiteChess= ImageIO.read(new File("resource\\white.png"));
            grayChess= ImageIO.read(new File("resource\\gray.png"));
            panelImage=ImageIO.read(new File("resource\\panel.png"));
            panelBackGroundImage=ImageIO.read(new File("resource\\background.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        chessBoardPanel = new ChessBoardPanel((int) (this.getWidth() * 0.75), (int) (this.getHeight() * 0.75));
        backGroundPanel=new BackGroundPanel();
        statusPanel = new StatusPanel((int) (this.getWidth() * 0.7), (int) (this.getHeight() * 0.1));

        controller = new GameController(chessBoardPanel, statusPanel);
        controller.setGamePanel(chessBoardPanel);
        chessBoardPanel.checkPlaceable(controller.getCurrentPlayer(),null);

        this.setJMenuBar(menuBar);
        this.add(chessBoardPanel);
        this.add(backGroundPanel);
        this.add(statusPanel);


        this.getContentPane().setBackground(new Color(17,17,17));
        statusPanel.setBackground(new Color(30,30,30));
        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        new Thread(new Time()).start();
    }

    public static void setUndoEnabled(boolean u){
        undoMenuItem.setEnabled(u);
    }

    public static Image getPanelImage() {
        return panelImage;
    }

    public static Image getBackGroundImage() {
        return panelBackGroundImage;
    }

    public void resize() {

        int W=this.getContentPane().getWidth(),H=this.getContentPane().getHeight();
        int M=Math.min(W,H-60);

        statusPanel.setSize(W, 60);
        statusPanel.setLocation(0, H-60);
        statusPanel.reSize();

        chessBoardPanel.reSize((int)(M*0.95), (int)(M*0.95));
        chessBoardPanel.setLocation((W - chessBoardPanel.getWidth()) / 2, (int) (M * 0.025));

        backGroundPanel.reSize(M, M);
        backGroundPanel.setLocation((W - backGroundPanel.getWidth()) / 2, 0);
    }
}
