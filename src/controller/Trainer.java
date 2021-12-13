package controller;

import view.GameFrame;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * AI自我对下来判断两个权值表谁更好
 * 正常运行时不应执行这些代码
 */
public class Trainer {
    public static boolean on = false;

    public static void T() throws InterruptedException, IOException {
        System.out.println("Self Playing...");

        on = true;
        int[][][] score = new int[2][8][8];

        FileReader fileReader = new FileReader("resource/Score1.txt");
        BufferedReader in = new BufferedReader(fileReader);
        Scanner S=new Scanner(in);

        for(int ii=0;ii<8;ii++) {
            for (int jj = 0; jj < 8; jj++) {
                score[0][ii][jj]=S.nextInt();
                System.out.print(" "+score[0][ii][jj]);
            }
            System.out.println("");
        }

        S.close();
        in.close();
        fileReader.close();

        fileReader = new FileReader("resource/Score2.txt");
        in = new BufferedReader(fileReader);
        S=new Scanner(in);

        for(int ii=0;ii<8;ii++) {
            for (int jj = 0; jj < 8; jj++) {
                score[1][ii][jj]=S.nextInt();
                System.out.print(" "+score[1][ii][jj]);
            }
            System.out.println("");
        }

        in.close();
        fileReader.close();

        tr(score, true);
        Thread.sleep(100);
        tr(score, false);
    }

    private static void tr(int[][][] score, boolean flg) {
        while (GameController.whoWin == 0) {
            if (flg) AI.setPanelScore(score[1]);
            else AI.setPanelScore(score[0]);
            AI.AIPlay(8, GameFrame.controller.getCurrentPlayer());
            flg = !flg;

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        switch (GameController.whoWin) {
            case 1 -> System.out.println("Black");
            case 2 -> System.out.println("White");
            default -> System.out.println("Draw");
        }
        GameController.whoWin=0;
    }
}
