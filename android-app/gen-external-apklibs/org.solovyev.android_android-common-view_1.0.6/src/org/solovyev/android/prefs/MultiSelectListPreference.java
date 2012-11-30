package org.solovyev.android.prefs;


import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.preference.ListPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.view.R;
import org.solovyev.common.text.CollectionTransformations;
import org.solovyev.common.text.StringMapper;
import org.solovyev.common.text.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Preference} that displays a list of entries as
 * a dialog and allows multiple selections
 * <p>
 * This preference will store a string into the SharedPreferences. This string will be the values selected
 * from the {@link #setEntryValues(CharSequence[])} array.
 * </p>
 */
public class MultiSelectListPreference<T> extends ListPreference {

	@NotNull
	private static final String DEFAULT_SEPARATOR = ";";

	@NotNull
	private final org.solovyev.common.text.Mapper<List<String>> mapper;

	private boolean[] checkedIndices;

	/*
	**********************************************************************
	*
	*                           CONSTRUCTORS
	*
	**********************************************************************
	*/

	public MultiSelectListPreference(Context context) {
		this(context, null);
	}

	public MultiSelectListPreference(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);

		String separator = DEFAULT_SEPARATOR;

		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MultiSelectListPreference);
		for (int i = 0; i < a.getIndexCount(); i++) {
			int attr = a.getIndex(i);

			final String attrValue = a.getString(attr);

			if (!StringUtils.isEmpty(attrValue)) {
				switch (attr) {
					case R.styleable.MultiSelectListPreference_separator:
						separator = attrValue;
						break;
				}
			}
		}

		this.mapper = new Mapper(separator);

		this.checkedIndices = new boolean[getEntries().length];
	}

	@Override
	public void setEntries(@NotNull CharSequence[] entries) {
		super.setEntries(entries);

		checkedIndices = new boolean[entries.length];
	}

	@Override
	protected void onPrepareDialogBuilder(@NotNull Builder builder) {
		final CharSequence[] entries = getEntries();
		final CharSequence[] entryValues = getEntryValues();

		if (entries == null || entryValues == null || entries.length != entryValues.length) {
			throw new IllegalStateException("ListPreference requires an entries array and an entryValues array which are both the same length");
		}

		restoreCheckedEntries();

		builder.setMultiChoiceItems(entries, checkedIndices,
				new DialogInterface.OnMultiChoiceClickListener() {
					public void onClick(DialogInterface dialog, int which, boolean value) {
						checkedIndices[which] = value;
					}
				});
	}


	private void restoreCheckedEntries() {
		final CharSequence[] entryValues = getEntryValues();

		final List<String> values = mapper.parseValue(getValue());
		if (values != null) {
			for (String value : values) {
				for (int i = 0; i < entryValues.length; i++) {
					final CharSequence entry = entryValues[i];
					if (entry.equals(value)) {
						checkedIndices[i] = true;
						break;
					}
				}
			}
		}
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		final CharSequence[] entryValues = getEntryValues();
		if (positiveResult && entryValues != null) {

			final List<String> checkedValues = new ArrayList<String>();
			for (int i = 0; i < entryValues.length; i++) {
				if (checkedIndices[i]) {
					checkedValues.add(entryValues[i].toString());
				}
			}


			final String value = mapper.formatValue(checkedValues);
			if (callChangeListener(value)) {
				setValue(value);
			}
		}
	}

	public static class Mapper implements org.solovyev.common.text.Mapper<List<String>> {

		@NotNull
		private final String separator;

		public Mapper(@NotNull String separator) {
			this.separator = separator;
		}

		@Override
		public String formatValue(@Nullable List<String> value) throws IllegalArgumentException {
			return CollectionTransformations.formatValue(value, separator, StringMapper.getInstance());
		}

		@Override
		public List<String> parseValue(@Nullable String value) throws IllegalArgumentException {
			return CollectionTransformations.split(value, separator, StringMapper.getInstance());
		}
	}

    @NotNull
    public static <T> org.solovyev.common.text.Mapper<List<T>> newListMapper(@NotNull org.solovyev.common.text.Mapper<T> nestedMapper) {
        return new ListMapper<T>(DEFAULT_SEPARATOR, nestedMapper);
    }

    @NotNull
    public static <T> org.solovyev.common.text.Mapper<List<T>> newListMapper(@NotNull org.solovyev.common.text.Mapper<T> nestedMapper,
                                                                             @NotNull String separator) {
        return new ListMapper<T>(separator, nestedMapper);
    }


	private static class ListMapper<T> implements org.solovyev.common.text.Mapper<List<T>> {

		@NotNull
		private final String separator;

		@NotNull
		private final org.solovyev.common.text.Mapper<T> nestedMapper;

		public ListMapper(@NotNull String separator, @NotNull org.solovyev.common.text.Mapper<T> nestedMapper) {
			this.separator = separator;
			this.nestedMapper = nestedMapper;
		}

		@Override
		public String formatValue(@Nullable List<T> value) throws IllegalArgumentException {
			return CollectionTransformations.formatValue(value, separator, nestedMapper);
		}

		@Override
		public List<T> parseValue(@Nullable String value) throws IllegalArgumentException {
			return CollectionTransformations.split(value, separator, nestedMapper);
		}
	}
}

