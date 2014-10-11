import java.util.concurrent.*;

public class Sorts {

  // For multi-threaded sorts:
  static ExecutorService threadPool = Executors.newCachedThreadPool();

  static boolean PARALLEL = true;

  static <V> Future<V> run(Callable<V> c) {
    return threadPool.submit(c);
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
      // find the ith smallest element...
      for(int j = i + 1 ; j < fia.length() ; j++) {
        // ... by doing a CompareAndSwap on everything after it
        fia.compareAndSwap(i,j);
      }
    }
  }

  public static void BubbleSort(FancyIntegerArray fia) {
    for(int i = fia.length() - 1 ; i > 0 ; i--) {
      // find the ith largest element...
      for(int j = 0 ; j < i ; j++) {
        // ... by bubbling it up to the end...
        // if any two adjacent elements are out of order, swap them
        fia.compareAndSwap(j, j+1);
      }
    }
  }

  public static void SelectionSort(FancyIntegerArray fia) {
    for(int i = fia.length()-1 ; i > 0 ; i--) {
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
    FancyIntegerArray counts = new FancyIntegerArray(fia.height(),
                                                     10 * fia.length() /  fia.height(),
                                                     "element counts");
    for (int i = 0; i < fia.height(); i++) {
      counts.write(i, 0);
    }
    for (int i = 0; i < fia.length(); i++) {
      int val = fia.read(i);
      counts.write(val, counts.read(val) + 1);
    }
    for (int i = 0, j = 0; i < fia.height(); i++) {
      for (int count = counts.read(i); count > 0; count--) {
        fia.write(j++, i);
      }
    }
    counts.destroy();
  }

  public static void MergeSort(FancyIntegerArray fia) {
    FancyIntegerArray scratch = new FancyIntegerArray(
        fia.length(), fia.height(), "merge scratch buffer");
    // recurse on the entire array
    boolean resultInScratch = MergeSortRecurse(fia, 0, fia.length(), scratch);
    if (resultInScratch) {
      FancyIntegerArray.memcp(scratch, 0,
                              fia, 0,
                              fia.length());
    }
    scratch.destroy();
  }
  private static boolean MergeSortRecurse(final FancyIntegerArray fia,
                                          final int left, final int right,
                                          final FancyIntegerArray scratch) {
    if (right - left <= 1) {
      // A single element is (or no elements are) already sorted.
      return false;
    }
    final int mid = (left + right) / 2;

    // Recursively sort each half.
    Future<Boolean> leftDone = run(new Callable<Boolean>() {
      public Boolean call() {
        return MergeSortRecurse(fia, left, mid, scratch);
      }
    });
    Future<Boolean> rightDone = run(new Callable<Boolean>() {
      public Boolean call() {
        return MergeSortRecurse(fia, mid, right, scratch);
      }
    });
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

    FancyIntegerArray src = fia;
    FancyIntegerArray dest = scratch;
    if (leftInScratch) {
      src = scratch;
      dest = fia;
    }

    if (src.compare(mid, mid - 1)) {
      // The entire first half is less than the entire right half; no need to merge.
      //return;
    }

    // begin merge...
    int i = left;  // index into first sub-array
    int j = mid;  // index into second sub-array
    int k = left; // index into merged array

    while (i < mid && j < right) {
      // Pick the smaller and put it into the array.
      if (src.compare(i, j)) {
        dest.write(k++, src.read(j++));
      } else {
        dest.write(k++, src.read(i++));
      }
    }

    // Copy remainder of other sub-array without comparing.
    while (i < mid) {
      dest.write(k++, src.read(i++));
    }
    while (j < right) {
      dest.write(k++, src.read(j++));
    }

    return !leftInScratch;
    // Write the merged array back into the fia.
//    FancyIntegerArray.memcp(scratch, left,
//                            fia, left,
//                            right - left);
  }

  public static void BidirectionalBubbleSort(FancyIntegerArray fia) {
    BidirectionalBubbleSort(fia, 0, fia.length());
  }
  public static void BidirectionalBubbleSort(FancyIntegerArray fia, int left, int right) {
    while (left < right) {
      for (int j = left; j < right - 1; j++) {
        if (!fia.compareAndSwap(j, j+1) && j+1 == right - 1) {
          right--;
        }
      }
      for (int j = right - 1; j > left; j--) {
        if (!fia.compareAndSwap(j-1, j) && j-1 == left) {
          left++;
        }
      }
    }
  }

  public static void ShakerSort(FancyIntegerArray fia) {
    int i = 0;
    int k = fia.length() - 1;
    while (i < k) {
      int min = i;
      int max = i;
      for (int j = i + 1; j <= k; j++) {
        if (fia.compare(min, j)) min = j;
        if (fia.compare(j, max)) max = j;
      }
      fia.swap(min, i);

      if (max == i) fia.swap(min, k);
      else fia.swap(max, k);
      i++;
      k--;
    }
  }

  public static void InsertionSort(FancyIntegerArray fia) {
    InsertionSort(fia, 0, fia.length());
  }
  public static void InsertionSort(FancyIntegerArray fia, int left, int right) {
    InsertionSort(fia, left, right, 1);
  }
  public static void InsertionSort(FancyIntegerArray fia, int left, int right, int gap) {
    for (int i = left + gap; i < right; i++) {
      for (int j = i; (j >= gap) && (fia.compare(j - gap, j)); j -= gap) {
        fia.swap(j, j - gap);
      }
    }
  }

  public static void CombSort(FancyIntegerArray fia) {
    final float SHRINKFACTOR = (float)1.3;
    boolean flipped = false;
    int top;
    int i, j;
    int gap = fia.length();
    do {
      gap = (int) ((float) gap / SHRINKFACTOR);
      if (gap < 1) gap = 1;
      flipped = false;
      top = fia.length() - gap;
      for (i = 0; i < top; i++) {
        j = i + gap;
        if (fia.compare(i,j)) {
          fia.swap(i, j);
          flipped = true;
        }
      }
    } while (flipped || (gap > 1));
  }

  public static void ShellSort(FancyIntegerArray fia) {
    int gap = fia.length() / 3;
    while(gap > 0) {
      InsertionSort(fia, 0, fia.length(), gap);
      gap /= 3;
    }
  }

  public static void HeapSort(FancyIntegerArray fia) {
    int N = fia.length();
    // Heapify.
    for (int k = N/2; k > 0; k--) {
      HeapSortPush(fia, k, N);
    }
    // Repeatedly extract highest and swap to end.
    do {
      fia.swap(0, --N);
      HeapSortPush(fia, 1, N);
    } while (N > 1);
  }

  private static void HeapSortPush(FancyIntegerArray fia, int k, int N) {
    while (k <= N/2) {
      int j = 2 * k;
      if ((j < N) && (fia.compare(j, j-1))) {
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
  }
  private static void QuickSortRecurse(final FancyIntegerArray fia,
                                       final int left, final int right) {
    final int tolerance = 8;  // Smaller than this will get insertion sorted.
    if (right - left < tolerance) {
      InsertionSort(fia, left, right);
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
      System.out.println("Starting radix pass " + i + " of " + digits);
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
      System.out.println("clear called");
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
