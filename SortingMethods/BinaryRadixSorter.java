package SortingMethods;

import Framework.*;

import java.util.concurrent.*;

public class BinaryRadixSorter implements Sorter {
  public void sort(IntegerArray ia) {
    int bit = (int) Math.ceil(Math.log(ia.height()) / Math.log(2));
    BinaryRadixSortRecurse(ia, 0, ia.length(), bit - 1);
  }
  public static void BinaryRadixSortRecurse(final IntegerArray ia,
                                            final int left, final int right,
                                            final int bit) {
    if (bit < 0) return;
    if (right - left <= 1) return;
    final int pivot = BinaryRadixSortPartition(ia, left, right, bit);
    Future<Void> leftDone = SortUtils.run(new Callable<Void>() {
            public Void call() {
                BinaryRadixSortRecurse(ia, left, pivot, bit - 1);
                return null;
            }
        });
    Future<Void> rightDone = SortUtils.run(new Callable<Void>() {
            public Void call() {
                BinaryRadixSortRecurse(ia, pivot, right, bit - 1);
                return null;
            }
        });
    SortUtils.join(leftDone);
    SortUtils.join(rightDone);
  }
  public static int BinaryRadixSortPartition(IntegerArray ia,
                                             int left, int right,
                                             int bit) {
    ia.log("BinaryRadixSort: partitioning subarray [" + left + "," + right + ")");
    while (left < right) {
      if (SortUtils.getNthBit(ia.read(left), bit)) {
        ia.swap(left, --right);
      } else {
        left++;
      }
    }

    return left;
  }
}
