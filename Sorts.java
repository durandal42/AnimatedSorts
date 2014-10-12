import java.util.concurrent.*;

public class Sorts {

  static void log(String s) {
    System.out.println(s);
  }

  // For multi-threaded sorts:
  static ExecutorService threadPool = Executors.newCachedThreadPool();

  static boolean PARALLEL = true;

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

  public static void ExchangeSort(FancyIntegerArray fia) {
    for (int i = 0 ; i < fia.length() - 1 ; i++) {
      log("ExchangeSort: finding the " + i + "th smallest element (of " + fia.length() + ")...");
      // find the ith smallest element...
      for(int j = i + 1 ; j < fia.length() ; j++) {
        // ... by doing a CompareAndSwap on everything after it
        fia.compareAndSwap(i,j);
      }
    }
  }

  public static void BubbleSort(FancyIntegerArray fia) {
    for(int i = fia.length() - 1 ; i > 0 ; i--) {
      log("BubbleSort: " + (i+1) + " elements remain to be sorted");
      // find the ith largest element...
      int lastTouched = 0;
      for(int j = 0 ; j < i ; j++) {
        // ... by bubbling it up to the end...
        // if any two adjacent elements are out of order, swap them
        if (fia.compareAndSwap(j, j+1)) {
          lastTouched = j+1;  // remember the last element we actually touched...
        }
      }
      i = lastTouched;  // ... and narrow future passes, knowing everything past it is sorted.
    }
  }

  public static void BidirectionalBubbleSort(FancyIntegerArray fia) {
    // Bubble-like sort that alternates passes in each direction.
    int left = 0;
    int right = fia.length();
    while (left < right) {
      log("BidirectionalBubbleSort: right pass: remaining gap: " + (right - left));
      int lastTouched = left;
      for (int j = left; j < right - 1; j++) {
        if (fia.compareAndSwap(j, j+1)) {
          lastTouched = j+1;
        }
      }
      right = lastTouched;
      log("BidirectionalBubbleSort: left pass: remaining gap: " + (right - left));
      for (int j = right - 1; j > left; j--) {
        if (fia.compareAndSwap(j-1, j)) {
          lastTouched = j;
        }
      }
      left = lastTouched;
    }
  }

  public static void CombSort(FancyIntegerArray fia) {
    // Bubble-like sort that compares by progressively smaller gaps.
    final float SHRINKFACTOR = 1.3f;
    boolean flipped = false;
    int gap = fia.length();
    int passes = 0;
    while (flipped || (gap > 1)) {
      gap = Math.max(1, (int) ((float) gap / SHRINKFACTOR));
      log("CombSort: pass " + (passes++) + ", with gap = " + gap);
      flipped = false;
      for (int i = 0; i + gap < fia.length(); i++) {
        if (fia.compareAndSwap(i, i + gap)) {
          flipped = true;
        }
      }
    } 
  }

  public static void SelectionSort(FancyIntegerArray fia) {
    for(int i = fia.length()-1 ; i > 0 ; i--) {
      log("SelectionSort: finding the " + (fia.length() - i - 1) + "the largest element (of " +
          fia.length() + ")...");
      // find the ith largest element...
      int maxIndex = 0;
      for(int j = 1 ; j <= i ; j++) {
        // ... by comparing it with the current max
        if (fia.compare(j, maxIndex)) {
          maxIndex = j;
        }
      }
      // ... then swap it to the end
      fia.swap(i, maxIndex);
    }
  }

