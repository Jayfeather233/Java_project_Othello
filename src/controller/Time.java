package controller;

import view.GameFrame;

public class Time implements Runnable {

    private static long time;

    public static long getTime() {
        return time;
    }

    @Override
    public void run(){
        try {
            while(true) {
                time = System.currentTimeMillis();
                Thread.sleep(16);
                if(GameFrame.animation){
                    GameFrame.controller.getGamePanel().repaint();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
