package SortingMethods;

import Framework.*;

public class CountingSorter implements Sorter {

  public void sort(IntegerArray ia) {
    IntegerArray counts = ia.scratch(
        ia.height(),  // how many possible values are in the data
        10 * ia.length() / ia.height(),  // generous estimate of max count
        "element counts");

    counts.log("CountingSort: zeroing counts.");
    ia.log("CountingSort");
    for (int i = 0; i < ia.height(); i++) {
      counts.write(i, 0);
    }

    counts.log("CountingSort: accumulating counts.");
    ia.log("CountingSort: scanning elements.");
    for (int i = 0; i < ia.length(); i++) {
      int val = ia.read(i);
      counts.write(val, counts.read(val) + 1);
    }

    counts.log("CountingSort: scanning counts.");
    ia.log("CountingSort: writing counted elements.");
    for (int i = 0, j = 0; i < ia.height(); i++) {
      for (int count = counts.read(i); count > 0; count--) {
        ia.write(j++, i);
      }
    }
  }
}
