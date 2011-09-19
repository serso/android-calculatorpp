package org.solovyev.android.view.prefs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.view.widgets.AbstractRangeSeekBar;
import org.solovyev.android.view.widgets.NumberRangeSeekBar;
import org.solovyev.common.utils.CollectionsUtils;
import org.solovyev.common.utils.Converter;
import org.solovyev.common.utils.StringMapper;

import java.util.Arrays;
import java.util.List;

/**
 * User: serso
 * Date: 9/19/11
 * Time: 12:27 PM
 */
public abstract class RangeSeekBarPreference<T extends Number> extends AbstractDialogPreference implements AbstractRangeSeekBar.OnRangeSeekBarChangeListener<T> {

	public final static String localNameSpace = "http://schemas.android.com/apk/res/org.solovyev.android.calculator";

	@NotNull
	private AbstractRangeSeekBar<T> rangeSeekBar;

	@NotNull
	private T min;

	@NotNull
	private T max;

	@NotNull
	private T selectedMin;

	@NotNull
	private T selectedMax;


	public RangeSeekBarPreference(@NotNull Context context, AttributeSet attrs) {
		super(context, attrs);


		final Converter<String, T> c = getConverter();

		String minString = attrs.getAttributeValue(localNameSpace, "min");
		min = c.convert(minString == null ? "0" : minString);
		String maxString = attrs.getAttributeValue(localNameSpace, "max");
		max = c.convert(maxString == null ? "100" : maxString);


		this.rangeSeekBar = new NumberRangeSeekBar<T>(min, max, context);
		rangeSeekBar.setOnRangeSeekBarChangeListener(this);
	}

	public void setMin(@NotNull String min) {
		this.min = getConverter().convert(min);
	}

	public void setMax(@NotNull String max) {
		this.max = getConverter().convert(max);
	}

	@NotNull
	abstract Converter<String, T> getConverter();

	@Override
	protected LinearLayout onCreateDialogView() {
		final LinearLayout result = super.onCreateDialogView();

		this.rangeSeekBar = new NumberRangeSeekBar<T>(min, max, context);
		rangeSeekBar.setOnRangeSeekBarChangeListener(this);
		initRangeSeekBar();

		result.addView(rangeSeekBar);

		return result;
	}

	@Override
	protected void onBindDialogView(View v) {
		super.onBindDialogView(v);
		initRangeSeekBar();
	}

	private void initRangeSeekBar() {
		rangeSeekBar.setSelectedMinValue(selectedMin);
		rangeSeekBar.setSelectedMaxValue(selectedMax);
	}

	@Override
	protected void onSetInitialValue(boolean restore, Object defaultValue) {
		super.onSetInitialValue(restore, defaultValue);

		final List<String> values;
		if (restore) {
			values = CollectionsUtils.split(getPersistedString("0;100"), ";", new StringMapper());
		} else {
			values = CollectionsUtils.split(String.valueOf(defaultValue), ";", new StringMapper());
		}

		selectedMin = getConverter().convert(values.get(0));
		selectedMax = getConverter().convert(values.get(1));
	}


	@Override
	public void rangeSeekBarValuesChanged(T minValue, T maxValue) {
		final String value = CollectionsUtils.formatValue(Arrays.asList(String.valueOf(minValue), String.valueOf(maxValue)), ";", new StringMapper());
		if (shouldPersist()) {
			persistString(value);
		}
		callChangeListener(value);
	}
}
