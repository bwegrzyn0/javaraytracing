package main;

import java.util.ArrayList;

import math.Vector3;
import ray.Sphere;

public class Main implements Runnable {

  public static final int WIDTH = 1000, HEIGHT = WIDTH * 9 / 16;
  public Screen screen;
  public static boolean running = false;
  private Thread thread;
  public static final float viewportWidth = 1f;
  public static final float viewportHeight = viewportWidth * ((float) Main.HEIGHT / (float) Main.WIDTH);
  public static final float focalLength = 0.6f;
  public static ArrayList<Sphere> spheres = new ArrayList<>();

  public Main() {
    screen = new Screen();
    int[] color1 = { 254, 100, 20 };
    int[] color2 = { 254, 254, 254 };
    int[] color3 = { 100, 254, 50 };
    int[] color4 = { 20, 150, 254 };
    spheres.add(new Sphere(new Vector3(0, 2, 10), 2f, color1, 0f));
    spheres.add(new Sphere(new Vector3(5.1f, 3, 10), 3f, color2, 0.9f));
    spheres.add(new Sphere(new Vector3(-4, 1, 10), 1f, color4, 0.3f));
    spheres.add(new Sphere(new Vector3(0, -1000, 20), 1000f, color3, 1f));
    new Window(WIDTH, HEIGHT, "Raytracing", screen);
    start();
  }

  public void start() {
    if (running)
      return;
    thread = new Thread(this);
    thread.start();
    running = true;
  }

  public void stop() {
    if (!running)
      return;
    try {
      thread.join();
    } catch (Exception e) {
      e.printStackTrace();
    }
    running = false;
    System.exit(0);
  }

  public void run() {
    (new Thread() {
      public void run() {
        while (running) {
          screen.sendRays();
          screen.frames++;
        }
      }
    }).start();
    while (running) {
      screen.render();
      try {
        Thread.sleep(16);
      } catch (Exception e) {
      }
    }
  }

  public static void main(String[] args) {
    new Main();
  }
}
