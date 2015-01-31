package SortingMethods;

import Framework.*;

public class SelectionSorter implements Sorter {

  public void sort(IntegerArray ia) {
    for(int i = ia.length()-1 ; i > 0 ; i--) {
      ia.log("SelectionSort: finding the " + (ia.length() - i - 1) +
                    "the largest element (of " + ia.length() + ")...");
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

}
