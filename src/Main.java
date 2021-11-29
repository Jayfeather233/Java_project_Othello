import view.GameFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            GameFrame mainFrame = new GameFrame(800);
            mainFrame.setVisible(true);
            mainFrame.setMinimumSize(new Dimension(400, 550));

            mainFrame.addComponentListener(new ComponentAdapter() {//让窗口响应大小改变事件

                @Override
                public void componentResized(ComponentEvent e) {
                    mainFrame.resize();
                }
            });
        });
    }
}
