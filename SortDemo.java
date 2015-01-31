import SortingMethods.*;
import Framework.*;

import java.util.*;

public class SortDemo {

  public static final int ARRAY_LENGTH = 1024;
  public static final int ARRAY_HEIGHT = 768;

  static Map<String, Sorter> methods = registerSorters();

  static Map<String, Sorter> registerSorters() {
    // TODO(durandal): load all implementations of Sorter automatically, derive names.
    Map<String, Sorter> sorters = new HashMap<String, Sorter>();
    sorters.put("bidirectionalbubble", new BidirectionalBubbleSorter());
    sorters.put("comb", new CombSorter());
    sorters.put("bubble", new BubbleSorter());
    sorters.put("exchange", new ExchangeSorter());
    sorters.put("selection", new SelectionSorter());
    sorters.put("counting", new CountingSorter());
    sorters.put("merge", new MergeSorter());
    sorters.put("shaker", new ShakerSorter());
    sorters.put("insertion", new InsertionSorter());
    sorters.put("shell", new ShellSorter());
    sorters.put("heap", new HeapSorter());
    sorters.put("quick", new QuickSorter());
    sorters.put("radix", new RadixSorter());
    sorters.put("binaryradix", new BinaryRadixSorter());
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
    ia.log("Filling with random data...");
    Random r = new Random();
    for (int i = 0; i < ia.length(); i++) {
      ia.write(i, r.nextInt(ia.height()));
    }
  }

  public static void sort(IntegerArray ia, String method) {
    System.out.println("Begin sorting...");
    if (methods.containsKey(method)) methods.get(method).sort(ia);
    else if (method.equals("silly")) SortUtils.SillySort(ia);
    else if (method.equals("threestooges")) SortUtils.ThreeStoogesSort(ia);
    else usage();

    System.out.println("Done sorting.");
  }

  public static void usage() {
    System.out.println("java SortDemo <sort-method>");
    System.out.println("sort-method can be any of the following:");
    for (String method : methods.keySet()) {
        System.out.println("\t" + method);
    }
    System.out.println("\tsilly");
    System.out.println("\tthreestooges");
    System.exit(0);
  }

}
