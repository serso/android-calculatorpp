package org.solovyev.android.calculator.language;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.Preferences;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public final class Languages implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Nonnull
    public static final String SYSTEM_LANGUAGE_CODE = "00";
    @Nonnull
    public static final Language SYSTEM_LANGUAGE = new Language(SYSTEM_LANGUAGE_CODE, Locale.getDefault());
    @Nullable
    private static Locale[] locales;
    @Nonnull
    private final List<Language> list = new ArrayList<>();
    @Nonnull
    private final SharedPreferences preferences;

    public Languages(@Nonnull SharedPreferences preferences) {
        this.preferences = preferences;
    }

    @Nullable
    private static Language makeLanguage(@Nonnull String localeId) {
        final Locale locale = findLocaleById(localeId);
        if (locale == null) {
            return null;
        }
        return new Language(localeId, locale);
    }

    @Nullable
    private static Locale findLocaleById(@Nonnull String id) {
        for (Locale locale : getLocales()) {
            if (TextUtils.equals(locale.toString(), id)) {
                return locale;
            }
        }

        final String language;
        final int underscore = id.indexOf("_");
        if (underscore >= 0) {
            language = id.substring(0, underscore);
        } else {
            language = id;
        }

        for (Locale locale : getLocales()) {
            if (TextUtils.equals(locale.getLanguage(), language)) {
                return locale;
            }
        }

        Log.d("Languages", "No locale found for " + id);
        return null;
    }

    @Nonnull
    public static Locale[] getLocales() {
        if (locales == null) {
            locales = Locale.getAvailableLocales();
        }
        return locales;
    }

    /**
     * This method should be called only when default values have been set to application's preferences
     */
    public void init() {
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Nonnull
    public List<Language> getList() {
        Check.isMainThread();
        if (list.isEmpty()) {
            loadList();
        }
        return list;
    }

    private void loadList() {
        Check.isMainThread();
        Check.isEmpty(list);
        tryAddLanguage("ar");
        tryAddLanguage("cs");
        tryAddLanguage("en");
        tryAddLanguage("es_ES");
        tryAddLanguage("de");
        tryAddLanguage("fi");
        tryAddLanguage("fr");
        tryAddLanguage("it");
        tryAddLanguage("it");
        tryAddLanguage("pl");
        tryAddLanguage("pt_BR");
        tryAddLanguage("pt_PT");
        tryAddLanguage("ru");
        tryAddLanguage("tr");
        tryAddLanguage("vi");
        tryAddLanguage("uk");
        tryAddLanguage("ja");
        tryAddLanguage("zh_CN");
        tryAddLanguage("zh_TW");
        Collections.sort(list, new Comparator<Language>() {
            @Override
            public int compare(Language lhs, Language rhs) {
                return lhs.name.compareTo(rhs.name);
            }
        });
        list.add(0, SYSTEM_LANGUAGE);
    }

    private void tryAddLanguage(@Nonnull String locale) {
        final Language language = makeLanguage(locale);
        if (language != null) {
            list.add(language);
        }
    }

    @Nonnull
    public Language getCurrent() {
        return get(Preferences.Gui.language.getPreference(preferences));
    }

    @Nonnull
    public Language get(@Nonnull String code) {
        if (TextUtils.equals(SYSTEM_LANGUAGE.code, code)) {
            // quick check to avoid list loading
            return SYSTEM_LANGUAGE;
        }
        Language language = findLanguageByCode(code);
        if (language != null) {
            return language;
        }
        return SYSTEM_LANGUAGE;
    }

    @Nullable
    private Language findLanguageByCode(@Nonnull String code) {
        for (Language language : getList()) {
            if (TextUtils.equals(language.code, code)) {
                return language;
            }
        }
        return null;
    }

    @Override
    public void onSharedPreferenceChanged(@Nonnull SharedPreferences p, String key) {
        if (Preferences.Gui.language.isSameKey(key)) {
            updateContextLocale(App.getApplication(), false);
        }
    }

    public void updateContextLocale(@Nonnull Context context, boolean initial) {
        final Language language = getCurrent();
        // we don't need to set system language while starting up the app
        if (initial && language.isSystem()) {
            return;
        }
        if (!Locale.getDefault().equals(language.locale)) {
            Locale.setDefault(language.locale);
        }

        final Resources r = context.getResources();
        final DisplayMetrics dm = r.getDisplayMetrics();
        final Configuration c = r.getConfiguration();
        if (c.locale == null || !c.locale.equals(language.locale)) {
            c.locale = language.locale;
            r.updateConfiguration(c, dm);
        }
    }
}