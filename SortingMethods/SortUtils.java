package SortingMethods;

import Framework.*;

import java.util.concurrent.*;

public class SortUtils {

  // For multi-threaded sorts:
  static ExecutorService threadPool = Executors.newCachedThreadPool();

  static boolean PARALLEL = false;

  static <V> Future<V> run(Callable<V> c) {
    if (PARALLEL) {
      return threadPool.submit(c);
    } else {
      try {
        final V result = c.call();
        return new Future<V>() {
          public V get() {
            return result;  // return pre-calculated result.
          }
          public V get(long l, TimeUnit tu) {
            return result;  // return pre-calculated result.
          }
          public boolean isDone() {
            return true;  // already completed.
          }
          public boolean isCancelled() {
              // by the time this future exists, there is nothing to cancel.
              return false;
          }
          public boolean cancel(boolean mayInterruptIfRunning) {
            return false;  // already completed.
          }
        };
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(0);
      }
    }
    return null;  // this should never happen.
  }

  // Call get on a Future, and swallow any exceptions.
  static <V> V join(Future<V> f) {
    try {
      return f.get();
    } catch (InterruptedException e) {
      e.printStackTrace();
      System.exit(0);
    } catch (ExecutionException e) {
      e.printStackTrace();
      System.exit(0);
    }
    return null;
  }

  public static void memcp(IntegerArray from, int fromIndex,
                           IntegerArray to, int toIndex,
                           int num) {
    for (int i = 0; i < num; i++) {
        to.write(toIndex+i, from.read(fromIndex+i));
    }
  }

  public static boolean compareAndSwap(IntegerArray ia, int i, int j) {
    if (ia.compare(i,j)) {
      ia.swap(i,j);
      return true;
    } else {
      return false;
    }
  }

  public static boolean getNthBit(int x, int n) {
    return ((x & (1 << n)) != 0);
  }

  public static void SillySort(IntegerArray ia) {
    int count = 0;
    int mostFailures = 0;
    ia.log("SillySort: randomly CompareAndSwapping until " + ia.length() + " consecutive failures.");
    while(count++ < ia.length()) {
      int i = (int) (Math.random() * (double) ia.length());
      int j = (int) (Math.random() * (double) ia.length());
      if ((i > j && compareAndSwap(ia, j,i)) ||
          (i < j && compareAndSwap(ia, i,j))) {
        if (count > mostFailures) {
          ia.log("SillySort: " + count + " failed CompareAndSwaps before a success.");
          mostFailures = count;
        }
        count = 0;
      }
    }
    new InsertionSorter().sort(ia);
  }

  public static void ThreeStoogesSort(IntegerArray ia) {
    ThreeStoogesRecurse(ia, 0, ia.length());
  }
  public static void ThreeStoogesRecurse(IntegerArray ia,
                                         int left, int right) {
    if (right - left < 2) return;
    if (right - left == 2) {
      compareAndSwap(ia, left, right - 1);
      return;
    }
    int pivot1 = left + (right - left) / 3;
    int pivot2 = left + (right - left) * 2 / 3;
    ThreeStoogesRecurse(ia, left, pivot2);
    ThreeStoogesRecurse(ia, pivot1, right);
    ThreeStoogesRecurse(ia, left, pivot2);
  }

}
