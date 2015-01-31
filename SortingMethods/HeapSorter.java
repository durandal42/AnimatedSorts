package SortingMethods;

import Framework.*;

public class HeapSorter implements Sorter {
  public void sort(IntegerArray ia) {
    ia.log("HeapSort: heapifying...");
    for (int i = ia.length() / 2; i > 0; i--) {
      HeapSortPush(ia, i, ia.length());
    }
    ia.log("HeapSort: popping from the heap...");
    for (int i = ia.length() - 1; i > 0; i--) {
      ia.swap(0, i);
      HeapSortPush(ia, 1, i);
    }
  }

  // Assuming that the heap-property already holds for the subtree rooted at index root,
  // except for the root itself, push the root down into the subtree such that the heap-property
  // holds for the root as well.
  // Heap-indexing logic is easier with a 1-based backing array, so note that all array accesses
  // are -1 from what you might expect.
  private static void HeapSortPush(IntegerArray ia, int root, int limit) {
    while (root <= limit / 2) {  // Any higher, and root is actually a leaf.
      int child = 2 * root;  // root's left child.
      if (child < limit && ia.compare(child, child - 1)) {
        child++;  // root's right child.
      }
      // child is now the larger child of root.
      if (SortUtils.compareAndSwap(ia, child - 1, root - 1)) {
        root = child;
        // We've pushed root down, swapping with its larger child;
        // Next time around the loop will enforce the heap-property for that child.
      } else {
        break;  // If no swap was needed, the heap-property already holds.
      }
    }
  }
}
