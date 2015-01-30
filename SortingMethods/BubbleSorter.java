package SortingMethods;

import Framework.*;

public class BubbleSorter implements Sorter {
  public void sort(IntegerArray ia) {
    for(int i = ia.length() - 1 ; i > 0 ; i--) {
      SortUtils.log("BubbleSort: " + (i+1) + " elements remain to be sorted");
      // find the ith largest element...
      int lastTouched = 0;
      for(int j = 0 ; j < i ; j++) {
        // ... by bubbling it up to the end...
        // if any two adjacent elements are out of order, swap them
        if (SortUtils.compareAndSwap(ia, j, j+1)) {
          lastTouched = j+1;  // remember the last element we actually touched...
        }
      }
      i = lastTouched;  // ... and narrow future passes, knowing everything past it is sorted.
    }
  }
}
