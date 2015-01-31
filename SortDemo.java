import SortingMethods.*;
import Framework.*;

import java.util.*;
import java.util.concurrent.*;

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
    sorters.put("silly", new SillySorter());
    sorters.put("threestooges", new ThreeStoogesSorter());
    return java.util.Collections.unmodifiableMap(sorters);
  }

  // For parallel demos.
  static ExecutorService threadPool = Executors.newCachedThreadPool();

  public static void main(String[] args) {
    if (args.length == 0) {
      //usage();
      for (String method : methods.keySet()) {
        demo(method);
      }
    } else {
      for (String method : args) {
        demo(method);
      }
    }
  }

  public static void demo(final String method) {
    if (!methods.containsKey(method)) usage();
    threadPool.submit(new Callable<Void>() {
      public Void call() {
        IntegerArray ia = new AnimatedIntegerArray(ARRAY_LENGTH, ARRAY_HEIGHT, method);
        randomize(ia);
        ia.log("Sorting: " + method);
        methods.get(method).sort(ia);
        if (verify(ia)) {
          ia.log("Success: " + method);
          ia.destroy();
        }
        return null;
      }
    });
  }

  public static void randomize(IntegerArray ia) {
    ia.log("Filling with random data...");
    Random r = new Random();
    for (int i = 0; i < ia.length(); i++) {
      ia.write(i, r.nextInt(ia.height()));
    }
  }

  public static boolean verify(IntegerArray ia) {
    ia.log("Verifying sort...");
    for (int i = 0; i < ia.length()-1; i++) {
      if (ia.compare(i, i+1)) {
        ia.log("Out-of-order elements found at " + i + "," + (i+1));
        return false;
      }
    }
    return true;
  }

  public static void usage() {
    System.out.println("java SortDemo <sort-method> [<additional sort-method> ...]");
    System.out.println("sort-method can be any of the following:");
    for (String method : methods.keySet()) {
        System.out.println("\t" + method);
    }
    System.exit(0);
  }

}
