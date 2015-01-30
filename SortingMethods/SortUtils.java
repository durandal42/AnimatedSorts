package SortingMethods;

import Framework.*;

import java.util.concurrent.*;

public class SortUtils {

  public static void log(String s) {
    System.out.println(s);
  }

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

  public static void SelectionSort(IntegerArray ia) {
    for(int i = ia.length()-1 ; i > 0 ; i--) {
      log("SelectionSort: finding the " + (ia.length() - i - 1) + "the largest element (of " +
          ia.length() + ")...");
      // find the ith largest element...
      int maxIndex = 0;
      for(int j = 1 ; j <= i ; j++) {
        // ... by comparing it with the current max
        if (ia.compare(j, maxIndex)) {
          maxIndex = j;
        }
      }
      // ... then swap it to the end
      ia.swap(i, maxIndex);
    }
  }

  public static void CountingSort(IntegerArray ia) {
    IntegerArray counts = ia.scratch(
        ia.height(),  // how many possible values are in the data
        10 * ia.length() / ia.height(),  // generous estimate of max count
        "element counts");

    log("CountingSort: zeroing counts.");
    for (int i = 0; i < ia.height(); i++) {
      counts.write(i, 0);
    }

    log("CountingSort: counting elements.");
    for (int i = 0; i < ia.length(); i++) {
      int val = ia.read(i);
      counts.write(val, counts.read(val) + 1);
    }

    log("CountingSort: writing counted elements back into array.");
    for (int i = 0, j = 0; i < ia.height(); i++) {
      for (int count = counts.read(i); count > 0; count--) {
        ia.write(j++, i);
      }
    }
  }

  static int maxDepthReached;
  static int minDepthReturned;
  static void resetDepth() {
    maxDepthReached = Integer.MIN_VALUE;
    minDepthReturned = Integer.MAX_VALUE;
  }
  static void depthReached(int depth, String label) {
    if (depth > maxDepthReached) {
      log(label + ": new maximum depth reached: " + depth);
      maxDepthReached = depth;
    }
  }
  static void depthReturned(int depth, String label) {
    if (depth < minDepthReturned) {
      log(label + ": new minimum depth returned from: " + depth);
      minDepthReturned = depth;
    }
  }

  public static void MergeSort(IntegerArray ia) {
    IntegerArray scratch = ia.scratch(
        ia.length(), ia.height(), "merge scratch buffer");
    resetDepth();
    // Recurse on the entire array.
    boolean resultInScratch = MergeSortRecurse(ia, 0, ia.length(), scratch, 0);
    if (resultInScratch) {
      memcp(scratch, 0,
            ia, 0,
            ia.length());
    }
  }
  // Assume that the data to be sorted is in the source buffer.
  // Returns whether the sorted data is now in the provided scratch buffer.
  private static boolean MergeSortRecurse(final IntegerArray ia,
                                          final int left, final int right,
                                          final IntegerArray scratch,
                                          final int depth) {
    depthReached(depth, "MergeSort");

    if (right - left <= 1) {
      // A single element is (or no elements are) already sorted.
      return false;  // Assume data in source.
    }

    final int mid = (left + right) / 2;

    // Recursively sort each half.
    Future<Boolean> leftDone = run(new Callable<Boolean>() {
      public Boolean call() {
        return MergeSortRecurse(ia, left, mid, scratch, depth + 1);
      }
    });
    Future<Boolean> rightDone = run(new Callable<Boolean>() {
      public Boolean call() {
        return MergeSortRecurse(ia, mid, right, scratch, depth + 1);
      }
    });
    // Recursive calls tell us where their results are stored.
    boolean leftInScratch = join(leftDone);
    boolean rightInScratch = join(rightDone);

    if (leftInScratch != rightInScratch) {
      // left and right side aren't in the same place; put them both in scratch.
      if (leftInScratch) {
        // left is already in scratch; move right.
        memcp(ia, mid,
              scratch, mid,
              right - mid);
        rightInScratch = true;
      } else {
        // right is already in scratch; move left.
        memcp(ia, left,
              scratch, left,
              mid - left);
        leftInScratch = true;
      }
    }
    // left and right are now in the same place.

    IntegerArray mergeFrom = ia;
    IntegerArray mergeTo = scratch;
    if (leftInScratch) {
      // Data is in scratch; merge it back into source buffer.
      mergeFrom = scratch;
      mergeTo = ia;
    }

    if (mergeFrom.compare(mid, mid - 1)) {
      // The entire first half is less than the entire right half; no need to merge.
      depthReturned(depth, "MergeSort");
      return leftInScratch;  // We didn't actually move the data.
    }

    // begin merge...
    int i = left;  // index into first sub-array
    int j = mid;  // index into second sub-array
    int k = left; // index into merged array

    while (i < mid && j < right) {
      // Pick the smaller and put it into the array.
      if (mergeFrom.compare(i, j)) {
        mergeTo.write(k++, mergeFrom.read(j++));
      } else {
        mergeTo.write(k++, mergeFrom.read(i++));
      }
    }

    // Copy remainder of other sub-array without comparing.
    while (i < mid) {
      mergeTo.write(k++, mergeFrom.read(i++));
    }
    while (j < right) {
      mergeTo.write(k++, mergeFrom.read(j++));
    }

    depthReturned(depth, "MergeSort");
    return !leftInScratch;
  }

