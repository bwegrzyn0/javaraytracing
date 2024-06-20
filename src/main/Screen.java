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
    sendRays();
    Graphics graphics = bufferStrategy.getDrawGraphics();
    graphics.drawImage(screenImage, 0, 0, null);
    graphics.dispose();
    bufferStrategy.show();
  }

  Random random = new Random();
  float samplesPerPixelSquare = 7;
  int samplesPerPixel = (int) (samplesPerPixelSquare * samplesPerPixelSquare);

  public Vector3 randomVectorInUnitSphere() {
    while (true) {
      float x = random.nextFloat(2f) - 1f;
      float y = random.nextFloat(2f) - 1f;
      float z = random.nextFloat(2f) - 1f;
      if (x * x + y * y + z * z <= 1) {
        Vector3 vector3 = new Vector3(x, y, z);
        vector3.normalize();
        return vector3;
      }
    }
  }

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
      System.out.println("Column " + i + "/" + Main.WIDTH + " " + (float) i / (float) Main.WIDTH * 100 + "%");
      for (int j = 0; j < Main.HEIGHT; j++) {
        int[] color = { 0, 0, 0 };
        for (int k = 0; k < samplesPerPixelSquare; k++) {
          for (int l = 0; l < samplesPerPixelSquare; l++)
            for (int m = 0; m < 3; m++) {
              color[m] += rayColor(getRay(i, j, k, l))[m];
            }
        }

        int colorInt = ((color[0] / samplesPerPixel) << 16) + ((color[1] / samplesPerPixel) << 8)
            + (color[2] / samplesPerPixel);
        drawPixel(i, j, colorInt);
      }
    }
  }

  int bounces = 0;
  int bounceLimit = 6;

  public int[] rayColor(Ray ray) {
    float blue = 255f;
    float green = 100f * 0.5f * (1 + ray.dir.y) + 255f * (0.5f - 0.5f *
        ray.dir.y);
    float red = 255f * (0.5f - 0.5f * ray.dir.y) + 100f * 0.5f * (1 + ray.dir.y);
    for (Sphere sphere : Main.spheres) {
      float intersect = sphere.rayIntersection(ray);
      if (intersect >= 0) {
        if (bounces >= bounceLimit || intersect < 0.01f) {
          break;
        }
        bounces++;
        Vector3 intersectionPoint = new Vector3(ray.pointAt(intersect));
        Vector3 normal = new Vector3(intersectionPoint);
        normal.add(sphere.position.multiplied(-1));
        normal.normalize();
        Vector3 newRayDir = randomVectorInUnitSphere();
        if (newRayDir.dot(normal) < 0) {
          newRayDir.inverse();
        }
        newRayDir.normalize();
        int[] newColor = rayColor(new Ray(newRayDir, intersectionPoint));
        red = (float) sphere.color[0] / 255f * newColor[0];
        green = (float) sphere.color[1] / 255f * newColor[1];
        blue = (float) sphere.color[2] / 255f * newColor[2];
        break;
      }
    }
    int[] color = { (int) red, (int) green, (int) blue };
    bounces = 0;
    return color;
  }
}
