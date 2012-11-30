package org.solovyev.android.prefs;

import android.content.Context;
import android.util.AttributeSet;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.view.NumberRange;
import org.solovyev.android.view.Picker;
import org.solovyev.common.interval.Interval;
import org.solovyev.common.text.NumberIntervalMapper;

/**
 * User: serso
 * Date: 8/10/12
 * Time: 12:57 AM
 */
public abstract class NumberPickerDialogPreference<N extends Number & Comparable<N>> extends AbstractPickerDialogPreference<N> {

    @NotNull
    private final Interval<N> boundaries;

    @NotNull
    private final N step;

    protected NumberPickerDialogPreference(Context context,
                                           AttributeSet attrs,
                                           @NotNull NumberIntervalMapper<N> mapper) {
        super(context, attrs, null, false, mapper.getMapper());

        //noinspection ConstantConditions
        boundaries = mapper.parseValue(attrs.getAttributeValue(AbstractDialogPreference.localNameSpace, "boundaries"));

        final String stringStep = attrs.getAttributeValue(AbstractDialogPreference.localNameSpace, "step");
        if (stringStep == null) {
            step = getDefaultStep();
        } else {
            step = mapper.getMapper().parseValue(stringStep);
        }
    }

    @NotNull
    protected abstract N getDefaultStep();

    @NotNull
    @Override
    protected Picker.Range<N> createRange(@NotNull N selected) {
        return createRange(boundaries, step, selected);
    }

    @NotNull
    protected abstract NumberRange<N> createRange(@NotNull Interval<N> boundaries, @NotNull N step, @NotNull N selected);

}
