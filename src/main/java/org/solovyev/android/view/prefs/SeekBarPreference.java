/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.view.prefs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.NumberMapper;
import org.solovyev.common.utils.Mapper;


/* The following code was written by Matthew Wiggins
 * and is released under the APACHE 2.0 license
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

public class SeekBarPreference extends AbstractDialogPreference<Integer> implements SeekBar.OnSeekBarChangeListener {

	@NotNull
	private SeekBar seekBar;

	private int max = 0;

	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs, "50", true);

		max = attrs.getAttributeIntValue(androidns, "max", 100);
	}

	@Override
	protected LinearLayout.LayoutParams getParams() {
		return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
	}

	@NotNull
	@Override
	protected View createPreferenceView() {
		seekBar = new SeekBar(context);
		seekBar.setOnSeekBarChangeListener(this);

		return seekBar;
	}

	@Override
	protected void initPreferenceView(@Nullable View v ) {
		if ( v == null) {
			v = seekBar;
		}

		((SeekBar) v).setMax(max);
		if (value != null) {
			((SeekBar) v).setProgress(value);
			setValueText(value);
		}
	}

	@NotNull
	@Override
	protected Mapper<Integer> getMapper() {
		return new NumberMapper<Integer>(Integer.class);
	}

	public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
		setValueText(value);

		persistValue(value);
	}

	private void setValueText(int value) {
		String t = String.valueOf(value);
		valueTextView.setText(valueText == null ? t : t.concat(valueText));
	}

	public void onStartTrackingTouch(SeekBar seek) {
	}

	public void onStopTrackingTouch(SeekBar seek) {
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getMax() {
		return max;
	}

	public void setProgress(int progress) {
		value = progress;
		seekBar.setProgress(progress);
	}

	public int getProgress() {
		return value;
	}
}

