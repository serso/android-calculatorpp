package org.solovyev.android.prefs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.view.AbstractRangeSeekBar;
import org.solovyev.android.view.NumberRangeSeekBar;
import org.solovyev.common.interval.Interval;
import org.solovyev.common.interval.IntervalImpl;
import org.solovyev.common.text.NumberIntervalMapper;

/**
 * User: serso
 * Date: 9/19/11
 * Time: 12:27 PM
 */
public abstract class RangeSeekBarPreference<T extends Number & Comparable<T>> extends AbstractDialogPreference<Interval<T>> implements AbstractRangeSeekBar.OnRangeSeekBarChangeListener<T> {

	@NotNull
	private final Interval<T> boundaries;

    @NotNull
	private final T step;

	public RangeSeekBarPreference(@NotNull Context context, AttributeSet attrs, @NotNull NumberIntervalMapper<T> mapper) {
		super(context, attrs, null, true, mapper);

		//noinspection ConstantConditions
		boundaries = mapper.parseValue(attrs.getAttributeValue(AbstractDialogPreference.localNameSpace, "boundaries"));

		final String stepValue = attrs.getAttributeValue(AbstractDialogPreference.localNameSpace, "step");
        if ( stepValue == null ) {
           step = getDefaultStep();
        } else {
           step = mapper.getMapper().parseValue(stepValue);
        }

	}

    @NotNull
    protected abstract T getDefaultStep();

    @NotNull
	protected View createPreferenceView(@NotNull Context context) {
        int count = 0;
        for ( T t = boundaries.getLeftLimit(); t.compareTo(boundaries.getRightLimit()) <= 0; t = add(t, step) ) {
            count += 1;
        }
        final NumberRangeSeekBar<T> result = new NumberRangeSeekBar<T>(boundaries, count, context);

        result.setNotifyWhileDragging(true);
		result.setOnRangeSeekBarChangeListener(this);

		return result;
	}

    @NotNull
    protected abstract T add(@NotNull T l, @NotNull T r);

    @Override
	protected LinearLayout.LayoutParams getParams() {
		return null;
	}

	@Override
	protected void initPreferenceView(@NotNull View v, Interval<T> value) {
		if (value != null) {
			((NumberRangeSeekBar<T>) v).setSelectedMinValue(value.getLeftLimit());
			((NumberRangeSeekBar<T>) v).setSelectedMaxValue(value.getRightLimit());
			setValueText(value);
		}
	}

	@Override
	public void rangeSeekBarValuesChanged(T minValue, T maxValue, boolean changeComplete) {
		final Interval<T> interval = IntervalImpl.newClosed(minValue, maxValue);

		if (changeComplete) {
			persistValue(interval);
		}

		setValueText(interval);
	}

	private void setValueText(@NotNull Interval<T> interval) {
		final String t = String.valueOf(interval);
        final String valueText = getValueText();
		updateValueText(valueText == null ? t : t.concat(valueText));
	}
}
