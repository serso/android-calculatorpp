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
 * Time: 12:21 PM
 */

public interface Preference<T> {

	@NotNull
	String getKey();

	T getDefaultValue();

	T getPreference(@NotNull SharedPreferences preferences);

	 void putPreference(@NotNull SharedPreferences preferences, @Nullable T value);

	void putDefault(@NotNull SharedPreferences preferences);

}
