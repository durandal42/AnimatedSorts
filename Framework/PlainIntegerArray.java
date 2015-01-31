package Framework;

public class PlainIntegerArray implements IntegerArray {
  // the actual data
  private int[] array;
  private int length, height;

  public PlainIntegerArray(int length, int height) {
    this.length = length;
    this.height = height;
    array = new int[length];
  }

  public int read(int i) {
    return array[i];
  }

  public void write(int i, int value) {
    array[i] = value;
  }

  public boolean compare(int i, int j) {
    return array[i] > array[j];
  }

  public void swap(int i, int j) {
    int temp = array[i];
    array[i] = array[j];
    array[j] = temp;
  }

  public int length() {
    return length;
  }

  public int height() {
    return height;
  }

  public IntegerArray scratch(int length, int height, String name) {
    return new PlainIntegerArray(length, height);
  }

  public void destroy() {
    // nothing to do;
  }

  public void log(String message) {
    System.out.println(message);
  }

}
