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
 * Time: 1:07 PM
 */
public class LongPreference extends AbstractPreference<Long> {

	protected LongPreference(@NotNull String key, @Nullable Long defaultValue) {
		super(key, defaultValue);
	}

	@Override
	protected Long getPersistedValue(@NotNull SharedPreferences preferences) {
		return preferences.getLong(getKey(), -1);
	}

	@Override
	protected void putPersistedValue(@NotNull SharedPreferences.Editor editor, @NotNull Long value) {
		editor.putLong(getKey(), value);
	}
}
