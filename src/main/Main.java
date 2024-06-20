package main;

import java.util.ArrayList;

import math.Vector3;
import ray.Sphere;

public class Main implements Runnable {

  public static final int WIDTH = 600, HEIGHT = WIDTH * 9 / 16;
  public Screen screen;
  public static boolean running = false;
  private Thread thread;
  public static final float viewportWidth = 1f;
  public static final float viewportHeight = viewportWidth * ((float) Main.HEIGHT / (float) Main.WIDTH);
  public static final float focalLength = 0.5f;
  public static ArrayList<Sphere> spheres = new ArrayList<>();

  public Main() {
    screen = new Screen();
    int[] color1 = { 255, 50, 60 };
    int[] color2 = { 255, 255, 255 };
    int[] color3 = { 50, 255, 100 };
    int[] color4 = { 100, 50, 100 };
    spheres.add(new Sphere(new Vector3(0, 0, 10), 2f, color1));
    spheres.add(new Sphere(new Vector3(5.1f, 0, 10), 3f, color2));
    spheres.add(new Sphere(new Vector3(-4, 0, 10), 1f, color4));
    spheres.add(new Sphere(new Vector3(0, -1005, 10), 1000f, color3));
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
    while (running) {
      screen.render();
    }
  }

  public static void main(String[] args) {
    new Main();
  }
}
