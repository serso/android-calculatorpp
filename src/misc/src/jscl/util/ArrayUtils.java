package jscl.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

public class ArrayUtils {
    private ArrayUtils() {}

    public static Object[] concat(Object o1[], Object o2[], Object res[]) {
        System.arraycopy(o1,0,res,0,o1.length);
        System.arraycopy(o2,0,res,o1.length,o2.length);
        return res;
    }

    public static Object[] toArray(List list, Object res[]) {
//      return list.toArray(res);
        int n=list.size();
        for(int i=0;i<n;i++) res[i]=list.get(i);
        return res;
    }

    public static int[] toArray(List list, int res[]) {
        int n=list.size();
        for(int i=0;i<n;i++) res[i]=((Integer)list.get(i)).intValue();
        return res;
    }

    public static List list(Collection collection) {
//      List list=new ArrayList(collection);
        List list=new ArrayList();
        Iterator it=collection.iterator();
        while(it.hasNext()) list.add(it.next());
        return list;
    }

    public static String toString(Object obj[]) {
        StringBuffer buffer=new StringBuffer();
        buffer.append("{");
        for(int i=0;i<obj.length;i++) {
            buffer.append(obj[i]).append(i<obj.length-1?", ":"");
        }
        buffer.append("}");
        return buffer.toString();
    }

    private static final int BINARYSEARCH_THRESHOLD   = 5000;

    public static int binarySearch(List list, Object key) {
        if (list instanceof RandomAccess || list.size()<BINARYSEARCH_THRESHOLD)
            return indexedBinarySearch(list, key);
        else
            return iteratorBinarySearch(list, key);
    }

    private static int indexedBinarySearch(List list, Object key) {
        int low = 0;
        int high = list.size()-1;

        while (low <= high) {
            int mid = (low + high) >> 1;
            Object midVal = list.get(mid);
            int cmp = ((Comparable)midVal).compareTo(key);

            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found
    }

    private static int iteratorBinarySearch(List list, Object key) {
        int low = 0;
        int high = list.size()-1;
        ListIterator i = list.listIterator();

        while (low <= high) {
            int mid = (low + high) >> 1;
            Object midVal = get(i, mid);
            int cmp = ((Comparable)midVal).compareTo(key);

            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found
    }

    private static Object get(ListIterator i, int index) {
        Object obj = null;
        int pos = i.nextIndex();
        if (pos <= index) {
            do {
                obj = i.next();
            } while (pos++ < index);
        } else {
            do {
                obj = i.previous();
            } while (--pos > index);
        }
        return obj;
    }
}
