import java.util.Random;

public class SortTest {

  public static final int ARRAY_LENGTH = 1024;
  public static final int ARRAY_HEIGHT = 768;

  public static void main(String[] args) {
    if (args.length == 0) usage();
    String method = args[0];

    IntegerArray ia = new FancyIntegerArray(ARRAY_LENGTH, ARRAY_HEIGHT, method);
    randomize(ia);

    sort(ia, method);
    System.exit(0);
  }

  public static void randomize(IntegerArray ia) {
    Random r = new Random();
    for (int i = 0; i < ia.length(); i++) {
      ia.write(i, r.nextInt(ia.height()));
    }
  }

  public static void sort(IntegerArray ia, String method) {
    System.out.println("Begin sorting...");
    if (method.equals("merge")) Sorts.MergeSort(ia);
    else if (method.equals("heap")) Sorts.HeapSort(ia);
    else if (method.equals("quick")) Sorts.QuickSort(ia);
    else if (method.equals("radix")) Sorts.RadixSort(ia);
    else if (method.equals("selection")) Sorts.SelectionSort(ia);
    else if (method.equals("insertion")) Sorts.InsertionSort(ia);
    else if (method.equals("bidirectionalbubble")) Sorts.BidirectionalBubbleSort(ia);
    else if (method.equals("shaker")) Sorts.ShakerSort(ia);
    else if (method.equals("shell")) Sorts.ShellSort(ia);
    else if (method.equals("comb")) Sorts.CombSort(ia);
    else if (method.equals("bubble")) Sorts.BubbleSort(ia);
    else if (method.equals("silly")) Sorts.SillySort(ia);
    else if (method.equals("exchange")) Sorts.ExchangeSort(ia);
    else if (method.equals("threestooges")) Sorts.ThreeStoogesSort(ia);
    else if (method.equals("counting")) Sorts.CountingSort(ia);
    else if (method.equals("binaryradix")) Sorts.BinaryRadixSort(ia);
    else usage();

    System.out.println("Done sorting.");
  }

  public static void usage() {
    System.out.println("java SortTest <sort-method>");
    System.out.println("sort-method can be any of the following:");
    System.out.println("\tmerge");
    System.out.println("\theap");
    System.out.println("\tquick");
    System.out.println("\tselection");
    System.out.println("\tinsertion");
    System.out.println("\tbidirectionalbubble");
    System.out.println("\tshaker");
    System.out.println("\tshell");
    System.out.println("\tcomb");
    System.out.println("\tbubble");
    System.out.println("\tradix");
    System.out.println("\tsilly");
    System.out.println("\texchange");
    System.out.println("\tthreestooges");
    System.out.println("\tcounting");
    System.out.println("\tbinaryradix");
    System.exit(0);
  }

}
