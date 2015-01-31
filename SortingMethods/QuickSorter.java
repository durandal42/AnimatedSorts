package SortingMethods;

import Framework.*;

import java.util.concurrent.*;

public class QuickSorter implements Sorter {
  public void sort(IntegerArray ia) {
    QuickSortRecurse(ia, 0, ia.length());
  }
  private static void QuickSortRecurse(final IntegerArray ia,
                                       final int left, final int right) {
    if (right - left <= 1) {
      return;
    }

    final int pivot = QuickSortPartition(ia, left, right);

    // Recursively sort each half.
    Future<Void> leftDone = SortUtils.run(new Callable<Void>() {
      public Void call() {
        QuickSortRecurse(ia, left, pivot);
        return null;
      }
    });
    Future<Void> rightDone = SortUtils.run(new Callable<Void>() {
      public Void call() {
        QuickSortRecurse(ia, pivot + 1, right);
        return null;
      }
    });
    SortUtils.join(leftDone);
    SortUtils.join(rightDone);
  }
  public static int QuickSortSelectPivotIndex(IntegerArray ia, int left, int right) {
    return (left + right) / 2;
  }
  public static int QuickSortPartition(IntegerArray ia,
                                       int left, int right) {
    ia.log("QuickSort: partitioning subarray [" + left + "," + right + ")");
    int pivot = QuickSortSelectPivotIndex(ia, left, right);

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
  }}
