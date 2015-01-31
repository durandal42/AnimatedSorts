package SortingMethods;

import Framework.*;

public class CombSorter implements Sorter {

  final float SHRINKFACTOR = 1.3f;

  public void sort(IntegerArray ia) {
    // Bubble-like sort that compares by progressively smaller gaps.
    boolean flipped = false;
    int gap = ia.length();
    int passes = 0;
    while (flipped || (gap > 1)) {
      gap = Math.max(1, (int) ((float) gap / SHRINKFACTOR));
      ia.log("CombSort: pass " + (passes++) + ", with gap = " + gap);
      flipped = false;
      for (int i = 0; i + gap < ia.length(); i++) {
        if (SortUtils.compareAndSwap(ia, i, i + gap)) {
          flipped = true;
        }
      }
    } 
  }

}
