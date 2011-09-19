/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.view.prefs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.LinearLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/* The following code was written by Matthew Wiggins
 * and is released under the APACHE 2.0 license
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

public class SeekBarPreference extends AbstractDialogPreference implements SeekBar.OnSeekBarChangeListener {

	@NotNull
	private SeekBar seekBar;

	private int defaultValue, max, value = 0;

	@Nullable
	protected String valueText;

	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);

		defaultValue = attrs.getAttributeIntValue(androidns, "defaultValue", 0);
		max = attrs.getAttributeIntValue(androidns, "max", 100);
		valueText = attrs.getAttributeValue(androidns, "text");
	}

	@Override
	protected LinearLayout onCreateDialogView() {
		final LinearLayout layout = super.onCreateDialogView();

		seekBar = new SeekBar(context);
		seekBar.setOnSeekBarChangeListener(this);
		layout.addView(seekBar, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

		if (shouldPersist())
			value = getPersistedInt(defaultValue);

		initSeekBar();

		return layout;
	}

	private void initSeekBar() {
		seekBar.setMax(max);
		seekBar.setProgress(value);
	}

	@Override
	protected void onBindDialogView(View v) {
		super.onBindDialogView(v);
		initSeekBar();
	}

	@Override
	protected void onSetInitialValue(boolean restore, Object defaultValue) {
		super.onSetInitialValue(restore, defaultValue);
		if (restore)
			value = shouldPersist() ? getPersistedInt(this.defaultValue) : 0;
		else
			value = (Integer) defaultValue;
	}

	public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
		String t = String.valueOf(value);
		valueTextView.setText(valueText == null ? t : t.concat(valueText));
		if (shouldPersist())
			persistInt(value);
		callChangeListener(new Integer(value));
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
		if (seekBar != null)
			seekBar.setProgress(progress);
	}

	public int getProgress() {
		return value;
	}
}