  public static void CountingSort(FancyIntegerArray fia) {
    FancyIntegerArray counts = new FancyIntegerArray(
        fia.height(),  // how many possible values are in the data
        10 * fia.length() / fia.height(),  // generous estimate of max count
        "element counts");

    log("CountingSort: zeroing counts.");
    for (int i = 0; i < fia.height(); i++) {
      counts.write(i, 0);
    }

    log("CountingSort: counting elements.");
    for (int i = 0; i < fia.length(); i++) {
      int val = fia.read(i);
      counts.write(val, counts.read(val) + 1);
    }

    log("CountingSort: writing counted elements back into array.");
    for (int i = 0, j = 0; i < fia.height(); i++) {
      for (int count = counts.read(i); count > 0; count--) {
        fia.write(j++, i);
      }
    }
    counts.destroy();
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

  public static void MergeSort(FancyIntegerArray fia) {
    FancyIntegerArray scratch = new FancyIntegerArray(
        fia.length(), fia.height(), "merge scratch buffer");
    resetDepth();
    // Recurse on the entire array.
    boolean resultInScratch = MergeSortRecurse(fia, 0, fia.length(), scratch, 0);
    if (resultInScratch) {
      FancyIntegerArray.memcp(scratch, 0,
                              fia, 0,
                              fia.length());
    }
    scratch.destroy();
  }
  // Assume that the data to be sorted is in the source buffer.
  // Returns whether the sorted data is now in the provided scratch buffer.
  private static boolean MergeSortRecurse(final FancyIntegerArray fia,
                                          final int left, final int right,
                                          final FancyIntegerArray scratch,
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
        return MergeSortRecurse(fia, left, mid, scratch, depth + 1);
      }
    });
    Future<Boolean> rightDone = run(new Callable<Boolean>() {
      public Boolean call() {
        return MergeSortRecurse(fia, mid, right, scratch, depth + 1);
      }
    });
    // Recursive calls tell us where their results are stored.
    boolean leftInScratch = join(leftDone);
    boolean rightInScratch = join(rightDone);

    if (leftInScratch != rightInScratch) {
      // left and right side aren't in the same place; put them both in scratch.
      if (leftInScratch) {
        // left is already in scratch; move right.
        FancyIntegerArray.memcp(fia, mid,
                                scratch, mid,
                                right - mid);
        rightInScratch = true;
      } else {
        // right is already in scratch; move left.
        FancyIntegerArray.memcp(fia, left,
                                scratch, left,
                                mid - left);
        leftInScratch = true;
      }
    }
    // left and right are now in the same place.

    FancyIntegerArray mergeFrom = fia;
    FancyIntegerArray mergeTo = scratch;
    if (leftInScratch) {
      // Data is in scratch; merge it back into source buffer.
      mergeFrom = scratch;
      mergeTo = fia;
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

  public static void ShakerSort(FancyIntegerArray fia) {
    int left = 0;
    int right = fia.length() - 1;
    while (left < right) {
      log("ShakerSort: remaining gap: " + (right - left));
      // Find both the min and max...
      int min = left;
      int max = left;
      for (int j = left + 1; j <= right; j++) {
        if (fia.compare(min, j)) min = j;
        if (fia.compare(j, max)) max = j;
      }

      // ... and swap them into place.
      fia.swap(min, left);
      if (max == left) {
        fia.swap(min, right);
      } else { 
        fia.swap(max, right);
      }

      left++;
      right--;
    }
  }

  public static void InsertionSort(FancyIntegerArray fia) {
    for (int i = 1; i < fia.length(); i++) {
      log("InsertionSort: " + i + " of " + fia.length() + " elements are sorted");
      for (int j = i; j >= 1; j--) {
        if (!fia.compareAndSwap(j - 1, j)) break;
      }
    }
  }

  public static void ShellSort(FancyIntegerArray fia) {
    int gap = fia.length() / 3;
    while(gap > 0) {
      log("ShellSort: InsertionSorting with a gap of: " + gap);
      for (int i = gap; i < fia.length(); i++) {
        for (int j = i; j >= gap; j -= gap) {
         if (!fia.compareAndSwap(j - gap, j)) break;
        }
      }
      gap /= 3;
    }
  }

  public static void HeapSort(FancyIntegerArray fia) {
    int n = fia.length();
    log("HeapSort: heapifying...")
    for (int k = n/2; k > 0; k--) {
      HeapSortPush(fia, k, n);
    }
    log("HeapSort: popping from the heap...")
    // Repeatedly extract highest and swap to end.
    do {
      fia.swap(0, --n);
      HeapSortPush(fia, 1, n);
    } while (n > 1);
  }

  private static void HeapSortPush(FancyIntegerArray fia, int k, int n) {
    while (k <= n/2) {
      int j = 2 * k;
      if ((j < n) && (fia.compare(j, j-1))) {
        j++;  // j now points at the larger child
      }
      if (fia.compare(k-1, j-1)) {
        break;  // heap property holds
      }
      else {
        fia.swap(k-1, j-1);  // swap with larger child
        k = j;
      }
    }
  }

  public static void QuickSort(FancyIntegerArray fia) {
    QuickSortRecurse(fia, 0, fia.length());
    InsertionSort(fia);
  }
  private static void QuickSortRecurse(final FancyIntegerArray fia,
                                       final int left, final int right) {
    final int TOLERANCE = 8;  // Smaller than this will get insertion sorted.
    if (right - left < TOLERANCE) {
      return;
    }

    final int pivot = QuickSortPartition(fia, left, right);

    // Recursively sort each half.
    Future<Void> leftDone = run(new Callable<Void>() {
      public Void call() {
        QuickSortRecurse(fia, left, pivot);
        return null;
      }
    });
    Future<Void> rightDone = run(new Callable<Void>() {
      public Void call() {
        QuickSortRecurse(fia, pivot + 1, right);
        return null;
      }
    });
    join(leftDone);
    join(rightDone);
  }
  public static int QuickSortSelectPivot(FancyIntegerArray fia, int left, int right) {
    return (left + right) / 2;
  }
  public static int QuickSortPartition(FancyIntegerArray fia,
                                       int left, int right) {
    int pivot = QuickSortSelectPivot(fia, left, right);

    fia.swap(pivot, right - 1);  // move pivot to end
    pivot = right - 1;

    right--;  // don't include pivot at end
    while (left < right) {
      if (fia.compare(left, pivot)) {
        fia.swap(left, --right);
      } else {
        left++;
      }
    }

    fia.swap(pivot, left);  // move pivot to its final place
    pivot = left;
    return pivot;
  }

  public static void BinaryRadixSort(FancyIntegerArray fia) {
    int bit = (int) Math.ceil(Math.log(fia.height()) / Math.log(2));
    BinaryRadixSortRecurse(fia, 0, fia.length(), bit-1);
  }
  public static void BinaryRadixSortRecurse(final FancyIntegerArray fia,
                                            final int left, final int right,
                                            final int bit) {
    if (bit < 0) return;
    if (right - left <= 1) return;
    final int pivot = BinaryRadixSortPartition(fia, left, right, bit);
    Future<Void> leftDone = run(new Callable<Void>() {
            public Void call() {
                BinaryRadixSortRecurse(fia, left, pivot, bit-1);
                return null;
            }
        });
    join(leftDone);  // If *after* the second recursive call, will parallelize.
    Future<Void> rightDone = run(new Callable<Void>() {
            public Void call() {
                BinaryRadixSortRecurse(fia, pivot, right, bit-1);
                return null;
            }
        });
    join(leftDone);
    join(rightDone);
  }
  public static int BinaryRadixSortPartition(FancyIntegerArray fia,
                                             int left, int right,
                                             int bit) {
    while (left < right) {
      if ((fia.read(left) & (1 << bit)) != 0) {
        fia.swap(left, --right);
      } else {
        left++;
      }
    }

    return left;
  }

  public static void RadixSort(FancyIntegerArray fia) {
    int digits = (int) Math.ceil(Math.log(fia.height()) / Math.log(2));

    FiaPartition partition = new FiaPartition(fia.length(), fia.height());

    for(int i = 0 ; i < digits ; i++) {
      log("Starting radix pass " + i + " of " + digits);
      int count = 0;
      for(int j = 0 ; j < fia.length() ; j++) {
        int x = fia.read(j);
        partition.add(getNthBit(x, i), x);
      }
      while(!partition.isEmpty(true)) {
        fia.write(count++, partition.remove(true));
      }
      while(!partition.isEmpty(false)) {
        fia.write(count++, partition.remove(false));
      }
      partition.clear();
    }

    partition.destroy();
  }
  private static boolean getNthBit(int x, int n) {
    return ((x & (1 << n)) != 0);
  }
  private static class FiaPartition {
    // FIA-backed partition for a fixed number of elements.
    // Elements removed in FIFO order.
    FancyIntegerArray data;

    int leftMin, leftMax, rightMin, rightMax;
 
    public FiaPartition(int length, int height) {
      data = new FancyIntegerArray(length, height, "partition buffer");
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

    void destroy() {
      data.destroy();
    }
  }

  public static void SillySort(FancyIntegerArray fia) {
    int count = 0;
    while(count++ < fia.length()) {
      int i = (int) (Math.random() * (double) fia.length());
      int j = (int) (Math.random() * (double) fia.length());
      if ((i > j && fia.compareAndSwap(j,i)) ||
          (i < j && fia.compareAndSwap(i,j))) {
        count = 0;
      }
    }
    InsertionSort(fia);
  }

  public static void ThreeStoogesSort(FancyIntegerArray fia) {
    ThreeStoogesRecurse(fia, 0, fia.length());
  }
  public static void ThreeStoogesRecurse(FancyIntegerArray fia, int left, int right) {
    if (right - left < 2) return;
    if (right - left == 2) {
      fia.compareAndSwap(left, right - 1);
      return;
    }
    int pivot1 = left + (right - left)/3;
    int pivot2 = left + (right - left)*2/3;
    ThreeStoogesRecurse(fia, left, pivot2);
    ThreeStoogesRecurse(fia, pivot1, right);
    ThreeStoogesRecurse(fia, left, pivot2);
  }

}
