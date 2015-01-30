package SortingMethods;

import Framework.*;

import java.util.concurrent.*;

public class MergeSorter implements Sorter {

  public void sort(IntegerArray ia) {
    IntegerArray scratch = ia.scratch(
        ia.length(), ia.height(), "merge scratch buffer");
    // Recurse on the entire array.
    boolean resultInScratch = MergeSortRecurse(ia, 0, ia.length(), scratch);
    if (resultInScratch) {
      SortUtils.memcp(scratch, 0, ia, 0, ia.length());
    }
    scratch.destroy();
  }
  // Assume that the data to be sorted is in the source buffer.
  // Returns whether the sorted data is now in the provided scratch buffer.
  private boolean MergeSortRecurse(final IntegerArray ia,
                                   final int left, final int right,
                                   final IntegerArray scratch) {
    if (right - left <= 1) {
      // A single element is (or no elements are) already sorted.
      return false;  // Assume data in source.
    }

    final int mid = (left + right) / 2;

    // Recursively sort each half.
    Future<Boolean> leftDone = SortUtils.run(new Callable<Boolean>() {
      public Boolean call() {
        return MergeSortRecurse(ia, left, mid, scratch);
      }
    });
    Future<Boolean> rightDone = SortUtils.run(new Callable<Boolean>() {
      public Boolean call() {
        return MergeSortRecurse(ia, mid, right, scratch);
      }
    });
    // Recursive calls tell us where their results are stored.
    boolean leftInScratch = SortUtils.join(leftDone);
    boolean rightInScratch = SortUtils.join(rightDone);

    if (leftInScratch != rightInScratch) {
      // left and right side aren't in the same place; put them both in scratch.
      if (leftInScratch) {
        // left is already in scratch; move right.
        SortUtils.memcp(ia, mid, scratch, mid, right - mid);
        rightInScratch = true;
      } else {
        // right is already in scratch; move left.
        SortUtils.memcp(ia, left, scratch, left, mid - left);
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

    return !leftInScratch;
  }

}
