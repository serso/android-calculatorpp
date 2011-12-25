/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.view.prefs;

import android.content.SharedPreferences;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 12/25/11
 * Time: 12:23 PM
 */
public abstract class AbstractPreference<T> implements Preference<T> {

	@NotNull
	private final String key;

	private final T defaultValue;

	protected AbstractPreference(@NotNull String key, @Nullable T defaultValue) {
		this.key = key;
		this.defaultValue = defaultValue;
	}

	@NotNull
	public String getKey() {
		return key;
	}

	public T getDefaultValue() {
		return defaultValue;
	}

	@Override
	public final T getPreference(@NotNull SharedPreferences preferences) {
		if ( preferences.contains(this.key) ) {
			return getPersistedValue(preferences);
		} else {
			return this.defaultValue;
		}
	}

	@Nullable
	protected abstract T getPersistedValue(@NotNull SharedPreferences preferences);

	@Override
	public void putDefault(@NotNull SharedPreferences preferences) {
		putPreference(preferences, this.defaultValue);
	}

	@Override
	public void putPreference(@NotNull SharedPreferences preferences, @Nullable T value) {
		if (value != null) {
			final SharedPreferences.Editor editor = preferences.edit();
			putPersistedValue(editor, value);
			editor.commit();
		}
	}

	protected abstract void putPersistedValue(@NotNull SharedPreferences.Editor editor, @NotNull T value);
}
