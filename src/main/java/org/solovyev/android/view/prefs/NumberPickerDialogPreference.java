/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.view.prefs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.view.widgets.NumberPicker;
import org.solovyev.common.GenericIntervalMapper;
import org.solovyev.common.utils.Interval;
import org.solovyev.common.utils.Mapper;

/**
 * User: serso
 * Date: 9/26/11
 * Time: 10:31 PM
 */
public class NumberPickerDialogPreference extends AbstractDialogPreference<Integer> implements NumberPicker.OnChangedListener {

	@NotNull
	private NumberPicker numberPicker;

	@NotNull
	private final Interval<Integer> boundaries;

	public NumberPickerDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs, null);

		//noinspection ConstantConditions
		boundaries = new GenericIntervalMapper<Integer>(getMapper()).parseValue(attrs.getAttributeValue(localNameSpace, "boundaries"));

		createPreferenceView();
	}

	@NotNull
	@Override
	protected LinearLayout onCreateDialogView() {
		final LinearLayout result = super.onCreateDialogView();

		createPreferenceView();
		initPreferenceView();

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		result.addView(numberPicker, params);

		return result;
	}

	protected void createPreferenceView() {
		this.numberPicker = new NumberPicker(context);
		this.numberPicker.setOnChangeListener(this);

		initPreferenceView();
	}

	@Override
	protected void initPreferenceView() {
		if (value != null) {
			numberPicker.setRange(boundaries.getLeftBorder(), boundaries.getRightBorder());
			numberPicker.setCurrent(value);
		}
	}

	@NotNull
	@Override
	protected Mapper<Integer> getMapper() {
		return new Mapper<Integer>() {
			@Override
			public String formatValue(@Nullable Integer value) throws IllegalArgumentException {
				return String.valueOf(value);
			}

			@Override
			public Integer parseValue(@Nullable String value) throws IllegalArgumentException {
				return Integer.valueOf(value);
			}
		};
	}

	@Override
	public void onChanged(NumberPicker picker, int oldVal, int newVal) {
		persistValue(newVal);
	}
}
