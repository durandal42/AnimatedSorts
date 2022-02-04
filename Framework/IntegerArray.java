package Framework;

public interface IntegerArray {
  // How wide is this array:
  public int length();
  // What's the largest element this array can hold?
  // (useful for some non-comparison-based sorting algorithms.)
  public int height();

  // All a comparison-based sort will need:
  public boolean compare(int i, int j);
  public void swap(int i, int j);

  // For algorithms that need to know actual values:
  public int read(int i);
  public void write(int i, int value);

  // For algorithms that need scratch space:
  public IntegerArray scratch(int length, int height, String name);
  public void destroy();

  // Displays a message.
  public void log(String message);
}
