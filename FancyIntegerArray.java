import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;

//import java.awt.*;
//import java.awt.event.*;
//import java.applet.*;
//import javax.swing.*;


public class FancyIntegerArray extends Canvas implements Runnable {

  public static final int MAX_FPS = 100;
  public static final int ENFORCED_DELAY_MS = 1;

  private Frame frame;  // owning frame.

  private int length;
  private int height;
  private int[] array;  // actual data.

  // flags values that have been read/written since the last graphics update.
  private boolean[] dataRead;
  private boolean[] dataWritten;

  // count how many times certain operations have taken place.
  private int readCount = 0;
  private int writeCount = 0;
  private int compareCount = 0;
  private int swapCount = 0;

  // Sentinel for shutting down display loop.
  private boolean alive = true;

  public FancyIntegerArray(int length, int height) {
    this(length, height, "unnamed buffer");
  }

  public FancyIntegerArray(int length, int height, String name) {
    this.length = length;
    this.height = height;
    array = new int[length];

    dataRead = new boolean[length];
    dataWritten = new boolean[length];

    // Create and show parent frame.
    frame = new Frame(name);
    frame.add("Center", this);
    frame.pack();
    frame.setSize(new Dimension(length, height+25));
    frame.setVisible(true);
    
    Thread displayLoop = new Thread(this);
    displayLoop.start();
  }

  public void run() {
    createBufferStrategy(2);  // double buffering
    BufferStrategy strategy = getBufferStrategy();

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

  public void randomize() {
    Random r = new Random();
    for(int i = 0 ; i < length() ; i++) {
      write(i, r.nextInt(height));
    }
    readCount = 0;
    writeCount = 0;
    compareCount = 0;
    swapCount = 0;
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
    g.fillRect(0, 0, length + 1, height + 1);
    g.setColor(Color.white);
    for(int i = 0; i < length ; i++) {
      if (dataWritten[i]) {
        g.setColor(Color.red);        
      } else if (dataRead[i]) {
        g.setColor(Color.green);        
      } else {
        continue;
      }
      dataRead[i] = false;
      dataWritten[i] = false;
      g.drawLine(i, height, i, 0);
    }

    g.setColor(Color.white);
    for(int i = 0; i < length ; i++) {
      g.fillRect(i, height - array[i], 1, 1);
    }
  }

  public int read(int i) {
    readCount++;
    dataRead[i] = true;
    delay();
    return array[i];
  }

  public void write(int i, int x) {
    writeCount++;
    array[i] = x;
    dataWritten[i] = true;
    delay();
  }

  public boolean compare(int i , int j) {
    compareCount++;
    dataRead[i] = true;
    dataRead[j] = true;
    delay();
    return array[i] > array[j];
  }

  public void swap(int i, int j) {
    swapCount++;
    int temp = array[i];
    array[i] = array[j];
    array[j] = temp;
    dataWritten[i] = true;
    dataWritten[j] = true;
    delay();
  }

  public boolean compareAndSwap(int i, int j) {
    if (compare(i,j)) {
      swap(i,j);
      return true;
    } else {
      return false;
    }
  }

  public static void memcp(FancyIntegerArray from, int fromIndex,
                           FancyIntegerArray to, int toIndex,
                           int num) {
    // Copies a block of memory, only invoking delay() once.
    for (int i = 0; i < num; i++) {
        from.readCount++;
        from.dataRead[fromIndex+i] = true;
        to.writeCount++;
        to.dataWritten[toIndex+i] = true;
        to.array[toIndex+i] = from.array[fromIndex+i];
    }
    from.delay();
    to.delay();
  }

  public int length() {
    return length;
  }

  public int height() {
    return height;
  }

  public void printCounts() {
    if (readCount > 0)    System.out.println("DisplayState.READs:   \t" + readCount);
    if (writeCount > 0)   System.out.println("DisplayState.WRITEs:  \t" + writeCount);
    if (compareCount > 0) System.out.println("Compares:\t" + compareCount);
    if (swapCount > 0)    System.out.println("Swaps:   \t" + swapCount);
  }
}
