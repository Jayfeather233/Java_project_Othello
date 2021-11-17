package controller;

import model.ChessPiece;

public class AIThread implements Runnable{

    int lev;
    ChessPiece cur;
    public AIThread(int level,ChessPiece cur){
        this.lev=level;
        this.cur=cur;
    }
    @Override
    public void run() {
        try {
            Thread.sleep(50);//稍微等待重新绘制
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AI.AIPlay(lev,cur);
    }
}
