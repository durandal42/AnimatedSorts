public class PlainIntegerArray implements IntegerArray {
  // the actual data
  private int[] array;
  private int length, height;

  // count how many times certain operations have taken place.
  private int readCount = 0;
  private int writeCount = 0;
  private int compareCount = 0;
  private int swapCount = 0;

  public PlainIntegerArray(int length, int height) {
    this.length = length;
    this.height = height;
    array = new int[length];
  }

  public int read(int i) {
    readCount++;
    return array[i];
  }

  public void write(int i, int value) {
    writeCount++;
    array[i] = value;
  }

  public boolean compare(int i, int j) {
    compareCount++;
    return array[i] > array[j];
  }

  public void swap(int i, int j) {
    swapCount++;
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

  public void printCounts() {
    if (readCount > 0)    System.out.println("Reads:   \t" + readCount);
    if (writeCount > 0)   System.out.println("Writes:  \t" + writeCount);
    if (compareCount > 0) System.out.println("Compares:\t" + compareCount);
    if (swapCount > 0)    System.out.println("Swaps:   \t" + swapCount);
  }

}