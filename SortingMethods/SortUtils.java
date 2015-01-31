package SortingMethods;

import Framework.*;

import java.util.concurrent.*;

public class SortUtils {

  // For multi-threaded sorts:
  static ExecutorService threadPool = Executors.newCachedThreadPool();

  static boolean PARALLEL = false;

  static <V> Future<V> run(Callable<V> c) {
    if (PARALLEL) {
      return threadPool.submit(c);
    } else {
      try {
        final V result = c.call();
        return new Future<V>() {
          public V get() {
            return result;  // return pre-calculated result.
          }
          public V get(long l, TimeUnit tu) {
            return result;  // return pre-calculated result.
          }
          public boolean isDone() {
            return true;  // already completed.
          }
          public boolean isCancelled() {
              // by the time this future exists, there is nothing to cancel.
              return false;
          }
          public boolean cancel(boolean mayInterruptIfRunning) {
            return false;  // already completed.
          }
        };
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(0);
      }
    }
    return null;  // this should never happen.
  }

  // Call get on a Future, and swallow any exceptions.
  static <V> V join(Future<V> f) {
    try {
      return f.get();
    } catch (InterruptedException e) {
      e.printStackTrace();
      System.exit(0);
    } catch (ExecutionException e) {
      e.printStackTrace();
      System.exit(0);
    }
    return null;
  }

  public static void memcp(IntegerArray from, int fromIndex,
                           IntegerArray to, int toIndex,
                           int num) {
    for (int i = 0; i < num; i++) {
        to.write(toIndex+i, from.read(fromIndex+i));
    }
  }

  public static boolean compareAndSwap(IntegerArray ia, int i, int j) {
    if (ia.compare(i,j)) {
      ia.swap(i,j);
      return true;
    } else {
      return false;
    }
  }

  public static void HeapSort(IntegerArray ia) {
    ia.log("HeapSort: heapifying...");
    for (int i = ia.length() / 2; i > 0; i--) {
      HeapSortPush(ia, i, ia.length());
    }
    ia.log("HeapSort: popping from the heap...");
    for (int i = ia.length() - 1; i > 0; i--) {
      ia.swap(0, i);
      HeapSortPush(ia, 1, i);
    }
  }

  // Assuming that the heap-property already holds for the subtree rooted at index root,
  // except for the root itself, push the root down into the subtree such that the heap-property
  // holds for the root as well.
  // Heap-indexing logic is easier with a 1-based backing array, so note that all array accesses
  // are -1 from what you might expect.
  private static void HeapSortPush(IntegerArray ia, int root, int limit) {
    while (root <= limit / 2) {  // Any higher, and root is actually a leaf.
      int child = 2 * root;  // root's left child.
      if (child < limit && ia.compare(child, child - 1)) {
        child++;  // root's right child.
      }
      // child is now the larger child of root.
      if (compareAndSwap(ia, child - 1, root - 1)) {
        root = child;
        // We've pushed root down, swapping with its larger child;
        // Next time around the loop will enforce the heap-property for that child.
      } else {
        break;  // If no swap was needed, the heap-property already holds.
      }
    }
  }

  public static void QuickSort(IntegerArray ia) {
    QuickSortRecurse(ia, 0, ia.length());
  }
  private static void QuickSortRecurse(final IntegerArray ia,
                                       final int left, final int right) {
    if (right - left <= 1) {
      return;
    }

    final int pivot = QuickSortPartition(ia, left, right);

    // Recursively sort each half.
    Future<Void> leftDone = run(new Callable<Void>() {
      public Void call() {
        QuickSortRecurse(ia, left, pivot);
        return null;
      }
    });
    Future<Void> rightDone = run(new Callable<Void>() {
      public Void call() {
        QuickSortRecurse(ia, pivot + 1, right);
        return null;
      }
    });
    join(leftDone);
    join(rightDone);
  }
  public static int QuickSortSelectPivot(IntegerArray ia, int left, int right) {
    return (left + right) / 2;
  }
  public static int QuickSortPartition(IntegerArray ia,
                                       int left, int right) {
    int pivot = QuickSortSelectPivot(ia, left, right);

    ia.swap(pivot, right - 1);  // move pivot to end
    pivot = right - 1;

    right--;  // don't include pivot at end
    while (left < right) {
      if (ia.compare(left, pivot)) {
        ia.swap(left, --right);
      } else {
        left++;
      }
    }

    ia.swap(pivot, left);  // move pivot to its final place
    pivot = left;
    return pivot;
  }

