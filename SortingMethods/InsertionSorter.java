package SortingMethods;

import Framework.*;

public class InsertionSorter implements Sorter {
  public void sort(IntegerArray ia) {
    for (int i = 1; i < ia.length(); i++) {
      SortUtils.log("InsertionSort: " + i + " of " + ia.length() + " elements are sorted");
      for (int j = i; j >= 1; j--) {
        if (!SortUtils.compareAndSwap(ia, j - 1, j)) break;
      }
    }
  }
}
