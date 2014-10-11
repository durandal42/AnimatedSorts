import java.util.*;

public class SortingNetwork {
  class Swap {
    int i;
    int j;
    Swap(int i, int j) {
      this.i = i;
      this.j = j;
    }
  }

  List<Swap> swaps = new LinkedList<Swap>();
  int maxIndex = 0;

  boolean valid = true;

  public void invalidate() {
    valid = false;
  }

  public boolean valid() {
    return valid;
  }

  public synchronized void add(int i, int j) {
    swaps.add(new Swap(i, j));
    if (i > maxIndex) maxIndex = i;
    if (j > maxIndex) maxIndex = j;
  }

  public synchronized List<List<Integer>> getSwapsForParallelCompareAndSwap() {
    List<List<Integer>> result = new LinkedList<List<Integer>>();

    BitSet dirty = new BitSet(maxIndex);
    while(swaps.size() > 0) {
      dirty.clear();
      List<Integer> round = new LinkedList<Integer>();
      for (Iterator<Swap> i = swaps.iterator(); i.hasNext(); ) {
        Swap s = i.next();
        if (!dirty.get(s.i) && !dirty.get(s.j)) {
          round.add(s.i);
          round.add(s.j);
          i.remove();
        }
        dirty.set(s.i);
        dirty.set(s.j);
      }
      result.add(round);
    }
    return result;
  }
}