  public static void ShakerSort(IntegerArray ia) {
    int left = 0;
    int right = ia.length() - 1;
    while (left < right) {
      log("ShakerSort: remaining gap: " + (right - left));
      // Find both the min and max...
      int min = left;
      int max = left;
      for (int j = left + 1; j <= right; j++) {
        if (ia.compare(min, j)) min = j;
        if (ia.compare(j, max)) max = j;
      }

      // ... and swap them into place.
      ia.swap(min, left);
      if (max == left) {
        ia.swap(min, right);
      } else { 
        ia.swap(max, right);
      }

      left++;
      right--;
    }
  }

  public static void InsertionSort(IntegerArray ia) {
    for (int i = 1; i < ia.length(); i++) {
      log("InsertionSort: " + i + " of " + ia.length() + " elements are sorted");
      for (int j = i; j >= 1; j--) {
        if (!compareAndSwap(ia, j - 1, j)) break;
      }
    }
  }

  public static void ShellSort(IntegerArray ia) {
    int gap = ia.length();
    do {
      gap = Math.max(1, gap / 3);
      log("ShellSort: InsertionSorting with a gap of: " + gap);
      for (int i = gap; i < ia.length(); i++) {
        for (int j = i; j >= gap; j -= gap) {
         if (!compareAndSwap(ia, j - gap, j)) break;
        }
      }
    } while (gap != 1);
  }

  public static void HeapSort(IntegerArray ia) {
    log("HeapSort: heapifying...");
    for (int i = ia.length() / 2; i > 0; i--) {
      HeapSortPush(ia, i, ia.length());
    }
    log("HeapSort: popping from the heap...");
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
    resetDepth();
    QuickSortRecurse(ia, 0, ia.length(), 0);
  }
  private static void QuickSortRecurse(final IntegerArray ia,
                                       final int left, final int right,
                                       final int depth) {
    depthReached(depth, "QuickSort");
    if (right - left <= 1) {
      return;
    }

    final int pivot = QuickSortPartition(ia, left, right);

    // Recursively sort each half.
    Future<Void> leftDone = run(new Callable<Void>() {
      public Void call() {
        QuickSortRecurse(ia, left, pivot, depth + 1);
        return null;
      }
    });
    Future<Void> rightDone = run(new Callable<Void>() {
      public Void call() {
        QuickSortRecurse(ia, pivot + 1, right, depth + 1);
        return null;
      }
    });
    join(leftDone);
    join(rightDone);
    depthReturned(depth, "QuickSort");
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
    resetDepth();
    BinaryRadixSortRecurse(ia, 0, ia.length(), bit - 1, 0);
  }
  public static void BinaryRadixSortRecurse(final IntegerArray ia,
                                            final int left, final int right,
                                            final int bit,
                                            final int depth) {
    depthReached(depth, "BinaryRadixSort");
    if (bit < 0) return;
    if (right - left <= 1) return;
    final int pivot = BinaryRadixSortPartition(ia, left, right, bit);
    Future<Void> leftDone = run(new Callable<Void>() {
            public Void call() {
                BinaryRadixSortRecurse(ia, left, pivot, bit - 1, depth + 1);
                return null;
            }
        });
    Future<Void> rightDone = run(new Callable<Void>() {
            public Void call() {
                BinaryRadixSortRecurse(ia, pivot, right, bit - 1, depth + 1);
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
      log("Starting radix pass " + i + " of " + digits);
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
      log("clear called");
    }
  }

  public static void SillySort(IntegerArray ia) {
    int count = 0;
    int mostFailures = 0;
    log("SillySort: randomly CompareAndSwapping until " + ia.length() + " consecutive failures.");
    while(count++ < ia.length()) {
      int i = (int) (Math.random() * (double) ia.length());
      int j = (int) (Math.random() * (double) ia.length());
      if ((i > j && compareAndSwap(ia, j,i)) ||
          (i < j && compareAndSwap(ia, i,j))) {
        if (count > mostFailures) {
          log("SillySort: " + count + " failed CompareAndSwaps before a success.");
          mostFailures = count;
        }
        count = 0;
      }
    }
    InsertionSort(ia);
  }

  public static void ThreeStoogesSort(IntegerArray ia) {
    resetDepth();
    ThreeStoogesRecurse(ia, 0, ia.length(), 0);
  }
  public static void ThreeStoogesRecurse(IntegerArray ia,
                                         int left, int right,
                                         int depth) {
    depthReached(depth, "ThreeStoogesSort");
    if (right - left < 2) return;
    if (right - left == 2) {
      compareAndSwap(ia, left, right - 1);
      return;
    }
    int pivot1 = left + (right - left) / 3;
    int pivot2 = left + (right - left) * 2 / 3;
    ThreeStoogesRecurse(ia, left, pivot2, depth + 1);
    ThreeStoogesRecurse(ia, pivot1, right, depth + 1);
    ThreeStoogesRecurse(ia, left, pivot2, depth + 1);
    depthReturned(depth, "ThreeStoogesSort");
  }

}