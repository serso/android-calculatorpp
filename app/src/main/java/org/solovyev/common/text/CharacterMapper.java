package org.solovyev.common.text;

import android.text.TextUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CharacterMapper implements Mapper<Character> {

    @Nonnull
    public static final CharacterMapper INSTANCE = new CharacterMapper();

    private CharacterMapper() {
    }

    @Nonnull
    @Override
    public String formatValue(@Nullable Character value) throws IllegalArgumentException {
        return value == null || value == 0 ? "" : String.valueOf(value);
    }

    @Nonnull
    @Override
    public Character parseValue(@Nullable String value) throws IllegalArgumentException {
        return TextUtils.isEmpty(value) ? 0 : value.charAt(0);
    }
}
