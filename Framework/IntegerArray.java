package Framework;

public interface IntegerArray {
  public int read(int i);
  public void write(int i, int value);
  public boolean compare(int i, int j);
  public void swap(int i, int j);

  public int length();
  public int height();

  public IntegerArray scratch(int length, int height, String name);
  public void destroy();
}
