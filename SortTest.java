public class SortTest {

  public static final int ARRAY_LENGTH = 1024;
  public static final int ARRAY_HEIGHT = 768;

  public static void main(String args[]) {
    if (args.length == 0) usage();
    String method = args[0];

    FancyIntegerArray fia = new FancyIntegerArray(ARRAY_LENGTH, ARRAY_HEIGHT, method);
    fia.randomize();

    sort(fia, method);
    fia.destroy();
    System.exit(0);
  }

  public static void sort(FancyIntegerArray fia, String method) {
    System.out.println("Begin sorting...");
    if (method.equals("merge")) Sorts.MergeSort(fia);
    else if (method.equals("heap")) Sorts.HeapSort(fia);
    else if (method.equals("quick")) Sorts.QuickSort(fia);
    else if (method.equals("radix")) Sorts.RadixSort(fia);
    else if (method.equals("selection")) Sorts.SelectionSort(fia);
    else if (method.equals("insertion")) Sorts.InsertionSort(fia);
    else if (method.equals("bidirectionalbubble")) Sorts.BidirectionalBubbleSort(fia);
    else if (method.equals("shaker")) Sorts.ShakerSort(fia);
    else if (method.equals("shell")) Sorts.ShellSort(fia);
    else if (method.equals("comb")) Sorts.CombSort(fia);
    else if (method.equals("bubble")) Sorts.BubbleSort(fia);
    else if (method.equals("silly")) Sorts.SillySort(fia);
    else if (method.equals("exchange")) Sorts.ExchangeSort(fia);
    else if (method.equals("threestooges")) Sorts.ThreeStoogesSort(fia);
    else if (method.equals("counting")) Sorts.CountingSort(fia);
    else if (method.equals("binaryradix")) Sorts.BinaryRadixSort(fia);
    else usage();

    System.out.println("Done sorting.");
    fia.printCounts();
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
