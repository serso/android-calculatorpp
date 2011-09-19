package org.solovyev.android.view.prefs;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.view.widgets.NumberPicker;

/**
 * User: serso
 * Date: 9/19/11
 * Time: 12:27 PM
 */
public class IntervalPreference extends AbstractDialogPreference implements NumberPicker.OnChangedListener {

	@NotNull
	private final NumberPicker leftBorder;

	@NotNull
	private final NumberPicker rightBorder;


	public IntervalPreference(@NotNull Context context, AttributeSet attrs) {
		super(context, attrs);
		this.leftBorder = new NumberPicker(context);
		this.rightBorder = new NumberPicker(context);
	}

	@Override
	public void onChanged(NumberPicker picker, int oldVal, int newVal) {
/*		if (shouldPersist())
			persistString(value);
		callChangeListener(new Integer(value));*/
	}

	@Override
	protected LinearLayout onCreateDialogView() {
		final LinearLayout result = super.onCreateDialogView();

		final LinearLayout horizontal = new LinearLayout(context);
		horizontal.setOrientation(LinearLayout.HORIZONTAL);

		horizontal.addView(leftBorder);
		horizontal.addView(rightBorder);

		return result;
	}
}
