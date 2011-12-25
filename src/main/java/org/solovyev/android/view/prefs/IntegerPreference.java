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
 * Time: 12:47 PM
 */
public class IntegerPreference extends AbstractPreference<Integer> {

	public IntegerPreference(@NotNull String key, @Nullable Integer defaultValue) {
		super(key, defaultValue);
	}

	@Override
	protected Integer getPersistedValue(@NotNull SharedPreferences preferences) {
		return preferences.getInt(getKey(), -1);
	}

	@Override
	protected void putPersistedValue(@NotNull SharedPreferences.Editor editor, @NotNull Integer value) {
		editor.putInt(getKey(), value);
	}

}
