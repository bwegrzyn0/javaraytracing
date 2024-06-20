package main;

import java.awt.*;
import java.awt.image.*;

import math.*;
import ray.*;

public class Screen extends Canvas {

  BufferedImage screenImage;
  public int[] pixels;
  Vector3 uVector = new Vector3(Main.viewportWidth, 0, 0);
  Vector3 vVector = new Vector3(0, Main.viewportHeight, 0);
  Vector3 cameraCenter = new Vector3(0, 0, 0);
  Vector3 upperLeft = new Vector3(cameraCenter);
  Vector3 deltaU = new Vector3(uVector);
  Vector3 deltaV = new Vector3(vVector);
  Vector3 pixel00Center;

  public Screen() {
    screenImage = new BufferedImage(Main.WIDTH, Main.HEIGHT, BufferedImage.TYPE_INT_RGB);
    pixels = ((DataBufferInt) screenImage.getRaster().getDataBuffer()).getData();
    upperLeft.z += Main.focalLength;
    upperLeft.add(uVector.multiplied(-0.5f));
    upperLeft.add(vVector.multiplied(0.5f));
    deltaU.multiply(1 / (float) Main.WIDTH);
    deltaV.multiply(1 / (float) Main.HEIGHT);
    pixel00Center = new Vector3(upperLeft);
    pixel00Center.add(deltaU.multiplied(0.5f));
    pixel00Center.add(deltaV.multiplied(-0.5f));
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
    sendRays();
    graphics.drawImage(screenImage, 0, 0, null);
    graphics.dispose();
    bufferStrategy.show();
  }

  public void sendRays() {
    for (int i = 0; i < Main.WIDTH; i++) {
      for (int j = 0; j < Main.HEIGHT; j++) {
        Vector3 pixelCenter = new Vector3(pixel00Center);
        pixelCenter.add(deltaU.multiplied(i));
        pixelCenter.add(deltaV.multiplied(-j));
        Vector3 rayDir = new Vector3(pixelCenter);
        rayDir.add(cameraCenter.multiplied(-1));
        rayDir.normalize();
        Ray ray = new Ray(rayDir, cameraCenter);
        drawPixel(i, j, rayColor(ray));
      }
    }
  }

  public int rayColor(Ray ray) {
    int color = (ray.dir.y >= 0) ? 255 + (100 << 8) : (100 << 16) + (100 << 8) + 100;
    for (Sphere sphere : Main.spheres) {
      float intersect = sphere.rayIntersection(ray);
      if (intersect >= 0) {
        Vector3 normal = ray.pointAt(intersect);
        normal.add(sphere.position.multiplied(-1));
        normal.normalize();
        color = ((int) ((1 + normal.x) * 255 / 2) << 16) + ((int) ((1 + normal.y) * 255 / 2) << 8)
            + (int) ((normal.z + 1) * 255 / 2);
        break;
      }
    }
    return color;
  }
}
