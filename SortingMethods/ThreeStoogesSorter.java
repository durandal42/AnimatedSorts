package SortingMethods;

import Framework.*;

public class ThreeStoogesSorter implements Sorter {
  public void sort(IntegerArray ia) {
    ThreeStoogesRecurse(ia, 0, ia.length());
  }
  public void ThreeStoogesRecurse(IntegerArray ia,
                                  int left, int right) {
    SortUtils.compareAndSwap(ia, left, right - 1);
    int width = right - left;
    if (width > 2) {
      ThreeStoogesRecurse(ia, left, right - width / 3);
      ThreeStoogesRecurse(ia, left + width / 3, right);
      ThreeStoogesRecurse(ia, left, right - width / 3);
    }
  }
}
