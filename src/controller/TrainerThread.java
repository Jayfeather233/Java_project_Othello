package controller;

public class TrainerThread implements Runnable {
    @Override
    public void run() {
        Trainer.T();
    }
}
