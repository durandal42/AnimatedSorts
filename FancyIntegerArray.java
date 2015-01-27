import java.util.*;
import java.awt.*;
import java.awt.image.BufferStrategy;

public class FancyIntegerArray implements IntegerArray, Runnable {

  public static final int MAX_FPS = 100;
  public static final int ENFORCED_DELAY_MS = 1;

  private Frame frame;
  private Canvas canvas;

  private IntegerArray array;  // actual data.

  // flags values that have been read/written since the last graphics update.
  private boolean[] dataRead;
  private boolean[] dataWritten;

  // Sentinel for shutting down display loop.
  private boolean alive = true;

  public FancyIntegerArray(int length, int height) {
    this(length, height, "unnamed buffer");
  }

  public FancyIntegerArray(int length, int height, String name) {
    array = new PlainIntegerArray(length, height);

    dataRead = new boolean[length];
    dataWritten = new boolean[length];

    canvas = new Canvas();

    // Create and show parent frame.
    frame = new Frame(name);
    frame.add("Center", canvas);
    frame.pack();
    frame.setSize(new Dimension(length, height+25));
    frame.setVisible(true);
    
    Thread displayLoop = new Thread(this);
    displayLoop.start();
  }

  public void run() {
    canvas.createBufferStrategy(2);  // double buffering
    BufferStrategy strategy = canvas.getBufferStrategy();

    long lastFpsUpdate = System.currentTimeMillis();
    int frames = 0;

    final long frameLength = 1000 / MAX_FPS;

    while (alive) {
      long lastFrame = System.currentTimeMillis();
      Graphics graphics = strategy.getDrawGraphics();
      paint(graphics);
      graphics.dispose();
      strategy.show();

      final int FPS_REPORT_INTERVAL = 0;  // in frames, not seconds
      frames++;
      if (FPS_REPORT_INTERVAL > 0 && frames % FPS_REPORT_INTERVAL == 0) {
        long currentTime = System.currentTimeMillis();
        System.out.println("FPS: " + (1000 * FPS_REPORT_INTERVAL / (currentTime - lastFpsUpdate)));
        lastFpsUpdate = currentTime;
      }

      long targetWakeTime = lastFrame + frameLength;
      long sleepTime = targetWakeTime - System.currentTimeMillis();
      if (sleepTime > 0) {
        try {
          Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
        // do nothing
        }
      }
    }
  }

  public void destroy() {
    alive = false;
    frame.setVisible(false);
    frame.dispose();
  }

  private void delay() {
    if (ENFORCED_DELAY_MS <= 0) return;
    try {
      Thread.sleep(ENFORCED_DELAY_MS);
    } catch (InterruptedException e) {
    }
  }

  public void paint(Graphics g) {
    g.setColor(Color.black);
    g.fillRect(0, 0, array.length() + 1, array.height() + 1);
    g.setColor(Color.white);
    for (int i = 0; i < array.length(); i++) {
      if (dataWritten[i]) {
        g.setColor(Color.red);
      } else if (dataRead[i]) {
        g.setColor(Color.green);        
      } else {
        continue;
      }
      dataRead[i] = false;
      dataWritten[i] = false;
      g.drawLine(i, array.height(), i, 0);
    }

    g.setColor(Color.white);
    for (int i = 0; i < array.length(); i++) {
      g.fillRect(i, array.height() - array.read(i), 1, 1);
    }
  }

  public int read(int i) {
    dataRead[i] = true;
    delay();
    return array.read(i);
  }

  public void write(int i, int value) {
    array.write(i, value);
    dataWritten[i] = true;
    delay();
  }

  public boolean compare(int i, int j) {
    dataRead[i] = true;
    dataRead[j] = true;
    delay();
    return array.compare(i, j);
  }

  public void swap(int i, int j) {
    array.swap(i, j);
    dataWritten[i] = true;
    dataWritten[j] = true;
    delay();
  }

  public int length() {
    return array.length();
  }

  public int height() {
    return array.height();
  }

}
