/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.view.prefs;

import android.content.SharedPreferences;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.utils.Mapper;
import org.solovyev.common.utils.StringMapper;

/**
 * User: serso
 * Date: 12/25/11
 * Time: 12:37 PM
 */
public class StringPreference<T> extends AbstractPreference<T> {

	@NotNull
	private final Mapper<T> mapper;

	public StringPreference(@NotNull String id, @Nullable T defaultValue, @NotNull Mapper<T> mapper) {
		super(id, defaultValue);
		this.mapper = mapper;
	}

	@NotNull
	public static StringPreference<String> newInstance(@NotNull String id, @Nullable String defaultValue) {
		return new StringPreference<String>(id, defaultValue, new StringMapper());
	}

	@NotNull
	public static <T> StringPreference<T> newInstance(@NotNull String id, @Nullable String defaultValue, @NotNull Mapper<T> parser) {
		return new StringPreference<T>(id, parser.parseValue(defaultValue), parser);
	}

	@Override
	protected T getPersistedValue(@NotNull SharedPreferences preferences) {
		return mapper.parseValue(preferences.getString(getKey(), null));
	}

	@Override
	protected void putPersistedValue(@NotNull SharedPreferences.Editor editor, @NotNull T value) {
		editor.putString(getKey(), mapper.formatValue(value));
	}
}
