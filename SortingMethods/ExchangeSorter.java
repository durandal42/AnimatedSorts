package SortingMethods;

import Framework.*;

public class ExchangeSorter implements Sorter {
  public void sort(IntegerArray ia) {
    for (int i = 0 ; i < ia.length() - 1 ; i++) {
      ia.log("ExchangeSort: finding the " + i + "th smallest element (of " + ia.length() + ")...");
      // find the ith smallest element...
      for(int j = i + 1 ; j < ia.length() ; j++) {
        // ... by doing a CompareAndSwap on everything after it
        SortUtils.compareAndSwap(ia, i, j);
      }
    }
  }
}
