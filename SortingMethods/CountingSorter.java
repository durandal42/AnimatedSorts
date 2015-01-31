package SortingMethods;

import Framework.*;

public class CountingSorter implements Sorter {

  public void sort(IntegerArray ia) {
    IntegerArray counts = ia.scratch(
        ia.height(),  // how many possible values are in the data
        10 * ia.length() / ia.height(),  // generous estimate of max count
        "element counts");

    ia.log("CountingSort: zeroing counts.");
    for (int i = 0; i < ia.height(); i++) {
      counts.write(i, 0);
    }

    ia.log("CountingSort: counting elements.");
    for (int i = 0; i < ia.length(); i++) {
      int val = ia.read(i);
      counts.write(val, counts.read(val) + 1);
    }

    ia.log("CountingSort: writing counted elements back into array.");
    for (int i = 0, j = 0; i < ia.height(); i++) {
      for (int count = counts.read(i); count > 0; count--) {
        ia.write(j++, i);
      }
    }
  }
}
