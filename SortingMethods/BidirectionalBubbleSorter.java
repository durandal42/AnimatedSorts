package SortingMethods;

import Framework.*;

public class BidirectionalBubbleSorter implements Sorter {
  
  public void sort(IntegerArray ia) {
    // Bubble-like sort that alternates passes in each direction.
    int left = 0;
    int right = ia.length();
    while (left < right) {
      SortUtils.log("BidirectionalBubbleSort: right pass: remaining gap: " + (right - left));
      int lastTouched = left;
      for (int j = left; j < right - 1; j++) {
        if (SortUtils.compareAndSwap(ia, j, j+1)) {
          lastTouched = j+1;
        }
      }
      right = lastTouched;
      SortUtils.log("BidirectionalBubbleSort: left pass: remaining gap: " + (right - left));
      for (int j = right - 1; j > left; j--) {
        if (SortUtils.compareAndSwap(ia, j-1, j)) {
          lastTouched = j;
        }
      }
      left = lastTouched;
    }
  }
}
