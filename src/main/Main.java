package main;

import java.util.ArrayList;

import math.Vector3;
import ray.Sphere;

public class Main implements Runnable {

  public static final int WIDTH = 800, HEIGHT = 600;
  public Screen screen;
  private boolean running = false;
  private Thread thread;
  public static final float viewportWidth = 1f;
  public static final float viewportHeight = viewportWidth * ((float) Main.HEIGHT / (float) Main.WIDTH);
  public static final float focalLength = 1f;
  public static ArrayList<Sphere> spheres = new ArrayList<>();

  public Main() {
    screen = new Screen();
    spheres.add(new Sphere(new Vector3(0, 0, 10), 2f, (100 << 8) + 255));
    spheres.add(new Sphere(new Vector3(3, -3, 30), 5f, (100 << 8) + 255));
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

  int FPS = 0;
  long lastTime = System.currentTimeMillis();

  public void run() {
    while (running) {
      screen.render();
      FPS++;
      if (System.currentTimeMillis() - lastTime >= 1000) {
        System.out.println("FPS: " + FPS);
        lastTime = System.currentTimeMillis();
        FPS = 0;
      }
    }
  }

  public static void main(String[] args) {
    new Main();
  }
}
