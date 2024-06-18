package main;

import java.awt.*;
import javax.swing.*;

public class Window {

  public Window(int WIDTH, int HEIGHT, String TITLE, Canvas canvas) {
    JFrame frame = new JFrame(TITLE);
    frame.setSize(WIDTH, HEIGHT);
    frame.setResizable(false);
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setIgnoreRepaint(true);
    frame.setVisible(true);
    frame.add(canvas);
  }
}