  private static boolean getNthBit(int x, int n) {
    return ((x & (1 << n)) != 0);
  }
  public static void BinaryRadixSort(IntegerArray ia) {
    int bit = (int) Math.ceil(Math.log(ia.height()) / Math.log(2));
    BinaryRadixSortRecurse(ia, 0, ia.length(), bit - 1);
  }
  public static void BinaryRadixSortRecurse(final IntegerArray ia,
                                            final int left, final int right,
                                            final int bit) {
    if (bit < 0) return;
    if (right - left <= 1) return;
    final int pivot = BinaryRadixSortPartition(ia, left, right, bit);
    Future<Void> leftDone = run(new Callable<Void>() {
            public Void call() {
                BinaryRadixSortRecurse(ia, left, pivot, bit - 1);
                return null;
            }
        });
    Future<Void> rightDone = run(new Callable<Void>() {
            public Void call() {
                BinaryRadixSortRecurse(ia, pivot, right, bit - 1);
                return null;
            }
        });
    join(leftDone);
    join(rightDone);
  }
  public static int BinaryRadixSortPartition(IntegerArray ia,
                                             int left, int right,
                                             int bit) {
    while (left < right) {
      if (getNthBit(ia.read(left), bit)) {
        ia.swap(left, --right);
      } else {
        left++;
      }
    }

    return left;
  }

  public static void RadixSort(IntegerArray ia) {
    int digits = (int) Math.ceil(Math.log(ia.height()) / Math.log(2));

    iaPartition partition = new iaPartition(ia);

    for(int i = 0 ; i < digits ; i++) {
      ia.log("Starting radix pass " + i + " of " + digits);
      int count = 0;
      for(int j = 0 ; j < ia.length() ; j++) {
        int x = ia.read(j);
        partition.add(getNthBit(x, i), x);
      }
      while(!partition.isEmpty(false)) {
        ia.write(count++, partition.remove(false));
      }
      while(!partition.isEmpty(true)) {
        ia.write(count++, partition.remove(true));
      }
      partition.clear();
    }
  }
  private static class iaPartition {
    // ia-backed partition for a fixed number of elements.
    // Elements removed in FIFO order.
    IntegerArray data;

    int leftMin, leftMax, rightMin, rightMax;
 
    public iaPartition(IntegerArray ia) {
      data = ia.scratch(ia.length(), ia.height(), "partition buffer");
      clear();
    }

    void add(boolean p, int x) {
      if (p) {
        data.write(leftMax++, x);
      } else {
        data.write(--rightMax, x);
      }
    }

    int remove(boolean p) {
      if (p) {
        return data.read(leftMin++);
      } else {
        return data.read(--rightMin);
      }
    }

    boolean isEmpty(boolean p) {
      if (p) {
        return (leftMin == leftMax);
      } else {
        return (rightMin == rightMax);
      }
    }

    void clear() {
      leftMin = 0;
      leftMax = 0;
      rightMin = data.length();
      rightMax = data.length();
      data.log("clear called");
    }
  }

  public static void SillySort(IntegerArray ia) {
    int count = 0;
    int mostFailures = 0;
    ia.log("SillySort: randomly CompareAndSwapping until " + ia.length() + " consecutive failures.");
    while(count++ < ia.length()) {
      int i = (int) (Math.random() * (double) ia.length());
      int j = (int) (Math.random() * (double) ia.length());
      if ((i > j && compareAndSwap(ia, j,i)) ||
          (i < j && compareAndSwap(ia, i,j))) {
        if (count > mostFailures) {
          ia.log("SillySort: " + count + " failed CompareAndSwaps before a success.");
          mostFailures = count;
        }
        count = 0;
      }
    }
    new InsertionSorter().sort(ia);
  }

  public static void ThreeStoogesSort(IntegerArray ia) {
    ThreeStoogesRecurse(ia, 0, ia.length());
  }
  public static void ThreeStoogesRecurse(IntegerArray ia,
                                         int left, int right) {
    if (right - left < 2) return;
    if (right - left == 2) {
      compareAndSwap(ia, left, right - 1);
      return;
    }
    int pivot1 = left + (right - left) / 3;
    int pivot2 = left + (right - left) * 2 / 3;
    ThreeStoogesRecurse(ia, left, pivot2);
    ThreeStoogesRecurse(ia, pivot1, right);
    ThreeStoogesRecurse(ia, left, pivot2);
  }

}
