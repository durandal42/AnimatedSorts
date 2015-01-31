package SortingMethods;

import Framework.*;

import java.util.Random;

public class SillySorter implements Sorter {
  public void sort(IntegerArray ia) {
    int failureTolerance = ia.length();
    int consecutiveFailures = 0;
    int longestStreak = 0;
    ia.log("SillySort: randomly CompareAndSwapping until " + failureTolerance + " consecutive failures");
    Random r = new Random();
    while(consecutiveFailures < failureTolerance) {
      int i = r.nextInt(ia.length());
      int j = r.nextInt(ia.length());
      if ((i > j && SortUtils.compareAndSwap(ia, j, i)) ||
          (i < j && SortUtils.compareAndSwap(ia, i, j))) {
        consecutiveFailures = 0;  // Swap succeeded.
      } else {
        consecutiveFailures++;  // Swap failed.
        if (consecutiveFailures > longestStreak) {
          longestStreak = consecutiveFailures;
          ia.log("SillySort: randomly CompareAndSwapping until " + ia.length() +
                 " consecutive failures; longest streak: " + longestStreak);
        }
      }
    }
    new InsertionSorter().sort(ia);
  }
}
