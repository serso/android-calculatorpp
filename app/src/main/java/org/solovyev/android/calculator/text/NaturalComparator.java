package org.solovyev.android.calculator.text;

import java.util.Comparator;

public class NaturalComparator implements Comparator<Object> {
    public static final NaturalComparator INSTANCE = new NaturalComparator();

    @Override
    public int compare(Object lhs, Object rhs) {
        return lhs.toString().compareTo(rhs.toString());
    }
}
