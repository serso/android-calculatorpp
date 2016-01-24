package jscl.util;

import java.util.Comparator;

public class ArrayComparator implements Comparator<Comparable[]> {

    public static final Comparator<Comparable[]> comparator = new ArrayComparator();

    private ArrayComparator() {
    }

    public int compare(Comparable l[], Comparable r[]) {
        if (l.length < r.length) {
            return -1;
        } else if (l.length > r.length) {
            return 1;
        }

        for (int i = l.length - 1; i >= 0; i--) {
            if (l[i] == null && r[i] == null) {
                // continue
            } else if (l[i] != null && r[i] == null) {
                return -1;
            } else if (l[i] == null && r[i] != null) {
                return 1;
            } else if (l[i].compareTo(r[i]) < 0) {
                return -1;
            } else if (l[i].compareTo(r[i]) > 0) {
                return 1;
            }
        }

        return 0;
    }
}
