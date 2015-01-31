package SortingMethods;

import Framework.*;

public class RadixSorter implements Sorter {
  public void sort(IntegerArray ia) {
    int digits = (int) Math.ceil(Math.log(ia.height()) / Math.log(2));

    IntegerArray scratch = ia.scratch(ia.length(), ia.height(), "partition buffer");
    iaPartition partition = new iaPartition(scratch);

    for(int i = 0 ; i < digits ; i++) {
      int count = 0;
      ia.log("RadixSort: read pass " + i + " of " + digits);
      scratch.log("RadixSort: filling binary partition.");
      for(int j = 0 ; j < ia.length() ; j++) {
        int x = ia.read(j);
        partition.add(SortUtils.getNthBit(x, i), x);
      }
      ia.log("RadixSort: write pass " + i + " of " + digits);
      scratch.log("RadixSort: reading binary partition[0].");
      while(!partition.isEmpty(false)) {
        ia.write(count++, partition.remove(false));
      }
      scratch.log("RadixSort: reading binary partition[1].");
      while(!partition.isEmpty(true)) {
        ia.write(count++, partition.remove(true));
      }
      partition.clear();
    }
    scratch.destroy();
  }
  private static class iaPartition {
    // ia-backed partition for a fixed number of elements.
    // Elements removed in FIFO order.
    IntegerArray data;

    int leftMin, leftMax, rightMin, rightMax;
 
    public iaPartition(IntegerArray scratch) {
      data = scratch;
      clear();
    }

    void add(boolean p, int x) {
      if (p) {
        data.write(leftMax++, x);
      } else {
        data.write(--rightMax, x);
      }
    }

    int remove(boolean p) {
      if (p) {
        return data.read(leftMin++);
      } else {
        return data.read(--rightMin);
      }
    }

    boolean isEmpty(boolean p) {
      if (p) {
        return (leftMin == leftMax);
      } else {
        return (rightMin == rightMax);
      }
    }

    void clear() {
      leftMin = 0;
      leftMax = 0;
      rightMin = data.length();
      rightMax = data.length();
    }
  }
}