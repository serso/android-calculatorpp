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
public class IntervalPreference extends DialogPreference implements NumberPicker.OnChangedListener {

	@NotNull
	private final Context context;

	public IntervalPreference(@NotNull Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	public IntervalPreference(@NotNull Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	@Override
	public void onChanged(NumberPicker picker, int oldVal, int newVal) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	protected View onCreateDialogView() {
		LinearLayout.LayoutParams params;
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(6, 6, 6, 6);

		return layout;
	}
}
