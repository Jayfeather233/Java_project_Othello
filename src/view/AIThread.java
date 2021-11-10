package view;

import components.ChessGridComponent;
import model.ChessPiece;

public class AIThread implements Runnable{

    int lev;
    ChessPiece cur;
    ChessBoardPanel panel;
    public AIThread(int level,ChessPiece cur,ChessBoardPanel pa){
        this.lev=level;
        this.cur=cur;
        this.panel=pa;
    }
    @Override
    public void run() {
        try {
            Thread.sleep(50);//稍微等待重新绘制
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        panel.AIPlay(lev,cur);
    }
}
