package SortingMethods;

import Framework.*;

public class ShakerSorter implements Sorter {
  public void sort(IntegerArray ia) {
    int left = 0;
    int right = ia.length() - 1;
    while (left < right) {
      ia.log("ShakerSort: remaining gap: " + (right - left));
      // Find both the min and max...
      int min = left;
      int max = left;
      for (int j = left + 1; j <= right; j++) {
        if (ia.compare(min, j)) min = j;
        if (ia.compare(j, max)) max = j;
      }

      // ... and swap them into place.
      ia.swap(min, left);
      if (max == left) {
        ia.swap(min, right);
      } else { 
        ia.swap(max, right);
      }

      left++;
      right--;
    }
  }

}
