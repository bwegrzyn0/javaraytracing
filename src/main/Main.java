package main;

public class Main implements Runnable {

  public static final int WIDTH = 800, HEIGHT = 600;
  public Screen screen;
  private boolean running = false;
  private Thread thread;
  public static final float viewportWidth = 1f;
  public static final float viewportHeight = viewportWidth * ((float) Main.HEIGHT / (float) Main.WIDTH);
  public static final float focalLength = 1f;

  public Main() {
    screen = new Screen();
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
