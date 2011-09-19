package org.solovyev.android.view.prefs;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 9/19/11
 * Time: 3:17 PM
 */
public class AbstractDialogPreference extends DialogPreference {

	@NotNull
	protected static final String androidns = "http://schemas.android.com/apk/res/android";

	@NotNull
	protected TextView splashText, valueText;

	@NotNull
	protected final Context context;

	protected String dialogMessage, suffix;

	public AbstractDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

		dialogMessage = attrs.getAttributeValue(androidns, "dialogMessage");
		suffix = attrs.getAttributeValue(androidns, "text");
	}

	@Override
	protected LinearLayout onCreateDialogView() {
		LinearLayout.LayoutParams params;
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(6, 6, 6, 6);

		splashText = new TextView(context);
		if (dialogMessage != null)
			splashText.setText(dialogMessage);
		layout.addView(splashText);

		valueText = new TextView(context);
		valueText.setGravity(Gravity.CENTER_HORIZONTAL);
		valueText.setTextSize(32);
		params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		layout.addView(valueText, params);

		return layout;
	}


}
