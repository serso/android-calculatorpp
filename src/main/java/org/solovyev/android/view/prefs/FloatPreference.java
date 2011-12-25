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
 * Time: 1:08 PM
 */
public class FloatPreference extends AbstractPreference<Float> {

	protected FloatPreference(@NotNull String key, @Nullable Float defaultValue) {
		super(key, defaultValue);
	}

	@Override
	protected Float getPersistedValue(@NotNull SharedPreferences preferences) {
		return preferences.getFloat(getKey(), -1f);
	}

	@Override
	protected void putPersistedValue(@NotNull SharedPreferences.Editor editor, @NotNull Float value) {
		editor.putFloat(getKey(), value);
	}
}
