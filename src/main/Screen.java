package main;

import java.awt.*;
import java.awt.image.*;
import java.util.Random;

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

  Random random = new Random();
  float samplesPerPixelSquare = 5;
  int samplesPerPixel = (int) (samplesPerPixelSquare * samplesPerPixelSquare);

  public Ray getRay(int i, int j, int k, int l) {
    Vector3 pixelCenter = new Vector3(pixel00Center);
    pixelCenter.add(deltaU.multiplied((float) i + (float) k * (1f / samplesPerPixelSquare) - 0.5f));
    pixelCenter.add(deltaV.multiplied(-(float) j + (float) l * (1f / samplesPerPixelSquare) - 0.5f));
    Vector3 rayDir = new Vector3(pixelCenter);
    rayDir.add(cameraCenter.multiplied(-1));
    rayDir.normalize();
    return new Ray(rayDir, cameraCenter);
  }

  public void sendRays() {
    for (int i = 0; i < Main.WIDTH; i++) {
      for (int j = 0; j < Main.HEIGHT; j++) {
        int[] color = { 0, 0, 0 };
        for (int k = 0; k < samplesPerPixelSquare; k++) {
          for (int l = 0; l < samplesPerPixelSquare; l++)
            for (int m = 0; m < 3; m++) {
              color[m] += rayColor(getRay(i, j, k, l), samplesPerPixel)[m];
            }
        }

        int colorInt = ((color[0] / samplesPerPixel) << 16) + ((color[1] / samplesPerPixel) << 8)
            + (color[2] / samplesPerPixel);
        drawPixel(i, j, colorInt);
      }
    }
  }

  public int[] rayColor(Ray ray, float samplePoints) {
    float blue = 255f;
    float green = 100f * 0.5f * (1 + ray.dir.y) + 255f * (0.5f - 0.5f * ray.dir.y);
    float red = 255f * (0.5f - 0.5f * ray.dir.y);
    for (Sphere sphere : Main.spheres) {
      float intersect = sphere.rayIntersection(ray);
      if (intersect >= 0) {
        Vector3 normal = ray.pointAt(intersect);
        normal.add(sphere.position.multiplied(-1));
        normal.normalize();
        red = (1 + normal.x) * 255f / 2f;
        green = (1 + normal.y) * 255f / 2f;
        blue = (normal.z + 1) * 255f / 2f;
        break;
      }
    }
    int[] color = { (int) red, (int) green, (int) blue };
    return color;
  }
}
