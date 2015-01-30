import SortingMethods.*;
import Framework.*;

import java.util.*;

public class SortDemo {

  public static final int ARRAY_LENGTH = 1024;
  public static final int ARRAY_HEIGHT = 768;

  static Map<String, Sorter> methods = registerSorters();

  static Map<String, Sorter> registerSorters() {
    Map<String, Sorter> sorters = new HashMap<String, Sorter>();
    sorters.put("bidirectionalbubble", new BidirectionalBubbleSorter());
    sorters.put("comb", new CombSorter());
    sorters.put("bubble", new BubbleSorter());
    sorters.put("exchange", new ExchangeSorter());
    return java.util.Collections.unmodifiableMap(sorters);
  }

  public static void main(String[] args) {
    if (args.length == 0) usage();
    String method = args[0];

    IntegerArray ia = new AnimatedIntegerArray(ARRAY_LENGTH, ARRAY_HEIGHT, method);
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
    if (methods.containsKey(method)) methods.get(method).sort(ia);
    else if (method.equals("merge")) SortUtils.MergeSort(ia);
    else if (method.equals("heap")) SortUtils.HeapSort(ia);
    else if (method.equals("quick")) SortUtils.QuickSort(ia);
    else if (method.equals("radix")) SortUtils.RadixSort(ia);
    else if (method.equals("selection")) SortUtils.SelectionSort(ia);
    else if (method.equals("insertion")) SortUtils.InsertionSort(ia);
    else if (method.equals("shaker")) SortUtils.ShakerSort(ia);
    else if (method.equals("shell")) SortUtils.ShellSort(ia);
    else if (method.equals("silly")) SortUtils.SillySort(ia);
    else if (method.equals("threestooges")) SortUtils.ThreeStoogesSort(ia);
    else if (method.equals("counting")) SortUtils.CountingSort(ia);
    else if (method.equals("binaryradix")) SortUtils.BinaryRadixSort(ia);
    else usage();

    System.out.println("Done sorting.");
  }

  public static void usage() {
    System.out.println("java SortDemo <sort-method>");
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
