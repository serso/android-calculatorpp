package org.solovyev.android.view.prefs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.view.widgets.AbstractRangeSeekBar;
import org.solovyev.android.view.widgets.NumberRangeSeekBar;
import org.solovyev.common.utils.Interval;
import org.solovyev.common.utils.NumberInterval;

/**
 * User: serso
 * Date: 9/19/11
 * Time: 12:27 PM
 */
public abstract class RangeSeekBarPreference<T extends Number> extends AbstractDialogPreference<Interval<T>> implements AbstractRangeSeekBar.OnRangeSeekBarChangeListener<T> {

	@NotNull
	private AbstractRangeSeekBar<T> rangeSeekBar;

	@NotNull
	private final Interval<T> boundaries;

	private Integer steps;

	public RangeSeekBarPreference(@NotNull Context context, AttributeSet attrs) {
		super(context, attrs, null, true);

		//noinspection ConstantConditions
		boundaries = getMapper().parseValue(attrs.getAttributeValue(localNameSpace, "boundaries"));

		steps = attrs.getAttributeIntValue(localNameSpace, "steps", -1);
		if ( steps.equals(-1) ) {
			steps = null;
		}

		assert steps == null || steps >= 2;

		createPreferenceView();
	}

	@NotNull
	protected View createPreferenceView() {
		this.rangeSeekBar = new NumberRangeSeekBar<T>(boundaries, steps, context);
		this.rangeSeekBar.setNotifyWhileDragging(true);
		this.rangeSeekBar.setOnRangeSeekBarChangeListener(this);

		initPreferenceView(this.rangeSeekBar);

		return this.rangeSeekBar;
	}

	@Override
	protected LinearLayout.LayoutParams getParams() {
		return null;
	}

	@Override
	protected void initPreferenceView(@Nullable View v) {
		if ( v == null ) {
			v = rangeSeekBar;
		}

		if (value != null) {
			((AbstractRangeSeekBar<T>) v).setSelectedMinValue(value.getLeftBorder());
			((AbstractRangeSeekBar<T>) v).setSelectedMaxValue(value.getRightBorder());
			setValueText(value);
		}
	}

	@Override
	public void rangeSeekBarValuesChanged(T minValue, T maxValue, boolean changeComplete) {
		final Interval<T> interval = new NumberInterval<T>(minValue, maxValue);

		if (changeComplete) {
			persistValue(interval);
		}

		setValueText(interval);
	}

	private void setValueText(@NotNull Interval<T> interval) {
		final String t = String.valueOf(interval);
		valueTextView.setText(valueText == null ? t : t.concat(valueText));
	}
}
