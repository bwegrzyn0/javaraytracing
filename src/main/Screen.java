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
  Vector3 cameraCenter = new Vector3(0, 3, 0);
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

  public int frames = 0;

  public void drawPixel(int x, int y, int[] color) {

    if (frames != 0) {
      float w = 1 / ((float) frames + 1f);
      float red = (float) color[0] * w;
      float green = (float) color[1] * w;
      float blue = (float) color[2] * w;
      float oldRed = pixels[Main.WIDTH * y + x] >> 16;
      float oldGreen = pixels[Main.WIDTH * y + x];
      oldGreen -= ((int) oldRed << 16);
      oldGreen = (int) oldGreen >> 8;
      float oldBlue = pixels[Main.WIDTH * y + x] - ((int) oldRed << 16) - ((int) oldGreen << 8);
      oldRed *= (1f - w);
      oldGreen *= (1f - w);
      oldBlue *= (1f - w);
      float newRed = oldRed + red;
      float newGreen = oldGreen + green;
      float newBlue = oldBlue + blue;
      int rgb = ((int) newRed << 16) + ((int) newGreen << 8) + ((int) newBlue);
      pixels[Main.WIDTH * y + x] = rgb;
    } else

      pixels[Main.WIDTH * y + x] = (color[0] << 16) + (color[1] << 8) + color[2];

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

  Random random = new Random();
  int samplesPerPixel = 30;

  public Vector3 randomVectorInUnitSphere() {
    while (true) {
      float x = random.nextFloat(2f) - 1f;
      float y = random.nextFloat(2f) - 1f;
      float z = random.nextFloat(2f) - 1f;
      if (x * x + y * y + z * z <= 1) {
        return new Vector3(x, y, z);
      }
    }
  }

  public Ray getRay(int i, int j) {
    Vector3 pixelCenter = new Vector3(pixel00Center);
    pixelCenter.add(deltaU.multiplied((float) i + random.nextFloat(1f) - 0.5f));
    pixelCenter.add(deltaV.multiplied(-(float) j + random.nextFloat(1f) - 0.5f));
    Vector3 rayDir = new Vector3(pixelCenter);
    rayDir.add(cameraCenter.multiplied(-1));
    rayDir.normalize();
    return new Ray(rayDir, cameraCenter);
  }

  float clamp(float x, float a, float b) {
    if (x < a)
      return a;
    else if (x > b)
      return b;
    else
      return x;
  }

  public void sendRays() {
    for (int i = 0; i < Main.WIDTH; i++) {
      // System.out.println("Column " + i + "/" + Main.WIDTH + " " + (float) i /
      // (float) Main.WIDTH * 100 + "%");
      for (int j = 0; j < Main.HEIGHT; j++) {
        int[] color = { 0, 0, 0 };
        for (int k = 0; k < samplesPerPixel; k++) {
          for (int m = 0; m < 3; m++)
            color[m] += rayColor(getRay(i, j))[m];
        }
        for (int k = 0; k < 3; k++) {
          color[k] /= samplesPerPixel;
          color[k] = (int) clamp(color[k], 0, 255);
        }
        drawPixel(i, j, color);
      }
    }
  }

  int bounces = 0;
  int bounceLimit = 50;

  public int[] rayColor(Ray ray) {
    float blue1 = 255f;
    float green1 = 100f * 0.5f * (1 + ray.dir.y) + 255f * (0.5f - 0.5f *
        ray.dir.y);
    float red1 = 255f * (0.5f - 0.5f * ray.dir.y) + 100f * 0.5f * (1 + ray.dir.y);
    float red = (ray.dir.y >= 0.95f && ray.dir.y <= 1f) ? 255f : red1;
    float green = (ray.dir.y >= 0.95f && ray.dir.y <= 1f) ? 255f : green1;
    float blue = (ray.dir.y >= 0.95f && ray.dir.y <= 1f) ? 255f : blue1;
    for (Sphere sphere : Main.spheres) {
      float intersect = sphere.rayIntersection(ray);
      if (intersect >= 0) {
        if (bounces >= bounceLimit || intersect < 0.001f) {
          break;
        }
        bounces++;
        Vector3 intersectionPoint = new Vector3(ray.pointAt(intersect));
        Vector3 normal = new Vector3(intersectionPoint);
        normal.add(sphere.position.multiplied(-1));
        normal.normalize();
        Vector3 newRayDir;
        if (random.nextFloat(1f) <= sphere.roughness) {
          newRayDir = randomVectorInUnitSphere();
          newRayDir.add(normal);
          newRayDir.normalize();
        } else {
          newRayDir = new Vector3(ray.dir);
          Vector3 b = new Vector3(normal);
          b.multiply(Math.abs(ray.dir.dot(normal)) * 2);
          newRayDir.add(b);
        }
        int[] newColor = rayColor(new Ray(newRayDir, intersectionPoint));
        red = (float) sphere.color[0] / 255f * (float) newColor[0];
        green = (float) sphere.color[1] / 255f * (float) newColor[1];
        blue = (float) sphere.color[2] / 255f * (float) newColor[2];
        break;
      }
    }
    int[] color = { (int) red, (int) green, (int) blue };
    bounces = 0;
    return color;
  }
}
