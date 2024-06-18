package main;

import java.awt.*;
import java.awt.image.*;
import java.util.Random;

public class Screen extends Canvas {

  BufferedImage screenImage;
  int[] pixels;
  Random r = new Random();

  public Screen() {
    screenImage = new BufferedImage(Main.WIDTH, Main.HEIGHT, BufferedImage.TYPE_INT_RGB);
    pixels = ((DataBufferInt) screenImage.getRaster().getDataBuffer()).getData();
    for (int i = 0; i < pixels.length; i++) {
      pixels[i] = r.nextInt();
    }
  }

  public void drawPixel(int x, int y, int rgb) {
    pixels[Main.WIDTH * y + x] = rgb;
  }

  public void render() {
    BufferStrategy bufferStrategy = getBufferStrategy();
    if (bufferStrategy == null) {
      createBufferStrategy(3);
      return;
    }
    Graphics graphics = bufferStrategy.getDrawGraphics();
    graphics.drawImage(screenImage, 0, 0, null);
    graphics.dispose();
    bufferStrategy.show();
  }
}
