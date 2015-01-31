package SortingMethods;

import Framework.*;

public class ShellSorter implements Sorter {
  public void sort(IntegerArray ia) {
    int gap = ia.length();
    do {
      gap = Math.max(1, gap / 3);
      ia.log("ShellSort: InsertionSorting with a gap of: " + gap);
      for (int i = gap; i < ia.length(); i++) {
        for (int j = i; j >= gap; j -= gap) {
         if (!SortUtils.compareAndSwap(ia, j - gap, j)) break;
        }
      }
    } while (gap != 1);
  }
}
