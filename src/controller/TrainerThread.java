package controller;

import java.io.IOException;

public class TrainerThread implements Runnable {
    @Override
    public void run() {
        try {
            Trainer.T();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
