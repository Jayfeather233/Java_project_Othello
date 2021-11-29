package view;

import javax.swing.*;
import java.awt.*;

public class BackGroundPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(GameFrame.getBackGroundImage(), 0, 0, this.getWidth(), this.getHeight(), null);
    }

    public void reSize(int x, int y) {
        this.setSize(Math.min(x, y), Math.min(x, y));
    }
}
