package org.solovyev.android.prefs;

import android.content.SharedPreferences;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CharacterPreference extends AbstractPreference<Character> {
    private CharacterPreference(@Nonnull String key, @Nullable Character defaultValue) {
        super(key, defaultValue);
    }

    public static CharacterPreference of(@Nonnull String key, @Nullable Character defaultValue) {
        return new CharacterPreference(key, defaultValue);
    }

    @Nullable
    @Override
    protected Character getPersistedValue(@Nonnull SharedPreferences preferences) {
        return (char) preferences.getInt(getKey(), 0);
    }

    @Override
    protected void putPersistedValue(@Nonnull SharedPreferences.Editor editor, @Nonnull Character value) {
        editor.putInt(getKey(), value);
    }
}
