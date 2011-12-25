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
 * Time: 1:06 PM
 */
public class BooleanPreference extends AbstractPreference<Boolean>{

	public BooleanPreference(@NotNull String key, @Nullable Boolean defaultValue) {
		super(key, defaultValue);
	}

	@Override
	protected Boolean getPersistedValue(@NotNull SharedPreferences preferences) {
		return preferences.getBoolean(getKey(), false);
	}

	@Override
	protected void putPersistedValue(@NotNull SharedPreferences.Editor editor, @NotNull Boolean value) {
		editor.putBoolean(getKey(), value);
	}
}
