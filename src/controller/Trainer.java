package controller;

import view.GameFrame;

public class Trainer {
    public static boolean on=false;

    public static void T(){
        System.out.println("Self Playing...");

        on=true;
        int[][][] score=new int[500][8][8];
        int[][] scoreMove=new int[500][30];
        int[] scoreS=new int[500];

        for(int T=0;T<500;T++){
            for(int i=0;i<8;i++) {
                for (int j = 0; j < 8; j++) {
                    score[T][i][j]=(int)(Math.random()*170)-64;
                    score[T][i][j]=Math.min(Math.max(score[T][i][j],-64),64);
                }
            }
            scoreS[T]=0;
        }

        int T=0;
        while(T<500){

            for(int i=0;i<10;i++){
                for(int j=i+1;j<10;j++){
                    System.out.printf("Running %d %d...\n",i,j);
                    boolean flg=true;
                    while(GameController.whoWin==0){
                        if(flg) AI.setPanelScore(score[i]);
                        else AI.setPanelScore(score[j]);
                        AI.AIPlay(8, GameFrame.controller.getCurrentPlayer());
                        flg=!flg;

                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    switch (GameController.whoWin){
                        case 1:scoreS[i]++;break;
                        case 2:scoreS[j]++;break;
                        default:;
                    }
                    GameController.whoWin=0;
                }
            }

            int maxn=0,maxp=-1;
            for(int i=0;i<50;i++){
                if(scoreS[i]>maxn){
                    maxn=scoreS[i];
                    maxp=i;
                }
            }
            for(int i=0;i<8;i++) {
                for (int j = 0; j < 8; j++) {
                    System.out.printf("%d ",score[maxp][i][j]);
                }
                System.out.println("");
            }

            T++;
        }

        while(GameController.whoWin==0){
            AI.AIPlay(10, GameFrame.controller.getCurrentPlayer());
        }
    }
}
