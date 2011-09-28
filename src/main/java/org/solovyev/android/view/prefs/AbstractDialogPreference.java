package org.solovyev.android.view.prefs;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.utils.Mapper;

/**
 * User: serso
 * Date: 9/19/11
 * Time: 3:17 PM
 */
public abstract class AbstractDialogPreference<T> extends DialogPreference {

	@NotNull
	protected final static String localNameSpace = "http://schemas.android.com/apk/res/org.solovyev.android.calculator";

	@NotNull
	protected final static String androidns = "http://schemas.android.com/apk/res/android";

	@Nullable
	protected TextView valueTextView;

	@Nullable
	protected String valueText;

	@NotNull
	protected final Context context;

	@Nullable
	protected String description;

	@Nullable
	protected T value;

	@Nullable
	private T defaultValue;

	@Nullable
	private final String defaultStringValue;

	private final boolean needValueText;

	public AbstractDialogPreference(Context context, AttributeSet attrs, @Nullable String defaultStringValue, boolean needValueText) {
		super(context, attrs);
		this.context = context;
		this.defaultStringValue = defaultStringValue;
		this.needValueText = needValueText;

		final String defaultValueFromAttrs = attrs.getAttributeValue(androidns, "defaultValue");
		if ( defaultValueFromAttrs != null ) {
			defaultValue = getMapper().parseValue(defaultValueFromAttrs);
		} else if (defaultStringValue != null) {
			defaultValue = getMapper().parseValue(defaultStringValue);
		} else {
			throw new IllegalArgumentException();
		}

		description = attrs.getAttributeValue(androidns, "dialogMessage");
		valueText = attrs.getAttributeValue(androidns, "text");
	}

	@Override
	@NotNull
	protected final LinearLayout onCreateDialogView() {
		if (shouldPersist()) {
			value = getPersistedValue();
		}

		final LinearLayout result = new LinearLayout(context);
		result.setOrientation(LinearLayout.VERTICAL);
		result.setPadding(6, 6, 6, 6);

		if (description != null) {
			final TextView splashText = new TextView(context);
			splashText.setText(description);
			result.addView(splashText);
		}

		if (needValueText) {
			valueTextView = new TextView(context);
			valueTextView.setGravity(Gravity.CENTER_HORIZONTAL);
			valueTextView.setTextSize(32);

			final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			result.addView(valueTextView, params);
		}

		final View v = createPreferenceView();
		initPreferenceView(v);

		final LinearLayout.LayoutParams params = getParams();
		if (params != null) {
			result.addView(v, params);
		} else {
			result.addView(v);
		}

		return result;
	}

	@Nullable
	protected abstract LinearLayout.LayoutParams getParams();

	@Override
	protected void onSetInitialValue(boolean restore, Object defaultValue) {
		super.onSetInitialValue(restore, defaultValue);

		if (restore) {
			if (shouldPersist()) {
				value = getPersistedValue();
			} else {
				value = this.defaultValue;
			}
		} else {
			value = (T) defaultValue;
			if (shouldPersist()) {
				persist(this.value);
			}
		}
	}

	@Override
	protected void onBindDialogView(View v) {
		super.onBindDialogView(v);
		initPreferenceView(null);
	}

	@NotNull
	protected abstract View createPreferenceView();

	protected abstract void initPreferenceView(@Nullable View v);

	@Nullable
	private T getPersistedValue() {
		String persistedString = getPersistedString(defaultStringValue);
		if ( persistedString == defaultStringValue ) {
			return defaultValue;
		} else {
			return getMapper().parseValue(persistedString);
		}
	}

	protected void persistValue(@Nullable T value) {
		Log.d(AbstractDialogPreference.class.getName(), "Trying to persist value: " + value);
		this.value = value;

		Log.d(AbstractDialogPreference.class.getName(), "android.preference.Preference.callChangeListener()");
		if (callChangeListener(value)) {
			Log.d(AbstractDialogPreference.class.getName(), "android.preference.Preference.shouldPersist()");
			if (shouldPersist()) {
				Log.d(AbstractDialogPreference.class.getName(), "org.solovyev.android.view.prefs.AbstractDialogPreference.persist()");
				persist(value);
			}
		}
	}

	private void persist(@Nullable T value) {
		if (value != null) {
			final String toBePersistedString = getMapper().formatValue(value);
			if (toBePersistedString != null) {
				if ( callChangeListener(toBePersistedString) ) {
					persistString(toBePersistedString);
				}
			}
		}
	}

	@NotNull
	protected abstract Mapper<T> getMapper();
}
