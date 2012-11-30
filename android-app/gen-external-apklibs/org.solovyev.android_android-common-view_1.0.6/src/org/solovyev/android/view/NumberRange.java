package org.solovyev.android.view;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.text.Formatter;

/**
 * User: serso
 * Date: 8/9/12
 * Time: 11:44 PM
 */
public abstract class NumberRange<N extends Number & Comparable<N>> implements Picker.Range<N> {

    @Nullable
    private Formatter<N> formatter;

    @NotNull
    private final N min;

    @NotNull
    private final N max;

    @NotNull
    private final N step;

    private final int startPosition;

    private int count = -1;

    public NumberRange(@NotNull N min,
                       @NotNull N max,
                       @NotNull N step,
                       int startPosition,
                       @Nullable Formatter<N> formatter) {
        assert min.compareTo(max) <= 0;

        this.min = min;
        this.max = max;
        this.step = step;
        this.startPosition = startPosition;
        this.formatter = formatter;
    }

    @Override
    public int getStartPosition() {
        if ( this.startPosition < getCount() ) {
            return this.startPosition;
        } else {
            return getCount() - 1;
        }
    }

    @Override
    public int getCount() {
        if (count == -1) {
            count = getCount(min, max, step);
        }
        return count;
    }

    protected abstract int getCount(@NotNull N min, @NotNull N max, @NotNull N step);

    @NotNull
    @Override
    public String getStringValueAt(int position) {
        int count = getCount();
        if (position < 0 || position >= count) {
            throw new IllegalArgumentException("Position " + position + " must be >= 0 and < " + count + "!");
        }

        final N number = getValueAt(position, min, max, step);
        return formatter == null ? number.toString() : formatter.formatValue(number);
    }

    @NotNull
    @Override
    public N getValueAt(int position) {
        return getValueAt(position, min, max, step);
    }

    @NotNull
    protected abstract N getValueAt(int position, @NotNull N min, @NotNull N max, @NotNull N step);
}
