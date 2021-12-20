package view;


import LocalNet.*;
import components.*;
import controller.*;
import model.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

public class GameFrame extends JFrame {
    private static GameFrame th;
    public static final int PORT=9089;
    public static GameController controller;
    public static boolean animation = true;
    private ChessBoardPanel chessBoardPanel;
    private StatusPanel statusPanel;
    private BackGroundPanel backGroundPanel;
    public static boolean cheat = false, AICheat = false;
    public static ChessPiece AIPiece = null;
    public static int AI_Level = 1;
    private static Image blackChess, whiteChess, grayChess, panelImage, panelBackGroundImage;
    private static JMenuItem undoMenuItem;

    private static NetSender sender;

    /**
     * 获取对应图片
     */
    public static Image getImage(ChessPiece u) {
        if (u == ChessPiece.BLACK) return blackChess;
        else if (u == ChessPiece.WHITE) return whiteChess;
        else return grayChess;
    }

    public GameFrame(int frameSize) {
        this.setTitle("2021F CS102A Project Reversi");
        this.setLayout(null);
        th=this;
        new NetReceiver(PORT);

        //获取窗口边框的长度，将这些值加到主窗口大小上，这能使窗口大小和预期相符
        Insets inset = this.getInsets();
        this.setSize(frameSize + inset.left + inset.right, frameSize + inset.top + inset.bottom);


        this.setLocationRelativeTo(null);

        try {
            blackChess = ImageIO.read(new File("resource\\black.png"));
            whiteChess = ImageIO.read(new File("resource\\white.png"));
            grayChess = ImageIO.read(new File("resource\\gray.png"));
            panelImage = ImageIO.read(new File("resource\\panel.png"));
            panelBackGroundImage = ImageIO.read(new File("resource\\background.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        chessBoardPanel = new ChessBoardPanel((int) (this.getWidth() * 0.75), (int) (this.getHeight() * 0.75));
        backGroundPanel = new BackGroundPanel();
        statusPanel = new StatusPanel((int) (this.getWidth() * 0.7), (int) (this.getHeight() * 0.1));

        controller = new GameController(chessBoardPanel, statusPanel);
        controller.setGamePanel(chessBoardPanel);
        chessBoardPanel.checkPlaceable(controller.getCurrentPlayer(), null);

        this.setJMenuBar(initialMenuBar());
        this.add(chessBoardPanel);
        this.add(backGroundPanel);
        this.add(statusPanel);


        this.getContentPane().setBackground(new Color(17, 17, 17));
        statusPanel.setBackground(new Color(30, 30, 30));
        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        new Thread(new Time()).start();
    }

    public static void playWith(String substring) {
        sender=new NetSender(substring,PORT);
        th.doRestart();
        ChessGridComponent.netOn=true;
        AIPiece=ChessPiece.BLACK;
    }

    public static void send(String s) {
        sender.send(s);
    }

    private JMenuBar initialMenuBar() {

        //创建菜单栏MenuBar，至211行
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenu gameMenu = new JMenu("Game");
        JMenu localMenu = new JMenu("Local-net");
        JMenu AIMenu = new JMenu("vsAI");
        JMenu panelMenu = new JMenu("Panel");

        fileMenu.setMnemonic(KeyEvent.VK_F);
        gameMenu.setMnemonic(KeyEvent.VK_G);
        AIMenu.setMnemonic(KeyEvent.VK_A);
        panelMenu.setMnemonic(KeyEvent.VK_P);

        menuBar.add(fileMenu);
        menuBar.add(gameMenu);
        menuBar.add(localMenu);
        menuBar.add(AIMenu);
        menuBar.add(panelMenu);

        JMenuItem loadFileMenuItem = new JMenuItem("Load");
        JMenuItem saveFileMenuItem = new JMenuItem("Save");
        fileMenu.add(loadFileMenuItem);
        fileMenu.add(saveFileMenuItem);
        loadFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        saveFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        loadFileMenuItem.addActionListener(e -> {
            System.out.println("clicked Load Btn");
            String filePath = JOptionPane.showInputDialog(this, "input the path here(need .txt)");
            int error=controller.readFileData(filePath);
            if (error>0)
                JOptionPane.showMessageDialog(this, String.format("Cannot load the file! Error code:%d",error));
        });
        saveFileMenuItem.addActionListener(e -> {
            System.out.println("clicked Save Btn");
            String filePath = JOptionPane.showInputDialog(this, "input the path here");
            try {
                controller.writeDataToFile(filePath);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        JMenuItem restartMenuItem = new JMenuItem("Restart");
        undoMenuItem = new JMenuItem("Undo");
        JMenuItem surrenderMenuItem = new JMenuItem("Surrender");
        JMenuItem reverseX = new JMenuItem("Horizontal Flip");
        JMenuItem reverseY = new JMenuItem("Vertical Flip");

        gameMenu.add(restartMenuItem);
        gameMenu.add(undoMenuItem);
        gameMenu.add(surrenderMenuItem);
        gameMenu.add(reverseX);
        gameMenu.add(reverseY);
        restartMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
        undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));

        restartMenuItem.addActionListener(e->doRestart());
        setUndoEnabled(false);
        undoMenuItem.addActionListener(e -> {
            GameFrame.controller.getGamePanel().doUndo();
            if (!GameFrame.controller.getGamePanel().hasNextUndo()) {
                setUndoEnabled(false);
            }
            UndoList uu = controller.getGamePanel().undoList;
            if (uu.stepList.size() != 0)
                ChessGridComponent.setLast(uu.stepList.get(uu.stepList.size() - 1).rowIndex, uu.stepList.get(uu.stepList.size() - 1).columnIndex);//设置上一个落子点（红）
            else ChessGridComponent.setLast(0, 0);
        });
        surrenderMenuItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(controller.getGamePanel(), (controller.getCurrentPlayer() == ChessPiece.WHITE ? "BLACK" : "WHITE") + " WINS!");
            restartMenuItem.doClick();
        });
        reverseX.addActionListener(e -> chessBoardPanel.flipX());
        reverseY.addActionListener(e -> chessBoardPanel.flipY());

        gameMenu.addSeparator();

        JCheckBoxMenuItem cheatMode = new JCheckBoxMenuItem("Cheat mode");
        JCheckBoxMenuItem AICheatMode = new JCheckBoxMenuItem("AI Cheat mode");
        gameMenu.add(cheatMode);
        gameMenu.add(AICheatMode);
        cheatMode.addActionListener(e -> {
            System.out.println("Cheat mode " + (cheat ? "off" : "on"));
            cheat = !cheat;
            controller.getGamePanel().checkPlaceable(controller.getCurrentPlayer(), null);
            if (cheat) {
                AICheatMode.setSelected(true);
                AICheat = true;
            }

            if (controller.getGamePanel().checkGray()) {//没有灰色，跳过落子
                controller.getGamePanel().doJump();
            }

            if (GameFrame.AIPiece == GameFrame.controller.getCurrentPlayer()) {//如果开启AI就让AI跑下一步
                ChessGridComponent.AIOn = true;
                new Thread(new AIThread(GameFrame.AI_Level, GameFrame.AIPiece)).start();//以线程方式启动AI
            }
        });
        AICheatMode.addActionListener(e -> AICheat = !AICheat);

        JMenuItem startListen = new JMenuItem("Play with...");
        localMenu.add(startListen);
        startListen.addActionListener(e->{
            String ip=null;
            boolean flg;
            do {
                flg=false;
                int[] targetIP=new int[4];
                String[] ips=null;
                do {
                    if (ips != null) JOptionPane.showMessageDialog(this, "Error! Invalid IP address.");
                    try {
                        ip = JOptionPane.showInputDialog("Your IP is " + InetAddress.getLocalHost().getHostAddress() + "\nInput the IP here:");
                    } catch (UnknownHostException ex) {
                        ex.printStackTrace();
                    }
                    if (ip != null) ips = ip.split("\\.");

                } while (ip!=null && Objects.requireNonNull(ips).length != 4 );
                if(ip==null) break;
                for (String s : ips) {
                    if(checkIsNumber(s)){
                        flg=true;
                        break;
                    }
                }
                if(!flg) {
                    for(int i=0;i<ips.length;i++){
                        targetIP[i]=Integer.parseInt(ips[i]);
                        if(targetIP[i]>255||targetIP[i]<0){
                            flg=true;
                            break;
                        }
                    }
                }
            }while(flg);

            if(ip!=null){
                sender=new NetSender(ip,PORT);
                try {
                    sender.send("N"+InetAddress.getLocalHost().getHostAddress());
                    doRestart();
                    ChessGridComponent.netOn=true;
                    AIPiece=ChessPiece.WHITE;
                } catch (UnknownHostException ex) {
                    JOptionPane.showMessageDialog(this,ex.getMessage());
                }
            }else{
                JOptionPane.showMessageDialog(this,"Canceled.");
            }
        });


        JCheckBoxMenuItem AIMode = new JCheckBoxMenuItem("AI mode");
        AIMenu.add(AIMode);
        AIMode.addActionListener(e -> {
            System.out.println("AI mode " + (AIPiece == null ? "on" : "off"));
            if (AIPiece == null) {
                AIPiece = controller.getCurrentPlayer() == ChessPiece.BLACK ? ChessPiece.WHITE : ChessPiece.BLACK;
            } else {
                AIPiece = null;
            }
        });
        AIMenu.addSeparator();

        JRadioButtonMenuItem AILevel1 = new JRadioButtonMenuItem("Easy");
        JRadioButtonMenuItem AILevel2 = new JRadioButtonMenuItem("Normal");
        JRadioButtonMenuItem AILevel3 = new JRadioButtonMenuItem("Hard");
        JRadioButtonMenuItem AILevel4 = new JRadioButtonMenuItem("Very Hard");
        JRadioButtonMenuItem AILevel5 = new JRadioButtonMenuItem("Very Slow Hard");
        AIMenu.add(AILevel1);
        AIMenu.add(AILevel2);
        AIMenu.add(AILevel3);
        AIMenu.add(AILevel4);
        AIMenu.add(AILevel5);
        AILevel1.addActionListener(e -> AI_Level = 2);
        AILevel2.addActionListener(e -> AI_Level = 4);
        AILevel3.addActionListener(e -> AI_Level = 8);
        AILevel4.addActionListener(e -> AI_Level = 10);
        AILevel5.addActionListener(e -> AI_Level = 12);
        ButtonGroup AILevel = new ButtonGroup();
        AILevel.add(AILevel1);
        AILevel.add(AILevel2);
        AILevel.add(AILevel3);
        AILevel.add(AILevel4);
        AILevel.add(AILevel5);
        AILevel1.setSelected(true);

        AIMenu.addSeparator();

        JCheckBoxMenuItem AIPlay = new JCheckBoxMenuItem("AI play itself");
        AIMenu.add(AIPlay);
        AIPlay.addActionListener(e -> {
            if (AIPlay.getState()) {
                Thread a = new Thread(new TrainerThread());
                a.start();
            }
        });


        JCheckBoxMenuItem animate = new JCheckBoxMenuItem("Enable animation");
        panelMenu.add(animate);
        panelMenu.addSeparator();
        animate.addActionListener(e -> animation = animate.getState());
        animate.setSelected(true);

        JMenuItem backColor = new JMenuItem("Choose background color");
        JMenuItem panelColor = new JMenuItem("Choose status panel color");
        panelMenu.add(backColor);
        panelMenu.add(panelColor);
        backColor.addActionListener(e -> this.getContentPane().setBackground(JColorChooser.showDialog(this, "Choose background color", Color.lightGray)));
        panelColor.addActionListener(e -> statusPanel.setBackground(JColorChooser.showDialog(this, "Choose status panel color", Color.lightGray)));

        //菜单栏到此结束
        return menuBar;
    }

    public static boolean checkIsNumber(String a){
        for(int i=0;i<a.length();i++) {
            if (a.charAt(i) > '9' || a.charAt(i) < '0') {
                return true;
            }
        }
        return false;
    }

    public static void setUndoEnabled(boolean u) {
        undoMenuItem.setEnabled(u);
    }

    public static Image getPanelImage() {
        return panelImage;
    }

    public static Image getBackGroundImage() {
        return panelBackGroundImage;
    }

    /**
     * 当窗口大小改变，处理所有内部元件位置和大小
     */
    public void resize() {

        int W = this.getContentPane().getWidth(), H = this.getContentPane().getHeight();
        int M = Math.min(W, H - 60);

        statusPanel.setSize(W, 60);
        statusPanel.setLocation(0, H - 60);
        statusPanel.reSize();

        chessBoardPanel.reSize((int) (M * 0.95), (int) (M * 0.95));
        chessBoardPanel.setLocation((W - chessBoardPanel.getWidth()) / 2, (int) (M * 0.025));

        backGroundPanel.reSize(M, M);
        backGroundPanel.setLocation((W - backGroundPanel.getWidth()) / 2, 0);
    }

    public void doRestart(){
        chessBoardPanel.initialGame();
        controller.resetScore();
        controller.getGamePanel().repaint();
        controller.getGamePanel().getUndoList().resetUndoList();
        chessBoardPanel.checkPlaceable(controller.getCurrentPlayer(), null);
        setUndoEnabled(false);
        statusPanel.repaint();
    }
}
