package org.solovyev.android.view;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.text.Formatter;

/**
 * User: serso
 * Date: 8/10/12
 * Time: 12:51 AM
 */
public class DoubleRange extends NumberRange<Double> {

    public DoubleRange(@NotNull Double min,
                       @NotNull Double max,
                       @NotNull Double step,
                       int startPosition,
                       @Nullable Formatter<Double> formatter) {
        super(min, max, step, startPosition, formatter);
    }

    @NotNull
    public static NumberRange<Double> newInstance(@NotNull Double min,
                                                  @NotNull Double max,
                                                  @NotNull Double step,
                                                  @NotNull Double selected,
                                                  @Nullable Formatter<Double> formatter) {
        if (selected < min || selected > max) {
            throw new IllegalArgumentException("Selected value: " + selected + " should be >= " + min + " and <= " + max + "!");
        }

        int startPosition = 0;
        for ( double i = min; i < selected; i += step ) {
            startPosition += 1;
        }

        return new DoubleRange(min, max, step, startPosition, formatter);
    }

    @Override
    protected int getCount(@NotNull Double min, @NotNull Double max, @NotNull Double step) {
        int result = (int) ((max - min) / step);
        return result + 1;
    }

    @NotNull
    @Override
    protected Double getValueAt(int position, @NotNull Double min, @NotNull Double max, @NotNull Double step) {
        return min + position * step;
    }
}
