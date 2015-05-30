package org.solovyev.android.calculator.language;

import android.content.Context;
import android.text.TextUtils;

import org.solovyev.android.calculator.R;

import java.util.Locale;

import javax.annotation.Nonnull;

public final class Language {
	@Nonnull
	public final String code;

	@Nonnull
	public final Locale locale;

	@Nonnull
	final String name;

	public Language(@Nonnull String code, @Nonnull Locale locale) {
		this.code = code;
		this.locale = locale;
		this.name = makeName(code, locale);
	}

	@Nonnull
	public String getName(@Nonnull Context context) {
		if (!isSystem()) {
			return name;
		} else {
			return context.getString(R.string.cpp_system_language) + " (" + locale.getDisplayLanguage(locale) + ")";
		}
	}

	@Nonnull
	private static String makeName(@Nonnull String code, @Nonnull Locale locale) {
		if (code.equals(Languages.SYSTEM_LANGUAGE_CODE)) {
			return "";
		}

		final int underscore = code.indexOf("_");
		if (underscore >= 0 && TextUtils.isEmpty(locale.getDisplayCountry(locale))) {
			return locale.getDisplayName(locale) + " (" + code.substring(underscore + 1) + ")";
		}

		return locale.getDisplayName(locale);
	}

	public boolean isSystem() {
		return code.equals(Languages.SYSTEM_LANGUAGE_CODE);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final Language language = (Language) o;
		return code.equals(language.code);

	}

	@Override
	public int hashCode() {
		return code.hashCode();
	}
}
